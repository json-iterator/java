package com.jsoniter.spi;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.*;

public class ExtensionManager {

    static List<Extension> extensions = new ArrayList<Extension>();
    static volatile Map<String, Encoder> encoders = new HashMap<String, Encoder>();
    static volatile Map<String, Decoder> decoders = new HashMap<String, Decoder>();

    public static void registerExtension(Extension extension) {
        extensions.add(extension);
    }

    public static List<Extension> getExtensions() {
        return Collections.unmodifiableList(extensions);
    }

    public static void registerTypeDecoder(Class clazz, Decoder decoder) {
        addNewDecoder(TypeLiteral.generateDecoderCacheKey(clazz), decoder);
    }

    public static void registerTypeDecoder(TypeLiteral typeLiteral, Decoder decoder) {
        addNewDecoder(typeLiteral.getCacheKey(), decoder);
    }

    public static void registerFieldDecoder(Class clazz, String field, Decoder decoder) {
        addNewDecoder(field + "@" + TypeLiteral.generateDecoderCacheKey(clazz), decoder);
    }

    public static void registerFieldDecoder(TypeLiteral typeLiteral, String field, Decoder decoder) {
        addNewDecoder(field + "@" + typeLiteral.getCacheKey(), decoder);
    }

    public static void registerTypeEncoder(Class clazz, Encoder encoder) {
        addNewEncoder(TypeLiteral.generateEncoderCacheKey(clazz), encoder);
    }

    public static void registerTypeEncoder(TypeLiteral typeLiteral, Encoder encoder) {
        addNewEncoder(typeLiteral.getCacheKey(), encoder);
    }

    public static void registerFieldEncoder(Class clazz, String field, Encoder encoder) {
        addNewEncoder(field + "@" + TypeLiteral.generateEncoderCacheKey(clazz), encoder);
    }

    public static void registerFieldEncoder(TypeLiteral typeLiteral, String field, Encoder encoder) {
        addNewEncoder(field + "@" + typeLiteral.getCacheKey(), encoder);
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

    public static ClassDescriptor getClassDescriptor(Class clazz, boolean includingPrivate) {
        ClassDescriptor desc = new ClassDescriptor();
        desc.clazz = clazz;
        desc.ctor = getCtor(clazz);
        desc.fields = getFields(clazz, includingPrivate);
        desc.setters = getSetters(clazz, includingPrivate);
        desc.getters = getGetters(clazz, includingPrivate);
        for (Extension extension : extensions) {
            extension.updateClassDescriptor(desc);
        }
        if (includingPrivate) {
            if (desc.ctor.ctor != null) {
                desc.ctor.ctor.setAccessible(true);
            }
            if (desc.ctor.staticFactory != null) {
                desc.ctor.staticFactory.setAccessible(true);
            }
            for (SetterDescriptor setter : desc.setters) {
                setter.method.setAccessible(true);
            }
        }
        for (Binding binding : desc.allDecoderBindings()) {
            if (binding.fromNames == null) {
                binding.fromNames = new String[]{binding.name};
            }
            if (binding.toNames == null) {
                binding.toNames = new String[]{binding.name};
            }
            if (binding.field != null && includingPrivate) {
                binding.field.setAccessible(true);
            }
            binding.clazz = clazz;
            binding.valueTypeLiteral = createTypeLiteral(binding.valueType);
        }
        return desc;
    }

    private static ConstructorDescriptor getCtor(Class clazz) {
        ConstructorDescriptor cctor = new ConstructorDescriptor();
        try {
            cctor.ctor = clazz.getDeclaredConstructor();
        } catch (Exception e) {
            cctor.ctor = null;
        }
        return cctor;
    }

    private static List<Binding> getFields(Class clazz, boolean includingPrivate) {
        ArrayList<Binding> bindings = new ArrayList<Binding>();
        for (Field field : getAllFields(clazz, includingPrivate)) {
            if (Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            if (includingPrivate) {
                field.setAccessible(true);
            }
            Binding binding = createBindingFromField(clazz, field);
            bindings.add(binding);
        }
        Binding binding = new Binding();
        binding.fromNames = new String[0];
        binding.name = "*";
        binding.clazz = clazz;
        return bindings;
    }

    private static Binding createBindingFromField(Class clazz, Field field) {
        Binding binding = new Binding();
        binding.fromNames = new String[]{field.getName()};
        binding.name = field.getName();
        binding.valueType = field.getType();
        binding.valueTypeLiteral = createTypeLiteral(binding.valueType);
        binding.clazz = clazz;
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

    private static TypeLiteral createTypeLiteral(Type valueType) {
        return new TypeLiteral(valueType, TypeLiteral.generateDecoderCacheKey(valueType));
    }

    private static List<SetterDescriptor> getSetters(Class clazz, boolean includingPrivate) {
        ArrayList<SetterDescriptor> setters = new ArrayList<SetterDescriptor>();
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
            String fromName = methodName.substring("set".length());
            char[] fromNameChars = fromName.toCharArray();
            fromNameChars[0] = Character.toLowerCase(fromNameChars[0]);
            fromName = new String(fromNameChars);
            SetterDescriptor setter = new SetterDescriptor();
            setter.method = method;
            setter.methodName = methodName;
            Binding param = new Binding();
            param.fromNames = new String[]{fromName};
            param.name = fromName;
            param.valueType = paramTypes[0];
            param.valueTypeLiteral = createTypeLiteral(param.valueType);
            param.clazz = clazz;
            setter.parameters.add(param);
            setters.add(setter);
        }
        return setters;
    }

    private static List<Binding> getGetters(Class clazz, boolean includingPrivate) {
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
            String fromName = methodName.substring("get".length());
            char[] fromNameChars = fromName.toCharArray();
            fromNameChars[0] = Character.toLowerCase(fromNameChars[0]);
            fromName = new String(fromNameChars);
            Binding getter = new Binding();
            getter.fromNames = new String[]{methodName + "()"};
            getter.name = fromName;
            getter.valueType = method.getGenericReturnType();
            getter.clazz = clazz;
            getters.add(getter);
        }
        return getters;
    }
}
