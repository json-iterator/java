package com.jsoniter.spi;

import com.jsoniter.JsonException;

import java.lang.reflect.*;
import java.util.*;

public class JsoniterSpi {

    static List<Extension> extensions = new ArrayList<Extension>();
    static Map<Class, Class> typeImpls = new HashMap<Class, Class>();
    static volatile Map<String, Encoder> encoders = new HashMap<String, Encoder>();
    static volatile Map<String, Decoder> decoders = new HashMap<String, Decoder>();
    static volatile Map<Class, Extension> objectFactories = new HashMap<Class, Extension>();

    public static void registerExtension(Extension extension) {
        extensions.add(extension);
    }

    public static List<Extension> getExtensions() {
        return Collections.unmodifiableList(extensions);
    }

    public static void registerTypeImplementation(Class superClazz, Class implClazz) {
        typeImpls.put(superClazz, implClazz);
    }

    public static Class getTypeImplementation(Class superClazz) {
        return typeImpls.get(superClazz);
    }

    public static void registerTypeDecoder(Class clazz, Decoder decoder) {
        addNewDecoder(TypeLiteral.create(clazz).getDecoderCacheKey(), decoder);
    }

    public static void registerTypeDecoder(TypeLiteral typeLiteral, Decoder decoder) {
        addNewDecoder(typeLiteral.getDecoderCacheKey(), decoder);
    }

    public static void registerPropertyDecoder(Class clazz, String field, Decoder decoder) {
        addNewDecoder(field + "@" + TypeLiteral.create(clazz).getDecoderCacheKey(), decoder);
    }

    public static void registerPropertyDecoder(TypeLiteral typeLiteral, String field, Decoder decoder) {
        addNewDecoder(field + "@" + typeLiteral.getDecoderCacheKey(), decoder);
    }

    public static void registerTypeEncoder(Class clazz, Encoder encoder) {
        addNewEncoder(TypeLiteral.create(clazz).getEncoderCacheKey(), encoder);
    }

    public static void registerTypeEncoder(TypeLiteral typeLiteral, Encoder encoder) {
        addNewEncoder(typeLiteral.getDecoderCacheKey(), encoder);
    }

    public static void registerPropertyEncoder(Class clazz, String field, Encoder encoder) {
        addNewEncoder(field + "@" + TypeLiteral.create(clazz).getEncoderCacheKey(), encoder);
    }

    public static void registerPropertyEncoder(TypeLiteral typeLiteral, String field, Encoder encoder) {
        addNewEncoder(field + "@" + typeLiteral.getDecoderCacheKey(), encoder);
    }

    public static Decoder getDecoder(String cacheKey) {
        return decoders.get(cacheKey);
    }

    public synchronized static void addNewDecoder(String cacheKey, Decoder decoder) {
        HashMap<String, Decoder> newCache = new HashMap<String, Decoder>(decoders);
        newCache.put(cacheKey, decoder);
        decoders = newCache;
    }

    public static Encoder getEncoder(String cacheKey) {
        return encoders.get(cacheKey);
    }

    public synchronized static void addNewEncoder(String cacheKey, Encoder encoder) {
        HashMap<String, Encoder> newCache = new HashMap<String, Encoder>(encoders);
        newCache.put(cacheKey, encoder);
        encoders = newCache;
    }

    public static boolean canCreate(Class clazz) {
        if (objectFactories.containsKey(clazz)) {
            return true;
        }
        for (Extension extension : extensions) {
            if (extension.canCreate(clazz)) {
                addObjectFactory(clazz, extension);
                return true;
            }
        }
        return false;
    }

    public static Object create(Class clazz) {
        return objectFactories.get(clazz).create(clazz);
    }

    private synchronized static void addObjectFactory(Class clazz, Extension extension) {
        HashMap<Class, Extension> copy = new HashMap<Class, Extension>(objectFactories);
        copy.put(clazz, extension);
        objectFactories = copy;
    }

