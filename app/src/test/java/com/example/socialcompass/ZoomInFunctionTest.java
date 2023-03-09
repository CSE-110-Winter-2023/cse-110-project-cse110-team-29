package com.example.socialcompass;
import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.TextView;
import com.example.socialcompass.CircularActivity;
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
public class ZoomInFunctionTest {
    @Rule
    public GrantPermissionRule mRuntimePermissionRule = GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION);
    @Test
    public void ZoomInTest() {
        try (ActivityScenario<CircularActivity> scenario = ActivityScenario.launch(CircularActivity.class)) {
            scenario.onActivity(activity -> {
                var zoomButt = activity.findViewById(R.id.zoom_in_btn);
                //check before zoom in the number of zoom click should be 0
                assertEquals(0,activity.getCircleCount());
                //check after the first click of zoom in
                zoomButt.performClick();
                assertEquals(1,activity.getCircleCount());
                var cir4 = activity.findViewById(R.id.circle_4);
                assertEquals(cir4.INVISIBLE,cir4.getVisibility());
                //check after the second click of zoom in
                zoomButt.performClick();
                assertEquals(2,activity.getCircleCount());
                var cir3 = activity.findViewById(R.id.circle_3);
                assertEquals(cir4.INVISIBLE,cir4.getVisibility());
                assertEquals(cir3.INVISIBLE,cir3.getVisibility());
                //check after the third click of zoom in
                zoomButt.performClick();
                assertEquals(3,activity.getCircleCount());
                var cir2 = activity.findViewById(R.id.circle_2);
                assertEquals(cir4.INVISIBLE,cir4.getVisibility());
                assertEquals(cir3.INVISIBLE,cir3.getVisibility());
                assertEquals(cir2.INVISIBLE,cir2.getVisibility());
                //check the max zoom in already click, and continue click on zoom in
                zoomButt.performClick();
                assertEquals(3,activity.getCircleCount());
                assertEquals(cir4.INVISIBLE,cir4.getVisibility());
                assertEquals(cir3.INVISIBLE,cir3.getVisibility());
                assertEquals(cir2.INVISIBLE,cir2.getVisibility());
            });
        }

    }
}
