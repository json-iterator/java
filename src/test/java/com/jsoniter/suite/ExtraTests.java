package com.jsoniter.suite;

import com.jsoniter.extra.*;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({TestBase64.class, TestJdkDatetime.class, TestNamingStrategy.class, TestPreciseFloat.class})
public class ExtraTests {

}
