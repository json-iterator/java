package com.jsoniter.extra;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.jsoniter.annotation.JsonIgnore;
import com.jsoniter.annotation.JsonProperty;
import com.jsoniter.annotation.JsoniterAnnotationSupport;
import com.jsoniter.spi.*;

import java.lang.annotation.Annotation;

public class GsonCompatibilityMode extends JsoniterAnnotationSupport {

    private Builder builder;

    private GsonCompatibilityMode(Builder builder) {
        this.builder = builder;
    }

    public static class Builder {
        private boolean excludeFieldsWithoutExposeAnnotation = false;

        public Builder excludeFieldsWithoutExposeAnnotation() {
            excludeFieldsWithoutExposeAnnotation = true;
            return this;
        }

        public GsonCompatibilityMode build() {
            return new GsonCompatibilityMode(this);
        }
    }

    private final static GsonCompatibilityMode INSTANCE = new GsonCompatibilityMode(new Builder());

    public static void enable() {
        JsoniterSpi.registerExtension(INSTANCE);
    }

    public static void disable() {
        JsoniterSpi.deregisterExtension(INSTANCE);
    }

    @Override
    public void updateClassDescriptor(ClassDescriptor desc) {
        super.updateClassDescriptor(desc);
        removeGetterAndSetter(desc);
    }

    private void removeGetterAndSetter(ClassDescriptor desc) {
        for (Binding binding : desc.allBindings()) {
            if (binding.method != null) {
                binding.toNames = new String[0];
                binding.fromNames = new String[0];
            }
        }
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

    @Override
    protected JsonIgnore getJsonIgnore(Annotation[] annotations) {

        JsonIgnore jsoniterObj = super.getJsonIgnore(annotations);
        if (jsoniterObj != null) {
            return jsoniterObj;
        }
        final Expose gsonObj = getAnnotation(
                annotations, Expose.class);
        if (gsonObj != null) {
            return new JsonIgnore() {
                @Override
                public boolean ignoreDecoding() {
                    return !gsonObj.deserialize();
                }

                @Override
                public boolean ignoreEncoding() {
                    return !gsonObj.serialize();
                }

                @Override
                public Class<? extends Annotation> annotationType() {
                    return JsonIgnore.class;
                }
            };
        }
        if (builder.excludeFieldsWithoutExposeAnnotation) {
            return new JsonIgnore() {
                @Override
                public boolean ignoreDecoding() {
                    return true;
                }

                @Override
                public boolean ignoreEncoding() {
                    return true;
                }

                @Override
                public Class<? extends Annotation> annotationType() {
                    return JsonIgnore.class;
                }
            };
        }
        return null;
    }
}
