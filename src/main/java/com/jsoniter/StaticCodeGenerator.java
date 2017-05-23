package com.jsoniter;

import com.jsoniter.output.EncodingMode;
import com.jsoniter.output.JsonStream;
import com.jsoniter.spi.CodegenConfig;
import com.jsoniter.spi.JsonException;
import com.jsoniter.spi.StaticCodegenTarget;

import java.io.File;
import java.nio.file.Path;

public class StaticCodeGenerator {
    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            System.out.println("StaticCodeGenerator configClassName [outputDir]");
            System.out.println("configClassName: like a.b.Config, a class defining what to codegen");
            System.out.println("outputDir: if not specified, will write to source directory of configClass");
            return;
        }
        String configClassName = args[0];
        String configJavaFile = configClassName.replace('.', '/') + ".java";
        StaticCodegenTarget staticCodegenTarget = new StaticCodegenTarget();
        if (args.length > 1) {
            staticCodegenTarget.outputDir = args[1];
        } else {
            if (!new File(configJavaFile).exists()) {
                throw new JsonException("must execute static code generator in the java source code directory which contains: " + configJavaFile);
            }
            staticCodegenTarget.outputDir = new File(".").getAbsolutePath();
        }
        Class<?> clazz = Class.forName(configClassName);
        CodegenConfig config = (CodegenConfig) clazz.newInstance();
        JsonIterator.setMode(DecodingMode.DYNAMIC_MODE_AND_MATCH_FIELD_WITH_HASH);
        JsonStream.setMode(EncodingMode.DYNAMIC_MODE);
        config.setup();
        CodegenAccess.staticGenDecoders(config.whatToCodegen(), staticCodegenTarget);
        com.jsoniter.output.CodegenAccess.staticGenEncoders(config.whatToCodegen(), staticCodegenTarget);
    }
}
