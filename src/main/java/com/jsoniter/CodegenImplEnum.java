package com.jsoniter;

import java.util.*;

public class CodegenImplEnum {
    public static String genEnum(Class clazz) {
        StringBuilder lines = new StringBuilder();
        append(lines, "if (iter.readNull()) { return null; }");
        append(lines, "com.jsoniter.Slice field = com.jsoniter.CodegenAccess.readSlice(iter);");
        append(lines, "switch (field.len()) {");
        append(lines, renderTriTree(buildTriTree(Arrays.asList(clazz.getEnumConstants()))));
        append(lines, "}"); // end of switch
        append(lines, String.format("throw iter.reportError(\"decode enum\", field + \" is not valid enum for %s\");", clazz.getName()));
        return lines.toString();
    }

    private static Map<Integer, Object> buildTriTree(List<Object> allConsts) {
        Map<Integer, Object> trieTree = new HashMap<Integer, Object>();
        for (Object e : allConsts) {
                byte[] fromNameBytes = e.toString().getBytes();
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
                current.put(fromNameBytes[fromNameBytes.length - 1], e);
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
                Object e = entry.getValue();
                append(lines, String.format("return %s.%s;", e.getClass().getName(), e.toString()));
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

    private static void append(StringBuilder lines, String str) {
        lines.append(str);
        lines.append("\n");
    }
}
