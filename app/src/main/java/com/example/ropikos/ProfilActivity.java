package com.example.ropikos;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ropikos.db.DBHelper;
import com.example.ropikos.model.User;

public class ProfilActivity extends AppCompatActivity {

    private TextView tvNama, tvAlamat;
    private Button btnLogout;
    private CardView cardPengaturanAkun;
    private DBHelper dbHelper;
    private User currentUser;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);

        dbHelper = new DBHelper(this);
        sharedPreferences = getSharedPreferences("mypref", MODE_PRIVATE);

        tvNama = findViewById(R.id.tv_nama);
        tvAlamat = findViewById(R.id.tv_alamat);
        btnLogout = findViewById(R.id.btn_logout);
        cardPengaturanAkun = findViewById(R.id.card_pengaturan_akun);

        // Load data user saat activity dibuat
        loadUserData();

        // Tombol Logout
        btnLogout.setOnClickListener(v -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear(); // Hapus semua sesi
            editor.apply();

            Intent intent = new Intent(ProfilActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        // Tombol ke Edit Profile
        cardPengaturanAkun.setOnClickListener(v -> {
            Intent intent = new Intent(ProfilActivity.this, EditProfilActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload data setiap kali halaman ini dibuka kembali (misal setelah edit profil)
        loadUserData();
    }

    private void loadUserData() {
        // Ambil username yang sedang login dari SharedPref
        String username = sharedPreferences.getString("username", "");

        if (!username.isEmpty()) {
            currentUser = dbHelper.getUser(username);
            if (currentUser != null) {
                // Tampilkan data ke UI
                tvNama.setText(currentUser.getFullname());

                String alamat = currentUser.getAddress();
                if (alamat == null || alamat.isEmpty()) {
                    tvAlamat.setText("-");
                } else {
                    tvAlamat.setText(alamat);
                }
            }
        } else {
            // Jika sesi hilang, paksa logout
            Toast.makeText(this, "Sesi berakhir, silakan login ulang.", Toast.LENGTH_SHORT).show();
            btnLogout.performClick();
        }
    }
}