package com.jsoniter.output;

import com.jsoniter.any.Any;
import com.jsoniter.spi.Encoder;

import java.io.IOException;

class ReflectionEnumEncoder implements Encoder {
    public ReflectionEnumEncoder(Class clazz) {
    }

    @Override
    public void encode(Object obj, JsonStream stream) throws IOException {
        stream.write('"');
        stream.writeRaw(obj.toString());
        stream.write('"');
    }

    @Override
    public Any wrap(Object obj) {
        return Any.wrap(obj.toString());
    }
}
