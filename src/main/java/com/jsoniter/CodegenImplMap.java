package com.jsoniter;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

class CodegenImplMap {

    public static String genMap(Class clazz, Type[] typeArgs) {
        Type valueType = typeArgs[1];
        StringBuilder lines = new StringBuilder();
        append(lines, "{{clazz}} map = ({{clazz}})com.jsoniter.CodegenAccess.resetExistingObject(iter);");
        append(lines, "if (iter.readNull()) { return null; }");
        append(lines, "if (map == null) { map = new {{clazz}}(); }");
        append(lines, "if (!com.jsoniter.CodegenAccess.readObjectStart(iter)) {");
        append(lines, "return map;");
        append(lines, "}");
        append(lines, "do {");
        append(lines, "String field = com.jsoniter.CodegenAccess.readObjectFieldAsString(iter);");
        append(lines, "map.put(field, {{op}});");
        append(lines, "} while (com.jsoniter.CodegenAccess.nextToken(iter) == ',');");
        append(lines, "return map;");
        return lines.toString().replace("{{clazz}}", clazz.getName()).replace("{{op}}", CodegenImplNative.genReadOp(valueType));
    }

    private static void append(StringBuilder lines, String str) {
        lines.append(str);
        lines.append("\n");
    }
}
