package com.jsoniter;

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

    public static String genObjectUsingSlice(Class clazz, String cacheKey, CustomizedConstructor ctor,
                                             List<CustomizedSetter> setters, List<Binding> fields) {
        ArrayList<Binding> allBindings = new ArrayList<Binding>(fields);
        allBindings.addAll(ctor.parameters);
        for (CustomizedSetter setter : setters) {
            allBindings.addAll(setter.parameters);
        }
        if (allBindings.isEmpty()) {
            return genObjectUsingSkip(clazz, ctor);
        }
        Map<Integer, Object> trieTree = buildTriTree(allBindings);
        StringBuilder lines = new StringBuilder();
        append(lines, "public static Object decode_(com.jsoniter.JsonIterator iter) {");
        append(lines, "if (iter.readNull()) { return null; }");
        if (ctor.parameters.isEmpty()) {
            append(lines, "{{clazz}} obj = {{newInst}};");
            append(lines, "if (!com.jsoniter.CodegenAccess.readObjectStart(iter)) { return obj; }");
        } else {
            for (Binding parameter : ctor.parameters) {
                appendVarDef(lines, parameter);
            }
            append(lines, "if (!com.jsoniter.CodegenAccess.readObjectStart(iter)) { return {{newInst}}; }");
            for (Binding field : fields) {
                appendVarDef(lines, field);
            }
        }
        for (CustomizedSetter setter : setters) {
            for (Binding param : setter.parameters) {
                appendVarDef(lines, param);
            }
        }
        append(lines, "com.jsoniter.CodegenAccess.resetExistingObject(iter);");
        append(lines, "com.jsoniter.Slice field = com.jsoniter.CodegenAccess.readObjectFieldAsSlice(iter);");
        append(lines, "boolean once = true;");
        append(lines, "while (once) {");
        append(lines, "once = false;");
        append(lines, "switch (field.len) {");
        String rendered = renderTriTree(cacheKey, trieTree);
        for (Binding field : fields) {
            if (ctor.parameters.isEmpty() && fields.contains(field)) {
                if (shouldReuseObject(field.valueType)) {
                    rendered = rendered.replace("_" + field.name + "_", String.format("obj.%s", field.name));
                } else {
                    rendered = rendered.replace("_" + field.name + "_", String.format(
                            "com.jsoniter.CodegenAccess.setExistingObject(iter, obj.%s);\nobj.%s", field.name, field.name));
                }
            }
        }
        append(lines, rendered);
        append(lines, "}"); // end of switch
        append(lines, "iter.skip();");
        append(lines, "}"); // end of while
        append(lines, "while (com.jsoniter.CodegenAccess.nextToken(iter) == ',') {");
        append(lines, "field = com.jsoniter.CodegenAccess.readObjectFieldAsSlice(iter);");
        append(lines, "switch (field.len) {");
        append(lines, rendered);
        append(lines, "}"); // end of switch
        append(lines, "iter.skip();");
        append(lines, "}"); // end of while
        if (!ctor.parameters.isEmpty()) {
            append(lines, String.format("%s obj = {{newInst}};", CodegenImplNative.getTypeName(clazz)));
            for (Binding field : fields) {
                append(lines, String.format("obj.%s = _%s_;", field.name, field.name));
            }
        }
        appendSetter(setters, lines);
        append(lines, "return obj;");
        append(lines, "}");
        return lines.toString()
                .replace("{{clazz}}", clazz.getCanonicalName())
                .replace("{{newInst}}", genNewInstCode(clazz, ExtensionManager.getCtor(clazz)));
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

    private static Map<Integer, Object> buildTriTree(ArrayList<Binding> allBindings) {
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
                append(lines, String.format("_%s_ = %s;", field.name, CodegenImplNative.genField(field, cacheKey)));
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

    public static String genObjectUsingHash(Class clazz, String cacheKey, CustomizedConstructor ctor,
                                            List<CustomizedSetter> setters, List<Binding> fields) {
        ArrayList<Binding> allBindings = new ArrayList<Binding>(fields);
        allBindings.addAll(ctor.parameters);
        for (CustomizedSetter setter : setters) {
            allBindings.addAll(setter.parameters);
        }
        if (allBindings.isEmpty()) {
            return genObjectUsingSkip(clazz, ctor);
        }
        StringBuilder lines = new StringBuilder();
        append(lines, "public static Object decode_(com.jsoniter.JsonIterator iter) {");
        append(lines, "if (iter.readNull()) { return null; }");
        if (ctor.parameters.isEmpty()) {
            // has default ctor
            append(lines, "{{clazz}} obj = {{newInst}};");
            append(lines, "if (!com.jsoniter.CodegenAccess.readObjectStart(iter)) { return obj; }");
        } else {
            // ctor requires binding
            for (Binding parameter : ctor.parameters) {
                appendVarDef(lines, parameter);
            }
            append(lines, "if (!com.jsoniter.CodegenAccess.readObjectStart(iter)) { return {{newInst}}; }");
            for (Binding field : fields) {
                appendVarDef(lines, field);
            }
        }
        for (CustomizedSetter setter : setters) {
            for (Binding param : setter.parameters) {
                appendVarDef(lines, param);
            }
        }
        append(lines, "com.jsoniter.CodegenAccess.resetExistingObject(iter);");
        append(lines, "switch (com.jsoniter.CodegenAccess.readObjectFieldAsHash(iter)) {");
        HashSet<Integer> knownHashes = new HashSet<Integer>();
        for (Binding field : allBindings) {
            for (String fromName : field.fromNames) {
                long hash = 0x811c9dc5;
                for (byte b : fromName.getBytes()) {
                    hash ^= b;
                    hash *= 0x1000193;
                }
                int intHash = (int) hash;
                if (intHash == 0) {
                    // hash collision, 0 can not be used as sentinel
                    return genObjectUsingSlice(clazz, cacheKey, ctor, setters, fields);
                }
                if (knownHashes.contains(intHash)) {
                    // hash collision with other field can not be used as sentinel
                    return genObjectUsingSlice(clazz, cacheKey, ctor, setters, fields);
                }
                knownHashes.add(intHash);
                append(lines, "case " + intHash + ": ");
                appendFieldSet(lines, cacheKey, ctor, fields, field);
                append(lines, "break;");
            }
        }
        append(lines, "default:");
        append(lines, "iter.skip();");
        append(lines, "}");
        append(lines, "while (com.jsoniter.CodegenAccess.nextToken(iter) == ',') {");
        append(lines, "switch (com.jsoniter.CodegenAccess.readObjectFieldAsHash(iter)) {");
        for (Binding field : allBindings) {
            for (String fromName : field.fromNames) {
                long hash = 0x811c9dc5;
                for (byte b : fromName.getBytes()) {
                    hash ^= b;
                    hash *= 0x1000193;
                }
                int intHash = (int) hash;
                append(lines, "case " + intHash + ": ");
                appendFieldSet(lines, cacheKey, ctor, fields, field);
                append(lines, "continue;");
            }
        }
        append(lines, "}");
        append(lines, "iter.skip();");
        append(lines, "}");
        if (!ctor.parameters.isEmpty()) {
            append(lines, CodegenImplNative.getTypeName(clazz) + " obj = {{newInst}};");
            for (Binding field : fields) {
                append(lines, String.format("obj.%s = _%s_;", field.name, field.name));
            }
        }
        appendSetter(setters, lines);
        append(lines, "return obj;");
        append(lines, "}");
        return lines.toString()
                .replace("{{clazz}}", clazz.getCanonicalName())
                .replace("{{newInst}}", genNewInstCode(clazz, ctor));
    }

    private static void appendFieldSet(StringBuilder lines, String cacheKey, CustomizedConstructor ctor, List<Binding> fields, Binding field) {
        if (ctor.parameters.isEmpty() && fields.contains(field)) {
            if (!shouldReuseObject(field.valueType)) {
                append(lines, String.format("com.jsoniter.CodegenAccess.setExistingObject(iter, obj.%s);", field.name));
            }
            append(lines, String.format("obj.%s = %s;", field.name, CodegenImplNative.genField(field, cacheKey)));
        } else {
            append(lines, String.format("_%s_ = %s;", field.name, CodegenImplNative.genField(field, cacheKey)));
        }
    }

    private static void appendSetter(List<CustomizedSetter> setters, StringBuilder lines) {
        for (CustomizedSetter setter : setters) {
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

    private static String genObjectUsingSkip(Class clazz, CustomizedConstructor ctor) {
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

    private static String genNewInstCode(Class clazz, CustomizedConstructor ctor) {
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
            code.append(String.format(" : (%s)com.jsoniter.CodegenAccess.existingObject(iter))", clazz.getCanonicalName()));
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
}
