package com.jsoniter;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

class Codegen {
    final static Map<String, String> NATIVE_READS = new HashMap<String, String>() {{
        put("float", "iter.readFloat()");
        put("double", "iter.readDouble()");
        put("boolean", "iter.readBoolean()");
        put("byte", "iter.readShort()");
        put("short", "iter.readShort()");
        put("int", "iter.readInt()");
        put("char", "iter.readInt()");
        put("long", "iter.readLong()");
        put(Float.class.getName(), "Float.valueOf(iter.readFloat())");
        put(Double.class.getName(), "Double.valueOf(iter.readDouble())");
        put(Boolean.class.getName(), "Boolean.valueOf(iter.readBoolean())");
        put(Byte.class.getName(), "Byte.valueOf(iter.readShort())");
        put(Short.class.getName(), "Short.valueOf(iter.readShort())");
        put(Integer.class.getName(), "Integer.valueOf(iter.readInt())");
        put(Long.class.getName(), "Long.valueOf(iter.readLong())");
        put(BigDecimal.class.getName(), "iter.readBigDecimal()");
        put(BigInteger.class.getName(), "iter.readBigInteger()");
        put(String.class.getName(), "iter.readString()");
        put(Object.class.getName(), "iter.readAnyObject()");
        put(Any.class.getName(), "iter.readAny()");
    }};
    final static Set<Class> WITH_CAPACITY_COLLECTION_CLASSES = new HashSet<Class>() {{
        add(ArrayList.class);
        add(HashSet.class);
        add(Vector.class);
    }};
    static volatile Map<String, Decoder> cache = new HashMap<String, Decoder>();
    static List<Extension> extensions = new ArrayList<Extension>();
    static ClassPool pool = ClassPool.getDefault();

    static Decoder getDecoder(String cacheKey, Type type, Type... typeArgs) {
        Decoder decoder = cache.get(cacheKey);
        if (decoder != null) {
            return decoder;
        }
        return gen(cacheKey, type, typeArgs);
    }

    private synchronized static Decoder gen(String cacheKey, Type type, Type[] typeArgs) {
        Decoder decoder = cache.get(cacheKey);
        if (decoder != null) {
            return decoder;
        }
        Class clazz;
        if (type instanceof ParameterizedType) {
            ParameterizedType pType = (ParameterizedType) type;
            clazz = (Class) pType.getRawType();
            typeArgs = pType.getActualTypeArguments();
        } else {
            clazz = (Class) type;
        }
        String source = genSource(cacheKey, clazz, typeArgs);
        if ("true".equals(System.getenv("JSONITER_DEBUG"))) {
            System.out.println(">>> " + cacheKey);
            System.out.println(source);
        }
        try {
            CtClass ctClass = pool.makeClass(cacheKey);
            ctClass.setInterfaces(new CtClass[]{pool.get(Decoder.class.getName())});
            CtMethod staticMethod = CtNewMethod.make(source, ctClass);
            ctClass.addMethod(staticMethod);
            CtMethod interfaceMethod = CtNewMethod.make("" +
                    "public Object decode(com.jsoniter.Jsoniter iter) {" +
                    "return decode_(iter);" +
                    "}", ctClass);
            ctClass.addMethod(interfaceMethod);
            decoder = (Decoder) ctClass.toClass().newInstance();
            addNewDecoder(cacheKey, decoder);
            return decoder;
        } catch (Exception e) {
            System.err.println("failed to generate encoder for: " + type + " with " + Arrays.toString(typeArgs));
            System.err.println(source);
            throw new RuntimeException(e);
        }
    }

