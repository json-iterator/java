package com.jsoniter.demo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.afterburner.AfterburnerModule;
import com.jsoniter.JsonIterator;
import com.jsoniter.ReflectionDecoder;
import com.jsoniter.annotation.JacksonAnnotationSupport;
import com.jsoniter.spi.ExtensionManager;
import com.jsoniter.spi.TypeLiteral;
import org.junit.Test;
import org.openjdk.jmh.Main;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.io.IOException;

@State(Scope.Thread)
public class PrivateFieldBinding {

    private TypeLiteral<TestObject> typeLiteral;
    private ObjectMapper jackson;
    private byte[] input;
    private TypeReference<TestObject> typeRef;
    private String inputStr;

    public static class TestObject {
        @JsonProperty
        private int field1;
        @JsonProperty
        private int field2;

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
    public void benchSetup() {
        inputStr = "{'field1':100,'field2':101}";
        input = inputStr.replace('\'', '"').getBytes();
        iter = JsonIterator.parse(input);
        typeLiteral = new TypeLiteral<TestObject>() {
        };
        typeRef = new TypeReference<TestObject>() {
        };
        JacksonAnnotationSupport.enable();
        ExtensionManager.registerTypeDecoder(TestObject.class, new ReflectionDecoder(TestObject.class));
        jackson = new ObjectMapper();
        jackson.registerModule(new AfterburnerModule());
    }

    @Test
    public void test() throws IOException {
        benchSetup();
        System.out.println(withJsoniter());
        System.out.println(withJackson());
    }

    @Benchmark
    public void withJsoniter(Blackhole bh) throws IOException {
        bh.consume(withJsoniter());
    }

    @Benchmark
    public void withJackson(Blackhole bh) throws IOException {
        bh.consume(withJackson());
    }

    public static void main(String[] args) throws Exception {
        Main.main(new String[]{
                "PrivateFieldBinding.*",
                "-i", "5",
                "-wi", "5",
                "-f", "1",
        });
    }

    private TestObject withJsoniter() throws IOException {
        iter.reset();
        return iter.read(typeLiteral);
    }

    private TestObject withJackson() throws IOException {
        return jackson.readValue(input, typeRef);
    }
}
