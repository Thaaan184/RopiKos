package com.example.ropikos;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ropikos.db.DBHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.NumberFormat;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private DBHelper dbHelper;
    private TextView tvTotalKamar, tvTotalPenyewa, tvTotalLunas, tvTotalPerbaikan;
    private TextView tvPendapatanValue, tvPendapatanDesc;
    private ProgressBar pbPendapatan;
    private ImageButton btnProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DBHelper(this);

        // Init Views
        tvTotalKamar = findViewById(R.id.tv_total_kamar_value);
        tvTotalPenyewa = findViewById(R.id.tv_total_penyewa_value);
        tvTotalLunas = findViewById(R.id.tv_total_lunas_value);
        tvTotalPerbaikan = findViewById(R.id.tv_total_perbaikan_value);
        tvPendapatanValue = findViewById(R.id.tv_pendapatan_value);
        tvPendapatanDesc = findViewById(R.id.tv_pendapatan_desc);
        pbPendapatan = findViewById(R.id.pb_pendapatan);
        btnProfile = findViewById(R.id.btn_profile);

        // Profile Button Logic
        btnProfile.setOnClickListener(v -> {
            // Intent ke ProfileActivity (Jika sudah ada)
            Toast.makeText(this, "Ke Menu Profil", Toast.LENGTH_SHORT).show();
        });

        setupBottomNavigation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDashboardData();
    }

    private void loadDashboardData() {
        // Ambil data statistik dari DBHelper
        int totalKamar = dbHelper.getTotalKamar();
        int totalPenyewa = dbHelper.getTotalPenyewa();
        int totalPerbaikan = dbHelper.getTotalPerbaikan();
        int totalLunas = dbHelper.getTotalLunas(); // Logic pelunasan

        // Set Text
        tvTotalKamar.setText(String.valueOf(totalKamar));
        tvTotalPenyewa.setText(String.valueOf(totalPenyewa));
        tvTotalLunas.setText(String.valueOf(totalLunas));
        tvTotalPerbaikan.setText(String.valueOf(totalPerbaikan));

        // Simulasi Pendapatan (Nanti diganti real logic)
        double pendapatan = dbHelper.getPendapatanBulanIni();
        double target = 5000000; // Contoh target
        int progress = (int) ((pendapatan / target) * 100);

        tvPendapatanValue.setText(formatRupiah(pendapatan));
        pbPendapatan.setProgress(progress);
        tvPendapatanDesc.setText(progress + "% dari target bulan ini");
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_dashboard); // Set active item

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_dashboard) {
                return true;
            } else if (itemId == R.id.nav_kamar) {
                startActivity(new Intent(getApplicationContext(), ListKamarActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (itemId == R.id.nav_penyewa) {
                startActivity(new Intent(getApplicationContext(), ListPenyewaActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (itemId == R.id.nav_keuangan) {
                startActivity(new Intent(getApplicationContext(), KeuanganActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            }
            return false;
        });
    }

    private String formatRupiah(double number) {
        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        return formatRupiah.format(number);
    }
}