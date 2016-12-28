package com.jsoniter.demo;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.afterburner.AfterburnerModule;
import com.jsoniter.DecodingMode;
import com.jsoniter.JsonIterator;
import com.jsoniter.ReflectionDecoderFactory;
import com.jsoniter.annotation.JacksonAnnotationSupport;
import com.jsoniter.spi.JsoniterSpi;
import com.jsoniter.spi.TypeLiteral;
import org.junit.Test;
import org.openjdk.jmh.Main;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.BenchmarkParams;
import org.openjdk.jmh.infra.Blackhole;

import java.io.IOException;

@State(Scope.Thread)
public class SetterBinding {

    private TypeLiteral<ConstructorBinding.TestObject> typeLiteral;
    private ObjectMapper jackson;
    private byte[] input;
    private TypeReference<ConstructorBinding.TestObject> typeRef;
    private String inputStr;

    public static class TestObject {
        private int field1;
        private int field2;

        public void setField1(int field1) {
            this.field1 = field1;
        }

        public void setField2(int field2) {
            this.field2 = field2;
        }

//        @JsonSetter
//        public void initialize(
//                @JsonProperty("field1") int field1,
//                @JsonProperty("field2") int field2) {
//            this.field1 = field1;
//            this.field2 = field2;
//        }
    }


    private JsonIterator iter;

    @Setup(Level.Trial)
    public void benchSetup(BenchmarkParams params) {
        inputStr = "{'field1':100,'field2':101}".replace('\'', '"');
        input = inputStr.getBytes();
        iter = JsonIterator.parse(input);
        typeLiteral = new TypeLiteral<ConstructorBinding.TestObject>() {
        };
        typeRef = new TypeReference<ConstructorBinding.TestObject>() {
        };
        JacksonAnnotationSupport.enable();
        jackson = new ObjectMapper();
        jackson.registerModule(new AfterburnerModule());
        if (params != null) {
            if (params.getBenchmark().contains("withJsoniterStrictMode")) {
                JsonIterator.setMode(DecodingMode.DYNAMIC_MODE_AND_MATCH_FIELD_STRICTLY);
            }
            if (params.getBenchmark().contains("withJsoniterReflection")) {
                JsoniterSpi.registerTypeDecoder(ConstructorBinding.TestObject.class, ReflectionDecoderFactory.create(TestObject.class));
            }
        }
    }

    @Test
    public void test() throws IOException {
        benchSetup(null);
        JsoniterSpi.registerTypeDecoder(ConstructorBinding.TestObject.class, ReflectionDecoderFactory.create(TestObject.class));
        System.out.println(withJsoniter());
        System.out.println(withJackson());
    }

    public static void main(String[] args) throws Exception {
        Main.main(new String[]{
                "ConstructorBinding",
                "-i", "5",
                "-wi", "5",
                "-f", "1",
        });
    }

    @Benchmark
    public void withJsoniterHashMode(Blackhole bh) throws IOException {
        bh.consume(withJsoniter());
    }

    @Benchmark
    public void withJsoniterStrictMode(Blackhole bh) throws IOException {
        bh.consume(withJsoniter());
    }

    @Benchmark
    public void withJsoniterReflection(Blackhole bh) throws IOException {
        bh.consume(withJsoniter());
    }

    @Benchmark
    public void withJackson(Blackhole bh) throws IOException {
        bh.consume(withJackson());
    }

    private ConstructorBinding.TestObject withJsoniter() throws IOException {
        iter.reset();
        return iter.read(typeLiteral);
    }

    private ConstructorBinding.TestObject withJackson() throws IOException {
        return jackson.readValue(input, typeRef);
    }
}
