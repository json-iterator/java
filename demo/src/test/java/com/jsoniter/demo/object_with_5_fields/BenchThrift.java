package com.jsoniter.demo.object_with_5_fields;

import org.apache.thrift.TDeserializer;
import org.apache.thrift.TException;
import org.apache.thrift.TSerializer;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TTupleProtocol;
import org.junit.Test;
import org.openjdk.jmh.Main;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.BenchmarkParams;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.RunnerException;

import java.io.IOException;

/*
Tuple
Benchmark           Mode  Cnt        Score        Error  Units
BenchThrift.deser  thrpt    5  4894731.174 ± 190486.954  ops/s (1.38x)
BenchThrift.ser    thrpt    5  2537935.619 ± 132875.762  ops/s (0.47x)

Compact
Benchmark           Mode  Cnt        Score        Error  Units
BenchThrift.deser  thrpt    5  4490620.091 ± 118728.895  ops/s
BenchThrift.ser    thrpt    5  2114218.709 ±  66750.207  ops/s

Binary
Benchmark           Mode  Cnt        Score       Error  Units
BenchThrift.deser  thrpt    5  4463916.092 ± 74085.264  ops/s
BenchThrift.ser    thrpt    5  1780672.495 ± 21550.292  ops/s
 */
@State(Scope.Thread)
public class BenchThrift {

    private TSerializer serializer;
    private ThriftTestObject testObject;
    private TDeserializer deserializer;
    private byte[] testData;

    @Setup(Level.Trial)
    public void benchSetup(BenchmarkParams params) throws TException {
        testObject = new ThriftTestObject();
        testObject.field1 = "field1";
        testObject.field2 = "field2";
        testObject.field3 = "field3";
        testObject.field4 = "field4";
        testObject.field5 = "field5";
//        serializer = new TSerializer(new TTupleProtocol.Factory());
        serializer = new TSerializer(new TCompactProtocol.Factory());
//        serializer = new TSerializer(new TBinaryProtocol.Factory());
//        deserializer = new TDeserializer(new TTupleProtocol.Factory());
        deserializer = new TDeserializer(new TCompactProtocol.Factory());
//        deserializer = new TDeserializer(new TBinaryProtocol.Factory());
        testData = serializer.serialize(testObject);
    }

    @Test
    public void test() throws TException {
        byte[] output = new TSerializer(new TCompactProtocol.Factory()).serialize(testObject);
        System.out.println(output.length);
    }

    @Benchmark
    public void ser(Blackhole bh) throws TException {
        bh.consume(serializer.serialize(testObject));
    }

    @Benchmark
    public void deser(Blackhole bh) throws TException {
        ThriftTestObject obj = new ThriftTestObject();
        deserializer.deserialize(testObject, testData);
        bh.consume(obj);
    }

    public static void main(String[] args) throws IOException, RunnerException {
        Main.main(new String[]{
                "object_with_5_fields.BenchThrift",
                "-i", "5",
                "-wi", "5",
                "-f", "1",
        });
    }
}
