package com.jsoniter.output;

import java.lang.reflect.Type;
import java.util.*;

class CodegenImplArray {

    public static CodegenResult genArray(Class clazz) {
        Class compType = clazz.getComponentType();
        if (compType.isArray()) {
            throw new IllegalArgumentException("nested array not supported: " + clazz.getCanonicalName());
        }
        CodegenResult ctx = new CodegenResult();
        ctx.append("public static void encode_(java.lang.Object obj, com.jsoniter.output.JsonStream stream) throws java.io.IOException {");
        ctx.append("if (obj == null) { stream.writeNull(); return; }");
        ctx.append(String.format("%s[] arr = (%s[])obj;", compType.getCanonicalName(), compType.getCanonicalName()));
        ctx.append("if (arr.length == 0) { return; }");
        ctx.buffer('[');
        ctx.append("int i = 0;");
        CodegenImplNative.genWriteOp(ctx, "arr[i++]", compType);
        ctx.append("while (i < arr.length) {");
        ctx.buffer(',');
        CodegenImplNative.genWriteOp(ctx, "arr[i++]", compType);
        ctx.append("}");
        ctx.buffer(']');
        ctx.append("}");
        return ctx;
    }

    public static CodegenResult genCollection(Class clazz, Type[] typeArgs) {
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

    private static CodegenResult genCollection(Class clazz, Type compType) {
        CodegenResult ctx = new CodegenResult();
        ctx.append("public static void encode_(java.lang.Object obj, com.jsoniter.output.JsonStream stream) throws java.io.IOException {");
        ctx.append("if (obj == null) { stream.writeNull(); return; }");
        ctx.append("java.util.Iterator iter = ((java.util.Collection)obj).iterator();");
        ctx.append("if (!iter.hasNext()) { return; }");
        ctx.buffer('[');
        CodegenImplNative.genWriteOp(ctx, "iter.next()", compType);
        ctx.append("while (iter.hasNext()) {");
        ctx.buffer(',');
        CodegenImplNative.genWriteOp(ctx, "iter.next()", compType);
        ctx.append("}");
        ctx.buffer(']');
        ctx.append("}");
        return ctx;
    }

}
