package com.github.jsoniter;

import com.alibaba.fastjson.parser.JSONReaderScanner;
import org.openjdk.jmh.Main;
import org.openjdk.jmh.annotations.Benchmark;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class JsoniterBenchmark {

    @Benchmark
    public void fastjson() {
        new JSONReaderScanner(new InputStreamReader(new ByteArrayInputStream(JsoniterBenchmarkState.input))).intValue();
    }

    @Benchmark
    public void jsoniter() throws IOException {
        Jsoniter.parse(new ByteArrayInputStream(JsoniterBenchmarkState.input), 4096).ReadUnsignedInt();
    }

    public static void main(String[] args) throws Exception {
        Main.main(args);
    }
}
