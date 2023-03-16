/*
package com.example.socialcompass;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static org.hamcrest.Matchers.allOf;

import android.view.View;

import androidx.test.espresso.ViewInteraction;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.GrantPermissionRule;

import org.hamcrest.core.IsInstanceOf;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class EspressoTestForDisplayMultipleCircle {

    @Rule
    public ActivityScenarioRule<MainActivity> mActivityScenarioRule =
            new ActivityScenarioRule<>(MainActivity.class);
    @Rule
    public GrantPermissionRule mRuntimePermissionRule = GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION);

    @Test
    public void espressoTestForDisplayMultipleCircle() {
//        ViewInteraction imageView = onView(
//                allOf(withId(R.id.circle_4),
//                        withParent(allOf(withId(R.id.clock),
//                                withParent(IsInstanceOf.<View>instanceOf(android.view.ViewGroup.class)))),
//                        isDisplayed()));
//        imageView.check(matches(isDisplayed()));
//
//        ViewInteraction imageView2 = onView(
//                allOf(withId(R.id.circle_3),
//                        withParent(allOf(withId(R.id.clock),
//                                withParent(IsInstanceOf.<View>instanceOf(android.view.ViewGroup.class)))),
//                        isDisplayed()));
//        imageView2.check(matches(isDisplayed()));

        ViewInteraction imageView3 = onView(
                allOf(withId(R.id.circle_2),
                        withParent(allOf(withId(R.id.clock),
                                withParent(IsInstanceOf.<View>instanceOf(android.view.ViewGroup.class)))),
                        isDisplayed()));
        imageView3.check(matches(isDisplayed()));

        ViewInteraction imageView4 = onView(
                allOf(withId(R.id.circle_1),
                        withParent(allOf(withId(R.id.clock),
                                withParent(IsInstanceOf.<View>instanceOf(android.view.ViewGroup.class)))),
                        isDisplayed()));
        imageView4.check(matches(isDisplayed()));

        ViewInteraction imageView5 = onView(
                allOf(withId(R.id.center_dot),
                        withParent(allOf(withId(R.id.clock),
                                withParent(IsInstanceOf.<View>instanceOf(android.view.ViewGroup.class)))),
                        isDisplayed()));
        imageView5.check(matches(isDisplayed()));

        ViewInteraction imageView6 = onView(
                allOf(withId(R.id.center_dot),
                        withParent(allOf(withId(R.id.clock),
                                withParent(IsInstanceOf.<View>instanceOf(android.view.ViewGroup.class)))),
                        isDisplayed()));
        imageView6.check(matches(isDisplayed()));
    }
}
*/
