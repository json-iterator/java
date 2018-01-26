package com.jsoniter.any;

import com.jsoniter.ValueType;
import com.jsoniter.output.JsonStream;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;

class StringAny extends Any {

    private final static String FALSE = "false";
    private String val;

    public StringAny(String val) {
        this.val = val;
    }

    @Override
    public ValueType valueType() {
        return ValueType.STRING;
    }

    @Override
    public Object object() {
        return val;
    }

    public Any set(String newVal) {
        val = newVal;
        return this;
    }

    @Override
    public void writeTo(JsonStream stream) throws IOException {
        stream.writeVal(val);
    }

    @Override
    public boolean toBoolean() {
        int len = val.length();
        if (len == 0) {
            return false;
        }
        if (len == 5 && FALSE.equals(val)) {
            return false;
        }
        for (int i = 0; i < len; i++) {
            switch (val.charAt(i)) {
                case ' ':
                case '\t':
                case '\n':
                case '\r':
                    continue;
                default:
                    return true;
            }
        }
        return false;
    }

    @Override
    public int toInt() {
        return Integer.valueOf(val);
    }

    @Override
    public long toLong() {
        return Long.valueOf(val);
    }

    @Override
    public float toFloat() {
        return Float.valueOf(val);
    }

    @Override
    public double toDouble() {
        return Double.valueOf(val);
    }

    @Override
    public BigInteger toBigInteger() {
        return new BigInteger(val);
    }

    @Override
    public BigDecimal toBigDecimal() {
        return new BigDecimal(val);
    }

    @Override
    public String toString() {
        return val;
    }
}

