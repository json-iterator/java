package com.jsoniter.extra;

import com.jsoniter.spi.JsonException;
import com.jsoniter.any.Any;
import com.jsoniter.output.JsonStream;
import com.jsoniter.spi.Encoder;
import com.jsoniter.spi.JsoniterSpi;

import java.io.IOException;

/**
 * default float/double encoding will keep 6 decimal places
 * enable precise encoding will use JDK toString to be precise
 */
public class PreciseFloatSupport {
    private static boolean enabled;

    public static synchronized void enable() {
        if (enabled) {
            throw new JsonException("PreciseFloatSupport.enable can only be called once");
        }
        enabled = true;
        JsoniterSpi.registerTypeEncoder(Double.class, new Encoder() {
            @Override
            public void encode(Object obj, JsonStream stream) throws IOException {
                stream.writeRaw(obj.toString());
            }

            @Override
            public Any wrap(Object obj) {
                Double number = (Double) obj;
                return Any.wrap(number.doubleValue());
            }
        });
        JsoniterSpi.registerTypeEncoder(double.class, new Encoder.DoubleEncoder() {
            @Override
            public void encodeDouble(double obj, JsonStream stream) throws IOException {
                stream.writeRaw(Double.toString(obj));
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
