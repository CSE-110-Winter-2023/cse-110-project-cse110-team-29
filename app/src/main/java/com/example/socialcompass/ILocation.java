package com.example.socialcompass;

import java.io.Serializable;

public class ILocation implements Serializable {
    private String label;
    private float latitude;
    private float longitude;

    public ILocation(String label, float latitude, float longitude) {
        this.label = label;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getLabel() {
        return label;
    }

    public float getLatitude() {
        return latitude;
    }

    public float getLongitude() {
        return longitude;
    }
}
