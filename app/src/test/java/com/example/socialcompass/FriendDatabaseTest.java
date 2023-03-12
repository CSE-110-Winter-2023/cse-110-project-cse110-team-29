package com.example.socialcompass;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.socialcompass.model.Friend;
import com.example.socialcompass.model.FriendDao;
import com.example.socialcompass.model.FriendDatabase;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class FriendDatabaseTest {
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

    /*
    @Test
    public void testInsert() {
        TodoListItem item1 = new Friend("Pizza time", false, 0);
        TodoListItem item2 = new TodoListItem("Photos of Spider-Man", false, 1);

        long id1 = dao.insert(item1);
        long id2 = dao.insert(item2);

        assertNotEquals(id1, id2);
    }
    */


    @Test
    public void testExists() {
        Friend insertedFriend = new Friend("1","test friend");
        long id = dao.upsert(insertedFriend);

        boolean exists = dao.exists("1");

        assertTrue(exists);
    }

    @Test
    public void testGet() {
        Friend insertedFriend = new Friend("1","test friend");
        long id = dao.upsert(insertedFriend);

        var Friend = dao.get("1");



        assertEquals( insertedFriend.uid,Friend.uid);
        assertEquals( insertedFriend.friend_name,Friend.friend_name);

    }

    @Test
    public void testGetAll() {
        ArrayList<Friend>  friends = new ArrayList<>();
        friends.add(new Friend("1","test friend"));
        friends.add(new Friend("2","test friend2"));

        for (int i = 0; i < friends.size(); i++){
            long id = dao.upsert(friends.get(i));
        }
        var friends_tested = dao.getAll();

        for (int i = 0; i < friends.size(); i++){
            assertEquals( friends.get(i).uid,friends_tested.get(i).uid);
            assertEquals( friends.get(i).friend_name,friends_tested.get(i).friend_name);
        }





    }

    @After
    public void closeDb() throws IOException {
        db.close();
    }
}
