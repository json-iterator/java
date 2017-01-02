package com.jsoniter;

public enum DecodingMode {
    /**
     * dynamically codegen, generate set decoder using hash
     */
    DYNAMIC_MODE_AND_MATCH_FIELD_WITH_HASH,
    /**
     * dynamically codegen, generate set decoder which compares fields strictly
     */
    DYNAMIC_MODE_AND_MATCH_FIELD_STRICTLY,
    /**
     * statically codegen
     */
    STATIC_MODE,
    /**
     * decoding only using reflection, do not need code generation
     */
    REFLECTION_MODE
}
