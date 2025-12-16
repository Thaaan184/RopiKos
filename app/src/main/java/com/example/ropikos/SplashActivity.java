package com.example.ropikos;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    private ProgressBar pbLoading;
    private SharedPreferences sharedPreferences;
    private static final String SHARED_PREF_NAME = "mypref";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        pbLoading = findViewById(R.id.pb_loading);

        // Tahap 1: Stay di Splash selama 3 detik (3000 ms)
        new Handler(Looper.getMainLooper()).postDelayed(() -> {

            // Tahap 2: Munculkan Loading
            pbLoading.setVisibility(View.VISIBLE);

            // Tahap 3: Loading muter selama 2 detik (2000 ms), lalu cek login
            new Handler(Looper.getMainLooper()).postDelayed(this::checkLoginAndNavigate, 2000);

        }, 3000);
    }

    private void checkLoginAndNavigate() {
        // Cek Status Login dari SharedPreferences
        sharedPreferences = getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);

        Intent intent;
        if (isLoggedIn) {
            // Jika sudah login -> Ke Main Activity
            intent = new Intent(SplashActivity.this, MainActivity.class);
        } else {
            // Jika belum login -> Ke Login Activity
            intent = new Intent(SplashActivity.this, LoginActivity.class);
        }

        startActivity(intent);
        finish(); // Tutup SplashActivity agar tidak bisa di-back
    }
}