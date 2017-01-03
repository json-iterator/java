package com.jsoniter.output;

import java.lang.reflect.Type;
import java.util.*;

class CodegenImplArray {

    public static String genArray(Class clazz) {
        Class compType = clazz.getComponentType();
        if (compType.isArray()) {
            throw new IllegalArgumentException("nested array not supported: " + clazz.getCanonicalName());
        }
        StringBuilder lines = new StringBuilder();
        append(lines, "public static void encode_(java.lang.Object obj, com.jsoniter.output.JsonStream stream) throws java.io.IOException {");
        append(lines, "if (obj == null) { stream.writeNull(); return; }");
        append(lines, "{{comp}}[] arr = ({{comp}}[])obj;");
        append(lines, "if (arr.length == 0) { stream.writeEmptyArray(); return; }");
        append(lines, "stream.writeArrayStart();");
        append(lines, "int i = 0;");
        append(lines, "{{op}}");
        append(lines, "while (i < arr.length) {");
        append(lines, "stream.writeMore();");
        append(lines, "{{op}}");
        append(lines, "}");
        append(lines, "stream.writeArrayEnd();");
        append(lines, "}");
        return lines.toString()
                .replace("{{comp}}", compType.getCanonicalName())
                .replace("{{op}}", CodegenImplNative.genWriteOp("arr[i++]", compType));
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
        append(lines, "public static void encode_(java.lang.Object obj, com.jsoniter.output.JsonStream stream) throws java.io.IOException {");
        append(lines, "if (obj == null) { stream.writeNull(); return; }");
        append(lines, "java.util.Iterator iter = ((java.util.Collection)obj).iterator();");
        append(lines, "if (!iter.hasNext()) { stream.writeEmptyArray(); return; }");
        append(lines, "stream.writeArrayStart();");
        append(lines, "{{op}}");
        append(lines, "while (iter.hasNext()) {");
        append(lines, "stream.writeMore();");
        append(lines, "{{op}}");
        append(lines, "}");
        append(lines, "stream.writeArrayEnd();");
        append(lines, "}");
        return lines.toString()
                .replace("{{comp}}", CodegenImplNative.getTypeName(compType))
                .replace("{{op}}", CodegenImplNative.genWriteOp("iter.next()", compType));
    }

}
