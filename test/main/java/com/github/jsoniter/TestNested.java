package com.github.jsoniter;

import junit.framework.TestCase;

import java.io.IOException;

import static org.junit.Assert.assertArrayEquals;

public class TestNested extends TestCase {
    public void test_array_of_objects() throws IOException {
        Jsoniter iter = Jsoniter.parseString(
                "[{'field1':'11','field2':'12'},{'field1':'21','field2':'22'}]".replace('\'', '"'));
        SimpleObject[] objects = iter.read(SimpleObject[].class);
        assertArrayEquals(new SimpleObject[]{
                new SimpleObject() {{
                    field1 = "11";
                    field2 = "12";
                }},
                new SimpleObject() {{
                    field1 = "21";
                    field2 = "22";
                }}
        }, objects);
    }
}
