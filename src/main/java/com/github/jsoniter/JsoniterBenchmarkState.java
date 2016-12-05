package com.github.jsoniter;

import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

@State(Scope.Benchmark)
public class JsoniterBenchmarkState {
//    public static byte[] inputBytes = "{'field1': 'hello', 'field2': 'world'}".replace('\'', '"').getBytes();
//    public static String inputString = "{'field1': 'hello', 'field2': 'world'}".replace('\'', '"');
//    public static byte[] inputBytes = "[1,2,3,4,5,6,7,8,9]".getBytes();
//    public static String inputString = "[1,2,3,4,5,6,7,8,9]";
    public static byte[] inputBytes = "[{'field1':'11','field2':'12'},{'field1':'21','field2':'22'}]".replace('\'', '"').getBytes();
    public static String inputString = "[{'field1':'11','field2':'12'},{'field1':'21','field2':'22'}]".replace('\'', '"');
}
