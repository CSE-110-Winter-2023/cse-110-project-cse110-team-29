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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class DatabaseUnitTest {
    private FriendDao dao;
    private FriendDatabase db;

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, FriendDatabase.class)
                .allowMainThreadQueries()
                .build();
        dao = db.getDao();
    }

    @Test
    public void testExists() {
        Friend friend = new Friend("secret_code", "test", 0.0, 0.0);

        long id1 = dao.upsert(friend);

        assertTrue(dao.exists("secret_code"));
    }

    @Test
    public void testGetAll() {
        Friend friendOne = new Friend("secret_code1", "test1", 0.0, 0.0);
        Friend friendTwo = new Friend("secret_code2", "test2", 0.0, 0.0);

        long id1 = dao.upsert(friendOne);
        long id2 = dao.upsert(friendTwo);

        List<Friend> friends = dao.getAll();

        Friend firstRetrieved = friends.get(0);
        Friend secondRetrieved = friends.get(1);

        assertEquals(firstRetrieved.getUid(), friendOne.getUid());
        assertEquals(firstRetrieved.getName(), friendOne.getName());
        assertTrue(firstRetrieved.getLatitude() == friendOne.getLatitude());
        assertTrue(firstRetrieved.getLongitude() == friendOne.getLongitude());

        assertEquals(secondRetrieved.getUid(), friendTwo.getUid());
        assertEquals(secondRetrieved.getName(), friendTwo.getName());
        assertTrue(secondRetrieved.getLatitude() == friendTwo.getLatitude());
        assertTrue(secondRetrieved.getLongitude() == friendTwo.getLongitude());
    }

    @After
    public void closeDb() throws IOException {
        db.close();
    }
}
