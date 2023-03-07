package com.example.socialcompass;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import java.io.Serializable;

public class ILocation implements Serializable {
    private String label;
    private float latitude;
    private float longitude;
    private TextView view;
    private double angleFromLocation;
    private double orientationAngle;

    public ILocation(String label, float latitude, float longitude, Context context) {
        this.label = label;
        this.latitude = latitude;
        this.longitude = longitude;

        this.view = new TextView(context);
        this.view.setText(label);
        ConstraintLayout cLayout = ((Activity)context).findViewById(R.id.clock);
        cLayout.addView(this.view);

        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) this.view.getLayoutParams();

        layoutParams.width = ConstraintLayout.LayoutParams.WRAP_CONTENT;
        layoutParams.height = ConstraintLayout.LayoutParams.WRAP_CONTENT;
        layoutParams.circleConstraint = R.id.circle_1;
        layoutParams.circleRadius = 425;

        this.view.setLayoutParams(layoutParams);
    }

    public String getLabel() {
        return label;
    }

    public float getLatitude() {
        return latitude;
    }

    public float getLongitude() {
        return longitude;
    }
    public View getTextView() {
        return this.view;
    }
    public double getAngleFromLocation() {
        return this.angleFromLocation;
    }
    public void setAngleFromLocation(double angleFromLocation) {
        this.angleFromLocation = angleFromLocation;
    }
    public void setOrientationAngle(double orientationAngle) {
        this.orientationAngle = orientationAngle;
    }
    public double getOrientationAngle() {
        return this.orientationAngle;
    }
}
