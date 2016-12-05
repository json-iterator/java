package com.github.jsoniter;

import java.io.IOException;

public interface Decoder {
    void decode(Object obj, Jsoniter iter) throws IOException;
}
