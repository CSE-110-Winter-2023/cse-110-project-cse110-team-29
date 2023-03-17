package com.example.socialcompass;
import android.Manifest;
import android.view.View;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.GrantPermissionRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;
@RunWith(AndroidJUnit4.class)
public class ZoomInOutFunctionTest {
    @Rule
    public GrantPermissionRule mRuntimePermissionRule = GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION);
    @Test
    public void ZoomInTest() {
        try (ActivityScenario<CircularActivity> scenario = ActivityScenario.launch(CircularActivity.class)) {
            scenario.onActivity(activity -> {
                var zoomInButt = activity.findViewById(R.id.zoom_in_btn);
                var cir4 = activity.findViewById(R.id.circle_4);
                var cir3 = activity.findViewById(R.id.circle_3);
                var cir2 = activity.findViewById(R.id.circle_2);

                //check before zoom in the number of zoom click should be 0
                assertEquals(2,activity.getCircleCount());

                //check after the first click of zoom in
                zoomInButt.performClick();
                assertEquals(3,activity.getCircleCount());

                assertEquals(View.INVISIBLE,cir4.getVisibility());
                assertEquals(View.INVISIBLE,cir3.getVisibility());
                assertEquals(View.INVISIBLE,cir2.getVisibility());

                zoomInButt.performClick();
                assertEquals(3,activity.getCircleCount());
                assertEquals(View.INVISIBLE,cir4.getVisibility());
                assertEquals(View.INVISIBLE,cir3.getVisibility());
                assertEquals(View.INVISIBLE,cir2.getVisibility());

                var zoomOutBtn = activity.findViewById(R.id.zoom_out_btn);
                zoomOutBtn.performClick();
                assertEquals(2,activity.getCircleCount());
                assertEquals(View.INVISIBLE,cir4.getVisibility());
                assertEquals(View.INVISIBLE,cir3.getVisibility());
                assertEquals(View.VISIBLE,cir2.getVisibility());

                zoomOutBtn.performClick();
                assertEquals(1,activity.getCircleCount());
                assertEquals(View.INVISIBLE,cir4.getVisibility());
                assertEquals(View.VISIBLE,cir3.getVisibility());
                assertEquals(View.VISIBLE,cir2.getVisibility());

                zoomOutBtn.performClick();
                assertEquals(0,activity.getCircleCount());
                assertEquals(View.VISIBLE,cir4.getVisibility());
                assertEquals(View.VISIBLE,cir3.getVisibility());
                assertEquals(View.VISIBLE,cir2.getVisibility());
            });
        }

    }
}
