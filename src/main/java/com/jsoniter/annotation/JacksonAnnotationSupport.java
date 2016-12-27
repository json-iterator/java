package com.jsoniter.annotation;

import com.jsoniter.spi.Decoder;
import com.jsoniter.spi.ExtensionManager;

import java.lang.annotation.Annotation;

public class JacksonAnnotationSupport extends JsoniterAnnotationSupport {

    public static void enable() {
        ExtensionManager.registerExtension(new JacksonAnnotationSupport());
    }

    @Override
    protected JsonIgnore getJsonIgnore(Annotation[] annotations) {
        JsonIgnore jsoniterObj = super.getJsonIgnore(annotations);
        if (jsoniterObj != null) {
            return jsoniterObj;
        }
        final com.fasterxml.jackson.annotation.JsonIgnore jacksonObj = getAnnotation(
                annotations, com.fasterxml.jackson.annotation.JsonIgnore.class);
        if (jacksonObj == null) {
            return null;
        }
        return new JsonIgnore() {
            @Override
            public boolean value() {
                return jacksonObj.value();
            }

            @Override
            public Class<? extends Annotation> annotationType() {
                return JsonIgnore.class;
            }
        };
    }

    @Override
    protected JsonProperty getJsonProperty(Annotation[] annotations) {
        JsonProperty jsoniterObj = super.getJsonProperty(annotations);
        if (jsoniterObj != null) {
            return jsoniterObj;
        }
        final com.fasterxml.jackson.annotation.JsonProperty jacksonObj = getAnnotation(
                annotations, com.fasterxml.jackson.annotation.JsonProperty.class);
        if (jacksonObj == null) {
            return null;
        }
        return new JsonProperty() {
            @Override
            public String value() {
                return jacksonObj.value();
            }

            @Override
            public String[] from() {
                return new String[0];
            }

            @Override
            public boolean required() {
                return jacksonObj.required();
            }

            @Override
            public Class<? extends Decoder> decoder() {
                return null;
            }

            @Override
            public Class<? extends Annotation> annotationType() {
                return JsonProperty.class;
            }
        };
    }

    @Override
    protected JsonCreator getJsonCreator(Annotation[] annotations) {
        JsonCreator jsoniterObj = super.getJsonCreator(annotations);
        if (jsoniterObj != null) {
            return jsoniterObj;
        }
        com.fasterxml.jackson.annotation.JsonCreator jacksonObj = getAnnotation(
                annotations, com.fasterxml.jackson.annotation.JsonCreator.class);
        if (jacksonObj == null) {
            return null;
        }
        return new JsonCreator() {
            @Override
            public Class<? extends Annotation> annotationType() {
                return JsonCreator.class;
            }
        };
    }
}
