package com.github.jsoniter;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class TypeLiteral<T> {

    final Type type;
    final String cacheKey;

    /**
     * Constructs a new type literal. Derives represented class from type parameter.
     * <p>
     * <p>Clients create an empty anonymous subclass. Doing so embeds the type parameter in the
     * anonymous class's type hierarchy so we can reconstitute it at runtime despite erasure.
     */
    @SuppressWarnings("unchecked")
    protected TypeLiteral() {
        this.type = getSuperclassTypeParameter(getClass());
        cacheKey = generateCacheKey(type);

    }

    public static String generateCacheKey(Type type) {
        StringBuilder decoderClassName = new StringBuilder("codegen.");
        if (type instanceof Class) {
            Class clazz = (Class) type;
            decoderClassName.append(clazz.getName().replace("[", "array_"));
        } else if (type instanceof ParameterizedType) {
            ParameterizedType pType = (ParameterizedType) type;
            Class clazz = (Class) pType.getRawType();
            decoderClassName.append(clazz.getName().replace("[", "array_"));
            for (int i = 0; i < pType.getActualTypeArguments().length; i++) {
                Class typeArg = (Class) pType.getActualTypeArguments()[i];
                decoderClassName.append('_');
                decoderClassName.append(typeArg.getName());
            }
        } else {
            throw new UnsupportedOperationException("do not know how to handle: " + type);
        }
        return decoderClassName.toString().replace("$", "_");
    }

    static Type getSuperclassTypeParameter(Class<?> subclass) {
        Type superclass = subclass.getGenericSuperclass();
        if (superclass instanceof Class) {
            throw new RuntimeException("Missing type parameter.");
        }
        ParameterizedType parameterized = (ParameterizedType) superclass;
        return parameterized.getActualTypeArguments()[0];
    }

    public Type getType() {
        return type;
    }

    public String getCacheKey() {
        return cacheKey;
    }
}