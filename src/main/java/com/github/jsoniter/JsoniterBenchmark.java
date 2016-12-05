package com.github.jsoniter;

import com.alibaba.fastjson.JSON;
import org.openjdk.jmh.Main;
import org.openjdk.jmh.annotations.Benchmark;

import java.io.IOException;
import java.util.Arrays;

public class JsoniterBenchmark {

    @Benchmark
    public void fastjson() {
        JSON.parseObject(JsoniterBenchmarkState.inputString, float[].class);
    }

    @Benchmark
    public void jsoniter() throws IOException {
        Jsoniter jsoniter = Jsoniter.parseBytes(JsoniterBenchmarkState.inputBytes);
        jsoniter.read(float[].class);
//        jsoniter.read(TestObj.class);
    }

    public static void main(String[] args) throws Exception {
        Main.main(args);
    }
}
