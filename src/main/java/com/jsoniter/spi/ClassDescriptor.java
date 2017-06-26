package com.jsoniter.spi;

import java.lang.reflect.*;
import java.util.*;

import static java.lang.reflect.Modifier.isTransient;

public class ClassDescriptor {

    public ClassInfo classInfo;
    public Class clazz;
    public Map<String, Type> lookup;
    public ConstructorDescriptor ctor;
    public List<Binding> fields;
    public List<Binding> setters;
    public List<Binding> getters;
    public List<WrapperDescriptor> bindingTypeWrappers;
    public List<Method> keyValueTypeWrappers;
    public List<UnwrapperDescriptor> unwrappers;
    public boolean asExtraForUnknownProperties;
    public Binding onMissingProperties;
    public Binding onExtraProperties;

    private ClassDescriptor() {
    }

    public static ClassDescriptor getDecodingClassDescriptor(ClassInfo classInfo, boolean includingPrivate) {
        Class clazz = classInfo.clazz;
        Map<String, Type> lookup = collectTypeVariableLookup(clazz);
        ClassDescriptor desc = new ClassDescriptor();
        desc.classInfo = classInfo;
        desc.clazz = clazz;
        desc.lookup = lookup;
        desc.ctor = getCtor(clazz);
        desc.setters = getSetters(lookup, classInfo, includingPrivate);
        desc.getters = new ArrayList<Binding>();
        desc.fields = getFields(lookup, classInfo, includingPrivate);
        desc.bindingTypeWrappers = new ArrayList<WrapperDescriptor>();
        desc.keyValueTypeWrappers = new ArrayList<Method>();
        desc.unwrappers = new ArrayList<UnwrapperDescriptor>();
        for (Extension extension : JsoniterSpi.getExtensions()) {
            extension.updateClassDescriptor(desc);
        }
        for (Binding field : desc.fields) {
            if (field.valueType instanceof Class) {
                Class valueClazz = (Class) field.valueType;
                if (valueClazz.isArray()) {
                    field.valueCanReuse = false;
                    continue;
                }
            }
            field.valueCanReuse = field.valueTypeLiteral.nativeType == null;
        }
        decodingDeduplicate(desc);
        if (includingPrivate) {
            if (desc.ctor.ctor != null) {
                desc.ctor.ctor.setAccessible(true);
            }
            if (desc.ctor.staticFactory != null) {
                desc.ctor.staticFactory.setAccessible(true);
            }
            for (WrapperDescriptor setter : desc.bindingTypeWrappers) {
                setter.method.setAccessible(true);
            }
        }
        for (Binding binding : desc.allDecoderBindings()) {
            if (binding.fromNames == null) {
                binding.fromNames = new String[]{binding.name};
            }
            if (binding.field != null && includingPrivate) {
                binding.field.setAccessible(true);
            }
            if (binding.method != null && includingPrivate) {
                binding.method.setAccessible(true);
            }
            if (binding.decoder != null) {
                JsoniterSpi.addNewDecoder(binding.decoderCacheKey(), binding.decoder);
            }
        }
        return desc;
    }

    public static ClassDescriptor getEncodingClassDescriptor(ClassInfo classInfo, boolean includingPrivate) {
        Class clazz = classInfo.clazz;
        Map<String, Type> lookup = collectTypeVariableLookup(clazz);
        ClassDescriptor desc = new ClassDescriptor();
        desc.classInfo = classInfo;
        desc.clazz = clazz;
        desc.lookup = lookup;
        desc.fields = getFields(lookup, classInfo, includingPrivate);
        desc.getters = getGetters(lookup, classInfo, includingPrivate);
        desc.bindingTypeWrappers = new ArrayList<WrapperDescriptor>();
        desc.keyValueTypeWrappers = new ArrayList<Method>();
        desc.unwrappers = new ArrayList<UnwrapperDescriptor>();
        for (Extension extension : JsoniterSpi.getExtensions()) {
            extension.updateClassDescriptor(desc);
        }
        encodingDeduplicate(desc);
        for (Binding binding : desc.allEncoderBindings()) {
            if (binding.toNames == null) {
                binding.toNames = new String[]{binding.name};
            }
            if (binding.field != null && includingPrivate) {
                binding.field.setAccessible(true);
            }
            if (binding.method != null && includingPrivate) {
                binding.method.setAccessible(true);
            }
            if (binding.encoder != null) {
                JsoniterSpi.addNewEncoder(binding.encoderCacheKey(), binding.encoder);
            }
        }
        return desc;
    }

