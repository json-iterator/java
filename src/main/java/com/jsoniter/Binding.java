package com.jsoniter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

public class Binding {
    public Class clazz;
    public String[] fromNames;
    public String name;
    public Type valueType;
    public Annotation[] annotations;

    public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
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
