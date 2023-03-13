package com.example.socialcompass.model;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import androidx.room.Transaction;
import androidx.room.Update;
import androidx.room.Upsert;
import java.util.List;

/** Data access object for the {@link Friend} class. */
@Dao
public abstract class FriendDao {
    /**
     * In the TodoList app, our DAO used the @Insert, @Update to define methods that insert and
     * update items from the database.
     * <p>
     * Here we replace both @Insert and @Update with @Upsert. An @Upsert method will insert a new
     * item into the database if one with the title doesn't already exist, or update an existing
     * item if it does.
     */
    @Upsert
    public abstract long upsert(Friend friend);

    @Query("SELECT EXISTS(SELECT 1 FROM friends WHERE uid = :uid)")
    public abstract boolean exists(String uid);

    @Query("SELECT * FROM friends WHERE uid = :uid")
    public abstract Friend get(String uid);

    @Query("SELECT * FROM friends ORDER BY uid")
    public abstract List<Friend> getAll();

    @Delete
    public abstract int delete(Friend friend);
}