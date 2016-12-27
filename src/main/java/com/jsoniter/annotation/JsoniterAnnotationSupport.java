package com.jsoniter.annotation;

import com.jsoniter.spi.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JsoniterAnnotationSupport extends EmptyExtension {

    public static void enable() {
        ExtensionManager.registerExtension(new JsoniterAnnotationSupport());
    }

    @Override
    public void updateClassDescriptor(ClassDescriptor desc) {
        JsonObject jsonObject = (JsonObject) desc.clazz.getAnnotation(JsonObject.class);
        if (jsonObject != null) {
            if (jsonObject.asExtraForUnknownProperties()) {
                desc.asExtraForUnknownProperties = true;
            }
            for (String fieldName : jsonObject.unknownPropertiesWhitelist()) {
                Binding binding = new Binding(desc.clazz, desc.lookup, Object.class);
                binding.name = fieldName;
                binding.shouldSkip = true;
                desc.fields.add(binding);
            }
            for (String fieldName : jsonObject.unknownPropertiesBlacklist()) {
                Binding binding = new Binding(desc.clazz, desc.lookup, Object.class);
                binding.name = fieldName;
                binding.asExtraWhenPresent = true;
                desc.fields.add(binding);
            }
        }
        updateBindings(desc);
        detectCtorBinding(desc);
        detectStaticFactoryBinding(desc);
        detectSetterBinding(desc);
    }

    private void detectSetterBinding(ClassDescriptor desc) {
        for (Method method : desc.clazz.getMethods()) {
            if (Modifier.isStatic(method.getModifiers())) {
                continue;
            }
            if (method.getAnnotation(JsonSetter.class) == null) {
                continue;
            }
            Annotation[][] annotations = method.getParameterAnnotations();
            String[] paramNames = getParamNames(method, annotations.length);
            SetterDescriptor setter = new SetterDescriptor();
            setter.methodName = method.getName();
            for (int i = 0; i < annotations.length; i++) {
                Annotation[] paramAnnotations = annotations[i];
                Binding binding = new Binding(desc.clazz, desc.lookup, method.getGenericParameterTypes()[i]);
                JsonProperty jsonProperty = getJsonProperty(paramAnnotations);
                if (jsonProperty != null) {
                    binding.name = jsonProperty.value();
                    if (jsonProperty.required()) {
                        binding.asMissingWhenNotPresent = true;
                    }
                }
                if (binding.name == null || binding.name.length() == 0) {
                    binding.name = paramNames[i];
                }
                binding.annotations = paramAnnotations;
                setter.parameters.add(binding);
            }
            desc.setters.add(setter);
        }
    }

    private String[] getParamNames(Object obj, int paramCount) {
        String[] paramNames = new String[paramCount];
        try {
            Object params = reflectCall(obj, "getParameters");
            for (int i = 0; i < paramNames.length; i++) {
                paramNames[i] = (String) reflectCall(Array.get(params, i), "getName");
            }
        } catch (Exception e) {
        }
        return paramNames;
    }

    private Object reflectCall(Object obj, String methodName, Object... args) throws Exception {
        Method method = obj.getClass().getMethod(methodName);
        return method.invoke(obj, args);
    }

    private void detectStaticFactoryBinding(ClassDescriptor desc) {
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
            String[] paramNames = getParamNames(method, annotations.length);
            for (int i = 0; i < annotations.length; i++) {
                Annotation[] paramAnnotations = annotations[i];
                JsonProperty jsonProperty = getJsonProperty(paramAnnotations);
                Binding binding = new Binding(desc.clazz, desc.lookup, method.getGenericParameterTypes()[i]);
                if (jsonProperty != null) {
                    binding.name = jsonProperty.value();
                    if (jsonProperty.required()) {
                        binding.asMissingWhenNotPresent = true;
                    }
                }
                if (binding.name == null || binding.name.length() == 0) {
                    binding.name = paramNames[i];
                }
                binding.annotations = paramAnnotations;
                desc.ctor.parameters.add(binding);
            }
        }
    }

    private void detectCtorBinding(ClassDescriptor desc) {
        for (Constructor ctor : desc.clazz.getDeclaredConstructors()) {
            JsonCreator jsonCreator = getJsonCreator(ctor.getAnnotations());
            if (jsonCreator == null) {
                continue;
            }
            desc.ctor.staticMethodName = null;
            desc.ctor.ctor = ctor;
            desc.ctor.staticFactory = null;
            Annotation[][] annotations = ctor.getParameterAnnotations();
            String[] paramNames = getParamNames(ctor, annotations.length);
            for (int i = 0; i < annotations.length; i++) {
                Annotation[] paramAnnotations = annotations[i];
                JsonProperty jsonProperty = getJsonProperty(paramAnnotations);
                Binding binding = new Binding(desc.clazz, desc.lookup, ctor.getGenericParameterTypes()[i]);
                if (jsonProperty != null) {
                    binding.name = jsonProperty.value();
                    if (jsonProperty.required()) {
                        binding.asMissingWhenNotPresent = true;
                    }
                }
                if (binding.name == null || binding.name.length() == 0) {
                    binding.name = paramNames[i];
                }
                binding.annotations = paramAnnotations;
                desc.ctor.parameters.add(binding);
            }
        }
    }

    private void updateBindings(ClassDescriptor desc) {
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
                if (jsonProperty.required()) {
                    field.asMissingWhenNotPresent = true;
                }
            }
            if (getAnnotation(field.annotations, JsonMissingProperties.class) != null) {
                // this field will not bind from json
                // instead it will be set by jsoniter with missing property names
                field.fromNames = new String[0];
                desc.onMissingProperties = field;
            }
            if (getAnnotation(field.annotations, JsonExtraProperties.class) != null) {
                // this field will not bind from json
                // instead it will be set by jsoniter with extra properties
                field.fromNames = new String[0];
                desc.onExtraProperties = field;
            }
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
