package com.jsoniter.demo.object_with_1_field;

import com.dslplatform.json.CompiledJson;
import com.jsoniter.output.JsonStream;

@CompiledJson
public class TestObject {

    public String field1;

    public static TestObject createTestObject() {
        TestObject testObject = new TestObject();
        testObject.field1 = "field1field2field3field4field5";
        return testObject;
    }

    public static byte[] createTestJSON() {
        return JsonStream.serialize(createTestObject()).getBytes();
    }
}
