package com.example.socialcompass.model;

import android.os.StrictMode;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class FriendAPI {
    private volatile static FriendAPI instance = null;
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private OkHttpClient client;

    public FriendAPI() {
        this.client = new OkHttpClient();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    public static FriendAPI provide() {
        if (instance == null) {
            instance = new FriendAPI();
        }
        return instance;
    }

    /**
     * Sends a GET request to the server.
     * @param public_code - Public UID of the user.
     * @return a User object, if the public code is found.
     */
    public Friend get(String endpoint, String public_code) {
        // URLs cannot contain spaces, so we replace them with %20.
        public_code = public_code.replace(" ", "%20");

        Request request = new Request.Builder()
                .url(endpoint + public_code)
                .method("GET", null)
                .build();

        try (okhttp3.Response response = client.newCall(request).execute()) {
            assert response.body() != null;

            String body = response.body().string();
            if (body.equals("{\"detail\":\"Location not found.\"}")) {
                return null;
            }
            return Friend.fromJSON(body);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Sends a PUT request to the server.
     * @param private_code - Private password of the user.
     * @param friend - The user to be updated
     */
    public void put(String endpoint, String private_code, Friend friend) {
        // URLs cannot contain spaces, so we replace them with %20.
        String public_code = friend.getUid();
        public_code = public_code.replace(" ", "%20");
        RequestBody requestBody = RequestBody.create(friend.toPutJSON(private_code), JSON);
        Request request = new Request.Builder()
                .url(endpoint + public_code)
                .method("PUT", requestBody)
                .build();

        try (okhttp3.Response response = client.newCall(request).execute()) {
            assert response.body() != null;
            String body = response.body().string();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
