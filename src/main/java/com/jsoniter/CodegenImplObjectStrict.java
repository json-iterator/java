package com.jsoniter;

import com.jsoniter.spi.*;

import java.lang.reflect.Method;
import java.util.*;

import static com.jsoniter.CodegenImplObjectHash.appendVarDef;
import static com.jsoniter.CodegenImplObjectHash.appendWrappers;

class CodegenImplObjectStrict {

    final static Map<String, String> DEFAULT_VALUES = new HashMap<String, String>() {{
        put("float", "0.0f");
        put("double", "0.0d");
        put("boolean", "false");
        put("byte", "0");
        put("short", "0");
        put("int", "0");
        put("char", "0");
        put("long", "0");
    }};

    public static String genObjectUsingStrict(ClassDescriptor desc) {
        List<Binding> allBindings = desc.allDecoderBindings();
        int lastRequiredIdx = assignMaskForRequiredProperties(allBindings);
        boolean hasRequiredBinding = lastRequiredIdx > 0;
        long expectedTracker = Long.MAX_VALUE >> (63 - lastRequiredIdx);
        Map<Integer, Object> trieTree = buildTriTree(allBindings);
        StringBuilder lines = new StringBuilder();
        /*
         * only strict mode binding support missing/extra properties tracking
         * 1. if null, return null
         * 2. if empty, return empty
         * 3. bind first field
         * 4. while (nextToken() == ',') { bind more fields }
         * 5. handle missing/extra properties
         * 6. create obj with args (if ctor binding)
         * 7. assign fields to obj (if ctor binding)
         * 8. apply multi param wrappers
         */
        // === if null, return null
        append(lines, "java.lang.Object existingObj = com.jsoniter.CodegenAccess.resetExistingObject(iter);");
        append(lines, "if (iter.readNull()) { return null; }");
        // === if input is empty obj, return empty obj
        if (hasRequiredBinding) {
            append(lines, "long tracker = 0;");
        }
        if (desc.ctor.parameters.isEmpty()) {
            append(lines, "{{clazz}} obj = {{newInst}};");
            append(lines, "if (!com.jsoniter.CodegenAccess.readObjectStart(iter)) {");
            if (hasRequiredBinding) {
                appendMissingRequiredProperties(lines, desc);
            }
            append(lines, "return obj;");
            append(lines, "}");
            // because obj can be created without binding
            // so that fields and setters can be bind to obj directly without temp var
        } else {
            for (Binding parameter : desc.ctor.parameters) {
                appendVarDef(lines, parameter);
            }
            append(lines, "if (!com.jsoniter.CodegenAccess.readObjectStart(iter)) {");
            if (hasRequiredBinding) {
                appendMissingRequiredProperties(lines, desc);
            } else {
                append(lines, "return {{newInst}};");
            }
            append(lines, "}");
            for (Binding field : desc.fields) {
                if (field.fromNames.length == 0) {
                    continue;
                }
                appendVarDef(lines, field);
            }
            for (Binding setter : desc.setters) {
                appendVarDef(lines, setter);
            }
        }
        for (WrapperDescriptor wrapper : desc.bindingTypeWrappers) {
            for (Binding param : wrapper.parameters) {
                appendVarDef(lines, param);
            }
        }
        // === bind first field
        if (desc.onExtraProperties != null || !desc.keyValueTypeWrappers.isEmpty()) {
            append(lines, "java.util.Map extra = null;");
        }
        append(lines, "com.jsoniter.spi.Slice field = com.jsoniter.CodegenAccess.readObjectFieldAsSlice(iter);");
        append(lines, "boolean once = true;");
        append(lines, "while (once) {");
        append(lines, "once = false;");
        String rendered = renderTriTree(trieTree);
        if (desc.ctor.parameters.isEmpty()) {
            // if not field or setter, the value will set to temp variable
            for (Binding field : desc.fields) {
                if (field.fromNames.length == 0) {
                    continue;
                }
                rendered = updateBindingSetOp(rendered, field);
            }
            for (Binding setter : desc.setters) {
                rendered = updateBindingSetOp(rendered, setter);
            }
        }
        if (hasAnythingToBindFrom(allBindings)) {
            append(lines, "switch (field.len()) {");
            append(lines, rendered);
            append(lines, "}"); // end of switch
        }
        appendOnUnknownField(lines, desc);
        append(lines, "}"); // end of while
        // === bind all fields
        append(lines, "while (com.jsoniter.CodegenAccess.nextToken(iter) == ',') {");
        append(lines, "field = com.jsoniter.CodegenAccess.readObjectFieldAsSlice(iter);");
        if (hasAnythingToBindFrom(allBindings)) {
            append(lines, "switch (field.len()) {");
            append(lines, rendered);
            append(lines, "}"); // end of switch
        }
        appendOnUnknownField(lines, desc);
        append(lines, "}"); // end of while
        if (hasRequiredBinding) {
            append(lines, "if (tracker != " + expectedTracker + "L) {");
            appendMissingRequiredProperties(lines, desc);
            append(lines, "}");
        }
        if (desc.onExtraProperties != null) {
            appendSetExtraProperteis(lines, desc);
        }
        if (!desc.keyValueTypeWrappers.isEmpty()) {
            appendSetExtraToKeyValueTypeWrappers(lines, desc);
        }
        if (!desc.ctor.parameters.isEmpty()) {
            append(lines, String.format("%s obj = {{newInst}};", CodegenImplNative.getTypeName(desc.clazz)));
            for (Binding field : desc.fields) {
                if (field.fromNames.length == 0) {
                    continue;
                }
                append(lines, String.format("obj.%s = _%s_;", field.field.getName(), field.name));
            }
            for (Binding setter : desc.setters) {
                append(lines, String.format("obj.%s(_%s_);", setter.method.getName(), setter.name));
            }
        }
        appendWrappers(desc.bindingTypeWrappers, lines);
        append(lines, "return obj;");
        return lines.toString()
                .replace("{{clazz}}", desc.clazz.getCanonicalName())
                .replace("{{newInst}}", CodegenImplObjectHash.genNewInstCode(desc.clazz, desc.ctor));
    }

