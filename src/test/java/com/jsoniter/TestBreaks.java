package com.jsoniter;


import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class TestBreaks {

    @Test
    public void breaks() {
        Set<Byte> breaks = new HashSet<Byte>(
                Arrays.asList((byte) ' ', (byte) '\t', (byte) '\n', (byte) '\r', (byte) ',', (byte) '}', (byte) ']')
        );
        for (int i = 0; i < 128; i++) {
            assertEquals(IterImplSkip.isBreak((byte) i), breaks.contains((byte) i));
        }
    }

}
