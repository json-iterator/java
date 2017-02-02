package com.dslplatform.json;

public class CustomJsonReader extends JsonReader {
    public CustomJsonReader(byte[] buffer) {
        super(buffer, null);
    }
    public void reset() {
        super.reset(length());
    }
}
