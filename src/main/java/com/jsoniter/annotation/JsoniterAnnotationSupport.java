package com.jsoniter.annotation;

import com.jsoniter.JsonException;
import com.jsoniter.spi.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JsoniterAnnotationSupport extends EmptyExtension {

    private static boolean enabled = false;

    public static void enable() {
        if (enabled) {
            return;
        }
        enabled = true;
        JsoniterSpi.registerExtension(new JsoniterAnnotationSupport());
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
        List<Method> allMethods = new ArrayList<Method>();
        Class current = desc.clazz;
        while (current != null) {
            allMethods.addAll(Arrays.asList(current.getDeclaredMethods()));
            current = current.getSuperclass();
        }
        updateBindings(desc);
        detectCtor(desc);
        detectStaticFactory(desc, allMethods);
        detectWrappers(desc, allMethods);
        detectUnwrappers(desc, allMethods);
    }

    private void detectUnwrappers(ClassDescriptor desc, List<Method> allMethods) {
        for (Method method : allMethods) {
            if (Modifier.isStatic(method.getModifiers())) {
                continue;
            }
            if (method.getAnnotation(JsonUnwrapper.class) == null) {
                continue;
            }
            desc.unWrappers.add(method);
        }
    }

    private void detectWrappers(ClassDescriptor desc, List<Method> allMethods) {
        for (Method method : allMethods) {
            if (Modifier.isStatic(method.getModifiers())) {
                continue;
            }
            if (method.getAnnotation(JsonWrapper.class) == null) {
                continue;
            }
            Annotation[][] annotations = method.getParameterAnnotations();
            String[] paramNames = getParamNames(method, annotations.length);
            WrapperDescriptor setter = new WrapperDescriptor();
            setter.method = method;
            for (int i = 0; i < annotations.length; i++) {
                Annotation[] paramAnnotations = annotations[i];
                Binding binding = new Binding(desc.clazz, desc.lookup, method.getGenericParameterTypes()[i]);
                JsonProperty jsonProperty = getJsonProperty(paramAnnotations);
                if (jsonProperty != null) {
                    updateBindingWithJsonProperty(binding, jsonProperty);
                }
                if (binding.name == null || binding.name.length() == 0) {
                    binding.name = paramNames[i];
                }
                binding.annotations = paramAnnotations;
                setter.parameters.add(binding);
            }
            desc.wrappers.add(setter);
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

    private void detectStaticFactory(ClassDescriptor desc, List<Method> allMethods) {
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
                    updateBindingWithJsonProperty(binding, jsonProperty);
                }
                if (binding.name == null || binding.name.length() == 0) {
                    binding.name = paramNames[i];
                }
                binding.annotations = paramAnnotations;
                desc.ctor.parameters.add(binding);
            }
        }
    }

    private void detectCtor(ClassDescriptor desc) {
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
                    updateBindingWithJsonProperty(binding, jsonProperty);
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
        for (Binding binding : desc.allBindings()) {
            JsonIgnore jsonIgnore = getJsonIgnore(binding.annotations);
            if (jsonIgnore != null && jsonIgnore.value()) {
                binding.fromNames = new String[0];
                binding.toNames = new String[0];
            }
            JsonProperty jsonProperty = getJsonProperty(binding.annotations);
            if (jsonProperty != null) {
                updateBindingWithJsonProperty(binding, jsonProperty);
            }
            if (getAnnotation(binding.annotations, JsonMissingProperties.class) != null) {
                // this binding will not bind from json
                // instead it will be set by jsoniter with missing property names
                binding.fromNames = new String[0];
                desc.onMissingProperties = binding;
            }
            if (getAnnotation(binding.annotations, JsonExtraProperties.class) != null) {
                // this binding will not bind from json
                // instead it will be set by jsoniter with extra properties
                binding.fromNames = new String[0];
                desc.onExtraProperties = binding;
            }
        }
    }

    private void updateBindingWithJsonProperty(Binding binding, JsonProperty jsonProperty) {
        binding.asMissingWhenNotPresent = jsonProperty.required();
        binding.isNullable = jsonProperty.nullable();
        binding.isCollectionValueNullable = jsonProperty.collectionValueNullable();
        binding.shouldOmitNull = jsonProperty.omitNull();
        String altName = jsonProperty.value();
        if (!altName.isEmpty()) {
            binding.name = altName;
            binding.fromNames = new String[]{altName};
        }
        if (jsonProperty.from().length > 0) {
            binding.fromNames = jsonProperty.from();
        }
        if (jsonProperty.to().length > 0) {
            binding.toNames = jsonProperty.to();
        }
        if (jsonProperty.decoder() != Decoder.class) {
            try {
                binding.decoder = jsonProperty.decoder().newInstance();
            } catch (Exception e) {
                throw new JsonException(e);
            }
        }
        if (jsonProperty.encoder() != Encoder.class) {
            try {
                binding.encoder = jsonProperty.encoder().newInstance();
            } catch (Exception e) {
                throw new JsonException(e);
            }
        }
        if (jsonProperty.implementation() != Object.class) {
            binding.valueType = ParameterizedTypeImpl.useImpl(binding.valueType, jsonProperty.implementation());
            binding.valueTypeLiteral = TypeLiteral.create(binding.valueType);
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
