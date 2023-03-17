package com.example.socialcompass.model;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class FriendRepository {
    private final FriendDao dao;
    private final FriendAPI api;
    private ScheduledFuture<?> friendFuture;

    public FriendRepository(FriendDao dao) {
        this.dao = dao;
        this.api = FriendAPI.provide();
    }

    // Synced Methods
    // ==============

    /**
     * This is where the magic happens. This method will return a LiveData object that will be
     * updated when the note is updated either locally or remotely on the server. Our activities
     * however will only need to observe this one LiveData object, and don't need to care where
     * it comes from!
     *
     * This method will always prefer the newest version of the note.
     *
     * @param public_code the uid of the friend
     * @return a LiveData object that will be updated when the note is updated locally or remotely.
     */
    public LiveData<Friend> getSynced(String endpoint, String public_code) {
        var friend = new MediatorLiveData<Friend>();

        Observer<Friend> updateFromRemote = them -> {
            Log.d("hey", "updateFromRemoteCalled");
            var ourFriend = friend.getValue();
            if (them == null) return;
            if (ourFriend == null || ourFriend.updatedAt < them.updatedAt) {
                Log.d("hey", "upserting local");
                upsertLocal(them);
            }
        };

        // If we get a local update, pass it on.
        friend.addSource(getLocal(public_code), friend::postValue);

        // If we get a remote update, update the local version (triggering the above observer)
        friend.addSource(getRemote(endpoint, public_code), updateFromRemote);

        return friend;
    }

    public void upsertSynced(String endpoint, String private_code, Friend friend) {
        upsertLocal(friend);
        upsertRemote(endpoint, private_code, friend);
    }

    // Local Methods
    // =============

    public LiveData<Friend> getLocal(String public_code) {
        return dao.get(public_code);
    }

    public List<Friend> getAllLocal() {
        return dao.getAll();
    }

    public void upsertLocal(Friend friend) {
        // friend.updatedAt = System.currentTimeMillis();
        dao.upsert(friend);
    }

    public boolean existsLocal(String title) {
        return dao.exists(title);
    }

    // Remote Methods
    // ==============

    public LiveData<Friend> getRemote(String endpoint, String public_code) {

        Friend friend = api.get(endpoint, public_code);

        if (friend != null) {
            upsertLocal(friend);
        }

        MutableLiveData<Friend> realLiveFriend = new MutableLiveData<>();

        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        friendFuture = executor.scheduleAtFixedRate(() -> {
            // Log.d("hey", "executor run " + public_code);
            Friend tempFriend = api.get(endpoint, public_code);

            if (tempFriend != null) {
                realLiveFriend.postValue(tempFriend);
                // Log.d("hey", "executor posts value" + String.valueOf(tempFriend.getLatitude()) + ", " + String.valueOf(tempFriend.getLongitude()));
            }

        }, 0, 3, TimeUnit.SECONDS);

        return realLiveFriend;
    }

    public void upsertRemote(String endpoint, String private_code, Friend friend) {
        // temp

        //api.put(endpoint, private_code, friend);
    }
}
