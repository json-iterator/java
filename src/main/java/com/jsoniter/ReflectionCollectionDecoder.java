package com.jsoniter;

import com.jsoniter.spi.Decoder;
import com.jsoniter.spi.JsonException;
import com.jsoniter.spi.TypeLiteral;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.util.Collection;

class ReflectionCollectionDecoder implements Decoder {
    private final Constructor ctor;
    private final Decoder compTypeDecoder;

    public ReflectionCollectionDecoder(Class clazz, Type[] typeArgs) {
        try {
            ctor = clazz.getConstructor();
        } catch (NoSuchMethodException e) {
            throw new JsonException(e);
        }
        compTypeDecoder = Codegen.getDecoder(TypeLiteral.create(typeArgs[0]).getDecoderCacheKey(), typeArgs[0]);
    }

    @Override
    public Object decode(JsonIterator iter) throws IOException {
        try {
            return decode_(iter);
        }  catch (JsonException e) {
            throw e;
        } catch (Exception e) {
            throw new JsonException(e);
        }
    }

    private Object decode_(JsonIterator iter) throws Exception {
        Collection col = (Collection) CodegenAccess.resetExistingObject(iter);
        if (iter.readNull()) {
            return null;
        }
        col = (Collection) this.ctor.newInstance();
        while (iter.readArray()) {
            col.add(compTypeDecoder.decode(iter));
        }
        return col;
    }
}
