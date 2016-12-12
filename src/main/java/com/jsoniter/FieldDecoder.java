package com.jsoniter;

public interface FieldDecoder extends Decoder {
    /**
     * if json field does not match your object, specify what they can be
     *
     * @return null, if field is the field name
     */
    String[] getAlternativeFieldNames();

    /**
     * do not override decode
     * @return true, for default decoder
     */
    boolean useDefaultDecoder();
}
