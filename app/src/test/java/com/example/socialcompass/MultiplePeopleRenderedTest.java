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
public class MultiplePeopleRenderedTest {
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
    public void test_friend_exist() {
        // set mock positions
        mockOrientation.setValue(0.0f);
        mockLocation.setValue(new Pair<Double,Double>(32.867604, -117.247244));

        // create mock friend
        final String mockUID = "uid";
        MutableLiveData<Friend> mockFriend = new MutableLiveData<>(new Friend(
                "pc",
                "Mock",
                99,
                10));
        activity.addMockLocationDisplayer(mockUID, "Mock", mockFriend);

        // check position constraints
        TextView friendLabel = activity.findViewById(mockUID.hashCode());

        //Test for friends who out of scope
        String name = friendLabel.getText().toString();
        Assert.assertEquals("•", name);

        final String mockUID2 = "uid2";
        MutableLiveData<Friend> mockFriend2 = new MutableLiveData<>(new Friend(
                "pc",
                "Mock2",
                -117.247244,
                32.867604));
        activity.addMockLocationDisplayer(mockUID2, "Mock2", mockFriend2);
        TextView friendLabel2 = activity.findViewById(mockUID2.hashCode());
        //Test for Friends in the scope
        String name2 = friendLabel2.getText().toString();
        Assert.assertEquals("Mock2", name2);

    }
}