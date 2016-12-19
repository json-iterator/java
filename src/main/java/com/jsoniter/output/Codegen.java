package com.jsoniter.output;

import com.jsoniter.spi.Extension;
import com.jsoniter.spi.ExtensionManager;
import com.jsoniter.JsonException;
import com.jsoniter.spi.Encoder;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

class Codegen {

    static ClassPool pool = ClassPool.getDefault();

    public static Encoder getEncoder(String cacheKey, Type type) {
        Encoder encoder = ExtensionManager.getEncoder(cacheKey);
        if (encoder != null) {
            return encoder;
        }
        return gen(cacheKey, type);
    }

    private static synchronized Encoder gen(String cacheKey, Type type) {
        Encoder encoder = ExtensionManager.getEncoder(cacheKey);
        if (encoder != null) {
            return encoder;
        }
        encoder = CodegenImplNative.NATIVE_ENCODERS.get(type);
        if (encoder != null) {
            ExtensionManager.addNewEncoder(cacheKey, encoder);
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
            ExtensionManager.addNewEncoder(cacheKey, encoder);
            return encoder;
        } catch (Exception e) {
            System.err.println("failed to generate encoder for: " + type + " with " + Arrays.toString(typeArgs));
            System.err.println(source);
            ExtensionManager.dump();
            throw new JsonException(e);
        }
    }


    private static String genSource(String cacheKey, Class clazz, Type[] typeArgs) {
        if (clazz.isArray()) {
            return CodegenImplArray.genArray(clazz);
        }
        if (Map.class.isAssignableFrom(clazz)) {
            return CodegenImplMap.genMap(clazz, typeArgs);
        }
        if (Collection.class.isAssignableFrom(clazz)) {
            return CodegenImplArray.genCollection(clazz, typeArgs);
        }
        return CodegenImplObject.genObject(cacheKey, clazz);
    }
}
