package com.jsoniter.spi;

import com.jsoniter.any.Any;
import com.jsoniter.output.JsonStream;

import java.io.IOException;

public class EmptyEncoder implements Encoder {

    @Override
    public void encode(Object obj, JsonStream stream) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Any wrap(Object obj) {
        throw new UnsupportedOperationException();
    }
}
