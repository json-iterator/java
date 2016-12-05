package com.github.jsoniter;

import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

@State(Scope.Benchmark)
public class JsoniterBenchmarkState {
    public static byte[] inputBytes = "[1,2,3]".getBytes();
    public static String inputString = "[1,2,3]";
}
