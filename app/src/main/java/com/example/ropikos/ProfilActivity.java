package com.example.ropikos;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ropikos.db.DBHelper;
import com.example.ropikos.model.User;

public class ProfilActivity extends AppCompatActivity {

    // UI Components
    private TextView tvNama, tvAlamat;
    private Button btnLogout;
    private CardView cardPengaturanAkun, cardFaq, cardAbout;
    private ImageView btnBack; // Tombol Back baru

    // Data & Utils
    private DBHelper dbHelper;
    private User currentUser;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);

        dbHelper = new DBHelper(this);
        sharedPreferences = getSharedPreferences("mypref", MODE_PRIVATE);

        // --- Inisialisasi View ---
        tvNama = findViewById(R.id.tv_nama);
        tvAlamat = findViewById(R.id.tv_alamat);
        btnLogout = findViewById(R.id.btn_logout);

        // Card Menu
        cardPengaturanAkun = findViewById(R.id.card_pengaturan_akun);
        cardFaq = findViewById(R.id.card_faq);
        cardAbout = findViewById(R.id.card_about);

        // Tombol Back di Header
        btnBack = findViewById(R.id.btn_back_profile);

        // Load Data User Awal
        loadUserData();

        // --- Setup Listeners ---

        // 1. Logika Tombol Back
        btnBack.setOnClickListener(v -> finish());

        // 2. Logika Logout
        btnLogout.setOnClickListener(v -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear(); // Hapus semua sesi login
            editor.apply();

            Intent intent = new Intent(ProfilActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        // 3. Logika Pindah Halaman
        cardPengaturanAkun.setOnClickListener(v -> startActivity(new Intent(ProfilActivity.this, EditProfilActivity.class)));
        cardFaq.setOnClickListener(v -> startActivity(new Intent(ProfilActivity.this, FaqActivity.class)));
        cardAbout.setOnClickListener(v -> startActivity(new Intent(ProfilActivity.this, AboutActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUserData(); // Refresh data saat kembali ke halaman ini
    }

    private void loadUserData() {
        String username = sharedPreferences.getString("username", "");
        if (!username.isEmpty()) {
            currentUser = dbHelper.getUser(username);
            if (currentUser != null) {
                tvNama.setText(currentUser.getFullname());
                String alamat = currentUser.getAddress();
                if (alamat == null || alamat.isEmpty()) {
                    tvAlamat.setText("-");
                } else {
                    tvAlamat.setText(alamat);
                }
            }
        } else {
            // Jika sesi invalid, paksa logout
            Toast.makeText(this, "Sesi berakhir", Toast.LENGTH_SHORT).show();
            btnLogout.performClick();
        }
    }
}