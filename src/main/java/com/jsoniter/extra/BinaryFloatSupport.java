package com.jsoniter.extra;

import com.jsoniter.JsonIterator;
import com.jsoniter.any.Any;
import com.jsoniter.output.JsonStream;
import com.jsoniter.spi.Decoder;
import com.jsoniter.spi.Encoder;
import com.jsoniter.spi.JsonException;
import com.jsoniter.spi.JsoniterSpi;

import java.io.IOException;

/**
 * store float/double as binary data, encoded as base64
 */
public class BinaryFloatSupport {
    private static boolean enabled;

    public static synchronized void enable() {
        if (enabled) {
            throw new JsonException("BinaryFloatSupport.enable can only be called once");
        }
        enabled = true;
        JsoniterSpi.registerTypeEncoder(Double.class, new Encoder() {
            @Override
            public void encode(Object obj, JsonStream stream) throws IOException {
                Double number = (Double) obj;
                long bits = Double.doubleToLongBits(number.doubleValue());
                stream.writeVal(bits);
            }

            @Override
            public Any wrap(Object obj) {
                Double number = (Double) obj;
                return Any.wrap(number.doubleValue());
            }
        });
        JsoniterSpi.registerTypeDecoder(Double.class, new Decoder() {
            @Override
            public Object decode(JsonIterator iter) throws IOException {
                return Double.longBitsToDouble(iter.readLong());
            }
        });
        JsoniterSpi.registerTypeEncoder(double.class, new Encoder.DoubleEncoder() {
            @Override
            public void encodeDouble(double obj, JsonStream stream) throws IOException {
                long bits = Double.doubleToLongBits(obj);
                stream.writeVal(bits);
            }
        });
        JsoniterSpi.registerTypeDecoder(double.class, new Decoder.DoubleDecoder() {
            @Override
            public double decodeDouble(JsonIterator iter) throws IOException {
                return Double.longBitsToDouble(iter.readLong());
            }
        });
        JsoniterSpi.registerTypeEncoder(Float.class, new Encoder() {
            @Override
            public void encode(Object obj, JsonStream stream) throws IOException {
                stream.writeRaw(obj.toString());
            }

            @Override
            public Any wrap(Object obj) {
                Float number = (Float) obj;
                return Any.wrap(number.floatValue());
            }
        });
        JsoniterSpi.registerTypeEncoder(float.class, new Encoder.FloatEncoder() {
            @Override
            public void encodeFloat(float obj, JsonStream stream) throws IOException {
                stream.writeRaw(Float.toString(obj));
            }
        });
    }
}
