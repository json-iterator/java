package com.github.jsoniter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.JSONReaderScanner;
import com.alibaba.fastjson.parser.JSONScanner;
import com.alibaba.fastjson.parser.JSONToken;
import org.openjdk.jmh.Main;
import org.openjdk.jmh.annotations.Benchmark;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;

public class JsoniterBenchmark {

    @Benchmark
    public void fastjson() {
        JSON.parseObject(JsoniterBenchmarkState.inputString, TestObj.class);
    }

    @Benchmark
    public void jsoniter() throws IOException {
        Jsoniter jsoniter = Jsoniter.parseBytes(JsoniterBenchmarkState.inputBytes);
//        jsoniter.Read(int[].class);
        jsoniter.Read(TestObj.class);
    }

    public static void main(String[] args) throws Exception {
        Main.main(args);
    }
}
