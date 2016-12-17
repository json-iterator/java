package com.jsoniter.output;

public class CodegenImplArray {
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
        append(lines, "stream.writeVal(arr[i]);");
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
}
