package com.example.socialcompass;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.LiveData;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Pair;
import android.widget.ImageView;
import android.widget.TextView;

public class CircularActivity extends AppCompatActivity {
    private LocationService locationService;
    private OrientationService orientationService;
    private float orientationAngel;
    private double longitude;
    private double latitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circular);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 200);
        }

        orientationService = OrientationService.singleton(this);
        locationService = LocationService.singleton(this);

        this.reobserveOrientation();
        this.reobserveLocation();

    }

    public void reobserveOrientation() {
        LiveData<Float> orientationData = orientationService.getOrientation();
        orientationData.observe(this, this::onOrientationChanged);
    }

    private void reobserveLocation() {
        LiveData<Pair<Double, Double>> locationData = locationService.getLocation();
        locationData.observe(this, this::onLocationChanged);
    }

    private void onOrientationChanged(Float orientation) {
        TextView orientationText = findViewById(R.id.label);
        orientationSet(orientationText, orientation);
    }

    private void onLocationChanged(Pair<Double, Double> latLong) {
        TextView textView = findViewById(R.id.latTextView);
        TextView textView1 = findViewById(R.id.longTextView);

        textView.setText(latLong.first.toString());
        textView1.setText(latLong.second.toString());

        SharedPreferences prefs = getSharedPreferences("data", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putFloat("parentsLat", latLong.first.floatValue());
        editor.putFloat("parentsLong", latLong.second.floatValue());
        editor.apply();
    }

    private void orientationSet(TextView image, float degree) {
        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) image.getLayoutParams();
        degree = -degree;
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