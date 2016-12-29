package com.jsoniter.output;

import com.jsoniter.spi.Binding;
import com.jsoniter.spi.ClassDescriptor;
import com.jsoniter.spi.Encoder;
import com.jsoniter.spi.JsoniterSpi;

import java.lang.reflect.Method;

class CodegenImplObject {
    public static String genObject(Class clazz) {
        ClassDescriptor desc = JsoniterSpi.getEncodingClassDescriptor(clazz, false);
        StringBuilder lines = new StringBuilder();
        append(lines, String.format("public static void encode_(%s obj, com.jsoniter.output.JsonStream stream) throws java.io.IOException {", clazz.getCanonicalName()));
        append(lines, "if (obj == null) { stream.writeNull(); return; }");
        if (hasFieldOutput(desc)) {
            boolean notFirst = false;
            append(lines, "stream.writeObjectStart();");
            for (Binding field : desc.allEncoderBindings()) {
                for (String toName : field.toNames) {
                    if (notFirst) {
                        append(lines, "stream.writeMore();");
                    } else {
                        notFirst = true;
                    }
                    append(lines, String.format("stream.writeObjectField(\"%s\");", toName));
                    append(lines, genField(field));
                }
            }
            for (Method unwrapper : desc.unwrappers) {
                append(lines, String.format("obj.%s(stream);", unwrapper.getName()));
            }
            append(lines, "stream.writeObjectEnd();");
        } else {
            append(lines, "stream.writeEmptyObject();");
        }
        append(lines, "}");
        return lines.toString().replace("{{clazz}}", clazz.getCanonicalName());
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

    private static String genField(Binding binding) {
        String fieldCacheKey = binding.encoderCacheKey();
        Encoder encoder = JsoniterSpi.getEncoder(fieldCacheKey);
        if (binding.field != null) {
            if (encoder == null) {
                return CodegenImplNative.genWriteOp("obj." + binding.field.getName(), binding.valueType);
            } else {
                return String.format("com.jsoniter.output.CodegenAccess.writeVal(\"%s\", obj.%s, stream);",
                        fieldCacheKey, binding.field.getName());
            }
        } else {
            if (encoder == null) {
                return CodegenImplNative.genWriteOp("obj." + binding.method.getName() + "()", binding.valueType);
            } else {
                return String.format("com.jsoniter.output.CodegenAccess.writeVal(\"%s\", obj.%s(), stream);",
                        fieldCacheKey, binding.method.getName());
            }
        }
    }

    private static void append(StringBuilder lines, String str) {
        lines.append(str);
        lines.append("\n");
    }
}
