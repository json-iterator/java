package com.github.jsoniter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONReader;
import com.dslplatform.json.JsonReader;
import org.openjdk.jmh.Main;
import org.openjdk.jmh.annotations.Benchmark;

import java.io.IOException;
import java.util.Arrays;

public class JsoniterBenchmark {

    @Benchmark
    public void dsl() throws IOException {
        JsonReader jsonReader = new JsonReader(JsoniterBenchmarkState.inputBytes, null);
        jsonReader.skip();
    }

//    @Benchmark
//    public void jsoniter() throws IOException {
//        Jsoniter jsoniter = Jsoniter.parseBytes(JsoniterBenchmarkState.inputBytes);
//        jsoniter.skip
////        jsoniter.read(SimpleObject.class);
//    }

    public static void main(String[] args) throws Exception {
        Main.main(args);
    }
}
