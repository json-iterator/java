package com.jsoniter;

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

    public static CustomizedConstructor getCtor(Class clazz) {
        return getCtor(clazz, false);
    }

    public static CustomizedConstructor getCtor(Class clazz, boolean includingPrivate) {
        for (Extension extension : extensions) {
            CustomizedConstructor ctor = extension.getConstructor(clazz);
            if (ctor != null) {
                if (ctor.ctor != null && includingPrivate) {
                    ctor.ctor.setAccessible(true);
                }
                for (Binding param : ctor.parameters) {
                    if (param.fromNames == null) {
                        param.fromNames = new String[]{param.name};
                    }
                    param.clazz = clazz;
                    param.valueTypeLiteral = createTypeLiteral(param.valueType);
                    updateFromNames(param);
                }
                return ctor;
            }
        }
        CustomizedConstructor cctor = new CustomizedConstructor();
        try {
            cctor.ctor = clazz.getDeclaredConstructor();
            if (includingPrivate) {
                cctor.ctor.setAccessible(true);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return cctor;
    }

    public static List<Binding> getFields(Class clazz) {
        return getFields(clazz, false);
    }

    public static List<Binding> getFields(Class clazz, boolean includingPrivate) {
        ArrayList<Binding> bindings = new ArrayList<Binding>();
        List<Field> allFields = Arrays.asList(clazz.getFields());
        if (includingPrivate) {
            allFields = new ArrayList<Field>();
            Class current = clazz;
            while (current != null) {
                allFields.addAll(Arrays.asList(current.getDeclaredFields()));
                current = current.getSuperclass();
            }
        }
        for (Field field : allFields) {
            if (Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            if (includingPrivate) {
                field.setAccessible(true);
            }
            Binding binding = new Binding();
            binding.fromNames = new String[]{field.getName()};
            binding.name = field.getName();
            binding.valueType = field.getType();
            binding.valueTypeLiteral = createTypeLiteral(binding.valueType);
            binding.clazz = clazz;
            binding.annotations = field.getAnnotations();
            binding.field = field;
            updateFromNames(binding);
            bindings.add(binding);
        }
        return bindings;
    }

    private static TypeLiteral createTypeLiteral(Type valueType) {
        return new TypeLiteral(valueType, TypeLiteral.generateDecoderCacheKey(valueType));
    }

    private static void updateFromNames(Binding binding) {
        for (Extension extension : extensions) {
            if (extension.updateBinding(binding)) {
                break;
            }
        }
    }

    public static List<CustomizedSetter> getSetters(Class clazz) {
        return getSetters(clazz, false);
    }

    public static List<CustomizedSetter> getSetters(Class clazz, boolean includingPrivate) {
        ArrayList<CustomizedSetter> setters = new ArrayList<CustomizedSetter>();
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
            CustomizedSetter setter = new CustomizedSetter();
            setter.method = method;
            setter.methodName = methodName;
            Binding param = new Binding();
            param.fromNames = new String[]{fromName};
            param.name = fromName;
            param.valueType = paramTypes[0];
            param.valueTypeLiteral = createTypeLiteral(param.valueType);
            param.clazz = clazz;
            updateFromNames(param);
            setter.parameters.add(param);
            setters.add(setter);
        }
        for (Extension extension : extensions) {
            List<CustomizedSetter> moreSetters = extension.getSetters(clazz);
            if (moreSetters != null) {
                for (CustomizedSetter moreSetter : moreSetters) {
                    for (Binding param : moreSetter.parameters) {
                        if (param.fromNames == null) {
                            param.fromNames = new String[]{param.name};
                        }
                        param.clazz = clazz;
                        param.valueTypeLiteral = createTypeLiteral(param.valueType);
                        updateFromNames(param);
                    }
                }
                setters.addAll(moreSetters);
            }
        }
        return setters;
    }

    public static List<Binding> getGetters(Class clazz) {
        return getGetters(clazz, false);
    }

    public static List<Binding> getGetters(Class clazz, boolean includingPrivate) {
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
}
