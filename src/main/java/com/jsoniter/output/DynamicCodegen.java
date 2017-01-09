package com.jsoniter.output;

import com.jsoniter.spi.EmptyEncoder;
import com.jsoniter.spi.Encoder;
import com.sun.org.apache.xml.internal.utils.StringBufferPool;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;

class DynamicCodegen {

    static ClassPool pool = ClassPool.getDefault();

    public static Encoder gen(Class clazz, String cacheKey, CodegenResult source) throws Exception {
        source.flushBuffer();
        CtClass ctClass = pool.makeClass(cacheKey);
        ctClass.setInterfaces(new CtClass[]{pool.get(Encoder.class.getName())});
        ctClass.setSuperclass(pool.get(EmptyEncoder.class.getName()));
        CtMethod staticMethod = CtNewMethod.make(source.toString(), ctClass);
        ctClass.addMethod(staticMethod);
        StringBuilder lines = new StringBuilder();
        append(lines, "public void encode(Object obj, com.jsoniter.output.JsonStream stream) throws java.io.IOException {");
        append(lines, "if (obj == null) { stream.writeNull(); return; }");
        if (source.prelude != null) {
            append(lines, CodegenResult.bufferToWriteOp(source.prelude));
        }
        append(lines, String.format("encode_((%s)obj, stream);", clazz.getCanonicalName()));
        if (source.epilogue != null) {
            append(lines, CodegenResult.bufferToWriteOp(source.epilogue));
        }
        append(lines, "}");
        CtMethod interfaceMethod = CtNewMethod.make(lines.toString(), ctClass);
        ctClass.addMethod(interfaceMethod);
        return (Encoder) ctClass.toClass().newInstance();
    }

    private static void append(StringBuilder lines, String line) {
        lines.append(line);
        lines.append('\n');
    }
}
