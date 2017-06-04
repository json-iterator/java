package com.jsoniter;

import com.jsoniter.spi.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

class Codegen {

    // only read/write when generating code with synchronized protection
    private final static Set<String> generatedClassNames = new HashSet<String>();
    static CodegenAccess.StaticCodegenTarget isDoingStaticCodegen = null;
    static DecodingMode mode = DecodingMode.REFLECTION_MODE;
    static {
        String envMode = System.getenv("JSONITER_DECODING_MODE");
        if (envMode != null) {
            mode = DecodingMode.valueOf(envMode);
        }
    }

    public static void setMode(DecodingMode mode) {
        Codegen.mode = mode;
    }

    static Decoder getDecoder(String cacheKey, Type type) {
        Decoder decoder = JsoniterSpi.getDecoder(cacheKey);
        if (decoder != null) {
            return decoder;
        }
        return gen(cacheKey, type);
    }

    private synchronized static Decoder gen(String cacheKey, Type type) {
        Decoder decoder = JsoniterSpi.getDecoder(cacheKey);
        if (decoder != null) {
            return decoder;
        }
        List<Extension> extensions = JsoniterSpi.getExtensions();
        for (Extension extension : extensions) {
            type = extension.chooseImplementation(type);
        }
        type = chooseImpl(type);
        for (Extension extension : extensions) {
            decoder = extension.createDecoder(cacheKey, type);
            if (decoder != null) {
                JsoniterSpi.addNewDecoder(cacheKey, decoder);
                return decoder;
            }
        }
        ClassInfo classInfo = new ClassInfo(type);
        decoder = CodegenImplNative.NATIVE_DECODERS.get(classInfo.clazz);
        if (decoder != null) {
            return decoder;
        }
        addPlaceholderDecoderToSupportRecursiveStructure(cacheKey);
        if (mode == DecodingMode.REFLECTION_MODE) {
            decoder = ReflectionDecoderFactory.create(classInfo);
            JsoniterSpi.addNewDecoder(cacheKey, decoder);
            return decoder;
        }
        if (isDoingStaticCodegen == null) {
            try {
                decoder = (Decoder) Class.forName(cacheKey).newInstance();
                JsoniterSpi.addNewDecoder(cacheKey, decoder);
                return decoder;
            } catch (Exception e) {
                if (mode == DecodingMode.STATIC_MODE) {
                    throw new JsonException("static gen should provide the decoder we need, but failed to create the decoder", e);
                }
            }
        }
        String source = genSource(classInfo);
        source = "public static java.lang.Object decode_(com.jsoniter.JsonIterator iter) throws java.io.IOException { "
                + source + "}";
        if ("true".equals(System.getenv("JSONITER_DEBUG"))) {
            System.out.println(">>> " + cacheKey);
            System.out.println(source);
        }
        try {
            generatedClassNames.add(cacheKey);
            if (isDoingStaticCodegen == null) {
                decoder = DynamicCodegen.gen(cacheKey, source);
            } else {
                staticGen(cacheKey, source);
            }
            JsoniterSpi.addNewDecoder(cacheKey, decoder);
            return decoder;
        } catch (Exception e) {
            String msg = "failed to generate decoder for: " + classInfo + " with " + Arrays.toString(classInfo.typeArgs) + ", exception: " + e;
            msg = msg + "\n" + source;
            throw new JsonException(msg, e);
        }
    }

    private static void addPlaceholderDecoderToSupportRecursiveStructure(final String cacheKey) {
        JsoniterSpi.addNewDecoder(cacheKey, new Decoder() {
            @Override
            public Object decode(JsonIterator iter) throws IOException {
                return JsoniterSpi.getDecoder(cacheKey).decode(iter);
            }
        });
    }

    public static boolean canStaticAccess(String cacheKey) {
        return generatedClassNames.contains(cacheKey);
    }

