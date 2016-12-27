package com.jsoniter;

import com.jsoniter.spi.Binding;
import com.jsoniter.spi.ClassDescriptor;
import com.jsoniter.spi.EmptyExtension;
import com.jsoniter.spi.ExtensionManager;
import junit.framework.TestCase;

import java.io.IOException;

public class TestCustomizeField extends TestCase {

    static {
        JsonIterator.setMode(DecodingMode.DYNAMIC_MODE_AND_MATCH_FIELD_STRICTLY);
    }


    public static class TestObject9 {
        public String field1;
        public String field2;
    }


    public void test_mandatory_fields_not_missing() throws IOException {
        ExtensionManager.registerExtension(new EmptyExtension() {
            @Override
            public void updateClassDescriptor(ClassDescriptor desc) {
                if (desc.clazz != TestObject9.class) {
                    return;
                }
                for (Binding field : desc.allDecoderBindings()) {
                    field.asMissingWhenNotPresent = true;
                }
            }
        });
        JsonIterator iter = JsonIterator.parse("{'field1': '100', 'field2': '200'}".replace('\'', '"'));
        assertEquals("100", iter.read(TestObject9.class).field1);
    }


    public static class TestObject10 {
        public String field1;
        public String field2;
    }

    public void test_mandatory_fields_missing() throws IOException {
        ExtensionManager.registerExtension(new EmptyExtension() {
            @Override
            public void updateClassDescriptor(ClassDescriptor desc) {
                if (desc.clazz != TestObject10.class) {
                    return;
                }
                for (Binding field : desc.allDecoderBindings()) {
                    field.asMissingWhenNotPresent = true;
                }
            }
        });
        JsonIterator iter = JsonIterator.parse("{'field1': '100'}".replace('\'', '"'));
        try {
            iter.read(TestObject10.class);
            fail("should throw exception");
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static class TestObject11 {
        public String field1;
    }

    public void test_fail_on_present() throws IOException {
        ExtensionManager.registerExtension(new EmptyExtension() {
            @Override
            public void updateClassDescriptor(ClassDescriptor desc) {
                if (desc.clazz != TestObject11.class) {
                    return;
                }
                for (Binding field : desc.allDecoderBindings()) {
                    field.asExtraWhenPresent = true;
                }
            }
        });
        JsonIterator iter = JsonIterator.parse("{'field1': '100'}".replace('\'', '"'));
        try {
            iter.read(TestObject11.class);
            fail("should throw exception");
        } catch (Exception e) {
        }
    }
}
