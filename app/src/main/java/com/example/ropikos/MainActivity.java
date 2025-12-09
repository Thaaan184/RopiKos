package com.example.ropikos;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.ProgressBar;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.text.NumberFormat;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    // Deklarasi Variabel UI sesuai Mockup (Gambar 3)
    private TextView tvTotalKamar, tvTotalPenyewa, tvTotalLunas, tvTotalPerbaikan;
    private TextView tvPendapatanBulanIni, tvPersentaseTarget;
    private ProgressBar progressBarTarget;
    private BottomNavigationView bottomNavigationView;

    private DatabaseHelper dbHelper;
    private final double TARGET_PENDAPATAN = 10000000; // Contoh target hardcoded atau ambil dari settings

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Pastikan layout XML sesuai

        // Inisialisasi Database
        dbHelper = new DatabaseHelper(this);

        // Inisialisasi Komponen UI
        initViews();

        // Setup Navigasi Bawah (FR-15)
        setupBottomNavigation();
    }

    // FR-17: Pembaruan Otomatis Statistik
    // Data dimuat ulang setiap kali user kembali ke halaman ini (setelah edit/tambah data)
    @Override
    protected void onResume() {
        super.onResume();
        loadDashboardData();
    }

    private void initViews() {
        tvTotalKamar = findViewById(R.id.tv_total_kamar);
        tvTotalPenyewa = findViewById(R.id.tv_total_penyewa);
        tvTotalLunas = findViewById(R.id.tv_total_lunas);
        tvTotalPerbaikan = findViewById(R.id.tv_total_perbaikan);
        tvPendapatanBulanIni = findViewById(R.id.tv_pendapatan_value);
        tvPersentaseTarget = findViewById(R.id.tv_target_persen);
        progressBarTarget = findViewById(R.id.progress_bar_pendapatan);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
    }

    private void loadDashboardData() {
        // Mengambil data dari SQLite (FR-05)
        int totalKamar = dbHelper.getTotalKamar();
        int totalPenyewa = dbHelper.getTotalPenyewa();
        int totalLunas = dbHelper.getTotalLunasBulanIni();
        int totalPerbaikan = dbHelper.getTotalPerbaikan();
        double pendapatan = dbHelper.getPendapatanBulanIni();

        // Update UI Text
        tvTotalKamar.setText(String.valueOf(totalKamar));
        tvTotalPenyewa.setText(String.valueOf(totalPenyewa));
        tvTotalLunas.setText(String.valueOf(totalLunas));
        tvTotalPerbaikan.setText(String.valueOf(totalPerbaikan));

        // Format Mata Uang (Rupiah)
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        tvPendapatanBulanIni.setText(formatRupiah.format(pendapatan));

        // Update Logic Progress Bar Target
        int percentage = (int) ((pendapatan / TARGET_PENDAPATAN) * 100);
        if (percentage > 100) percentage = 100;

        progressBarTarget.setProgress(percentage);
        tvPersentaseTarget.setText(percentage + "% dari target bulan ini");
    }

    private void setupBottomNavigation() {
        // FR-15: Navigasi Bawah berisi Dashboard, Kamar, Penyewa, Pendapatan
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_dashboard) {
                return true; // Sudah di dashboard
            } else if (itemId == R.id.nav_kamar) {
                // Intent ke KamarActivity (List Kamar)
                startActivity(new Intent(this, KamarActivity.class));
                return true;
            } else if (itemId == R.id.nav_penyewa) {
                // Intent ke PenyewaActivity (List Penyewa)
                startActivity(new Intent(this, PenyewaActivity.class));
                return true;
            } else if (itemId == R.id.nav_pendapatan) {
                // Intent ke PendapatanActivity (Laporan)
                startActivity(new Intent(this, PendapatanActivity.class));
                return true;
            }
            return false;
        });

        // Set default selection
        bottomNavigationView.setSelectedItemId(R.id.nav_dashboard);
    }
}