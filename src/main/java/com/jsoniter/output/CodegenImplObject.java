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
            boolean notFirst = false;
            ctx.buffer('{');
            for (Binding binding : desc.allEncoderBindings()) {
                for (String toName : binding.toNames) {
                    if (notFirst) {
                        ctx.buffer(',');
                    } else {
                        notFirst = true;
                    }
                    genField(ctx, binding, toName);
                }
            }
            for (Method unwrapper : desc.unWrappers) {
                if (notFirst) {
                    ctx.buffer(',');
                } else {
                    notFirst = true;
                }
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

    private static void genField(CodegenResult ctx, Binding binding, String toName) {
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
                ctx.append(String.format("if (%s != null) {", valueAccessor));
                ctx.append("stream.write('\"');");
                ctx.buffer(toName);
                ctx.append("stream.write('\"', ':');");
            } else {
                ctx.buffer('"');
                ctx.buffer(toName);
                ctx.buffer('"');
                ctx.buffer(':');
                ctx.append(String.format("if (%s == null) { stream.writeNull(); } else {", valueAccessor));
            }
        } else {
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
