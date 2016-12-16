package com.jsoniter.annotation;

import com.jsoniter.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class JsoniterAnnotationSupport extends EmptyExtension {

    public static void enable() {
        JsonIterator.registerExtension(new JsoniterAnnotationSupport());
    }

    @Override
    public String[] getBindFrom(Binding field) {
        JsonIgnore jsonIgnore = field.getAnnotation(JsonIgnore.class);
        if (jsonIgnore != null) {
            return new String[0];
        }
        JsonProperty jsonProperty = field.getAnnotation(JsonProperty.class);
        if (jsonProperty != null) {
            String alternativeField = jsonProperty.value();
            if (alternativeField.equals(JsonProperty.USE_DEFAULT_NAME)) {
                alternativeField = field.name;
            }
            return new String[]{alternativeField};
        }
        return null;
    }

    @Override
    public CustomizedConstructor getConstructor(Class clazz) {
        for (Constructor ctor : clazz.getConstructors()) {
            Annotation jsonCreator = ctor.getAnnotation(JsonCreator.class);
            if (jsonCreator == null) {
                continue;
            }
            CustomizedConstructor cctor = new CustomizedConstructor();
            cctor.staticMethodName = null;
            for (int i = 0; i < ctor.getParameterAnnotations().length; i++) {
                Annotation[] paramAnnotations = ctor.getParameterAnnotations()[i];
                JsonProperty jsonProperty = getAnnotation(paramAnnotations, JsonProperty.class);
                if (jsonProperty == null) {
                    throw new RuntimeException("must mark all parameters using @JsonProperty: " + ctor);
                }
                Binding binding = new Binding();
                binding.name = jsonProperty.value();
                binding.valueType = ctor.getParameterTypes()[i];
                cctor.parameters.add(binding);
            }
            return cctor;
        }
        for (Method method : clazz.getMethods()) {
            if (!Modifier.isStatic(method.getModifiers())) {
                continue;
            }
            JsonCreator jsonCreator = method.getAnnotation(JsonCreator.class);
            if (jsonCreator == null) {
                continue;
            }
            CustomizedConstructor cctor = new CustomizedConstructor();
            cctor.staticMethodName = method.getName();
            for (int i = 0; i < method.getParameterAnnotations().length; i++) {
                Annotation[] paramAnnotations = method.getParameterAnnotations()[i];
                JsonProperty jsonProperty = getAnnotation(paramAnnotations, JsonProperty.class);
                if (jsonProperty == null) {
                    throw new RuntimeException("must mark all parameters using @JsonProperty: " + method);
                }
                Binding binding = new Binding();
                binding.name = jsonProperty.value();
                binding.valueType = method.getParameterTypes()[i];
                cctor.parameters.add(binding);
            }
            return cctor;
        }
        return null;
    }

    private static <T extends Annotation> T getAnnotation(Annotation[] annotations, Class<T> annotationClass) {
        if (annotations == null) {
            return null;
        }
        for (Annotation annotation : annotations) {
            if (annotationClass.isAssignableFrom(annotation.getClass())) {
                return (T) annotation;
            }
        }
        return null;
    }

    @Override
    public List<CustomizedSetter> getSetters(Class clazz) {
        List<CustomizedSetter> setters = null;
        for (Method method : clazz.getMethods()) {
            if (Modifier.isStatic(method.getModifiers())) {
                continue;
            }
            if (method.getAnnotation(JsonSetter.class) == null) {
                continue;
            }
            if (setters == null) {
                setters = new ArrayList<CustomizedSetter>();
            }
            CustomizedSetter setter = new CustomizedSetter();
            setter.methodName = method.getName();
            for (int i = 0; i < method.getParameterAnnotations().length; i++) {
                Annotation[] paramAnnotations = method.getParameterAnnotations()[i];
                JsonProperty jsonProperty = getAnnotation(paramAnnotations, JsonProperty.class);
                if (jsonProperty == null) {
                    throw new RuntimeException("must mark all parameters using @JsonProperty: " + method);
                }
                Binding binding = new Binding();
                binding.name = jsonProperty.value();
                binding.valueType = method.getParameterTypes()[i];
                setter.parameters.add(binding);
            }
            setters.add(setter);
        }
        return setters;
    }
}
