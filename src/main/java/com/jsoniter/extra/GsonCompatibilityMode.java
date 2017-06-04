package com.jsoniter.extra;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.jsoniter.annotation.JsonIgnore;
import com.jsoniter.annotation.JsonProperty;
import com.jsoniter.spi.Config;
import com.jsoniter.spi.*;

import java.lang.annotation.Annotation;

public class GsonCompatibilityMode extends Config {

    private GsonCompatibilityMode(String configName, Builder builder) {
        super(configName, builder);
    }

    protected Builder builder() {
        return (Builder) super.builder();
    }

    public static class Builder extends Config.Builder {
        private boolean excludeFieldsWithoutExposeAnnotation = false;

        public Builder excludeFieldsWithoutExposeAnnotation() {
            excludeFieldsWithoutExposeAnnotation = true;
            return this;
        }

        public GsonCompatibilityMode build() {
            return (GsonCompatibilityMode) super.build();
        }

        @Override
        protected Config doBuild(String configName) {
            return new GsonCompatibilityMode(configName, this);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            if (!super.equals(o)) return false;

            Builder builder = (Builder) o;

            return excludeFieldsWithoutExposeAnnotation == builder.excludeFieldsWithoutExposeAnnotation;
        }

        @Override
        public int hashCode() {
            int result = super.hashCode();
            result = 31 * result + (excludeFieldsWithoutExposeAnnotation ? 1 : 0);
            return result;
        }
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
        if (builder().excludeFieldsWithoutExposeAnnotation) {
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
