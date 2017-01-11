package com.jsoniter.demo;


import com.dslplatform.json.DslJson;
import com.dslplatform.json.JsonWriter;
import com.dslplatform.json.NumberConverter;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jsoniter.annotation.JsonWrapper;
import com.jsoniter.output.JsonStream;
import org.junit.Test;
import org.openjdk.jmh.Main;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.BenchmarkParams;
import org.openjdk.jmh.infra.Blackhole;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@State(Scope.Thread)
public class IntegerOutput {

    private ByteArrayOutputStream baos;
    private ObjectMapper objectMapper;
    private JsonStream stream;
    private byte[] buffer;
    private DslJson dslJson;
    private JsonWriter jsonWriter;

    public static void main(String[] args) throws Exception {
        Main.main(new String[]{
                "IntegerOutput",
                "-i", "5",
                "-wi", "5",
                "-f", "1",
                "-prof", "stack"
        });
    }

    @Test
    public void test() throws IOException {
        benchSetup(null);
        jsoniter(null);
        System.out.println(baos.toString());
        jackson();
        System.out.println(baos.toString());
        dsljson(null);
        System.out.println(baos.toString());
    }

    @Setup(Level.Trial)
    public void benchSetup(BenchmarkParams params) {
        baos = new ByteArrayOutputStream(1024 * 64);
        objectMapper = new ObjectMapper();
        objectMapper.getFactory().configure(JsonGenerator.Feature.ESCAPE_NON_ASCII, true);
        stream = new JsonStream(baos, 4096);
        buffer = new byte[4096];
        dslJson = new DslJson();
        jsonWriter = new JsonWriter();
    }

    @Benchmark
    public void jsoniter(Blackhole bh) throws IOException {
        baos.reset();
        stream.reset(baos);
        stream.writeVal(1024);
        stream.flush();
//        bh.consume(stream);
    }

    @Benchmark
    public void jackson() throws IOException {
        baos.reset();
        objectMapper.writeValue(baos, 1024);
    }

//    @Benchmark
    public void dsljson(Blackhole bh) throws IOException {
//        baos.reset();
        jsonWriter.reset();
        NumberConverter.serialize(1024, jsonWriter);
//        bh.consume(jsonWriter);
//        jsonWriter.toStream(baos);
    }
}
