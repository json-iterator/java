package com.jsoniter;

import com.jsoniter.Jsoniter;
import junit.framework.TestCase;

import java.io.IOException;

public class TestSkip extends TestCase {
    public void test_skip_number() throws IOException {
        Jsoniter iter = Jsoniter.parse("[1,2]");
        assertTrue(iter.readArray());
        iter.skip();
        assertTrue(iter.readArray());
        assertEquals(2, iter.readUnsignedInt());
        assertFalse(iter.readArray());
    }

    public void test_skip_string() throws IOException {
        Jsoniter iter = Jsoniter.parse("['hello',2]".replace('\'', '"'));
        assertTrue(iter.readArray());
        iter.skip();
        assertTrue(iter.readArray());
        assertEquals(2, iter.readUnsignedInt());
        assertFalse(iter.readArray());
    }

    public void test_skip_object() throws IOException {
        Jsoniter iter = Jsoniter.parse("[{'hello': {'world': 'a'}},2]".replace('\'', '"'));
        assertTrue(iter.readArray());
        iter.skip();
        assertTrue(iter.readArray());
        assertEquals(2, iter.readUnsignedInt());
        assertFalse(iter.readArray());
    }

    public void test_find_string_end() throws IOException {
        Jsoniter iter = Jsoniter.parse("\"a");
        assertEquals(1, iter.findStringEnd());
    }

//    public void test_large_file() throws IOException {
//        for (int i = 0; i < 100; i++) {
//            FileInputStream fileInputStream = new FileInputStream("/tmp/large-file.json");
//            Jsoniter iter = Jsoniter.parse(fileInputStream, 4096);
//            int total = 0;
//            while (iter.readArray()) {
//                iter.skip();
//                total++;
//            }
//            if (total != 11351) {
//                throw new RuntimeException();
//            }
//            fileInputStream.close();
//        }
//    }
}
