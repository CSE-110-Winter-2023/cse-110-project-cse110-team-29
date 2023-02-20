package com.example.socialcompass;

import static org.junit.Assert.*;

import org.junit.Assert;
import org.junit.Test;

public class UtilitiesTest {
    @Test
    public void test_angle_from_coordinate() {
        Assert.assertEquals(0, Utilities.angleInActivity(0, 90, 0, 90), 0.00001);
        Assert.assertEquals(272.79281, Utilities.angleInActivity(12.355, -60.443, 157.264, 12.6035), 0.00001);
        Assert.assertEquals(90, Utilities.angleInActivity(0, 0, 0, 90), 0.00001);
    }
}
