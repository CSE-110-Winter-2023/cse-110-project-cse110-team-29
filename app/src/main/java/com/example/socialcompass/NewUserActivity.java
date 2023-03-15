package com.example.socialcompass;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.example.socialcompass.model.Friend;
import com.example.socialcompass.model.FriendDao;
import com.example.socialcompass.model.FriendDatabase;
import com.example.socialcompass.model.FriendRepository;

import java.util.UUID;

public class NewUserActivity extends AppCompatActivity {
    private EditText name;
    private FriendRepository repo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);

        name = findViewById(R.id.name);

        FriendDao dao = FriendDatabase.provide(this).getDao();
        repo = new FriendRepository(dao);
    }

    /**
     * Creates a new user in the remote database and locally given a name
     **/
    public void onSubmit(View view) {
        if (name.getText().toString().equals("")) {
            name.setError("User must have a name.");
            return;
        }
        String public_code = UUID.randomUUID().toString();
        String private_code = UUID.randomUUID().toString();
        Friend new_user = new Friend(name.getText().toString(), public_code, 0, 0);

        SharedPreferences preferences = getSharedPreferences("codes", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("private_code", private_code);
        editor.putString("public_code", public_code);
        editor.putString("name", name.getText().toString());
        editor.apply();
        repo.upsertSynced(private_code, new_user);
        Intent intent = new Intent(this, CircularActivity.class);
        startActivity(intent);
    }
}
