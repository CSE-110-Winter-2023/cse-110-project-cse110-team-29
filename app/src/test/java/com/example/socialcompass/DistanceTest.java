package com.example.socialcompass;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class DistanceTest {
    @Test
    public void test_zeroLat() {
        double lat1 = 50;
        double long1 = 100;
        double lat2 = 0;
        double long2 = 30;

        double temp = Utilities.distance(lat1,long1,lat2,long2);
        double expected = 4638;

        assertEquals(expected, temp, 1);
    }

    @Test
    public void same_location() {
        double lat1 = 80;
        double long1 = 120;
        double lat2 = 80;
        double long2 = 120;

        double temp = Utilities.distance(lat1,long1,lat2,long2);
        double expected = 0;

        assertEquals(expected, temp, 1);
    }

}