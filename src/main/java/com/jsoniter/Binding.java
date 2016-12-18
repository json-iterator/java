package com.jsoniter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;

public class Binding {
    // input
    public Class clazz;
    public String name;
    public Type valueType;
    public TypeLiteral valueTypeLiteral;
    public Annotation[] annotations;
    // output
    public String[] fromNames;
    public Decoder decoder;
    // optional
    public Field field;
    public int idx;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Binding binding = (Binding) o;

        if (clazz != null ? !clazz.equals(binding.clazz) : binding.clazz != null) return false;
        return name != null ? name.equals(binding.name) : binding.name == null;
    }

    @Override
    public int hashCode() {
        int result = clazz != null ? clazz.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Binding{" +
                "clazz=" + clazz +
                ", name='" + name + '\'' +
                ", valueType=" + valueType +
                '}';
    }
}
