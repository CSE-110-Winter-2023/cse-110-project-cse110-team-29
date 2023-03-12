package com.example.socialcompass.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.io.InputStreamReader;

@Entity(tableName = "Friends")
public class Friend {
    /** The title of the note. Used as the primary key for shared notes (even on the cloud). */
    @PrimaryKey
    @SerializedName("UID")
    @NonNull
    public String uid;

    /** The content of the note. */
    @SerializedName("friend name")
    @NonNull
    public String friend_name;

    /**
     * When the note was last modified. Used for resolving local (db) vs remote (api) conflicts.
     * Defaults to 0 (Jan 1, 1970), so that if a note already exists remotely, its content is
     * always preferred to a new empty note.
     */
    @SerializedName(value = "updated_at", alternate = "updatedAt")
    public String updatedAt = "";

    /** General constructor for a note. */
    public Friend(@NonNull String title, @NonNull String content) {
        this.uid = title;
        this.friend_name = content;
    }

    public static Friend fromJSON(String json) {
        return new Gson().fromJson(json, Friend.class);
    }
}