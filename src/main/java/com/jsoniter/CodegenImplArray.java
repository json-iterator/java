package com.jsoniter;

import java.lang.reflect.Type;
import java.util.*;

class CodegenImplArray {

    final static Set<Class> WITH_CAPACITY_COLLECTION_CLASSES = new HashSet<Class>() {{
        add(ArrayList.class);
        add(HashSet.class);
        add(Vector.class);
    }};

    public static String genArray(Class clazz) {
        Class compType = clazz.getComponentType();
        if (compType.isArray()) {
            throw new IllegalArgumentException("nested array not supported: " + clazz.getCanonicalName());
        }
        StringBuilder lines = new StringBuilder();
        append(lines, "com.jsoniter.CodegenAccess.resetExistingObject(iter);");
        append(lines, "if (iter.readNull()) { return null; }");
        append(lines, "if (!com.jsoniter.CodegenAccess.readArrayStart(iter)) {");
        append(lines, "return new {{comp}}[0];");
        append(lines, "}");
        append(lines, "{{comp}} a1 = {{op}};");
        append(lines, "if (com.jsoniter.CodegenAccess.nextToken(iter) != ',') {");
        append(lines, "return new {{comp}}[]{ a1 };");
        append(lines, "}");
        append(lines, "{{comp}} a2 = {{op}};");
        append(lines, "if (com.jsoniter.CodegenAccess.nextToken(iter) != ',') {");
        append(lines, "return new {{comp}}[]{ a1, a2 };");
        append(lines, "}");
        append(lines, "{{comp}} a3 = {{op}};");
        append(lines, "if (com.jsoniter.CodegenAccess.nextToken(iter) != ',') {");
        append(lines, "return new {{comp}}[]{ a1, a2, a3 };");
        append(lines, "}");
        append(lines, "{{comp}} a4 = ({{comp}}) {{op}};");
        append(lines, "{{comp}}[] arr = new {{comp}}[8];");
        append(lines, "arr[0] = a1;");
        append(lines, "arr[1] = a2;");
        append(lines, "arr[2] = a3;");
        append(lines, "arr[3] = a4;");
        append(lines, "int i = 4;");
        append(lines, "while (com.jsoniter.CodegenAccess.nextToken(iter) == ',') {");
        append(lines, "if (i == arr.length) {");
        append(lines, "{{comp}}[] newArr = new {{comp}}[arr.length * 2];");
        append(lines, "System.arraycopy(arr, 0, newArr, 0, arr.length);");
        append(lines, "arr = newArr;");
        append(lines, "}");
        append(lines, "arr[i++] = {{op}};");
        append(lines, "}");
//        append(lines, "if (c != ']') { com.jsoniter.CodegenAccess.reportIncompleteArray(iter); }");
        append(lines, "{{comp}}[] result = new {{comp}}[i];");
        append(lines, "System.arraycopy(arr, 0, result, 0, i);");
        append(lines, "return result;");
        return lines.toString().replace(
                "{{comp}}", compType.getCanonicalName()).replace(
                "{{op}}", CodegenImplNative.genReadOp(compType));
    }

    public static String genCollection(Class clazz, Type[] typeArgs) {
        if (WITH_CAPACITY_COLLECTION_CLASSES.contains(clazz)) {
            return CodegenImplArray.genCollectionWithCapacity(clazz, typeArgs[0]);
        } else {
            return CodegenImplArray.genCollectionWithoutCapacity(clazz, typeArgs[0]);
        }
    }

