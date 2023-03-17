package com.example.socialcompass;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.Nullable;
import androidx.core.util.Pair;

import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.example.socialcompass.model.Friend;

public class LocationDisplayer {
    private String uid;
    private TextView view;
    private LiveData<Pair<Double, Double>> userLoc;
    private LiveData<Friend> friend;
    private LiveData<Float> phoneAngle;

    private String label; //user for setNormalText
    private int offset;
    private boolean stackable;
    private CircularActivity context;

    public LocationDisplayer(CircularActivity context,
                             String uid,
                                 String label,
                             LiveData<Pair<Double, Double>> userLoc,
                             LiveData<Friend> friend,
                             LiveData<Float> phoneAngle) {
        this.uid = uid;
        this.userLoc = userLoc;
        this.friend = friend;
        this.phoneAngle = phoneAngle;
        this.context = context;
        this.label = label;
        this.stackable = true;

        // create the view
        this.view = new TextView(context);
        this.view.setText(label);
        this.view.setId(uid.hashCode());
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
                // updateView();
                stackable = true;
            }
        });

        friend.observe((LifecycleOwner) context, new Observer<Friend>() {
            @Override
            public void onChanged(Friend f) {
                // updateView();
                stackable = true;
            }
        });

        phoneAngle.observe((LifecycleOwner) context, new Observer<Float>() {
            @Override
            public void onChanged(Float f) {
                //Log.i("new Angle", f.toString());
                // updateView();
                stackable = true;
            }
        });
    }

    // temp changed from private to public
    public void updateView() {
        if (userLoc.getValue() == null || friend.getValue() == null || phoneAngle.getValue() == null)
            return;

        Double uLat = userLoc.getValue().first;
        Double uLon = userLoc.getValue().second;
        Double tLat = friend.getValue().getLatitude();
        Double tLon = friend.getValue().getLongitude();

        // calculate new constraints
        Double angle = Utilities.angleInActivity(uLat, uLon, tLat, tLon) + Math.toDegrees(phoneAngle.getValue());
        double distance = Utilities.distance(uLat, uLon, tLat, tLon);
        int distance_range = Utilities.distance_range(distance);

        // update view
        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) view.getLayoutParams();
        layoutParams.circleAngle = -angle.floatValue();

        layoutParams.circleRadius = getRadius(distance_range, distance) + offset;

        setText(distance_range);
    }

    private void setText(int distance_range) {
        double numOfClickZoom = CircularActivity.getCircleCount();

        if (numOfClickZoom == 3) {
            if (distance_range != 0) {
                setDotView(view);
            } else
                setNormalView(view);
        } else if (numOfClickZoom == 2) {
            if (distance_range != 0 && distance_range != 1) {
                setDotView(view);
            } else
                setNormalView(view);
        } else if (numOfClickZoom == 1) {
            if (distance_range == 3) {
                setDotView(view);
            } else
                setNormalView(view);
        } else
            setNormalView(view);
    }

    private void setDotView(TextView view) {
        view.setText("•");
        view.setTextSize(25);
        view.setTextColor(Color.BLUE);
    }

    private void setNormalView(TextView view) {
        view.setText(label);
        view.setTextSize(15);
        view.setTextColor(Color.BLACK);
    }

    //dynamically set the distance between the center of the circle and the location of different labels
    //hardcode style
    private int getRadius(int distance_range, double distance) {
        double radius;
        double numOfClickZoom = CircularActivity.getCircleCount();

        //four circles:
        //first: 132
        //second: 263
        //third: 394
        //fourth: 525

        //three circles:
        //first:197
        //second: 368
        //third: 525

        //two circles:
        //first: 289
        //second: 525

        //one circles:
        //first: 525

        if (numOfClickZoom == 3) {
            if (distance_range == 0)
                radius = 525 * distance;
            else
                radius = 525;
        } else if (numOfClickZoom == 2) {
            if (distance_range == 0) {
                //distance * inner circle radius / 1 mile
                radius = 289 * distance;
            } else if (distance_range == 1) {
                //inner circle radius + distance * (outer circle radius - inner circle radius) / 9 mile
                radius = 289 + distance * (525 - 289) / 9;
            } else
                radius = 525;
        } else if (numOfClickZoom == 1) {
            if (distance_range == 0) {
                //distance * inner circle radius / 1 mile
                radius = 197 * distance;
            } else if (distance_range == 1) {
                //inner circle radius + distance * (outer circle radius - inner circle radius) / 9 mile
                radius = 197 + distance * (368 - 197) / 9;
            } else if (distance_range == 2) {
                //middle circle radius + distance * (most outer circle radius - middle circle radius) / 490 mile
                radius = 368 + distance * (525 - 368) / 490;
            } else {
                radius = 525;
            }
        } else {
            if (distance_range == 0) {
                radius = 132 * distance;
            } else if (distance_range == 1) {
                radius = 132 + distance * (263 - 132) / 9;
            } else if (distance_range == 2) {
                radius = 263 + distance * (394 - 263) / 490;
            } else {
                //third circle radius + distance * 0.001 mile
                radius = 394 + distance * 0.001;
            }
        }

        return (int) radius;
    }

    public TextView getView() { return this.view; }

    public void setOffset(int length) {
            this.offset = length;
    }

    public int getOffset(){ return this.offset; }

    public void removeThis() {
        ConstraintLayout cLayout = ((Activity) context).findViewById(R.id.clock);
        cLayout.removeView(this.view);
    }

    public void resetTruncation() {
        view.setText(label);
    }

    public void truncate() {
        view.setText(label.substring(0, label.length()/3));
    }
}
