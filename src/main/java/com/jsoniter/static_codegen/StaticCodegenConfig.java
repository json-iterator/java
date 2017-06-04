package com.jsoniter.static_codegen;

import com.jsoniter.spi.TypeLiteral;

public interface StaticCodegenConfig {
    /**
     * register decoder/encoder before codegen
     * register extension before codegen
     */
    void setup();

    /**
     * what to codegen
     * @return generate encoder/decoder for the types
     */
    TypeLiteral[] whatToCodegen();
}
