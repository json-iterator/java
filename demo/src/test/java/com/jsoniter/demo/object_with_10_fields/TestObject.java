package com.jsoniter.demo.object_with_10_fields;

import com.dslplatform.json.CompiledJson;
import com.jsoniter.output.JsonStream;

@CompiledJson
public class TestObject {

    public String field1;
    public String field2;
    public String field3;
    public String field4;
    public String field5;
    public String field6;
    public String field7;
    public String field8;
    public String field9;
    public String field10;

    public static TestObject createTestObject() {
        TestObject testObject = new TestObject();
        testObject.field1 = "";
        testObject.field2 = "";
        testObject.field3 = "";
        testObject.field4 = "";
        testObject.field5 = "";
        testObject.field6 = "";
        testObject.field7 = "";
        testObject.field8 = "";
        testObject.field9 = "";
        testObject.field10 = "";
        return testObject;
    }

    public static byte[] createTestJSON() {
        return JsonStream.serialize(createTestObject()).getBytes();
    }
}
