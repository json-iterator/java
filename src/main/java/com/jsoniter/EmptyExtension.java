package com.jsoniter;

import java.util.List;

public class EmptyExtension implements Extension {

    @Override
    public Decoder createDecoder(Binding field) {
        return null;
    }

    @Override
    public String[] getBindFrom(Binding field) {
        return null;
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
