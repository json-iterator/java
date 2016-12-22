package com.jsoniter;

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
        append(lines, "if (iter.readNull()) { return null; }");
        append(lines, "{{clazz}} map = ({{clazz}})com.jsoniter.CodegenAccess.resetExistingObject(iter);");
        append(lines, "if (map == null) { map = new {{clazz}}(); }");
        append(lines, "if (!com.jsoniter.CodegenAccess.readObjectStart(iter)) {");
        append(lines, "return map;");
        append(lines, "}");
        append(lines, "String field = com.jsoniter.CodegenAccess.readObjectFieldAsString(iter);");
        append(lines, "map.put(field, {{op}});");
        append(lines, "while (com.jsoniter.CodegenAccess.nextToken(iter) == ',') {");
        append(lines, "field = com.jsoniter.CodegenAccess.readObjectFieldAsString(iter);");
        append(lines, "map.put(field, {{op}});");
        append(lines, "}");
        append(lines, "return map;");
        return lines.toString().replace("{{clazz}}", clazz.getName()).replace("{{op}}", CodegenImplNative.genReadOp(valueType));
    }

    private static void append(StringBuilder lines, String str) {
        lines.append(str);
        lines.append("\n");
    }
}
