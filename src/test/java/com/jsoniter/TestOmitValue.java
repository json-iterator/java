package com.jsoniter;

import com.jsoniter.spi.OmitValue.*;
import junit.framework.TestCase;

public class TestOmitValue extends TestCase {

    public void test_shouldOmitInputPositiveOutputFalse() {

        // Arrange
        final ZeroByte objectUnderTest = new ZeroByte();
        final Object val = (byte)1;
    
        // Act
        final boolean retval = objectUnderTest.shouldOmit(val);
    
        // Assert result
        assertEquals(false, retval);
    } 

    public void test_shouldOmitInputPositiveOutputFalse2() {

        // Arrange
        final ZeroInt objectUnderTest = new ZeroInt();
        final Object val = 1;
    
        // Act
        final boolean retval = objectUnderTest.shouldOmit(val);
    
        // Assert result
        assertEquals(false, retval);
    }

    public void test_shouldOmitInputPositiveOutputFalse3() {

        // Arrange
        final ZeroLong objectUnderTest = new ZeroLong();
        final Object val = 1L;
    
        // Act
        final boolean retval = objectUnderTest.shouldOmit(val);
    
        // Assert result
        assertEquals(false, retval);
    }

    public void test_shouldOmitInputZeroOutputTrue() {

        // Arrange
        final ZeroLong objectUnderTest = new ZeroLong();
        final Object val = 0L;
    
        // Act
        final boolean retval = objectUnderTest.shouldOmit(val);
    
        // Assert result
        assertEquals(true, retval);
    }

    public void test_shouldOmitInputPositiveOutputFalse4() {

        // Arrange
        final ZeroShort objectUnderTest = new ZeroShort();
        final Object val = (short)1;
    
        // Act
        final boolean retval = objectUnderTest.shouldOmit(val);
    
        // Assert result
        assertEquals(false, retval);
    }

    public void test_shouldOmitInputTrueOutputFalse() {

        // Arrange
        final False objectUnderTest = new False();
        final Object val = true;
    
        // Act
        final boolean retval = objectUnderTest.shouldOmit(val);
    
        // Assert result
        assertEquals(false, retval);
    }

    public void test_shouldOmitInputNotNullOutputFalse() {

        // Arrange
        final ZeroChar objectUnderTest = new ZeroChar();
        final Object val = '\u0001';
    
        // Act
        final boolean retval = objectUnderTest.shouldOmit(val);
    
        // Assert result
        assertEquals(false, retval);
    }

    public void test_shouldOmitInputPositiveOutputFalse5() {

        // Arrange
        final ZeroDouble objectUnderTest = new ZeroDouble();
        final Object val = 0x0.0000000000001p-1022 /* 4.94066e-324 */;
    
        // Act
        final boolean retval = objectUnderTest.shouldOmit(val);
    
        // Assert result
        assertEquals(false, retval);
    }

    public void test_shouldOmitInputZeroOutputTrue2() {

        // Arrange
        final ZeroDouble objectUnderTest = new ZeroDouble();
        final Object val = 0.0;
    
        // Act
        final boolean retval = objectUnderTest.shouldOmit(val);
    
        // Assert result
        assertEquals(true, retval);
    }

    public void test_shouldOmitInputPositiveOutputFalse6() {

        // Arrange
        final ZeroFloat objectUnderTest = new ZeroFloat();
        final Object val = 0x1p-149f /* 1.4013e-45 */;
    
        // Act
        final boolean retval = objectUnderTest.shouldOmit(val);
    
        // Assert result
        assertEquals(false, retval);
    }
}
