package com.jsoniter.output;

import com.jsoniter.Binding;
import com.jsoniter.ExtensionManager;

import java.util.ArrayList;
import java.util.List;

class CodegenImplObject {
    public static String genObject(Class clazz) {
        List<Binding> allBindings = new ArrayList<Binding>(ExtensionManager.getFields(clazz));
        allBindings.addAll(ExtensionManager.getGetters(clazz));
        StringBuilder lines = new StringBuilder();
        append(lines, "public static void encode_(Object rawObj, com.jsoniter.output.JsonStream stream) {");
        append(lines, "if (rawObj == null) { stream.writeNull(); return; }");
        if (allBindings.isEmpty()) {
            append(lines, "stream.writeEmptyObject();");
        } else {
            append(lines, "{{clazz}} obj = ({{clazz}})rawObj;");
            append(lines, "stream.startObject();");
            for (Binding field : allBindings) {
                for (String fromName : field.fromNames) {
                    append(lines, String.format("stream.writeField(\"%s\");", field.name));
                    append(lines, String.format("stream.writeVal(obj.%s);", fromName));
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
