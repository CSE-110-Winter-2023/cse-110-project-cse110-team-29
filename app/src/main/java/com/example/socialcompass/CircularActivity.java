package com.example.socialcompass;

import static androidx.test.InstrumentationRegistry.getContext;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.LiveData;


import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;

import androidx.core.util.Pair;
import android.location.LocationManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.socialcompass.model.Friend;
import com.example.socialcompass.model.FriendDatabase;
import com.example.socialcompass.model.FriendRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class CircularActivity extends AppCompatActivity {
    private LocationService locationService;
    private OrientationService orientationService;

    private LocationManager locationManager;

    SharedPreferences prefs;

    // for a single saved location
    Location parentLocation;

    // multiple locations
    List<ILocation> locations;

    LiveData<List<Friend>> friends;

    List<LocationDisplayer> locationDisplayers;

    private double orientationOffset;

    TextView northView;

    FriendRepository friendRepo;
    TextView timeView;
    private long gpstime;
    private int hours;
    private int mins;


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

        //this.northView = findViewById(R.id.north);

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
        /*
        for (ILocation data : locations) {
            MutableLiveData<Pair<Double, Double>> thisLoc = new MutableLiveData<>();
            thisLoc.setValue(new Pair<>((double) data.getLatitude(), (double) data.getLongitude()));
            locationDisplayers.add(new LocationDisplayer(this, data.getLabel(), data.getLabel(), locationService.getLocation(), thisLoc, orientationService.getOrientation()));
        }*/

        locationDisplayers = new ArrayList<>();

        friendRepo = new FriendRepository(FriendDatabase.provide(this).getDao());
        List<Friend> friends = friendRepo.getAllLocal();
        Log.d("hey", friends.toString());

        friends = friends == null ? new ArrayList<Friend>() : friends;

        for (Friend f : friends) {
            LiveData<Friend> liveFriend = friendRepo.getSynced(f.getUid());
            locationDisplayers.add(new LocationDisplayer(
                    this,
                    f.getUid(),
                    f.getName(),
                    locationService.getLocation(),
                    liveFriend,
                    orientationService.getOrientation()
            ));
        }

        ImageView gpsDot = findViewById(R.id.GPSSignal);
        timeView = findViewById(R.id.lostTime);
        gpstime = locationService.getLastGPSTime();
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        var timeSinceLastGPS = System.currentTimeMillis()-gpstime;
                        //long timeSinceLastGPS = System.currentTimeMillis()-gpstime;
                        if(!locationService.getLocationManager().isProviderEnabled(locationService.getLocationManager().GPS_PROVIDER)) {
                            gpsDot.clearColorFilter();
                            var time = (timeSinceLastGPS/1000);
                            if(time>=60) {
                                hours = (int)(time / 3600);
                                mins = (int)(time / 60);
                                var lostTime = hours + "hours " + mins + "mins ";
                                timeView.setText(lostTime);
                            }
                            else{
                                var lostTime = time+"s";
                                timeView.setText(lostTime);
                            }
                        }
                        else{
                            gpsDot.setColorFilter(Color.GREEN, PorterDuff.Mode.MULTIPLY);
                            gpstime = locationService.getLastGPSTime();
                            timeView.setText("");
                        }
                    }
                });
            }
        },0,1000);



        //public_code = friend.getPublicCode();
        //repo.getSynced(public_code)


        //Story 18: Default Zoom is inner two levels
        setMultipleCircles();
    }

    protected void onResume() {
        super.onResume();

        locationDisplayers = new ArrayList<>();
        friendRepo = new FriendRepository(FriendDatabase.provide(this).getDao());

        List<Friend> friends = friendRepo.getAllLocal();
        Log.d("hey", friends.toString());
        friends = friends == null ? new ArrayList<Friend>() : friends;
        for (Friend f : friends) {
            LiveData<Friend> liveFriend = friendRepo.getSynced(f.getUid());
            locationDisplayers.add(new LocationDisplayer(
                    this,
                    f.getUid(),
                    f.getName(),
                    locationService.getLocation(),
                    liveFriend,
                    orientationService.getOrientation()
            ));
        }
    }

    private void setUpOrientationAndLocation() {
        locationService = LocationService.singleton(this);
        orientationService = OrientationService.singleton(this);
        this.prefs = getSharedPreferences("data", MODE_PRIVATE);
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
}