    private static String genSource(String cacheKey, Class clazz, Type[] typeArgs) {
        if (NATIVE_READS.containsKey(clazz.getName())) {
            return genNative(clazz.getName());
        }
        if (clazz.isArray()) {
            return genArray(clazz);
        }
        if (Map.class.isAssignableFrom(clazz)) {
            Type keyType = String.class;
            Type valueType = Object.class;
            if (typeArgs.length == 0) {
                // default to Map<String, Object>
            } else if (typeArgs.length == 2) {
                keyType = typeArgs[0];
                valueType = typeArgs[1];
            } else {
                throw new IllegalArgumentException(
                        "can not bind to generic collection without argument types, " +
                                "try syntax like TypeLiteral<Map<String, String>>{}");
            }
            if (keyType != String.class) {
                throw new IllegalArgumentException("map key must be String");
            }
            if (clazz == Map.class) {
                clazz = HashMap.class;
            }
            return genMap(clazz, valueType);
        }
        if (Collection.class.isAssignableFrom(clazz)) {
            Type compType = Object.class;
            if (typeArgs.length == 0) {
                // default to List<Object>
            } else if (typeArgs.length == 1) {
                compType = typeArgs[0];
            } else {
                throw new IllegalArgumentException(
                        "can not bind to generic collection without argument types, " +
                                "try syntax like TypeLiteral<List<Integer>>{}");
            }
            if (clazz == List.class) {
                clazz = ArrayList.class;
            } else if (clazz == Set.class) {
                clazz = HashSet.class;
            }
            if (WITH_CAPACITY_COLLECTION_CLASSES.contains(clazz)) {
                return genCollectionWithCapacity(clazz, compType);
            } else {
                return genCollection(clazz, compType);
            }
        }
        if (clazz.getFields().length == 0) {
            StringBuilder lines = new StringBuilder();
            append(lines, "public static Object decode_(com.jsoniter.Jsoniter iter) {");
            append(lines, "if (iter.readNull()) { return null; }");
            append(lines, "{{clazz}} obj = {{newInst}};");
            append(lines, "iter.skip();");
            append(lines, "return obj;");
            append(lines, "}");
            return lines.toString().replace("{{clazz}}", clazz.getCanonicalName()).replace("{{newInst}}", genNewInstCode(clazz));
        }
        return genObjectUsingHash(clazz, cacheKey);
    }

    private static String genMap(Class clazz, Type valueType) {
        StringBuilder lines = new StringBuilder();
        append(lines, "public static Object decode_(com.jsoniter.Jsoniter iter) {");
        append(lines, "{{clazz}} map = new {{clazz}}();");
        append(lines, "for (String field = iter.readObject(); field != null; field = iter.readObject()) {");
        append(lines, "map.put(field, {{op}});");
        append(lines, "}");
        append(lines, "return map;");
        append(lines, "}");
        return lines.toString().replace("{{clazz}}", clazz.getName()).replace("{{op}}", genReadOp(valueType));
    }

    private static String genNative(String nativeReadKey) {
        StringBuilder lines = new StringBuilder();
        append(lines, "public static Object decode_(com.jsoniter.Jsoniter iter) {");
        append(lines, "return " + NATIVE_READS.get(nativeReadKey) + ";");
        append(lines, "}");
        return lines.toString();
    }

    public static void addNewDecoder(String cacheKey, Decoder decoder) {
        HashMap<String, Decoder> newCache = new HashMap<String, Decoder>(cache);
        newCache.put(cacheKey, decoder);
        cache = newCache;
    }

