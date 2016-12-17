package com.jsoniter;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

class ExtensionManager {
    static List<Extension> extensions = new ArrayList<Extension>();

    public static void registerExtension(Extension extension) {
        extensions.add(extension);
    }

    public static CustomizedConstructor getCtor(Class clazz) {
        for (Extension extension : extensions) {
            CustomizedConstructor ctor = extension.getConstructor(clazz);
            if (ctor != null) {
                for (Binding param : ctor.parameters) {
                    if (param.fromNames == null) {
                        param.fromNames = new String[]{param.name};
                    }
                    param.clazz = clazz;
                    updateFromNames(param);
                }
                return ctor;
            }
        }
        return CustomizedConstructor.DEFAULT_INSTANCE;
    }

    public static List<Binding> getFields(Class clazz) {
        ArrayList<Binding> bindings = new ArrayList<Binding>();
        for (Field field : clazz.getFields()) {
            if (Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            Binding binding = new Binding();
            binding.fromNames = new String[]{field.getName()};
            binding.name = field.getName();
            binding.valueType = field.getType();
            binding.clazz = clazz;
            binding.annotations = field.getAnnotations();
            updateFromNames(binding);
            bindings.add(binding);
        }
        return bindings;
    }

    private static void updateFromNames(Binding binding) {
        for (Extension extension : extensions) {
            if (extension.updateBinding(binding)) {
                break;
            }
        }
    }

    public static List<CustomizedSetter> getSetters(Class clazz) {
        ArrayList<CustomizedSetter> setters = new ArrayList<CustomizedSetter>();
        for (Method method : clazz.getMethods()) {
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
            String fromName = methodName.substring("set".length());
            char[] fromNameChars = fromName.toCharArray();
            fromNameChars[0] = Character.toLowerCase(fromNameChars[0]);
            fromName = new String(fromNameChars);
            CustomizedSetter setter = new CustomizedSetter();
            setter.methodName = methodName;
            Binding param = new Binding();
            param.fromNames = new String[]{fromName};
            param.name = fromName;
            param.valueType = paramTypes[0];
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
                        updateFromNames(param);
                    }
                }
                setters.addAll(moreSetters);
            }
        }
        return setters;
    }
}
