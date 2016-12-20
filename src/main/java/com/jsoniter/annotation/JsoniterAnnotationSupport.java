package com.jsoniter.annotation;

import com.jsoniter.JsonException;
import com.jsoniter.spi.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JsoniterAnnotationSupport extends EmptyExtension {

    public static void enable() {
        ExtensionManager.registerExtension(new JsoniterAnnotationSupport());
    }

    @Override
    public void updateClassDescriptor(ClassDescriptor desc) {
        for (Binding field : desc.allDecoderBindings()) {
            JsonIgnore jsonIgnore = getJsonIgnore(field.annotations);
            if (jsonIgnore != null && jsonIgnore.value()) {
                field.fromNames = new String[0];
            }
            JsonProperty jsonProperty = getJsonProperty(field.annotations);
            if (jsonProperty != null) {
                String alternativeField = jsonProperty.value();
                if (!alternativeField.isEmpty()) {
                    field.fromNames = new String[]{alternativeField};
                }
            }
        }
        for (Constructor ctor : desc.clazz.getDeclaredConstructors()) {
            JsonCreator jsonCreator = getJsonCreator(ctor.getAnnotations());
            if (jsonCreator == null) {
                continue;
            }
            desc.ctor.staticMethodName = null;
            desc.ctor.ctor = ctor;
            desc.ctor.staticFactory = null;
            Annotation[][] annotations = ctor.getParameterAnnotations();
            for (int i = 0; i < annotations.length; i++) {
                Annotation[] paramAnnotations = annotations[i];
                JsonProperty jsonProperty = getJsonProperty(paramAnnotations);
                if (jsonProperty == null) {
                    throw new JsonException("must mark all parameters using @JsonProperty: " + ctor);
                }
                Binding binding = new Binding();
                binding.name = jsonProperty.value();
                binding.valueType = ctor.getParameterTypes()[i];
                binding.annotations = paramAnnotations;
                desc.ctor.parameters.add(binding);
            }
        }
        List<Method> allMethods = new ArrayList<Method>();
        Class current = desc.clazz;
        while (current != null) {
            allMethods.addAll(Arrays.asList(current.getDeclaredMethods()));
            current = current.getSuperclass();
        }
        for (Method method : allMethods) {
            if (!Modifier.isStatic(method.getModifiers())) {
                continue;
            }
            JsonCreator jsonCreator = getJsonCreator(method.getAnnotations());
            if (jsonCreator == null) {
                continue;
            }
            desc.ctor.staticMethodName = method.getName();
            desc.ctor.staticFactory = method;
            desc.ctor.ctor = null;
            Annotation[][] annotations = method.getParameterAnnotations();
            for (int i = 0; i < annotations.length; i++) {
                Annotation[] paramAnnotations = annotations[i];
                JsonProperty jsonProperty = getJsonProperty(paramAnnotations);
                if (jsonProperty == null) {
                    throw new JsonException("must mark all parameters using @JsonProperty: " + method);
                }
                Binding binding = new Binding();
                binding.name = jsonProperty.value();
                binding.valueType = method.getParameterTypes()[i];
                binding.annotations = paramAnnotations;
                desc.ctor.parameters.add(binding);
            }
        }
        for (Method method : desc.clazz.getMethods()) {
            if (Modifier.isStatic(method.getModifiers())) {
                continue;
            }
            if (method.getAnnotation(JsonSetter.class) == null) {
                continue;
            }
            SetterDescriptor setter = new SetterDescriptor();
            setter.methodName = method.getName();
            Annotation[][] annotations = method.getParameterAnnotations();
            for (int i = 0; i < annotations.length; i++) {
                Annotation[] paramAnnotations = annotations[i];
                JsonProperty jsonProperty = getJsonProperty(paramAnnotations);
                if (jsonProperty == null) {
                    throw new JsonException("must mark all parameters using @JsonProperty: " + method);
                }
                Binding binding = new Binding();
                binding.name = jsonProperty.value();
                binding.valueType = method.getParameterTypes()[i];
                binding.annotations = paramAnnotations;
                setter.parameters.add(binding);
            }
            desc.setters.add(setter);
        }
    }

    protected JsonCreator getJsonCreator(Annotation[] annotations) {
        return getAnnotation(annotations, JsonCreator.class);
    }

    protected JsonProperty getJsonProperty(Annotation[] annotations) {
        return getAnnotation(annotations, JsonProperty.class);
    }

    protected JsonIgnore getJsonIgnore(Annotation[] annotations) {
        return getAnnotation(annotations, JsonIgnore.class);
    }

    protected static <T extends Annotation> T getAnnotation(Annotation[] annotations, Class<T> annotationClass) {
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
}
