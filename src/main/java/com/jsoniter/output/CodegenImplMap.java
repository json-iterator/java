package com.jsoniter.output;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

class CodegenImplMap {
    public static String genMap(Class clazz, Type[] typeArgs) {
        Type keyType = String.class;
        Type valueType = Object.class;
        if (typeArgs.length == 0) {
            // default to Map<String, Object>
        } else if (typeArgs.length == 2) {
            keyType = typeArgs[0];
            valueType = typeArgs[1];
        } else {
            throw new IllegalArgumentException(
                    "can not bind to generic collection without argument types, " +
                            "try syntax like TypeLiteral<Map<String, String>>{}");
        }
        if (keyType != String.class) {
            throw new IllegalArgumentException("map key must be String");
        }
        if (clazz == Map.class) {
            clazz = HashMap.class;
        }
        StringBuilder lines = new StringBuilder();
        append(lines, "public static void encode_(Object obj, com.jsoniter.output.JsonStream stream) {");
        append(lines, "if (obj == null) { stream.writeNull(); return; }");
        append(lines, "java.util.Map map = (java.util.Map)obj;");
        append(lines, "java.util.Iterator iter = map.entrySet().iterator();");
        append(lines, "if(!iter.hasNext()) { stream.writeEmptyObject(); return; }");
        append(lines, "java.util.Map.Entry entry = iter.next();");
        append(lines, "stream.startObject();");
        append(lines, "stream.writeField((String)entry.getKey());");
        append(lines, "stream.writeVal(entry.getValue());");
        append(lines, "stream.writeMore();");
        append(lines, "while(iter.hasNext()) {");
        append(lines, "entry = iter.next();");
        append(lines, "stream.writeField((String)entry.getKey());");
        append(lines, "stream.writeVal(entry.getValue());");
        append(lines, "stream.writeMore();");
        append(lines, "}");
        append(lines, "stream.endObject();");
        append(lines, "}");
        return lines.toString().replace("{{clazz}}", clazz.getName());
    }

    private static void append(StringBuilder lines, String str) {
        lines.append(str);
        lines.append("\n");
    }
}
