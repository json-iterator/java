package com.jsoniter.extra;

import com.google.gson.FieldNamingStrategy;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.jsoniter.JsonIterator;
import com.jsoniter.annotation.JsonIgnore;
import com.jsoniter.annotation.JsonProperty;
import com.jsoniter.output.JsonStream;
import com.jsoniter.spi.Config;
import com.jsoniter.spi.*;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class GsonCompatibilityMode extends Config {

    private GsonCompatibilityMode(String configName, Builder builder) {
        super(configName, builder);
    }

    protected Builder builder() {
        return (Builder) super.builder();
    }

    public static class Builder extends Config.Builder {
        private boolean excludeFieldsWithoutExposeAnnotation = false;
        private boolean serializeNulls = false;
        private ThreadLocal<DateFormat> dateFormat = new ThreadLocal<DateFormat>() {
            @Override
            protected DateFormat initialValue() {
                return DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, Locale.US);
            }
        };
        private FieldNamingStrategy fieldNamingStrategy;

        public Builder excludeFieldsWithoutExposeAnnotation() {
            excludeFieldsWithoutExposeAnnotation = true;
            return this;
        }

        public Builder serializeNulls() {
            serializeNulls = true;
            return this;
        }

        public Builder setDateFormat(int dateStyle) {
            // no op, same as gson
            return this;
        }

        public Builder setDateFormat(final int dateStyle, final int timeStyle) {
            dateFormat = new ThreadLocal<DateFormat>() {
                @Override
                protected DateFormat initialValue() {
                    return DateFormat.getDateTimeInstance(dateStyle, timeStyle, Locale.US);
                }
            };
            return this;
        }

        public Builder setDateFormat(final String pattern) {
            dateFormat = new ThreadLocal<DateFormat>() {
                @Override
                protected DateFormat initialValue() {
                    return new SimpleDateFormat(pattern, Locale.US);
                }
            };
            return this;
        }

        public Builder setFieldNamingStrategy(FieldNamingStrategy fieldNamingStrategy) {
            this.fieldNamingStrategy = fieldNamingStrategy;
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

            if (excludeFieldsWithoutExposeAnnotation != builder.excludeFieldsWithoutExposeAnnotation) return false;
            if (serializeNulls != builder.serializeNulls) return false;
            if (!dateFormat.get().equals(builder.dateFormat.get())) return false;
            return fieldNamingStrategy != null ? fieldNamingStrategy.equals(builder.fieldNamingStrategy) : builder.fieldNamingStrategy == null;
        }

        @Override
        public int hashCode() {
            int result = super.hashCode();
            result = 31 * result + (excludeFieldsWithoutExposeAnnotation ? 1 : 0);
            result = 31 * result + (serializeNulls ? 1 : 0);
            result = 31 * result + dateFormat.get().hashCode();
            result = 31 * result + (fieldNamingStrategy != null ? fieldNamingStrategy.hashCode() : 0);
            return result;
        }
    }

    @Override
    public Encoder createEncoder(String cacheKey, Type type) {
        if (Date.class == type) {
            return new Encoder() {
                @Override
                public void encode(Object obj, JsonStream stream) throws IOException {
                    DateFormat dateFormat = builder().dateFormat.get();
                    stream.writeVal(dateFormat.format(obj));
                }
            };
        }
        return super.createEncoder(cacheKey, type);
    }

    @Override
    public Decoder createDecoder(String cacheKey, Type type) {
        if (Date.class == type) {
            return new Decoder() {
                @Override
                public Object decode(JsonIterator iter) throws IOException {
                    DateFormat dateFormat = builder().dateFormat.get();
                    try {
                        return dateFormat.parse(iter.readString());
                    } catch (ParseException e) {
                        throw new JsonException(e);
                    }
                }
            };
        }
        return super.createDecoder(cacheKey, type);
    }

    @Override
    public void updateClassDescriptor(ClassDescriptor desc) {
        FieldNamingStrategy fieldNamingStrategy = builder().fieldNamingStrategy;
        for (Binding binding : desc.allBindings()) {
            if (binding.method != null) {
                binding.toNames = new String[0];
                binding.fromNames = new String[0];
            }
            if (fieldNamingStrategy != null && binding.field != null) {
                String translated = fieldNamingStrategy.translateName(binding.field);
                binding.toNames = new String[]{translated};
                binding.fromNames = new String[]{translated};
            }
        }
        for (Binding binding : desc.allEncoderBindings()) {
            if (builder().serializeNulls) {
                binding.shouldOmitNull = false;
            }
        }
        super.updateClassDescriptor(desc);
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
