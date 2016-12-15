package com.jsoniter;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.List;

public class EmptyExtension implements Extension {

    @Override
    public Decoder createDecoder(Binding field) {
        return null;
    }

    @Override
    public String[] getAlternativeFieldNames(Binding field) {
        return null;
    }

    @Override
    public CustomizedConstructor getConstructor(Class clazz) {
        return null;
    }
}
