package com.jsoniter;

import junit.framework.TestCase;

import java.io.IOException;

public class TestReflectionDecoder extends TestCase {

    public static class PackageLocal {
        String field;
    }

    public void test_package_local() throws IOException {
        ExtensionManager.registerTypeDecoder(PackageLocal.class, new ReflectionDecoder(PackageLocal.class));
        JsonIterator iter = JsonIterator.parse("{'field': 'hello'}".replace('\'', '"'));
        PackageLocal obj = iter.read(PackageLocal.class);
        assertEquals("hello", obj.field);
    }

    public static class Inherited extends PackageLocal {
    }

    public void test_inherited() throws IOException {
        ExtensionManager.registerTypeDecoder(Inherited.class, new ReflectionDecoder(Inherited.class));
        JsonIterator iter = JsonIterator.parse("{'field': 'hello'}".replace('\'', '"'));
        Inherited obj = iter.read(Inherited.class);
        assertEquals("hello", obj.field);
    }

    public static class ObjectWithInt {
        private int field;
    }

    public void test_int_field() throws IOException {
        ExtensionManager.registerTypeDecoder(ObjectWithInt.class, new ReflectionDecoder(ObjectWithInt.class));
        JsonIterator iter = JsonIterator.parse("{'field': 100}".replace('\'', '"'));
        ObjectWithInt obj = iter.read(ObjectWithInt.class);
        assertEquals(100, obj.field);
    }
}
