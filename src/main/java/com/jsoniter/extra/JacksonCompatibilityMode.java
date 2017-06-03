package com.jsoniter.extra;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.jsoniter.annotation.*;
import com.jsoniter.spi.Decoder;
import com.jsoniter.spi.Encoder;
import com.jsoniter.spi.JsoniterSpi;

import java.lang.annotation.Annotation;

public class JacksonCompatibilityMode extends JsoniterConfig {

    public static class Builder extends JsoniterConfig.Builder {
        public JacksonCompatibilityMode build() {
            return new JacksonCompatibilityMode(this);
        }
    }

    private JacksonCompatibilityMode(Builder builder) {
        super(builder);
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
            public boolean ignoreDecoding() {
                return jacksonObj.value();
            }

            @Override
            public boolean ignoreEncoding() {
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
                return "";
            }

            @Override
            public String[] from() {
                return new String[]{jacksonObj.value()};
            }

            @Override
            public String[] to() {
                return new String[]{jacksonObj.value()};
            }

            @Override
            public boolean required() {
                return jacksonObj.required();
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

    @Override
    protected JsonUnwrapper getJsonUnwrapper(Annotation[] annotations) {
        JsonUnwrapper jsoniterObj = super.getJsonUnwrapper(annotations);
        if (jsoniterObj != null) {
            return jsoniterObj;
        }
        JsonAnyGetter jacksonObj = getAnnotation(annotations, JsonAnyGetter.class);
        if (jacksonObj == null) {
            return null;
        }
        return new JsonUnwrapper(){
            @Override
            public Class<? extends Annotation> annotationType() {
                return JsonUnwrapper.class;
            }
        };
    }

    @Override
    protected JsonWrapper getJsonWrapper(Annotation[] annotations) {
        JsonWrapper jsoniterObj = super.getJsonWrapper(annotations);
        if (jsoniterObj != null) {
            return jsoniterObj;
        }
        JsonAnySetter jacksonObj = getAnnotation(annotations, JsonAnySetter.class);
        if (jacksonObj == null) {
            return null;
        }
        return new JsonWrapper(){
            @Override
            public JsonWrapperType value() {
                return JsonWrapperType.KEY_VALUE;
            }

            @Override
            public Class<? extends Annotation> annotationType() {
                return JsonWrapper.class;
            }
        };
    }
}
