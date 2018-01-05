package com.jsoniter.spi;

import com.jsoniter.any.Any;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

public class TypeLiteral<T> {

    public enum NativeType {
        FLOAT,
        DOUBLE,
        BOOLEAN,
        BYTE,
        SHORT,
        INT,
        CHAR,
        LONG,
        BIG_DECIMAL,
        BIG_INTEGER,
        STRING,
        OBJECT,
        ANY,
    }

    public static Map<Type, NativeType> nativeTypes = new HashMap<Type, NativeType>() {{
        put(float.class, NativeType.FLOAT);
        put(Float.class, NativeType.FLOAT);
        put(double.class, NativeType.DOUBLE);
        put(Double.class, NativeType.DOUBLE);
        put(boolean.class, NativeType.BOOLEAN);
        put(Boolean.class, NativeType.BOOLEAN);
        put(byte.class, NativeType.BYTE);
        put(Byte.class, NativeType.BYTE);
        put(short.class, NativeType.SHORT);
        put(Short.class, NativeType.SHORT);
        put(int.class, NativeType.INT);
        put(Integer.class, NativeType.INT);
        put(char.class, NativeType.CHAR);
        put(Character.class, NativeType.CHAR);
        put(long.class, NativeType.LONG);
        put(Long.class, NativeType.LONG);
        put(BigDecimal.class, NativeType.BIG_DECIMAL);
        put(BigInteger.class, NativeType.BIG_INTEGER);
        put(String.class, NativeType.STRING);
        put(Object.class, NativeType.OBJECT);
        put(Any.class, NativeType.ANY);
    }};

    private volatile static Map<Type, TypeLiteral> typeLiteralCache = new HashMap<Type, TypeLiteral>();
    private final Type type;
    private final String decoderCacheKey;
    private final String encoderCacheKey;
    // TODO: remove native type
    private final NativeType nativeType;

    /**
     * Constructs a new type literal. Derives represented class from type parameter.
     * Clients create an empty anonymous subclass. Doing so embeds the type parameter in the
     * anonymous class's type hierarchy so we can reconstitute it at runtime despite erasure.
     */
    @SuppressWarnings("unchecked")
    protected TypeLiteral() {
        this.type = getSuperclassTypeParameter(getClass());
        this.nativeType = nativeTypes.get(this.type);
        this.decoderCacheKey = generateDecoderCacheKey(type);
        this.encoderCacheKey = generateEncoderCacheKey(type);
    }

    public TypeLiteral(Type type, String decoderCacheKey, String encoderCacheKey) {
        this.type = type;
        this.nativeType = nativeTypes.get(this.type);
        this.decoderCacheKey = decoderCacheKey;
        this.encoderCacheKey = encoderCacheKey;
    }

    private static String generateDecoderCacheKey(Type type) {
        return generateCacheKey(type, "decoder.");
    }

    private static String generateEncoderCacheKey(Type type) {
        return generateCacheKey(type, "encoder.");
    }

    private static String generateCacheKey(Type type, String prefix) {
        StringBuilder decoderClassName = new StringBuilder(prefix);
        if (type instanceof Class) {
            Class clazz = (Class) type;
            if (clazz.isAnonymousClass()) {
                throw new JsonException("anonymous class not supported: " + clazz);
            }
            if (clazz.isArray()) {
                decoderClassName.append(clazz.getCanonicalName().replace("[]", "_array"));
            } else {
                // for nested class $
                decoderClassName.append(clazz.getName().replace("[]", "_array"));
            }
        } else if (type instanceof ParameterizedType) {
            try {
                ParameterizedType pType = (ParameterizedType) type;
                Class clazz = (Class) pType.getRawType();
                decoderClassName.append(clazz.getCanonicalName().replace("[]", "_array"));
                for (int i = 0; i < pType.getActualTypeArguments().length; i++) {
                    String typeName = formatTypeWithoutSpecialCharacter(pType.getActualTypeArguments()[i]);
                    decoderClassName.append('_');
                    decoderClassName.append(typeName);
                }
            } catch (JsonException e) {
                throw e;
            } catch (Exception e) {
                throw new JsonException("failed to generate cache key for: " + type, e);
            }
        } else if (type instanceof GenericArrayType) {
            GenericArrayType gaType = (GenericArrayType) type;
            Type compType = gaType.getGenericComponentType();
            decoderClassName.append(formatTypeWithoutSpecialCharacter(compType));
            decoderClassName.append("_array");
        } else if (type instanceof WildcardType) {
            decoderClassName.append(Object.class.getName());
        } else {
            throw new UnsupportedOperationException("do not know how to handle: " + type);
        }
        return decoderClassName.toString().replace("$", "_");
    }

    private static String formatTypeWithoutSpecialCharacter(Type type) {
        if (type instanceof Class) {
            Class clazz = (Class) type;
            return clazz.getCanonicalName();
        }
        if (type instanceof ParameterizedType) {
            ParameterizedType pType = (ParameterizedType) type;
            StringBuilder typeName = new StringBuilder(formatTypeWithoutSpecialCharacter(pType.getRawType()));
            for (Type typeArg : pType.getActualTypeArguments()) {
                typeName.append("_");
                typeName.append(formatTypeWithoutSpecialCharacter(typeArg));
            }
            return typeName.toString();
        }
        if (type instanceof GenericArrayType) {
            GenericArrayType gaType = (GenericArrayType) type;
            return formatTypeWithoutSpecialCharacter(gaType.getGenericComponentType()) + "_array";
        }
        if (type instanceof WildcardType) {
            return Object.class.getCanonicalName();
        }
        throw new JsonException("unsupported type: " + type + ", of class " + type.getClass());
    }

    private static Type getSuperclassTypeParameter(Class<?> subclass) {
        Type superclass = subclass.getGenericSuperclass();
        if (superclass instanceof Class) {
            throw new JsonException("Missing type parameter.");
        }
        ParameterizedType parameterized = (ParameterizedType) superclass;
        return parameterized.getActualTypeArguments()[0];
    }

    public static TypeLiteral create(Type valueType) {
        TypeLiteral typeLiteral = typeLiteralCache.get(valueType);
        if (typeLiteral != null) {
            return typeLiteral;
        }
        return createNew(valueType);
    }

    private synchronized static TypeLiteral createNew(Type valueType) {
        TypeLiteral typeLiteral = typeLiteralCache.get(valueType);
        if (typeLiteral != null) {
            return typeLiteral;
        }
        HashMap<Type, TypeLiteral> copy = new HashMap<Type, TypeLiteral>(typeLiteralCache);
        typeLiteral = new TypeLiteral(valueType,
                generateDecoderCacheKey(valueType),
                generateEncoderCacheKey(valueType));
        copy.put(valueType, typeLiteral);
        typeLiteralCache = copy;
        return typeLiteral;
    }

    public Type getType() {
        return type;
    }

    public String getDecoderCacheKey() {
        return getDecoderCacheKey(JsoniterSpi.getCurrentConfig().configName());
    }

    public String getDecoderCacheKey(String configName) {
        return configName + decoderCacheKey;
    }

    public String getEncoderCacheKey() {
        return getEncoderCacheKey(JsoniterSpi.getCurrentConfig().configName());
    }

    public String getEncoderCacheKey(String configName) {
        return configName + encoderCacheKey;
    }

    public NativeType getNativeType() {
        return nativeType;
    }

    @Override
    public String toString() {
        return "TypeLiteral{" +
                "type=" + type +
                ", decoderCacheKey='" + decoderCacheKey + '\'' +
                ", encoderCacheKey='" + encoderCacheKey + '\'' +
                '}';
    }
}