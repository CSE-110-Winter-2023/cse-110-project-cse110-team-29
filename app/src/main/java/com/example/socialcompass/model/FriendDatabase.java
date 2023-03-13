package com.example.socialcompass.model;


import android.content.Context;

import androidx.annotation.VisibleForTesting;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Friend.class}, version = 1, exportSchema = false)
public abstract class FriendDatabase extends RoomDatabase {
    private volatile static FriendDatabase instance = null;

    public abstract FriendDao getDao();

    public synchronized static FriendDatabase provide(Context context) {
        if (instance == null) {
            instance = FriendDatabase.make(context);
        }
        return instance;
    }

    private static FriendDatabase make(Context context) {
        return Room.databaseBuilder(context, FriendDatabase.class, "friend_app.db")
                .allowMainThreadQueries()
                .build();
    }

    @VisibleForTesting
    public static void inject(FriendDatabase testDatabase) {
        if (instance != null ) {
            instance.close();
        }
        instance = testDatabase;
    }
}
