package com.jsoniter;

import java.lang.reflect.Field;

public interface FieldDecoderFactory {
    /**
     * Customize field of certain kind, for example having certain annotation
     *
     * @param field the field reflection object
     * @return null, if no special customization needed
     */
    Decoder createDecoder(Field field);

    /**
     * Customize the field map to
     * @param field the field reflection object
     * @return null, if fallback to default behavior
     */
    String[] getAlternativeFieldNames(Field field);
}
