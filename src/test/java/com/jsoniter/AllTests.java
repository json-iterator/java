package com.jsoniter;

import com.jsoniter.output.*;
import org.junit.BeforeClass;
import org.junit.experimental.categories.Categories;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

public class AllTests {

    public interface StreamingCategory {
    }

    @RunWith(Suite.class)
    @Suite.SuiteClasses({TestAnnotation.class, TestArray.class, TestCustomizeType.class, TestDemo.class,
            TestExisting.class, TestGenerics.class, TestGenerics.class, TestIO.class, TestNested.class,
            TestObject.class, TestReadAny.class, TestReflection.class, TestSkip.class, TestSlice.class,
            TestString.class, TestWhatIsNext.class, com.jsoniter.output.TestAnnotation.class,
            TestAny.class, com.jsoniter.output.TestArray.class, TestCustomizeField.class, com.jsoniter.output.TestCustomizeType.class,
            TestMap.class, TestNative.class, TestNested.class, TestObject.class})
    public static class AllTestCases {
    }

    @RunWith(Categories.class)
    @Categories.ExcludeCategory(StreamingCategory.class)
    @Suite.SuiteClasses({AllTestCases.class})
    public static class NonStreamingTests {

    }

    @RunWith(Categories.class)
    @Categories.ExcludeCategory(StreamingCategory.class)
    @Suite.SuiteClasses({AllTestCases.class})
    public static class NonStreamingTests4Hash {
        @BeforeClass
        public static void setup() {
            JsonStream.setMode(EncodingMode.DYNAMIC_MODE);
            JsonIterator.setMode(DecodingMode.DYNAMIC_MODE_AND_MATCH_FIELD_WITH_HASH);
        }
    }

    @RunWith(Categories.class)
    @Categories.ExcludeCategory(StreamingCategory.class)
    @Suite.SuiteClasses({AllTestCases.class})
    public static class NonStreamingTests4Strict {
        @BeforeClass
        public static void setup() {
            JsonStream.setMode(EncodingMode.DYNAMIC_MODE);
            JsonIterator.setMode(DecodingMode.DYNAMIC_MODE_AND_MATCH_FIELD_STRICTLY);
        }
    }

    @RunWith(Categories.class)
    @Categories.IncludeCategory(StreamingCategory.class)
    @Suite.SuiteClasses({AllTestCases.class})
    public static class StreamingTests {

    }
}
