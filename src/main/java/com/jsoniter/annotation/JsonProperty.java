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

    /**
     * @return alternative name for the field/getter/setter/parameter
     */
    String value() default "";

    /**
     * @return when bind from multiple possible names, set this
     */
    String[] from() default {};

    /**
     * @return when one field will write to multiple object fields, set this
     */
    String[] to() default {};

    /**
     * @return used in decoding only, the field must present in the JSON, regardless null or not
     */
    boolean required() default false;

    /**
     * @return set different decoder just for this field
     */
    Class<? extends Decoder> decoder() default Decoder.class;

    /**
     * @return used in decoding only, choose concrete class for interface/abstract type
     */
    Class<?> implementation() default Object.class;

    /**
     * @return set different encoder just for this field
     */
    Class<? extends Encoder> encoder() default Encoder.class;

    /**
     * @return used in encoding only, should check null for this field,
     * skip null checking will make encoding faster
     */
    boolean nullable() default true;

    /**
     * @return used in encoding only, should check null for the value, if it is collection,
     * skip null checking will make encoding faster
     */
    boolean collectionValueNullable() default true;

    /**
     * @return if true, do not write the field altogether if value is null
     */
    boolean omitNull() default true;
}
