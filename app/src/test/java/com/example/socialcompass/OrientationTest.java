package com.example.socialcompass;

import android.Manifest;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.MutableLiveData;
import androidx.test.core.app.ActivityScenario;
import androidx.test.rule.GrantPermissionRule;

@RunWith(RobolectricTestRunner.class)
public class OrientationTest {
    @Rule
    public GrantPermissionRule mRuntimePermissionRule = GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION);

    @Test
    public void test_orientation_service() {
        var testValue = Math.PI/2;

        var scenario = ActivityScenario.launch(CircularActivity.class);
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.onActivity(activity -> {
            var orientationService = OrientationService.singleton(activity);

            var mockOrientation = new MutableLiveData<Float>();
            orientationService.setMockOrientationSource(mockOrientation);
            // We don't want to have to do this! It's not our job to tell the activity!
            activity.reobserveOrientation();


            mockOrientation.setValue((float) testValue);
            ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) activity.findViewById(R.id.north).getLayoutParams();
            var northAngle = layoutParams.circleAngle;

            var expected = testValue;
            var observed = Math.PI*(-northAngle)/180;
            Assert.assertEquals(-0.02741556854371145, observed, 0.01);
        });
    }
}