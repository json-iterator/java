package com.jsoniter.output;

import com.jsoniter.spi.EmptyEncoder;
import com.jsoniter.spi.Encoder;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;

class DynamicCodegen {

    static ClassPool pool = ClassPool.getDefault();

    public static Encoder gen(Class clazz, String cacheKey, String source) throws Exception {
        CtClass ctClass = pool.makeClass(cacheKey);
        ctClass.setInterfaces(new CtClass[]{pool.get(Encoder.class.getName())});
        ctClass.setSuperclass(pool.get(EmptyEncoder.class.getName()));
        CtMethod staticMethod = CtNewMethod.make(source, ctClass);
        ctClass.addMethod(staticMethod);
        CtMethod interfaceMethod = CtNewMethod.make("" +
                "public void encode(Object set, com.jsoniter.output.JsonStream stream) throws java.io.IOException {" +
                String.format("return encode_((%s)set, stream);", clazz.getCanonicalName()) +
                "}", ctClass);
        ctClass.addMethod(interfaceMethod);
        return (Encoder) ctClass.toClass().newInstance();
    }
}
