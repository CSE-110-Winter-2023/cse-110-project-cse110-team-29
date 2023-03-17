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
public class TruncateTest {
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
    public void test_labels_stacked() {

            // set mock positions
            mockOrientation.setValue(0.0f);
            mockLocation.setValue(new Pair<Double,Double>(32.867604, -117.247244));

            // create mock friend
            final String mockUID = "uid";
            MutableLiveData<Friend> seaWorld = new MutableLiveData<>(new Friend(
                    "pc",
                    "SeaWorld",
                    -117.229457,
                    32.763903));
            activity.addMockLocationDisplayer(mockUID, "SeaWorld", seaWorld);

            // check position constraints
            TextView seaWorldLabel = activity.findViewById(mockUID.hashCode());

            //Test for friends who out of scope
            String name = seaWorldLabel.getText().toString();
            Assert.assertEquals("SeaWorld", name);

            final String mockUID2 = "uid2";
            MutableLiveData<Friend> missionBay = new MutableLiveData<>(new Friend(
                    "pc",
                    "MissionBay",
                    -117.226456,
                    32.777813));
            activity.addMockLocationDisplayer(mockUID2, "MissionBay", missionBay);
            TextView missionBayLabel = activity.findViewById(mockUID2.hashCode());
            //Test for Friends in the scope
            String name2 = missionBayLabel.getText().toString();
            Assert.assertEquals("MissionBay", name2);

            // check position constraints
            ConstraintLayout.LayoutParams seaWorldLabelLayoutParams = (ConstraintLayout.LayoutParams) seaWorldLabel.getLayoutParams();


            ConstraintLayout.LayoutParams missionBayLayoutParams = (ConstraintLayout.LayoutParams) missionBayLabel.getLayoutParams();

    }

}