package com.jsoniter.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface JsonObject {

    // if the unknown property is in this list, it will be treated as extra
    // if @JsonExtraProperties not defined, it will be treated as error
    String[] unknownPropertiesBlacklist() default {};

    // if the unknown property is in this list, it will be silently ignored
    String[] unknownPropertiesWhitelist() default {};

    // if true, all known properties will be treated as extra
    // if @JsonExtraProperties not defined, it will be treated as error
    boolean asExtraForUnknownProperties() default false;
}
