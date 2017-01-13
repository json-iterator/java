package com.jsoniter.suite;

import com.jsoniter.DecodingMode;
import com.jsoniter.JsonIterator;
import com.jsoniter.StreamingCategory;
import com.jsoniter.output.EncodingMode;
import com.jsoniter.output.JsonStream;
import org.junit.BeforeClass;
import org.junit.experimental.categories.Categories;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;


@RunWith(Categories.class)
@Categories.ExcludeCategory(StreamingCategory.class)
@Suite.SuiteClasses({AllTestCases.class})
public class NonStreamingTests4Strict {
    @BeforeClass
    public static void setup() {
        JsonStream.setMode(EncodingMode.DYNAMIC_MODE);
        JsonIterator.setMode(DecodingMode.DYNAMIC_MODE_AND_MATCH_FIELD_STRICTLY);
    }
}
