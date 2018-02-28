package com.jsoniter;

import com.jsoniter.spi.Decoder;
import com.jsoniter.spi.JsonException;
import com.jsoniter.spi.JsoniterSpi;
import junit.framework.TestCase;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

public class TestCodegenImplNative extends TestCase {

	public void testGenReadOpBool() throws Exception {
	    // contract: If the boolean class and a cachKey mapping to a suitable decoder for
        // the boolean class is passed as argument to genreadOp a string on the form
        // "com.jsoniter.CodegenAccess.readBoolean($(cash_key), iter)" should be
        // returned. Otherwise an exception should be thrown.
        Object codegenImplNative = Class.forName("com.jsoniter.CodegenImplNative").newInstance();
        Method genReadOp = codegenImplNative.getClass().getDeclaredMethod("genReadOp", String.class, Type.class);
        genReadOp.setAccessible(true);
        JsoniterSpi.addNewDecoder("boolean_decoder", new Decoder.BooleanDecoder() {
            @Override
            public boolean decodeBoolean(JsonIterator iter) throws IOException {
                return false;
            }
        });
        Type type = boolean.class;
        String cacheKey = "boolean_decoder";
		assertEquals("com.jsoniter.CodegenAccess.readBoolean(\"boolean_decoder\", iter)", genReadOp.invoke(codegenImplNative, cacheKey, type));

        // Replace the decoder with a decoder for different type
        JsoniterSpi.addNewDecoder("boolean_decoder", new Decoder.IntDecoder() {
            @Override
            public int decodeInt(JsonIterator iter) throws IOException {
                return 0;
            }
        });
        boolean exceptionThrown = false;
        try {
            genReadOp.invoke(codegenImplNative,cacheKey,type);
        } catch (Exception _) {
            exceptionThrown = true;
        }

        assertTrue(exceptionThrown);
	}

    public void testGenReadOpByte() throws Exception {
        // contract: If the byte class and a cachKey mapping to a suitable decoder for
        // the byte class is passed as argument to genreadOp a string on the form
        // "com.jsoniter.CodegenAccess.readShort($(cash_key), iter)" should be
        // returned. Otherwise an exception should be thrown.
        Object codegenImplNative = Class.forName("com.jsoniter.CodegenImplNative").newInstance();
        Method genReadOp = codegenImplNative.getClass().getDeclaredMethod("genReadOp", String.class, Type.class);
        genReadOp.setAccessible(true);
        JsoniterSpi.addNewDecoder("byte_decoder", new Decoder.ShortDecoder(){
            @Override
            public short decodeShort(JsonIterator iter) throws IOException {
                return 0;
            }
        });
        Type type = byte.class;
        String cacheKey = "byte_decoder";
        assertEquals("com.jsoniter.CodegenAccess.readShort(\"byte_decoder\", iter)", genReadOp.invoke(codegenImplNative, cacheKey, type));

        // Replace the decoder with a decoder for different type
        JsoniterSpi.addNewDecoder("byte_decoder", new Decoder.IntDecoder() {
            @Override
            public int decodeInt(JsonIterator iter) throws IOException {
                return 0;
            }
        });
        boolean exceptionThrown = false;
        try {
            genReadOp.invoke(codegenImplNative,cacheKey,type);
        } catch (Exception _) {
            exceptionThrown = true;
        }

        assertTrue(exceptionThrown);
    }

    public void testGenReadOpShort() throws Exception {
        // contract: If the byte class and a cachKey mapping to a suitable decoder for
        // the byte class is passed as argument to genreadOp a string on the form
        // "com.jsoniter.CodegenAccess.readShort($(cash_key), iter)" should be
        // returned. Otherwise an exception should be thrown.
        Object codegenImplNative = Class.forName("com.jsoniter.CodegenImplNative").newInstance();
        Method genReadOp = codegenImplNative.getClass().getDeclaredMethod("genReadOp", String.class, Type.class);
        genReadOp.setAccessible(true);
        JsoniterSpi.addNewDecoder("short_decoder", new Decoder.ShortDecoder(){
            @Override
            public short decodeShort(JsonIterator iter) throws IOException {
                return 0;
            }
        });
        Type type = short.class;
        String cacheKey = "short_decoder";
        assertEquals("com.jsoniter.CodegenAccess.readShort(\"short_decoder\", iter)", genReadOp.invoke(codegenImplNative, cacheKey, type));

        // Replace the decoder with a decoder for different type
        JsoniterSpi.addNewDecoder("short_decoder", new Decoder.IntDecoder() {
            @Override
            public int decodeInt(JsonIterator iter) throws IOException {
                return 0;
            }
        });
        boolean exceptionThrown = false;
        try {
            genReadOp.invoke(codegenImplNative,cacheKey,type);
        } catch (Exception _) {
            exceptionThrown = true;
        }

        assertTrue(exceptionThrown);
    }

    public void testGenReadOpInt() throws Exception {
        Object codegenImplNative = Class.forName("com.jsoniter.CodegenImplNative").newInstance();
        Method genReadOp = codegenImplNative.getClass().getDeclaredMethod("genReadOp", String.class, Type.class);
        genReadOp.setAccessible(true);
        // char and and short should return readShort
        Type typeChar = char.class;
        Type typeInt = int.class;
        String cacheKey = "cacheKey";
        assertEquals("iter.readInt()", genReadOp.invoke(codegenImplNative, cacheKey, typeChar));
        assertEquals("iter.readInt()", genReadOp.invoke(codegenImplNative, cacheKey, typeInt));
    }

