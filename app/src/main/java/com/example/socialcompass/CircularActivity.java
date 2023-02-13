package com.example.socialcompass;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

public class CircularActivity extends AppCompatActivity {
    private LocationService locationService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circular);
        locationService = LocationService.singleton(this);
        locationService.getLocation().observe(this, loc -> {
            loadInput(loc.first, loc.second);
            // Utilities.showAlert(this, String.valueOf(loc.first) + "," + String.valueOf(loc.second));
        });
    }

    public void loadInput(double myLat, double myLong) {
        SharedPreferences prefs = getSharedPreferences("data", MODE_PRIVATE);

        String label = prefs.getString("parentsLabel","");
        Float latitude = prefs.getFloat("parentsLat",0);
        Float longitude = prefs.getFloat("parentsLong",0);

        TextView labelView = findViewById(R.id.label);
        //get the label from user input
        labelView.setText(label);

        // manually update angle (it works)
        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) labelView.getLayoutParams();

        // coordinates of Geisel Library
        // final double myLat = 32.881174;
        // final double myLong = -117.2378661;
        layoutParams.circleAngle = (float) angle_in_activity(myLat, myLong, (double)latitude, (double)longitude);
    }

    private double angle_in_activity(double lat1, double long1, double lat2,
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