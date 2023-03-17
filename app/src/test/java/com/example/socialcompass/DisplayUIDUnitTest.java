package com.example.socialcompass;
import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.GrantPermissionRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;
@RunWith(AndroidJUnit4.class)
public class DisplayUIDUnitTest {
    @Rule
    public GrantPermissionRule mRuntimePermissionRule = GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION);


    @Test
    public void DisplayUID() {
        try (ActivityScenario<InputActivity> scenario = ActivityScenario.launch(InputActivity.class)) {
            scenario.onActivity(activity -> {
                var view = (TextView)activity.findViewById(R.id.uidTextView);
                SharedPreferences prefs = activity.getSharedPreferences("codes", Context.MODE_PRIVATE);
                assertEquals(prefs.getString("public_code",""),view.getText().toString());
            });
        }
    }
}
