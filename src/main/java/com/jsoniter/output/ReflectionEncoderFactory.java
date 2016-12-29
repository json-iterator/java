package com.jsoniter.output;

import com.jsoniter.spi.Encoder;

import java.lang.reflect.Type;

public class ReflectionEncoderFactory {

    public static Encoder create(Class clazz, Type... typeArgs) {
        return new ReflectionObjectEncoder(clazz);
    }
}
