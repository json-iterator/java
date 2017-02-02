package com.jsoniter.demo.object_with_1_field;

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
Benchmark           Mode  Cnt         Score         Error  Units
BenchThrift.deser  thrpt    5  19999891.957 ± 1690769.199  ops/s (2.45x)
BenchThrift.ser    thrpt    5   7776020.372 ±  133622.260  ops/s (0.81x)

Binary
Benchmark           Mode  Cnt         Score        Error  Units
BenchThrift.deser  thrpt    5  18565469.435 ± 669296.325  ops/s (2.28x)
BenchThrift.ser    thrpt    5   6213563.710 ±  26744.572  ops/s (0.65x)
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
        serializer = new TSerializer(new TTupleProtocol.Factory());
//        serializer = new TSerializer(new TBinaryProtocol.Factory());
        deserializer = new TDeserializer(new TTupleProtocol.Factory());
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
                "object_with_1_field.BenchThrift",
                "-i", "5",
                "-wi", "5",
                "-f", "1",
        });
    }
}
