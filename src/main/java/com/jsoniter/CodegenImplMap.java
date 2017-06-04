package com.jsoniter;

import com.jsoniter.spi.ClassInfo;
import com.jsoniter.spi.TypeLiteral;

import java.lang.reflect.Type;

class CodegenImplMap {

    public static String genMap(ClassInfo classInfo) {
        Type keyType = classInfo.typeArgs[0];
        Type valueType = classInfo.typeArgs[1];
        StringBuilder lines = new StringBuilder();
        append(lines, "{{clazz}} map = ({{clazz}})com.jsoniter.CodegenAccess.resetExistingObject(iter);");
        append(lines, "if (iter.readNull()) { return null; }");
        append(lines, "if (map == null) { map = new {{clazz}}(); }");
        append(lines, "if (!com.jsoniter.CodegenAccess.readObjectStart(iter)) {");
        append(lines, "return map;");
        append(lines, "}");
        append(lines, "do {");
        if (keyType == String.class) {
            append(lines, "Object mapKey = com.jsoniter.CodegenAccess.readObjectFieldAsString(iter);");
        } else {
            append(lines, "Object mapKey = com.jsoniter.CodegenAccess.readMapKey(\"" +
                    TypeLiteral.create(keyType).getDecoderCacheKey() +"\", iter);");
        }
        append(lines, "map.put(mapKey, {{op}});");
        append(lines, "} while (com.jsoniter.CodegenAccess.nextToken(iter) == ',');");
        append(lines, "return map;");
        return lines.toString()
                .replace("{{clazz}}", classInfo.clazz.getName())
                .replace("{{op}}", CodegenImplNative.genReadOp(valueType));
    }

    private static void append(StringBuilder lines, String str) {
        lines.append(str);
        lines.append("\n");
    }
}
