package com.jsoniter.demo.object_with_1_int_field;

import com.jsoniter.DecodingMode;
import com.jsoniter.JsonIterator;
import com.jsoniter.output.EncodingMode;
import com.jsoniter.output.JsonStream;
import com.jsoniter.spi.TypeLiteral;
import org.openjdk.jmh.Main;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.BenchmarkParams;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.RunnerException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/*
Benchmark             Mode  Cnt         Score         Error  Units
BenchJsoniter.deser  thrpt    5  44446325.748 ± 1126285.176  ops/s (5.36x)
BenchJsoniter.ser    thrpt    5  33525853.836 ± 1197831.185  ops/s (3.27x)
 */
@State(Scope.Thread)
public class BenchJsoniter {

    private TestObject testObject;
    private JsonStream stream;
    private ByteArrayOutputStream byteArrayOutputStream;
    private byte[] testJSON;
    private JsonIterator iter;
    private TypeLiteral typeLiteral;

    @Setup(Level.Trial)
    public void benchSetup(BenchmarkParams params) {
        JsonStream.setMode(EncodingMode.DYNAMIC_MODE);
        JsonIterator.setMode(DecodingMode.DYNAMIC_MODE_AND_MATCH_FIELD_WITH_HASH);
        testObject = TestObject.createTestObject();
        testJSON = TestObject.createTestJSON();
        stream = new JsonStream(null, 512);
        byteArrayOutputStream = new ByteArrayOutputStream();
        iter = new JsonIterator();
        typeLiteral = TypeLiteral.create(TestObject.class);
    }

    @Benchmark
    public void ser(Blackhole bh) throws IOException {
        byteArrayOutputStream.reset();
        stream.reset(byteArrayOutputStream);
        stream.writeVal(testObject);
        bh.consume(byteArrayOutputStream);
    }

    @Benchmark
    public void deser(Blackhole bh) throws IOException {
        iter.reset(testJSON);
        bh.consume(iter.read(typeLiteral));
    }

    public static void main(String[] args) throws IOException, RunnerException {
        Main.main(new String[]{
                "object_with_1_int_field.BenchJsoniter",
                "-i", "5",
                "-wi", "5",
                "-f", "1",
        });
    }
}
