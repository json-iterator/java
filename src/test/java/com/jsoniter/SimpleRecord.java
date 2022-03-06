package com.jsoniter;

public record SimpleRecord(String field1, String field2) {
    public SimpleRecord() {
        this(null, null);
    }
    public SimpleRecord(String field1, String field2) {
        this.field1 = field1;
        this.field2 = field2;
    }
}