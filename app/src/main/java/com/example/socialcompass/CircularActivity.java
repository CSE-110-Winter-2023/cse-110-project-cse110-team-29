package com.example.socialcompass;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.LiveData;


import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import androidx.core.util.Pair;
import android.view.View;
import android.widget.TextView;



public class CircularActivity extends AppCompatActivity {
    private LocationService locationService;
    private OrientationService orientationService;

    SharedPreferences prefs;

    // for a single saved location
    private double orientationAngle;
    private double angleFromLocation;
    private double parentLongitude;
    private double parentLatitude;

    private double orientationOffset;

    TextView northView;
    TextView parentsHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circular);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 200);
        }

        locationService = LocationService.singleton(this);
        this.reobserveLocation();

        orientationService = OrientationService.singleton(this);
        this.reobserveOrientation();

        this.prefs = getSharedPreferences("data", MODE_PRIVATE);
        this.northView = findViewById(R.id.north);
        this.parentsHome = findViewById(R.id.ParentHome);

        if(!prefs.contains("parentsLabel") || !prefs.contains("parentsLat") || !prefs.contains("parentsLong")) {
            Intent inputIntent = new Intent(this, InputActivity.class);
            startActivity(inputIntent);
        }

        this.parentsHome.setText(prefs.getString("parentsLabel",""));
        this.parentLongitude = prefs.getFloat("parentsLong", 0);
        this.parentLatitude = prefs.getFloat("parentsLat", 0);
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
        if(orientation == this.orientationAngle) {
            return;
        }

        this.orientationAngle = orientation;
        orientationSet(this.northView, orientation + this.orientationOffset);
        orientationSet(this.parentsHome, this.angleFromLocation + orientation + this.orientationOffset);
    }

    private void onLocationChanged(Pair<Double, Double> userLocation) {
        Double userLat = userLocation.first, userLong = userLocation.second;

        Double newAngle = angleInActivity(userLat, userLong, this.parentLatitude, this.parentLongitude);

        if(newAngle == this.angleFromLocation) {
            return;
        }

        this.angleFromLocation = newAngle;

        orientationSet(this.parentsHome, newAngle);
    }

    private void orientationSet(TextView image, Double degree) {
        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) image.getLayoutParams();
        degree = -degree;

        layoutParams.circleAngle = (float) (180 * degree / (Math.PI));

        image.setLayoutParams(layoutParams);
    }

    private double angleInActivity(double lat1, double long1, double lat2, double long2) {

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

    public void onEditOrientation(View view) {
        TextView orientationText = findViewById(R.id.editOrientation);
        double newOrientation = Double.parseDouble(orientationText.getText().toString());
        newOrientation = Math.toRadians(newOrientation);
        this.orientationOffset = newOrientation;

        // offset orientation by value entered
        orientationSet(this.northView, this.orientationAngle + this.orientationOffset);
        orientationSet(this.parentsHome, this.angleFromLocation + this.orientationAngle + this.orientationOffset);
    }

    public void onEditLabel(View view) {
        TextView newLabel = findViewById(R.id.editLabel);
        String label = newLabel.getText().toString();

        if(label.equals("")) {
            return;
        }

        this.parentsHome.setText(label);
        newLabel.setText("");

        SharedPreferences.Editor editor = this.prefs.edit();
        editor.putString("parentsLabel", label);

        editor.apply();
    }
}