package com.jsoniter.output;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

class Codegen {

    static volatile Map<String, Encoder> cache = new HashMap<String, Encoder>();
    static ClassPool pool = ClassPool.getDefault();

    public static Encoder getEncoder(String cacheKey, Type type) {
        Encoder encoder = cache.get(cacheKey);
        if (encoder != null) {
            return encoder;
        }
        return gen(cacheKey, type);
    }

    private static synchronized Encoder gen(String cacheKey, Type type) {
        Encoder encoder = cache.get(cacheKey);
        if (encoder != null) {
            return encoder;
        }
        encoder = CodegenImplNative.NATIVE_ENCODERS.get(type);
        if (encoder != null) {
            addNewEncoder(cacheKey, encoder);
            return encoder;
        }
        Type[] typeArgs = new Type[0];
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
            ctClass.setInterfaces(new CtClass[]{pool.get(Encoder.class.getName())});
            CtMethod staticMethod = CtNewMethod.make(source, ctClass);
            ctClass.addMethod(staticMethod);
            CtMethod interfaceMethod = CtNewMethod.make("" +
                    "public void encode(Object obj, com.jsoniter.output.JsonStream stream) {" +
                    "return encode_(obj, stream);" +
                    "}", ctClass);
            ctClass.addMethod(interfaceMethod);
            encoder = (Encoder) ctClass.toClass().newInstance();
            addNewEncoder(cacheKey, encoder);
            return encoder;
        } catch (Exception e) {
            System.err.println("failed to generate encoder for: " + type + " with " + Arrays.toString(typeArgs));
            System.err.println(source);
            throw new RuntimeException(e);
        }
    }

    public static void addNewEncoder(String cacheKey, Encoder encoder) {
        HashMap<String, Encoder> newCache = new HashMap<String, Encoder>(cache);
        newCache.put(cacheKey, encoder);
        cache = newCache;
    }

    private static String genSource(String cacheKey, Class clazz, Type[] typeArgs) {
        if (clazz.isArray()) {
            return CodegenImplArray.genArray(clazz);
        }
        if (Collection.class.isAssignableFrom(clazz)) {
            return CodegenImplArray.genCollection(clazz, typeArgs);
        }
        return CodegenImplObject.genObject(clazz);
    }
}