    public static ClassDescriptor getDecodingClassDescriptor(Class clazz, boolean includingPrivate) {
        Map<String, Type> lookup = collectTypeVariableLookup(clazz);
        ClassDescriptor desc = new ClassDescriptor();
        desc.clazz = clazz;
        desc.lookup = lookup;
        desc.ctor = getCtor(clazz);
        desc.fields = getFields(lookup, clazz, includingPrivate);
        desc.setters = getSetters(lookup, clazz, includingPrivate);
        desc.wrappers = new ArrayList<WrapperDescriptor>();
        desc.unwrappers = new ArrayList<Method>();
        for (Extension extension : extensions) {
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
            for (WrapperDescriptor setter : desc.wrappers) {
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

    public static ClassDescriptor getEncodingClassDescriptor(Class clazz, boolean includingPrivate) {
        Map<String, Type> lookup = collectTypeVariableLookup(clazz);
        ClassDescriptor desc = new ClassDescriptor();
        desc.clazz = clazz;
        desc.lookup = lookup;
        desc.fields = getFields(lookup, clazz, includingPrivate);
        desc.getters = getGetters(lookup, clazz, includingPrivate);
        desc.wrappers = new ArrayList<WrapperDescriptor>();
        desc.unwrappers = new ArrayList<Method>();
        for (Extension extension : extensions) {
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

    private static void decodingDeduplicate(ClassDescriptor desc) {
        HashMap<String, Binding> byName = new HashMap<String, Binding>();
        for (Binding field : desc.fields) {
            if (byName.containsKey(field.name)) {
                throw new JsonException("field name conflict: " + field.name);
            }
            byName.put(field.name, field);
        }
        for (Binding setter : desc.setters) {
            Binding existing = byName.get(setter.name);
            if (existing == null) {
                byName.put(setter.name, setter);
                continue;
            }
            if (desc.fields.remove(existing)) {
                continue;
            }
            throw new JsonException("setter name conflict: " + setter.name);
        }
        for (WrapperDescriptor wrapper : desc.wrappers) {
            for (Binding param : wrapper.parameters) {
                Binding existing = byName.get(param.name);
                if (existing == null) {
                    byName.put(param.name, param);
                    continue;
                }
                if (desc.fields.remove(existing)) {
                    continue;
                }
                if (desc.setters.remove(existing)) {
                    continue;
                }
                throw new JsonException("wrapper parameter name conflict: " + param.name);
            }
        }
        for (Binding param : desc.ctor.parameters) {
            Binding existing = byName.get(param.name);
            if (existing == null) {
                byName.put(param.name, param);
                continue;
            }
            if (desc.fields.remove(existing)) {
                continue;
            }
            if (desc.setters.remove(existing)) {
                continue;
            }
            throw new JsonException("ctor parameter name conflict: " + param.name);
        }
    }

    private static void encodingDeduplicate(ClassDescriptor desc) {
        HashMap<String, Binding> byName = new HashMap<String, Binding>();
        for (Binding field : desc.fields) {
            if (byName.containsKey(field.name)) {
                throw new JsonException("field name conflict: " + field.name);
            }
            byName.put(field.name, field);
        }
        for (Binding getter : desc.getters) {
            Binding existing = byName.get(getter.name);
            if (existing == null) {
                byName.put(getter.name, getter);
                continue;
            }
            if (desc.fields.remove(existing)) {
                continue;
            }
            throw new JsonException("getter name conflict: " + getter.name);
        }
    }

    private static ConstructorDescriptor getCtor(Class clazz) {
        ConstructorDescriptor cctor = new ConstructorDescriptor();
        if (canCreate(clazz)) {
            cctor.objectFactory = objectFactories.get(clazz);
            return cctor;
        }
        try {
            cctor.ctor = clazz.getDeclaredConstructor();
        } catch (Exception e) {
            cctor.ctor = null;
        }
        return cctor;
    }

    private static List<Binding> getFields(Map<String, Type> lookup, Class clazz, boolean includingPrivate) {
        ArrayList<Binding> bindings = new ArrayList<Binding>();
        for (Field field : getAllFields(clazz, includingPrivate)) {
            if (Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            if (includingPrivate) {
                field.setAccessible(true);
            }
            Binding binding = createBindingFromField(lookup, clazz, field);
            bindings.add(binding);
        }
        return bindings;
    }

    private static Binding createBindingFromField(Map<String, Type> lookup, Class clazz, Field field) {
        Binding binding = new Binding(clazz, lookup, field.getGenericType());
        binding.fromNames = new String[]{field.getName()};
        binding.name = field.getName();
        binding.annotations = field.getAnnotations();
        binding.field = field;
        return binding;
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

    private static List<Binding> getSetters(Map<String, Type> lookup, Class clazz, boolean includingPrivate) {
        ArrayList<Binding> setters = new ArrayList<Binding>();
        List<Method> allMethods = Arrays.asList(clazz.getMethods());
        if (includingPrivate) {
            allMethods = new ArrayList<Method>();
            Class current = clazz;
            while (current != null) {
                allMethods.addAll(Arrays.asList(current.getDeclaredMethods()));
                current = current.getSuperclass();
            }
        }
        for (Method method : allMethods) {
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
            if (includingPrivate) {
                method.setAccessible(true);
            }
            String fromName = translateSetterName(methodName);
            Binding binding = new Binding(clazz, lookup, paramTypes[0]);
            binding.fromNames = new String[]{fromName};
            binding.name = fromName;
            binding.method = method;
            binding.annotations = method.getAnnotations();
            setters.add(binding);
        }
        return setters;
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

    private static List<Binding> getGetters(Map<String, Type> lookup, Class clazz, boolean includingPrivate) {
        ArrayList<Binding> getters = new ArrayList<Binding>();
        for (Method method : clazz.getMethods()) {
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
            char[] fromNameChars = toName.toCharArray();
            fromNameChars[0] = Character.toLowerCase(fromNameChars[0]);
            toName = new String(fromNameChars);
            Binding getter = new Binding(clazz, lookup, method.getGenericReturnType());
            getter.toNames = new String[]{toName};
            getter.name = toName;
            getter.method = method;
            getter.annotations = method.getAnnotations();
            getters.add(getter);
        }
        return getters;
    }

    public static void dump() {
        for (String cacheKey : decoders.keySet()) {
            System.err.println(cacheKey);
        }
        for (String cacheKey : encoders.keySet()) {
            System.err.println(cacheKey);
        }
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
}
