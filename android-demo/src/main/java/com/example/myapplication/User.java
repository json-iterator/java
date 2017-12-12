package com.example.myapplication;

import com.jsoniter.annotation.JsonProperty;

public class User {
    @JsonProperty(nullable = false)
    public String firstName;
    @JsonProperty(nullable = false)
    public String lastName;
    public int score;
}
