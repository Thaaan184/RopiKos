package com.example.ropikos;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText etFullname, etUsername, etPassword, etConfirmPassword;
    private Button btnRegister;
    private TextView tvLoginLink;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        dbHelper = new DatabaseHelper(this);
        initViews();

        // Logika Tombol Register
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptRegister();
            }
        });

        // Link pindah ke halaman Login jika sudah punya akun
        tvLoginLink.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void initViews() {
        etFullname = findViewById(R.id.et_fullname);
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        btnRegister = findViewById(R.id.btn_register);
        // Bind TextView "Sudah punya akun? Masuk sekarang"
        // Catatan: Di XML Anda belum ada ID untuk TextView paling bawah,
        // Anda perlu menambahkannya android:id="@+id/tv_login_link" di XML
        // Untuk contoh ini saya asumsikan ID-nya sudah ada atau dilookup via parent
        tvLoginLink = findViewById(R.id.tv_login_link); // Pastikan tambah ID ini di XML!
    }

    private void attemptRegister() {
        String fullname = etFullname.getText().toString().trim();
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPass = etConfirmPassword.getText().toString().trim();

        // 1. Validasi Input Kosong
        if (fullname.isEmpty() || username.isEmpty() || password.isEmpty() || confirmPass.isEmpty()) {
            Toast.makeText(this, "Mohon lengkapi semua data", Toast.LENGTH_SHORT).show();
            return;
        }

        // 2. Validasi Konfirmasi Password
        if (!password.equals(confirmPass)) {
            Toast.makeText(this, "Konfirmasi password tidak sesuai", Toast.LENGTH_SHORT).show();
            return;
        }

        // 3. Cek Username Unik
        if (dbHelper.checkUsername(username)) {
            Toast.makeText(this, "Username sudah digunakan", Toast.LENGTH_SHORT).show();
            return;
        }

        // 4. Simpan ke Database
        boolean isInserted = dbHelper.registerUser(fullname, username, password);
        if (isInserted) {
            Toast.makeText(this, "Registrasi Berhasil! Silakan Login.", Toast.LENGTH_SHORT).show();
            // Pindah ke Login Activity
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Registrasi Gagal, coba lagi.", Toast.LENGTH_SHORT).show();
        }
    }
}