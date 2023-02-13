package com.example.socialcompass;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void angles_North() {
        double lat1 = 100;
        double long1 = 50;
        double lat2 = 100;
        double long2 = 50;
        double temp = Utilities.angleFromCoordinate(lat1,long1,lat2,long2);
        double expec = 0;
        assertEquals(expec,temp, 1);
    }

    @Test
    public void angles_West() {
        double lat1 = 0;
        double long1 = 0;
        double lat2 = 0;
        double long2 = 90;
        double temp = Utilities.angleFromCoordinate(lat1,long1,lat2,long2);
        double expec = 90;
        assertEquals(expec,temp, 1);
    }

}