package com.jsoniter;

import com.jsoniter.any.Any;
import com.jsoniter.spi.DecodingMode;
import junit.framework.TestCase;

import java.math.BigDecimal;
import java.math.BigInteger;

public class TestNull extends TestCase {

    static {
//        JsonIterator.setMode(DecodingMode.DYNAMIC_MODE_AND_MATCH_FIELD_STRICTLY);
    }

    public static class TestObject1 {
        public Boolean field;
    }

    public void test_null_as_Boolean() {
        TestObject1 val = JsonIterator.deserialize("{\"field\":null}", TestObject1.class);
        assertNull(val.field);
    }

    public static class TestObject2 {
        public Float field;
    }

    public void test_null_as_Float() {
        TestObject2 val = JsonIterator.deserialize("{\"field\":null}", TestObject2.class);
        assertNull(val.field);
    }

    public static class TestObject3 {
        public Double field;
    }

    public void test_null_as_Double() {
        TestObject3 val = JsonIterator.deserialize("{\"field\":null}", TestObject3.class);
        assertNull(val.field);
    }

    public static class TestObject4 {
        public Byte field;
    }

    public void test_null_as_Byte() {
        TestObject4 val = JsonIterator.deserialize("{\"field\":null}", TestObject4.class);
        assertNull(val.field);
    }

    public static class TestObject5 {
        public Character field;
    }

    public void test_null_as_Character() {
        TestObject5 val = JsonIterator.deserialize("{\"field\":null}", TestObject5.class);
        assertNull(val.field);
    }

    public static class TestObject6 {
        public Short field;
    }

    public void test_null_as_Short() {
        TestObject6 val = JsonIterator.deserialize("{\"field\":null}", TestObject6.class);
        assertNull(val.field);
    }

    public static class TestObject7 {
        public Integer field;
    }

    public void test_null_as_Integer() {
        TestObject7 val = JsonIterator.deserialize("{\"field\":null}", TestObject7.class);
        assertNull(val.field);
    }

    public static class TestObject8 {
        public Long field;
    }

    public void test_null_as_Long() {
        TestObject8 val = JsonIterator.deserialize("{\"field\":null}", TestObject8.class);
        assertNull(val.field);
    }

    public static class TestObject9 {
        public BigDecimal field;
    }

    public void test_null_as_BigDecimal() {
        TestObject9 val = JsonIterator.deserialize("{\"field\":null}", TestObject9.class);
        assertNull(val.field);
    }

    public static class TestObject10 {
        public BigInteger field;
    }

    public void test_null_as_BigInteger() {
        TestObject10 val = JsonIterator.deserialize("{\"field\":null}", TestObject10.class);
        assertNull(val.field);
    }

    public static class TestObject11 {
        public Any field;
    }

    public void test_null_as_Any() {
        TestObject11 val = JsonIterator.deserialize("{\"field\":null}", TestObject11.class);
        assertNull(val.field.object());
    }
}
