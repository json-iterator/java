package com.jsoniter.annotation.jsoniter;

import com.jsoniter.*;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Type;

public class JsoniterAnnotationSupport implements FieldDecoderFactory {

    public static void enable() {
        Jsoniter.registerFieldDecoderFactory(new JsoniterAnnotationSupport());
    }

    @Override
    public Decoder createDecoder(final Field field) {
        JsonProperty annotation = field.getAnnotation(JsonProperty.class);
        if (annotation == null) {
            return null;
        }
        String alternativeField = annotation.value();
        if (alternativeField.equals(JsonProperty.USE_DEFAULT_NAME)) {
            alternativeField = field.getName();
        }
        final String[] alternativeFields = new String[]{alternativeField};
        final String fieldCacheKey = TypeLiteral.generateCacheKey(field.getGenericType());
        final Class<?> fieldType = field.getType();
        return new FieldDecoder() {
            @Override
            public String[] getAlternativeFieldNames() {
                return alternativeFields;
            }

            @Override
            public boolean useDefaultDecoder() {
                return false;
            }

            @Override
            public Object decode(Type type, Jsoniter iter) throws IOException {
                return iter.read(fieldCacheKey, fieldType);
            }
        };
    }
}
