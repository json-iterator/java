package com.jsoniter.output;

import com.jsoniter.spi.Binding;
import com.jsoniter.spi.ClassDescriptor;
import com.jsoniter.spi.ExtensionManager;

class CodegenImplObject {
    public static String genObject(Class clazz) {
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
                for (String fromName : field.fromNames) {
                    append(lines, String.format("stream.writeField(\"%s\");", field.name));
                    append(lines, CodegenImplNative.genWriteOp("obj." + fromName, field.valueType));
                    append(lines, "stream.writeMore();");
                }
            }
            append(lines, "stream.endObject();");
        }
        append(lines, "}");
        return lines.toString().replace("{{clazz}}", clazz.getCanonicalName());
    }

    private static void append(StringBuilder lines, String str) {
        lines.append(str);
        lines.append("\n");
    }
}
