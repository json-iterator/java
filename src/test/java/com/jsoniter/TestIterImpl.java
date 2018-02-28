package com.jsoniter;

import junit.framework.TestCase;
import org.junit.Test;

public class TestIterImpl extends TestCase {

    // Contract: readStringSlowPath casts IndexOutOfBoundsException: incomplete
    // string if iter = JsonIterator.parse("\\b")
    @Test(expected = IndexOutOfBoundsException.class)
    public void testReadStringSlowPathIncompleteString1() {
        String json = "\\b";
        JsonIterator iter = JsonIterator.parse(json);
        try {
            IterImpl.readStringSlowPath(iter, 0);
        } catch (Exception e) {}
    }

    // Contract: readStringSlowPath casts IndexOutOfBoundsException: incomplete
    // string if iter = JsonIterator.parse("\\n")
    @Test(expected = IndexOutOfBoundsException.class)
    public void testReadStringSlowPathIncompleteString2() {
        String json = "\\n";
        JsonIterator iter = JsonIterator.parse(json);
        try {
            IterImpl.readStringSlowPath(iter, 0);
        } catch (Exception e) {}
    }

    // Contract: readStringSlowPath casts IndexOutOfBoundsException: incomplete
    // string if iter = JsonIterator.parse("\\f")
    @Test(expected = IndexOutOfBoundsException.class)
    public void testReadStringSlowPathIncompleteString3() {
        String json = "\\f";
        JsonIterator iter = JsonIterator.parse(json);
        try {
            IterImpl.readStringSlowPath(iter, 0);
        } catch (Exception e) {}
    }

    // Contract: readStringSlowPath casts IndexOutOfBoundsException: incomplete
    // string if iter = JsonIterator.parse("\\r")
    @Test(expected = IndexOutOfBoundsException.class)
    public void testReadStringSlowPathIncompleteString4() {
        String json = "\\r";
        JsonIterator iter = JsonIterator.parse(json);
        try {
            IterImpl.readStringSlowPath(iter, 0);
        } catch (Exception e) {}
    }
}
