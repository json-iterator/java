package com.jsoniter;

import com.jsoniter.annotation.JsonIgnore;
import com.jsoniter.annotation.JsonProperty;
import com.jsoniter.annotation.JsonWrapper;
import com.jsoniter.annotation.JsonWrapperType;
import com.jsoniter.spi.DecodingMode;
import junit.framework.TestCase;

import java.io.IOException;

public class TestAnnotationJsonWrapper extends TestCase {

    static {
//        JsonIterator.setMode(DecodingMode.DYNAMIC_MODE_AND_MATCH_FIELD_WITH_HASH);
//        JsonIterator.setMode(DecodingMode.REFLECTION_MODE);
    }

    public static class TestObject1 {

        private int _field1;

        @JsonWrapper
        public void initialize(@JsonProperty("field1") int field1) {
            this._field1 = field1;
        }
    }

    public void test_binding() throws IOException {
        JsonIterator iter = JsonIterator.parse("{'field1': 100}".replace('\'', '"'));
        TestObject1 obj = iter.read(TestObject1.class);
        assertEquals(100, obj._field1);
    }

    public static class TestObject2 {

        private int _field1;

        @JsonWrapper(JsonWrapperType.KEY_VALUE)
        public void setProperties(String key, Object value) {
            if (key.equals("field1")) {
                _field1 = ((Long) value).intValue();
            }
        }
    }

    public void test_key_value() throws IOException {
        JsonIterator iter = JsonIterator.parse("{'field1': 100}".replace('\'', '"'));
        TestObject2 obj = iter.read(TestObject2.class);
        assertEquals(100, obj._field1);
    }

    public static class AAA {
        @JsonProperty("name_1")
        public String name;

        @JsonIgnore
        public String partA;
        @JsonIgnore
        public String partB;

        @JsonWrapper
        public void foreignFromJson(@JsonProperty(value = "parts", from ={"p2"}, required = false) String parts) {
            if(parts == null){
                return;
            }
            String[] ps = parts.split(",");
            partA = ps[0];
            partB = ps.length > 1 ? ps[1] : null;
        }
    }

    public void test_issue_104() {
        String jsonStr = "{'name':'aaa', 'name_1':'bbb'}".replace('\'', '\"');
        AAA aaa = JsonIterator.deserialize(jsonStr, AAA.class);
        assertEquals("bbb", aaa.name);
    }
}
