package com.jsoniter.extra;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.FieldNamingStrategy;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.annotations.Since;
import com.google.gson.annotations.Until;
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

    private final static int SURR1_FIRST = 0xD800;
    private final static int SURR1_LAST = 0xDBFF;
    private final static int SURR2_FIRST = 0xDC00;
    private final static int SURR2_LAST = 0xDFFF;
    private static final String[] REPLACEMENT_CHARS;
    private static final String[] HTML_SAFE_REPLACEMENT_CHARS;
    static {
        REPLACEMENT_CHARS = new String[128];
        for (int i = 0; i <= 0x1f; i++) {
            REPLACEMENT_CHARS[i] = String.format("\\u%04x", (int) i);
        }
        REPLACEMENT_CHARS['"'] = "\\\"";
        REPLACEMENT_CHARS['\\'] = "\\\\";
        REPLACEMENT_CHARS['\t'] = "\\t";
        REPLACEMENT_CHARS['\b'] = "\\b";
        REPLACEMENT_CHARS['\n'] = "\\n";
        REPLACEMENT_CHARS['\r'] = "\\r";
        REPLACEMENT_CHARS['\f'] = "\\f";
        HTML_SAFE_REPLACEMENT_CHARS = REPLACEMENT_CHARS.clone();
        HTML_SAFE_REPLACEMENT_CHARS['<'] = "\\u003c";
        HTML_SAFE_REPLACEMENT_CHARS['>'] = "\\u003e";
        HTML_SAFE_REPLACEMENT_CHARS['&'] = "\\u0026";
        HTML_SAFE_REPLACEMENT_CHARS['='] = "\\u003d";
        HTML_SAFE_REPLACEMENT_CHARS['\''] = "\\u0027";
    }
    private GsonCompatibilityMode(String configName, Builder builder) {
        super(configName, builder);
    }

    protected Builder builder() {
        return (Builder) super.builder();
    }

    public static class Builder extends Config.Builder {
        private boolean excludeFieldsWithoutExposeAnnotation = false;
        private boolean serializeNulls = false;
        private boolean disableHtmlEscaping = false;
        private ThreadLocal<DateFormat> dateFormat = new ThreadLocal<DateFormat>() {
            @Override
            protected DateFormat initialValue() {
                return DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, Locale.US);
            }
        };
        private FieldNamingStrategy fieldNamingStrategy;
        private Double version;

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

        public Builder setFieldNamingPolicy(FieldNamingPolicy namingConvention) {
            this.fieldNamingStrategy = namingConvention;
            return this;
        }

        public Builder setPrettyPrinting() {
            indentionStep(2);
            return this;
        }

        public Builder disableHtmlEscaping() {
            disableHtmlEscaping = true;
            return this;
        }

        public Builder setVersion(double version) {
            this.version = version;
            return this;
        }

        public GsonCompatibilityMode build() {
            escapeUnicode(false);
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
            if (disableHtmlEscaping != builder.disableHtmlEscaping) return false;
            if (!dateFormat.get().equals(builder.dateFormat.get())) return false;
            if (fieldNamingStrategy != null ? !fieldNamingStrategy.equals(builder.fieldNamingStrategy) : builder.fieldNamingStrategy != null)
                return false;
            return version != null ? version.equals(builder.version) : builder.version == null;
        }

        @Override
        public int hashCode() {
            int result = super.hashCode();
            result = 31 * result + (excludeFieldsWithoutExposeAnnotation ? 1 : 0);
            result = 31 * result + (serializeNulls ? 1 : 0);
            result = 31 * result + (disableHtmlEscaping ? 1 : 0);
            result = 31 * result + dateFormat.get().hashCode();
            result = 31 * result + (fieldNamingStrategy != null ? fieldNamingStrategy.hashCode() : 0);
            result = 31 * result + (version != null ? version.hashCode() : 0);
            return result;
        }

        @Override
        public Config.Builder copy() {
            Builder copied = (Builder) super.copy();
            copied.excludeFieldsWithoutExposeAnnotation = excludeFieldsWithoutExposeAnnotation;
            copied.serializeNulls = serializeNulls;
            copied.disableHtmlEscaping = disableHtmlEscaping;
            copied.dateFormat = dateFormat;
            copied.fieldNamingStrategy = fieldNamingStrategy;
            copied.version = version;
            return copied;
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
        } else if (String.class == type) {
            final String[] replacements;
            if (builder().disableHtmlEscaping) {
                replacements = REPLACEMENT_CHARS;
            } else {
                replacements = HTML_SAFE_REPLACEMENT_CHARS;
            }
            return new Encoder() {
                @Override
                public void encode(Object obj, JsonStream stream) throws IOException {
                    String value = (String) obj;
                    stream.write('"');
                    int _surrogate;
                    for (int i = 0; i < value.length(); i++) {
                        int c = value.charAt(i);
                        String replacement;
                        if (c < 128) {
                            replacement = replacements[c];
                            if (replacement == null) {
                                stream.write(c);
                            } else {
                                stream.writeRaw(replacement);
                            }
                        } else if (c == '\u2028') {
                            stream.writeRaw("\\u2028");
                        } else if (c == '\u2029') {
                            stream.writeRaw("\\u2029");
                        } else {
                            if (c < 0x800) { // 2-byte
                                stream.write(
                                        (byte) (0xc0 | (c >> 6)),
                                        (byte) (0x80 | (c & 0x3f))
                                );
                            } else { // 3 or 4 bytes
                                // Surrogates?
                                if (c < SURR1_FIRST || c > SURR2_LAST) {
                                    stream.write(
                                            (byte) (0xe0 | (c >> 12)),
                                            (byte) (0x80 | ((c >> 6) & 0x3f)),
                                            (byte) (0x80 | (c & 0x3f))
                                    );
                                    continue;
                                }
                                // Yup, a surrogate:
                                if (c > SURR1_LAST) { // must be from first range
                                    throw new JsonException("illegalSurrogate");
                                }
                                _surrogate = c;
                                // and if so, followed by another from next range
                                if (i >= value.length()) { // unless we hit the end?
                                    break;
                                }
                                int firstPart = _surrogate;
                                _surrogate = 0;
                                // Ok, then, is the second part valid?
                                if (c < SURR2_FIRST || c > SURR2_LAST) {
                                    throw new JsonException("Broken surrogate pair: first char 0x"+Integer.toHexString(firstPart)+", second 0x"+Integer.toHexString(c)+"; illegal combination");
                                }
                                c = 0x10000 + ((firstPart - SURR1_FIRST) << 10) + (c - SURR2_FIRST);
                                if (c > 0x10FFFF) { // illegal in JSON as well as in XML
                                    throw new JsonException("illegalSurrogate");
                                }
                                stream.write(
                                        (byte) (0xf0 | (c >> 18)),
                                        (byte) (0x80 | ((c >> 12) & 0x3f)),
                                        (byte) (0x80 | ((c >> 6) & 0x3f)),
                                        (byte) (0x80 | (c & 0x3f))
                                );
                            }
                        }
                    }
                    stream.write('"');
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
            if (builder().version != null) {
                Since since = binding.getAnnotation(Since.class);
                if (since != null && builder().version < since.value()) {
                    binding.toNames = new String[0];
                    binding.fromNames = new String[0];
                }
                Until until = binding.getAnnotation(Until.class);
                if (until != null && builder().version >= until.value()) {
                    binding.toNames = new String[0];
                    binding.fromNames = new String[0];
                }
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
