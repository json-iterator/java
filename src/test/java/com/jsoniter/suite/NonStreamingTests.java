package com.jsoniter.suite;

import com.jsoniter.Category;
import org.junit.experimental.categories.Categories;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Categories.class)
@Categories.ExcludeCategory(Category.StreamingCategory.class)
@Suite.SuiteClasses({AllTestCases.class})
public class NonStreamingTests {

}
