package com.jsoniter;

import java.lang.reflect.Field;

public class EmptyExtension implements Extension {

    @Override
    public Decoder createDecoder(Field field) {
        return null;
    }

    @Override
    public String[] getAlternativeFieldNames(Field field) {
        return null;
    }
}
