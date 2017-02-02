package com.jsoniter.demo.object_with_1_int_field;

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
BenchThrift.deser  thrpt    5  75471992.596 ± 2732275.161  ops/s (9.10x)
BenchThrift.ser    thrpt    5  17130913.598 ±   74007.433  ops/s (1.67x)

Binary
Benchmark           Mode  Cnt         Score         Error  Units
BenchThrift.deser  thrpt    5  56765029.626 ± 2660078.316  ops/s (6.84x)
BenchThrift.ser    thrpt    5   8511186.468 ±   49663.163  ops/s (0.83x)
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
        testObject.setField1(1024);
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
                "object_with_1_int_field.BenchThrift",
                "-i", "5",
                "-wi", "5",
                "-f", "1",
        });
    }
}
