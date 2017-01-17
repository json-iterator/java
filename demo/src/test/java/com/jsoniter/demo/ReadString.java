package com.jsoniter.demo;


import com.jsoniter.JsonIterator;
import org.junit.Test;
import org.openjdk.jmh.Main;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.BenchmarkParams;
import org.openjdk.jmh.infra.Blackhole;

import java.io.IOException;

@State(Scope.Thread)
public class ReadString {


    private JsonIterator jsonIterator;
    private byte[] input;

    public static void main(String[] args) throws Exception {
        Main.main(new String[]{
                "ReadString",
                "-i", "5",
                "-wi", "5",
                "-f", "1",
        });
    }

    @Test
    public void test() throws IOException {
        benchSetup(null);
    }

    @Setup(Level.Trial)
    public void benchSetup(BenchmarkParams params) {
        jsonIterator = new JsonIterator();
        input = "\"hello world\"".getBytes();
    }

    @Benchmark
    public void jsoniter(Blackhole bh) throws IOException {
        jsonIterator.reset(input);
        bh.consume(jsonIterator.readString());
    }
}
