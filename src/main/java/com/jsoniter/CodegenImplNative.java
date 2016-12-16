package com.jsoniter;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

class CodegenImplNative {
    final static Map<String, String> NATIVE_READS = new HashMap<String, String>() {{
        put("float", "iter.readFloat()");
        put("double", "iter.readDouble()");
        put("boolean", "iter.readBoolean()");
        put("byte", "iter.readShort()");
        put("short", "iter.readShort()");
        put("int", "iter.readInt()");
        put("char", "iter.readInt()");
        put("long", "iter.readLong()");
        put(Float.class.getName(), "Float.valueOf(iter.readFloat())");
        put(Double.class.getName(), "Double.valueOf(iter.readDouble())");
        put(Boolean.class.getName(), "Boolean.valueOf(iter.readBoolean())");
        put(Byte.class.getName(), "Byte.valueOf((byte)iter.readShort())");
        put(Character.class.getName(), "Character.valueOf((char)iter.readShort())");
        put(Short.class.getName(), "Short.valueOf(iter.readShort())");
        put(Integer.class.getName(), "Integer.valueOf(iter.readInt())");
        put(Long.class.getName(), "Long.valueOf(iter.readLong())");
        put(BigDecimal.class.getName(), "iter.readBigDecimal()");
        put(BigInteger.class.getName(), "iter.readBigInteger()");
        put(String.class.getName(), "iter.readString()");
        put(Object.class.getName(), "iter.readAnyObject()");
        put(Any.class.getName(), "iter.readAny()");
    }};

    public static String genNative(String nativeReadKey) {
        if ("boolean".equals(nativeReadKey)) {
            nativeReadKey = Boolean.class.getName();
        } else if ("byte".equals(nativeReadKey)) {
            nativeReadKey = Byte.class.getName();
        } else if ("char".equals(nativeReadKey)) {
            nativeReadKey = Character.class.getName();
        } else if ("short".equals(nativeReadKey)) {
            nativeReadKey = Short.class.getName();
        } else if ("int".equals(nativeReadKey)) {
            nativeReadKey = Integer.class.getName();
        } else if ("long".equals(nativeReadKey)) {
            nativeReadKey = Long.class.getName();
        } else if ("float".equals(nativeReadKey)) {
            nativeReadKey = Float.class.getName();
        } else if ("double".equals(nativeReadKey)) {
            nativeReadKey = Double.class.getName();
        }
        String op = NATIVE_READS.get(nativeReadKey);
        if (op == null) {
            throw new RuntimeException("do not know how to read: " + nativeReadKey);
        }
        StringBuilder lines = new StringBuilder();
        append(lines, "public static Object decode_(com.jsoniter.JsonIterator iter) {");
        append(lines, "return " + op + ";");
        append(lines, "}");
        return lines.toString();
    }

    public static String genReadOp(Type type) {
        if (type instanceof Class) {
            Class clazz = (Class) type;
            String nativeRead = NATIVE_READS.get(clazz.getCanonicalName());
            if (nativeRead != null) {
                return nativeRead;
            }
        }
        String cacheKey = TypeLiteral.generateCacheKey(type);
        Decoder decoder = Codegen.getDecoder(cacheKey, type);// set the decoder to cache
        if (cacheKey.equals(decoder.getClass().getCanonicalName())) {
            return String.format("%s.decode_(iter)", cacheKey);
        } else {
            // can not use static "decode_" method to access, go through codegen cache
            return String.format("com.jsoniter.CodegenAccess.read(\"%s\", iter);", cacheKey);
        }
    }

