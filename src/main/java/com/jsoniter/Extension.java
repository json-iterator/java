package com.jsoniter;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.List;

public interface Extension {
    /**
     * Customize field of certain kind, for example having certain annotation
     *
     * @param field the field reflection object
     * @return null, if no special customization needed
     */
    Decoder createDecoder(Binding field);

    /**
     * Customize the field map to
     *
     * @param field the field reflection object
     * @return null, if fallback to default behavior
     */
    String[] getAlternativeFieldNames(Binding field);

    /**
     * customize which constructor to call
     * @param clazz the instance class to create
     * @return null, if fallback to default behavior
     */
    CustomizedConstructor getConstructor(Class clazz);
}
