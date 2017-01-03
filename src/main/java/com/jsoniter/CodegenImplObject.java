package com.jsoniter;

import com.jsoniter.spi.*;

import java.lang.reflect.Type;
import java.util.*;

class CodegenImplObject {

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
        append(lines, "if (iter.readNull()) { com.jsoniter.CodegenAccess.resetExistingObject(iter); return null; }");
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
                appendVarDef(lines, field);
            }
            for (Binding setter : desc.setters) {
                appendVarDef(lines, setter);
            }
        }
        for (WrapperDescriptor wrapper : desc.wrappers) {
            for (Binding param : wrapper.parameters) {
                appendVarDef(lines, param);
            }
        }
        // === bind first field
        if (desc.onExtraProperties != null) {
            append(lines, "java.util.Map extra = null;");
        }
        append(lines, "com.jsoniter.Slice field = com.jsoniter.CodegenAccess.readObjectFieldAsSlice(iter);");
        append(lines, "boolean once = true;");
        append(lines, "while (once) {");
        append(lines, "once = false;");
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
        if (!desc.ctor.parameters.isEmpty()) {
            append(lines, String.format("%s obj = {{newInst}};", CodegenImplNative.getTypeName(clazz)));
            for (Binding field : desc.fields) {
                append(lines, String.format("obj.%s = _%s_;", field.field.getName(), field.name));
            }
            for (Binding setter : desc.setters) {
                append(lines, String.format("obj.%s(_%s_);", setter.method.getName(), setter.name));
            }
        }
        appendWrappers(desc.wrappers, lines);
        append(lines, "return obj;");
        return lines.toString()
                .replace("{{clazz}}", clazz.getCanonicalName())
                .replace("{{newInst}}", genNewInstCode(clazz, desc.ctor));
    }

    private static void appendSetExtraProperteis(StringBuilder lines, ClassDescriptor desc) {
        Binding onExtraProperties = desc.onExtraProperties;
        if (ParameterizedTypeImpl.isSameClass(onExtraProperties.valueType, Map.class)) {
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
            append(lines, "throw new com.jsoniter.JsonException(\"missing required properties: \" + missingFields);");
        } else {
            if (desc.onMissingProperties.field != null) {
                append(lines, String.format("obj.%s = missingFields;", desc.onMissingProperties.field.getName()));
            } else {
                append(lines, String.format("obj.%s(missingFields);", desc.onMissingProperties.method.getName()));
            }
        }
    }

    private static void appendOnUnknownField(StringBuilder lines, ClassDescriptor desc) {
        if (desc.asExtraForUnknownProperties) {
            if (desc.onExtraProperties == null) {
                append(lines, "throw new com.jsoniter.JsonException('extra property: ' + field.toString());".replace('\'', '"'));
            } else {
                append(lines, "if (extra == null) { extra = new java.util.HashMap(); }");
                append(lines, "extra.put(field.toString(), iter.readAny());");
            }
        } else {
            append(lines, "iter.skip();");
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
                            "throw new com.jsoniter.JsonException('extra property: %s');".replace('\'', '"'),
                            field.name));
                } else if (field.shouldSkip) {
                    append(lines, "iter.skip();");
                    append(lines, "continue;");
                } else {
                    append(lines, String.format("_%s_ = %s;", field.name, genField(field)));
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

    // the implementation is from dsljson, it is the fastest although has the risk not matching field strictly
    public static String genObjectUsingHash(Class clazz, ClassDescriptor desc) {
        StringBuilder lines = new StringBuilder();
        // === if null, return null
        append(lines, "if (iter.readNull()) { com.jsoniter.CodegenAccess.resetExistingObject(iter); return null; }");
        // === if empty, return empty
        if (desc.ctor.parameters.isEmpty()) {
            // has default ctor
            append(lines, "{{clazz}} obj = {{newInst}};");
            append(lines, "if (!com.jsoniter.CodegenAccess.readObjectStart(iter)) { return obj; }");
        } else {
            // ctor requires binding
            for (Binding parameter : desc.ctor.parameters) {
                appendVarDef(lines, parameter);
            }
            append(lines, "if (!com.jsoniter.CodegenAccess.readObjectStart(iter)) { return {{newInst}}; }");
            for (Binding field : desc.fields) {
                appendVarDef(lines, field);
            }
            for (Binding setter : desc.setters) {
                appendVarDef(lines, setter);
            }
        }
        for (WrapperDescriptor setter : desc.wrappers) {
            for (Binding param : setter.parameters) {
                appendVarDef(lines, param);
            }
        }
        // === bind fields
        append(lines, "switch (com.jsoniter.CodegenAccess.readObjectFieldAsHash(iter)) {");
        HashSet<Integer> knownHashes = new HashSet<Integer>();
        List<Binding> bindings = desc.allDecoderBindings();
        for (Binding field : bindings) {
            for (String fromName : field.fromNames) {
                long hash = 0x811c9dc5;
                for (byte b : fromName.getBytes()) {
                    hash ^= b;
                    hash *= 0x1000193;
                }
                int intHash = (int) hash;
                if (intHash == 0) {
                    // hash collision, 0 can not be used as sentinel
                    return genObjectUsingStrict(clazz, desc);
                }
                if (knownHashes.contains(intHash)) {
                    // hash collision with other field can not be used as sentinel
                    return genObjectUsingStrict(clazz, desc);
                }
                knownHashes.add(intHash);
                append(lines, "case " + intHash + ": ");
                appendBindingSet(lines, desc, field);
                append(lines, "break;");
            }
        }
        append(lines, "default:");
        append(lines, "iter.skip();");
        append(lines, "}");
        // === bind more fields
        append(lines, "while (com.jsoniter.CodegenAccess.nextToken(iter) == ',') {");
        append(lines, "switch (com.jsoniter.CodegenAccess.readObjectFieldAsHash(iter)) {");
        for (Binding field : bindings) {
            for (String fromName : field.fromNames) {
                long hash = 0x811c9dc5;
                for (byte b : fromName.getBytes()) {
                    hash ^= b;
                    hash *= 0x1000193;
                }
                int intHash = (int) hash;
                append(lines, "case " + intHash + ": ");
                appendBindingSet(lines, desc, field);
                append(lines, "continue;");
            }
        }
        append(lines, "}");
        append(lines, "iter.skip();");
        append(lines, "}");
        if (!desc.ctor.parameters.isEmpty()) {
            append(lines, CodegenImplNative.getTypeName(clazz) + " obj = {{newInst}};");
            for (Binding field : desc.fields) {
                append(lines, String.format("obj.%s = _%s_;", field.field.getName(), field.name));
            }
            for (Binding setter : desc.setters) {
                append(lines, String.format("obj.%s(_%s_);", setter.method.getName(), setter.name));
            }
        }
        appendWrappers(desc.wrappers, lines);
        append(lines, "return obj;");
        return lines.toString()
                .replace("{{clazz}}", clazz.getCanonicalName())
                .replace("{{newInst}}", genNewInstCode(clazz, desc.ctor));
    }

    private static void appendBindingSet(StringBuilder lines, ClassDescriptor desc, Binding binding) {
        if (desc.ctor.parameters.isEmpty() && (desc.fields.contains(binding) || desc.setters.contains(binding))) {
            if (binding.valueCanReuse) {
                append(lines, String.format("com.jsoniter.CodegenAccess.setExistingObject(iter, obj.%s);", binding.field.getName()));
            }
            if (binding.field != null) {
                append(lines, String.format("obj.%s = %s;", binding.field.getName(), genField(binding)));
            } else {
                append(lines, String.format("obj.%s(%s);", binding.method.getName(), genField(binding)));
            }
        } else {
            append(lines, String.format("_%s_ = %s;", binding.name, genField(binding)));
        }
    }

    private static void appendWrappers(List<WrapperDescriptor> wrappers, StringBuilder lines) {
        for (WrapperDescriptor wrapper : wrappers) {
            lines.append("obj.");
            lines.append(wrapper.method.getName());
            appendInvocation(lines, wrapper.parameters);
            lines.append(";\n");
        }
    }

    private static void appendVarDef(StringBuilder lines, Binding parameter) {
        String typeName = CodegenImplNative.getTypeName(parameter.valueType);
        append(lines, String.format("%s _%s_ = %s;", typeName, parameter.name, DEFAULT_VALUES.get(typeName)));
    }

    public static String genObjectUsingSkip(Class clazz, ConstructorDescriptor ctor) {
        StringBuilder lines = new StringBuilder();
        append(lines, "if (iter.readNull()) { return null; }");
        append(lines, "{{clazz}} obj = {{newInst}};");
        append(lines, "iter.skip();");
        append(lines, "return obj;");
        return lines.toString()
                .replace("{{clazz}}", clazz.getCanonicalName())
                .replace("{{newInst}}", genNewInstCode(clazz, ctor));
    }

    private static String genNewInstCode(Class clazz, ConstructorDescriptor ctor) {
        StringBuilder code = new StringBuilder();
        if (ctor.parameters.isEmpty()) {
            // nothing to bind, safe to reuse existing object
            code.append("(com.jsoniter.CodegenAccess.existingObject(iter) == null ? ");
        }
        if (ctor.objectFactory != null) {
            code.append(String.format("(%s)com.jsoniter.spi.JsoniterSpi.create(%s.class)",
                    clazz.getCanonicalName(), clazz.getCanonicalName()));
        } else {
            if (ctor.staticMethodName == null) {
                code.append(String.format("new %s", clazz.getCanonicalName()));
            } else {
                code.append(String.format("%s.%s", clazz.getCanonicalName(), ctor.staticMethodName));
            }
        }
        List<Binding> params = ctor.parameters;
        if (ctor.objectFactory == null) {
            appendInvocation(code, params);
        }
        if (ctor.parameters.isEmpty()) {
            // nothing to bind, safe to reuse existing obj
            code.append(String.format(" : (%s)com.jsoniter.CodegenAccess.resetExistingObject(iter))", clazz.getCanonicalName()));
        }
        return code.toString();
    }

    private static void appendInvocation(StringBuilder code, List<Binding> params) {
        code.append("(");
        boolean isFirst = true;
        for (Binding ctorParam : params) {
            if (isFirst) {
                isFirst = false;
            } else {
                code.append(",");
            }
            code.append(String.format("_%s_", ctorParam.name));
        }
        code.append(")");
    }

    private static void append(StringBuilder lines, String str) {
        lines.append(str);
        lines.append("\n");
    }

    private static String genField(Binding field) {
        String fieldCacheKey = field.decoderCacheKey();
        // the field decoder might be registered directly
        Decoder decoder = JsoniterSpi.getDecoder(fieldCacheKey);
        Type fieldType = field.valueType;
        if (decoder == null) {
            return String.format("(%s)%s", CodegenImplNative.getTypeName(fieldType), CodegenImplNative.genReadOp(fieldType));
        }
        if (fieldType == boolean.class) {
            if (!(decoder instanceof Decoder.BooleanDecoder)) {
                throw new JsonException("decoder for field " + field + "must implement Decoder.BooleanDecoder");
            }
            return String.format("com.jsoniter.CodegenAccess.readBoolean(\"%s\", iter)", fieldCacheKey);
        }
        if (fieldType == byte.class) {
            if (!(decoder instanceof Decoder.ShortDecoder)) {
                throw new JsonException("decoder for field " + field + "must implement Decoder.ShortDecoder");
            }
            return String.format("com.jsoniter.CodegenAccess.readShort(\"%s\", iter)", fieldCacheKey);
        }
        if (fieldType == short.class) {
            if (!(decoder instanceof Decoder.ShortDecoder)) {
                throw new JsonException("decoder for field " + field + "must implement Decoder.ShortDecoder");
            }
            return String.format("com.jsoniter.CodegenAccess.readShort(\"%s\", iter)", fieldCacheKey);
        }
        if (fieldType == char.class) {
            if (!(decoder instanceof Decoder.IntDecoder)) {
                throw new JsonException("decoder for field " + field + "must implement Decoder.IntDecoder");
            }
            return String.format("com.jsoniter.CodegenAccess.readInt(\"%s\", iter)", fieldCacheKey);
        }
        if (fieldType == int.class) {
            if (!(decoder instanceof Decoder.IntDecoder)) {
                throw new JsonException("decoder for field " + field + "must implement Decoder.IntDecoder");
            }
            return String.format("com.jsoniter.CodegenAccess.readInt(\"%s\", iter)", fieldCacheKey);
        }
        if (fieldType == long.class) {
            if (!(decoder instanceof Decoder.LongDecoder)) {
                throw new JsonException("decoder for field " + field + "must implement Decoder.LongDecoder");
            }
            return String.format("com.jsoniter.CodegenAccess.readLong(\"%s\", iter)", fieldCacheKey);
        }
        if (fieldType == float.class) {
            if (!(decoder instanceof Decoder.FloatDecoder)) {
                throw new JsonException("decoder for field " + field + "must implement Decoder.FloatDecoder");
            }
            return String.format("com.jsoniter.CodegenAccess.readFloat(\"%s\", iter)", fieldCacheKey);
        }
        if (fieldType == double.class) {
            if (!(decoder instanceof Decoder.DoubleDecoder)) {
                throw new JsonException("decoder for field " + field + "must implement Decoder.DoubleDecoder");
            }
            return String.format("com.jsoniter.CodegenAccess.readDouble(\"%s\", iter)", fieldCacheKey);
        }
        return String.format("(%s)com.jsoniter.CodegenAccess.read(\"%s\", iter);",
                CodegenImplNative.getTypeName(fieldType), fieldCacheKey);
    }
}
