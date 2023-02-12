package com.example.socialcompass;

import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class LocationService {
    private MutableLiveData<Location> Location;
    private ExecutorService backgroundThreadExecutor = Executors.newSingleThreadExecutor();
    private Future<?> registerFuture;
    protected LocationService() {
        this.Location = new MutableLiveData<>();
    }

    public void registerLocation(Location input) {
        this.registerFuture = backgroundThreadExecutor.submit(() -> {

            this.Location.postValue(input);
        });
    }

    public MutableLiveData<Location>  getLocations(){
        return this.Location;
    }
}
