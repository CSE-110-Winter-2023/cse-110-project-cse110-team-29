package com.example.socialcompass;

import static com.example.socialcompass.Utilities.showAlert;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private LocationService locationService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 200);
        }

         SharedPreferences preferences = getSharedPreferences("codes", MODE_PRIVATE);

        String endpoint = preferences.getString("endpoint", null);

        if(endpoint == null) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("endpoint", "https://socialcompass.goto.ucsd.edu/location/");
            editor.apply();
        }

         String userPublicCode = preferences.getString("public_code", null);

         if(userPublicCode == null) {
             Intent intent = new Intent(this, NewUserActivity.class);
             startActivity(intent);
         } else {
            // temp always start input activity immediately
            Intent intent = new Intent(this, CircularActivity.class);
            startActivity(intent);
         }
    }
    public void debug(View view) {
        SharedPreferences prefs = getSharedPreferences("data", MODE_PRIVATE);
        String label = prefs.getString("parentsLabel", "what");
        Float lat = prefs.getFloat("parentsLat", 0);
        Float longi = prefs.getFloat("parentsLong", 0);
        showAlert(this, label + '\n' + String.valueOf(lat) + '\n' + String.valueOf(longi));
    }
}
