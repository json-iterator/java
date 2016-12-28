package com.jsoniter.output;

import com.jsoniter.DecodingMode;
import com.jsoniter.JsonException;
import com.jsoniter.ReflectionDecoderFactory;
import com.jsoniter.spi.Decoder;
import com.jsoniter.spi.Encoder;
import com.jsoniter.spi.JsoniterSpi;
import com.jsoniter.spi.TypeLiteral;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

class Codegen {

    private static EncodingMode mode = EncodingMode.DYNAMIC_MODE;
    static boolean isDoingStaticCodegen;
    // only read/write when generating code with synchronized protection
    private final static Set<String> generatedClassNames = new HashSet<String>();

    static {
        String envMode = System.getenv("JSONITER_ENCODING_MODE");
        if (envMode != null) {
            mode = EncodingMode.valueOf(envMode);
        }
    }

    public static void setMode(EncodingMode mode) {
        Codegen.mode = mode;
    }

    public static Encoder getEncoder(String cacheKey, Type type) {
        Encoder encoder = JsoniterSpi.getEncoder(cacheKey);
        if (encoder != null) {
            return encoder;
        }
        return gen(cacheKey, type);
    }

    private static synchronized Encoder gen(String cacheKey, Type type) {
        Encoder encoder = JsoniterSpi.getEncoder(cacheKey);
        if (encoder != null) {
            return encoder;
        }
        encoder = CodegenImplNative.NATIVE_ENCODERS.get(type);
        if (encoder != null) {
            JsoniterSpi.addNewEncoder(cacheKey, encoder);
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
        if (mode == EncodingMode.REFLECTION_MODE) {
            throw new RuntimeException("not implemented yet");
        }
        try {
            encoder = (Encoder) Class.forName(cacheKey).newInstance();
            JsoniterSpi.addNewEncoder(cacheKey, encoder);
            return encoder;
        } catch (Exception e) {
            if (mode == EncodingMode.STATIC_MODE) {
                throw new JsonException("static gen should provide the encoder we need, but failed to create the encoder", e);
            }
        }
        String source = genSource(clazz, typeArgs);
        if ("true".equals(System.getenv("JSONITER_DEBUG"))) {
            System.out.println(">>> " + cacheKey);
            System.out.println(source);
        }
        try {
            generatedClassNames.add(cacheKey);
            if (isDoingStaticCodegen) {
                staticGen(clazz, cacheKey, source);
            } else {
                encoder = DynamicCodegen.gen(clazz, cacheKey, source);
            }
            JsoniterSpi.addNewEncoder(cacheKey, encoder);
            return encoder;
        } catch (Exception e) {
            System.err.println("failed to generate encoder for: " + type + " with " + Arrays.toString(typeArgs));
            System.err.println(source);
            JsoniterSpi.dump();
            throw new JsonException(e);
        }
    }

    public static boolean canStaticAccess(String cacheKey) {
        return generatedClassNames.contains(cacheKey);
    }

    private static void staticGen(Class clazz, String cacheKey, String source) throws IOException {
        createDir(cacheKey);
        String fileName = cacheKey.replace('.', '/') + ".java";
        FileOutputStream fileOutputStream = new FileOutputStream(fileName);
        try {
            OutputStreamWriter writer = new OutputStreamWriter(fileOutputStream);
            try {
                staticGen(clazz, cacheKey, writer, source);
            } finally {
                writer.close();
            }
        } finally {
            fileOutputStream.close();
        }
    }

    private static void staticGen(Class clazz, String cacheKey, OutputStreamWriter writer, String source) throws IOException {
        String className = cacheKey.substring(cacheKey.lastIndexOf('.') + 1);
        String packageName = cacheKey.substring(0, cacheKey.lastIndexOf('.'));
        writer.write("package " + packageName + ";\n");
        writer.write("public class " + className + " implements com.jsoniter.spi.Encoder {\n");
        writer.write(source);
        writer.write("public void encode(java.lang.Object obj, com.jsoniter.output.JsonStream stream) throws java.io.IOException {\n");
        writer.write(String.format("encode_((%s)obj, stream);\n", clazz.getCanonicalName()));
        writer.write("}\n");
        writer.write("}\n");
    }

    private static void createDir(String cacheKey) {
        String[] parts = cacheKey.split("\\.");
        File parent = new File(".");
        for (int i = 0; i < parts.length - 1; i++) {
            String part = parts[i];
            File current = new File(parent, part);
            current.mkdir();
            parent = current;
        }
    }

    private static String genSource(Class clazz, Type[] typeArgs) {
        if (clazz.isArray()) {
            return CodegenImplArray.genArray(clazz);
        }
        if (Map.class.isAssignableFrom(clazz)) {
            return CodegenImplMap.genMap(clazz, typeArgs);
        }
        if (Collection.class.isAssignableFrom(clazz)) {
            return CodegenImplArray.genCollection(clazz, typeArgs);
        }
        return CodegenImplObject.genObject(clazz);
    }

    public static void staticGenEncoders(TypeLiteral[] typeLiterals) {
        isDoingStaticCodegen = true;
        for (TypeLiteral typeLiteral : typeLiterals) {
            gen(typeLiteral.getEncoderCacheKey(), typeLiteral.getType());
        }
    }
}
