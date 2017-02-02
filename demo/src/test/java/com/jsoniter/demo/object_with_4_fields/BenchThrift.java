package com.jsoniter.demo.object_with_4_fields;

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
BenchThrift.deser  thrpt    5  6136890.135 ± 259530.249  ops/s (1.47x)
BenchThrift.ser    thrpt    5  3101745.552 ±  59109.195  ops/s (0.54x)

Binary
Benchmark           Mode  Cnt        Score        Error  Units
BenchThrift.deser  thrpt    5  5423946.499 ± 465578.762  ops/s (1.30x)
BenchThrift.ser    thrpt    5  2193090.924 ±  65616.866  ops/s (0.38x)
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
        testObject.setField1("field1");
        testObject.setField2("field2");
        testObject.setField3("field3");
        testObject.setField4("field4");
//        serializer = new TSerializer(new TTupleProtocol.Factory());
        serializer = new TSerializer(new TBinaryProtocol.Factory());
//        deserializer = new TDeserializer(new TTupleProtocol.Factory());
        deserializer = new TDeserializer(new TBinaryProtocol.Factory());
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
                "object_with_4_fields.BenchThrift",
                "-i", "5",
                "-wi", "5",
                "-f", "1",
        });
    }
}
