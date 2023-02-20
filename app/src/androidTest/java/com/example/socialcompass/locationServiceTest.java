package com.example.socialcompass;

import static org.junit.Assert.assertEquals;

import android.content.Context;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.util.Pair;
import androidx.lifecycle.MutableLiveData;
import androidx.test.core.app.ActivityScenario;

import org.junit.Test;

public class locationServiceTest {
    @Test
    /**
     * This test is singlely for testing the passing in the mock data into
     * location service class and observe the angle that parent label change after
     * the location change
     */
    public void testForLocationService(){
        var pair = new Pair<Double,Double>(12.0,24.0);
        var scenario = ActivityScenario.launch(CircularActivity.class);
        scenario.onActivity(activity -> {
            var locationService = LocationService.singleton(activity);
            var mockLocation = new MutableLiveData<Pair<Double,Double>>();
            locationService.setMockOrientationData(mockLocation);
            activity.reobserveLocation();
            mockLocation.setValue(pair);
            TextView parent = activity.findViewById(R.id.ParentHome);
            var prefs = activity.getSharedPreferences("data", Context.MODE_PRIVATE);
            var expected = (float)(180*-Utilities.angleInActivity(pair.first,pair.second,prefs.getFloat("parentsLat", 0),prefs.getFloat("parentsLong", 0))/Math.PI);
            var layout = (ConstraintLayout.LayoutParams) parent.getLayoutParams();
            var actual  = layout.circleAngle;
            assertEquals(expected,actual,0);
        });
    }
}
