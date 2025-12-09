package com.example.ropikos;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etUsername, etPassword;
    private Button btnLogin;
    private TextView tvRegisterLink;
    private DatabaseHelper dbHelper;

    // Untuk Session Management (NFR-08)
    SharedPreferences sharedPreferences;
    private static final String PREF_NAME = "RopiKostSession";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USERNAME = "username";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Cek Session sebelum set content view
        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        if (sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false)) {
            goToMainActivity();
        }

        setContentView(R.layout.activity_login);
        dbHelper = new DatabaseHelper(this);
        initViews();

        btnLogin.setOnClickListener(v -> attemptLogin());

        // Link pindah ke Register jika belum punya akun
        tvRegisterLink.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private void initViews() {
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        // Tambahkan ID android:id="@+id/tv_register_link" pada TextView "Create now" di XML Anda
        tvRegisterLink = findViewById(R.id.tv_register_link);
    }

    private void attemptLogin() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Masukkan username dan password", Toast.LENGTH_SHORT).show();
            return;
        }

        // Verifikasi Kredensial ke SQLite (SRS 4.3.2)
        boolean isValid = dbHelper.checkLogin(username, password);

        if (isValid) {
            // Simpan Session
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(KEY_IS_LOGGED_IN, true);
            editor.putString(KEY_USERNAME, username);
            editor.apply();

            Toast.makeText(this, "Login Berhasil", Toast.LENGTH_SHORT).show();
            goToMainActivity();
        } else {
            Toast.makeText(this, "Username atau Password salah", Toast.LENGTH_SHORT).show();
        }
    }

    private void goToMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        // Clear task agar user tidak bisa kembali ke login dengan tombol back
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}