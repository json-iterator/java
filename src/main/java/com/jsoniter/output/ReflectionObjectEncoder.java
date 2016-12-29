package com.jsoniter.output;

import com.jsoniter.JsonException;
import com.jsoniter.spi.Binding;
import com.jsoniter.spi.ClassDescriptor;
import com.jsoniter.spi.Encoder;
import com.jsoniter.spi.JsoniterSpi;

import java.io.IOException;

class ReflectionObjectEncoder implements Encoder {

    private final ClassDescriptor desc;

    public ReflectionObjectEncoder(Class clazz) {
        desc = JsoniterSpi.getEncodingClassDescriptor(clazz, true);
    }

    @Override
    public void encode(Object obj, JsonStream stream) throws IOException {
        try {
            enocde_(obj, stream);
        } catch (Exception e) {
            throw new JsonException(e);
        }
    }

    private void enocde_(Object obj, JsonStream stream) throws Exception {
        if (obj == null) {
            stream.writeNull();
            return;
        }
        stream.writeObjectStart();
        boolean notFirst = false;
        for (Binding field : desc.fields) {
            Object val = field.field.get(obj);
            for (String toName : field.toNames) {
                if (notFirst) {
                    stream.writeMore();
                } else {
                    notFirst = true;
                }
                stream.writeObjectField(toName);
                stream.writeVal(val);
            }
        }
        for (Binding getter : desc.getters) {
            Object val = getter.method.invoke(obj);
            for (String toName : getter.toNames) {
                if (notFirst) {
                    stream.writeMore();
                } else {
                    notFirst = true;
                }
                stream.writeObjectField(toName);
                stream.writeVal(val);
            }
        }
        stream.writeObjectEnd();
    }
}
