package com.example.socialcompass;

import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AppCompatActivity;
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

import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.socialcompass.model.Friend;
import com.example.socialcompass.model.FriendDatabase;
import com.example.socialcompass.model.FriendRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class CircularActivity extends AppCompatActivity {
    private LocationService locationService;
    private OrientationService orientationService;

    List<LocationDisplayer> locationDisplayers;

    FriendRepository friendRepo;
    TextView timeView;

    private long gpstime;
    private int hours;
    private int mins;

    private static int numOfClickZoom = 2;

    private String endpoint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circular);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 200);
        }

        setUpOrientationAndLocation();

        locationDisplayers = new ArrayList<>();

        SharedPreferences preferences = getSharedPreferences("codes", MODE_PRIVATE);

        this.endpoint = preferences.getString("endpoint", null);

        friendRepo = new FriendRepository(FriendDatabase.provide(this).getDao());
        List<Friend> friends = friendRepo.getAllLocal();

        friends = friends == null ? new ArrayList<Friend>() : friends;

        for (Friend f : friends) {
            LiveData<Friend> liveFriend = friendRepo.getSynced(this.endpoint, f.getUid());
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
                        var timeSinceLastGPS = System.currentTimeMillis() - gpstime;
                        //long timeSinceLastGPS = System.currentTimeMillis()-gpstime;
                        if (!locationService.getLocationManager().isProviderEnabled(locationService.getLocationManager().GPS_PROVIDER)) {
                            gpsDot.clearColorFilter();
                            var time = (timeSinceLastGPS / 1000);
                            if (time >= 60) {
                                hours = (int) (time / 3600);
                                mins = (int) (time / 60);
                                var lostTime = hours + "hours " + mins + "mins ";
                                timeView.setText(lostTime);
                            } else {
                                var lostTime = time + "s";
                                timeView.setText(lostTime);
                            }
                        } else {
                            gpsDot.setColorFilter(Color.GREEN, PorterDuff.Mode.MULTIPLY);
                            gpstime = locationService.getLastGPSTime();
                            timeView.setText("");
                        }
                    }
                });
            }
        }, 0, 1000);

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
            LiveData<Friend> liveFriend = friendRepo.getSynced(this.endpoint, f.getUid());
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
    }

    public void onEditEndpoint(View view) {
        EditText newEndpointView = findViewById(R.id.editEndpoint);

        String newEndpoint = newEndpointView.getText().toString();

        SharedPreferences preferences = getSharedPreferences("codes", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("endpoint", newEndpoint);
        editor.apply();
        this.endpoint = newEndpoint;
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

    public LocationService getLocationService(){return this.locationService;}
    @Override
    protected void onDestroy() {
        super.onDestroy();
        orientationService.unregisterSensorListeners();
        locationService.unregisterLocationListener();
    }

    @VisibleForTesting
    public void addMockLocationDisplayer(String uid, String label, LiveData<Friend> f) {
        locationDisplayers.add(new LocationDisplayer(
                this,
                uid,
                label,
                locationService.getLocation(),
                f,
                orientationService.getOrientation()
        ));
    }
}