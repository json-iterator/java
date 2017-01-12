package com.jsoniter.output;

import com.jsoniter.spi.*;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;

class CodegenImplObject {
    public static CodegenResult genObject(Class clazz) {
        CodegenResult ctx = new CodegenResult();
        ClassDescriptor desc = JsoniterSpi.getEncodingClassDescriptor(clazz, false);
        ctx.append(String.format("public static void encode_(%s obj, com.jsoniter.output.JsonStream stream) throws java.io.IOException {", clazz.getCanonicalName()));
        if (hasFieldOutput(desc)) {
            int notFirst = 0;
            ctx.buffer('{');
            for (Binding binding : desc.allEncoderBindings()) {
                for (String toName : binding.toNames) {
                    notFirst = genField(ctx, binding, toName, notFirst);
                }
            }
            for (Method unwrapper : desc.unWrappers) {
                notFirst = appendComma(ctx, notFirst);
                ctx.append(String.format("obj.%s(stream);", unwrapper.getName()));
            }
            ctx.buffer('}');
        } else {
            ctx.buffer("{}");
        }
        ctx.append("}");
        return ctx;
    }


    private static boolean hasFieldOutput(ClassDescriptor desc) {
        if (!desc.unWrappers.isEmpty()) {
            return true;
        }
        for (Binding binding : desc.allEncoderBindings()) {
            if (binding.toNames.length > 0) {
                return true;
            }
        }
        return false;
    }

    private static int genField(CodegenResult ctx, Binding binding, String toName, int notFirst) {
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
                ctx.append(CodegenResult.bufferToWriteOp("\"" + toName + "\":"));
            } else {
                notFirst = appendComma(ctx, notFirst);
                ctx.buffer('"');
                ctx.buffer(toName);
                ctx.buffer('"');
                ctx.buffer(':');
                ctx.append(String.format("if (%s == null) { stream.writeNull(); } else {", valueAccessor));
            }
        } else {
            notFirst = appendComma(ctx, notFirst);
            ctx.buffer('"');
            ctx.buffer(toName);
            ctx.buffer('"');
            ctx.buffer(':');
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
        if (notFirst == 1) { // definitely not first
            ctx.buffer(',');
        } else if (notFirst == 2) { // maybe not first, previous field is omitNull
            ctx.append("if (notFirst) { stream.write(','); } else { notFirst = true; }");
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
