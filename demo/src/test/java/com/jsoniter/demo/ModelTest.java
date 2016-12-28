package com.jsoniter.demo;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.afterburner.AfterburnerModule;
import com.jsoniter.DecodingMode;
import com.jsoniter.JsonIterator;
import com.jsoniter.spi.TypeLiteral;
import org.junit.Test;
import org.openjdk.jmh.Main;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.BenchmarkParams;
import org.openjdk.jmh.infra.Blackhole;

import java.io.IOException;

// Benchmark            Mode  Cnt         Score        Error  Units
// ModelTest.fastjson  thrpt    5   7790201.506 ± 260185.529  ops/s
// ModelTest.jackson   thrpt    5   4063696.579 ± 169609.697  ops/s
// ModelTest.jsoniter  thrpt    5  16392968.819 ± 197563.536  ops/s
@State(Scope.Thread)
public class ModelTest {

    private String input;
    private JsonIterator iter;
    private byte[] inputBytes;
    private TypeLiteral<Model> modelTypeLiteral; // this is thread-safe can reused
    private ObjectMapper jackson;
    private TypeReference<Model> modelTypeReference;

    @Setup(Level.Trial)
    public void benchSetup(BenchmarkParams params) {
        input = "{\"name\":\"wenshao\",\"id\":1001}";
        inputBytes = input.getBytes();
        iter = new JsonIterator();
        modelTypeLiteral = new TypeLiteral<Model>() {
        };
        JsonIterator.setMode(DecodingMode.DYNAMIC_MODE_AND_MATCH_FIELD_WITH_HASH);
        jackson = new ObjectMapper();
        jackson.registerModule(new AfterburnerModule());
        modelTypeReference = new TypeReference<Model>() {
        };
    }

    @Test
    public void test() throws IOException {
        benchSetup(null);
        System.out.println(iter.read(inputBytes, modelTypeLiteral).name);
        System.out.println(JSON.parseObject(input, Model.class).name);

    }

    public static void main(String[] args) throws Exception {
        Main.main(new String[]{
                "ModelTest",
                "-i", "5",
                "-wi", "5",
                "-f", "1",
        });
    }

    @Benchmark
    public void jsoniter(Blackhole bh) throws IOException {
        bh.consume(iter.read(inputBytes, modelTypeLiteral));
    }

    @Benchmark
    public void fastjson(Blackhole bh) throws IOException {
        // this is not a exactly fair comparison,
        // as string => object is not
        // bytes => object
        bh.consume(JSON.parseObject(input, Model.class));
    }

    @Benchmark
    public void jackson(Blackhole bh) throws IOException {
        bh.consume(jackson.readValue(inputBytes, modelTypeReference));
    }

    public static class Model {
        public int id;
        public String name;
    }
}
