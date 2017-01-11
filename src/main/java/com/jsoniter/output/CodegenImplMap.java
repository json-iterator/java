package com.jsoniter.output;

import java.lang.reflect.Type;
import java.util.Collection;

class CodegenImplMap {
    public static CodegenResult genMap(String cacheKey, Class clazz, Type[] typeArgs) {
        boolean isCollectionValueNullable = true;
        if (cacheKey.endsWith("__value_not_nullable")) {
            isCollectionValueNullable = false;
        }
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
        CodegenResult ctx = new CodegenResult();
        ctx.append("public static void encode_(java.lang.Object obj, com.jsoniter.output.JsonStream stream) throws java.io.IOException {");
        ctx.append("if (obj == null) { stream.writeNull(); return; }");
        ctx.append("java.util.Map map = (java.util.Map)obj;");
        ctx.append("java.util.Iterator iter = map.entrySet().iterator();");
        ctx.append("if(!iter.hasNext()) { return; }");
        ctx.append("java.util.Map.Entry entry = (java.util.Map.Entry)iter.next();");
        ctx.buffer('{');
        ctx.append("stream.writeVal((String)entry.getKey());");
        ctx.buffer(':');
        if (isCollectionValueNullable) {
            ctx.append("if (entry.getValue() == null) { stream.writeNull(); } else {");
            CodegenImplNative.genWriteOp(ctx, "entry.getValue()", valueType, true);
            ctx.append("}");
        } else {
            CodegenImplNative.genWriteOp(ctx, "entry.getValue()", valueType, false);
        }
        ctx.append("while(iter.hasNext()) {");
        ctx.append("entry = (java.util.Map.Entry)iter.next();");
        ctx.buffer(',');
        ctx.append("stream.writeObjectField((String)entry.getKey());");
        if (isCollectionValueNullable) {
            ctx.append("if (entry.getValue() == null) { stream.writeNull(); } else {");
            CodegenImplNative.genWriteOp(ctx, "entry.getValue()", valueType, true);
            ctx.append("}");
        } else {
            CodegenImplNative.genWriteOp(ctx, "entry.getValue()", valueType, false);
        }
        ctx.append("}");
        ctx.buffer('}');
        ctx.append("}");
        return ctx;
    }
}
