package com.jsoniter.any;

import com.jsoniter.JsonIterator;
import junit.framework.TestCase;

public class TestNotFoundAny extends TestCase {

    public void test_exists() {
        Any any = JsonIterator.deserialize("{\"field\": \"ABC\"}");
        Any field = any.get("field");
        Any otherField = any.get("otherField");
        assertTrue(field.exists());
        assertFalse(otherField.exists());
    }

}
