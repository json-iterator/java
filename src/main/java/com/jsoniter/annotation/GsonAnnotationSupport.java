package com.jsoniter.annotation;

import com.google.gson.annotations.SerializedName;
import com.jsoniter.spi.Decoder;
import com.jsoniter.spi.Encoder;
import com.jsoniter.spi.JsoniterSpi;

import java.lang.annotation.Annotation;

public class GsonAnnotationSupport extends JsoniterAnnotationSupport {

    private final static GsonAnnotationSupport INSTANCE = new GsonAnnotationSupport();

    public static void enable() {
        JsoniterSpi.registerExtension(INSTANCE);
    }

    public static void disable() {
        JsoniterSpi.deregisterExtension(INSTANCE);
    }

    @Override
    protected JsonProperty getJsonProperty(Annotation[] annotations) {

        JsonProperty jsoniterObj = super.getJsonProperty(annotations);
        if (jsoniterObj != null) {
            return jsoniterObj;
        }
        final SerializedName gsonObj = getAnnotation(
                annotations, SerializedName.class);
        if (gsonObj == null) {
            return null;
        }
        return new JsonProperty() {

            @Override
            public String value() {
                return "";
            }

            @Override
            public String[] from() {
                return new String[]{gsonObj.value()};
            }

            @Override
            public String[] to() {
                return new String[]{gsonObj.value()};
            }

            @Override
            public boolean required() {
                return false;
            }

            @Override
            public Class<? extends Decoder> decoder() {
                return Decoder.class;
            }

            @Override
            public Class<?> implementation() {
                return Object.class;
            }

            @Override
            public Class<? extends Encoder> encoder() {
                return Encoder.class;
            }

            @Override
            public boolean nullable() {
                return true;
            }

            @Override
            public boolean collectionValueNullable() {
                return true;
            }

            @Override
            public boolean omitNull() {
                return true;
            }

            @Override
            public Class<? extends Annotation> annotationType() {
                return JsonProperty.class;
            }
        };
    }
}
