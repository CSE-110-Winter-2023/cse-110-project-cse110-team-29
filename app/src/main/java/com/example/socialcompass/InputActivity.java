package com.example.socialcompass;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.socialcompass.model.FriendDao;
import com.example.socialcompass.model.FriendDatabase;
import com.example.socialcompass.model.FriendRepository;

public class InputActivity extends AppCompatActivity {
    private EditText UIDInput;
    private FriendRepository repo;
    private SharedPreferences prefs;

    private TextView uidText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);

        prefs = getSharedPreferences("codes",MODE_PRIVATE);
        uidText = findViewById(R.id.uidTextView);
        uidText.setText(prefs.getString("public_code",""));
        UIDInput = findViewById(R.id.uid);
        FriendDao dao = FriendDatabase.provide(this).getDao();
        repo = new FriendRepository(dao);
    }

    public void saveFriend() {
        String uid = UIDInput.getText().toString();

        SharedPreferences preferences = getSharedPreferences("codes", MODE_PRIVATE);

        String endpoint = preferences.getString("endpoint", null);

        repo.getSynced(endpoint, uid);

        if (!repo.existsLocal(uid)) {
            UIDInput.setError("Invalid friend code.");
            return;
        }
    }


    //use for testing purposes
    public String getFriendPublicUid() {
        return UIDInput.getText().toString();
    }

    public void onFinish(View view) {
        saveFriend();
        finish();
    }
}