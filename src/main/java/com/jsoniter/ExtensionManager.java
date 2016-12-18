package com.jsoniter;

import com.jsoniter.spi.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ExtensionManager {

    static List<Extension> extensions = new ArrayList<Extension>();

    public static void registerExtension(Extension extension) {
        extensions.add(extension);
    }

    public static void registerTypeDecoder(Class clazz, Decoder decoder) {
        Codegen.addNewDecoder(TypeLiteral.generateDecoderCacheKey(clazz), decoder);
    }

    public static void registerTypeDecoder(TypeLiteral typeLiteral, Decoder decoder) {
        Codegen.addNewDecoder(typeLiteral.cacheKey, decoder);
    }

    public static void registerFieldDecoder(Class clazz, String field, Decoder decoder) {
        Codegen.addNewDecoder(field + "@" + TypeLiteral.generateDecoderCacheKey(clazz), decoder);
    }

    public static void registerFieldDecoder(TypeLiteral typeLiteral, String field, Decoder decoder) {
        Codegen.addNewDecoder(field + "@" + typeLiteral.cacheKey, decoder);
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
        }
        for (Binding binding : desc.allDecoderBindings()) {
            if (binding.fromNames == null) {
                binding.fromNames = new String[]{binding.name};
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
