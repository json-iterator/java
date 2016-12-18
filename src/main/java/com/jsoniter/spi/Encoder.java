package com.jsoniter.spi;

import com.jsoniter.output.JsonStream;

import java.io.IOException;

public interface Encoder {
    void encode(Object obj, JsonStream stream) throws IOException;
}
