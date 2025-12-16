package com.example.ropikos;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.ropikos.db.DBHelper;
import com.example.ropikos.model.User;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class EditProfilActivity extends AppCompatActivity {

    private TextInputEditText etNama, etUsername, etTelepon, etAlamat, etPwLama, etPwBaru;
    private TextInputLayout tilPwLama, tilPwBaru;
    private Button btnSimpan;
    private ImageView btnBack;
    private DBHelper dbHelper;
    private User currentUser;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        dbHelper = new DBHelper(this);
        sharedPreferences = getSharedPreferences("mypref", MODE_PRIVATE);

        // Binding Views
        btnBack = findViewById(R.id.btn_back);
        etNama = findViewById(R.id.et_nama);
        etUsername = findViewById(R.id.et_username);
        etTelepon = findViewById(R.id.et_telepon);
        etAlamat = findViewById(R.id.et_alamat);
        etPwLama = findViewById(R.id.et_pw_lama);
        etPwBaru = findViewById(R.id.et_pw_baru);
        btnSimpan = findViewById(R.id.btn_simpan_perubahan);
        tilPwLama = findViewById(R.id.layout_pw_lama);
        tilPwBaru = findViewById(R.id.layout_pw_baru);

        // Load Data Awal
        loadCurrentData();

        btnBack.setOnClickListener(v -> finish());

        btnSimpan.setOnClickListener(v -> simpanPerubahan());
    }

    private void loadCurrentData() {
        String usernameSession = sharedPreferences.getString("username", "");
        currentUser = dbHelper.getUser(usernameSession);

        if (currentUser != null) {
            etNama.setText(currentUser.getFullname());
            etUsername.setText(currentUser.getUsername());
            etTelepon.setText(currentUser.getPhoneNumber());
            etAlamat.setText(currentUser.getAddress());
        }
    }

    private void simpanPerubahan() {
        if (currentUser == null) return;

        String newNama = etNama.getText().toString().trim();
        String newUsername = etUsername.getText().toString().trim();
        String newPhone = etTelepon.getText().toString().trim();
        String newAlamat = etAlamat.getText().toString().trim();
        String pwLama = etPwLama.getText().toString().trim();
        String pwBaru = etPwBaru.getText().toString().trim();

        // Validasi Dasar
        if (TextUtils.isEmpty(newNama) || TextUtils.isEmpty(newUsername)) {
            Toast.makeText(this, "Nama dan Username tidak boleh kosong.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Cek jika Username diganti -> cek apakah sudah dipakai orang lain
        if (!newUsername.equals(currentUser.getUsername())) {
            if (dbHelper.checkUsernameExists(newUsername)) {
                Toast.makeText(this, "Username sudah digunakan user lain.", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        tilPwLama.setError(null);
        tilPwBaru.setError(null);

        // Logika Ganti Password
        boolean isChangingPass = !TextUtils.isEmpty(pwLama) || !TextUtils.isEmpty(pwBaru);

        if (isChangingPass) {
            // Validasi Password Lama Kosong
            if (TextUtils.isEmpty(pwLama)) {
                tilPwLama.setError("Masukkan password lama untuk konfirmasi");
                return;
            }

            // Validasi Password Baru Kosong
            if (TextUtils.isEmpty(pwBaru)) {
                tilPwBaru.setError("Masukkan password baru");
                return;
            }

            // Validasi Cek Password Lama Benar/Salah
            if (!pwLama.equals(currentUser.getPassword())) {
                tilPwLama.setError("Password lama salah!");
                return;
            }

            // Set Password Baru
            currentUser.setPassword(pwBaru);
        }

        // Update Object User
        currentUser.setFullname(newNama);
        currentUser.setUsername(newUsername);
        currentUser.setPhoneNumber(newPhone);
        currentUser.setAddress(newAlamat);

        // Simpan ke Database
        boolean success = dbHelper.updateUser(currentUser);

        if (success) {
            // Update Session jika username berubah
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("username", newUsername);
            editor.apply();

            Toast.makeText(this, "Profil berhasil diperbarui!", Toast.LENGTH_SHORT).show();
            finish(); // Kembali ke halaman profil
        } else {
            Toast.makeText(this, "Gagal memperbarui profil.", Toast.LENGTH_SHORT).show();
        }
    }
}