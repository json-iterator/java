package com.jsoniter.suite;

import com.jsoniter.extra.TestBase64;
import com.jsoniter.extra.TestJdkDatetime;
import com.jsoniter.extra.TestNamingStrategy;
import com.jsoniter.extra.TestPreciseFloat;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({TestBase64.class, TestJdkDatetime.class, TestNamingStrategy.class, TestPreciseFloat.class})
public class ExtraTests {

}
