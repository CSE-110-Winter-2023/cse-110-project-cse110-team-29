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
import androidx.lifecycle.MutableLiveData;

import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CircularActivity extends AppCompatActivity {
    private LocationService locationService;
    private OrientationService orientationService;

    SharedPreferences prefs;

    // for a single saved location
    Location parentLocation;

    // multiple locations
    List<ILocation> locations;

    List<LocationDisplayer> locationDisplayers;

    private double orientationOffset;

    TextView northView;

    HashMap<Integer,ArrayList<ILocation>> location_ranges;
    //0-1,1-10,10-500,500+
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circular);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 200);
        }

        // if(!prefs.contains("parentsLabel") || !prefs.contains("parentsLat") || !prefs.contains("parentsLong")) {
        //    Intent inputIntent = new Intent(this, InputActivity.class);
        //    startActivity(inputIntent);
        // }

        this.northView = findViewById(R.id.north);

        // this.parentLocation = new Location(findViewById(R.id.ParentHome), prefs.getFloat("parentsLong", 0), prefs.getFloat("parentsLat", 0));
        // this.parentLocation.updateLabel(prefs.getString("parentsLabel",""));

        locations = new ArrayList<>();

        locations.add(new ILocation("test1", 75, 200, this));
        locations.add(new ILocation("Sea World", 32.7641112f, -117.2284536f, this));
        locations.add(new ILocation("Geisel", 32.8810965f, -117.2397546f, this));

        location_ranges = new HashMap<>();

        setUpOrientationAndLocation();
        observeOrientationAndLocation();

        // temp code mocking other users
        locationDisplayers = new ArrayList<>();
        for (ILocation data : locations) {
            MutableLiveData<Pair<Double, Double>> thisLoc = new MutableLiveData<>();
            thisLoc.setValue(new Pair<>((double)data.getLatitude(), (double)data.getLongitude()));
            locationDisplayers.add(new LocationDisplayer(this, data.getLabel(), data.getLabel(), locationService.getLocation(), thisLoc, orientationService.getOrientation()));
        }
    }

    private void setUpOrientationAndLocation() {
        locationService = LocationService.singleton(this);
        orientationService = OrientationService.singleton(this);
        //this.prefs = getSharedPreferences("data", MODE_PRIVATE);
    }

    private void observeOrientationAndLocation() {
        this.reobserveLocation();
        this.reobserveOrientation();
    }

    public void reobserveOrientation() {
        LiveData<Float> orientationData = orientationService.getOrientation();
        orientationData.observe(this, this::onOrientationChanged);
    }

    public void reobserveLocation() {
        LiveData<Pair<Double, Double>> locationData = locationService.getLocation();
        locationData.observe(this, this::onLocationChanged);
    }

    private void onOrientationChanged(Float orientation) {
        /*
        for(ILocation location : this.locations) {
            if (orientation == location.getOrientationAngle()) {
                return;
            }

            Log.d("ORIENTATION", location.getLabel());

            location.setOrientationAngle(orientation);

            orientationSet(location.getTextView(), location.getAngleFromLocation() +
                    orientation);
        }

        orientationSet(this.northView, Double.valueOf(orientation));
        */

    }

    private void onLocationChanged(Pair<Double, Double> userLocation) {
        /*
        Double userLat = userLocation.first, userLong = userLocation.second;

        location_ranges.clear();
        for(ILocation location : this.locations) {
            Double newAngle = Utilities.angleInActivity(userLat, userLong, location.getLatitude(),
                    location.getLongitude());

            if(newAngle == location.getAngleFromLocation()) {
                return;
            }

            Log.d("LOCATION", location.getLabel());

            location.setAngleFromLocation(newAngle);

            orientationSet(location.getTextView(), newAngle);

            Double location_distance = Utilities.distance(userLat, userLong, location.getLatitude(),
                    location.getLongitude());
            if (location_ranges.get(Utilities.distance_range(location_distance) )== null){
                location_ranges.put(Utilities.distance_range(location_distance), new ArrayList<ILocation>());
            }
            location_ranges.get(Utilities.distance_range(location_distance)).add(location);


        }
        */

    }

    public void onEditOrientation(View view) {
        TextView orientationText = findViewById(R.id.editOrientation);
        double newOrientation = Double.parseDouble(orientationText.getText().toString());

        this.orientationOffset = newOrientation;

        // offset orientation by value entered
        orientationSet(this.northView, this.parentLocation.getOrientationAngle() + this.orientationOffset);
        orientationSet(this.parentLocation.getTextView(), this.parentLocation.getAngleFromLocation() +
                        this.parentLocation.getOrientationAngle() + this.orientationOffset);
    }

    public void onEditLabel(View view) {
        TextView newLabel = findViewById(R.id.editLabel);
        String label = newLabel.getText().toString();

        if(label.equals("")) {
            return;
        }

        this.parentLocation.updateLabel(label);
        newLabel.setText("");

        //SharedPreferences.Editor editor = this.prefs.edit();
        //editor.putString("parentsLabel", label);

        //editor.apply();
    }

    private void orientationSet(View label, Double degree) {
        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) label.getLayoutParams();

        layoutParams.circleAngle = degree.floatValue();

        label.setLayoutParams(layoutParams);
    }
}