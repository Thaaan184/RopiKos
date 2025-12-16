package com.example.ropikos;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.ropikos.db.DBHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.NumberFormat;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private DBHelper dbHelper;

    // UI Dashboard
    private TextView tvTotalKamar, tvTotalPenyewa, tvTotalLunas, tvTotalPerbaikan;
    private TextView tvPendapatanValue, tvPendapatanDesc;
    private ProgressBar pbPendapatan;

    // UI Welcome & Pendapatan Card
    private TextView tvWelcomeTitle, tvWelcomeDesc;
    private CardView cvWelcome, cvPendapatan;

    private ImageButton btnProfile;

    // Shared Preferences untuk menyimpan Target & Teks Welcome
    private SharedPreferences sharedPreferences;
    private static final String PREF_NAME = "RopiKosPrefs";
    private static final String KEY_TARGET = "target_pendapatan";
    private static final String KEY_WELCOME_TITLE = "welcome_title";
    private static final String KEY_WELCOME_DESC = "welcome_desc";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DBHelper(this);
        sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        // Init UI
        tvTotalKamar = findViewById(R.id.tv_total_kamar_value);
        tvTotalPenyewa = findViewById(R.id.tv_total_penyewa_value);
        tvTotalLunas = findViewById(R.id.tv_total_lunas_value);
        tvTotalPerbaikan = findViewById(R.id.tv_total_perbaikan_value);
        tvPendapatanValue = findViewById(R.id.tv_pendapatan_value);
        tvPendapatanDesc = findViewById(R.id.tv_pendapatan_desc);
        pbPendapatan = findViewById(R.id.pb_pendapatan);
        btnProfile = findViewById(R.id.btn_profile);

        // Init Card Welcome
        cvWelcome = findViewById(R.id.cv_welcome);
        tvWelcomeTitle = findViewById(R.id.tv_welcome_title);
        tvWelcomeDesc = findViewById(R.id.tv_welcome_desc);

        // Init Card Pendapatan
        cvPendapatan = findViewById(R.id.cv_pendapatan);

        btnProfile.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ProfilActivity.class);
            startActivity(intent);
        });

        // --- SETUP LISTENER CARD UNTUK POPUP EDIT ---
        setupEditListeners();

        setupBottomNavigation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDashboardData();
    }

    private void setupEditListeners() {
        // 1. Logic Klik Card Welcome -> Muncul Popup Edit Teks
        cvWelcome.setOnClickListener(v -> showEditWelcomeDialog());

        // 2. Logic Klik Card Pendapatan -> Muncul Popup Edit Target
        cvPendapatan.setOnClickListener(v -> showEditTargetDialog());
    }

    // --- DIALOG EDIT WELCOME ---
    private void showEditWelcomeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Ubah Teks Sapaan");

        // Layout Custom untuk Dialog
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 40, 50, 10);

        final EditText inputTitle = new EditText(this);
        inputTitle.setHint("Judul (Cth: Selamat Datang!)");
        inputTitle.setText(tvWelcomeTitle.getText());
        layout.addView(inputTitle);

        final EditText inputDesc = new EditText(this);
        inputDesc.setHint("Deskripsi");
        inputDesc.setText(tvWelcomeDesc.getText());
        layout.addView(inputDesc);

        builder.setView(layout);

        builder.setPositiveButton("Simpan", (dialog, which) -> {
            String newTitle = inputTitle.getText().toString();
            String newDesc = inputDesc.getText().toString();

            // Update UI
            tvWelcomeTitle.setText(newTitle);
            tvWelcomeDesc.setText(newDesc);

            // Simpan ke SharedPreferences
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(KEY_WELCOME_TITLE, newTitle);
            editor.putString(KEY_WELCOME_DESC, newDesc);
            editor.apply();
        });

        builder.setNegativeButton("Batal", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    // --- DIALOG EDIT TARGET PENDAPATAN ---
    private void showEditTargetDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Ubah Target Bulanan");

        final EditText inputTarget = new EditText(this);
        inputTarget.setInputType(InputType.TYPE_CLASS_NUMBER);

        // Ambil target saat ini dari Prefs
        long currentTarget = sharedPreferences.getLong(KEY_TARGET, 5000000);
        inputTarget.setText(String.valueOf(currentTarget));

        // Menambahkan padding ke EditText di dialog
        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setPadding(50, 40, 50, 10);
        container.addView(inputTarget);
        builder.setView(container);

        builder.setPositiveButton("Simpan", (dialog, which) -> {
            String targetStr = inputTarget.getText().toString();
            if (!targetStr.isEmpty()) {
                long newTarget = Long.parseLong(targetStr);

                // Simpan Target Baru
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putLong(KEY_TARGET, newTarget);
                editor.apply();

                // Reload Data Dashboard untuk update Progress Bar
                loadDashboardData();
            }
        });

        builder.setNegativeButton("Batal", null);
        builder.show();
    }

    private void loadDashboardData() {
        // Load data Statistik DB
        int totalKamar = dbHelper.getTotalKamar();
        int totalPenyewa = dbHelper.getTotalPenyewa();
        int totalPerbaikan = dbHelper.getTotalPerbaikan();
        int totalLunas = dbHelper.getTotalLunas();

        tvTotalKamar.setText(String.valueOf(totalKamar));
        tvTotalPenyewa.setText(String.valueOf(totalPenyewa));
        tvTotalLunas.setText(String.valueOf(totalLunas));
        tvTotalPerbaikan.setText(String.valueOf(totalPerbaikan));

        // Load Teks Welcome dari Prefs
        String savedTitle = sharedPreferences.getString(KEY_WELCOME_TITLE, "Selamat Datang!");
        String savedDesc = sharedPreferences.getString(KEY_WELCOME_DESC, "Kelola kamar dan penyewa Anda dengan mudah hari ini.");
        tvWelcomeTitle.setText(savedTitle);
        tvWelcomeDesc.setText(savedDesc);

        // Load Logic Pendapatan & Target
        double pendapatan = dbHelper.getPendapatanBulanIni();
        // Ambil target dari SharedPrefs, default 5 juta
        double target = sharedPreferences.getLong(KEY_TARGET, 5000000);

        int progress = 0;
        if (target > 0) {
            progress = (int) ((pendapatan / target) * 100);
        }

        if (progress > 100) progress = 100;

        tvPendapatanValue.setText(formatRupiah(pendapatan));
        pbPendapatan.setProgress(progress);
        tvPendapatanDesc.setText(progress + "% dari target Rp " + formatAngka(target));
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_dashboard);

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
        // Hilangkan .00 di belakang jika mau lebih rapi
        formatRupiah.setMaximumFractionDigits(0);
        return formatRupiah.format(number);
    }

    private String formatAngka(double number) {
        return String.format(Locale.US, "%,.0f", number);
    }
}