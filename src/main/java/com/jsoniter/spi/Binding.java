package com.jsoniter.spi;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.Map;

public class Binding {
    // input
    public Class clazz;
    public String name;
    public Type valueType;
    public TypeLiteral valueTypeLiteral;
    public Annotation[] annotations;
    // output
    public String[] fromNames; // for decoder
    public String[] toNames; // for encoder
    public Decoder decoder;
    public Encoder encoder;
    public boolean failOnMissing;
    public boolean failOnPresent;
    public boolean skip;
    // optional
    public Field field;
    public int idx;
    public long mask;

    public Binding(Class clazz, Map<String, Type> lookup, Type valueType) {
        this.clazz = clazz;
        this.valueType = substituteTypeVariables(lookup, valueType);
        this.valueTypeLiteral = TypeLiteral.create(this.valueType);
    }

    private static Type substituteTypeVariables(Map<String, Type> lookup, Type type) {
        if (type instanceof TypeVariable) {
            return translateTypeVariable(lookup, (TypeVariable) type);
        }
        if (type instanceof ParameterizedType) {
            ParameterizedType pType = (ParameterizedType) type;
            Type[] args = pType.getActualTypeArguments();
            for (int i = 0; i < args.length; i++) {
                args[i] = substituteTypeVariables(lookup, args[i]);
            }
            return new ParameterizedTypeImpl(args, pType.getOwnerType(), pType.getRawType());
        }
        if (type instanceof GenericArrayType) {
            GenericArrayType gaType = (GenericArrayType) type;
            return new GenericArrayTypeImpl(substituteTypeVariables(lookup, gaType.getGenericComponentType()));
        }
        return type;
    }

    private static Type translateTypeVariable(Map<String, Type> lookup, TypeVariable var) {
        GenericDeclaration declaredBy = var.getGenericDeclaration();
        if (!(declaredBy instanceof Class)) {
            // if the <T> is not defined by class, there is no way to get the actual type
            return Object.class;
        }
        Class clazz = (Class) declaredBy;
        Type actualType = lookup.get(var.getName() + "@" + clazz.getCanonicalName());
        if (actualType == null) {
            // should not happen
            return Object.class;
        }
        if (actualType instanceof TypeVariable) {
            // translate to another variable, try again
            return translateTypeVariable(lookup, (TypeVariable) actualType);
        }
        return actualType;
    }

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
