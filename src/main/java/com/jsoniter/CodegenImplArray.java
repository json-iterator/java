package com.jsoniter;

import com.jsoniter.spi.ClassInfo;

import java.lang.reflect.Type;
import java.util.*;

class CodegenImplArray {

    final static Set<Class> WITH_CAPACITY_COLLECTION_CLASSES = new HashSet<Class>() {{
        add(ArrayList.class);
        add(HashSet.class);
        add(Vector.class);
    }};

    public static String genArray(ClassInfo classInfo) {
        Class compType = classInfo.clazz.getComponentType();
        if (compType.isArray()) {
            throw new IllegalArgumentException("nested array not supported: " + classInfo.clazz.getCanonicalName());
        }
        StringBuilder lines = new StringBuilder();
        append(lines, "com.jsoniter.CodegenAccess.resetExistingObject(iter);");
        append(lines, "byte nextToken = com.jsoniter.CodegenAccess.readByte(iter);");
        append(lines, "if (nextToken != '[') {");
        append(lines, "if (nextToken == 'n') {");
        append(lines, "com.jsoniter.CodegenAccess.skipFixedBytes(iter, 3);");
        append(lines, "com.jsoniter.CodegenAccess.resetExistingObject(iter); return null;");
        append(lines, "} else {");
        append(lines, "nextToken = com.jsoniter.CodegenAccess.nextToken(iter);");
        append(lines, "if (nextToken == 'n') {");
        append(lines, "com.jsoniter.CodegenAccess.skipFixedBytes(iter, 3);");
        append(lines, "com.jsoniter.CodegenAccess.resetExistingObject(iter); return null;");
        append(lines, "}");
        append(lines, "}");
        append(lines, "}");
        append(lines, "nextToken = com.jsoniter.CodegenAccess.nextToken(iter);");
        append(lines, "if (nextToken == ']') {");
        append(lines, "return new {{comp}}[0];");
        append(lines, "}");
        append(lines, "com.jsoniter.CodegenAccess.unreadByte(iter);");
        append(lines, "{{comp}} a1 = {{op}};");
        append(lines, "if (!com.jsoniter.CodegenAccess.nextTokenIsComma(iter)) {");
        append(lines, "return new {{comp}}[]{ a1 };");
        append(lines, "}");
        append(lines, "{{comp}} a2 = {{op}};");
        append(lines, "if (!com.jsoniter.CodegenAccess.nextTokenIsComma(iter)) {");
        append(lines, "return new {{comp}}[]{ a1, a2 };");
        append(lines, "}");
        append(lines, "{{comp}} a3 = {{op}};");
        append(lines, "if (!com.jsoniter.CodegenAccess.nextTokenIsComma(iter)) {");
        append(lines, "return new {{comp}}[]{ a1, a2, a3 };");
        append(lines, "}");
        append(lines, "{{comp}} a4 = ({{comp}}) {{op}};");
        append(lines, "if (!com.jsoniter.CodegenAccess.nextTokenIsComma(iter)) {");
        append(lines, "return new {{comp}}[]{ a1, a2, a3, a4 };");
        append(lines, "}");
        append(lines, "{{comp}} a5 = ({{comp}}) {{op}};");
        append(lines, "{{comp}}[] arr = new {{comp}}[10];");
        append(lines, "arr[0] = a1;");
        append(lines, "arr[1] = a2;");
        append(lines, "arr[2] = a3;");
        append(lines, "arr[3] = a4;");
        append(lines, "arr[4] = a5;");
        append(lines, "int i = 5;");
        append(lines, "while (com.jsoniter.CodegenAccess.nextTokenIsComma(iter)) {");
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

    public static String genCollection(ClassInfo classInfo) {
        if (WITH_CAPACITY_COLLECTION_CLASSES.contains(classInfo.clazz)) {
            return CodegenImplArray.genCollectionWithCapacity(classInfo.clazz, classInfo.typeArgs[0]);
        } else {
            return CodegenImplArray.genCollectionWithoutCapacity(classInfo.clazz, classInfo.typeArgs[0]);
        }
    }

    private static String genCollectionWithCapacity(Class clazz, Type compType) {
        StringBuilder lines = new StringBuilder();
        append(lines, "{{clazz}} col = ({{clazz}})com.jsoniter.CodegenAccess.resetExistingObject(iter);");
        append(lines, "if (iter.readNull()) { com.jsoniter.CodegenAccess.resetExistingObject(iter); return null; }");
        append(lines, "if (!com.jsoniter.CodegenAccess.readArrayStart(iter)) {");
        append(lines, "return col == null ? new {{clazz}}(0): ({{clazz}})com.jsoniter.CodegenAccess.reuseCollection(col);");
        append(lines, "}");
        append(lines, "Object a1 = {{op}};");
        append(lines, "if (com.jsoniter.CodegenAccess.nextToken(iter) != ',') {");
        append(lines, "{{clazz}} obj = col == null ? new {{clazz}}(1): ({{clazz}})com.jsoniter.CodegenAccess.reuseCollection(col);");
        append(lines, "obj.add(a1);");
        append(lines, "return obj;");
        append(lines, "}");
        append(lines, "Object a2 = {{op}};");
        append(lines, "if (com.jsoniter.CodegenAccess.nextToken(iter) != ',') {");
        append(lines, "{{clazz}} obj = col == null ? new {{clazz}}(2): ({{clazz}})com.jsoniter.CodegenAccess.reuseCollection(col);");
        append(lines, "obj.add(a1);");
        append(lines, "obj.add(a2);");
        append(lines, "return obj;");
        append(lines, "}");
        append(lines, "Object a3 = {{op}};");
        append(lines, "if (com.jsoniter.CodegenAccess.nextToken(iter) != ',') {");
        append(lines, "{{clazz}} obj = col == null ? new {{clazz}}(3): ({{clazz}})com.jsoniter.CodegenAccess.reuseCollection(col);");
        append(lines, "obj.add(a1);");
        append(lines, "obj.add(a2);");
        append(lines, "obj.add(a3);");
        append(lines, "return obj;");
        append(lines, "}");
        append(lines, "Object a4 = {{op}};");
        append(lines, "{{clazz}} obj = col == null ? new {{clazz}}(8): ({{clazz}})com.jsoniter.CodegenAccess.reuseCollection(col);");
        append(lines, "obj.add(a1);");
        append(lines, "obj.add(a2);");
        append(lines, "obj.add(a3);");
        append(lines, "obj.add(a4);");
        append(lines, "while (com.jsoniter.CodegenAccess.nextToken(iter) == ',') {");
        append(lines, "obj.add({{op}});");
        append(lines, "}");
        append(lines, "return obj;");
        return lines.toString().replace(
                "{{clazz}}", clazz.getName()).replace(
                "{{op}}", CodegenImplNative.genReadOp(compType));
    }

    private static String genCollectionWithoutCapacity(Class clazz, Type compType) {
        StringBuilder lines = new StringBuilder();
        append(lines, "if (iter.readNull()) { com.jsoniter.CodegenAccess.resetExistingObject(iter); return null; }");
        append(lines, "{{clazz}} col = ({{clazz}})com.jsoniter.CodegenAccess.resetExistingObject(iter);");
        append(lines, "if (!com.jsoniter.CodegenAccess.readArrayStart(iter)) {");
        append(lines, "return col == null ? new {{clazz}}(): ({{clazz}})com.jsoniter.CodegenAccess.reuseCollection(col);");
        append(lines, "}");
        append(lines, "Object a1 = {{op}};");
        append(lines, "if (com.jsoniter.CodegenAccess.nextToken(iter) != ',') {");
        append(lines, "{{clazz}} obj = col == null ? new {{clazz}}(): ({{clazz}})com.jsoniter.CodegenAccess.reuseCollection(col);");
        append(lines, "obj.add(a1);");
        append(lines, "return obj;");
        append(lines, "}");
        append(lines, "Object a2 = {{op}};");
        append(lines, "if (com.jsoniter.CodegenAccess.nextToken(iter) != ',') {");
        append(lines, "{{clazz}} obj = col == null ? new {{clazz}}(): ({{clazz}})com.jsoniter.CodegenAccess.reuseCollection(col);");
        append(lines, "obj.add(a1);");
        append(lines, "obj.add(a2);");
        append(lines, "return obj;");
        append(lines, "}");
        append(lines, "Object a3 = {{op}};");
        append(lines, "if (com.jsoniter.CodegenAccess.nextToken(iter) != ',') {");
        append(lines, "{{clazz}} obj = col == null ? new {{clazz}}(): ({{clazz}})com.jsoniter.CodegenAccess.reuseCollection(col);");
        append(lines, "obj.add(a1);");
        append(lines, "obj.add(a2);");
        append(lines, "obj.add(a3);");
        append(lines, "return obj;");
        append(lines, "}");
        append(lines, "Object a4 = {{op}};");
        append(lines, "{{clazz}} obj = col == null ? new {{clazz}}(): ({{clazz}})com.jsoniter.CodegenAccess.reuseCollection(col);");
        append(lines, "obj.add(a1);");
        append(lines, "obj.add(a2);");
        append(lines, "obj.add(a3);");
        append(lines, "obj.add(a4);");
        append(lines, "while (com.jsoniter.CodegenAccess.nextToken(iter) == ',') {");
        append(lines, "obj.add({{op}});");
        append(lines, "}");
//        append(lines, "if (c != ']') { com.jsoniter.CodegenAccess.reportIncompleteArray(iter); }");
        append(lines, "return obj;");
        return lines.toString().replace(
                "{{clazz}}", clazz.getName()).replace(
                "{{op}}", CodegenImplNative.genReadOp(compType));
    }

    private static void append(StringBuilder lines, String str) {
        lines.append(str);
        lines.append("\n");
    }
}
