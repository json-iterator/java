package com.jsoniter.demo;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.afterburner.AfterburnerModule;
import com.jsoniter.DecodingMode;
import com.jsoniter.JsonIterator;
import com.jsoniter.spi.TypeLiteral;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import org.junit.Test;
import org.openjdk.jmh.Main;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.BenchmarkParams;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.RunnerException;

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
    private JsonAdapter<Model> moshiAdapter;

    @Setup(Level.Trial)
    public void benchSetup(BenchmarkParams params) {
//        JsonIterator.enableStreamingSupport();
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
        Moshi moshi = new Moshi.Builder().build();
        moshiAdapter = moshi.adapter(Model.class);
    }

    public static void main(String[] args) throws IOException, RunnerException {
        Main.main(new String[]{
                "ModelTest",
                "-i", "5",
                "-wi", "5",
                "-f", "1",
//                "-jvmArgsAppend", "-server -XX:+DoEscapeAnalysis",
        });
    }

    @Test
    public void test() throws IOException {
        benchSetup(null);
        iter.reset(inputBytes);
        System.out.println(iter.read(modelTypeLiteral).name);
        System.out.println(moshiAdapter.fromJson(input).name);
    }

//    public static void main(String[] args) throws Exception {
//        Options opt = new OptionsBuilder()
//                .include("ModelTest")
//                .addProfiler(JmhFlightRecorderProfiler.class)
//                .jvmArgs("-Xmx512m", "-Xms512m", "-XX:+UnlockCommercialFeatures",
//                        "-XX:+UnlockDiagnosticVMOptions", "-XX:+PrintAssembly",
//                        "-Djmh.stack.profiles=" + "/tmp",
//                        "-Djmh.executor=FJP",
//                        "-Djmh.fr.options=defaultrecording=true,settings=profile")
//                .warmupIterations(5)
//                .measurementTime(TimeValue.seconds(5))
//                .measurementIterations(5)
//                .forks(1)
//                .build();
//        new Runner(opt).run();
//    }

    @Benchmark
    public void jsoniter(Blackhole bh) throws IOException {
        iter.reset(inputBytes);
        bh.consume(iter.read(modelTypeLiteral));
    }

//    @Benchmark
    public void jsoniter_easy_mode(Blackhole bh) throws IOException {
        bh.consume(JsonIterator.deserialize(inputBytes, Model.class));
    }

//    @Benchmark
    public void fastjson(Blackhole bh) throws IOException {
        // this is not a exactly fair comparison,
        // as string => object is not
        // bytes => object
        bh.consume(JSON.parseObject(input, Model.class));
    }

    @Benchmark
    public void moshi(Blackhole bh) throws IOException {
        bh.consume(moshiAdapter.fromJson(input));
    }

//    @Benchmark
    public void jackson(Blackhole bh) throws IOException {
        bh.consume(jackson.readValue(inputBytes, modelTypeReference));
    }

    public static class Model {
        public int id;
        public String name;
    }
}
