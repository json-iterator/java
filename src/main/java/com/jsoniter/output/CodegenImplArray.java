package com.jsoniter.output;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

class CodegenImplArray {

    public static String genArray(Class clazz) {
        Class compType = clazz.getComponentType();
        if (compType.isArray()) {
            throw new IllegalArgumentException("nested array not supported: " + clazz.getCanonicalName());
        }
        StringBuilder lines = new StringBuilder();
        append(lines, "public static void encode_(Object obj, com.jsoniter.output.JsonStream stream) {");
        append(lines, "stream.startArray();");
        append(lines, "{{comp}}[] arr = ({{comp}}[])obj;");
        append(lines, "for (int i = 0; i < arr.length; i++) {");
        append(lines, "stream.writeVal(({{comp}})arr[i]);");
        append(lines, "stream.writeMore();");
        append(lines, "}");
        append(lines, "stream.endArray();");
        append(lines, "}");
        return lines.toString().replace("{{comp}}", compType.getCanonicalName());
    }

    private static void append(StringBuilder lines, String str) {
        lines.append(str);
        lines.append("\n");
    }

    public static String genCollection(Class clazz, Type[] typeArgs) {
        Type compType = Object.class;
        if (typeArgs.length == 0) {
            // default to List<Object>
        } else if (typeArgs.length == 1) {
            compType = typeArgs[0];
        } else {
            throw new IllegalArgumentException(
                    "can not bind to generic collection without argument types, " +
                            "try syntax like TypeLiteral<List<Integer>>{}");
        }
        if (clazz == List.class) {
            clazz = ArrayList.class;
        } else if (clazz == Set.class) {
            clazz = HashSet.class;
        }
        return genCollection(clazz, compType);
    }

    private static String genCollection(Class clazz, Type compType) {
        StringBuilder lines = new StringBuilder();
        append(lines, "public static void encode_(Object obj, com.jsoniter.output.JsonStream stream) {");
        append(lines, "stream.startArray();");
        append(lines, "java.util.Iterator iter = ((java.util.Collection)obj).iterator();");
        append(lines, "while (iter.hasNext()) {");
        append(lines, "stream.writeVal(({{comp}})iter.next());");
        append(lines, "stream.writeMore();");
        append(lines, "}");
        append(lines, "stream.endArray();");
        append(lines, "}");
        return lines.toString().replace("{{comp}}", getTypeName(compType));
    }

    private static String getTypeName(Type fieldType) {
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
}
