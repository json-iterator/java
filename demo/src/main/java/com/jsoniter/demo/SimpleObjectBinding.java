package com.jsoniter.demo;

import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.dslplatform.json.CompiledJson;
import com.dslplatform.json.DslJson;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.afterburner.AfterburnerModule;
import com.jsoniter.JsonIterator;
import com.jsoniter.spi.TypeLiteral;
import org.junit.Test;
import org.openjdk.jmh.Main;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.io.IOException;

@State(Scope.Thread)
public class SimpleObjectBinding {

    private TypeLiteral<TestObject> typeLiteral;
    private ObjectMapper jackson;
    private byte[] input;
    private TypeReference<TestObject> typeRef;
    private DslJson dslJson;
    private Class<TestObject> clazz;
    private String inputStr;

    @CompiledJson
    public static class TestObject {
        public int field1;
        public int field2;

        @Override
        public String toString() {
            return "TestObject{" +
                    "field1=" + field1 +
                    ", field2=" + field2 +
                    '}';
        }
    }


    private JsonIterator iter;

    @Setup(Level.Trial)
    public void benchSetup() {
        inputStr = "{'field1':100,'field2':101}";
        input = inputStr.replace('\'', '"').getBytes();
        iter = JsonIterator.parse(input);
        typeLiteral = new TypeLiteral<TestObject>() {
        };
        typeRef = new TypeReference<TestObject>() {
        };
        clazz = TestObject.class;
        jackson = new ObjectMapper();
        jackson.registerModule(new AfterburnerModule());
        dslJson = new DslJson();
    }

    @Test
    public void test() throws IOException {
        benchSetup();
        System.out.println(withIterator());
        System.out.println(withBindApiNoneStrictMode());
        System.out.println(withBindApiStrictMode());
        System.out.println(withJackson());
        System.out.println(withDsljson());
        System.out.println(withFastjson());
    }

    @Benchmark
    public void withIterator(Blackhole bh) throws IOException {
        bh.consume(withIterator());
    }

    @Benchmark
    public void withBindApiNoneStrictMode(Blackhole bh) throws IOException {
        bh.consume(withBindApiNoneStrictMode());
    }

    @Benchmark
    public void withBindApiStrictMode(Blackhole bh) throws IOException {
        bh.consume(withBindApiStrictMode());
    }

    @Benchmark
    public void withJackson(Blackhole bh) throws IOException {
        bh.consume(withJackson());
    }

    @Benchmark
    public void withDsljson(Blackhole bh) throws IOException {
        bh.consume(withDsljson());
    }

    @Benchmark
    public void withFastjson(Blackhole bh) throws IOException {
        bh.consume(withFastjson());
    }

    public static void main(String[] args) throws Exception {
        Main.main(new String[]{
                "SimpleObjectBinding.*",
                "-i", "5",
                "-wi", "5",
                "-f", "1"
        });
    }

    private TestObject withIterator() throws IOException {
        iter.reset();
        TestObject obj = new TestObject();
        for (String field = iter.readObject(); field != null; field = iter.readObject()) {
            switch (field) {
                case "field1":
                    obj.field1 = iter.readInt();
                    continue;
                case "field2":
                    obj.field2 = iter.readInt();
                    continue;
                default:
                    iter.skip();
            }
        }
        return obj;
    }

    private TestObject withBindApiNoneStrictMode() throws IOException {
        iter.reset();
        return iter.read(typeLiteral);
    }

    private TestObject withBindApiStrictMode() throws IOException {
        JsonIterator.enableStrictMode();
        iter.reset();
        return iter.read(typeLiteral);
    }

    private TestObject withJackson() throws IOException {
        return jackson.readValue(input, typeRef);
    }

    private TestObject withDsljson() throws IOException {
        return (TestObject) dslJson.deserialize(clazz, input, input.length);
    }

    private TestObject withFastjson() {
        return new DefaultJSONParser(inputStr).parseObject(TestObject.class);
    }
}
