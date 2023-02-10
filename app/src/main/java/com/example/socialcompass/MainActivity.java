package com.example.socialcompass;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // temp always start input activity immediately
        Intent intent = new Intent(this, InputActivity.class);
        startActivity(intent);
    }
}
