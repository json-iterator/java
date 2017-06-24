package com.jsoniter.output;

public class JsonStreamPool {

    private final static ThreadLocal<JsonStream> slot1 = new ThreadLocal<JsonStream>();
    private final static ThreadLocal<JsonStream> slot2 = new ThreadLocal<JsonStream>();

    public static JsonStream borrowJsonStream() {
        JsonStream stream = slot1.get();
        if (stream != null) {
            slot1.set(null);
            return stream;
        }
        stream = slot2.get();
        if (stream != null) {
            slot2.set(null);
            return stream;
        }
        return new JsonStream(null, 512);
    }

    public static void returnJsonStream(JsonStream jsonStream) {
        jsonStream.configCache = null;
        jsonStream.indention = 0;
        if (slot1.get() == null) {
            slot1.set(jsonStream);
            return;
        }
        if (slot2.get() == null) {
            slot2.set(jsonStream);
            return;
        }
    }

}
