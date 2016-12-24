package com.jsoniter.demo;

import com.dslplatform.json.DslJson;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.afterburner.AfterburnerModule;
import com.jsoniter.JsonIterator;
import com.jsoniter.annotation.JacksonAnnotationSupport;
import com.jsoniter.spi.TypeLiteral;
import org.junit.Test;
import org.openjdk.jmh.Main;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.BenchmarkParams;
import org.openjdk.jmh.infra.Blackhole;

import java.io.IOException;
import java.util.Arrays;

@State(Scope.Thread)
public class ArrayBinding {
    private TypeLiteral<String[]> typeLiteral;
    private ObjectMapper jackson;
    private byte[] input;
    private TypeReference<String[]> typeRef;
    private String inputStr;

    private JsonIterator iter;
    private DslJson dslJson;

    @Setup(Level.Trial)
    public void benchSetup(BenchmarkParams params) {
        inputStr = "['jackson','jsoniter','fastjson']".replace('\'', '"');
        input = inputStr.getBytes();
        iter = JsonIterator.parse(input);
        typeLiteral = new TypeLiteral<String[]>() {
        };
        typeRef = new TypeReference<String[]>() {
        };
        JacksonAnnotationSupport.enable();
        jackson = new ObjectMapper();
        jackson.registerModule(new AfterburnerModule());
        dslJson = new DslJson();
    }

    @Test
    public void test() throws IOException {
        benchSetup(null);
        System.out.println(Arrays.toString(withJsoniter()));
        System.out.println(Arrays.toString(withJackson()));
        System.out.println(Arrays.toString(withDsljson()));
    }

    public static void main(String[] args) throws Exception {
        Main.main(new String[]{
                "ArrayBinding",
                "-i", "5",
                "-wi", "5",
                "-f", "1",
        });
    }

    @Benchmark
    public void withJsoniterBinding(Blackhole bh) throws IOException {
        bh.consume(withJsoniter());
    }

    @Benchmark
    public void withJackson(Blackhole bh) throws IOException {
        bh.consume(withJackson());
    }

    @Benchmark
    public void withDsljson(Blackhole bh) throws IOException {
        bh.consume(withDsljson());
    }

    private String[] withJsoniter() throws IOException {
        iter.reset();
        return iter.read(typeLiteral);
    }

    private String[] withJackson() throws IOException {
        return jackson.readValue(input, typeRef);
    }

    private String[] withDsljson() throws IOException {
        return (String[]) dslJson.deserialize(String[].class, input, input.length);
    }
}
