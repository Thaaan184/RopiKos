package com.example.ropikos;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.example.ropikos.db.DBHelper;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etUsername, etPassword;
    private Button btnLogin;
    private TextView tvRegisterLink, tvForgotPass;
    private DBHelper db;

    SharedPreferences sharedPreferences;
    private static final String SHARED_PREF_NAME = "mypref";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Di dalam onCreate LoginActivity
        sharedPreferences = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
//          Is Logged in nya gw taro di splash
//        if (isLoggedIn) {
//            // Jika sudah login, langsung ke MainActivity
//            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//            startActivity(intent);
//            finish();
//            return; // agar tidak merender layout login kembali
//        }

        setContentView(R.layout.activity_login);

        // Inisialisasi Database Helper
        db = new DBHelper(this);

        // Hubungkan Variabel dengan ID di Layout (Binding Views)
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        tvRegisterLink = findViewById(R.id.tv_register_link);
        tvForgotPass = findViewById(R.id.tv_forgot_pass);

        // Aksi untuk Tombol Login
        btnLogin.setOnClickListener(v -> handleLogin());

        // Aksi untuk Link Daftar
        tvRegisterLink.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegistrasiActivity.class);
            startActivity(intent);
        });

        // Aksi untuk Link Lupa Password (saat ini hanya menampilkan pesan)
        tvForgotPass.setOnClickListener(v -> {
            Toast.makeText(LoginActivity.this, "Fitur Lupa Password belum diimplementasikan.", Toast.LENGTH_SHORT).show();
        });
    }

    private void handleLogin() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Validasi Input
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Username dan Password tidak boleh kosong.", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean checkLogin = db.checkUser(username, password);

        if (checkLogin) {
            // Simpan status login ke SharedPreferences
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(KEY_IS_LOGGED_IN, true);
            editor.putString("username", username); // Opsional: Simpan username buat dipanggil di profil
            editor.apply();

            Toast.makeText(this, "Login Berhasil!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            // Membersihkan stack activity agar user tidak bisa kembali ke Login dengan tombol back
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Username atau Password salah.", Toast.LENGTH_SHORT).show();
        }
    }
}