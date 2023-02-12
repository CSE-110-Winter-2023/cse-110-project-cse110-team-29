package com.example.socialcompass;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

public class CircularActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circular);
        loadInput();
    }
    public void loadInput(){
        SharedPreferences prefs = getSharedPreferences("data",MODE_PRIVATE);
        String label = prefs.getString("parentsLabel","");
        Float lat = prefs.getFloat("parentsLat",0);
        Float longi = prefs.getFloat("parentsLong",0);
        TextView labelView = findViewById(R.id.label);
        TextView latView = findViewById(R.id.lat);
        TextView longiView = findViewById(R.id.longi);
        labelView.setText(label);
        latView.setText(lat.toString());
        longiView.setText(longi.toString());
    }

}