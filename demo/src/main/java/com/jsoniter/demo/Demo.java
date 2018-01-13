package com.jsoniter.demo;

import com.jsoniter.JsonIterator;
import com.jsoniter.any.Any;
import com.jsoniter.output.EncodingMode;
import com.jsoniter.output.JsonStream;
import com.jsoniter.spi.DecodingMode;

public class Demo {
    static {
        // ensure the jsoniter is properly setup
        new DemoCodegenConfig().setup();
    }

    public static void main(String[] args) {
        User user = JsonIterator.deserialize("{\"firstName\": \"tao\", \"lastName\": \"wen\", \"score\": \"1024\"}", User.class);
        System.out.println(user.firstName);
        System.out.println(user.lastName);
        System.out.println(user.score);
        user.attachment = Any.wrapArray(new int[]{1, 2, 3});
        System.out.println(JsonStream.serialize(user));
    }
}
