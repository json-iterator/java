package com.jsoniter.demo;

import com.dslplatform.json.DslJson;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.afterburner.AfterburnerModule;
import com.jsoniter.JsonIterator;
import com.jsoniter.annotation.JacksonAnnotationSupport;
import com.jsoniter.spi.TypeLiteral;
import org.junit.Test;
import org.openjdk.jmh.Main;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.BenchmarkParams;
import org.openjdk.jmh.infra.Blackhole;

import java.io.IOException;

@State(Scope.Thread)
public class ArrayBinding {
    private TypeLiteral<int[]> typeLiteral;
    private ObjectMapper jackson;
    private byte[] input;
    private TypeReference<int[]> typeRef;
    private String inputStr;

    private JsonIterator iter;
    private DslJson dslJson;

    @Setup(Level.Trial)
    public void benchSetup(BenchmarkParams params) {
        inputStr = "[1,2,3,4,5,6,7,8,9]".replace('\'', '"');
        input = inputStr.getBytes();
        iter = JsonIterator.parse(input);
        typeLiteral = new TypeLiteral<int[]>() {
        };
        typeRef = new TypeReference<int[]>() {
        };
        JacksonAnnotationSupport.enable();
        jackson = new ObjectMapper();
        jackson.registerModule(new AfterburnerModule());
        dslJson = new DslJson();
    }

    @Test
    public void test() throws IOException {
        benchSetup(null);
        System.out.println(withJsoniter());
        System.out.println(withIterator());
        System.out.println(withJackson());
        System.out.println(withDsljson());
    }

    public static void main(String[] args) throws Exception {
        Main.main(new String[]{
                "ArrayBinding",
                "-i", "5",
                "-wi", "5",
                "-f", "1",
        });
    }

    @Benchmark
    public void withJsoniterBinding(Blackhole bh) throws IOException {
        bh.consume(withJsoniter());
    }

    @Benchmark
    public void withJsoniterIterator(Blackhole bh) throws IOException {
        bh.consume(withIterator());
    }

    @Benchmark
    public void withJackson(Blackhole bh) throws IOException {
        bh.consume(withJackson());
    }

    @Benchmark
    public void withDsljson(Blackhole bh) throws IOException {
        bh.consume(withDsljson());
    }

    private int withJsoniter() throws IOException {
        iter.reset(input);
        int[] arr = iter.read(typeLiteral);
        int total = 0;
        for (int i = 0; i < arr.length; i++) {
            total += arr[i];
        }
        return total;
    }

    private int withJackson() throws IOException {
        int[] arr = jackson.readValue(input, typeRef);
        int total = 0;
        for (int i = 0; i < arr.length; i++) {
            total += arr[i];
        }
        return total;
    }

    private int withDsljson() throws IOException {
        int[] arr = (int[]) dslJson.deserialize(int[].class, input, input.length);
        int total = 0;
        for (int i = 0; i < arr.length; i++) {
            total += arr[i];
        }
        return total;
    }

    private int withIterator() throws IOException {
        iter.reset(input);
        int total = 0;
        while (iter.readArray()) {
            total += iter.readInt();
        }
        return total;
    }
}
