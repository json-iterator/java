package com.jsoniter.output;

import com.jsoniter.spi.*;
import com.jsoniter.any.Any;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class ReflectionObjectEncoder implements Encoder.ReflectionEncoder {

    private final ClassDescriptor desc;
    private final List<EncodeTo> fields = new ArrayList<EncodeTo>();
    private final List<EncodeTo> getters = new ArrayList<EncodeTo>();

    public ReflectionObjectEncoder(ClassInfo classInfo) {
        desc = ClassDescriptor.getEncodingClassDescriptor(classInfo, true);
        for (EncodeTo encodeTo : desc.encodeTos()) {
            Binding binding = encodeTo.binding;
            if (binding.encoder == null) {
                // the field encoder might be registered directly
                binding.encoder = JsoniterSpi.getEncoder(binding.encoderCacheKey());
            }
            if (binding.field != null) {
                fields.add(encodeTo);
            } else {
                getters.add(encodeTo);
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
        HashMap<String, Object> copied = new HashMap<String, Object>();
        try {
            for (EncodeTo encodeTo : fields) {
                Object val = encodeTo.binding.field.get(obj);
                copied.put(encodeTo.toName, val);
            }
            for (EncodeTo getter : getters) {
                Object val = getter.binding.method.invoke(obj);
                copied.put(getter.toName, val);
            }
        } catch (Exception e) {
            throw new JsonException(e);
        }
        return Any.wrap(copied);
    }

    private void enocde_(Object obj, JsonStream stream) throws Exception {
        if (obj == null) {
            stream.writeNull();
            return;
        }
        stream.writeObjectStart();
        boolean notFirst = false;
        for (EncodeTo encodeTo : fields) {
            Object val = encodeTo.binding.field.get(obj);
            notFirst = writeEncodeTo(stream, notFirst, encodeTo, val);
        }
        for (EncodeTo encodeTo : getters) {
            Object val = encodeTo.binding.method.invoke(obj);
            notFirst = writeEncodeTo(stream, notFirst, encodeTo, val);
        }
        for (UnwrapperDescriptor unwrapper : desc.unwrappers) {
            if (unwrapper.isMap) {
                Map<Object, Object> map = (Map<Object, Object>) unwrapper.method.invoke(obj);
                for (Map.Entry<Object, Object> entry : map.entrySet()) {
                    if (notFirst) {
                        stream.writeMore();
                    } else {
                        notFirst = true;
                    }
                    stream.writeObjectField(entry.getKey().toString());
                    stream.writeVal(unwrapper.mapValueTypeLiteral, entry.getValue());
                }
            } else {
                if (notFirst) {
                    stream.writeMore();
                } else {
                    notFirst = true;
                }
                unwrapper.method.invoke(obj, stream);
            }
        }
        if (notFirst) {
            stream.writeObjectEnd();
        } else {
            stream.write('}');
        }
    }

    private boolean writeEncodeTo(JsonStream stream, boolean notFirst, EncodeTo encodeTo, Object val) throws IOException {
        if (!(encodeTo.binding.shouldOmitNull && val == null)) {
            if (notFirst) {
                stream.writeMore();
            } else {
                stream.writeIndention();
                notFirst = true;
            }
            stream.writeObjectField(encodeTo.toName);
            if (encodeTo.binding.encoder != null) {
                encodeTo.binding.encoder.encode(val, stream);
            } else {
                stream.writeVal(val);
            }
        }
        return notFirst;
    }
}
