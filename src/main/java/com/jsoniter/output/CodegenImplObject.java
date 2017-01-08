package com.jsoniter.output;

import com.jsoniter.spi.*;

import java.lang.reflect.Method;

class CodegenImplObject {
    public static String genObject(Class clazz) {
        ClassDescriptor desc = JsoniterSpi.getEncodingClassDescriptor(clazz, false);
        CodegenContext ctx = new CodegenContext();
        ctx.append(String.format("public static void encode_(%s obj, com.jsoniter.output.JsonStream stream) throws java.io.IOException {", clazz.getCanonicalName()));
        ctx.append("if (obj == null) { stream.writeNull(); return; }");
        if (hasFieldOutput(desc)) {
            boolean notFirst = false;
            ctx.buffered.append('{');
            for (Binding binding : desc.allEncoderBindings()) {
                for (String toName : binding.toNames) {
                    if (notFirst) {
                        ctx.buffered.append(',');
                    } else {
                        notFirst = true;
                    }
                    ctx.buffered.append("\\\"");
                    ctx.buffered.append(toName);
                    ctx.buffered.append("\\\"");
                    ctx.buffered.append(':');
                    genField(ctx, binding);
                }
            }
            for (Method unwrapper : desc.unwrappers) {
                if (notFirst) {
                    ctx.buffered.append(',');
                } else {
                    notFirst = true;
                }
                ctx.flushBuffer();
                ctx.append(String.format("obj.%s(stream);", unwrapper.getName()));
            }
            ctx.buffered.append('}');
            ctx.flushBuffer();
        } else {
            ctx.append("stream.writeEmptyObject();");
        }
        ctx.append("}");
        return ctx.toString().replace("{{clazz}}", clazz.getCanonicalName());
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

    private static void genField(CodegenContext ctx, Binding binding) {
        String fieldCacheKey = binding.encoderCacheKey();
        Encoder encoder = JsoniterSpi.getEncoder(fieldCacheKey);
        if (binding.field != null) {
            if (encoder == null) {
                if (binding.valueType == String.class) {
                    ctx.buffered.append("\\\"");
                    ctx.flushBuffer();
                    ctx.append(String.format("com.jsoniter.output.CodegenAccess.writeStringWithoutQuote(stream, obj.%s);", binding.field.getName()));
                    ctx.buffered.append("\\\"");
                } else {
                    ctx.flushBuffer();
                    ctx.append(CodegenImplNative.genWriteOp("obj." + binding.field.getName(), binding.valueType));
                }
            } else {
                ctx.flushBuffer();
                ctx.append(String.format("com.jsoniter.output.CodegenAccess.writeVal(\"%s\", obj.%s, stream);",
                        fieldCacheKey, binding.field.getName()));
            }
        } else {
            ctx.flushBuffer();
            if (encoder == null) {
                ctx.append(CodegenImplNative.genWriteOp("obj." + binding.method.getName() + "()", binding.valueType));
            } else {
                ctx.append(String.format("com.jsoniter.output.CodegenAccess.writeVal(\"%s\", obj.%s(), stream);",
                        fieldCacheKey, binding.method.getName()));
            }
        }
    }
}