    // TODO: do not remove, set fromNames to []
    private static void decodingDeduplicate(ClassDescriptor desc) {
        HashMap<String, Binding> byName = new HashMap<String, Binding>();
        for (Binding field : desc.fields) {
            for (String fromName : field.fromNames) {
                if (byName.containsKey(fromName)) {
                    throw new JsonException("field decode from same name: " + fromName);
                }
                byName.put(fromName, field);
            }
        }
        ArrayList<Binding> iteratingSetters = new ArrayList<Binding>(desc.setters);
        Collections.reverse(iteratingSetters);
        for (Binding setter : iteratingSetters) {
            for (String fromName : setter.fromNames) {
                Binding existing = byName.get(fromName);
                if (existing == null) {
                    byName.put(fromName, setter);
                    continue;
                }
                if (desc.fields.remove(existing)) {
                    continue;
                }
                if (existing.method != null && existing.method.getName().equals(setter.method.getName())) {
                    // inherited interface setter
                    // iterate in reverse order, so that the setter from child class will be kept
                    desc.setters.remove(existing);
                    continue;
                }
                throw new JsonException("setter decode from same name: " + fromName);
            }
        }
        for (WrapperDescriptor wrapper : desc.bindingTypeWrappers) {
            for (Binding param : wrapper.parameters) {
                for (String fromName : param.fromNames) {
                    Binding existing = byName.get(fromName);
                    if (existing == null) {
                        byName.put(fromName, param);
                        continue;
                    }
                    if (desc.fields.remove(existing)) {
                        continue;
                    }
                    if (desc.setters.remove(existing)) {
                        continue;
                    }
                    throw new JsonException("wrapper parameter decode from same name: " + fromName);
                }
            }
        }
        for (Binding param : desc.ctor.parameters) {
            for (String fromName : param.fromNames) {
                Binding existing = byName.get(fromName);
                if (existing == null) {
                    byName.put(fromName, param);
                    continue;
                }
                if (desc.fields.remove(existing)) {
                    continue;
                }
                if (desc.setters.remove(existing)) {
                    continue;
                }
                throw new JsonException("ctor parameter decode from same name: " + fromName);
            }
        }
    }

    // TODO: do not remove, set toNames to []
    private static void encodingDeduplicate(ClassDescriptor desc) {
        HashMap<String, Binding> byName = new HashMap<String, Binding>();
        for (Binding field : desc.fields) {
            for (String toName : field.toNames) {
                if (byName.containsKey(toName)) {
                    throw new JsonException("field encode to same name: " + toName);
                }
                byName.put(toName, field);
            }
        }
        for (Binding getter : new ArrayList<Binding>(desc.getters)) {
            for (String toName : getter.toNames) {
                Binding existing = byName.get(toName);
                if (existing == null) {
                    byName.put(toName, getter);
                    continue;
                }
                if (desc.fields.remove(existing)) {
                    continue;
                }
                if (existing.method != null && existing.method.getName().equals(getter.method.getName())) {
                    // inherited interface getter
                    desc.getters.remove(getter);
                    continue;
                }
                throw new JsonException("field encode to same name: " + toName);
            }
        }
    }

    private static ConstructorDescriptor getCtor(Class clazz) {
        ConstructorDescriptor cctor = new ConstructorDescriptor();
        if (JsoniterSpi.canCreate(clazz)) {
            cctor.objectFactory = JsoniterSpi.getObjectFactory(clazz);
            return cctor;
        }
        try {
            cctor.ctor = clazz.getDeclaredConstructor();
        } catch (Exception e) {
            cctor.ctor = null;
        }
        return cctor;
    }

