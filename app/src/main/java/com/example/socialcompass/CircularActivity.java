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
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.socialcompass.model.Friend;
import com.example.socialcompass.model.FriendDatabase;
import com.example.socialcompass.model.FriendRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CircularActivity extends AppCompatActivity {
    private LocationService locationService;
    private OrientationService orientationService;

    private SharedPreferences preferences;

    // for a single saved location
    Location parentLocation;

    // multiple locations
    private LiveData<List<Friend>> friends;

    private LiveData<Friend> currentUser;

    private FriendRepository friendRepo;

    List<LocationDisplayer> locationDisplayers;

    private double orientationOffset;

    TextView northView;


    HashMap<Integer, ArrayList<ILocation>> location_ranges;
    //0-1,1-10,10-500,500+
    private static int numOfClickZoom = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circular);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 200);
        }

        //if(!prefs.contains("parentsLabel") || !prefs.contains("parentsLat") || !prefs.contains("parentsLong")) {
        // Intent inputIntent = new Intent(this, InputActivity.class);
        // startActivity(inputIntent);
        //}

        this.northView = findViewById(R.id.north);

        friendRepo = new FriendRepository(FriendDatabase.provide(this).getDao());

        preferences = getPreferences(MODE_PRIVATE);

        // get public ID of current user
        currentUser = friendRepo.getLocal(preferences.getString("Public", ""));
        friends = friendRepo.getAllLocal();

        location_ranges = new HashMap<>();

        setUpOrientationAndLocation();
        observeOrientationAndLocation();

        // temp code mocking other users
//        locationDisplayers = new ArrayList<>();
//        for (ILocation data : friends) {
//            MutableLiveData<Pair<Double, Double>> thisLoc = new MutableLiveData<>();
//            thisLoc.setValue(new Pair<>((double)data.getLatitude(), (double)data.getLongitude()));
//            locationDisplayers.add(new LocationDisplayer(this, data.getLabel(), data.getLabel(), locationService.getLocation(), thisLoc, orientationService.getOrientation()));
//        }
    }

    public void updateFriendLocations() {
        // use this method in orientationChange and locationChange
        // TODO: also call patch to update users info locally and remotely

        //Story 18: Default Zoom is inner two levels
        setMultipleCircles();
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

        if (label.equals("")) {
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

    public void onClickZoomIn(View view) {
        if (numOfClickZoom < 3) {
            numOfClickZoom++;
        }
        setMultipleCircles();
    }

    public void onClickZoomOut(View view) {
        if (numOfClickZoom > 0) {
            numOfClickZoom--;
        }
        setMultipleCircles();
    }

    public void onClickBack(View view){
        Intent intent = new Intent(this, InputActivity.class);
        startActivity(intent);
    }

    private void setMultipleCircles() {
        View circle4 = findViewById(R.id.circle_4);
        View circle3 = findViewById(R.id.circle_3);
        View circle2 = findViewById(R.id.circle_2);
        View circle1 = findViewById(R.id.circle_1);

        ViewGroup.LayoutParams layoutParams4 = circle4.getLayoutParams();
        ViewGroup.LayoutParams layoutParams3 = circle3.getLayoutParams();
        ViewGroup.LayoutParams layoutParams2 = circle2.getLayoutParams();
        ViewGroup.LayoutParams layoutParams1 = circle1.getLayoutParams();

        if (numOfClickZoom == 0) {

            circle4.setVisibility(View.VISIBLE);
            circle3.setVisibility(View.VISIBLE);
            circle2.setVisibility(View.VISIBLE);
            circle1.setVisibility(View.VISIBLE);

            layoutParams4.width = 1050;
            layoutParams4.height = 1050;
            circle4.setLayoutParams(layoutParams4);

            layoutParams3.width = 788;
            layoutParams3.height = 788;
            circle3.setLayoutParams(layoutParams3);

            layoutParams2.width = 525;
            layoutParams2.height = 525;
            circle2.setLayoutParams(layoutParams2);

            layoutParams1.width = 263;
            layoutParams1.height = 263;
            circle1.setLayoutParams(layoutParams1);
        }

        if (numOfClickZoom == 1) {

            circle4.setVisibility(View.INVISIBLE);
            circle3.setVisibility(View.VISIBLE);
            circle2.setVisibility(View.VISIBLE);
            circle1.setVisibility(View.VISIBLE);

            layoutParams3.width = 1050;
            layoutParams3.height = 1050;
            circle3.setLayoutParams(layoutParams3);

            layoutParams2.width = 735;
            layoutParams2.height = 735;
            circle2.setLayoutParams(layoutParams2);

            layoutParams1.width = 394;
            layoutParams1.height = 394;
            circle1.setLayoutParams(layoutParams1);
        }

        if (numOfClickZoom == 2) {
            circle4.setVisibility(View.INVISIBLE);
            circle3.setVisibility(View.INVISIBLE);
            circle2.setVisibility(View.VISIBLE);
            circle1.setVisibility(View.VISIBLE);

            layoutParams2.width = 1050;
            layoutParams2.height = 1050;
            circle2.setLayoutParams(layoutParams2);

            layoutParams1.width = 578;
            layoutParams1.height = 578;
            circle1.setLayoutParams(layoutParams1);
        }

        if (numOfClickZoom == 3) {
            circle4.setVisibility(View.INVISIBLE);
            circle3.setVisibility(View.INVISIBLE);
            circle2.setVisibility(View.INVISIBLE);
            circle1.setVisibility(View.VISIBLE);

            layoutParams1.width = 1050;
            layoutParams1.height = 1050;
            circle1.setLayoutParams(layoutParams1);
        }
    }

    //Function for getting the number of zoomclick for JunitTest
    public static int getCircleCount() {
        return numOfClickZoom;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        orientationService.unregisterSensorListeners();
        locationService.unregisterLocationListener();
    }
}