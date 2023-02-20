package com.example.socialcompass;

import android.view.View;
import android.widget.TextView;

public class Location {
    private double orientationAngle;
    private double angleFromLocation;
    private double longitude;
    private double latitude;
    private TextView view;

    public Location(TextView view, double longitude, double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.view = view;
    }

    public double getLongitude() {
        return this.longitude;
    }

    public double getLatitude() {
        return this.latitude;
    }

    public double getOrientationAngle() {
        return this.orientationAngle;
    }

    public double getAngleFromLocation() {
        return this.angleFromLocation;
    }

    public void setOrientationAngle(double orientationAngle) {
        this.orientationAngle = orientationAngle;
    }

    public void setAngleFromLocation(double angleFromLocation) {
        this.angleFromLocation = angleFromLocation;
    }

    public void updateLabel(String text) {
        this.view.setText(text);
    }

    public View getTextView() {
        return this.view;
    }
}
