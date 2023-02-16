package com.example.socialcompass;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class CircularActivity extends AppCompatActivity {
    private LocationService locationService;
    private OrientationServiceS orientationService;
    private float orientationAngel;

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_circular);
//        locationService = LocationService.singleton(this);
//        locationService.getLocation().observe(this, loc -> {
//            loadInput(loc.first, loc.second);
//            // Utilities.showAlert(this, String.valueOf(loc.first) + "," + String.valueOf(loc.second));
//        });
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circular);

//        loadInput();

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 200);
        }

        locationService = LocationService.singleton(this);
        locationService.getLocation().observe(this, loc -> {
            loadInput(loc.first, loc.second);
            // Utilities.showAlert(this, String.valueOf(loc.first) + "," + String.valueOf(loc.second));
        });

        orientationService = new OrientationServiceS(this);
        ImageView image = findViewById(R.id.clockFace);

        orientationService.getOrientation().observe(this, orientation -> {
            orientationAngel = orientation;
            orientationSet(image, orientationAngel);
        });
    }

    private void orientationSet(ImageView image, float degree) {
        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) image.getLayoutParams();
        if (degree < 0) {
            degree += 2 * Math.PI;
        }
        layoutParams.circleAngle = (float) (180 * degree / (Math.PI));
        image.setLayoutParams(layoutParams);
    }

    public void loadInput(double myLat, double myLong) {
        SharedPreferences prefs = getSharedPreferences("data", MODE_PRIVATE);

        String label = prefs.getString("parentsLabel", "");
        Float latitude = prefs.getFloat("parentsLat", 0);
        Float longitude = prefs.getFloat("parentsLong", 0);

        TextView labelView = findViewById(R.id.label);
        //get the label from user input
        labelView.setText(label);

        // manually update angle (it works)
        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) labelView.getLayoutParams();

        // coordinates of Geisel Library
        // final double myLat = 32.881174;
        // final double myLong = -117.2378661;
        layoutParams.circleAngle = (float) angle_in_activity(myLat, myLong, (double) latitude, (double) longitude);
        labelView.setLayoutParams(layoutParams);

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