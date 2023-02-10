package com.example.socialcompass;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;

public class InputActivity extends AppCompatActivity {
    private ArrayList<Location> locations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);
        locations = new ArrayList<>();
        // TODO: update locations with saved locations
    }

    public void onSaveAndContinue(View view) {
        // get a new Location from user input
        EditText labelInput = findViewById(R.id.labelName);
        EditText latInput = findViewById(R.id.latitudeNum);
        EditText longInput = findViewById(R.id.longitudeNum);

        // TODO: validate inputs

        Location newLoc = new Location(
                labelInput.getText().toString(),
                Float.parseFloat(latInput.getText().toString()),
                Float.parseFloat(longInput.getText().toString()));

        locations.add(newLoc);

        // TODO: save locations

        // reset inputs
        labelInput.setText("");
        latInput.setText("");
        longInput.setText("");
    }

    public void onFinish(View view) {
        finish();
    }
}