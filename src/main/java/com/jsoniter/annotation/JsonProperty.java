package com.jsoniter.annotation;

import com.jsoniter.spi.Decoder;
import com.jsoniter.spi.Encoder;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.ANNOTATION_TYPE, ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface JsonProperty {

    String value() default "";

    String[] from() default {};

    String[] to() default {};

    boolean required() default false;

    Class<? extends Decoder> decoder() default Decoder.class;

    Class<?> implementation() default Object.class;

    Class<? extends Encoder> encoder() default Encoder.class;
}
