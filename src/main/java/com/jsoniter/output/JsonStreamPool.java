package com.jsoniter.output;

import com.jsoniter.spi.Config;
import com.jsoniter.spi.ConfigListener;
import com.jsoniter.spi.JsoniterSpi;

public class JsonStreamPool {

    private final static ThreadLocal<JsonStream> slot1 = new ThreadLocal<JsonStream>();
    private final static ThreadLocal<JsonStream> slot2 = new ThreadLocal<JsonStream>();
    private final static ThreadLocal<AsciiOutputStream> osSlot1 = new ThreadLocal<AsciiOutputStream>();
    private final static ThreadLocal<AsciiOutputStream> osSlot2 = new ThreadLocal<AsciiOutputStream>();

    static {
        JsoniterSpi.registerConfigListener(new ConfigListener() {
            @Override
            public void onCurrentConfigChanged(Config newConfig) {
                JsonStream stream = slot1.get();
                if (stream != null) {
                    stream.configCache = newConfig;
                }
                stream = slot2.get();
                if (stream != null) {
                    stream.configCache = newConfig;
                }
            }
        });
    }

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
        if (slot1.get() == null) {
            slot1.set(jsonStream);
            return;
        }
        if (slot2.get() == null) {
            slot2.set(jsonStream);
            return;
        }
    }

    public static AsciiOutputStream borrowAsciiOutputStream() {
        AsciiOutputStream stream = osSlot1.get();
        if (stream != null) {
            osSlot1.set(null);
            return stream;
        }
        stream = osSlot2.get();
        if (stream != null) {
            osSlot2.set(null);
            return stream;
        }
        return new AsciiOutputStream();
    }

    public static void returnAsciiOutputStream(AsciiOutputStream asciiOutputStream) {
        if (osSlot1.get() == null) {
            osSlot1.set(asciiOutputStream);
            return;
        }
        if (osSlot2.get() == null) {
            osSlot2.set(asciiOutputStream);
            return;
        }
    }
}
