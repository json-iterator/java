package com.github.jsoniter;

import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

@State(Scope.Benchmark)
public class JsoniterBenchmarkState {
    public static byte[] input = "123".getBytes();
}
