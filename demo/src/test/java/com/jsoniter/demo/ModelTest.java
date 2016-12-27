package com.jsoniter.demo;

import com.alibaba.fastjson.JSON;
import com.jsoniter.JsonIterator;
import com.jsoniter.spi.TypeLiteral;
import org.junit.Test;
import org.openjdk.jmh.Main;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.BenchmarkParams;
import org.openjdk.jmh.infra.Blackhole;

import java.io.IOException;

// Benchmark            Mode  Cnt         Score        Error  Units
// ModelTest.fastjson  thrpt    5   9293521.379 ± 402448.417  ops/s
// ModelTest.jsoniter  thrpt    5  17813770.824 ± 303816.547  ops/s
@State(Scope.Thread)
public class ModelTest {

    private String input;
    private JsonIterator iter;
    private byte[] inputBytes;
    private TypeLiteral<Model> modelTypeLiteral; // this is thread-safe can reused

    @Setup(Level.Trial)
    public void benchSetup(BenchmarkParams params) {
        input = "{\"id\":1001,\"name\":\"wenshao\"}";
        inputBytes = input.getBytes();
        iter = new JsonIterator();
        modelTypeLiteral = new TypeLiteral<Model>() {
        };
    }

    @Test
    public void test() throws IOException {
        benchSetup(null);
        iter.reset(inputBytes);
        System.out.println(iter.read(modelTypeLiteral).name);
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
        iter.reset(inputBytes);
        bh.consume(iter.read(modelTypeLiteral));
    }

    @Benchmark
    public void fastjson(Blackhole bh) throws IOException {
        // this is not a exactly fair comparison,
        // as string => object is not
        // bytes => object
        bh.consume(JSON.parseObject(input, Model.class));
    }

    public static class Model {
        public int id;
        public String name;
    }
}