    private static String genObjectUsingHash(Class clazz, String cacheKey) {
        StringBuilder lines = new StringBuilder();
        append(lines, "public static Object decode_(com.jsoniter.Jsoniter iter) {");
        append(lines, "if (iter.readNull()) { return null; }");
        append(lines, "{{clazz}} obj = {{newInst}};");
        append(lines, "if (!com.jsoniter.CodegenAccess.readObjectStart(iter)) { return obj; }");
        append(lines, "switch (com.jsoniter.CodegenAccess.readObjectFieldAsHash(iter)) {");
        HashSet<Integer> knownHashes = new HashSet<Integer>();
        for (Field field : clazz.getFields()) {
            long hash = 0x811c9dc5;
            for (byte b : field.getName().getBytes()) {
                hash ^= b;
                hash *= 0x1000193;
            }
            int intHash = (int) hash;
            if (intHash == 0) {
                // hash collision, 0 can not be used as sentinel
                return genObjectUsingSlice(clazz, cacheKey);
            }
            if (knownHashes.contains(intHash)) {
                // hash collision with other field can not be used as sentinel
                return genObjectUsingSlice(clazz, cacheKey);
            }
            knownHashes.add(intHash);
            append(lines, "case " + intHash + ": ");
            genField(lines, field, cacheKey);
            append(lines, "break;");
        }
        append(lines, "default:");
        append(lines, "iter.skip();");
        append(lines, "}");
        append(lines, "while (com.jsoniter.CodegenAccess.nextToken(iter) == ',') {");
        append(lines, "switch (com.jsoniter.CodegenAccess.readObjectFieldAsHash(iter)) {");
        for (Field field : clazz.getFields()) {
            long hash = 0x811c9dc5;
            for (byte b : field.getName().getBytes()) {
                hash ^= b;
                hash *= 0x1000193;
            }
            int intHash = (int) hash;
            append(lines, "case " + intHash + ": ");
            genField(lines, field, cacheKey);
            append(lines, "continue;");
        }
        append(lines, "}");
        append(lines, "iter.skip();");
        append(lines, "}");
//        append(lines, "if (c != '}') { com.jsoniter.CodegenAccess.reportIncompleteObject(iter); }");
        append(lines, "return obj;");
        append(lines, "}");
        return lines.toString()
                .replace("{{clazz}}", clazz.getCanonicalName())
                .replace("{{newInst}}", genNewInstCode(clazz));
    }

    private static String genNewInstCode(Class clazz) {
        String newInstanceCode = null;
        for (Extension extension : extensions) {
            newInstanceCode = extension.codegenNewInstance(clazz);
            if (newInstanceCode != null) {
                break;
            }
        }
        if (newInstanceCode == null) {
            newInstanceCode = "new " + clazz.getCanonicalName() + "()";
        }
        return newInstanceCode;
    }

    private static String genObjectUsingSlice(Class clazz, String cacheKey) {
        Map<Integer, Object> trieTree = buildTriTree(clazz);
        StringBuilder lines = new StringBuilder();
        append(lines, "public static Object decode_(com.jsoniter.Jsoniter iter) {");
        append(lines, "if (iter.readNull()) { return null; }");
        append(lines, "{{clazz}} obj = {{newInst}};");
        append(lines, "if (!com.jsoniter.CodegenAccess.readObjectStart(iter)) { return obj; }");
        append(lines, "com.jsoniter.Slice field = com.jsoniter.CodegenAccess.readObjectFieldAsSlice(iter);");
        append(lines, "boolean once = true;");
        append(lines, "while (once) {");
        append(lines, "once = false;");
        append(lines, "switch (field.len) {");
        for (Map.Entry<Integer, Object> entry : trieTree.entrySet()) {
            Integer len = entry.getKey();
            append(lines, "case " + len + ": ");
            Map<Byte, Object> current = (Map<Byte, Object>) entry.getValue();
            addFieldDispatch(lines, len, 0, current, cacheKey, new ArrayList<Byte>());
            append(lines, "break;");
        }
        append(lines, "}"); // end of switch
        append(lines, "iter.skip();");
        append(lines, "}"); // end of while
        append(lines, "while (com.jsoniter.CodegenAccess.nextToken(iter) == ',') {");
        append(lines, "field = com.jsoniter.CodegenAccess.readObjectFieldAsSlice(iter);");
        append(lines, "switch (field.len) {");
        for (Map.Entry<Integer, Object> entry : trieTree.entrySet()) {
            Integer len = entry.getKey();
            append(lines, "case " + len + ": ");
            Map<Byte, Object> current = (Map<Byte, Object>) entry.getValue();
            addFieldDispatch(lines, len, 0, current, cacheKey, new ArrayList<Byte>());
            append(lines, "break;");
        }
        append(lines, "}"); // end of switch
        append(lines, "iter.skip();");
        append(lines, "}"); // end of while
//        append(lines, "if (c != '}') { com.jsoniter.CodegenAccess.reportIncompleteObject(iter); }");
        append(lines, "return obj;");
        append(lines, "}");
        return lines.toString()
                .replace("{{clazz}}", clazz.getCanonicalName())
                .replace("{{newInst}}", genNewInstCode(clazz));
    }

