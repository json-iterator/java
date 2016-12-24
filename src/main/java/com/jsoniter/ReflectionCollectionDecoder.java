package com.jsoniter;

import com.jsoniter.spi.Decoder;
import com.jsoniter.spi.TypeLiteral;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collection;

public class ReflectionCollectionDecoder implements Decoder {
    private final Class clazz;
    private final TypeLiteral compTypeLiteral;

    public ReflectionCollectionDecoder(Class clazz, Type[] typeArgs) {
        this.clazz = clazz;
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
        if (iter.readNull()) {
            return null;
        }
        Collection col = (Collection) this.clazz.newInstance();
        while (iter.readArray()) {
            col.add(CodegenAccess.read(iter, compTypeLiteral));
        }
        return col;
    }
}
