package com.example.socialcompass;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.Nullable;
import androidx.core.util.Pair;

import android.util.Log;
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
        ConstraintLayout cLayout = ((Activity)context).findViewById(R.id.clock);
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
        if (userLoc.getValue() == null || thisLoc.getValue() == null || phoneAngle.getValue() == null) return;

        Double uLat = userLoc.getValue().first;
        Double uLon = userLoc.getValue().second;
        Double tLat = thisLoc.getValue().first;
        Double tLon = thisLoc.getValue().second;

        // calculate new constraints
        Double angle = Utilities.angleInActivity(uLat, uLon, tLat, tLon) + Math.toDegrees(phoneAngle.getValue());
        int distance_range = Utilities.distance_range(Utilities.distance(uLat, uLon, tLat, tLon));

        // update view
        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) view.getLayoutParams();
        layoutParams.circleAngle = -angle.floatValue();
        layoutParams.circleRadius = distance_range * 100;
        view.setLayoutParams(layoutParams);
    }
}
