package com.jsoniter;

import com.jsoniter.spi.Decoder;
import com.jsoniter.spi.JsonException;
import com.jsoniter.spi.JsoniterSpi;
import junit.framework.TestCase;

import java.io.IOException;
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
        // contract: If the byte class and a cachKey mapping to a suitable decoder for
        // the byte class is passed as argument to genreadOp a string on the form
        // "com.jsoniter.CodegenAccess.readInt($(cash_key), iter)" should be
        // returned. Otherwise an exception should be thrown.
        Object codegenImplNative = Class.forName("com.jsoniter.CodegenImplNative").newInstance();
        Method genReadOp = codegenImplNative.getClass().getDeclaredMethod("genReadOp", String.class, Type.class);
        genReadOp.setAccessible(true);
        JsoniterSpi.addNewDecoder("int_decoder", new Decoder.IntDecoder() {
            @Override
            public int decodeInt(JsonIterator iter) throws IOException {
                return 0;
            }
        });
        Type type = int.class;
        String cacheKey = "int_decoder";
        assertEquals("com.jsoniter.CodegenAccess.readInt(\"int_decoder\", iter)", genReadOp.invoke(codegenImplNative, cacheKey, type));

        // Replace the decoder with a decoder for different type
        JsoniterSpi.addNewDecoder("int_decoder", new Decoder.BooleanDecoder() {
            @Override
            public boolean decodeBoolean(JsonIterator iter) throws IOException {
                return false;
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

    public void testGenReadOpLong() throws Exception {
        Object codegenImplNative = Class.forName("com.jsoniter.CodegenImplNative").newInstance();
        Method genReadOp = codegenImplNative.getClass().getDeclaredMethod("genReadOp", String.class, Type.class);
        genReadOp.setAccessible(true);
        Type type = long.class;
        String cacheKey = "cacheKey";
        assertEquals("iter.readLong()", genReadOp.invoke(codegenImplNative, cacheKey, type));
    }

    public void testGenReadOpFloat() throws Exception {
        Object codegenImplNative = Class.forName("com.jsoniter.CodegenImplNative").newInstance();
        Method genReadOp = codegenImplNative.getClass().getDeclaredMethod("genReadOp", String.class, Type.class);
        genReadOp.setAccessible(true);
        Type type = float.class;
        String cacheKey = "cacheKey";
        assertEquals("iter.readFloat()", genReadOp.invoke(codegenImplNative, cacheKey, type));
    }

    public void testGenReadOpDouble() throws Exception {
        Object codegenImplNative = Class.forName("com.jsoniter.CodegenImplNative").newInstance();
        Method genReadOp = codegenImplNative.getClass().getDeclaredMethod("genReadOp", String.class, Type.class);
        genReadOp.setAccessible(true);
        Type type = double.class;
        String cacheKey = "cacheKey";
        assertEquals("iter.readDouble()", genReadOp.invoke(codegenImplNative, cacheKey, type));
    }
}