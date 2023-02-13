package com.example.socialcompass;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class LocationService implements LocationListener {
    public  static  LocationService instance;
    private Activity activity;

    private MutableLiveData<Pair<Double,Double>> locationValue;

    private final LocationManager locationManager;

    public static LocationService singleton(Activity activity) {
        if (instance == null) {
            instance = new LocationService(activity);
        }
        return  instance;
    }

    public LocationService(Activity activity) {
        this.locationValue = new MutableLiveData<>();
        this.activity = activity;
        this.locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        //Register sensor listeners
        this.registerLocationListener();
    }

    private void  registerLocationListener() {
        if (ActivityCompat.checkSelfPermission(activity,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            throw new IllegalStateException("App neds location permission to get latest location");
        }

        this.locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        this.locationValue.postValue(new Pair<Double,Double>(location.getLatitude(),location.getLongitude()));
    }

    private void unregisterLocationListener() {
        locationManager.removeUpdates(this);
    }

    public LiveData<Pair<Double,Double>> getLocation() {
        return this.locationValue;
    }

    public void setMockOrientationSource(MutableLiveData<Pair<Double,Double>> mockDataSource) {
        unregisterLocationListener();
        this.locationValue = mockDataSource;
    }


    @Override
    public void onProviderEnabled(@NonNull String provider) {
        LocationListener.super.onProviderEnabled(provider);
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        LocationListener.super.onProviderDisabled(provider);
    }
}