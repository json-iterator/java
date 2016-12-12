package com.jsoniter.annotation.jsoniter;

import com.jsoniter.Decoder;
import com.jsoniter.FieldDecoderFactory;
import com.jsoniter.Jsoniter;

import java.lang.reflect.Field;

public class JsoniterAnnotationSupport implements FieldDecoderFactory {

    public static void enable() {
        Jsoniter.registerFieldDecoderFactory(new JsoniterAnnotationSupport());
    }

    @Override
    public Decoder createDecoder(final Field field) {
        return null;
    }

    @Override
    public String[] getAlternativeFieldNames(Field field) {
        JsonProperty annotation = field.getAnnotation(JsonProperty.class);
        if (annotation == null) {
            return null;
        }
        String alternativeField = annotation.value();
        if (alternativeField.equals(JsonProperty.USE_DEFAULT_NAME)) {
            alternativeField = field.getName();
        }
        final String[] alternativeFields = new String[]{alternativeField};
        return alternativeFields;
    }
}
