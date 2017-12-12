package com.example.myapplication;

import com.jsoniter.JsonIterator;
import com.jsoniter.output.EncodingMode;
import com.jsoniter.output.JsonStream;
import com.jsoniter.spi.Decoder;
import com.jsoniter.spi.DecodingMode;
import com.jsoniter.spi.JsoniterSpi;
import com.jsoniter.spi.TypeLiteral;
import com.jsoniter.static_codegen.StaticCodegenConfig;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class DemoCodegenConfig implements StaticCodegenConfig {

    @Override
    public void setup() {
        // register custom decoder or extensions before codegen
        // so that we doing codegen, we know in which case, we need to callback
        JsonIterator.setMode(DecodingMode.STATIC_MODE);
        JsonStream.setMode(EncodingMode.STATIC_MODE);
        JsonStream.setIndentionStep(2);
        JsoniterSpi.registerPropertyDecoder(User.class, "score", new Decoder.IntDecoder() {
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
                new TypeLiteral<List<User>>() {
                },
                new TypeLiteral<Map<String, Object>>() {
                },
                // array
                TypeLiteral.create(int[].class),
                // object
                TypeLiteral.create(User.class)
        };
    }
}
