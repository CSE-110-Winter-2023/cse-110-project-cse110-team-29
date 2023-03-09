package com.example.socialcompass;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.TextView;

import androidx.test.core.app.ActivityScenario;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.GrantPermissionRule;

import org.junit.Assert;
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
                TextView label = (TextView)activity.findViewById(R.id.labelName);
                label.setText("Home");
                TextView lat = (TextView)activity.findViewById(R.id.latitudeNum);
                lat.setText("14");
                TextView longi = (TextView) activity.findViewById(R.id.longitudeNum);
                longi.setText("16");
                View but = activity.findViewById(R.id.button2);
                but.performClick();
                SharedPreferences prefs = activity.getSharedPreferences("data",Context.MODE_PRIVATE);
                assertEquals("Home",prefs.getString("parentsLabel",""));
                Float lat1 = prefs.getFloat("parentsLat",0);
                Float longit = prefs.getFloat("parentsLong",0);
                assertEquals("14.0",lat1.toString());
                assertEquals("16.0",longit.toString());
            });
        }
    }
}
