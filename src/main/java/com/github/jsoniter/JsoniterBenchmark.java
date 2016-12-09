package com.github.jsoniter;

import org.openjdk.jmh.Main;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.infra.Blackhole;

import java.io.IOException;

public class JsoniterBenchmark {

    @Benchmark
    public void dsl(Blackhole bh) throws IOException {
        SimpleObject obj = (SimpleObject) JsoniterBenchmarkState.dslJson.deserialize(
                SimpleObject.class, JsoniterBenchmarkState.inputBytes, JsoniterBenchmarkState.inputBytes.length);
        bh.consume(obj);
//        JsonReader jsonReader = new JsonReader(JsoniterBenchmarkState.inputBytes, null);
//        jsonReader.read();
//        jsonReader.readString();
//        ObjectConverter.deserializeObject(jsonReader);
    }

    @Benchmark
    public void jsoniter(Blackhole bh) throws IOException {
        JsoniterBenchmarkState.iter.reuse(JsoniterBenchmarkState.inputBytes);
        SimpleObject obj = JsoniterBenchmarkState.iter.read(SimpleObject.class);
        bh.consume(obj);
    }

    public static void main(String[] args) throws Exception {
        Main.main(args);
    }
}
