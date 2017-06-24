package com.jsoniter.output;

import com.jsoniter.spi.*;

import java.util.*;

class CodegenImplObject {
    public static CodegenResult genObject(ClassInfo classInfo) {
        boolean supportBuffer = JsoniterSpi.getCurrentConfig().indentionStep() == 0;
        CodegenResult ctx = new CodegenResult();
        ClassDescriptor desc = ClassDescriptor.getEncodingClassDescriptor(classInfo, false);
        List<EncodeTo> encodeTos = desc.encodeTos();
        ctx.append(String.format("public static void encode_(%s obj, com.jsoniter.output.JsonStream stream) throws java.io.IOException {", classInfo.clazz.getCanonicalName()));
        if (hasFieldOutput(desc)) {
            int notFirst = 0;
            if (supportBuffer) {
                ctx.buffer('{');
            } else {
                ctx.append("stream.writeObjectStart();");
            }
            for (EncodeTo encodeTo : encodeTos) {
                notFirst = genField(ctx, encodeTo.binding, encodeTo.toName, notFirst);
            }
            for (UnwrapperDescriptor unwrapper : desc.unwrappers) {
                if (unwrapper.isMap) {
                    ctx.append(String.format("java.util.Map map = (java.util.Map)obj.%s();", unwrapper.method.getName()));
                    ctx.append("java.util.Iterator iter = map.entrySet().iterator();");
                    ctx.append("while(iter.hasNext()) {");
                    ctx.append("java.util.Map.Entry entry = (java.util.Map.Entry)iter.next();");
                    notFirst = appendComma(ctx, notFirst);
                    ctx.append("stream.writeObjectField(entry.getKey().toString());");
                    ctx.append("if (entry.getValue() == null) { stream.writeNull(); } else {");
                    CodegenImplNative.genWriteOp(ctx, "entry.getValue()", unwrapper.mapValueTypeLiteral.getType(), true);
                    ctx.append("}");
                    ctx.append("}");
                } else {
                    notFirst = appendComma(ctx, notFirst);
                    ctx.append(String.format("obj.%s(stream);", unwrapper.method.getName()));
                }
            }
            if (supportBuffer) {
                ctx.buffer('}');
            } else {
                ctx.append("stream.writeObjectEnd();");
            }
        } else {
            ctx.buffer("{}");
        }
        ctx.append("}");
        return ctx;
    }


    private static boolean hasFieldOutput(ClassDescriptor desc) {
        if (!desc.unwrappers.isEmpty()) {
            return true;
        }
        return !desc.encodeTos().isEmpty();
    }

    private static int genField(CodegenResult ctx, Binding binding, String toName, int notFirst) {
        boolean supportBuffer = JsoniterSpi.getCurrentConfig().indentionStep() == 0;
        String fieldCacheKey = binding.encoderCacheKey();
        Encoder encoder = JsoniterSpi.getEncoder(fieldCacheKey);
        boolean isCollectionValueNullable = binding.isCollectionValueNullable;
        Class valueClazz;
        String valueAccessor;
        if (binding.field != null) {
            valueClazz = binding.field.getType();
            valueAccessor = "obj." + binding.field.getName();
        } else {
            valueClazz = binding.method.getReturnType();
            valueAccessor = "obj." + binding.method.getName() + "()";
        }
        if (!supportCollectionValueNullable(valueClazz)) {
            isCollectionValueNullable = true;
        }
        boolean nullable = !valueClazz.isPrimitive();
        if (!binding.isNullable) {
            nullable = false;
        }
        if (nullable) {
            if (binding.shouldOmitNull) {
                if (notFirst == 0) { // no previous field
                    notFirst = 2; // maybe
                    ctx.append("boolean notFirst = false;");
                }
                ctx.append(String.format("if (%s != null) {", valueAccessor));
                notFirst = appendComma(ctx, notFirst);
                if (supportBuffer) {
                    ctx.append(CodegenResult.bufferToWriteOp("\"" + toName + "\":"));
                } else {
                    ctx.append(String.format("stream.writeObjectField(\"%s\");", toName));
                }
            } else {
                notFirst = appendComma(ctx, notFirst);
                if (supportBuffer) {
                    ctx.buffer('"');
                    ctx.buffer(toName);
                    ctx.buffer('"');
                    ctx.buffer(':');
                } else {
                    ctx.append(String.format("stream.writeObjectField(\"%s\");", toName));
                }
                ctx.append(String.format("if (%s == null) { stream.writeNull(); } else {", valueAccessor));
            }
        } else {
            notFirst = appendComma(ctx, notFirst);
            if (supportBuffer) {
                ctx.buffer('"');
                ctx.buffer(toName);
                ctx.buffer('"');
                ctx.buffer(':');
            } else {
                ctx.append(String.format("stream.writeObjectField(\"%s\");", toName));
            }
        }
        if (encoder == null) {
            CodegenImplNative.genWriteOp(ctx, valueAccessor, binding.valueType, nullable, isCollectionValueNullable);
        } else {
            ctx.append(String.format("com.jsoniter.output.CodegenAccess.writeVal(\"%s\", %s, stream);",
                    fieldCacheKey, valueAccessor));
        }
        if (nullable) {
            ctx.append("}");
        }
        return notFirst;
    }

    private static int appendComma(CodegenResult ctx, int notFirst) {
        boolean supportBuffer = JsoniterSpi.getCurrentConfig().indentionStep() == 0;
        if (notFirst == 1) { // definitely not first
            if (supportBuffer) {
                ctx.buffer(',');
            } else {
                ctx.append("stream.writeMore();");
            }
        } else if (notFirst == 2) { // maybe not first, previous field is omitNull
            if (supportBuffer) {
                ctx.append("if (notFirst) { stream.write(','); } else { notFirst = true; }");
            } else {
                ctx.append("if (notFirst) { stream.writeMore(); } else { notFirst = true; }");
            }
        } else { // this is the first, do not write comma
            notFirst = 1;
        }
        return notFirst;
    }


    private static boolean supportCollectionValueNullable(Class clazz) {
        if (clazz.isArray()) {
            return true;
        }
        if (Map.class.isAssignableFrom(clazz)) {
            return true;
        }
        if (Collection.class.isAssignableFrom(clazz)) {
            return true;
        }
        return false;
    }
}
