package com.github.jsoniter;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

class Codegen {
    final static Map<String, String> NATIVE_READS = new HashMap<String, String>() {{
        put("float", "iter.readFloat()");
        put("double", "iter.readDouble()");
        put("boolean", "iter.readBoolean()");
        put("byte", "iter.readShort()");
        put("short", "iter.readShort()");
        put("int", "iter.readInt()");
        put("long", "iter.readLong()");
        put(Float.class.getName(), "Float.valueOf(iter.readFloat())");
        put(Double.class.getName(), "Double.valueOf(iter.readDouble())");
        put(Boolean.class.getName(), "Boolean.valueOf(iter.readBoolean())");
        put(Byte.class.getName(), "Byte.valueOf(iter.readShort())");
        put(Short.class.getName(), "Short.valueOf(iter.readShort())");
        put(Integer.class.getName(), "Integer.valueOf(iter.readInt())");
        put(Long.class.getName(), "Long.valueOf(iter.readLong())");
        put(String.class.getName(), "iter.readString()");
    }};
    final static Set<Class> WITH_CAPACITY_COLLECTION_CLASSES = new HashSet<Class>(){{
        add(ArrayList.class);
        add(HashSet.class);
    }};
    static volatile Map<String, Decoder> cache = new HashMap<>();
    static ClassPool pool = ClassPool.getDefault();

    static Decoder getDecoder(String cacheKey, Type type, Type[] typeArgs) {
        Decoder decoder = cache.get(cacheKey);
        if (decoder != null) {
            return decoder;
        }
        return gen(cacheKey, type, typeArgs);
    }

    private synchronized static Decoder gen(String cacheKey, Type type, Type[] typeArgs) {
        Decoder decoder = cache.get(cacheKey);
        if (decoder != null) {
            return decoder;
        }
        Class clazz;
        if (type instanceof ParameterizedType) {
            ParameterizedType pType = (ParameterizedType) type;
            clazz = (Class) pType.getRawType();
            typeArgs = pType.getActualTypeArguments();
        } else {
            clazz = (Class) type;
        }
        String source = genSource(cacheKey, typeArgs, clazz);
        try {
            CtClass ctClass = pool.makeClass(cacheKey);
            ctClass.setInterfaces(new CtClass[]{pool.get(Decoder.class.getName())});
            CtMethod method = CtNewMethod.make(source, ctClass);
            ctClass.addMethod(method);
            decoder = (Decoder) ctClass.toClass().newInstance();
            addNewDecoder(cacheKey, decoder);
            return decoder;
        } catch (Exception e) {
            System.err.println("failed to generate encoder for: " + type + " with " + Arrays.toString(typeArgs));
            System.err.println(source);
            throw new RuntimeException(e);
        }
    }

    private static String genSource(String cacheKey, Type[] typeArgs, Class clazz) {
        if (clazz.isArray()) {
            return genArray(clazz);
        } else if (Collection.class.isAssignableFrom(clazz)) {
            Class compType = (Class) typeArgs[0];
            if (clazz == List.class) {
                clazz = ArrayList.class;
            } else if (clazz == Set.class) {
                clazz = HashSet.class;
            }
            if (WITH_CAPACITY_COLLECTION_CLASSES.contains(clazz)) {
                return genCollectionWithCapacity(clazz, compType);
            } else {
                return genCollection(clazz, compType);
            }
        } else {
            return genObject(clazz, cacheKey);
        }
    }

    public static void addNewDecoder(String cacheKey, Decoder decoder) {
        HashMap<String, Decoder> newCache = new HashMap<>(cache);
        newCache.put(cacheKey, decoder);
        cache = newCache;
    }

    private static String genObject(Class clazz, String cacheKey) {
        Map<Integer, Object> map = new HashMap<>();
        for (Field field : clazz.getFields()) {
            byte[] fieldName = field.getName().getBytes();
            Map<Byte, Object> current = (Map<Byte, Object>) map.get(fieldName.length);
            if (current == null) {
                current = new HashMap<>();
                map.put(fieldName.length, current);
            }
            for (int i = 0; i < fieldName.length - 1; i++) {
                byte b = fieldName[i];
                Map<Byte, Object> next = (Map<Byte, Object>) current.get(b);
                if (next == null) {
                    next = new HashMap<>();
                    current.put(b, next);
                }
                current = next;
            }
            current.put(fieldName[fieldName.length - 1], field);
        }
        if (map.isEmpty()) {
            StringBuilder lines = new StringBuilder();
            append(lines, "public Object decode(java.lang.reflect.Type type, com.github.jsoniter.Jsoniter iter) {");
            append(lines, "{{clazz}} obj = new {{clazz}}();");
            append(lines, "iter.skip();");
            append(lines, "return obj;");
            append(lines, "}");
            return lines.toString().replace("{{clazz}}", clazz.getName());
        }
        StringBuilder lines = new StringBuilder();
        append(lines, "public Object decode(java.lang.reflect.Type type, com.github.jsoniter.Jsoniter iter) {");
        append(lines, "{{clazz}} obj = new {{clazz}}();");
        append(lines, "for (com.github.jsoniter.Slice field = iter.readObjectAsSlice(); field != null; field = iter.readObjectAsSlice()) {");
        append(lines, "switch (field.len) {");
        for (Map.Entry<Integer, Object> entry : map.entrySet()) {
            Integer len = entry.getKey();
            append(lines, "case " + len + ": ");
            Map<Byte, Object> current = (Map<Byte, Object>) entry.getValue();
            addFieldDispatch(lines, len, 0, current, cacheKey);
            append(lines, "break;");
        }
        append(lines, "}");
        append(lines, "iter.skip();");
        append(lines, "}");
        append(lines, "return obj;");
        append(lines, "}");
        return lines.toString().replace("{{clazz}}", clazz.getName());
    }

