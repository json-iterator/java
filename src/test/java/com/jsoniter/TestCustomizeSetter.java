package com.jsoniter;

import junit.framework.TestCase;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class TestCustomizeSetter extends TestCase {

    static {
//        JsonIterator.enableStrictMode();
    }

    public static class ObjectWithDefaultSetter {
        String field;

        public void setField(String field) {
            this.field = field;
        }
    }

    public void test_default_setter() throws IOException {
        JsonIterator iter = JsonIterator.parse("{'field': 'hello'}".replace('\'', '"'));
        ObjectWithDefaultSetter obj = iter.read(ObjectWithDefaultSetter.class);
        assertEquals("hello", obj.field);
    }

    public static class ObjectWithCustomizedSetter {
        String field1;
        String field2;

        public void initialize(String field1, String field2) {
            this.field1 = field1;
            this.field2 = field2;
        }
    }

    public void test_customized_setter() throws IOException {
        ExtensionManager.registerExtension(new EmptyExtension() {
            @Override
            public List<CustomizedSetter> getSetters(Class clazz) {
                if (clazz == ObjectWithCustomizedSetter.class) {
                    return (List) Arrays.asList(new CustomizedSetter(){{
                        methodName = "initialize";
                        parameters = (List) Arrays.asList(new Binding() {{
                            name = "field1";
                            valueType = String.class;
                        }}, new Binding() {{
                            name = "field2";
                            valueType = String.class;
                        }});
                    }});
                }
                return null;
            }
        });
        JsonIterator iter = JsonIterator.parse("{'field1': 'hello', 'field2': 'world'}".replace('\'', '"'));
        ObjectWithCustomizedSetter obj = iter.read(ObjectWithCustomizedSetter.class);
        assertEquals("hello", obj.field1);
        assertEquals("world", obj.field2);
    }
}
