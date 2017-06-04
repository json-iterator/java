package com.jsoniter.spi;

import java.lang.reflect.Type;

public interface Config extends Extension {
    String configName();
    String getDecoderCacheKey(Type type);
    String getEncoderCacheKey(Type type);
}
