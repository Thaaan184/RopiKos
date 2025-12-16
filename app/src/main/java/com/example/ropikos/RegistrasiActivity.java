package com.example.ropikos;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.example.ropikos.db.DBHelper;
import com.example.ropikos.model.User;

public class RegistrasiActivity extends AppCompatActivity {

    private TextInputEditText etFullname, etUsername, etPassword, etConfirmPassword;
    private Button btnRegister;
    private TextView tvLoginLink;
    private DBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Inisialisasi Database Helper
        db = new DBHelper(this);

        // Hubungkan Variabel dengan ID di Layout (Binding Views)
        etFullname = findViewById(R.id.et_fullname);
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        btnRegister = findViewById(R.id.btn_register);
        tvLoginLink = findViewById(R.id.tv_login_link);

        // Aksi untuk Tombol Register
        btnRegister.setOnClickListener(v -> handleRegistration());

        // Aksi untuk Link Login
        tvLoginLink.setOnClickListener(v -> {
            Intent intent = new Intent(RegistrasiActivity.this, LoginActivity.class);
            startActivity(intent);
            finish(); // Tutup activity register setelah pindah ke login
        });
    }

    private void handleRegistration() {
        String fullname = etFullname.getText().toString().trim();
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPass = etConfirmPassword.getText().toString().trim();

        // Validasi Input Kosong
        if (TextUtils.isEmpty(fullname) || TextUtils.isEmpty(username) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPass)) {
            Toast.makeText(this, "Semua field harus diisi.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validasi Kesesuaian Password
        if (!password.equals(confirmPass)) {
            Toast.makeText(this, "Password dan Konfirmasi Password tidak cocok.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Asumsi minimal panjang password 6 karakter (bisa disesuaikan)
        if (password.length() < 6) {
            Toast.makeText(this, "Password minimal 6 karakter.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Cek Ketersediaan Username
        // Catatan: Asumsi method checkUsernameExists ada di DBHelper
        if (db.checkUsernameExists(username)) {
            Toast.makeText(this, "Username sudah terdaftar. Silakan gunakan username lain.", Toast.LENGTH_LONG).show();
            return;
        }

        // Buat objek User baru
        // Catatan: Asumsi constructor User adalah: User(String fullname, String username, String password)
        User newUser = new User(fullname, username, password);

        // Tambahkan user ke database
        // Catatan: Asumsi method addUser ada di DBHelper dan mengembalikan boolean
        boolean isInserted = db.addUser(newUser);

        if (isInserted) {
            Toast.makeText(this, "Pendaftaran Berhasil! Silakan masuk.", Toast.LENGTH_LONG).show();
            // Arahkan ke LoginActivity
            Intent intent = new Intent(RegistrasiActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Pendaftaran Gagal. Coba lagi.", Toast.LENGTH_SHORT).show();
        }
    }
}