    private static String genCollectionWithCapacity(Class clazz, Type compType) {
        StringBuilder lines = new StringBuilder();
        append(lines, "{{clazz}} col = ({{clazz}})com.jsoniter.CodegenAccess.resetExistingObject(iter);");
        append(lines, "if (iter.readNull()) { return null; }");
        append(lines, "if (!com.jsoniter.CodegenAccess.readArrayStart(iter)) {");
        append(lines, "return col == null ? new {{clazz}}(0): ({{clazz}})com.jsoniter.CodegenAccess.reuseCollection(col);");
        append(lines, "}");
        append(lines, "Object a1 = {{op}};");
        append(lines, "if (com.jsoniter.CodegenAccess.nextToken(iter) != ',') {");
        append(lines, "{{clazz}} set = col == null ? new {{clazz}}(1): ({{clazz}})com.jsoniter.CodegenAccess.reuseCollection(col);");
        append(lines, "set.add(a1);");
        append(lines, "return set;");
        append(lines, "}");
        append(lines, "Object a2 = {{op}};");
        append(lines, "if (com.jsoniter.CodegenAccess.nextToken(iter) != ',') {");
        append(lines, "{{clazz}} set = col == null ? new {{clazz}}(2): ({{clazz}})com.jsoniter.CodegenAccess.reuseCollection(col);");
        append(lines, "set.add(a1);");
        append(lines, "set.add(a2);");
        append(lines, "return set;");
        append(lines, "}");
        append(lines, "Object a3 = {{op}};");
        append(lines, "if (com.jsoniter.CodegenAccess.nextToken(iter) != ',') {");
        append(lines, "{{clazz}} set = col == null ? new {{clazz}}(3): ({{clazz}})com.jsoniter.CodegenAccess.reuseCollection(col);");
        append(lines, "set.add(a1);");
        append(lines, "set.add(a2);");
        append(lines, "set.add(a3);");
        append(lines, "return set;");
        append(lines, "}");
        append(lines, "Object a4 = {{op}};");
        append(lines, "{{clazz}} set = col == null ? new {{clazz}}(8): ({{clazz}})com.jsoniter.CodegenAccess.reuseCollection(col);");
        append(lines, "set.add(a1);");
        append(lines, "set.add(a2);");
        append(lines, "set.add(a3);");
        append(lines, "set.add(a4);");
        append(lines, "while (com.jsoniter.CodegenAccess.nextToken(iter) == ',') {");
        append(lines, "set.add({{op}});");
        append(lines, "}");
//        append(lines, "if (c != ']') { com.jsoniter.CodegenAccess.reportIncompleteArray(iter); }");
        append(lines, "return set;");
        return lines.toString().replace(
                "{{clazz}}", clazz.getName()).replace(
                "{{op}}", CodegenImplNative.genReadOp(compType));
    }

    private static String genCollectionWithoutCapacity(Class clazz, Type compType) {
        StringBuilder lines = new StringBuilder();
        append(lines, "if (iter.readNull()) { return null; }");
        append(lines, "{{clazz}} col = ({{clazz}})com.jsoniter.CodegenAccess.resetExistingObject(iter);");
        append(lines, "if (!com.jsoniter.CodegenAccess.readArrayStart(iter)) {");
        append(lines, "return col == null ? new {{clazz}}(): ({{clazz}})com.jsoniter.CodegenAccess.reuseCollection(col);");
        append(lines, "}");
        append(lines, "Object a1 = {{op}};");
        append(lines, "if (com.jsoniter.CodegenAccess.nextToken(iter) != ',') {");
        append(lines, "{{clazz}} set = col == null ? new {{clazz}}(): ({{clazz}})com.jsoniter.CodegenAccess.reuseCollection(col);");
        append(lines, "set.add(a1);");
        append(lines, "return set;");
        append(lines, "}");
        append(lines, "Object a2 = {{op}};");
        append(lines, "if (com.jsoniter.CodegenAccess.nextToken(iter) != ',') {");
        append(lines, "{{clazz}} set = col == null ? new {{clazz}}(): ({{clazz}})com.jsoniter.CodegenAccess.reuseCollection(col);");
        append(lines, "set.add(a1);");
        append(lines, "set.add(a2);");
        append(lines, "return set;");
        append(lines, "}");
        append(lines, "Object a3 = {{op}};");
        append(lines, "if (com.jsoniter.CodegenAccess.nextToken(iter) != ',') {");
        append(lines, "{{clazz}} set = col == null ? new {{clazz}}(): ({{clazz}})com.jsoniter.CodegenAccess.reuseCollection(col);");
        append(lines, "set.add(a1);");
        append(lines, "set.add(a2);");
        append(lines, "set.add(a3);");
        append(lines, "return set;");
        append(lines, "}");
        append(lines, "Object a4 = {{op}};");
        append(lines, "{{clazz}} set = col == null ? new {{clazz}}(): ({{clazz}})com.jsoniter.CodegenAccess.reuseCollection(col);");
        append(lines, "set.add(a1);");
        append(lines, "set.add(a2);");
        append(lines, "set.add(a3);");
        append(lines, "set.add(a4);");
        append(lines, "while (com.jsoniter.CodegenAccess.nextToken(iter) == ',') {");
        append(lines, "set.add({{op}});");
        append(lines, "}");
//        append(lines, "if (c != ']') { com.jsoniter.CodegenAccess.reportIncompleteArray(iter); }");
        append(lines, "return set;");
        return lines.toString().replace(
                "{{clazz}}", clazz.getName()).replace(
                "{{op}}", CodegenImplNative.genReadOp(compType));
    }

    private static void append(StringBuilder lines, String str) {
        lines.append(str);
        lines.append("\n");
    }
}
