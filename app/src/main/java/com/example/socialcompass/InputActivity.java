package com.example.socialcompass;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class InputActivity extends AppCompatActivity {
    private EditText UIDInput;
    //private EditText latInput;
    //private EditText longInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);

        UIDInput = findViewById(R.id.uid);
        //latInput = findViewById(R.id.latitudeNum);
        //longInput = findViewById(R.id.longitudeNum);

        // update locations with saved locations
        //SharedPreferences prefs = getSharedPreferences("data", MODE_PRIVATE);

        //labelInput.setText(prefs.getString("parentsLabel",""));
        //latInput.setText(String.valueOf(prefs.getFloat("parentsLat",0)));
        //longInput.setText(String.valueOf(prefs.getFloat("parentsLong",0)));
    }

    /*
    public void saveLocation() {
        SharedPreferences prefs = getSharedPreferences("data", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putString("parentsLabel", labelInput.getText().toString());
        editor.putFloat("parentsLat", Float.parseFloat(latInput.getText().toString()));
        editor.putFloat("parentsLong", Float.parseFloat(longInput.getText().toString()));
        editor.apply();

        Intent intent = new Intent(this,CircularActivity.class);
        startActivity(intent);
    }
    */


    public void onFinish(View view) {
        //saveLocation();
        finish();
    }
}
