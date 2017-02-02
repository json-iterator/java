package com.jsoniter;

import com.jsoniter.spi.*;

import java.util.*;

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

    public static String genObjectUsingStrict(Class clazz, ClassDescriptor desc) {
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
        CodegenImplObjectHash.append(lines, "if (iter.readNull()) { com.jsoniter.CodegenAccess.resetExistingObject(iter); return null; }");
        // === if input is empty obj, return empty obj
        if (hasRequiredBinding) {
            CodegenImplObjectHash.append(lines, "long tracker = 0;");
        }
        if (desc.ctor.parameters.isEmpty()) {
            CodegenImplObjectHash.append(lines, "{{clazz}} obj = {{newInst}};");
            CodegenImplObjectHash.append(lines, "if (!com.jsoniter.CodegenAccess.readObjectStart(iter)) {");
            if (hasRequiredBinding) {
                appendMissingRequiredProperties(lines, desc);
            }
            CodegenImplObjectHash.append(lines, "return obj;");
            CodegenImplObjectHash.append(lines, "}");
            // because obj can be created without binding
            // so that fields and setters can be bind to obj directly without temp var
        } else {
            for (Binding parameter : desc.ctor.parameters) {
                CodegenImplObjectHash.appendVarDef(lines, parameter);
            }
            CodegenImplObjectHash.append(lines, "if (!com.jsoniter.CodegenAccess.readObjectStart(iter)) {");
            if (hasRequiredBinding) {
                appendMissingRequiredProperties(lines, desc);
            } else {
                CodegenImplObjectHash.append(lines, "return {{newInst}};");
            }
            CodegenImplObjectHash.append(lines, "}");
            for (Binding field : desc.fields) {
                CodegenImplObjectHash.appendVarDef(lines, field);
            }
            for (Binding setter : desc.setters) {
                CodegenImplObjectHash.appendVarDef(lines, setter);
            }
        }
        for (WrapperDescriptor wrapper : desc.wrappers) {
            for (Binding param : wrapper.parameters) {
                CodegenImplObjectHash.appendVarDef(lines, param);
            }
        }
        // === bind first field
        if (desc.onExtraProperties != null) {
            CodegenImplObjectHash.append(lines, "java.util.Map extra = null;");
        }
        CodegenImplObjectHash.append(lines, "com.jsoniter.Slice field = com.jsoniter.CodegenAccess.readObjectFieldAsSlice(iter);");
        CodegenImplObjectHash.append(lines, "boolean once = true;");
        CodegenImplObjectHash.append(lines, "while (once) {");
        CodegenImplObjectHash.append(lines, "once = false;");
        String rendered = renderTriTree(trieTree);
        if (desc.ctor.parameters.isEmpty()) {
            // if not field or setter, the value will set to temp variable
            for (Binding field : desc.fields) {
                rendered = updateBindingSetOp(rendered, field);
            }
            for (Binding setter : desc.setters) {
                rendered = updateBindingSetOp(rendered, setter);
            }
        }
        if (hasAnythingToBindFrom(allBindings)) {
            CodegenImplObjectHash.append(lines, "switch (field.len()) {");
            CodegenImplObjectHash.append(lines, rendered);
            CodegenImplObjectHash.append(lines, "}"); // end of switch
        }
        appendOnUnknownField(lines, desc);
        CodegenImplObjectHash.append(lines, "}"); // end of while
        // === bind all fields
        CodegenImplObjectHash.append(lines, "while (com.jsoniter.CodegenAccess.nextToken(iter) == ',') {");
        CodegenImplObjectHash.append(lines, "field = com.jsoniter.CodegenAccess.readObjectFieldAsSlice(iter);");
        if (hasAnythingToBindFrom(allBindings)) {
            CodegenImplObjectHash.append(lines, "switch (field.len()) {");
            CodegenImplObjectHash.append(lines, rendered);
            CodegenImplObjectHash.append(lines, "}"); // end of switch
        }
        appendOnUnknownField(lines, desc);
        CodegenImplObjectHash.append(lines, "}"); // end of while
        if (hasRequiredBinding) {
            CodegenImplObjectHash.append(lines, "if (tracker != " + expectedTracker + "L) {");
            appendMissingRequiredProperties(lines, desc);
            CodegenImplObjectHash.append(lines, "}");
        }
        if (desc.onExtraProperties != null) {
            appendSetExtraProperteis(lines, desc);
        }
        if (!desc.ctor.parameters.isEmpty()) {
            CodegenImplObjectHash.append(lines, String.format("%s obj = {{newInst}};", CodegenImplNative.getTypeName(clazz)));
            for (Binding field : desc.fields) {
                CodegenImplObjectHash.append(lines, String.format("obj.%s = _%s_;", field.field.getName(), field.name));
            }
            for (Binding setter : desc.setters) {
                CodegenImplObjectHash.append(lines, String.format("obj.%s(_%s_);", setter.method.getName(), setter.name));
            }
        }
        CodegenImplObjectHash.appendWrappers(desc.wrappers, lines);
        CodegenImplObjectHash.append(lines, "return obj;");
        return lines.toString()
                .replace("{{clazz}}", clazz.getCanonicalName())
                .replace("{{newInst}}", CodegenImplObjectHash.genNewInstCode(clazz, desc.ctor));
    }

    private static void appendSetExtraProperteis(StringBuilder lines, ClassDescriptor desc) {
        Binding onExtraProperties = desc.onExtraProperties;
        if (ParameterizedTypeImpl.isSameClass(onExtraProperties.valueType, Map.class)) {
            if (onExtraProperties.field != null) {
                CodegenImplObjectHash.append(lines, String.format("obj.%s = extra;", onExtraProperties.field.getName()));
            } else {
                CodegenImplObjectHash.append(lines, String.format("obj.%s(extra);", onExtraProperties.method.getName()));
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
        CodegenImplObjectHash.append(lines, "java.util.List missingFields = new java.util.ArrayList();");
        for (Binding binding : desc.allDecoderBindings()) {
            if (binding.asMissingWhenNotPresent) {
                long mask = binding.mask;
                CodegenImplObjectHash.append(lines, String.format("com.jsoniter.CodegenAccess.addMissingField(missingFields, tracker, %sL, \"%s\");",
                        mask, binding.name));
            }
        }
        if (desc.onMissingProperties == null || !desc.ctor.parameters.isEmpty()) {
            CodegenImplObjectHash.append(lines, "throw new com.jsoniter.spi.JsonException(\"missing required properties: \" + missingFields);");
        } else {
            if (desc.onMissingProperties.field != null) {
                CodegenImplObjectHash.append(lines, String.format("obj.%s = missingFields;", desc.onMissingProperties.field.getName()));
            } else {
                CodegenImplObjectHash.append(lines, String.format("obj.%s(missingFields);", desc.onMissingProperties.method.getName()));
            }
        }
    }

    private static void appendOnUnknownField(StringBuilder lines, ClassDescriptor desc) {
        if (desc.asExtraForUnknownProperties) {
            if (desc.onExtraProperties == null) {
                CodegenImplObjectHash.append(lines, "throw new com.jsoniter.spi.JsonException('extra property: ' + field.toString());".replace('\'', '"'));
            } else {
                CodegenImplObjectHash.append(lines, "if (extra == null) { extra = new java.util.HashMap(); }");
                CodegenImplObjectHash.append(lines, "extra.put(field.toString(), iter.readAny());");
            }
        } else {
            CodegenImplObjectHash.append(lines, "iter.skip();");
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
            CodegenImplObjectHash.append(switchBody, "case " + len + ": ");
            Map<Byte, Object> current = (Map<Byte, Object>) entry.getValue();
            addFieldDispatch(switchBody, len, 0, current, new ArrayList<Byte>());
            CodegenImplObjectHash.append(switchBody, "break;");
        }
        return switchBody.toString();
    }

    private static void addFieldDispatch(
            StringBuilder lines, int len, int i, Map<Byte, Object> current, List<Byte> bytesToCompare) {
        for (Map.Entry<Byte, Object> entry : current.entrySet()) {
            Byte b = entry.getKey();
            if (i == len - 1) {
                CodegenImplObjectHash.append(lines, "if (");
                for (int j = 0; j < bytesToCompare.size(); j++) {
                    Byte a = bytesToCompare.get(j);
                    CodegenImplObjectHash.append(lines, String.format("field.at(%d)==%s && ", i - bytesToCompare.size() + j, a));
                }
                CodegenImplObjectHash.append(lines, String.format("field.at(%d)==%s", i, b));
                CodegenImplObjectHash.append(lines, ") {");
                Binding field = (Binding) entry.getValue();
                if (field.asExtraWhenPresent) {
                    CodegenImplObjectHash.append(lines, String.format(
                            "throw new com.jsoniter.spi.JsonException('extra property: %s');".replace('\'', '"'),
                            field.name));
                } else if (field.shouldSkip) {
                    CodegenImplObjectHash.append(lines, "iter.skip();");
                    CodegenImplObjectHash.append(lines, "continue;");
                } else {
                    CodegenImplObjectHash.append(lines, String.format("_%s_ = %s;", field.name, CodegenImplNative.genField(field)));
                    if (field.asMissingWhenNotPresent) {
                        CodegenImplObjectHash.append(lines, "tracker = tracker | " + field.mask + "L;");
                    }
                    CodegenImplObjectHash.append(lines, "continue;");
                }
                CodegenImplObjectHash.append(lines, "}");
                continue;
            }
            Map<Byte, Object> next = (Map<Byte, Object>) entry.getValue();
            if (next.size() == 1) {
                ArrayList<Byte> nextBytesToCompare = new ArrayList<Byte>(bytesToCompare);
                nextBytesToCompare.add(b);
                addFieldDispatch(lines, len, i + 1, next, nextBytesToCompare);
                continue;
            }
            CodegenImplObjectHash.append(lines, "if (");
            for (int j = 0; j < bytesToCompare.size(); j++) {
                Byte a = bytesToCompare.get(j);
                CodegenImplObjectHash.append(lines, String.format("field.at(%d)==%s && ", i - bytesToCompare.size() + j, a));
            }
            CodegenImplObjectHash.append(lines, String.format("field.at(%d)==%s", i, b));
            CodegenImplObjectHash.append(lines, ") {");
            addFieldDispatch(lines, len, i + 1, next, new ArrayList<Byte>());
            CodegenImplObjectHash.append(lines, "}");
        }
    }

    public static String genObjectUsingSkip(Class clazz, ConstructorDescriptor ctor) {
        StringBuilder lines = new StringBuilder();
        CodegenImplObjectHash.append(lines, "if (iter.readNull()) { return null; }");
        CodegenImplObjectHash.append(lines, "{{clazz}} obj = {{newInst}};");
        CodegenImplObjectHash.append(lines, "iter.skip();");
        CodegenImplObjectHash.append(lines, "return obj;");
        return lines.toString()
                .replace("{{clazz}}", clazz.getCanonicalName())
                .replace("{{newInst}}", CodegenImplObjectHash.genNewInstCode(clazz, ctor));
    }
}
