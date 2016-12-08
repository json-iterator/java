package com.github.jsoniter;

import org.openjdk.jmh.Main;
import org.openjdk.jmh.annotations.Benchmark;

import java.io.IOException;

public class JsoniterBenchmark {

//    @Benchmark
//    public void dsl() throws IOException {
//        JsonReader jsonReader = new JsonReader(JsoniterBenchmarkState.inputBytes, null);
//        jsonReader.skip();
//    }

    @Benchmark
    public void jsoniter() throws IOException {
        Jsoniter jsoniter = Jsoniter.parseBytes(JsoniterBenchmarkState.inputBytes);
        jsoniter.readString();
//        jsoniter.read(SimpleObject.class);
    }

    public static void main(String[] args) throws Exception {
        Main.main(args);
    }
}
