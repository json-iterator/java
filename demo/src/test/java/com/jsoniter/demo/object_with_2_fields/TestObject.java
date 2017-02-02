package com.jsoniter.demo.object_with_2_fields;

import com.dslplatform.json.CompiledJson;
import com.jsoniter.output.JsonStream;

@CompiledJson
public class TestObject {

    public String field1;
    public String field2;

    public static TestObject createTestObject() {
        TestObject testObject = new TestObject();
        testObject.field1 = "field1";
        testObject.field2 = "field2";
        return testObject;
    }

    public static byte[] createTestJSON() {
        return JsonStream.serialize(createTestObject()).getBytes();
    }
}
