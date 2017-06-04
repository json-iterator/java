package com.jsoniter.spi;

public interface ConfigListener {
    void onCurrentConfigChanged(Config newConfig);
}
