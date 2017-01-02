package com.jsoniter.spi;

import com.jsoniter.any.Any;
import com.jsoniter.output.JsonStream;

import java.io.IOException;

public interface Encoder {

    void encode(Object obj, JsonStream stream) throws IOException;

    Any wrap(Object obj);

    abstract class BooleanEncoder implements Encoder {
        @Override
        public void encode(Object obj, JsonStream stream) throws IOException {
            encodeBoolean((Boolean) obj, stream);
        }

        public abstract void encodeBoolean(boolean obj, JsonStream stream) throws IOException;
    }

    abstract class ShortEncoder implements Encoder {

        @Override
        public void encode(Object obj, JsonStream stream) throws IOException {
            encodeShort((Short) obj, stream);
        }

        @Override
        public Any wrap(Object obj) {
            Short val = (Short) obj;
            return Any.wrap((int) val);
        }

        public abstract void encodeShort(short obj, JsonStream stream) throws IOException;
    }

    class StringShortEncoder extends ShortEncoder {

        @Override
        public void encodeShort(short obj, JsonStream stream) throws IOException {
            stream.write('"');
            stream.writeVal(obj);
            stream.write('"');
        }
    }

    abstract class IntEncoder implements Encoder {
        @Override
        public void encode(Object obj, JsonStream stream) throws IOException {
            encodeInt((Integer) obj, stream);
        }

        @Override
        public Any wrap(Object obj) {
            Integer val = (Integer) obj;
            return Any.wrap((int)val);
        }

        public abstract void encodeInt(int obj, JsonStream stream) throws IOException;
    }

    class StringIntEncoder extends IntEncoder {

        @Override
        public void encodeInt(int obj, JsonStream stream) throws IOException {
            stream.write('"');
            stream.writeVal(obj);
            stream.write('"');
        }
    }

    abstract class LongEncoder implements Encoder {
        @Override
        public void encode(Object obj, JsonStream stream) throws IOException {
            encodeLong((Long) obj, stream);
        }

        @Override
        public Any wrap(Object obj) {
            Long val = (Long) obj;
            return Any.wrap((long)val);
        }

        public abstract void encodeLong(long obj, JsonStream stream) throws IOException;
    }

    class StringLongEncoder extends LongEncoder {

        @Override
        public void encodeLong(long obj, JsonStream stream) throws IOException {
            stream.write('"');
            stream.writeVal(obj);
            stream.write('"');
        }
    }

    abstract class FloatEncoder implements Encoder {
        @Override
        public void encode(Object obj, JsonStream stream) throws IOException {
            encodeFloat((Float) obj, stream);
        }

        @Override
        public Any wrap(Object obj) {
            Float val = (Float) obj;
            return Any.wrap((float)val);
        }

        public abstract void encodeFloat(float obj, JsonStream stream) throws IOException;
    }

    class StringFloatEncoder extends FloatEncoder {

        @Override
        public void encodeFloat(float obj, JsonStream stream) throws IOException {
            stream.write('"');
            stream.writeVal(obj);
            stream.write('"');
        }
    }

    abstract class DoubleEncoder implements Encoder {
        @Override
        public void encode(Object obj, JsonStream stream) throws IOException {
            encodeDouble((Double) obj, stream);
        }

        @Override
        public Any wrap(Object obj) {
            Double val = (Double) obj;
            return Any.wrap((double)val);
        }

        public abstract void encodeDouble(double obj, JsonStream stream) throws IOException;
    }

    class StringDoubleEncoder extends DoubleEncoder {

        @Override
        public void encodeDouble(double obj, JsonStream stream) throws IOException {
            stream.write('"');
            stream.writeVal(obj);
            stream.write('"');
        }
    }
}
