package com.jsoniter.demo;

import com.jsoniter.DecodingMode;
import com.jsoniter.JsonException;
import com.jsoniter.JsonIterator;
import com.jsoniter.annotation.JsonProperty;
import com.jsoniter.annotation.JsonUnknownProperties;
import com.jsoniter.annotation.JsoniterAnnotationSupport;
import com.jsoniter.spi.TypeLiteral;
import org.junit.Assert;
import org.junit.Test;
import org.openjdk.jmh.Main;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.io.IOException;

@State(Scope.Thread)
public class FieldMatching {
    private TypeLiteral<TestObject0> testObject0Type;
    private TypeLiteral<TestObject1> testObject1Type;
    private TypeLiteral<TestObject2> testObject2Type;
    private TypeLiteral<TestObject3> testObject3Type;
    private TypeLiteral<TestObject4> testObject4Type;
    private JsonIterator iter0;
    private JsonIterator iter1Success;

    public static class TestObject0 {
        public int field1;
        public int field2;
        public int field3;
    }

    public static class TestObject1 {
        @JsonProperty(required = true)
        public int field1;
        @JsonProperty(required = true)
        public int field2;
        @JsonProperty(required = true)
        public int field3;
    }

    @JsonUnknownProperties(failOnUnkown = true)
    public static class TestObject2 {
        public int field1;
        public int field2;
    }

    @JsonUnknownProperties(failOnUnkown = true, whitelist = {"field2"})
    public static class TestObject3 {
        public int field1;
    }

    @JsonUnknownProperties(blacklist = {"field3"})
    public static class TestObject4 {
        public int field1;
    }

    @Setup(Level.Trial)
    public void benchSetup() {
        JsoniterAnnotationSupport.enable();
        JsonIterator.setMode(DecodingMode.DYNAMIC_MODE_AND_MATCH_FIELD_STRICTLY);
        iter0 = JsonIterator.parse("{'field1':101,'field2':101,'field3':101}".replace('\'', '"').getBytes());
        iter1Success = JsonIterator.parse("{'field1':101,'field2':101,'field3':101}".replace('\'', '"').getBytes());
        testObject0Type = new TypeLiteral<TestObject0>() {
        };
        testObject1Type = new TypeLiteral<TestObject1>() {
        };
        testObject2Type = new TypeLiteral<TestObject2>() {
        };
        testObject3Type = new TypeLiteral<TestObject3>() {
        };
        testObject4Type = new TypeLiteral<TestObject4>() {
        };
    }

    @Test
    public void test() throws IOException {
        benchSetup();
        try {
            JsonIterator iter1Failure = JsonIterator.parse("{'field2':101}".replace('\'', '"').getBytes());
            iter1Failure.read(testObject1Type);
            Assert.fail();
        } catch (JsonException e) {
            System.out.println(e);
        }
        try {
            JsonIterator iter2Failure = JsonIterator.parse("{'field1':101,'field2':101,'field3':101}".replace('\'', '"').getBytes());
            iter2Failure.read(testObject2Type);
            Assert.fail();
        } catch (JsonException e) {
            System.out.println(e);
        }
        try {
            JsonIterator iter3Failure = JsonIterator.parse("{'field1':101,'field2':101,'field3':101}".replace('\'', '"').getBytes());
            iter3Failure.read(testObject3Type);
            Assert.fail();
        } catch (JsonException e) {
            System.out.println(e);
        }
        try {
            JsonIterator iter4Failure = JsonIterator.parse("{'field1':101,'field2':101,'field3':101}".replace('\'', '"').getBytes());
            iter4Failure.read(testObject4Type);
            Assert.fail();
        } catch (JsonException e) {
            System.out.println(e);
        }
    }

    public static void main(String[] args) throws Exception {
        Main.main(new String[]{
                "FieldMatching",
                "-i", "5",
                "-wi", "5",
                "-f", "1",
        });
    }

    @Benchmark
    public void iter0(Blackhole bh) throws IOException {
        iter0.reset();
        bh.consume(iter0.read(testObject0Type));
    }

    @Benchmark
    public void iter1Success(Blackhole bh) throws IOException {
        iter1Success.reset();
        bh.consume(iter1Success.read(testObject1Type));
    }
}
