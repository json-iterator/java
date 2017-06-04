package com.jsoniter;

import com.jsoniter.spi.Config;
import com.jsoniter.spi.ConfigListener;
import com.jsoniter.spi.JsoniterSpi;

public class JsonIteratorPool {

    private static ThreadLocal<JsonIterator> slot1 = new ThreadLocal<JsonIterator>();
    private static ThreadLocal<JsonIterator> slot2 = new ThreadLocal<JsonIterator>();

    static {
        JsoniterSpi.registerConfigListener(new ConfigListener() {

            @Override
            public void onCurrentConfigChanged(Config newConfig) {
                JsonIterator iter = slot1.get();
                if (iter != null) {
                    iter.configCache = newConfig;
                }
                iter = slot2.get();
                if (iter != null) {
                    iter.configCache = newConfig;
                }
            }
        });
    }

    public static JsonIterator borrowJsonIterator() {
        JsonIterator iter = slot1.get();
        if (iter != null) {
            slot1.set(null);
            return iter;
        }
        iter = slot2.get();
        if (iter != null) {
            slot2.set(null);
            return iter;
        }
        return new JsonIterator();
    }

    public static void returnJsonIterator(JsonIterator iter) {
        if (slot1.get() == null) {
            slot1.set(iter);
            return;
        }
        if (slot2.get() == null) {
            slot2.set(iter);
            return;
        }
    }
}
