package com.jsoniter.suite;

import com.jsoniter.*;
import com.jsoniter.any.TestList;
import com.jsoniter.output.TestAny;
import com.jsoniter.output.TestCustomizeField;
import com.jsoniter.output.TestNative;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        TestAnnotationJsonWrapper.class,
        TestAnnotation.class,
        com.jsoniter.output.TestAnnotation.class,
        TestCustomizeType.class, TestDemo.class,
        TestExisting.class, TestGenerics.class, TestGenerics.class, TestIO.class, TestNested.class,
        TestObject.class, TestReadAny.class, TestReflection.class, TestSkip.class, TestSlice.class,
        TestString.class, TestWhatIsNext.class,
        TestAny.class,
        com.jsoniter.output.TestArray.class,
        com.jsoniter.any.TestArray.class,
        com.jsoniter.TestArray.class,
        TestCustomizeField.class, com.jsoniter.output.TestCustomizeType.class,
        com.jsoniter.TestMap.class,
        com.jsoniter.output.TestMap.class,
        TestNative.class, TestNested.class, TestObject.class, TestBoolean.class, TestFloat.class,
        TestList.class,
        com.jsoniter.output.TestJackson.class,
        com.jsoniter.TestJackson.class})
public abstract class AllTestCases {
}
