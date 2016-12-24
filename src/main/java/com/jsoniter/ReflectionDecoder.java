package com.jsoniter;

import com.jsoniter.spi.Decoder;
import com.jsoniter.spi.TypeLiteral;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class ReflectionDecoder {
    public static Decoder create(Type type) {
        final TypeLiteral typeLiteral = TypeLiteral.create(type);
        TypeLiteral.NativeType nativeType = typeLiteral.getNativeType();
        if (nativeType != null) {
            return new Decoder() {
                @Override
                public Object decode(JsonIterator iter) throws IOException {
                    return CodegenAccess.read(iter, typeLiteral);
                }
            };
        }
        Type[] typeArgs = new Type[0];
        Class clazz;
        if (type instanceof ParameterizedType) {
            ParameterizedType pType = (ParameterizedType) type;
            clazz = (Class) pType.getRawType();
            typeArgs = pType.getActualTypeArguments();
        } else {
            clazz = (Class) type;
        }
        if (clazz.isArray()) {
            return new ReflectionArrayDecoder(clazz);
        }
        return new ReflectionObjectDecoder(clazz);
    }
}
