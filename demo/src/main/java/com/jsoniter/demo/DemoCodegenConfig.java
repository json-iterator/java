package com.jsoniter.demo;

import com.jsoniter.JsonIterator;
import com.jsoniter.StaticCodeGenerator;
import com.jsoniter.spi.CodegenConfig;
import com.jsoniter.spi.Decoder;
import com.jsoniter.spi.ExtensionManager;
import com.jsoniter.spi.TypeLiteral;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class DemoCodegenConfig implements CodegenConfig {

    @Override
    public void setup() {
        // register custom decoder or extensions before codegen
        // so that we doing codegen, we know in which case, we need to callback
        ExtensionManager.registerPropertyDecoder(User.class, "score", new Decoder.IntDecoder() {
            @Override
            public int decodeInt(JsonIterator iter) throws IOException {
                return Integer.valueOf(iter.readString());
            }
        });
    }

    @Override
    public TypeLiteral[] whatToCodegen() {
        return new TypeLiteral[]{
                // generic types, need to use this syntax
                new TypeLiteral<List<Integer>>() {
                },
                new TypeLiteral<Map<String, Object>>() {
                },
                // array
                TypeLiteral.create(int[].class),
                // object
                TypeLiteral.create(User.class)
        };
    }

    public static void main(String[] args) throws Exception {
        StaticCodeGenerator.main(new String[]{DemoCodegenConfig.class.getCanonicalName()});
    }
}
