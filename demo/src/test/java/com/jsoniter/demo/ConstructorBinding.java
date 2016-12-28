package com.jsoniter.demo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
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
public class ConstructorBinding {

    private TypeLiteral<TestObject> typeLiteral;
    private ObjectMapper jackson;
    private byte[] input;
    private TypeReference<TestObject> typeRef;
    private String inputStr;

    public static class TestObject {
        @JsonIgnore
        private int field1;
        @JsonIgnore
        private int field2;

        @JsonCreator
        public TestObject(
                @JsonProperty("field1") int field1,
                @JsonProperty("field2") int field2) {
            this.field1 = field1;
            this.field2 = field2;
        }

        @Override
        public String toString() {
            return "TestObject1{" +
                    "field1=" + field1 +
                    ", field2=" + field2 +
                    '}';
        }
    }


    private JsonIterator iter;

    @Setup(Level.Trial)
    public void benchSetup(BenchmarkParams params) {
        inputStr = "{'field1':100,'field2':101}";
        input = inputStr.replace('\'', '"').getBytes();
        iter = JsonIterator.parse(input);
        typeLiteral = new TypeLiteral<TestObject>() {
        };
        typeRef = new TypeReference<TestObject>() {
        };
        JacksonAnnotationSupport.enable();
        jackson = new ObjectMapper();
        jackson.registerModule(new AfterburnerModule());
        if (params != null) {
            if (params.getBenchmark().contains("withJsoniterStrictMode")) {
                JsonIterator.setMode(DecodingMode.DYNAMIC_MODE_AND_MATCH_FIELD_STRICTLY);
            }
            if (params.getBenchmark().contains("withJsoniterReflection")) {
                JsoniterSpi.registerTypeDecoder(TestObject.class, ReflectionDecoderFactory.create(TestObject.class));
            }
        }
    }

    @Test
    public void test() throws IOException {
        benchSetup(null);
        JsoniterSpi.registerTypeDecoder(TestObject.class, ReflectionDecoderFactory.create(TestObject.class));
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

    private TestObject withJsoniter() throws IOException {
        iter.reset();
        return iter.read(typeLiteral);
    }

    private TestObject withJackson() throws IOException {
        return jackson.readValue(input, typeRef);
    }
}
