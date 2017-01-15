package com.jsoniter.suite;

import com.jsoniter.*;
import com.jsoniter.output.TestAny;
import com.jsoniter.output.TestCustomizeField;
import com.jsoniter.output.TestMap;
import com.jsoniter.output.TestNative;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({TestAnnotation.class, TestArray.class, TestCustomizeType.class, TestDemo.class,
        TestExisting.class, TestGenerics.class, TestGenerics.class, TestIO.class, TestNested.class,
        TestObject.class, TestReadAny.class, TestReflection.class, TestSkip.class, TestSlice.class,
        TestString.class, TestWhatIsNext.class, com.jsoniter.output.TestAnnotation.class,
        TestAny.class, com.jsoniter.output.TestArray.class, TestCustomizeField.class, com.jsoniter.output.TestCustomizeType.class,
        TestMap.class, TestNative.class, TestNested.class, TestObject.class, TestBoolean.class, TestFloat.class})
public abstract class AllTestCases {
}
