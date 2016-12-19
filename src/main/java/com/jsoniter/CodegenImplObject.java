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


    public static String genObjectUsingSlice(Class clazz, String cacheKey, ClassDescriptor desc) {
        // TODO: when setter is single argument, decode like field
        List<Binding> allBindings = desc.allDecoderBindings();
        int currentIdx = 0;
        for (Binding binding : allBindings) {
            if (binding.failOnMissing) {
                binding.idx = currentIdx++;
            } else {
                binding.idx = -1;
            }
        }
        if (currentIdx > 63) {
            throw new JsonException("too many mandatory fields to track");
        }
        boolean hasMandatoryField = currentIdx > 0;
        long expectedTracker = Long.MAX_VALUE >> (63 - currentIdx);
        Map<Integer, Object> trieTree = buildTriTree(allBindings);
        StringBuilder lines = new StringBuilder();
        append(lines, "public static Object decode_(com.jsoniter.JsonIterator iter) {");
        // if null, return null
        append(lines, "if (iter.readNull()) { com.jsoniter.CodegenAccess.resetExistingObject(iter); return null; }");
        // if input is empty object, return empty object
        append(lines, "long tracker = 0;");
        if (desc.ctor.parameters.isEmpty()) {
            append(lines, "{{clazz}} obj = {{newInst}};");
            append(lines, "if (!com.jsoniter.CodegenAccess.readObjectStart(iter)) {");
            if (hasMandatoryField) {
                appendMissingMandatoryFields(lines);
            } else {
                append(lines, "return obj;");
            }
            append(lines, "}");
        } else {
            for (Binding parameter : desc.ctor.parameters) {
                appendVarDef(lines, parameter);
            }
            append(lines, "if (!com.jsoniter.CodegenAccess.readObjectStart(iter)) {");
            if (hasMandatoryField) {
                appendMissingMandatoryFields(lines);
            } else {
                append(lines, "return {{newInst}};");
            }
            append(lines, "}");
            for (Binding field : desc.fields) {
                appendVarDef(lines, field);
            }
        }
        for (SetterDescriptor setter : desc.setters) {
            for (Binding param : setter.parameters) {
                appendVarDef(lines, param);
            }
        }
        append(lines, "com.jsoniter.Slice field = com.jsoniter.CodegenAccess.readObjectFieldAsSlice(iter);");
        append(lines, "boolean once = true;");
        append(lines, "while (once) {");
        append(lines, "once = false;");
        String rendered = renderTriTree(cacheKey, trieTree);
        for (Binding field : desc.fields) {
            if (desc.ctor.parameters.isEmpty() && desc.fields.contains(field)) {
                if (shouldReuseObject(field.valueType)) {
                    rendered = rendered.replace("_" + field.name + "_", String.format("obj.%s", field.name));
                } else {
                    rendered = rendered.replace("_" + field.name + "_", String.format(
                            "com.jsoniter.CodegenAccess.setExistingObject(iter, obj.%s);\nobj.%s", field.name, field.name));
                }
            }
        }
        if (!allBindings.isEmpty()) {
            append(lines, "switch (field.len) {");
            append(lines, rendered);
            append(lines, "}"); // end of switch
        }
        appendOnUnknownField(lines, desc);
        append(lines, "}"); // end of while
        append(lines, "while (com.jsoniter.CodegenAccess.nextToken(iter) == ',') {");
        append(lines, "field = com.jsoniter.CodegenAccess.readObjectFieldAsSlice(iter);");
        if (!allBindings.isEmpty()) {
            append(lines, "switch (field.len) {");
            append(lines, rendered);
            append(lines, "}"); // end of switch
        }
        appendOnUnknownField(lines, desc);
        append(lines, "}"); // end of while
        append(lines, "if (tracker != " + expectedTracker + "L) {");
        appendMissingMandatoryFields(lines);
        append(lines, "}");
        if (!desc.ctor.parameters.isEmpty()) {
            append(lines, String.format("%s obj = {{newInst}};", CodegenImplNative.getTypeName(clazz)));
            for (Binding field : desc.fields) {
                append(lines, String.format("obj.%s = _%s_;", field.name, field.name));
            }
        }
        appendSetter(desc.setters, lines);
        append(lines, "return obj;");
        append(lines, "}");
        return lines.toString()
                .replace("{{clazz}}", clazz.getCanonicalName())
                .replace("{{newInst}}", genNewInstCode(clazz, desc.ctor));
    }

    private static void appendMissingMandatoryFields(StringBuilder lines) {
        append(lines, "throw new com.jsoniter.JsonException('missing mandatory fields');".replace('\'', '"'));
    }

    private static void appendOnUnknownField(StringBuilder lines, ClassDescriptor desc) {
        if (desc.failOnUnknownFields) {
            append(lines, "throw new com.jsoniter.JsonException('unknown field: ' + field.toString());".replace('\'', '"'));
        } else {
            append(lines, "iter.skip();");
        }
    }

    private static String renderTriTree(String cacheKey, Map<Integer, Object> trieTree) {
        StringBuilder switchBody = new StringBuilder();
        for (Map.Entry<Integer, Object> entry : trieTree.entrySet()) {
            Integer len = entry.getKey();
            append(switchBody, "case " + len + ": ");
            Map<Byte, Object> current = (Map<Byte, Object>) entry.getValue();
            addFieldDispatch(switchBody, len, 0, current, cacheKey, new ArrayList<Byte>());
            append(switchBody, "break;");
        }
        return switchBody.toString();
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

    private static void addFieldDispatch(
            StringBuilder lines, int len, int i, Map<Byte, Object> current, String cacheKey, List<Byte> bytesToCompare) {
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
                append(lines, String.format("_%s_ = %s;", field.name, genField(field, cacheKey)));
                if (field.failOnMissing) {
                    long mask = 1L << field.idx;
                    append(lines, "tracker = tracker | " + mask + "L;");
                }
                append(lines, "continue;");
                append(lines, "}");
                continue;
            }
            Map<Byte, Object> next = (Map<Byte, Object>) entry.getValue();
            if (next.size() == 1) {
                ArrayList<Byte> nextBytesToCompare = new ArrayList<Byte>(bytesToCompare);
                nextBytesToCompare.add(b);
                addFieldDispatch(lines, len, i + 1, next, cacheKey, nextBytesToCompare);
                continue;
            }
            append(lines, "if (");
            for (int j = 0; j < bytesToCompare.size(); j++) {
                Byte a = bytesToCompare.get(j);
                append(lines, String.format("field.at(%d)==%s && ", i - bytesToCompare.size() + j, a));
            }
            append(lines, String.format("field.at(%d)==%s", i, b));
            append(lines, ") {");
            addFieldDispatch(lines, len, i + 1, next, cacheKey, new ArrayList<Byte>());
            append(lines, "continue;");
            append(lines, "}");
        }
    }

    // the implementation from dsljson is not exactly correct
    // hash will collide, even the chance is small
    public static String genObjectUsingHash(Class clazz, String cacheKey, ClassDescriptor desc) {
        // TODO: when setter is single argument, decode like field
        StringBuilder lines = new StringBuilder();
        append(lines, "public static Object decode_(com.jsoniter.JsonIterator iter) {");
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
        }
        for (SetterDescriptor setter : desc.setters) {
            for (Binding param : setter.parameters) {
                appendVarDef(lines, param);
            }
        }
        // === bind fields
        append(lines, "switch (com.jsoniter.CodegenAccess.readObjectFieldAsHash(iter)) {");
        HashSet<Integer> knownHashes = new HashSet<Integer>();
        for (Binding field : desc.allDecoderBindings()) {
            for (String fromName : field.fromNames) {
                long hash = 0x811c9dc5;
                for (byte b : fromName.getBytes()) {
                    hash ^= b;
                    hash *= 0x1000193;
                }
                int intHash = (int) hash;
                if (intHash == 0) {
                    // hash collision, 0 can not be used as sentinel
                    return genObjectUsingSlice(clazz, cacheKey, desc);
                }
                if (knownHashes.contains(intHash)) {
                    // hash collision with other field can not be used as sentinel
                    return genObjectUsingSlice(clazz, cacheKey, desc);
                }
                knownHashes.add(intHash);
                append(lines, "case " + intHash + ": ");
                appendFieldSet(lines, cacheKey, desc.ctor, desc.fields, field);
                append(lines, "break;");
            }
        }
        append(lines, "default:");
        append(lines, "iter.skip();");
        append(lines, "}");
        // === bind more fields
        append(lines, "while (com.jsoniter.CodegenAccess.nextToken(iter) == ',') {");
        append(lines, "switch (com.jsoniter.CodegenAccess.readObjectFieldAsHash(iter)) {");
        for (Binding field : desc.allDecoderBindings()) {
            for (String fromName : field.fromNames) {
                long hash = 0x811c9dc5;
                for (byte b : fromName.getBytes()) {
                    hash ^= b;
                    hash *= 0x1000193;
                }
                int intHash = (int) hash;
                append(lines, "case " + intHash + ": ");
                appendFieldSet(lines, cacheKey, desc.ctor, desc.fields, field);
                append(lines, "continue;");
            }
        }
        append(lines, "}");
        append(lines, "iter.skip();");
        append(lines, "}");
        if (!desc.ctor.parameters.isEmpty()) {
            append(lines, CodegenImplNative.getTypeName(clazz) + " obj = {{newInst}};");
            for (Binding field : desc.fields) {
                append(lines, String.format("obj.%s = _%s_;", field.name, field.name));
            }
        }
        appendSetter(desc.setters, lines);
        append(lines, "return obj;");
        append(lines, "}");
        return lines.toString()
                .replace("{{clazz}}", clazz.getCanonicalName())
                .replace("{{newInst}}", genNewInstCode(clazz, desc.ctor));
    }

    private static void appendFieldSet(StringBuilder lines, String cacheKey, ConstructorDescriptor ctor, List<Binding> fields, Binding field) {
        if (ctor.parameters.isEmpty() && fields.contains(field)) {
            if (!shouldReuseObject(field.valueType)) {
                append(lines, String.format("com.jsoniter.CodegenAccess.setExistingObject(iter, obj.%s);", field.name));
            }
            append(lines, String.format("obj.%s = %s;", field.name, genField(field, cacheKey)));
        } else {
            append(lines, String.format("_%s_ = %s;", field.name, genField(field, cacheKey)));
        }
    }

    private static void appendSetter(List<SetterDescriptor> setters, StringBuilder lines) {
        for (SetterDescriptor setter : setters) {
            lines.append("obj.");
            lines.append(setter.methodName);
            appendInvocation(lines, setter.parameters);
            lines.append(";\n");
        }
    }

    private static void appendVarDef(StringBuilder lines, Binding parameter) {
        String typeName = CodegenImplNative.getTypeName(parameter.valueType);
        append(lines, String.format("%s _%s_ = %s;", typeName, parameter.name, DEFAULT_VALUES.get(typeName)));
    }

    public static String genObjectUsingSkip(Class clazz, ConstructorDescriptor ctor) {
        StringBuilder lines = new StringBuilder();
        append(lines, "public static Object decode_(com.jsoniter.JsonIterator iter) {");
        append(lines, "if (iter.readNull()) { return null; }");
        append(lines, "{{clazz}} obj = {{newInst}};");
        append(lines, "iter.skip();");
        append(lines, "return obj;");
        append(lines, "}");
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
        if (ctor.staticMethodName == null) {
            code.append(String.format("new %s", clazz.getCanonicalName()));
        } else {
            code.append(String.format("%s.%s", clazz.getCanonicalName(), ctor.staticMethodName));
        }
        List<Binding> params = ctor.parameters;
        appendInvocation(code, params);
        if (ctor.parameters.isEmpty()) {
            // nothing to bind, safe to reuse existing object
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

    public static boolean shouldReuseObject(Type valueType) {
        if (valueType instanceof  Class) {
            Class clazz = (Class) valueType;
            if (clazz.isArray()) {
                return false;
            }
        }
        return CodegenImplNative.isNative(valueType);
    }

    public static String genField(Binding field, String cacheKey) {
        String fieldCacheKey = field.name + "@" + cacheKey;
        if (field.decoder != null) {
            ExtensionManager.addNewDecoder(fieldCacheKey, field.decoder);
        }
        // the field decoder might be registered directly
        Decoder decoder = ExtensionManager.getDecoder(fieldCacheKey);
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