    private static void appendSetExtraToKeyValueTypeWrappers(StringBuilder lines, ClassDescriptor desc) {
        append(lines, "java.util.Iterator extraIter = extra.entrySet().iterator();");
        append(lines, "while(extraIter.hasNext()) {");
        for (Method wrapper : desc.keyValueTypeWrappers) {
            append(lines, "java.util.Map.Entry entry = (java.util.Map.Entry)extraIter.next();");
            append(lines, "String key = entry.getKey().toString();");
            append(lines, "com.jsoniter.any.Any value = (com.jsoniter.any.Any)entry.getValue();");
            append(lines, String.format("obj.%s(key, value.object());", wrapper.getName()));
        }
        append(lines, "}");
    }

    private static void appendSetExtraProperteis(StringBuilder lines, ClassDescriptor desc) {
        Binding onExtraProperties = desc.onExtraProperties;
        if (GenericsHelper.isSameClass(onExtraProperties.valueType, Map.class)) {
            if (onExtraProperties.field != null) {
                append(lines, String.format("obj.%s = extra;", onExtraProperties.field.getName()));
            } else {
                append(lines, String.format("obj.%s(extra);", onExtraProperties.method.getName()));
            }
            return;
        }
        throw new JsonException("extra properties can only be Map");
    }

    private static boolean hasAnythingToBindFrom(List<Binding> allBindings) {
        for (Binding binding : allBindings) {
            if (binding.fromNames.length > 0) {
                return true;
            }
        }
        return false;
    }

