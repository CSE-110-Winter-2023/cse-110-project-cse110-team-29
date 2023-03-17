package com.example.socialcompass.model;

import java.util.ArrayList;
import java.util.List;

public class MockFriendAPI extends FriendAPI {
    private volatile static MockFriendAPI instance = null;

    private List<Friend> friends;

    public MockFriendAPI() {
        this.friends = new ArrayList<>();

        this.friends.add(new Friend("test1", "test_1", 0.0, 0.0));
        this.friends.add(new Friend("test2", "test_2", 1.0, 0.0));
        this.friends.add(new Friend("test3", "test_3", 0.0, 1.0));
    }

    public static MockFriendAPI provide() {
        if(instance == null) {
            instance = new MockFriendAPI();
        }

        return instance;
    }

    /**
     * Sends a GET request to the server.
     * @param public_code - Public UID of the user.
     * @return a User object, if the public code is found.
     */
    public Friend get(String endpoint, String public_code) {
        for(Friend friend : this.friends) {
            if(friend.getUid().equals(public_code)) {
                return friend;
            }
        }

        return null;
    }

    /**
     * Sends a PUT request to the server.
     * @param private_code - Private password of the user.
     * @param friend - The user to be updated
     */
    public void put(String endpoint, String private_code, Friend friend) {
        for(int i = 0; i < this.friends.size(); i++) {
            Friend listFriend = this.friends.get(i);

            if(listFriend.getUid().equals(friend.getUid())) {
                this.friends.set(i, friend);
                return;
            }
        }

        this.friends.add(friend);
    }
}

