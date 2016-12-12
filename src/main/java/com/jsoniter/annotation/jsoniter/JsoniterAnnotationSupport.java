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
        JsonIgnore jsonIgnore = field.getAnnotation(JsonIgnore.class);
        if (jsonIgnore != null) {
            return new String[0];
        }
        JsonProperty jsonProperty = field.getAnnotation(JsonProperty.class);
        if (jsonProperty != null) {
            String alternativeField = jsonProperty.value();
            if (alternativeField.equals(JsonProperty.USE_DEFAULT_NAME)) {
                alternativeField = field.getName();
            }
            return new String[]{alternativeField};
        }
        return null;
    }
}
