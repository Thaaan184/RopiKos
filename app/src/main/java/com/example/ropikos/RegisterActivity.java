package com.example.ropikos;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.textfield.TextInputEditText;
import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText etFullname, etUsername, etPassword, etConfirmPassword;
    private Button btnRegister;
    private TextView tvLoginLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initViews();

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performRegister();
            }
        });

        tvLoginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Pindah ke halaman Login
                finish();
            }
        });
    }

    private void initViews() {
        etFullname = findViewById(R.id.et_fullname);
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        btnRegister = findViewById(R.id.btn_register);
        tvLoginLink = findViewById(R.id.tv_login_link);
    }

    private void performRegister() {
        String fullname = etFullname.getText().toString().trim();
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPass = etConfirmPassword.getText().toString().trim();

        // 1. Validasi Input
        if (fullname.isEmpty() || username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Harap isi semua kolom", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPass)) {
            Toast.makeText(this, "Password konfirmasi tidak cocok", Toast.LENGTH_SHORT).show();
            return;
        }

        // 2. Proses Simpan ke Database (CRUD CREATE)
        if (registerUserToDB(fullname, username, password)) {
            Toast.makeText(this, "Registrasi Berhasil! Silahkan Login.", Toast.LENGTH_SHORT).show();
            finish(); // Kembali ke Login
        } else {
            Toast.makeText(this, "Registrasi Gagal (Username mungkin sudah ada)", Toast.LENGTH_SHORT).show();
        }
    }

    // Siapkan Method ini untuk nanti dipanggil DB Helper
    private boolean registerUserToDB(String fullname, String username, String password) {
        // TODO: Panggil DBHelper.insertUser(fullname, username, password) disini nanti
        // Mock return true sementara
        return true;
    }
}