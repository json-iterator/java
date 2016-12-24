package com.jsoniter.demo;

import com.jsoniter.DecodingMode;
import com.jsoniter.JsonIterator;

public class Demo {
    static {
        // ensure the jsoniter is properly setup
        new DemoCodegenConfig().setup();
        JsonIterator.setMode(DecodingMode.STATIC_MODE);
    }
    public static void main(String[] args) {
        User user = new JsonIterator().read("{\"firstName\": \"tao\", \"lastName\": \"wen\", \"score\": \"1024\"}", User.class);
        System.out.println(user.firstName);
        System.out.println(user.lastName);
        System.out.println(user.score);
    }
}
