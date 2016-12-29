package com.jsoniter;

import com.jsoniter.output.EncodingMode;
import com.jsoniter.output.JsonStream;
import com.jsoniter.spi.CodegenConfig;

import java.io.File;

public class StaticCodeGenerator {
    public static void main(String[] args) throws Exception {
        String configClassName = args[0];
        Class<?> clazz = Class.forName(configClassName);
        CodegenConfig config = (CodegenConfig) clazz.newInstance();
        JsonIterator.setMode(DecodingMode.DYNAMIC_MODE_AND_MATCH_FIELD_WITH_HASH);
        JsonStream.setMode(EncodingMode.DYNAMIC_MODE);
        config.setup();
        CodegenAccess.staticGenDecoders(config.whatToCodegen());
        com.jsoniter.output.CodegenAccess.staticGenEncoders(config.whatToCodegen());
        String configJavaFile = configClassName.replace('.', '/') + ".java";
        if (!new File(configJavaFile).exists()) {
            throw new JsonException("must execute static code generator in the java source code directory which contains: " + configJavaFile);
        }
    }
}
