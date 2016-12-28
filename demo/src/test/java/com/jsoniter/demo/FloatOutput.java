package com.jsoniter.demo;


import com.dslplatform.json.DslJson;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jsoniter.output.JsonStream;
import org.junit.Test;
import org.openjdk.jmh.Main;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.BenchmarkParams;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@State(Scope.Thread)
public class FloatOutput {

    private ByteArrayOutputStream baos;
    private ObjectMapper objectMapper;
    private JsonStream stream;
    private byte[] buffer;
    private DslJson dslJson;

    public static void main(String[] args) throws Exception {
        Main.main(new String[]{
                "FloatOutput",
                "-i", "5",
                "-wi", "5",
                "-f", "1",
        });
    }

    @Test
    public void test() throws IOException {
        benchSetup(null);
        jsoniter();
        System.out.println(baos.toString());
        jackson();
        System.out.println(baos.toString());
        dsljson();
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
    }

    @Benchmark
    public void jsoniter() throws IOException {
        baos.reset();
        stream.reset(baos);
        stream.writeVal(10.24f);
        stream.flush();
    }

    @Benchmark
    public void jackson() throws IOException {
        baos.reset();
        objectMapper.writeValue(baos, 10.24f);
    }

    @Benchmark
    public void dsljson() throws IOException {
        baos.reset();
        dslJson.serialize(10.24f, baos);
    }
}