    private static int assignMaskForRequiredProperties(List<Binding> allBindings) {
        int requiredIdx = 0;
        for (Binding binding : allBindings) {
            if (binding.asMissingWhenNotPresent) {
                // one bit represent one field
                binding.mask = 1L << requiredIdx;
                requiredIdx++;
            }
        }
        if (requiredIdx > 63) {
            throw new JsonException("too many required properties to track");
        }
        return requiredIdx;
    }

    private static String updateBindingSetOp(String rendered, Binding binding) {
        if (binding.fromNames.length == 0) {
            return rendered;
        }
        while (true) {
            String marker = "_" + binding.name + "_";
            int start = rendered.indexOf(marker);
            if (start == -1) {
                return rendered;
            }
            int middle = rendered.indexOf('=', start);
            if (middle == -1) {
                throw new JsonException("can not find = in: " + rendered + " ,at " + start);
            }
            middle += 1;
            int end = rendered.indexOf(';', start);
            if (end == -1) {
                throw new JsonException("can not find ; in: " + rendered + " ,at " + start);
            }
            String op = rendered.substring(middle, end);
            if (binding.field != null) {
                if (binding.valueCanReuse) {
                    // reuse; then field set
                    rendered = String.format("%scom.jsoniter.CodegenAccess.setExistingObject(iter, obj.%s);obj.%s=%s%s",
                            rendered.substring(0, start), binding.field.getName(), binding.field.getName(), op, rendered.substring(end));
                } else {
                    // just field set
                    rendered = String.format("%sobj.%s=%s%s",
                            rendered.substring(0, start), binding.field.getName(), op, rendered.substring(end));
                }
            } else {
                // method set
                rendered = String.format("%sobj.%s(%s)%s",
                        rendered.substring(0, start), binding.method.getName(), op, rendered.substring(end));
            }
        }
    }

    private static void appendMissingRequiredProperties(StringBuilder lines, ClassDescriptor desc) {
        append(lines, "java.util.List missingFields = new java.util.ArrayList();");
        for (Binding binding : desc.allDecoderBindings()) {
            if (binding.asMissingWhenNotPresent) {
                long mask = binding.mask;
                append(lines, String.format("com.jsoniter.CodegenAccess.addMissingField(missingFields, tracker, %sL, \"%s\");",
                        mask, binding.name));
            }
        }
        if (desc.onMissingProperties == null || !desc.ctor.parameters.isEmpty()) {
            append(lines, "throw new com.jsoniter.spi.JsonException(\"missing required properties: \" + missingFields);");
        } else {
            if (desc.onMissingProperties.field != null) {
                append(lines, String.format("obj.%s = missingFields;", desc.onMissingProperties.field.getName()));
            } else {
                append(lines, String.format("obj.%s(missingFields);", desc.onMissingProperties.method.getName()));
            }
        }
    }

    private static void appendOnUnknownField(StringBuilder lines, ClassDescriptor desc) {
        if (desc.asExtraForUnknownProperties && desc.onExtraProperties == null) {
            append(lines, "throw new com.jsoniter.spi.JsonException('extra property: ' + field.toString());".replace('\'', '"'));
        } else {
            if (desc.asExtraForUnknownProperties || !desc.keyValueTypeWrappers.isEmpty()) {
                append(lines, "if (extra == null) { extra = new java.util.HashMap(); }");
                append(lines, "extra.put(field.toString(), iter.readAny());");
            } else {
                append(lines, "iter.skip();");
            }
        }
    }

