package com.jsoniter.output;

import com.jsoniter.JsonException;
import com.jsoniter.spi.TypeLiteral;
import com.jsoniter.spi.Encoder;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.IdentityHashMap;
import java.util.Map;

class CodegenImplNative {
    public static final Map<Type, Encoder> NATIVE_ENCODERS = new IdentityHashMap<Type, Encoder>() {{
        put(boolean.class, new Encoder() {
            @Override
            public void encode(Object obj, JsonStream stream) throws IOException {
                stream.writeVal((Boolean) obj);
            }
        });
        put(Boolean.class, new Encoder() {
            @Override
            public void encode(Object obj, JsonStream stream) throws IOException {
                stream.writeVal((Boolean) obj);
            }
        });
        put(byte.class, new Encoder() {
            @Override
            public void encode(Object obj, JsonStream stream) throws IOException {
                stream.writeVal((Short) obj);
            }
        });
        put(Byte.class, new Encoder() {
            @Override
            public void encode(Object obj, JsonStream stream) throws IOException {
                stream.writeVal((Short) obj);
            }
        });
        put(short.class, new Encoder() {
            @Override
            public void encode(Object obj, JsonStream stream) throws IOException {
                stream.writeVal((Short) obj);
            }
        });
        put(Short.class, new Encoder() {
            @Override
            public void encode(Object obj, JsonStream stream) throws IOException {
                stream.writeVal((Short) obj);
            }
        });
        put(int.class, new Encoder() {
            @Override
            public void encode(Object obj, JsonStream stream) throws IOException {
                stream.writeVal((Integer) obj);
            }
        });
        put(Integer.class, new Encoder() {
            @Override
            public void encode(Object obj, JsonStream stream) throws IOException {
                stream.writeVal((Integer) obj);
            }
        });
        put(char.class, new Encoder() {
            @Override
            public void encode(Object obj, JsonStream stream) throws IOException {
                stream.writeVal((Integer) obj);
            }
        });
        put(Character.class, new Encoder() {
            @Override
            public void encode(Object obj, JsonStream stream) throws IOException {
                stream.writeVal((Integer) obj);
            }
        });
        put(long.class, new Encoder() {
            @Override
            public void encode(Object obj, JsonStream stream) throws IOException {
                stream.writeVal((Long) obj);
            }
        });
        put(Long.class, new Encoder() {
            @Override
            public void encode(Object obj, JsonStream stream) throws IOException {
                stream.writeVal((Long) obj);
            }
        });
        put(float.class, new Encoder() {
            @Override
            public void encode(Object obj, JsonStream stream) throws IOException {
                stream.writeVal((Float) obj);
            }
        });
        put(Float.class, new Encoder() {
            @Override
            public void encode(Object obj, JsonStream stream) throws IOException {
                stream.writeVal((Float) obj);
            }
        });
        put(double.class, new Encoder() {
            @Override
            public void encode(Object obj, JsonStream stream) throws IOException {
                stream.writeVal((Double) obj);
            }
        });
        put(Double.class, new Encoder() {
            @Override
            public void encode(Object obj, JsonStream stream) throws IOException {
                stream.writeVal((Double) obj);
            }
        });
        put(String.class, new Encoder() {
            @Override
            public void encode(Object obj, JsonStream stream) throws IOException {
                stream.writeVal((String) obj);
            }
        });
        put(Object.class, new Encoder() {
            @Override
            public void encode(Object obj, JsonStream stream) throws IOException {
                stream.writeVal(obj);
            }
        });
    }};

    public static String genWriteOp(String code, Type valueType) {
        if (NATIVE_ENCODERS.containsKey(valueType)) {
            return String.format("stream.writeVal((%s)%s);", getTypeName(valueType), code);
        }

        String cacheKey = TypeLiteral.create(valueType).getEncoderCacheKey();
        Codegen.getEncoder(cacheKey, valueType);
        return String.format("%s.encode_((%s)%s, stream);", cacheKey, getTypeName(valueType), code);
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
            throw new JsonException("unsupported type: " + fieldType);
        }
    }
}
