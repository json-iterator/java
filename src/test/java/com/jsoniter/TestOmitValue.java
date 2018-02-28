package com.jsoniter;

import com.jsoniter.spi.OmitValue;
import junit.framework.TestCase;
import java.lang.reflect.Type;

public class TestOmitValue extends TestCase {

    public void test_default_true(){
        // Contract: test if parse in OmitValue.java steps in to default switch case.
        // Returns true iff none of the cases in OmitValue.java is true by having
        // the input parameter valueType set to a type not mentioned in previous
        // cases.

        OmitValue.Parsed parsed = new OmitValue.Parsed(int.class, "hello");

        boolean exceptionThrown = false;

        try {
            parsed.parse(Type.class, "cow");

        } catch (UnsupportedOperationException e) {
            exceptionThrown = true;
        }

        assertTrue(exceptionThrown);
    }

    public void test_char_true_length_1(){
        // Contract: test if parse in OmitValue.java steps in to case with valueType as char.
        // Returns true iff input to parsed.parse() gets a char.class and a defaultValueToOmit with
        // length 1.

        OmitValue.Parsed parsed = new OmitValue.Parsed(int.class, "hello");

        OmitValue result = parsed.parse(char.class, "c");

        OmitValue.Parsed omp = new OmitValue.Parsed("c", "'c' == %s");

        assertEquals('c', ((OmitValue.Parsed) result).getDefaultValue());
        assertEquals("'c' == %s", ((OmitValue.Parsed) result).getCode());
    }

    public void test_char_true_length_big(){
        // Contract: test if parse in OmitValue.java doesn't steps in to case with valueType as char.
        // Returns true iff input to parsed.parse() gets a char.class and a defaultValueToOmit with
        // length greater than 1.

        OmitValue.Parsed parsed = new OmitValue.Parsed(int.class, "hello");

        boolean exceptionThrown = false;

        try {
            parsed.parse(char.class, "toobig");

        } catch (UnsupportedOperationException e) {
            exceptionThrown = true;
        }

        assertTrue(exceptionThrown);
    }

    public void test_character_true_length_big(){
        // Contract: test if parse in OmitValue.java doesn't steps in to case with valueType as Character.
        // Returns true iff input to parsed.parse() gets a Character.class and a defaultValueToOmit with
        // length greater than 1.

        OmitValue.Parsed parsed = new OmitValue.Parsed(int.class, "hello");

        boolean exceptionThrown = false;

        try {
            parsed.parse(Character.class, "toobig");

        } catch (UnsupportedOperationException e) {
            exceptionThrown = true;
        }

        assertTrue(exceptionThrown);
    }
    
}
