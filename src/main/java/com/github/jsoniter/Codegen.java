package com.github.jsoniter;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;

import java.util.HashMap;
import java.util.Map;

class Codegen {
    static Map<Class, Decoder> cache = new HashMap<>();
    static ClassPool pool = ClassPool.getDefault();
    static Decoder gen(Class clazz) {
        Decoder decoder = cache.get(clazz);
        if (decoder != null) {
            return decoder;
        }
        try {
            CtClass ctClass = pool.makeClass("codegen." + clazz.getName().replace("[", "array_"));
            ctClass.setInterfaces(new CtClass[]{pool.get(Decoder.class.getName())});
            StringBuilder lines = new StringBuilder();
            lines.append("public void decode(Object obj, com.github.jsoniter.Jsoniter iter) {");
            lines.append("{{comp}}[] arr = ({{comp}}[]) obj;".replace("{{comp}}", clazz.getComponentType().getName()));
            lines.append("int i = 0;");
            lines.append("while (iter.ReadArray()) {");
            lines.append("arr[i++] = ({{comp}}) iter.ReadUnsignedInt();".replace("{{comp}}", clazz.getComponentType().getName()));
            lines.append("}");
            lines.append("}");
            CtMethod method = CtNewMethod.make(lines.toString(), ctClass);
            ctClass.addMethod(method);
            decoder = (Decoder) ctClass.toClass().newInstance();
            cache.put(clazz, decoder);
            return decoder;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
