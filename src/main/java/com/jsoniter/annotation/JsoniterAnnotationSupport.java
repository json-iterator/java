package com.jsoniter.annotation;

import com.jsoniter.*;
import com.jsoniter.spi.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class JsoniterAnnotationSupport extends EmptyExtension {

    public static void enable() {
        ExtensionManager.registerExtension(new JsoniterAnnotationSupport());
    }

    @Override
    public void updateClassDescriptor(ClassDescriptor desc) {
        for (Binding field : desc.allDecoderBindings()) {
            JsonIgnore jsonIgnore = field.getAnnotation(JsonIgnore.class);
            if (jsonIgnore != null) {
                field.fromNames = new String[0];
            }
            JsonProperty jsonProperty = field.getAnnotation(JsonProperty.class);
            if (jsonProperty != null) {
                String alternativeField = jsonProperty.value();
                if (!alternativeField.isEmpty()) {
                    field.fromNames = new String[]{alternativeField};
                }
            }
        }
        for (Constructor ctor : desc.clazz.getConstructors()) {
            Annotation jsonCreator = ctor.getAnnotation(JsonCreator.class);
            if (jsonCreator == null) {
                continue;
            }
            desc.ctor.staticMethodName = null;
            desc.ctor.ctor = ctor;
            desc.ctor.staticFactory = null;
            Annotation[][] annotations = ctor.getParameterAnnotations();
            for (int i = 0; i < annotations.length; i++) {
                Annotation[] paramAnnotations = annotations[i];
                JsonProperty jsonProperty = getAnnotation(paramAnnotations, JsonProperty.class);
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
        for (Method method : desc.clazz.getMethods()) {
            if (!Modifier.isStatic(method.getModifiers())) {
                continue;
            }
            JsonCreator jsonCreator = method.getAnnotation(JsonCreator.class);
            if (jsonCreator == null) {
                continue;
            }
            desc.ctor.staticMethodName = method.getName();
            desc.ctor.staticFactory = method;
            desc.ctor.ctor = null;
            Annotation[][] annotations = method.getParameterAnnotations();
            for (int i = 0; i < annotations.length; i++) {
                Annotation[] paramAnnotations = annotations[i];
                JsonProperty jsonProperty = getAnnotation(paramAnnotations, JsonProperty.class);
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
                JsonProperty jsonProperty = getAnnotation(paramAnnotations, JsonProperty.class);
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
}
