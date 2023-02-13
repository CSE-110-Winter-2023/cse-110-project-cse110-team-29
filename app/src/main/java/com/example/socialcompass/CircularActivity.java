package com.example.socialcompass;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class CircularActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circular);
        loadInput();
    }

    public void loadInput() {
        SharedPreferences prefs = getSharedPreferences("data", MODE_PRIVATE);

        String label = prefs.getString("parentsLabel","");
        Float latitude = prefs.getFloat("parentsLat",0);
        Float longitude = prefs.getFloat("parentsLong",0);

        TextView labelView = findViewById(R.id.label);
        //get the label from user input
        labelView.setText(label);

        // manually update angle (it works)
        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) labelView.getLayoutParams();
        layoutParams.circleAngle = 180;
    }

    private double angleFromCoordinate(double lat1, double long1, double lat2,
                                       double long2) {

        double dLon = (long2 - long1);

        double y = Math.sin(dLon) * Math.cos(lat2);
        double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1)
                * Math.cos(lat2) * Math.cos(dLon);

        double brng = Math.atan2(y, x);

        brng = Math.toDegrees(brng);
        brng = (brng + 360) % 360;
        brng = 360 - brng; // count degrees counter-clockwise - remove to make clockwise

        return brng;
    }
}