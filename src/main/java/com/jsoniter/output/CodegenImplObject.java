package com.jsoniter.output;

import com.jsoniter.spi.*;

import java.lang.reflect.Method;

class CodegenImplObject {
    public static CodegenResult genObject(Class clazz) {
        CodegenResult ctx = new CodegenResult();
        ClassDescriptor desc = JsoniterSpi.getEncodingClassDescriptor(clazz, false);
        ctx.append(String.format("public static void encode_(%s obj, com.jsoniter.output.JsonStream stream) throws java.io.IOException {", clazz.getCanonicalName()));
        ctx.append("if (obj == null) { stream.writeNull(); return; }");
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
                    ctx.buffer("\\\"");
                    ctx.buffer(toName);
                    ctx.buffer("\\\"");
                    ctx.buffer(':');
                    genField(ctx, binding);
                }
            }
            for (Method unwrapper : desc.unwrappers) {
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
        if (!desc.unwrappers.isEmpty()) {
            return true;
        }
        for (Binding binding : desc.allEncoderBindings()) {
            if (binding.toNames.length > 0) {
                return true;
            }
        }
        return false;
    }

    private static void genField(CodegenResult ctx, Binding binding) {
        String fieldCacheKey = binding.encoderCacheKey();
        Encoder encoder = JsoniterSpi.getEncoder(fieldCacheKey);
        if (binding.field != null) {
            if (encoder == null) {
                CodegenImplNative.genWriteOp(ctx, "obj." + binding.field.getName(), binding.valueType);
            } else {
                ctx.append(String.format("com.jsoniter.output.CodegenAccess.writeVal(\"%s\", obj.%s, stream);",
                        fieldCacheKey, binding.field.getName()));
            }
        } else {
            if (encoder == null) {
                CodegenImplNative.genWriteOp(ctx, "obj." + binding.method.getName() + "()", binding.valueType);
            } else {
                ctx.append(String.format("com.jsoniter.output.CodegenAccess.writeVal(\"%s\", obj.%s(), stream);",
                        fieldCacheKey, binding.method.getName()));
            }
        }
    }
}
