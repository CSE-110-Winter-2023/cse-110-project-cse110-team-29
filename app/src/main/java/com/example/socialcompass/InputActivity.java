package com.example.socialcompass;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;

public class InputActivity extends AppCompatActivity {
    // private ArrayList<Location> locations;
    private EditText labelInput;
    private EditText latInput;
    private EditText longInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);

        labelInput = findViewById(R.id.labelName);
        latInput = findViewById(R.id.latitudeNum);
        longInput = findViewById(R.id.longitudeNum);

        // locations = new ArrayList<>();
        // TODO: update locations with saved locations
    }

    public void saveLocation() {
        // get a new Location from user input
        // TODO: validate inputs
        iLocation newLoc = new iLocation(
                labelInput.getText().toString(),
                Float.parseFloat(latInput.getText().toString()),
                Float.parseFloat(longInput.getText().toString()));

        // locations.add(newLoc);


        // save locations

        SharedPreferences prefs = getSharedPreferences("data", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("parentsLabel", newLoc.getLabel());
        editor.putFloat("parentsLat", newLoc.getLatitude());
        editor.putFloat("parentsLong", newLoc.getLongitude());
        editor.apply();
    }

    public void onSaveAndContinue(View view) {
        saveLocation();

        // reset inputs
        labelInput.setText("");
        latInput.setText("");
        longInput.setText("");
    }

    public void onFinish(View view) {
        saveLocation();
        finish();
    }
}