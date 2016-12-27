package com.jsoniter.annotation;

import com.jsoniter.spi.Decoder;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.ANNOTATION_TYPE, ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface JsonProperty {
    String value() default "";
    String[] from() default {};
    boolean required() default false;
    Class<? extends Decoder> decoder() default Decoder.class;
}
