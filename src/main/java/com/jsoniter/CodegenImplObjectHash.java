package com.jsoniter;

import com.jsoniter.spi.*;

import java.util.*;

public class CodegenImplObjectHash {

    // the implementation is from dsljson, it is the fastest although has the risk not matching field strictly
    public static String genObjectUsingHash(Class clazz, ClassDescriptor desc) {
        StringBuilder lines = new StringBuilder();
        // === if null, return null
        append(lines, "java.lang.Object existingObj = com.jsoniter.CodegenAccess.resetExistingObject(iter);");
        append(lines, "byte nextToken = com.jsoniter.CodegenAccess.readByte(iter);");
        append(lines, "if (nextToken != '{') {");
        append(lines, "if (nextToken == 'n') {");
        append(lines, "com.jsoniter.CodegenAccess.skipFixedBytes(iter, 3);");
        append(lines, "return null;");
        append(lines, "} else {");
        append(lines, "nextToken = com.jsoniter.CodegenAccess.nextToken(iter);");
        append(lines, "if (nextToken == 'n') {");
        append(lines, "com.jsoniter.CodegenAccess.skipFixedBytes(iter, 3);");
        append(lines, "return null;");
        append(lines, "}");
        append(lines, "} // end of if null");
        append(lines, "} // end of if {");
        // === if empty, return empty
        // ctor requires binding
        for (Binding parameter : desc.ctor.parameters) {
            appendVarDef(lines, parameter);
        }
        append(lines, "nextToken = com.jsoniter.CodegenAccess.readByte(iter);");
        append(lines, "if (nextToken != '\"') {");
        append(lines, "if (nextToken == '}') {");
        append(lines, "return {{newInst}};");
        append(lines, "} else {");
        append(lines, "nextToken = com.jsoniter.CodegenAccess.nextToken(iter);");
        append(lines, "if (nextToken == '}') {");
        append(lines, "return {{newInst}};");
        append(lines, "} else {");
        append(lines, "com.jsoniter.CodegenAccess.unreadByte(iter);");
        append(lines, "}");
        append(lines, "} // end of if end");
        append(lines, "} else { com.jsoniter.CodegenAccess.unreadByte(iter); }// end of if not quote");
        for (Binding field : desc.fields) {
            appendVarDef(lines, field);
        }
        for (Binding setter : desc.setters) {
            appendVarDef(lines, setter);
        }
        for (WrapperDescriptor setter : desc.bindingTypeWrappers) {
            for (Binding param : setter.parameters) {
                appendVarDef(lines, param);
            }
        }
        // === bind fields
        HashSet<Integer> knownHashes = new HashSet<Integer>();
        HashMap<String, Binding> bindings = new HashMap<String, Binding>();
        for (Binding binding : desc.allDecoderBindings()) {
            for (String fromName : binding.fromNames) {
                bindings.put(fromName, binding);
            }
        }
        ArrayList<String> fromNames = new ArrayList<String>(bindings.keySet());
        Collections.sort(fromNames, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                int x = calcHash(o1);
                int y = calcHash(o2);
                return (x < y) ? -1 : ((x == y) ? 0 : 1);
            }
        });
        // === bind more fields
        append(lines, "do {");
        append(lines, "switch (com.jsoniter.CodegenAccess.readObjectFieldAsHash(iter)) {");
        for (String fromName : fromNames) {
            int intHash = calcHash(fromName);
            if (intHash == 0) {
                // hash collision, 0 can not be used as sentinel
                return CodegenImplObjectStrict.genObjectUsingStrict(clazz, desc);
            }
            if (knownHashes.contains(intHash)) {
                // hash collision with other field can not be used as sentinel
                return CodegenImplObjectStrict.genObjectUsingStrict(clazz, desc);
            }
            knownHashes.add(intHash);
            append(lines, "case " + intHash + ": ");
            appendBindingSet(lines, desc, bindings.get(fromName));
            append(lines, "continue;");
        }
        append(lines, "}");
        append(lines, "iter.skip();");
        append(lines, "} while (com.jsoniter.CodegenAccess.nextTokenIsComma(iter));");
        append(lines, CodegenImplNative.getTypeName(clazz) + " obj = {{newInst}};");
        for (Binding field : desc.fields) {
            append(lines, String.format("obj.%s = _%s_;", field.field.getName(), field.name));
        }
        for (Binding setter : desc.setters) {
            append(lines, String.format("obj.%s(_%s_);", setter.method.getName(), setter.name));
        }
        appendWrappers(desc.bindingTypeWrappers, lines);
        append(lines, "return obj;");
        return lines.toString()
                .replace("{{clazz}}", clazz.getCanonicalName())
                .replace("{{newInst}}", genNewInstCode(clazz, desc.ctor));
    }

    public static int calcHash(String fromName) {
        long hash = 0x811c9dc5;
        for (byte b : fromName.getBytes()) {
            hash ^= b;
            hash *= 0x1000193;
        }
        return (int) hash;
    }

    private static void appendBindingSet(StringBuilder lines, ClassDescriptor desc, Binding binding) {
        append(lines, String.format("_%s_ = %s;", binding.name, CodegenImplNative.genField(binding)));
    }

    static void appendWrappers(List<WrapperDescriptor> wrappers, StringBuilder lines) {
        for (WrapperDescriptor wrapper : wrappers) {
            lines.append("obj.");
            lines.append(wrapper.method.getName());
            appendInvocation(lines, wrapper.parameters);
            lines.append(";\n");
        }
    }

    static void appendVarDef(StringBuilder lines, Binding parameter) {
        String typeName = CodegenImplNative.getTypeName(parameter.valueType);
        append(lines, String.format("%s _%s_ = %s;", typeName, parameter.name, CodegenImplObjectStrict.DEFAULT_VALUES.get(typeName)));
    }

    static String genNewInstCode(Class clazz, ConstructorDescriptor ctor) {
        StringBuilder code = new StringBuilder();
        if (ctor.parameters.isEmpty()) {
            // nothing to bind, safe to reuse existing object
            code.append("(existingObj == null ? ");
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
            code.append(String.format(" : (%s)existingObj)", clazz.getCanonicalName()));
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

    static void append(StringBuilder lines, String str) {
        lines.append(str);
        lines.append("\n");
    }

}
