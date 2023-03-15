package com.example.socialcompass.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.InputStreamReader;
import java.time.Instant;

@Entity(tableName = "Friends")
public class Friend {
    /** The title of the note. Used as the primary key for shared notes (even on the cloud). */
    @NonNull
    @PrimaryKey
    @SerializedName("public_code")
    public String public_code;

    /** Represents the location of the user. **/
    @SerializedName("longitude")
    public double longitude;

    @SerializedName("latitude")
    public double latitude;

    @NonNull
    public String friend_name;

    /**
     * When the note was last modified. Used for resolving local (db) vs remote (api) conflicts.
     * Defaults to 0 (Jan 1, 1970), so that if a note already exists remotely, its content is
     * always preferred to a new empty note.
     */
    @JsonAdapter(TimestampAdapter.class)
    @SerializedName(value = "updated_at", alternate = "updatedAt")
    public long updatedAt = 0;

    /** General constructor for a note. */
    public Friend(@NonNull String public_code, @NonNull String friend_name, double longitude, double latitude) {
        this.friend_name = friend_name;
        this.longitude = longitude;
        this.latitude = latitude;
        this.public_code = public_code;
        this.updatedAt = Instant.now().getEpochSecond();
    }

    public String getName() {
        return friend_name;
    }

    public String getUid() {
        return public_code;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public long getLastUpdated() {
        return updatedAt;
    }

    public boolean equals(Friend friend) {
        return this.public_code.equals(friend.public_code);
    }

    public static Friend fromJSON(String json) {
        return new Gson().fromJson(json, Friend.class);
    }

    /** Creates the JSON body and removes fields to be compliant with the API for put. **/
    public String toPutJSON(String private_code) {
        JsonObject json = (JsonObject) new Gson().toJsonTree(this);
        json.remove("public_code");
        json.remove("updated_at");
        json.addProperty("private_code", private_code);
        return json.toString();
    }

    /** Creates the JSON body and removes fields to be compliant with the API for patch. **/
    public String toPatchJSON(String private_code) {
        JsonObject json = (JsonObject) new Gson().toJsonTree(this);
        json.remove("public_code");
        json.remove("updated_at");
        json.remove("label");
        json.addProperty("private_code", private_code);
        return json.toString();
    }
}