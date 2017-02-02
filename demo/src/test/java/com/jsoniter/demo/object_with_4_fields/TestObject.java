package com.jsoniter.demo.object_with_4_fields;

import com.dslplatform.json.CompiledJson;
import com.jsoniter.output.JsonStream;

@CompiledJson
public class TestObject {

    public String field1;
    public String field2;
    public String field3;
    public String field4;

    public static TestObject createTestObject() {
        TestObject testObject = new TestObject();
        testObject.field1 = "field1";
        testObject.field2 = "field2";
        testObject.field3 = "field3";
        testObject.field4 = "field4";
        return testObject;
    }

    public static byte[] createTestJSON() {
        return JsonStream.serialize(createTestObject()).getBytes();
    }
}
