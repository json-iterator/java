package com.jsoniter.output;

import com.jsoniter.JsonException;
import com.jsoniter.spi.Encoder;
import com.jsoniter.spi.Extension;
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

    private static EncodingMode mode = EncodingMode.REFLECTION_MODE;
    static boolean isDoingStaticCodegen;
    // only read/write when generating code with synchronized protection
    private final static Map<String, CodegenResult> generatedSources = new HashMap<String, CodegenResult>();
    private volatile static Map<String, Encoder> reflectionEncoders = new HashMap<String, Encoder>();

    static {
        String envMode = System.getenv("JSONITER_ENCODING_MODE");
        if (envMode != null) {
            mode = EncodingMode.valueOf(envMode);
        }
    }

    public static void setMode(EncodingMode mode) {
        Codegen.mode = mode;
    }


    public static Encoder getReflectionEncoder(String cacheKey, Type type) {
        Encoder encoder = CodegenImplNative.NATIVE_ENCODERS.get(type);
        if (encoder != null) {
            return encoder;
        }
        encoder = reflectionEncoders.get(cacheKey);
        if (encoder != null) {
            return encoder;
        }
        synchronized (Codegen.class) {
            encoder = reflectionEncoders.get(cacheKey);
            if (encoder != null) {
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
            encoder = ReflectionEncoderFactory.create(clazz, typeArgs);
            HashMap<String, Encoder> copy = new HashMap<String, Encoder>(reflectionEncoders);
            copy.put(cacheKey, encoder);
            reflectionEncoders = copy;
            return encoder;
        }
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
        List<Extension> extensions = JsoniterSpi.getExtensions();
        for (Extension extension : extensions) {
            encoder = extension.createEncoder(cacheKey, type);
            if (encoder != null) {
                JsoniterSpi.addNewEncoder(cacheKey, encoder);
                return encoder;
            }
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
            encoder = ReflectionEncoderFactory.create(clazz, typeArgs);
            JsoniterSpi.addNewEncoder(cacheKey, encoder);
            return encoder;
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
        CodegenResult source = genSource(clazz, typeArgs);
        if ("true".equals(System.getenv("JSONITER_DEBUG"))) {
            System.out.println(">>> " + cacheKey);
            System.out.println("prelude: " + source.prelude);
            System.out.println(source);
            System.out.println("epilogue: " + source.epilogue);
        }
        try {
            generatedSources.put(cacheKey, source);
            if (isDoingStaticCodegen) {
                staticGen(clazz, cacheKey, source);
            } else {
                encoder = DynamicCodegen.gen(clazz, cacheKey, source);
            }
            JsoniterSpi.addNewEncoder(cacheKey, encoder);
            return encoder;
        } catch (Exception e) {
            System.err.println("failed to generate encoder for: " + type + " with " + Arrays.toString(typeArgs) + ", exception: " + e);
            System.err.println(source);
            JsoniterSpi.dump();
            throw new JsonException(e);
        }
    }

    public static CodegenResult getGeneratedSource(String cacheKey) {
        return generatedSources.get(cacheKey);
    }

    private static void staticGen(Class clazz, String cacheKey, CodegenResult source) throws IOException {
        createDir(cacheKey);
        String fileName = cacheKey.replace('.', '/') + ".java";
        FileOutputStream fileOutputStream = new FileOutputStream(fileName);
        try {
            OutputStreamWriter writer = new OutputStreamWriter(fileOutputStream);
            try {
                throw new UnsupportedOperationException();
//                staticGen(clazz, cacheKey, writer, source);
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
        writer.write("public class " + className + " extends com.jsoniter.spi.EmptyEncoder {\n");
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

    private static CodegenResult genSource(Class clazz, Type[] typeArgs) {
        if (clazz.isArray()) {
            return CodegenImplArray.genArray(clazz);
        }
        if (Map.class.isAssignableFrom(clazz)) {
            return CodegenImplMap.genMap(clazz, typeArgs);
        }
        if (Collection.class.isAssignableFrom(clazz)) {
            return CodegenImplArray.genCollection(clazz, typeArgs);
        }
        if (clazz.isEnum()) {
            return CodegenImplNative.genEnum(clazz);
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
