package com.jsoniter.demo.object_with_1_double_field;

import com.dslplatform.json.CompiledJson;
import com.jsoniter.output.JsonStream;

@CompiledJson
public class TestObject {

    public double field1;

    public static TestObject createTestObject() {
        TestObject testObject = new TestObject();
        testObject.field1 = 10.24d;
        return testObject;
    }

    public static byte[] createTestJSON() {
        return JsonStream.serialize(createTestObject()).getBytes();
    }
}
