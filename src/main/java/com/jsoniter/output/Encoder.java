package com.jsoniter.output;

import java.io.IOException;

public interface Encoder {
    void encode(Object obj, com.jsoniter.output.JsonStream stream) throws IOException;
}
