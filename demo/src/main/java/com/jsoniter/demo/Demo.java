package com.jsoniter.demo;

import com.jsoniter.DecodingMode;
import com.jsoniter.JsonIterator;
import com.jsoniter.output.EncodingMode;
import com.jsoniter.output.JsonStream;

public class Demo {
    static {
        // ensure the jsoniter is properly setup
        new DemoCodegenConfig().setup();
        JsonIterator.setMode(DecodingMode.STATIC_MODE);
        JsonStream.setMode(EncodingMode.STATIC_MODE);
        JsonStream.defaultIndentionStep = 2;
    }

    public static void main(String[] args) {
        User user = JsonIterator.deserialize("{\"firstName\": \"tao\", \"lastName\": \"wen\", \"score\": \"1024\"}", User.class);
        System.out.println(user.firstName);
        System.out.println(user.lastName);
        System.out.println(user.score);
        System.out.println(JsonStream.serialize(user));
    }
}
