package com.jsoniter.output;

public enum EncodingMode {
    /**
     * dynamically codegen
     */
    DYNAMIC_MODE,
    /**
     * statically codegen
     */
    STATIC_MODE,
    /**
     * decoding only using reflection, do not need code generation
     */
    REFLECTION_MODE
}
