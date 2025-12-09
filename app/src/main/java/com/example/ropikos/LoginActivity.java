package com.example.ropikos;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.textfield.TextInputEditText;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etUsername, etPassword;
    private Button btnLogin;
    private TextView tvRegisterLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initViews();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performLogin();
            }
        });

        tvRegisterLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initViews() {
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        tvRegisterLink = findViewById(R.id.tv_register_link);
    }

    private void performLogin() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Username dan Password harus diisi", Toast.LENGTH_SHORT).show();
            return;
        }

        // Cek Login (CRUD READ)
        if (checkLoginFromDB(username, password)) {
            // TODO: Simpan session user ke SharedPreferences disini

            Toast.makeText(this, "Login Berhasil", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Username atau Password salah", Toast.LENGTH_SHORT).show();
        }
    }

    // Siapkan Method ini untuk koneksi DB
    private boolean checkLoginFromDB(String username, String password) {
        // TODO: Panggil DBHelper.checkUser(username, password) disini nanti
        // Mock logika sementara: admin/admin bisa masuk
        if (username.equals("admin") && password.equals("admin")) {
            return true;
        }
        return true; // Bypass dulu biar bisa masuk dashboard
    }
}