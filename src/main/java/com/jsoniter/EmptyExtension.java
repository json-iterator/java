package com.jsoniter;

import java.lang.reflect.Type;
import java.util.List;

public class EmptyExtension implements Extension {

    @Override
    public Decoder createDecoder(String cacheKey, Type type) {
        return null;
    }

    @Override
    public boolean updateBinding(Binding field) {
        return false;
    }

    @Override
    public CustomizedConstructor getConstructor(Class clazz) {
        return null;
    }

    @Override
    public List<CustomizedSetter> getSetters(Class clazz) {
        return null;
    }
}
