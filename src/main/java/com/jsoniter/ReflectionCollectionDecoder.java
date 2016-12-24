package com.jsoniter;

import com.jsoniter.spi.Decoder;
import com.jsoniter.spi.TypeLiteral;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.util.Collection;

class ReflectionCollectionDecoder implements Decoder {
    private final TypeLiteral compTypeLiteral;
    private final Constructor ctor;

    public ReflectionCollectionDecoder(Class clazz, Type[] typeArgs) {
        try {
            ctor = clazz.getConstructor();
        } catch (NoSuchMethodException e) {
            throw new JsonException(e);
        }
        compTypeLiteral = TypeLiteral.create(typeArgs[0]);
    }

    @Override
    public Object decode(JsonIterator iter) throws IOException {
        try {
            return decode_(iter);
        } catch (Exception e) {
            throw new JsonException(e);
        }
    }

    private Object decode_(JsonIterator iter) throws Exception {
        Collection col = (Collection) CodegenAccess.resetExistingObject(iter);
        if (iter.readNull()) {
            return null;
        }
        if (col == null) {
            col = (Collection) this.ctor.newInstance();
        } else {
            col.clear();
        }
        while (iter.readArray()) {
            col.add(CodegenAccess.read(iter, compTypeLiteral));
        }
        return col;
    }
}
