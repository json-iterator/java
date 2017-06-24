package com.jsoniter.output;

import com.jsoniter.spi.ClassInfo;

import java.lang.reflect.Type;
import java.util.*;

class CodegenImplArray {

    public static CodegenResult genArray(String cacheKey, ClassInfo classInfo) {
        Class clazz = classInfo.clazz;
        Class compType = clazz.getComponentType();
        if (compType.isArray()) {
            throw new IllegalArgumentException("nested array not supported: " + clazz.getCanonicalName());
        }
        boolean isCollectionValueNullable = true;
        if (cacheKey.endsWith("__value_not_nullable")) {
            isCollectionValueNullable = false;
        }
        if (compType.isPrimitive()) {
            isCollectionValueNullable = false;
        }
        CodegenResult ctx = new CodegenResult();
        ctx.append("public static void encode_(java.lang.Object obj, com.jsoniter.output.JsonStream stream) throws java.io.IOException {");
        ctx.append(String.format("%s[] arr = (%s[])obj;", compType.getCanonicalName(), compType.getCanonicalName()));
        ctx.append("if (arr.length == 0) { return; }");
        ctx.buffer('[');
        ctx.append("int i = 0;");
        ctx.append(String.format("%s e = arr[i++];", compType.getCanonicalName()));
        if (isCollectionValueNullable) {
            ctx.append("if (e == null) { stream.writeNull(); } else {");
            CodegenImplNative.genWriteOp(ctx, "e", compType, true);
            ctx.append("}"); // if
        } else {
            CodegenImplNative.genWriteOp(ctx, "e", compType, false);
        }
        ctx.append("while (i < arr.length) {");
        ctx.append("stream.write(',');");
        ctx.append("e = arr[i++];");
        if (isCollectionValueNullable) {
            ctx.append("if (e == null) { stream.writeNull(); } else {");
            CodegenImplNative.genWriteOp(ctx, "e", compType, true);
            ctx.append("}"); // if
        } else {
            CodegenImplNative.genWriteOp(ctx, "e", compType, false);
        }
        ctx.append("}"); // while
        ctx.buffer(']');
        ctx.append("}"); // public static void encode_
        return ctx;
    }

    public static CodegenResult genCollection(String cacheKey, ClassInfo classInfo) {
        Type[] typeArgs = classInfo.typeArgs;
        Class clazz = classInfo.clazz;
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
        if (List.class.isAssignableFrom(clazz)) {
            return genList(cacheKey, clazz, compType);
        } else {
            return genCollection(cacheKey, clazz, compType);
        }
    }

    private static CodegenResult genList(String cacheKey, Class clazz, Type compType) {
        boolean isCollectionValueNullable = true;
        if (cacheKey.endsWith("__value_not_nullable")) {
            isCollectionValueNullable = false;
        }
        CodegenResult ctx = new CodegenResult();
        ctx.append("public static void encode_(java.lang.Object obj, com.jsoniter.output.JsonStream stream) throws java.io.IOException {");
        ctx.append("java.util.List list = (java.util.List)obj;");
        ctx.append("int size = list.size();");
        ctx.append("if (size == 0) { return; }");
        ctx.buffer('[');
        ctx.append("java.lang.Object e = list.get(0);");
        if (isCollectionValueNullable) {
            ctx.append("if (e == null) { stream.writeNull(); } else {");
            CodegenImplNative.genWriteOp(ctx, "e", compType, true);
            ctx.append("}");
        } else {
            CodegenImplNative.genWriteOp(ctx, "e", compType, false);
        }
        ctx.append("for (int i = 1; i < size; i++) {");
        ctx.append("stream.write(',');");
        ctx.append("e = list.get(i);");
        if (isCollectionValueNullable) {
            ctx.append("if (e == null) { stream.writeNull(); } else {");
            CodegenImplNative.genWriteOp(ctx, "e", compType, true);
            ctx.append("}"); // if
        } else {
            CodegenImplNative.genWriteOp(ctx, "e", compType, false);
        }
        ctx.append("}"); // for
        ctx.buffer(']');
        ctx.append("}"); // public static void encode_
        return ctx;
    }

    private static CodegenResult genCollection(String cacheKey, Class clazz, Type compType) {
        boolean isCollectionValueNullable = true;
        if (cacheKey.endsWith("__value_not_nullable")) {
            isCollectionValueNullable = false;
        }
        CodegenResult ctx = new CodegenResult();
        ctx.append("public static void encode_(java.lang.Object obj, com.jsoniter.output.JsonStream stream) throws java.io.IOException {");
        ctx.append("java.util.Iterator iter = ((java.util.Collection)obj).iterator();");
        ctx.append("if (!iter.hasNext()) { return; }");
        ctx.buffer('[');
        ctx.append("java.lang.Object e = iter.next();");
        if (isCollectionValueNullable) {
            ctx.append("if (e == null) { stream.writeNull(); } else {");
            CodegenImplNative.genWriteOp(ctx, "e", compType, true);
            ctx.append("}"); // if
        } else {
            CodegenImplNative.genWriteOp(ctx, "e", compType, false);
        }
        ctx.append("while (iter.hasNext()) {");
        ctx.append("stream.write(',');");
        ctx.append("e = iter.next();");
        if (isCollectionValueNullable) {
            ctx.append("if (e == null) { stream.writeNull(); } else {");
            CodegenImplNative.genWriteOp(ctx, "e", compType, true);
            ctx.append("}"); // if
        } else {
            CodegenImplNative.genWriteOp(ctx, "e", compType, false);
        }
        ctx.append("}"); // while
        ctx.buffer(']');
        ctx.append("}"); // public static void encode_
        return ctx;
    }

}
