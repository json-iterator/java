package com.jsoniter;

import com.jsoniter.annotation.jsoniter.JsonProperty;

public class AnnotatedObject {
    @JsonProperty("field-1")
    public int field1;

    @JsonProperty("field-2")
    public int field2;
}
