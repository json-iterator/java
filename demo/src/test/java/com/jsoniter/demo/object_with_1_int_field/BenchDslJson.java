package com.jsoniter.demo.object_with_1_int_field;

import com.dslplatform.json.CustomJsonReader;
import com.dslplatform.json.JsonWriter;
import org.openjdk.jmh.Main;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.BenchmarkParams;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.RunnerException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/*
Benchmark            Mode  Cnt         Score        Error  Units
BenchDslJson.deser  thrpt    5  36999837.132 ± 765406.929  ops/s (4.46x)
BenchDslJson.ser    thrpt    5  23126447.202 ± 247889.872  ops/s (2.26x)
 */
@State(Scope.Thread)
public class BenchDslJson {

    private TestObject testObject;
    private JsonWriter jsonWriter;
    private ByteArrayOutputStream byteArrayOutputStream;
    private byte[] testJSON;
    private CustomJsonReader reader;

    @Setup(Level.Trial)
    public void benchSetup(BenchmarkParams params) {
        testObject = TestObject.createTestObject();
        testJSON = TestObject.createTestJSON();
        jsonWriter = new JsonWriter();
        byteArrayOutputStream = new ByteArrayOutputStream();
        reader = new CustomJsonReader(testJSON);
    }

    @Benchmark
    public void ser(Blackhole bh) throws IOException {
        jsonWriter.reset();
        byteArrayOutputStream.reset();
        ExternalSerialization.serialize(testObject, jsonWriter, false);
        jsonWriter.toStream(byteArrayOutputStream);
        bh.consume(byteArrayOutputStream);
    }

    @Benchmark
    public void deser(Blackhole bh) throws IOException {
        reader.reset();
        reader.read();
        reader.getNextToken();
        TestObject obj = new TestObject();
        ExternalSerialization.deserialize(obj, reader);
        bh.consume(obj);
    }

    public static void main(String[] args) throws IOException, RunnerException {
        Main.main(new String[]{
                "object_with_1_int_field.BenchDslJson",
                "-i", "5",
                "-wi", "5",
                "-f", "1",
        });
    }
}
