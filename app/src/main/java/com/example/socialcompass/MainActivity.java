package com.example.socialcompass;

import static com.example.socialcompass.Utilities.showAlert;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // temp always start input activity immediately
        Intent intent = new Intent(this, InputActivity.class);
        startActivity(intent);
    }

    public void debug(View view) {
        SharedPreferences prefs = getSharedPreferences("data", MODE_PRIVATE);
        String label = prefs.getString("parentsLabel", "what");
        Float lat = prefs.getFloat("parentsLat", 0);
        Float longi = prefs.getFloat("parentsLong", 0);
        showAlert(this, label + '\n' + String.valueOf(lat) + '\n' + String.valueOf(longi));
    }
}
