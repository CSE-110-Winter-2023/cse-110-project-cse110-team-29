package com.example.socialcompass;

import static androidx.test.InstrumentationRegistry.getContext;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import androidx.core.util.Pair;
import androidx.lifecycle.MutableLiveData;
import android.location.LocationManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class CircularActivity extends AppCompatActivity {
    private LocationService locationService;
    private OrientationService orientationService;

    private LocationManager locationManager;
    /**
     * In your Activity, create a LocationManager object and a MyLocationListener object.
     * Also, create a TextView object to display the time since the last GPS fix.
     * typescript
     *
     * public class MainActivity extends AppCompatActivity {
     *
     *     private LocationManager locationManager;
     *     private MyLocationListener locationListener;
     *     private TextView gpsStatusTextView;
     *     private long lastGpsFixTime;
     *
     *     @Override
     *     protected void onCreate(Bundle savedInstanceState) {
     *         super.onCreate(savedInstanceState);
     *         setContentView(R.layout.activity_main);
     *
     *         // Get a reference to the TextView that will display the GPS status
     *         gpsStatusTextView = findViewById(R.id.gps_status_text_view);
     *
     *         // Create a new LocationManager and LocationListener
     *         locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
     *         locationListener = new MyLocationListener();
     *
     *         // Request location updates from the LocationManager
     *         locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
     *     }
     *
     *     private class MyLocationListener implements LocationListener {
     *
     *         @Override
     *         public void onLocationChanged(Location location) {
     *             // Update the last GPS fix time
     *             lastGpsFixTime = System.currentTimeMillis();
     *
     *             // Do something with the new location
     *         }
     *
     *         @Override
     *         public void onStatusChanged(String provider, int status, Bundle extras) {
     *             // Do something when the status of the location provider changes
     *         }
     *
     *         @Override
     *         public void onProviderEnabled(String provider) {
     *             // Do something when the location provider is enabled
     *         }
     *
     *         @Override
     *         public void onProviderDisabled(String provider) {
     *             // Do something when the location provider is disabled
     *         }
     *     }
     *
     * }
     * In the onLocationChanged() method of your MyLocationListener class, update the user's location on the map.
     * You can use the Google Maps Android API to do this:
     *
     * @Override
     * public void onLocationChanged(Location location) {
     *     // Update the last GPS fix time
     *     lastGpsFixTime = System.currentTimeMillis();
     *
     *     // Do something with the new location
     *     LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
     *     mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
     * }
     *In the onCreate() method of your Activity,
     *set up a Timer object that will update the GPS status TextView every second:
     * // Set up a Timer to update the GPS status TextView
     * Timer timer = new Timer();
     * timer.schedule(new TimerTask() {
     *     @Override
     *     public void run() {
     *         long timeSinceLastGpsFix = System.currentTimeMillis() - lastGpsFixTime;
     *         final String gpsStatus;
     *         if (timeSinceLastGpsFix > 10000) {
     *             // If it's been more than 10 seconds since the last GPS fix, show a warning
     *             gpsStatus = "GPS signal lost";
     *         } else {
     *             gpsStatus = "GPS signal ok";
     *         }
     *         runOnUiThread(new Runnable() {
     *             @Override
     *             public void run() {
     *                 gpsStatusTextView.setText(gpsStatus);
     *             }
     *         });
     *     }
     * }, 0, 1000);
     */

    SharedPreferences prefs;

    // for a single saved location
    Location parentLocation;

    // multiple locations
    List<ILocation> locations;

    List<LocationDisplayer> locationDisplayers;

    private double orientationOffset;

    TextView northView;
    TextView timeView;
    private long gpstime;
    private int hours;
    private int mins;

    HashMap<Integer,ArrayList<ILocation>> location_ranges;
    //0-1,1-10,10-500,500+
    private int numOfClickZoom;

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
                            var time = (int)(timeSinceLastGPS/1000);
                            if(time>=60) {
                                hours = time / 3600;
                                mins = time / 60;
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
                            timeView.setText("");
                        }
                    }
                });
            }
        },0,1000);

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

    public void onClickZoomIn(View view) {
        if (numOfClickZoom < 3) {
            numOfClickZoom++;
        }
        setMultipleCircles(1); //1 represent it is from zoom in btn
    }

    public void onClickZoomOut(View view) {
        if (numOfClickZoom > 0) {
            numOfClickZoom--;
        }
        setMultipleCircles(0); //0 represent it is from zoom out btn
    }

    public void onClickBack(View view){
        Intent intent = new Intent(this, InputActivity.class);
        startActivity(intent);
    }

    private void setMultipleCircles(int source) {
        View circle4 = findViewById(R.id.circle_4);
        View circle3 = findViewById(R.id.circle_3);
        View circle2 = findViewById(R.id.circle_2);
        View circle1 = findViewById(R.id.circle_1);

        ViewGroup.LayoutParams layoutParams4 = circle4.getLayoutParams();
        ViewGroup.LayoutParams layoutParams3 = circle3.getLayoutParams();
        ViewGroup.LayoutParams layoutParams2 = circle2.getLayoutParams();
        ViewGroup.LayoutParams layoutParams1 = circle1.getLayoutParams();

        if(numOfClickZoom == 0) {
            if(source == 0)
                circle4.setVisibility(View.VISIBLE);

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


        if(numOfClickZoom == 1) {
            if (source == 1)
                circle4.setVisibility(View.INVISIBLE);
            if (source == 0)
                circle3.setVisibility(View.VISIBLE);

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


        if(numOfClickZoom == 2) {
            if (source == 1)
                circle3.setVisibility(View.INVISIBLE);
            if (source == 0)
                circle2.setVisibility(View.VISIBLE);

            layoutParams2.width = 1050;
            layoutParams2.height = 1050;
            circle2.setLayoutParams(layoutParams2);

            layoutParams1.width = 578;
            layoutParams1.height = 578;
            circle1.setLayoutParams(layoutParams1);
        }

        if(numOfClickZoom == 3) {
            if (source == 1)
                circle2.setVisibility(View.INVISIBLE);

            layoutParams1.width = 1050;
            layoutParams1.height = 1050;
            circle1.setLayoutParams(layoutParams1);
        }
    }
    //Function for getting the number of zoomclick for JunitTest
    public int getCircleCount()
    {
        return this.numOfClickZoom;
    }
}