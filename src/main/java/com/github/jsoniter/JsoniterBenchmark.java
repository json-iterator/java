package com.github.jsoniter;

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
        JSONScanner scanner = new JSONScanner(JsoniterBenchmarkState.inputString);
        scanner.nextToken();
        do {
            scanner.nextToken();
            scanner.intValue();
            scanner.nextToken();
        } while (scanner.token() == JSONToken.COMMA);
    }

    @Benchmark
    public void jsoniter() throws IOException {
        Jsoniter iter = Jsoniter.parseBytes(JsoniterBenchmarkState.inputBytes);
        while (iter.ReadArray()) {
            iter.ReadUnsignedInt();
        }
    }

    public static void main(String[] args) throws Exception {
        Main.main(args);
    }
}
