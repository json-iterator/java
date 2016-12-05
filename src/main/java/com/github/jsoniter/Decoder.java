package com.github.jsoniter;

import java.io.IOException;

public interface Decoder {
    Object decode(Class clazz, Jsoniter iter) throws IOException;
}
