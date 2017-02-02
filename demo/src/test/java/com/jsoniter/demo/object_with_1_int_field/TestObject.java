package com.jsoniter.demo.object_with_1_int_field;

import com.dslplatform.json.CompiledJson;
import com.jsoniter.output.JsonStream;

@CompiledJson
public class TestObject {

    public int field1;

    public static TestObject createTestObject() {
        TestObject testObject = new TestObject();
        testObject.field1 = 1024;
        return testObject;
    }

    public static byte[] createTestJSON() {
        return JsonStream.serialize(createTestObject()).getBytes();
    }
}
