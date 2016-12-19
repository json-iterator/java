package com.jsoniter.output;

import com.jsoniter.*;
import com.jsoniter.spi.*;

class CodegenImplObject {
    public static String genObject(String cacheKey, Class clazz) {
        ClassDescriptor desc = ExtensionManager.getClassDescriptor(clazz, false);
        StringBuilder lines = new StringBuilder();
        append(lines, "public static void encode_(Object rawObj, com.jsoniter.output.JsonStream stream) {");
        append(lines, "if (rawObj == null) { stream.writeNull(); return; }");
        if (desc.allEncoderBindings().isEmpty()) {
            append(lines, "stream.writeEmptyObject();");
        } else {
            append(lines, "{{clazz}} obj = ({{clazz}})rawObj;");
            append(lines, "stream.startObject();");
            for (Binding field : desc.allEncoderBindings()) {
                for (String toName : field.toNames) {
                    append(lines, String.format("stream.writeField(\"%s\");", toName));
                    append(lines, genField(cacheKey, field));
                    append(lines, "stream.writeMore();");
                }
            }
            append(lines, "stream.endObject();");
        }
        append(lines, "}");
        return lines.toString().replace("{{clazz}}", clazz.getCanonicalName());
    }

    private static String genField(String cacheKey, Binding field) {
        String fieldCacheKey = field.name + "@" + cacheKey;
        if (field.encoder != null) {
            ExtensionManager.addNewEncoder(fieldCacheKey, field.encoder);
        }
        // the field decoder might be registered directly
        Encoder encoder = ExtensionManager.getEncoder(fieldCacheKey);
        if (encoder == null) {
            return CodegenImplNative.genWriteOp("obj." + field.name, field.valueType);
        }
        return String.format("com.jsoniter.output.CodegenAccess.writeVal(\"%s\", obj.%s, stream);",
                fieldCacheKey, field.name);
    }

    private static void append(StringBuilder lines, String str) {
        lines.append(str);
        lines.append("\n");
    }
}
