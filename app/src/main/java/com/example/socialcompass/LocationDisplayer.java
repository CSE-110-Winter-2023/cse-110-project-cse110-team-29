package com.example.socialcompass;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.Nullable;
import androidx.core.util.Pair;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

public class LocationDisplayer {
    private String uid;
    private TextView view;
    private LiveData<Pair<Double, Double>> userLoc;
    private LiveData<Pair<Double, Double>> thisLoc;
    private LiveData<Float> phoneAngle;

    public LocationDisplayer(Context context,
                             String uid,
                             String label,
                             LiveData<Pair<Double, Double>> userLoc,
                             LiveData<Pair<Double, Double>> thisLoc,
                             LiveData<Float> phoneAngle) {
        this.uid = uid;
        this.userLoc = userLoc;
        this.thisLoc = thisLoc;
        this.phoneAngle = phoneAngle;

        // create the view
        this.view = new TextView(context);
        this.view.setText(label);
        ConstraintLayout cLayout = ((Activity) context).findViewById(R.id.clock);
        cLayout.addView(this.view);

        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) this.view.getLayoutParams();
        layoutParams.width = ConstraintLayout.LayoutParams.WRAP_CONTENT;
        layoutParams.height = ConstraintLayout.LayoutParams.WRAP_CONTENT;
        layoutParams.circleConstraint = R.id.circle_1;
        layoutParams.circleRadius = 425;
        this.view.setLayoutParams(layoutParams);

        userLoc.observe((LifecycleOwner) context, new Observer<Pair<Double, Double>>() {
            @Override
            public void onChanged(Pair<Double, Double> doubleDoublePair) {
                updateView();
            }
        });

        thisLoc.observe((LifecycleOwner) context, new Observer<Pair<Double, Double>>() {
            @Override
            public void onChanged(Pair<Double, Double> doubleDoublePair) {
                updateView();
            }
        });

        phoneAngle.observe((LifecycleOwner) context, new Observer<Float>() {
            @Override
            public void onChanged(Float f) {
                //Log.i("new Angle", f.toString());
                updateView();
            }
        });
    }

    private void updateView() {
        if (userLoc.getValue() == null || thisLoc.getValue() == null || phoneAngle.getValue() == null)
            return;

        Double uLat = userLoc.getValue().first;
        Double uLon = userLoc.getValue().second;
        Double tLat = thisLoc.getValue().first;
        Double tLon = thisLoc.getValue().second;

        // calculate new constraints
        Double angle = Utilities.angleInActivity(uLat, uLon, tLat, tLon) + Math.toDegrees(phoneAngle.getValue());
        double distance = Utilities.distance(uLat, uLon, tLat, tLon);
        int distance_range = Utilities.distance_range(distance);

        // update view
        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) view.getLayoutParams();
        layoutParams.circleAngle = -angle.floatValue();


        layoutParams.circleRadius = radiusSet(distance_range, distance);
        view.setLayoutParams(layoutParams);
    }


    //dynamically set the distance between the center of the circle and the location of different labels
    //hardcode style
    private int radiusSet(int distance_range, double distance) {
        double radius;
        double numOfClickZoom = CircularActivity.getCircleCount();

        //four circles:
        //third: 405
        //first: 125
        //second: 270
        //fourth: 510

        //three circles:
        //first:185
        //second: 345
        //third: 510

        //two circles:
        //first: 295
        //second: 510

        if (numOfClickZoom == 3) {
            if (distance_range == 0)
                radius = 510 * distance;
            else
                radius = 510;
        } else if (numOfClickZoom == 2) {
            if (distance_range == 0) {
                //distance * inner circle radius / 1 mile
                radius = 295 * distance;
            } else if (distance_range == 1) {
                //inner circle radius + distance * (outer circle radius - inner circle radius) / 9 mile
                radius = 295 + distance * (510 - 295) / 9;
            } else
                radius = 510;
        } else if (numOfClickZoom == 1) {
            if (distance_range == 0) {
                //distance * inner circle radius / 1 mile
                radius = 185 * distance;
            } else if (distance_range == 1) {
                //inner circle radius + distance * (outer circle radius - inner circle radius) / 9 mile
                radius = 185 + distance * (345 - 185) / 9;
            } else if (distance_range == 2) {
                //middle circle radius + distance * (most outer circle radius - middle circle radius) / 490 mile
                radius = 345 + distance * (510 - 345) / 490;
            } else {
                radius = 510;
            }
        } else {
            if (distance_range == 0) {
                radius = 125 * distance;
            } else if (distance_range == 1) {
                radius = 125 + distance * (270 - 125) / 9;
            } else if (distance_range == 2) {
                radius = 270 + distance * (405 - 270) / 490;
            } else {
                //third circle radius + distance * 0.001 mile
                radius = 405 + distance * 0.001;
            }
        }

        return (int) radius;
    }
}