    private static Map<Integer, Object> buildTriTree(Class clazz) {
        Map<Integer, Object> trieTree = new HashMap<Integer, Object>();
        for (Field field : clazz.getFields()) {
            String[] alternativeFieldNames = null;
            for (Extension extension : extensions) {
                alternativeFieldNames = extension.getAlternativeFieldNames(field);
                if (alternativeFieldNames != null) {
                    break;
                }
            }
            if (alternativeFieldNames == null) {
                alternativeFieldNames = new String[]{field.getName()};
            }
            for (String alternativeFieldName : alternativeFieldNames) {
                byte[] fieldName = alternativeFieldName.getBytes();
                Map<Byte, Object> current = (Map<Byte, Object>) trieTree.get(fieldName.length);
                if (current == null) {
                    current = new HashMap<Byte, Object>();
                    trieTree.put(fieldName.length, current);
                }
                for (int i = 0; i < fieldName.length - 1; i++) {
                    byte b = fieldName[i];
                    Map<Byte, Object> next = (Map<Byte, Object>) current.get(b);
                    if (next == null) {
                        next = new HashMap<Byte, Object>();
                        current.put(b, next);
                    }
                    current = next;
                }
                current.put(fieldName[fieldName.length - 1], field);
            }
        }
        return trieTree;
    }

    private static Decoder createFieldDecoder(String fieldCacheKey, Field field) {
        for (Extension extension : extensions) {
            Decoder decoder = extension.createDecoder(field);
            if (decoder != null) {
                addNewDecoder(fieldCacheKey, decoder);
                break;
            }
        }
        // the decoder can be just created by the factory
        // or it can be registered directly
        return cache.get(fieldCacheKey);
    }

