package com.jsoniter;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

class ExtensionManager {
    static List<Extension> extensions = new ArrayList<Extension>();

    public static void registerExtension(Extension extension) {
        extensions.add(extension);
    }

    public static Decoder createFieldDecoder(String fieldCacheKey, Binding field) {
        for (Extension extension : extensions) {
            Decoder decoder = extension.createDecoder(field);
            if (decoder != null) {
                return decoder;
            }
        }
        return null;
    }

    public static CustomizedConstructor getCtor(Class clazz) {
        for (Extension extension : extensions) {
            CustomizedConstructor ctor = extension.getConstructor(clazz);
            if (ctor != null) {
                for (Binding param : ctor.parameters) {
                    param.clazz = clazz;
                }
                return ctor;
            }
        }
        return CustomizedConstructor.DEFAULT_INSTANCE;
    }

    public static List<Binding> getFields(Class clazz) {
        ArrayList<Binding> bindings = new ArrayList<Binding>();
        for (Field field : clazz.getFields()) {
            Binding binding = new Binding();
            binding.fromNames = new String[]{field.getName()};
            for (Extension extension : extensions) {
                String[] fromNames = extension.getBindFrom(binding);
                if (fromNames != null) {
                    binding.fromNames = fromNames;
                    break;
                }
            }
            binding.name = field.getName();
            binding.valueType = field.getType();
            binding.clazz = clazz;
            binding.field = field;
            bindings.add(binding);
        }
        return bindings;
    }

    public static List<CustomizedSetter> getSetters(Class clazz) {
        ArrayList<CustomizedSetter> setters = new ArrayList<CustomizedSetter>();
        for (Method method : clazz.getMethods()) {
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
            String fromName = methodName.substring("set".length());
            char[] fromNameChars = fromName.toCharArray();
            fromNameChars[0] = Character.toLowerCase(fromNameChars[0]);
            fromName = new String(fromNameChars);
            CustomizedSetter setter = new CustomizedSetter();
            setter.methodName = methodName;
            Binding binding = new Binding();
            binding.fromNames = new String[]{fromName};
            binding.name = fromName;
            binding.valueType = paramTypes[0];
            binding.clazz = clazz;
            setter.parameters.add(binding);
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
                    }
                }
                setters.addAll(moreSetters);
            }
        }
        return setters;
    }
}
