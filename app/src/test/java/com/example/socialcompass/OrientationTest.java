package com.example.socialcompass;

import static org.robolectric.Shadows.shadowOf;

import android.Manifest;
import android.content.Context;
import android.os.Looper;
import android.view.ViewGroup;
import android.widget.TextView;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.LooperMode;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.util.Pair;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.test.core.app.ActivityScenario;
import androidx.test.rule.GrantPermissionRule;

import com.example.socialcompass.model.Friend;

@RunWith(RobolectricTestRunner.class)
public class OrientationTest {
    private CircularActivity activity;
    private MutableLiveData<Float> mockOrientation;
    private MutableLiveData<Pair<Double, Double>> mockLocation;

    @Rule
    public GrantPermissionRule mRuntimePermissionRule = GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION);
    @Before
    public void setup() {
        var scenario = ActivityScenario.launch(CircularActivity.class);
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.onActivity(activity -> {
            // set up mocks
            var orientationService = OrientationService.singleton(activity);
            var locationService = LocationService.singleton(activity);

            mockOrientation = new MutableLiveData<Float>();
            mockLocation = new MutableLiveData<Pair<Double, Double>>();
            orientationService.setMockOrientationSource(mockOrientation);
            locationService.setMockOrientationData(mockLocation);

            this.activity = activity;
        });
    }

    @Test
    public void test_user_movement() {
        // set mock positions
        mockOrientation.setValue(0.0f);
        mockLocation.setValue(new Pair<Double,Double>(0.0, 0.0));

        // create mock friend
        final String mockUID = "uid";
        LiveData<Friend> mockFriend = new MutableLiveData<>(new Friend(
                "pc",
                "Mock",
                0,
                10));
        activity.addMockLocationDisplayer(mockUID, "Mock", mockFriend);

        // check position constraints
        TextView friendLabel = activity.findViewById(mockUID.hashCode());
        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) friendLabel.getLayoutParams();
        var angle = layoutParams.circleAngle;
        Assert.assertEquals(0, angle, 0.1);

        // adjust user location
        mockLocation.setValue(new Pair<Double,Double>(0.0, 5.0));

        // check new position constraints
        layoutParams = (ConstraintLayout.LayoutParams) friendLabel.getLayoutParams();
        angle = layoutParams.circleAngle;
        Assert.assertEquals(-333.6975, angle, 0.1);
    }

    @Test
    public void test_friend_movement() {
        // set mock positions
        mockOrientation.setValue(0.0f);
        mockLocation.setValue(new Pair<Double,Double>(0.0, 0.0));

        // create mock friend
        final String mockUID = "uid";
        MutableLiveData<Friend> mockFriend = new MutableLiveData<>(new Friend(
                "pc",
                "Mock",
                0,
                10));
        activity.addMockLocationDisplayer(mockUID, "Mock", mockFriend);

        // check position constraints
        TextView friendLabel = activity.findViewById(mockUID.hashCode());
        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) friendLabel.getLayoutParams();
        var angle = layoutParams.circleAngle;
        Assert.assertEquals(0, angle, 0.1);

        // adjust friend location
        mockFriend.postValue(new Friend(
                "pc",
                "Mock",
                30,
                10));

        // check new position constraints
        shadowOf(Looper.getMainLooper()).idle();
        layoutParams = (ConstraintLayout.LayoutParams) friendLabel.getLayoutParams();
        angle = layoutParams.circleAngle;
        Assert.assertEquals(-70.574722, angle, 0.1);
    }
}