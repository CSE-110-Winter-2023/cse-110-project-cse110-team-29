package com.example.socialcompass;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.TextView;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.GrantPermissionRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;
@RunWith(AndroidJUnit4.class)
public class TestForLoadInput {

    @Rule
    public GrantPermissionRule mRuntimePermissionRule = GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION);

    @Test
    /**
     * Test for checking whether the user input can be perfectly load before
     * we put it into the compass.
     */
    public void LoadInputTest(){

        try(ActivityScenario<InputActivity>scenario = ActivityScenario.launch(InputActivity.class)){
            scenario.onActivity(activity -> {
                TextView label = (TextView)activity.findViewById(R.id.uid);
                label.setText("Home");
                assertEquals("Home",activity.getFriendPublicUid());
            });
        }
    }
}
