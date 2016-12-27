package com.jsoniter.spi;

import com.jsoniter.CodegenAccess;
import com.jsoniter.JsonIterator;

import java.io.IOException;

public interface Decoder {
    /**
     * Customized decoder to read values from iterator
     *
     * @param iter the iterator instance
     * @return the value to set
     * @throws IOException when reading from iterator triggered error
     */
    Object decode(JsonIterator iter) throws IOException;

    abstract class BooleanDecoder implements Decoder {
        @Override
        public Object decode(JsonIterator iter) throws IOException {
            return Boolean.valueOf(decodeBoolean(iter));
        }

        public abstract boolean decodeBoolean(JsonIterator iter) throws IOException;
    }

    abstract class ShortDecoder implements Decoder {
        @Override
        public Object decode(JsonIterator iter) throws IOException {
            return Short.valueOf(decodeShort(iter));
        }

        public abstract short decodeShort(JsonIterator iter) throws IOException;
    }

    class StringShortDecoder extends ShortDecoder {

        @Override
        public short decodeShort(JsonIterator iter) throws IOException {
            byte c = CodegenAccess.nextToken(iter);
            if (c != '"') {
                throw iter.reportError("StringShortDecoder", "expect \", but found: " + (char) c);
            }
            short val = iter.readShort();
            c = CodegenAccess.nextToken(iter);
            if (c != '"') {
                throw iter.reportError("StringShortDecoder", "expect \", but found: " + (char) c);
            }
            return val;
        }
    }

    abstract class IntDecoder implements Decoder {
        @Override
        public Object decode(JsonIterator iter) throws IOException {
            return Integer.valueOf(decodeInt(iter));
        }

        public abstract int decodeInt(JsonIterator iter) throws IOException;
    }

    class StringIntDecoder extends IntDecoder {

        @Override
        public int decodeInt(JsonIterator iter) throws IOException {
            byte c = CodegenAccess.nextToken(iter);
            if (c != '"') {
                throw iter.reportError("StringIntDecoder", "expect \", but found: " + (char) c);
            }
            int val = iter.readInt();
            c = CodegenAccess.nextToken(iter);
            if (c != '"') {
                throw iter.reportError("StringIntDecoder", "expect \", but found: " + (char) c);
            }
            return val;
        }
    }

    abstract class LongDecoder implements Decoder {
        @Override
        public Object decode(JsonIterator iter) throws IOException {
            return Long.valueOf(decodeLong(iter));
        }

        public abstract long decodeLong(JsonIterator iter) throws IOException;
    }

    class StringLongDecoder extends LongDecoder {

        @Override
        public long decodeLong(JsonIterator iter) throws IOException {
            byte c = CodegenAccess.nextToken(iter);
            if (c != '"') {
                throw iter.reportError("StringLongDecoder", "expect \", but found: " + (char) c);
            }
            long val = iter.readLong();
            c = CodegenAccess.nextToken(iter);
            if (c != '"') {
                throw iter.reportError("StringLongDecoder", "expect \", but found: " + (char) c);
            }
            return val;
        }
    }

    abstract class FloatDecoder implements Decoder {
        @Override
        public Object decode(JsonIterator iter) throws IOException {
            return Float.valueOf(decodeFloat(iter));
        }

        public abstract float decodeFloat(JsonIterator iter) throws IOException;
    }

    class StringFloatDecoder extends FloatDecoder {

        @Override
        public float decodeFloat(JsonIterator iter) throws IOException {
            byte c = CodegenAccess.nextToken(iter);
            if (c != '"') {
                throw iter.reportError("StringFloatDecoder", "expect \", but found: " + (char) c);
            }
            float val = iter.readFloat();
            c = CodegenAccess.nextToken(iter);
            if (c != '"') {
                throw iter.reportError("StringFloatDecoder", "expect \", but found: " + (char) c);
            }
            return val;
        }
    }

    abstract class DoubleDecoder implements Decoder {

        @Override
        public Object decode(JsonIterator iter) throws IOException {
            return Double.valueOf(decodeDouble(iter));
        }

        public abstract double decodeDouble(JsonIterator iter) throws IOException;
    }

    class StringDoubleDecoder extends DoubleDecoder {

        @Override
        public double decodeDouble(JsonIterator iter) throws IOException {
            byte c = CodegenAccess.nextToken(iter);
            if (c != '"') {
                throw iter.reportError("StringDoubleDecoder", "expect \", but found: " + (char) c);
            }
            double val = iter.readDouble();
            c = CodegenAccess.nextToken(iter);
            if (c != '"') {
                throw iter.reportError("StringDoubleDecoder", "expect \", but found: " + (char) c);
            }
            return val;
        }
    }
}
