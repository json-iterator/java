package com.jsoniter.suite;

import com.jsoniter.JsonIterator;
import org.junit.BeforeClass;
import org.junit.experimental.categories.Categories;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Categories.class)
@Suite.SuiteClasses({AllTestCases.class})
public class StreamingTests {

    @BeforeClass
    public static void setup() {
        JsonIterator.enableStreamingSupport();
    }
}
