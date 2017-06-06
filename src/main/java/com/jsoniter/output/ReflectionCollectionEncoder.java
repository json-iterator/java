package com.jsoniter.output;

import com.jsoniter.any.Any;
import com.jsoniter.spi.Encoder;
import com.jsoniter.spi.TypeLiteral;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Iterator;

class ReflectionCollectionEncoder implements Encoder.ReflectionEncoder {

    private final TypeLiteral compTypeLiteral;

    public ReflectionCollectionEncoder(Class clazz, Type[] typeArgs) {
        if (typeArgs.length > 0) {
            compTypeLiteral = TypeLiteral.create(typeArgs[0]);
        } else {
            compTypeLiteral = TypeLiteral.create(Object.class);
        }
    }

    @Override
    public void encode(Object obj, JsonStream stream) throws IOException {
        if (null == obj) {
            stream.writeNull();
            return;
        }
        Collection col = (Collection) obj;
        Iterator iter = col.iterator();
        if (!iter.hasNext()) {
            stream.writeEmptyArray();
            return;
        }
        stream.writeArrayStart();
        stream.writeVal(compTypeLiteral, iter.next());
        while (iter.hasNext()) {
            stream.writeMore();
            stream.writeVal(compTypeLiteral, iter.next());
        }
        stream.writeArrayEnd();
    }

    @Override
    public Any wrap(Object obj) {
        Collection col = (Collection) obj;
        return Any.wrap(col);
    }
}
