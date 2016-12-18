package com.jsoniter.output;

import com.jsoniter.TypeLiteral;
import com.jsoniter.spi.Encoder;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
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

    public static String genWriteOp(String code, Type valueType) {
        if (NATIVE_ENCODERS.containsKey(valueType)) {
            return String.format("stream.writeVal((%s)%s);", getTypeName(valueType), code);
        }

        String cacheKey = TypeLiteral.generateEncoderCacheKey(valueType);
        Codegen.getEncoder(cacheKey, valueType);
//        Encoder encoder = Codegen.cache.get(cacheKey);
        return String.format("%s.encode_(%s, stream);", cacheKey, code);
    }

    public static String getTypeName(Type fieldType) {
        if (fieldType instanceof Class) {
            Class clazz = (Class) fieldType;
            return clazz.getCanonicalName();
        } else if (fieldType instanceof ParameterizedType) {
            ParameterizedType pType = (ParameterizedType) fieldType;
            Class clazz = (Class) pType.getRawType();
            return clazz.getCanonicalName();
        } else {
            throw new RuntimeException("unsupported type: " + fieldType);
        }
    }
}
