package com.example.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import com.jsoniter.JsonIterator;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        User user = JsonIterator.deserialize("{\"firstName\": \"tao\", \"lastName\": \"wen\", \"score\": 1024}", User.class);
        Log.d("jsoniter", user.firstName);
    }
}
