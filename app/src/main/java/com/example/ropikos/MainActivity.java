package com.example.ropikos;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    // UI Components untuk Dashboard Stats
    private TextView tvTotalKamar, tvTotalPenyewa, tvTotalLunas, tvTotalPerbaikan;
    private TextView tvPendapatan, tvPendapatanDesc;
    private ProgressBar pbPendapatan;
    private ImageButton btnProfile;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        setupBottomNavigation();

        // Memuat data statistik Dashboard (CRUD READ - Aggregation)
        loadDashboardStats();

        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Intent ke ProfileActivity
                Toast.makeText(MainActivity.this, "Fitur Profil", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initViews() {
        tvTotalKamar = findViewById(R.id.tv_total_kamar_value);
        tvTotalPenyewa = findViewById(R.id.tv_total_penyewa_value);
        tvTotalLunas = findViewById(R.id.tv_total_lunas_value);
        tvTotalPerbaikan = findViewById(R.id.tv_total_perbaikan_value);
        tvPendapatan = findViewById(R.id.tv_pendapatan_value);
        tvPendapatanDesc = findViewById(R.id.tv_pendapatan_desc);
        pbPendapatan = findViewById(R.id.pb_pendapatan);

        btnProfile = findViewById(R.id.btn_profile);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.nav_dashboard) {
                    // Sudah di dashboard
                    return true;
                } else if (itemId == R.id.nav_kamar) {
                    // TODO: Intent ke KamarActivity
                    Toast.makeText(MainActivity.this, "Menu Kamar", Toast.LENGTH_SHORT).show();
                    return true;
                } else if (itemId == R.id.nav_penyewa) {
                    // TODO: Intent ke PenyewaActivity
                    Toast.makeText(MainActivity.this, "Menu Penyewa", Toast.LENGTH_SHORT).show();
                    return true;
                } else if (itemId == R.id.nav_keuangan) {
                    // TODO: Intent ke KeuanganActivity
                    Toast.makeText(MainActivity.this, "Menu Keuangan", Toast.LENGTH_SHORT).show();
                    return true;
                }
                return false;
            }
        });
    }

    // Method untuk load data dari DB ke Dashboard
    private void loadDashboardStats() {
        // TODO: Panggil DBHelper untuk ambil Count Kamar, Penyewa, Sum Pendapatan, dll.

        // --- Mock Data (Data Palsu Sementara) ---
        int totalKamar = 10;
        int totalPenyewa = 8;
        int totalLunas = 6;
        int totalPerbaikan = 2;
        int pendapatan = 8000000;
        int target = 10000000; // Misal target 10juta

        // Update UI
        tvTotalKamar.setText(String.valueOf(totalKamar));
        tvTotalPenyewa.setText(String.valueOf(totalPenyewa));
        tvTotalLunas.setText(String.valueOf(totalLunas));
        tvTotalPerbaikan.setText(String.valueOf(totalPerbaikan));

        tvPendapatan.setText("Rp " + String.format("%,d", pendapatan).replace(',', '.'));

        // Hitung progress
        int percentage = (int) (((double) pendapatan / target) * 100);
        pbPendapatan.setProgress(percentage);
        tvPendapatanDesc.setText(percentage + "% dari target bulan ini");
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh data saat kembali ke halaman ini (misal habis nambah kamar)
        loadDashboardStats();
    }
}