package com.example.socialcompass;
import androidx.core.util.Pair;

import android.content.Context;
import android.widget.EditText;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.MutableLiveData;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class orientationServiceTest {
    @Test
    /**
     * this test is for testing only orientation service by mocking a fake data
     * into the observer in order to check whether the parent label and the North
     * label will appear at the correct location(angle) of the compass.
     */
    public void test_orientation() {
        var testValue = (float) 30.0;
        try (ActivityScenario<CircularActivity> scenario = ActivityScenario.launch(CircularActivity.class)) {
            scenario.onActivity(activity -> {
                var orientationService = OrientationService.singleton(activity);
                var mockOrientation = new MutableLiveData<Float>();
                orientationService.setMockOrientationSource(mockOrientation);
                activity.reobserveOrientation();
                mockOrientation.setValue(testValue);
                TextView north = activity.findViewById(R.id.north);
                TextView parent = activity.findViewById(R.id.ParentHome);
                ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) north.getLayoutParams();
                ConstraintLayout.LayoutParams layoutParamsParent = (ConstraintLayout.LayoutParams) parent.getLayoutParams();
                var expected = (float) (180 * -testValue / (Math.PI));
                var angleNorth = layoutParams.circleAngle;
                var angleParent = layoutParamsParent.circleAngle;
                assertEquals(expected, angleNorth, 0);
                assertEquals(expected, angleParent, 0);
            });
        }
    }


}
