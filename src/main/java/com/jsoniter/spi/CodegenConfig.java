package com.jsoniter.spi;

public interface CodegenConfig {
    /**
     * register decoder/encoder before codegen
     * register extension before codegen
     */
    void beforeCodegen();

    /**
     * what to codegen
     * @return generate encoder/decoder for the types
     */
    TypeLiteral[] getTypeLiterals();
}
