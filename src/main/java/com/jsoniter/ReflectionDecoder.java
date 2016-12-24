package com.jsoniter;

import com.jsoniter.spi.Decoder;
import com.jsoniter.spi.TypeLiteral;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collection;

public class ReflectionDecoder {
    public static Decoder create(Class clazz, Type... typeArgs) {
        final TypeLiteral typeLiteral = TypeLiteral.create(clazz);
        TypeLiteral.NativeType nativeType = typeLiteral.getNativeType();
        if (nativeType != null) {
            return new Decoder() {
                @Override
                public Object decode(JsonIterator iter) throws IOException {
                    return CodegenAccess.read(iter, typeLiteral);
                }
            };
        }
        if (clazz.isArray()) {
            return new ReflectionArrayDecoder(clazz);
        }
        if (Collection.class.isAssignableFrom(clazz)) {
            return new ReflectionCollectionDecoder(clazz, typeArgs);
        }
        return new ReflectionObjectDecoder(clazz);
    }
}