    private static List<Binding> getFields(Map<String, Type> lookup, ClassInfo classInfo, boolean includingPrivate) {
        ArrayList<Binding> bindings = new ArrayList<Binding>();
        for (Field field : getAllFields(classInfo.clazz, includingPrivate)) {
            if (Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            if (!includingPrivate && !Modifier.isPublic(field.getType().getModifiers())) {
                continue;
            }
            if (includingPrivate) {
                field.setAccessible(true);
            }
            Binding binding = createBindingFromField(lookup, classInfo, field);
            if (isTransient(field.getModifiers())) {
                binding.toNames = new String[0];
                binding.fromNames = new String[0];
            }
            bindings.add(binding);
        }
        return bindings;
    }

    private static Binding createBindingFromField(Map<String, Type> lookup, ClassInfo classInfo, Field field) {
        try {
            Binding binding = new Binding(classInfo, lookup, field.getGenericType());
            binding.fromNames = new String[]{field.getName()};
            binding.toNames = new String[]{field.getName()};
            binding.name = field.getName();
            binding.annotations = field.getAnnotations();
            binding.field = field;
            return binding;
        } catch (Exception e) {
            throw new JsonException("failed to create binding for field: " + field, e);
        }
    }

    private static List<Field> getAllFields(Class clazz, boolean includingPrivate) {
        List<Field> allFields = Arrays.asList(clazz.getFields());
        if (includingPrivate) {
            allFields = new ArrayList<Field>();
            Class current = clazz;
            while (current != null) {
                allFields.addAll(Arrays.asList(current.getDeclaredFields()));
                current = current.getSuperclass();
            }
        }
        return allFields;
    }

    private static List<Binding> getSetters(Map<String, Type> lookup, ClassInfo classInfo, boolean includingPrivate) {
        ArrayList<Binding> setters = new ArrayList<Binding>();
        for (Method method : getAllMethods(classInfo.clazz, includingPrivate)) {
            if (Modifier.isStatic(method.getModifiers())) {
                continue;
            }
            String methodName = method.getName();
            if (methodName.length() < 4) {
                continue;
            }
            if (!methodName.startsWith("set")) {
                continue;
            }
            Type[] paramTypes = method.getGenericParameterTypes();
            if (paramTypes.length != 1) {
                continue;
            }
            if (!includingPrivate && !Modifier.isPublic(method.getParameterTypes()[0].getModifiers())) {
                continue;
            }
            if (includingPrivate) {
                method.setAccessible(true);
            }
            try {
                String fromName = translateSetterName(methodName);
                Field field = null;
                try {
                    field = method.getDeclaringClass().getDeclaredField(fromName);
                } catch (NoSuchFieldException e) {
                    // ignore
                }
                Binding setter = new Binding(classInfo, lookup, paramTypes[0]);
                if (field != null && isTransient(field.getModifiers())) {
                    setter.fromNames = new String[0];
                } else {
                    setter.fromNames = new String[]{fromName};
                }
                setter.name = fromName;
                setter.method = method;
                setter.annotations = method.getAnnotations();
                setters.add(setter);
            } catch (Exception e) {
                throw new JsonException("failed to create binding from setter: " + method, e);
            }
        }
        return setters;
    }

    private static List<Method> getAllMethods(Class clazz, boolean includingPrivate) {
        List<Method> allMethods = Arrays.asList(clazz.getMethods());
        if (includingPrivate) {
            allMethods = new ArrayList<Method>();
            Class current = clazz;
            while (current != null) {
                allMethods.addAll(Arrays.asList(current.getDeclaredMethods()));
                current = current.getSuperclass();
            }
        }
        return allMethods;
    }

    private static String translateSetterName(String methodName) {
        if (!methodName.startsWith("set")) {
            return null;
        }
        String fromName = methodName.substring("set".length());
        char[] fromNameChars = fromName.toCharArray();
        fromNameChars[0] = Character.toLowerCase(fromNameChars[0]);
        fromName = new String(fromNameChars);
        return fromName;
    }

    private static List<Binding> getGetters(Map<String, Type> lookup, ClassInfo classInfo, boolean includingPrivate) {
        ArrayList<Binding> getters = new ArrayList<Binding>();
        for (Method method : getAllMethods(classInfo.clazz, includingPrivate)) {
            if (Modifier.isStatic(method.getModifiers())) {
                continue;
            }
            String methodName = method.getName();
            if ("getClass".equals(methodName)) {
                continue;
            }
            if (methodName.length() < 4) {
                continue;
            }
            if (!methodName.startsWith("get")) {
                continue;
            }
            if (method.getGenericParameterTypes().length != 0) {
                continue;
            }
            String toName = methodName.substring("get".length());
            char[] toNameChars = toName.toCharArray();
            toNameChars[0] = Character.toLowerCase(toNameChars[0]);
            toName = new String(toNameChars);
            Binding getter = new Binding(classInfo, lookup, method.getGenericReturnType());
            Field field = null;
            try {
                field = method.getDeclaringClass().getDeclaredField(toName);
            } catch (NoSuchFieldException e) {
                // ignore
            }
            if (field != null && isTransient(field.getModifiers())) {
                getter.toNames = new String[0];
            } else {
                getter.toNames = new String[]{toName};
            }
            getter.name = toName;
            getter.method = method;
            getter.annotations = method.getAnnotations();
            getters.add(getter);
        }
        return getters;
    }

    private static Map<String, Type> collectTypeVariableLookup(Type type) {
        HashMap<String, Type> vars = new HashMap<String, Type>();
        if (null == type) {
            return vars;
        }
        if (type instanceof ParameterizedType) {
            ParameterizedType pType = (ParameterizedType) type;
            Type[] actualTypeArguments = pType.getActualTypeArguments();
            Class clazz = (Class) pType.getRawType();
            for (int i = 0; i < clazz.getTypeParameters().length; i++) {
                TypeVariable variable = clazz.getTypeParameters()[i];
                vars.put(variable.getName() + "@" + clazz.getCanonicalName(), actualTypeArguments[i]);
            }
            vars.putAll(collectTypeVariableLookup(clazz.getGenericSuperclass()));
            return vars;
        }
        if (type instanceof Class) {
            Class clazz = (Class) type;
            vars.putAll(collectTypeVariableLookup(clazz.getGenericSuperclass()));
            return vars;
        }
        throw new JsonException("unexpected type: " + type);
    }

    public List<Binding> allBindings() {
        ArrayList<Binding> bindings = new ArrayList<Binding>(8);
        bindings.addAll(fields);
        if (setters != null) {
            bindings.addAll(setters);
        }
        if (getters != null) {
            bindings.addAll(getters);
        }
        if (ctor != null) {
            bindings.addAll(ctor.parameters);
        }
        if (bindingTypeWrappers != null) {
            for (WrapperDescriptor setter : bindingTypeWrappers) {
                bindings.addAll(setter.parameters);
            }
        }
        return bindings;
    }

    public List<Binding> allDecoderBindings() {
        ArrayList<Binding> bindings = new ArrayList<Binding>(8);
        bindings.addAll(fields);
        bindings.addAll(setters);
        if (ctor != null) {
            bindings.addAll(ctor.parameters);
        }
        for (WrapperDescriptor setter : bindingTypeWrappers) {
            bindings.addAll(setter.parameters);
        }
        return bindings;
    }


    public List<Binding> allEncoderBindings() {
        ArrayList<Binding> bindings = new ArrayList<Binding>(8);
        bindings.addAll(fields);
        bindings.addAll(getters);
        return bindings;
    }

    public List<EncodeTo> encodeTos() {
        HashMap<String, Integer> previousAppearance = new HashMap<String, Integer>();
        ArrayList<EncodeTo> encodeTos = new ArrayList<EncodeTo>(8);
        collectEncodeTo(encodeTos, fields, previousAppearance);
        collectEncodeTo(encodeTos, getters, previousAppearance);
        ArrayList<EncodeTo> removedNulls = new ArrayList<EncodeTo>(encodeTos.size());
        for (EncodeTo encodeTo : encodeTos) {
            if (encodeTo != null) {
                removedNulls.add(encodeTo);
            }
        }
        return removedNulls;
    }

    private void collectEncodeTo(ArrayList<EncodeTo> encodeTos, List<Binding> fields, HashMap<String, Integer> previousAppearance) {
        for (Binding field : fields) {
            for (String toName : field.toNames) {
                if (previousAppearance.containsKey(toName)) {
                    encodeTos.set(previousAppearance.get(toName), null);
                }
                previousAppearance.put(toName, encodeTos.size());
                EncodeTo encodeTo = new EncodeTo();
                encodeTo.binding = field;
                encodeTo.toName = toName;
                encodeTos.add(encodeTo);
            }
        }
    }
}
