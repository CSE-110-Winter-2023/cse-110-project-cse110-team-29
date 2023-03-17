package com.example.socialcompass;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.socialcompass.model.Friend;
import com.example.socialcompass.model.FriendDao;
import com.example.socialcompass.model.FriendDatabase;
import com.example.socialcompass.model.MockFriendAPI;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class MockAPITest {
    private MockFriendAPI api;

    @Before
    public void initializeMockAPI() {
        this.api = MockFriendAPI.provide();
    }

    @Test
    public void testPut() {
        Friend friend = new Friend("secret_code", "test", 0.0, 0.0);

        this.api.put("https://socialcompass.goto.ucsd.edu/", "private_code", friend);

        Friend getFriend = this.api.get("https://socialcompass.goto.ucsd.edu/", "secret_code");

        assertTrue(friend.getUid().equals(getFriend.getUid()));
        assertTrue(friend.getName().equals(getFriend.getName()));
    }

    @Test
    public void testGet() {
        Friend f = new Friend("test1", "test_1", 0.0, 0.0);

        Friend getFriend = this.api.get("https://socialcompass.goto.ucsd.edu/", "test1");

        assertTrue(getFriend.getUid().equals(f.getUid()));
        assertTrue(getFriend.getName().equals(f.getName()));
    }
}
