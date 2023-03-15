package com.example.socialcompass.viewmodel;
/*
import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.socialcompass.model.Friend;
import com.example.socialcompass.model.FriendDatabase;
import com.example.socialcompass.model.FriendRepository;

import java.util.List;

public class LocationViewModel extends AndroidViewModel {
    private LiveData<List<Friend>> users;
    private final FriendRepository repo;

    public LocationViewModel(@NonNull Application application) {
        super(application);
        Context context = application.getApplicationContext();
        this.repo = new FriendRepository(FriendDatabase.provide(context).getDao());
    }

    /**
     * Load all friends from the database.
     * @return a LiveData object that will be updated when any notes change.
     *\/
    public LiveData<List<Friend>> getUsers() {
        if (users == null) {
            users = repo.getAllLocal();
        }
        return users;
    }
}
*/