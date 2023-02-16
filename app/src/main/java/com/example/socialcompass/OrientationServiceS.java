package com.example.socialcompass;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class OrientationServiceS implements SensorEventListener {
    private static OrientationServiceS instance;

    private final SensorManager sensorManager;
    private float[] accelerometerReading;
    private float[] magnetometerReading;
    private MutableLiveData<Float> azimuth;

    public OrientationServiceS(Activity activity) {
        this.azimuth = new MutableLiveData<>();
        this.sensorManager = (SensorManager) activity.getSystemService(Context.SENSOR_SERVICE);
        this.registerSensorListeners();
    }

    private void registerSensorListeners() {
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    public static OrientationServiceS singleton(Activity activity) {
        if (instance == null) {
            instance = new OrientationServiceS(activity);
        }
        return instance;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            accelerometerReading = event.values;
        }
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            magnetometerReading = event.values;
        }
        if (accelerometerReading != null && magnetometerReading != null) {
            onBothSensorDataAvailable();
        }
    }

    private void onBothSensorDataAvailable() {
        if (accelerometerReading == null || magnetometerReading == null) {
            throw new IllegalStateException("Both sensors must be available to compute orientation");
        }

        float[] r = new float[9];
        float[] i = new float[9];

        boolean success = SensorManager.getRotationMatrix(r, i, accelerometerReading, magnetometerReading);

        if (success) {
            float[] orientation = new float[3];
            SensorManager.getOrientation(r, orientation);
            this.azimuth.postValue(orientation[0]);
        }
    }

    public void unregisterSensorListeners() {
        sensorManager.unregisterListener(this);
    }

    public LiveData<Float> getOrientation() {
        return this.azimuth;
    }

    public void setMockOrientationSource(MutableLiveData<Float> mockDataSource) {
        unregisterSensorListeners();
        this.azimuth = mockDataSource;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}
