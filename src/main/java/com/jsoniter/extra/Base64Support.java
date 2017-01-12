package com.jsoniter.extra;

import com.jsoniter.JsonIterator;
import com.jsoniter.Slice;
import com.jsoniter.any.Any;
import com.jsoniter.output.JsonStream;
import com.jsoniter.spi.Decoder;
import com.jsoniter.spi.Encoder;
import com.jsoniter.spi.JsonException;
import com.jsoniter.spi.JsoniterSpi;

import java.io.IOException;

/**
 * byte[] <=> base64
 */
public class Base64Support {
    private static boolean enabled;
    public static synchronized void enable() {
        if (enabled) {
            throw new JsonException("Base64Support.enable can only be called once");
        }
        enabled = true;
        JsoniterSpi.registerTypeDecoder(byte[].class, new Decoder() {
            @Override
            public Object decode(JsonIterator iter) throws IOException {
                Slice slice = iter.readStringAsSlice();
                return Base64.decodeFast(slice.data(), slice.head(), slice.tail());
            }
        });
        JsoniterSpi.registerTypeEncoder(byte[].class, new Encoder() {
            @Override
            public void encode(Object obj, JsonStream stream) throws IOException {
                byte[] bytes = (byte[]) obj;
                stream.write('"');
                Base64.encodeToBytes(bytes, stream);
                stream.write('"');
            }

            @Override
            public Any wrap(Object obj) {
                return null;
            }
        });
    }
}
