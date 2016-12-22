package com.jsoniter;

public enum DecodingMode {
    /**
     * dynamically codegen, generate object decoder using hash
     */
    HASH_MODE,
    /**
     * dynamically codegen, generate object decoder which compares fields strictly
     */
    STRICT_MODE,
    /**
     * statically codegen
     */
    STATIC_MODE,
    /**
     * decoding only using reflection, do not codegen dyanmically or statically
     */
    REFLECTION_MODE
}
