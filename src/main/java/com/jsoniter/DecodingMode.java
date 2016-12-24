package com.jsoniter;

public enum DecodingMode {
    /**
     * dynamically codegen, generate object decoder using hash
     */
    DYNAMIC_MODE_AND_MATCH_FIELD_WITH_HASH,
    /**
     * dynamically codegen, generate object decoder which compares fields strictly
     */
    DYNAMIC_MODE_AND_MATCH_FIELD_STRICTLY,
    /**
     * statically codegen
     */
    STATIC_MODE,
    /**
     * decoding only using reflection, do not codegen dyanmically or statically
     */
    REFLECTION_MODE
}
