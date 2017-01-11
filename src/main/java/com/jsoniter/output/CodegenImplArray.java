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
        ctx.append(String.format("%s[] arr = (%s[])obj;", compType.getCanonicalName(), compType.getCanonicalName()));
        ctx.append("if (arr.length == 0) { return; }");
        ctx.buffer('[');
        ctx.append("int i = 0;");
        ctx.append(String.format("%s e = arr[i++];", compType.getCanonicalName()));
        if (compType.isPrimitive()) {
            CodegenImplNative.genWriteOp(ctx, "e", compType, false);
        } else {
            ctx.append("if (e == null) { stream.writeNull(); } else {");
            CodegenImplNative.genWriteOp(ctx, "e", compType, true);
            ctx.append("}");
        }
        ctx.append("while (i < arr.length) {");
        ctx.append("stream.write(',');");
        ctx.append("e = arr[i++];");
        if (compType.isPrimitive()) {
            CodegenImplNative.genWriteOp(ctx, "e", compType, false);
        } else {
            ctx.append("if (e == null) { stream.writeNull(); } else {");
            CodegenImplNative.genWriteOp(ctx, "e", compType, true);
            ctx.append("}");
        }
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
        if (List.class.isAssignableFrom(clazz)) {
            return genList(clazz, compType);
        } else {
            return genCollection(clazz, compType);
        }
    }

    private static CodegenResult genList(Class clazz, Type compType) {
        CodegenResult ctx = new CodegenResult();
        ctx.append("public static void encode_(java.lang.Object obj, com.jsoniter.output.JsonStream stream) throws java.io.IOException {");
        ctx.append("if (obj == null) { stream.writeNull(); return; }");
        ctx.append("java.util.List list = (java.util.List)obj;");
        ctx.append("int size = list.size();");
        ctx.append("if (size == 0) { return; }");
        ctx.buffer('[');
        ctx.append("java.lang.Object e = list.get(0);");
        ctx.append("if (e == null) { stream.writeNull(); } else {");
        CodegenImplNative.genWriteOp(ctx, "e", compType, true);
        ctx.append("}");
        ctx.append("for (int i = 1; i < size; i++) {");
        ctx.append("stream.write(',');");
        ctx.append("e = list.get(i);");
        ctx.append("if (e == null) { stream.writeNull(); } else {");
        CodegenImplNative.genWriteOp(ctx, "e", compType, true);
        ctx.append("}");
        ctx.append("}");
        ctx.buffer(']');
        ctx.append("}");
        return ctx;
    }

    private static CodegenResult genCollection(Class clazz, Type compType) {
        CodegenResult ctx = new CodegenResult();
        ctx.append("public static void encode_(java.lang.Object obj, com.jsoniter.output.JsonStream stream) throws java.io.IOException {");
        ctx.append("if (obj == null) { stream.writeNull(); return; }");
        ctx.append("java.util.Iterator iter = ((java.util.Collection)obj).iterator();");
        ctx.append("if (!iter.hasNext()) { return; }");
        ctx.buffer('[');
        ctx.append("java.lang.Object e = iter.next();");
        ctx.append("if (e == null) { stream.writeNull(); } else {");
        CodegenImplNative.genWriteOp(ctx, "e", compType, true);
        ctx.append("}");
        ctx.append("while (iter.hasNext()) {");
        ctx.append("stream.write(',');");
        ctx.append("e = iter.next();");
        ctx.append("if (e == null) { stream.writeNull(); } else {");
        CodegenImplNative.genWriteOp(ctx, "e", compType, true);
        ctx.append("}");
        ctx.append("}");
        ctx.buffer(']');
        ctx.append("}");
        return ctx;
    }

}
