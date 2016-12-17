package com.jsoniter.output;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.IdentityHashMap;
import java.util.Map;

class CodegenImplNative {
    public static final Map<Type, Encoder> NATIVE_ENCODERS = new IdentityHashMap<Type, Encoder>() {{
        put(String.class, new Encoder() {
            @Override
            public void encode(Object obj, JsonStream stream) throws IOException {
                stream.writeVal((String) obj);
            }
        });
    }};
}
