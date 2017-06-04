package com.jsoniter.output;

import com.jsoniter.spi.ClassInfo;
import com.jsoniter.spi.JsoniterSpi;

import java.lang.reflect.Type;

class CodegenImplMap {
    public static CodegenResult genMap(String cacheKey, ClassInfo classInfo) {
        Type[] typeArgs = classInfo.typeArgs;
        boolean isCollectionValueNullable = true;
        if (cacheKey.endsWith("__value_not_nullable")) {
            isCollectionValueNullable = false;
        }
        Type keyType = String.class;
        Type valueType = Object.class;
        if (typeArgs.length == 2) {
            keyType = typeArgs[0];
            valueType = typeArgs[1];
        }
        String mapCacheKey = JsoniterSpi.getMapKeyEncoderCacheKey(keyType);
        CodegenResult ctx = new CodegenResult();
        ctx.append("public static void encode_(java.lang.Object obj, com.jsoniter.output.JsonStream stream) throws java.io.IOException {");
        ctx.append("if (obj == null) { stream.writeNull(); return; }");
        ctx.append("java.util.Map map = (java.util.Map)obj;");
        ctx.append("java.util.Iterator iter = map.entrySet().iterator();");
        ctx.append("if(!iter.hasNext()) { return; }");
        ctx.append("java.util.Map.Entry entry = (java.util.Map.Entry)iter.next();");
        ctx.buffer('{');
        if (keyType == String.class) {
            ctx.append("stream.writeVal((java.lang.String)entry.getKey());");
        } else {
            ctx.append(String.format("com.jsoniter.output.CodegenAccess.writeMapKey(\"%s\", entry.getKey(), stream);", mapCacheKey));
        }
        ctx.append("stream.write(':');");
        if (isCollectionValueNullable) {
            ctx.append("if (entry.getValue() == null) { stream.writeNull(); } else {");
            CodegenImplNative.genWriteOp(ctx, "entry.getValue()", valueType, true);
            ctx.append("}");
        } else {
            CodegenImplNative.genWriteOp(ctx, "entry.getValue()", valueType, false);
        }
        ctx.append("while(iter.hasNext()) {");
        ctx.append("entry = (java.util.Map.Entry)iter.next();");
        ctx.append("stream.write(',');");
        if (keyType == String.class) {
            ctx.append("stream.writeVal((java.lang.String)entry.getKey());");
        } else {
            ctx.append(String.format("com.jsoniter.output.CodegenAccess.writeMapKey(\"%s\", entry.getKey(), stream);", mapCacheKey));
        }
        ctx.append("stream.write(':');");
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