    public static String genField(Binding field, String cacheKey) {
        Type fieldType = field.valueType;
        String fieldCacheKey = field.name + "@" + cacheKey;
        Decoder decoder = createFieldDecoder(fieldCacheKey, field);
        if (decoder == null) {
            return String.format("(%s)%s", getTypeName(fieldType), CodegenImplNative.genReadOp(fieldType));
        }
        if (fieldType == boolean.class) {
            if (!(decoder instanceof Decoder.BooleanDecoder)) {
                throw new RuntimeException("decoder for field " + field + "must implement Decoder.BooleanDecoder");
            }
            return String.format("com.jsoniter.CodegenAccess.readBoolean(\"%s\", iter)", fieldCacheKey);
        }
        if (fieldType == byte.class) {
            if (!(decoder instanceof Decoder.ShortDecoder)) {
                throw new RuntimeException("decoder for field " + field + "must implement Decoder.ShortDecoder");
            }
            return String.format("com.jsoniter.CodegenAccess.readShort(\"%s\", iter)", fieldCacheKey);
        }
        if (fieldType == short.class) {
            if (!(decoder instanceof Decoder.ShortDecoder)) {
                throw new RuntimeException("decoder for field " + field + "must implement Decoder.ShortDecoder");
            }
            return String.format("com.jsoniter.CodegenAccess.readShort(\"%s\", iter)", fieldCacheKey);
        }
        if (fieldType == char.class) {
            if (!(decoder instanceof Decoder.IntDecoder)) {
                throw new RuntimeException("decoder for field " + field + "must implement Decoder.IntDecoder");
            }
            return String.format("com.jsoniter.CodegenAccess.readInt(\"%s\", iter)", fieldCacheKey);
        }
        if (fieldType == int.class) {
            if (!(decoder instanceof Decoder.IntDecoder)) {
                throw new RuntimeException("decoder for field " + field + "must implement Decoder.IntDecoder");
            }
            return String.format("com.jsoniter.CodegenAccess.readInt(\"%s\", iter)", fieldCacheKey);
        }
        if (fieldType == long.class) {
            if (!(decoder instanceof Decoder.LongDecoder)) {
                throw new RuntimeException("decoder for field " + field + "must implement Decoder.LongDecoder");
            }
            return String.format("com.jsoniter.CodegenAccess.readLong(\"%s\", iter)", fieldCacheKey);
        }
        if (fieldType == float.class) {
            if (!(decoder instanceof Decoder.FloatDecoder)) {
                throw new RuntimeException("decoder for field " + field + "must implement Decoder.FloatDecoder");
            }
            return String.format("com.jsoniter.CodegenAccess.readFloat(\"%s\", iter)", fieldCacheKey);
        }
        if (fieldType == double.class) {
            if (!(decoder instanceof Decoder.DoubleDecoder)) {
                throw new RuntimeException("decoder for field " + field + "must implement Decoder.DoubleDecoder");
            }
            return String.format("com.jsoniter.CodegenAccess.readDouble(\"%s\", iter)", fieldCacheKey);
        }
        Codegen.getDecoder(fieldCacheKey, fieldType); // put decoder into cache
        return String.format("(%s)com.jsoniter.CodegenAccess.read(\"%s\", iter);",
                getTypeName(fieldType), fieldCacheKey);
    }

    private static Decoder createFieldDecoder(String fieldCacheKey, Binding field) {
        // directly registered field decoder
        Decoder decoder = Codegen.cache.get(fieldCacheKey);
        if (decoder != null) {
            return decoder;
        }
        // provided by extension
        decoder = ExtensionManager.createFieldDecoder(fieldCacheKey, field);
        if (decoder != null) {
            Codegen.addNewDecoder(fieldCacheKey, decoder);
            return decoder;
        }
        return null;
    }

    public static String getTypeName(Type fieldType) {
        if (fieldType instanceof Class) {
            Class clazz = (Class) fieldType;
            return clazz.getCanonicalName();
        } else if (fieldType instanceof ParameterizedType) {
            ParameterizedType pType = (ParameterizedType) fieldType;
            Class clazz = (Class) pType.getRawType();
            return clazz.getCanonicalName();
        } else {
            throw new RuntimeException("unsupported type: " + fieldType);
        }
    }

    private static void append(StringBuilder lines, String str) {
        lines.append(str);
        lines.append("\n");
    }
}