    public void testGenReadOpLongWithWrongDecoder() throws Exception {
	    // Contract: The read operation with a boolean decoder and an expected type of long should result
        // in an Exception being thrown
        Object codegenImplNative = Class.forName("com.jsoniter.CodegenImplNative").newInstance();
        Method genReadOp = codegenImplNative.getClass().getDeclaredMethod("genReadOp", String.class, Type.class);
        genReadOp.setAccessible(true);
        JsoniterSpi.addNewDecoder("boolean_decoder", new Decoder.BooleanDecoder() {
            @Override
            public boolean decodeBoolean(JsonIterator iter) throws IOException {
                return false;
            }
        });
        Type type = long.class;
        String cacheKey = "boolean_decoder";

        try {
            genReadOp.invoke(codegenImplNative, cacheKey, type);
            fail();
        } catch (InvocationTargetException e) {
            try {
                throw (Exception) e.getCause();
            } catch (JsonException err) {
                assertEquals( "decoder for boolean_decodermust implement Decoder.LongDecoder", err.getMessage());
            }

        }
    }

    public void testGenReadOpLong() throws Exception {
        // Contract: The read operation with a long decoder and an expected type of long should return
        // a CodegenAccess.read_long("long_decoder"); as String
        Object codegenImplNative = Class.forName("com.jsoniter.CodegenImplNative").newInstance();
        Method genReadOp = codegenImplNative.getClass().getDeclaredMethod("genReadOp", String.class, Type.class);
        genReadOp.setAccessible(true);
        JsoniterSpi.addNewDecoder("long_decoder", new Decoder.LongDecoder() {

            @Override
            public long decodeLong(JsonIterator iter) throws IOException {
                return 0;
            }
        });
        Type type = long.class;
        String cacheKey = "long_decoder";
        assertEquals("com.jsoniter.CodegenAccess.readLong(\"long_decoder\", iter)",
                genReadOp.invoke(codegenImplNative, cacheKey, type));
    }

    public void testGenReadOpFloatWithWrongDecoder() throws Exception {
        // Contract: The read operation with a boolean decoder and an expected type of float should result
        // in an Exception being thrown
        Object codegenImplNative = Class.forName("com.jsoniter.CodegenImplNative").newInstance();
        Method genReadOp = codegenImplNative.getClass().getDeclaredMethod("genReadOp", String.class, Type.class);
        genReadOp.setAccessible(true);
        JsoniterSpi.addNewDecoder("boolean_decoder", new Decoder.BooleanDecoder() {
            @Override
            public boolean decodeBoolean(JsonIterator iter) throws IOException {
                return false;
            }
        });
        Type type = float.class;
        String cacheKey = "boolean_decoder";

        try {
            genReadOp.invoke(codegenImplNative, cacheKey, type);
            fail();
        } catch (InvocationTargetException e) {
            try {
                throw (Exception) e.getCause();
            } catch (JsonException err) {
                assertEquals( "decoder for boolean_decodermust implement Decoder.FloatDecoder", err.getMessage());
            }

        }
    }

    public void testGenReadOpFloat() throws Exception {
        // Contract: The read operation with a float decoder and an expected type of float should return
        // a CodegenAccess.read_float("float_decoder"); as String
        Object codegenImplNative = Class.forName("com.jsoniter.CodegenImplNative").newInstance();
        Method genReadOp = codegenImplNative.getClass().getDeclaredMethod("genReadOp", String.class, Type.class);
        genReadOp.setAccessible(true);
        JsoniterSpi.addNewDecoder("float_decoder", new Decoder.FloatDecoder(){
            @Override
            public float decodeFloat(JsonIterator iter) throws IOException {
                return 0;
            }
        });
        Type type = float.class;
        String cacheKey = "float_decoder";
        assertEquals("com.jsoniter.CodegenAccess.readFloat(\"float_decoder\", iter)",
                genReadOp.invoke(codegenImplNative, cacheKey, type));
    }

    public void testGenReadOpDoubleWithWrongDecoder() throws Exception {
        // Contract: The read operation with a boolean decoder and an expected type of double should result
        // in an Exception being thrown
        Object codegenImplNative = Class.forName("com.jsoniter.CodegenImplNative").newInstance();
        Method genReadOp = codegenImplNative.getClass().getDeclaredMethod("genReadOp", String.class, Type.class);
        genReadOp.setAccessible(true);
        JsoniterSpi.addNewDecoder("boolean_decoder", new Decoder.BooleanDecoder() {
            @Override
            public boolean decodeBoolean(JsonIterator iter) throws IOException {
                return false;
            }
        });
        Type type = double.class;
        String cacheKey = "boolean_decoder";

        try {
            genReadOp.invoke(codegenImplNative, cacheKey, type);
            fail();
        } catch (InvocationTargetException e) {
            try {
                throw (Exception) e.getCause();
            } catch (JsonException err) {
                assertEquals( "decoder for boolean_decodermust implement Decoder.DoubleDecoder", err.getMessage());
            }

        }
    }

    public void testGenReadOpDouble() throws Exception {
        // Contract: The read operation with a double decoder and an expected type of double should return
        // a CodegenAccess.read_double("double_decoder"); as String
        Object codegenImplNative = Class.forName("com.jsoniter.CodegenImplNative").newInstance();
        Method genReadOp = codegenImplNative.getClass().getDeclaredMethod("genReadOp", String.class, Type.class);
        genReadOp.setAccessible(true);
        JsoniterSpi.addNewDecoder("double_decoder", new Decoder.DoubleDecoder() {
            @Override
            public double decodeDouble(JsonIterator iter) throws IOException {
                return 0;
            }
        });
        Type type = double.class;
        String cacheKey = "double_decoder";
        assertEquals("com.jsoniter.CodegenAccess.readDouble(\"double_decoder\", iter)",
                genReadOp.invoke(codegenImplNative, cacheKey, type));
    }
}
