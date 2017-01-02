package com.jsoniter;

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
            TestString.class, TestWhatIsNext.class})
    public static class AllTestCases {
    }

    @RunWith(Categories.class)
    @Categories.ExcludeCategory(StreamingCategory.class)
    @Suite.SuiteClasses({AllTestCases.class})
    public static class NonStreamingTests {

    }

    @RunWith(Categories.class)
    @Categories.IncludeCategory(StreamingCategory.class)
    @Suite.SuiteClasses({AllTestCases.class})
    public static class StreamingTests {
    }
}
