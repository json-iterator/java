package com.github.jsoniter;

import com.dslplatform.json.DslJson;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;

@State(Scope.Benchmark)
public class JsoniterBenchmarkState {
    public static DslJson dslJson = new DslJson();
    public static Jsoniter iter = Jsoniter.parseBytes(new byte[0]);
    public static byte[] inputBytes = "{'field1': 'hello', 'field2': 'world'}".replace('\'', '"').getBytes();
//    public static String inputString = "{'field1': 'hello', 'field2': 'world'}".replace('\'', '"');
//    public static byte[] inputBytes = "[1,2,3,4,5,6,7,8,9]".getBytes();
//    public static String inputString = "[1,2,3,4,5,6,7,8,9]";
//    public static byte[] inputBytes = "'abcd'".replace('\'', '"').getBytes();
//    public static byte[] inputBytes = {'"', (byte) '\\', (byte) 'u', (byte) '4', (byte) 'e', (byte) '2', (byte) 'd', '"'};
//    public static byte[] inputBytes = "[{'field1':'11','field2':'12'},{'field1':'21','field2':'22'}]".replace('\'', '"').getBytes();
//    public static String inputString = "[{'field1':'11','field2':'12'},{'field1':'21','field2':'22'}]".replace('\'', '"');
}