    private static void addFieldDispatch(StringBuilder lines, int len, int i, Map<Byte, Object> current, String cacheKey) {
        for (Map.Entry<Byte, Object> entry : current.entrySet()) {
            Byte b = entry.getKey();
            append(lines, String.format("if (field.at(%d)==%s) {", i, b));
            if (i == len - 1) {
                genField(lines, (Field) entry.getValue(), cacheKey);
                append(lines, "continue;");
            } else {
                addFieldDispatch(lines, len, i + 1, (Map<Byte, Object>) entry.getValue(), cacheKey);
            }
            append(lines, "}");
        }
    }

    private static void genField(StringBuilder lines, Field field, String cacheKey) {
        String fieldCacheKey = field.getName() + "@" + cacheKey;
        String fieldTypeName = field.getType().getCanonicalName();
        String nativeRead = NATIVE_READS.get(fieldTypeName);
        if (cache.containsKey(fieldCacheKey)) {
            append(lines, String.format("obj.%s = (%s)iter.read(\"%s\", %s.class);",
                    field.getName(), fieldTypeName, fieldCacheKey, fieldTypeName));
            return;
        }
        if (nativeRead == null) {
            if (field.getGenericType() instanceof ParameterizedType) {
                ParameterizedType pType = (ParameterizedType) field.getGenericType();
                Class arg1 = (Class) pType.getActualTypeArguments()[0];
                append(lines, String.format("obj.%s = (%s)iter.read(\"%s\", %s.class, %s.class);",
                        field.getName(), fieldTypeName, TypeLiteral.generateCacheKey(field.getGenericType()),
                        fieldTypeName, arg1.getName()));
            } else {
                append(lines, String.format("obj.%s = (%s)iter.read(%s.class);",
                        field.getName(), fieldTypeName, fieldTypeName));
            }
        } else {
            append(lines, String.format("obj.%s = %s;", field.getName(), nativeRead));
        }
    }

    private static String genArray(Class clazz) {
        Class compType = clazz.getComponentType();
        if (compType.isArray()) {
            throw new IllegalArgumentException("nested array not supported: " + clazz.getCanonicalName());
        }
        String nativeRead = NATIVE_READS.get(compType.getName());
        StringBuilder lines = new StringBuilder();
        append(lines, "public Object decode(java.lang.reflect.Type type, com.github.jsoniter.Jsoniter iter) {");
        append(lines, "if (!iter.readArray()) {");
        append(lines, "return new {{comp}}[0];");
        append(lines, "}");
        append(lines, "{{comp}} a1 = ({{comp}}) {{op}};");
        append(lines, "if (!iter.readArray()) {");
        append(lines, "return new {{comp}}[]{ a1 };");
        append(lines, "}");
        append(lines, "{{comp}} a2 = ({{comp}}) {{op}};");
        append(lines, "if (!iter.readArray()) {");
        append(lines, "return new {{comp}}[]{ a1, a2 };");
        append(lines, "}");
        append(lines, "{{comp}} a3 = ({{comp}}) {{op}};");
        append(lines, "if (!iter.readArray()) {");
        append(lines, "return new {{comp}}[]{ a1, a2, a3 };");
        append(lines, "}");
        append(lines, "{{comp}} a4 = ({{comp}}) {{op}};");
        append(lines, "{{comp}}[] arr = new {{comp}}[8];");
        append(lines, "arr[0] = a1;");
        append(lines, "arr[1] = a2;");
        append(lines, "arr[2] = a3;");
        append(lines, "arr[3] = a4;");
        append(lines, "int i = 4;");
        append(lines, "while (iter.readArray()) {");
        append(lines, "if (i == arr.length) {");
        append(lines, "{{comp}}[] newArr = new {{comp}}[arr.length * 2];");
        append(lines, "System.arraycopy(arr, 0, newArr, 0, arr.length);");
        append(lines, "arr = newArr;");
        append(lines, "}");
        append(lines, "arr[i++] = ({{comp}}) {{op}};");
        append(lines, "}");
        append(lines, "{{comp}}[] result = new {{comp}}[i];");
        append(lines, "System.arraycopy(arr, 0, result, 0, i);");
        append(lines, "return result;");
        append(lines, "}");
        String op = String.format("iter.read(%s.class)", compType.getCanonicalName());
        if (nativeRead != null) {
            op = nativeRead;
        }
        return lines.toString().replace(
                "{{comp}}", compType.getCanonicalName()).replace(
                "{{op}}", op);
    }

