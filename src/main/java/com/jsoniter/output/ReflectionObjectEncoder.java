package com.jsoniter.output;

import com.jsoniter.JsonException;
import com.jsoniter.any.Any;
import com.jsoniter.spi.Binding;
import com.jsoniter.spi.ClassDescriptor;
import com.jsoniter.spi.Encoder;
import com.jsoniter.spi.JsoniterSpi;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;

class ReflectionObjectEncoder implements Encoder {

    private final ClassDescriptor desc;

    public ReflectionObjectEncoder(Class clazz) {
        desc = JsoniterSpi.getEncodingClassDescriptor(clazz, true);
        for (Binding binding : desc.allEncoderBindings()) {
            if (binding.encoder == null) {
                // the field encoder might be registered directly
                binding.encoder = JsoniterSpi.getEncoder(binding.encoderCacheKey());
            }
        }
    }

    @Override
    public void encode(Object obj, JsonStream stream) throws IOException {
        try {
            enocde_(obj, stream);
        } catch (Exception e) {
            throw new JsonException(e);
        }
    }

    @Override
    public Any wrap(Object obj) {
        HashMap<String, Any> copied = new HashMap<String, Any>();
        try {
            for (Binding field : desc.fields) {
                Object val = field.field.get(obj);
                for (String toName : field.toNames) {
                    copied.put(toName, JsonStream.wrap(val));
                }
            }
            for (Binding getter : desc.getters) {
                Object val = getter.method.invoke(obj);
                for (String toName : getter.toNames) {
                    copied.put(toName, JsonStream.wrap(val));
                }
            }
        } catch (Exception e) {
            throw new JsonException(e);
        }
        return Any.wrapAnyMap(copied);
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
                if (field.encoder != null) {
                    field.encoder.encode(val, stream);
                } else {
                    stream.writeVal(val);
                }
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
                if (getter.encoder != null) {
                    getter.encoder.encode(val, stream);
                } else {
                    stream.writeVal(val);
                }
            }
        }
        for (Method unwrapper : desc.unWrappers) {
            if (notFirst) {
                stream.writeMore();
            } else {
                notFirst = true;
            }
            unwrapper.invoke(obj, stream);
        }
        stream.writeObjectEnd();
    }
}
