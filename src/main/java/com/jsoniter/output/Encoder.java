package com.jsoniter.output;

public interface Encoder {
    void encode(Object obj, com.jsoniter.output.JsonStream stream);
}