    private static void addFieldDispatch(StringBuilder lines, int len, int i, Map<Byte, Object> current, String cacheKey, List<Byte> bytesToCompare) {
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
                genField(lines, (Field) entry.getValue(), cacheKey);
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

    private static void genField(StringBuilder lines, Field field, String cacheKey) {
        Class<?> fieldType = field.getType();
        String fieldTypeName = fieldType.getCanonicalName();
        String fieldCacheKey = field.getName() + "@" + cacheKey;
        Decoder decoder = createFieldDecoder(fieldCacheKey, field);
        if (decoder != null) {
            if (fieldType == boolean.class) {
                if (!(decoder instanceof Decoder.BooleanDecoder)) {
                    throw new RuntimeException("decoder for field " + field + "must implement Decoder.BooleanDecoder");
                }
                append(lines, String.format("obj.%s = com.jsoniter.CodegenAccess.readBoolean(\"%s\", iter);", field.getName(), fieldCacheKey));
                return;
            }
            if (fieldType == byte.class) {
                if (!(decoder instanceof Decoder.ShortDecoder)) {
                    throw new RuntimeException("decoder for field " + field + "must implement Decoder.ShortDecoder");
                }
                append(lines, String.format("obj.%s = com.jsoniter.CodegenAccess.readShort(\"%s\", iter);", field.getName(), fieldCacheKey));
                return;
            }
            if (fieldType == short.class) {
                if (!(decoder instanceof Decoder.ShortDecoder)) {
                    throw new RuntimeException("decoder for field " + field + "must implement Decoder.ShortDecoder");
                }
                append(lines, String.format("obj.%s = com.jsoniter.CodegenAccess.readShort(\"%s\", iter);", field.getName(), fieldCacheKey));
                return;
            }
            if (fieldType == char.class) {
                if (!(decoder instanceof Decoder.IntDecoder)) {
                    throw new RuntimeException("decoder for field " + field + "must implement Decoder.IntDecoder");
                }
                append(lines, String.format("obj.%s = com.jsoniter.CodegenAccess.readInt(\"%s\", iter);", field.getName(), fieldCacheKey));
                return;
            }
            if (fieldType == int.class) {
                if (!(decoder instanceof Decoder.IntDecoder)) {
                    throw new RuntimeException("decoder for field " + field + "must implement Decoder.IntDecoder");
                }
                append(lines, String.format("obj.%s = com.jsoniter.CodegenAccess.readInt(\"%s\", iter);", field.getName(), fieldCacheKey));
                return;
            }
            if (fieldType == long.class) {
                if (!(decoder instanceof Decoder.LongDecoder)) {
                    throw new RuntimeException("decoder for field " + field + "must implement Decoder.LongDecoder");
                }
                append(lines, String.format("obj.%s = com.jsoniter.CodegenAccess.readLong(\"%s\", iter);", field.getName(), fieldCacheKey));
                return;
            }
            if (fieldType == float.class) {
                if (!(decoder instanceof Decoder.FloatDecoder)) {
                    throw new RuntimeException("decoder for field " + field + "must implement Decoder.FloatDecoder");
                }
                append(lines, String.format("obj.%s = com.jsoniter.CodegenAccess.readFloat(\"%s\", iter);", field.getName(), fieldCacheKey));
                return;
            }
            if (fieldType == double.class) {
                if (!(decoder instanceof Decoder.DoubleDecoder)) {
                    throw new RuntimeException("decoder for field " + field + "must implement Decoder.DoubleDecoder");
                }
                append(lines, String.format("obj.%s = com.jsoniter.CodegenAccess.readDouble(\"%s\", iter);", field.getName(), fieldCacheKey));
                return;
            }
            getDecoder(fieldCacheKey, fieldType); // put decoder into cache
            append(lines, String.format("obj.%s = (%s)com.jsoniter.CodegenAccess.read(\"%s\", iter);",
                    field.getName(), fieldTypeName, fieldCacheKey));
            return;
        }
        append(lines, String.format("obj.%s = (%s)%s;", field.getName(), fieldTypeName, genReadOp(field.getGenericType())));
    }

    private static String genReadOp(Type type) {
        if (type instanceof Class) {
            Class clazz = (Class) type;
            String nativeRead = NATIVE_READS.get(clazz.getCanonicalName());
            if (nativeRead != null) {
                return nativeRead;
            }
        }
        String cacheKey = TypeLiteral.generateCacheKey(type);
        getDecoder(cacheKey, type); // set the decoder to cache
        return String.format("%s.decode_(iter)", cacheKey);
    }

    private static String genArray(Class clazz) {
        Class compType = clazz.getComponentType();
        if (compType.isArray()) {
            throw new IllegalArgumentException("nested array not supported: " + clazz.getCanonicalName());
        }
        StringBuilder lines = new StringBuilder();
        append(lines, "public static Object decode_(com.jsoniter.Jsoniter iter) {");
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
        append(lines, "}");
        return lines.toString().replace(
                "{{comp}}", compType.getCanonicalName()).replace(
                "{{op}}", genReadOp(compType));
    }

    private static String genCollectionWithCapacity(Class clazz, Type compType) {
        StringBuilder lines = new StringBuilder();
        append(lines, "public static Object decode_(com.jsoniter.Jsoniter iter) {");
        append(lines, "if (!com.jsoniter.CodegenAccess.readArrayStart(iter)) {");
        append(lines, "return new {{clazz}}(0);");
        append(lines, "}");
        append(lines, "Object a1 = {{op}};");
        append(lines, "if (com.jsoniter.CodegenAccess.nextToken(iter) != ',') {");
        append(lines, "{{clazz}} obj = new {{clazz}}(1);");
        append(lines, "obj.add(a1);");
        append(lines, "return obj;");
        append(lines, "}");
        append(lines, "Object a2 = {{op}};");
        append(lines, "if (com.jsoniter.CodegenAccess.nextToken(iter) != ',') {");
        append(lines, "{{clazz}} obj = new {{clazz}}(2);");
        append(lines, "obj.add(a1);");
        append(lines, "obj.add(a2);");
        append(lines, "return obj;");
        append(lines, "}");
        append(lines, "Object a3 = {{op}};");
        append(lines, "if (com.jsoniter.CodegenAccess.nextToken(iter) != ',') {");
        append(lines, "{{clazz}} obj = new {{clazz}}(3);");
        append(lines, "obj.add(a1);");
        append(lines, "obj.add(a2);");
        append(lines, "obj.add(a3);");
        append(lines, "return obj;");
        append(lines, "}");
        append(lines, "Object a4 = {{op}};");
        append(lines, "{{clazz}} obj = new {{clazz}}(110);");
        append(lines, "obj.add(a1);");
        append(lines, "obj.add(a2);");
        append(lines, "obj.add(a3);");
        append(lines, "obj.add(a4);");
        append(lines, "while (com.jsoniter.CodegenAccess.nextToken(iter) == ',') {");
        append(lines, "obj.add({{op}});");
        append(lines, "}");
//        append(lines, "if (c != ']') { com.jsoniter.CodegenAccess.reportIncompleteArray(iter); }");
        append(lines, "return obj;");
        append(lines, "}");
        return lines.toString().replace(
                "{{clazz}}", clazz.getName()).replace(
                "{{op}}", genReadOp(compType));
    }

    private static String genCollection(Class clazz, Type compType) {
        StringBuilder lines = new StringBuilder();
        append(lines, "public static Object decode_(com.jsoniter.Jsoniter iter) {");
        append(lines, "if (!com.jsoniter.CodegenAccess.readArrayStart(iter)) {");
        append(lines, "return new {{clazz}}();");
        append(lines, "}");
        append(lines, "Object a1 = {{op}};");
        append(lines, "if (com.jsoniter.CodegenAccess.nextToken(iter) != ',') {");
        append(lines, "{{clazz}} obj = new {{clazz}}();");
        append(lines, "obj.add(a1);");
        append(lines, "return obj;");
        append(lines, "}");
        append(lines, "Object a2 = {{op}};");
        append(lines, "if (com.jsoniter.CodegenAccess.nextToken(iter) != ',') {");
        append(lines, "{{clazz}} obj = new {{clazz}}();");
        append(lines, "obj.add(a1);");
        append(lines, "obj.add(a2);");
        append(lines, "return obj;");
        append(lines, "}");
        append(lines, "Object a3 = {{op}};");
        append(lines, "if (com.jsoniter.CodegenAccess.nextToken(iter) != ',') {");
        append(lines, "{{clazz}} obj = new {{clazz}}();");
        append(lines, "obj.add(a1);");
        append(lines, "obj.add(a2);");
        append(lines, "obj.add(a3);");
        append(lines, "return obj;");
        append(lines, "}");
        append(lines, "Object a4 = {{op}};");
        append(lines, "{{clazz}} obj = new {{clazz}}();");
        append(lines, "obj.add(a1);");
        append(lines, "obj.add(a2);");
        append(lines, "obj.add(a3);");
        append(lines, "obj.add(a4);");
        append(lines, "while (com.jsoniter.CodegenAccess.nextToken(iter) == ',') {");
        append(lines, "obj.add({{op}});");
        append(lines, "}");
//        append(lines, "if (c != ']') { com.jsoniter.CodegenAccess.reportIncompleteArray(iter); }");
        append(lines, "return obj;");
        append(lines, "}");
        return lines.toString().replace(
                "{{clazz}}", clazz.getName()).replace(
                "{{op}}", genReadOp(compType));
    }

    private static void append(StringBuilder lines, String str) {
        lines.append(str);
        lines.append("\n");
    }

    public static void registerExtension(Extension extension) {
        extensions.add(extension);
    }

    public static Decoder.IntDecoder getIntDecoder(String cacheKey) {
        return (Decoder.IntDecoder) cache.get(cacheKey);
    }

    public static Decoder.BooleanDecoder getBooleanDecoder(String cacheKey) {
        return (Decoder.BooleanDecoder) cache.get(cacheKey);
    }

    public static Decoder.ShortDecoder getShortDecoder(String cacheKey) {
        return (Decoder.ShortDecoder) cache.get(cacheKey);
    }

    public static Decoder.LongDecoder getLongDecoder(String cacheKey) {
        return (Decoder.LongDecoder) cache.get(cacheKey);
    }

    public static Decoder.FloatDecoder getFloatDecoder(String cacheKey) {
        return (Decoder.FloatDecoder) cache.get(cacheKey);
    }

    public static Decoder.DoubleDecoder getDoubleDecoder(String cacheKey) {
        return (Decoder.DoubleDecoder) cache.get(cacheKey);
    }
}
