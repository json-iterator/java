package com.jsoniter;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;

import java.lang.reflect.*;
import java.util.*;

class Codegen {
    static boolean strictMode = false;
    static volatile Map<String, Decoder> cache = new HashMap<String, Decoder>();
    static ClassPool pool = ClassPool.getDefault();

    public static void enableStrictMode() {
        strictMode = true;
    }

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
        for (Extension extension : ExtensionManager.extensions) {
            decoder = extension.createDecoder(type, typeArgs);
            if (decoder != null) {
                addNewDecoder(cacheKey, decoder);
                return decoder;
            }
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
                    "public Object decode(com.jsoniter.JsonIterator iter) {" +
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
        if (CodegenImplNative.NATIVE_READS.containsKey(clazz.getName())) {
            return CodegenImplNative.genNative(clazz.getName());
        }
        if (clazz.isArray()) {
            return CodegenImplArray.genArray(clazz);
        }
        if (Map.class.isAssignableFrom(clazz)) {
            return genMap(clazz, typeArgs);
        }
        if (Collection.class.isAssignableFrom(clazz)) {
            return CodegenImplArray.genCollection(clazz, typeArgs);
        }
        CustomizedConstructor ctor = ExtensionManager.getCtor(clazz);
        List<CustomizedSetter> setters = ExtensionManager.getSetters(clazz);
        List<Binding> fields = ExtensionManager.getFields(clazz);
        if (strictMode) {
            return CodegenImplObject.genObjectUsingSlice(clazz, cacheKey, ctor, setters, fields);
        } else {
            return CodegenImplObject.genObjectUsingHash(clazz, cacheKey, ctor, setters, fields);
        }
    }

    private static String genMap(Class clazz, Type[] typeArgs) {
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
        StringBuilder lines = new StringBuilder();
        append(lines, "public static Object decode_(com.jsoniter.JsonIterator iter) {");
        append(lines, "{{clazz}} map = ({{clazz}})com.jsoniter.CodegenAccess.resetExistingObject(iter);");
        append(lines, "if (map == null) { map = new {{clazz}}(); }");
        append(lines, "for (String field = iter.readObject(); field != null; field = iter.readObject()) {");
        append(lines, "map.put(field, {{op}});");
        append(lines, "}");
        append(lines, "return map;");
        append(lines, "}");
        return lines.toString().replace("{{clazz}}", clazz.getName()).replace("{{op}}", CodegenImplNative.genReadOp(valueType));
    }

    public static void addNewDecoder(String cacheKey, Decoder decoder) {
        HashMap<String, Decoder> newCache = new HashMap<String, Decoder>(cache);
        newCache.put(cacheKey, decoder);
        cache = newCache;
    }

    private static void append(StringBuilder lines, String str) {
        lines.append(str);
        lines.append("\n");
    }

}
