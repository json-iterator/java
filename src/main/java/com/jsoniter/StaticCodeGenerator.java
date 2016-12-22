package com.jsoniter;

import com.jsoniter.spi.CodegenConfig;

import java.io.File;

public class StaticCodeGenerator {
    public static void main(String[] args) throws Exception {
        String configClassName = args[0];
        Class<?> clazz = Class.forName(configClassName);
        CodegenConfig config = (CodegenConfig) clazz.newInstance();
        config.beforeCodegen();
        CodegenAccess.staticGenDecoders(config.getTypeLiterals());
        String configJavaFile = configClassName.replace('.', '/') + ".java";
        if (!new File(configJavaFile).exists()) {
            throw new JsonException("must execute static code generator in the java source code directory which contains: " + configJavaFile);
        }
    }
}
