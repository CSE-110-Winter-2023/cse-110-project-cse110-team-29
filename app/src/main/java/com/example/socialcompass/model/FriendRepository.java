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
    private final FriendAPI api = new FriendAPI();
    private ScheduledFuture<?> friendFuture;

    private final MutableLiveData<Friend> realLiveContent;

    private final MediatorLiveData<Friend> liveContent;

    public FriendRepository(FriendDao dao) {
        this.dao = dao;

        realLiveContent = new MediatorLiveData<>();
        liveContent = new MediatorLiveData<>();
        liveContent.addSource(realLiveContent, liveContent::postValue);
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
    public LiveData<Friend> getSynced(String public_code) {
        var friend = new MediatorLiveData<Friend>();

        Observer<Friend> updateFromRemote = them -> {
            var ourFriend = friend.getValue();
            if (ourFriend == null) return;
            if (ourFriend == null || ourFriend.updatedAt < them.updatedAt) {
                upsertLocal(them);
            }
        };

        // If we get a local update, pass it on.
        friend.addSource(getLocal(public_code), friend::postValue);

        // If we get a remote update, update the local version (triggering the above observer)
        friend.addSource(getRemote(public_code), updateFromRemote);

        return friend;
    }

    public void upsertSynced(String private_code, Friend friend) {
        upsertLocal(friend);
        upsertRemote(private_code, friend);
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
        friend.updatedAt = System.currentTimeMillis();
        dao.upsert(friend);
    }

    public boolean existsLocal(String title) {
        return dao.exists(title);
    }

    // Remote Methods
    // ==============

    public LiveData<Friend> getRemote(String public_code) {
        if (friendFuture != null) {
            friendFuture.cancel(true);
        }

        Friend friend = api.get(public_code);
        Log.d("hey", "before friend_name");
        Log.d("hey", friend.friend_name);
        Log.d("hey", String.valueOf(friend.getLatitude()) + ", " + String.valueOf(friend.getLongitude()));

        if (friend != null) {
            upsertLocal(friend);
        }

        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        friendFuture = executor.scheduleAtFixedRate(() -> {
            Friend tempFriend = api.get(public_code);

            if (tempFriend != null) {
                realLiveContent.postValue(tempFriend);
            }

        }, 0, 3, TimeUnit.SECONDS);

        return liveContent;
    }

    public void upsertRemote(String private_code, Friend friend) {
        api.put(private_code, friend);
    }
}
