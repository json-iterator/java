package com.jsoniter;

import com.jsoniter.annotation.jsoniter.JsonIgnore;
import com.jsoniter.annotation.jsoniter.JsonProperty;

public class AnnotatedObject {
    @JsonProperty("field-1")
    public int field1;

    @JsonIgnore
    public int field2;
}
