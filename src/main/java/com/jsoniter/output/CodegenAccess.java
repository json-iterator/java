package com.jsoniter.output;

import com.jsoniter.spi.ExtensionManager;

import java.io.IOException;

public class CodegenAccess {
    public static void writeVal(String cacheKey, Object obj, JsonStream stream) throws IOException {
        ExtensionManager.getEncoder(cacheKey).encode(obj, stream);
    }
}