    private static Map<Integer, Object> buildTriTree(List<Binding> allBindings) {
        Map<Integer, Object> trieTree = new HashMap<Integer, Object>();
        for (Binding field : allBindings) {
            for (String fromName : field.fromNames) {
                byte[] fromNameBytes = fromName.getBytes();
                Map<Byte, Object> current = (Map<Byte, Object>) trieTree.get(fromNameBytes.length);
                if (current == null) {
                    current = new HashMap<Byte, Object>();
                    trieTree.put(fromNameBytes.length, current);
                }
                for (int i = 0; i < fromNameBytes.length - 1; i++) {
                    byte b = fromNameBytes[i];
                    Map<Byte, Object> next = (Map<Byte, Object>) current.get(b);
                    if (next == null) {
                        next = new HashMap<Byte, Object>();
                        current.put(b, next);
                    }
                    current = next;
                }
                current.put(fromNameBytes[fromNameBytes.length - 1], field);
            }
        }
        return trieTree;
    }

    private static String renderTriTree(Map<Integer, Object> trieTree) {
        StringBuilder switchBody = new StringBuilder();
        for (Map.Entry<Integer, Object> entry : trieTree.entrySet()) {
            Integer len = entry.getKey();
            append(switchBody, "case " + len + ": ");
            Map<Byte, Object> current = (Map<Byte, Object>) entry.getValue();
            addFieldDispatch(switchBody, len, 0, current, new ArrayList<Byte>());
            append(switchBody, "break;");
        }
        return switchBody.toString();
    }

    private static void addFieldDispatch(
            StringBuilder lines, int len, int i, Map<Byte, Object> current, List<Byte> bytesToCompare) {
        for (Map.Entry<Byte, Object> entry : current.entrySet()) {
            Byte b = entry.getKey();
            if (i == len - 1) {
                append(lines, "if (");
                for (int j = 0; j < bytesToCompare.size(); j++) {
                    Byte a = bytesToCompare.get(j);
                    append(lines, String.format("field.at(%d)==%s && ", i - bytesToCompare.size() + j, a));
                }
                append(lines, String.format("field.at(%d)==%s", i, b));
                append(lines, ") {");
                Binding field = (Binding) entry.getValue();
                if (field.asExtraWhenPresent) {
                    append(lines, String.format(
                            "throw new com.jsoniter.spi.JsonException('extra property: %s');".replace('\'', '"'),
                            field.name));
                } else if (field.shouldSkip) {
                    append(lines, "iter.skip();");
                    append(lines, "continue;");
                } else {
                    append(lines, String.format("_%s_ = %s;", field.name, CodegenImplNative.genField(field)));
                    if (field.asMissingWhenNotPresent) {
                        append(lines, "tracker = tracker | " + field.mask + "L;");
                    }
                    append(lines, "continue;");
                }
                append(lines, "}");
                continue;
            }
            Map<Byte, Object> next = (Map<Byte, Object>) entry.getValue();
            if (next.size() == 1) {
                ArrayList<Byte> nextBytesToCompare = new ArrayList<Byte>(bytesToCompare);
                nextBytesToCompare.add(b);
                addFieldDispatch(lines, len, i + 1, next, nextBytesToCompare);
                continue;
            }
            append(lines, "if (");
            for (int j = 0; j < bytesToCompare.size(); j++) {
                Byte a = bytesToCompare.get(j);
                append(lines, String.format("field.at(%d)==%s && ", i - bytesToCompare.size() + j, a));
            }
            append(lines, String.format("field.at(%d)==%s", i, b));
            append(lines, ") {");
            addFieldDispatch(lines, len, i + 1, next, new ArrayList<Byte>());
            append(lines, "}");
        }
    }

    public static String genObjectUsingSkip(Class clazz, ConstructorDescriptor ctor) {
        StringBuilder lines = new StringBuilder();
        append(lines, "if (iter.readNull()) { return null; }");
        append(lines, "{{clazz}} obj = {{newInst}};");
        append(lines, "iter.skip();");
        append(lines, "return obj;");
        return lines.toString()
                .replace("{{clazz}}", clazz.getCanonicalName())
                .replace("{{newInst}}", CodegenImplObjectHash.genNewInstCode(clazz, ctor));
    }

    static void append(StringBuilder lines, String str) {
        lines.append(str);
        lines.append("\n");
    }
}
