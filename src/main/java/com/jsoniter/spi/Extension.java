package com.jsoniter.spi;

import java.lang.reflect.Type;

public interface Extension {
    /**
     * Customize type decoding
     *
     * @param cacheKey
     * @param type change how to decode the type
     * @return null, if no special customization needed
     */
    Decoder createDecoder(String cacheKey, Type type);

    /**
     * Update binding is done for the class
     *
     * @param desc binding information
     */
    void updateClassDescriptor(ClassDescriptor desc);
}
