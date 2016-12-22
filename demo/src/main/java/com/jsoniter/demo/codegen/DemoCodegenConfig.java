package com.jsoniter.demo.codegen;

import com.jsoniter.StaticCodeGenerator;
import com.jsoniter.spi.CodegenConfig;
import com.jsoniter.spi.ExtensionManager;
import com.jsoniter.spi.TypeLiteral;

import java.util.List;

public class DemoCodegenConfig implements CodegenConfig {

    @Override
    public void beforeCodegen() {
        ExtensionManager.disableDynamicCodegen();
    }

    @Override
    public TypeLiteral[] getTypeLiterals() {
        return new TypeLiteral[]{
                new TypeLiteral<List<String>>() {
                }
        };
    }

    public static void main(String[] args) throws Exception {
        StaticCodeGenerator.main(new String[]{DemoCodegenConfig.class.getCanonicalName()});
    }
}
