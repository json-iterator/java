package com.jsoniter.demo;

import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.dslplatform.json.CompiledJson;
import com.dslplatform.json.DslJson;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.afterburner.AfterburnerModule;
import com.jsoniter.JsonIterator;
import com.jsoniter.ReflectionDecoder;
import com.jsoniter.spi.ExtensionManager;
import com.jsoniter.spi.TypeLiteral;
import org.junit.Test;
import org.openjdk.jmh.Main;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.BenchmarkParams;
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
    private TestObject testObject;

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
    public void benchSetup(BenchmarkParams params) {
        inputStr = "{'field1':100,'field2':101}";
        input = inputStr.replace('\'', '"').getBytes();
        iter = JsonIterator.parse(input);
        typeLiteral = new TypeLiteral<TestObject>() {
        };
        typeRef = new TypeReference<TestObject>() {
        };
        clazz = TestObject.class;
        jackson = new ObjectMapper();
        dslJson = new DslJson();
        testObject = new TestObject();
        if (params != null) {
            if (params.getBenchmark().contains("withReflection")) {
                ExtensionManager.registerTypeDecoder(TestObject.class, new ReflectionDecoder(TestObject.class));
            }
            if (params.getBenchmark().contains("withBindApiStrictMode")) {
                JsonIterator.enableStrictMode();
            }
            if (params.getBenchmark().contains("withJacksonAfterburner")) {
                jackson.registerModule(new AfterburnerModule());
            }
        }
    }

    @Test
    public void test() throws IOException {
        benchSetup(null);
        ExtensionManager.registerTypeDecoder(TestObject.class, new ReflectionDecoder(TestObject.class));
        System.out.println(withIterator());
        System.out.println(withIteratorIfElse());
        System.out.println(withIteratorIntern());
        System.out.println(withBindApi());
        System.out.println(withExistingObject());
        System.out.println(withJackson());
        System.out.println(withDsljson());
        System.out.println(withFastjson());
    }

    public static void main(String[] args) throws Exception {
        Main.main(new String[]{
                "SimpleObjectBinding.*",
                "-i", "5",
                "-wi", "5",
                "-f", "1"
        });
    }

//    @Benchmark
    public void withIterator(Blackhole bh) throws IOException {
        bh.consume(withIterator());
    }

//    @Benchmark
    public void withIteratorIfElse(Blackhole bh) throws IOException {
        bh.consume(withIteratorIfElse());
    }

//    @Benchmark
    public void withIteratorIntern(Blackhole bh) throws IOException {
        bh.consume(withIteratorIntern());
    }

//    @Benchmark
    public void withoutExistingObject(Blackhole bh) throws IOException {
        bh.consume(withBindApi());
    }

//    @Benchmark
    public void withBindApiStrictMode(Blackhole bh) throws IOException {
        bh.consume(withBindApi());
    }

    @Benchmark
    public void withReflection(Blackhole bh) throws IOException {
        bh.consume(withBindApi());
    }

//    @Benchmark
    public void withExistingObject(Blackhole bh) throws IOException {
        bh.consume(withExistingObject());
    }

//    @Benchmark
    public void withJacksonAfterburner(Blackhole bh) throws IOException {
        bh.consume(withJackson());
    }

//    @Benchmark
    public void withJacksonNoAfterburner(Blackhole bh) throws IOException {
        bh.consume(withJackson());
    }

//    @Benchmark
    public void withDsljson(Blackhole bh) throws IOException {
        bh.consume(withDsljson());
    }

//    @Benchmark
    public void withFastjson(Blackhole bh) throws IOException {
        bh.consume(withFastjson());
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

    private TestObject withIteratorIfElse() throws IOException {
        iter.reset();
        TestObject obj = new TestObject();
        for (String field = iter.readObject(); field != null; field = iter.readObject()) {
            if (field.equals("field1")) {
                obj.field1 = iter.readInt();
                continue;
            }
            if (field.equals("field2")) {
                obj.field2 = iter.readInt();
                continue;
            }
            iter.skip();
        }
        return obj;
    }

    private TestObject withIteratorIntern() throws IOException {
        iter.reset();
        TestObject obj = new TestObject();
        for (String field = iter.readObject(); field != null; field = iter.readObject()) {
            field = field.intern();
            if (field == "field1") {
                obj.field1 = iter.readInt();
                continue;
            }
            if (field == "field2") {
                obj.field2 = iter.readInt();
                continue;
            }
            iter.skip();
        }
        return obj;
    }

    private TestObject withBindApi() throws IOException {
        iter.reset();
        return iter.read(typeLiteral);
    }

    private TestObject withExistingObject() throws IOException {
        iter.reset();
        return iter.read(typeLiteral, testObject);
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