    private static String genCollectionWithCapacity(Class clazz, Class compType) {
        String nativeRead = NATIVE_READS.get(compType.getName());
        StringBuilder lines = new StringBuilder();
        append(lines, "public Object decode(java.lang.reflect.Type type, com.github.jsoniter.Jsoniter iter) {");
        append(lines, "if (!iter.readArray()) {");
        append(lines, "return new {{clazz}}(0);");
        append(lines, "}");
        append(lines, "{{comp}} a1 = ({{comp}}) {{op}};");
        append(lines, "if (!iter.readArray()) {");
        append(lines, "{{clazz}} obj = new {{clazz}}(1);");
        append(lines, "obj.add(a1);");
        append(lines, "return obj;");
        append(lines, "}");
        append(lines, "{{comp}} a2 = ({{comp}}) {{op}};");
        append(lines, "if (!iter.readArray()) {");
        append(lines, "{{clazz}} obj = new {{clazz}}(2);");
        append(lines, "obj.add(a1);");
        append(lines, "obj.add(a2);");
        append(lines, "return obj;");
        append(lines, "}");
        append(lines, "{{comp}} a3 = ({{comp}}) {{op}};");
        append(lines, "if (!iter.readArray()) {");
        append(lines, "{{clazz}} obj = new {{clazz}}(3);");
        append(lines, "obj.add(a1);");
        append(lines, "obj.add(a2);");
        append(lines, "obj.add(a3);");
        append(lines, "return obj;");
        append(lines, "}");
        append(lines, "{{comp}} a4 = ({{comp}}) {{op}};");
        append(lines, "{{clazz}} obj = new {{clazz}}(8);");
        append(lines, "obj.add(a1);");
        append(lines, "obj.add(a2);");
        append(lines, "obj.add(a3);");
        append(lines, "obj.add(a4);");
        append(lines, "int i = 4;");
        append(lines, "while (iter.readArray()) {");
        append(lines, "obj.add(({{comp}}) {{op}});");
        append(lines, "}");
        append(lines, "return obj;");
        append(lines, "}");
        String op = String.format("iter.read(%s.class)", compType.getCanonicalName());
        if (nativeRead != null) {
            op = nativeRead;
        }
        return lines.toString().replace(
                "{{clazz}}", clazz.getName()).replace(
                "{{comp}}", compType.getCanonicalName()).replace(
                "{{op}}", op);
    }

    private static String genCollection(Class clazz, Class compType) {
        String nativeRead = NATIVE_READS.get(compType.getName());
        StringBuilder lines = new StringBuilder();
        append(lines, "public Object decode(java.lang.reflect.Type type, com.github.jsoniter.Jsoniter iter) {");
        append(lines, "if (!iter.readArray()) {");
        append(lines, "return new {{clazz}}();");
        append(lines, "}");
        append(lines, "{{comp}} a1 = ({{comp}}) {{op}};");
        append(lines, "if (!iter.readArray()) {");
        append(lines, "{{clazz}} obj = new {{clazz}}();");
        append(lines, "obj.add(a1);");
        append(lines, "return obj;");
        append(lines, "}");
        append(lines, "{{comp}} a2 = ({{comp}}) {{op}};");
        append(lines, "if (!iter.readArray()) {");
        append(lines, "{{clazz}} obj = new {{clazz}}();");
        append(lines, "obj.add(a1);");
        append(lines, "obj.add(a2);");
        append(lines, "return obj;");
        append(lines, "}");
        append(lines, "{{comp}} a3 = ({{comp}}) {{op}};");
        append(lines, "if (!iter.readArray()) {");
        append(lines, "{{clazz}} obj = new {{clazz}}();");
        append(lines, "obj.add(a1);");
        append(lines, "obj.add(a2);");
        append(lines, "obj.add(a3);");
        append(lines, "return obj;");
        append(lines, "}");
        append(lines, "{{comp}} a4 = ({{comp}}) {{op}};");
        append(lines, "{{clazz}} obj = new {{clazz}}();");
        append(lines, "obj.add(a1);");
        append(lines, "obj.add(a2);");
        append(lines, "obj.add(a3);");
        append(lines, "obj.add(a4);");
        append(lines, "int i = 4;");
        append(lines, "while (iter.readArray()) {");
        append(lines, "obj.add(({{comp}}) {{op}});");
        append(lines, "}");
        append(lines, "return obj;");
        append(lines, "}");
        String op = String.format("iter.read(%s.class)", compType.getCanonicalName());
        if (nativeRead != null) {
            op = nativeRead;
        }
        return lines.toString().replace(
                "{{clazz}}", clazz.getName()).replace(
                "{{comp}}", compType.getCanonicalName()).replace(
                "{{op}}", op);
    }

    private static void append(StringBuilder lines, String str) {
        lines.append(str);
        lines.append("\n");
    }
}
