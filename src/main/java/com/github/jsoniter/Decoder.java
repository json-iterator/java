package com.github.jsoniter;

import java.io.IOException;
import java.lang.reflect.Type;

public interface Decoder {
    Object decode(Type type, Jsoniter iter) throws IOException;
}