    private static Type chooseImpl(Type type) {
        Type[] typeArgs = new Type[0];
        Class clazz;
        if (type instanceof ParameterizedType) {
            ParameterizedType pType = (ParameterizedType) type;
            clazz = (Class) pType.getRawType();
            typeArgs = pType.getActualTypeArguments();
        } else {
            clazz = (Class) type;
        }
        Class implClazz = JsoniterSpi.getTypeImplementation(clazz);
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
                clazz = implClazz == null ? ArrayList.class : implClazz;
            } else if (clazz == Set.class) {
                clazz = implClazz == null ? HashSet.class : implClazz;
            }
            return GenericsHelper.createParameterizedType(new Type[]{compType}, null, clazz);
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
            if (clazz == Map.class) {
                clazz = implClazz == null ? HashMap.class : implClazz;
            }
            if (keyType == Object.class) {
                keyType = String.class;
            }
            DefaultMapKeyDecoder.registerOrGetExisting(keyType);
            return GenericsHelper.createParameterizedType(new Type[]{keyType, valueType}, null, clazz);
        }
        if (implClazz != null) {
            if (typeArgs.length == 0) {
                return implClazz;
            } else {
                return GenericsHelper.createParameterizedType(typeArgs, null, implClazz);
            }
        }
        return type;
    }

    private static void staticGen(String cacheKey, String source) throws IOException {
        createDir(cacheKey);
        String fileName = cacheKey.replace('.', '/') + ".java";
        FileOutputStream fileOutputStream = new FileOutputStream(new File(isDoingStaticCodegen.outputDir, fileName));
        try {
            OutputStreamWriter writer = new OutputStreamWriter(fileOutputStream);
            try {
                staticGen(cacheKey, writer, source);
            } finally {
                writer.close();
            }
        } finally {
            fileOutputStream.close();
        }
    }

    private static void staticGen(String cacheKey, OutputStreamWriter writer, String source) throws IOException {
        String className = cacheKey.substring(cacheKey.lastIndexOf('.') + 1);
        String packageName = cacheKey.substring(0, cacheKey.lastIndexOf('.'));
        writer.write("package " + packageName + ";\n");
        writer.write("public class " + className + " implements com.jsoniter.spi.Decoder {\n");
        writer.write(source);
        writer.write("public java.lang.Object decode(com.jsoniter.JsonIterator iter) throws java.io.IOException {\n");
        writer.write("return decode_(iter);\n");
        writer.write("}\n");
        writer.write("}\n");
    }

    private static void createDir(String cacheKey) {
        String[] parts = cacheKey.split("\\.");
        File parent = new File(isDoingStaticCodegen.outputDir);
        for (int i = 0; i < parts.length - 1; i++) {
            String part = parts[i];
            File current = new File(parent, part);
            current.mkdir();
            parent = current;
        }
    }

    private static String genSource(ClassInfo classInfo) {
        if (classInfo.clazz.isArray()) {
            return CodegenImplArray.genArray(classInfo);
        }
        if (Map.class.isAssignableFrom(classInfo.clazz)) {
            return CodegenImplMap.genMap(classInfo);
        }
        if (Collection.class.isAssignableFrom(classInfo.clazz)) {
            return CodegenImplArray.genCollection(classInfo);
        }
        if (classInfo.clazz.isEnum()) {
            return CodegenImplEnum.genEnum(classInfo);
        }
        ClassDescriptor desc = ClassDescriptor.getDecodingClassDescriptor(classInfo, false);
        if (shouldUseStrictMode(desc)) {
            return CodegenImplObjectStrict.genObjectUsingStrict(desc);
        } else {
            return CodegenImplObjectHash.genObjectUsingHash(desc);
        }
    }

    private static boolean shouldUseStrictMode(ClassDescriptor desc) {
        if (mode == DecodingMode.DYNAMIC_MODE_AND_MATCH_FIELD_STRICTLY) {
            return true;
        }
        List<Binding> allBindings = desc.allDecoderBindings();
        for (Binding binding : allBindings) {
            if (binding.asMissingWhenNotPresent || binding.asExtraWhenPresent || binding.shouldSkip) {
                // only slice support mandatory tracking
                return true;
            }
        }
        if (desc.asExtraForUnknownProperties) {
            // only slice support unknown field tracking
            return true;
        }
        if (!desc.keyValueTypeWrappers.isEmpty()) {
            return true;
        }
        boolean hasBinding = false;
        for (Binding allBinding : allBindings) {
            if (allBinding.fromNames.length > 0) {
                hasBinding = true;
            }
        }
        if (!hasBinding) {
            // empty object can only be handled by strict mode
            return true;
        }
        return false;
    }

    public static void staticGenDecoders(TypeLiteral[] typeLiterals, CodegenAccess.StaticCodegenTarget staticCodegenTarget) {
        isDoingStaticCodegen = staticCodegenTarget;
        for (TypeLiteral typeLiteral : typeLiterals) {
            gen(typeLiteral.getDecoderCacheKey(), typeLiteral.getType());
        }
    }
}
