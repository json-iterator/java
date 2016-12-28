package com.jsoniter.spi;

import com.jsoniter.output.JsonStream;

import java.io.IOException;

public interface Encoder {

    void encode(Object obj, JsonStream stream) throws IOException;

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
