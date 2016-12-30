package com.jsoniter.demo;

import com.jsoniter.Any;
import com.jsoniter.JsonIterator;
import com.jsoniter.annotation.*;
import com.jsoniter.output.JsonStream;
import org.junit.Test;

import java.io.IOException;

public class WrapperUnwrapper {

    public static class Name {
        private final String firstName;
        private final String lastName;

        public Name(String firstName, String lastName) {
            this.firstName = firstName;
            this.lastName = lastName;
        }

        public String getFirstName() {
            return firstName;
        }

        public String getLastName() {
            return lastName;
        }
    }

public static class User {
    private Name name;
    public int score;

    @JsonIgnore
    public Name getName() {
        return name;
    }

    @JsonUnwrapper
    public void writeName(JsonStream stream) throws IOException {
        stream.writeObjectField("firstName");
        stream.writeVal(name.getFirstName());
        stream.writeMore();
        stream.writeObjectField("lastName");
        stream.writeVal(name.getLastName());
    }

    @JsonWrapper
    public void setName(@JsonProperty("firstName") String firstName, @JsonProperty("lastName") String lastName) {
        System.out.println(firstName);
        name = new Name(firstName, lastName);
    }
}

    @Test
    public void test() {
        JsoniterAnnotationSupport.enable();
        String input = "{'firstName': 'tao', 'lastName': 'wen', 'score': 100}".replace('\'', '\"');
        System.out.println(input);
        User user = JsonIterator.deserialize(input, User.class);
        System.out.println(user.getName().getFirstName());
        System.out.println(JsonStream.serialize(user));

        System.out.println(JsonStream.serialize(new int[]{1,2,3}));
    }
}
