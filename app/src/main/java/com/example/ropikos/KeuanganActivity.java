package com.example.ropikos;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ropikos.db.DBHelper;
import com.example.ropikos.model.Keuangan;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class KeuanganActivity extends AppCompatActivity {

    private DBHelper dbHelper;
    private RecyclerView rvTransaksi;
    private TextView tvMasuk, tvKeluar, tvFilterDate, tvEmptyState;
    private ImageButton btnProfile;
    private KeuanganAdapter adapter;

    // Variabel Filter: null = Semua, "MM-yyyy" = Filter Bulan Tertentu
    private String currentFilter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keuangan);

        dbHelper = new DBHelper(this);

        // Inisialisasi View
        rvTransaksi = findViewById(R.id.rv_transaksi);
        tvMasuk = findViewById(R.id.tv_keuangan_masuk);
        tvKeluar = findViewById(R.id.tv_keuangan_keluar);
        tvFilterDate = findViewById(R.id.tv_filter_date);
        tvEmptyState = findViewById(R.id.tv_empty_state);
        btnProfile = findViewById(R.id.btn_profile);

        rvTransaksi.setLayoutManager(new LinearLayoutManager(this));

        // Logic Filter Klik
        tvFilterDate.setOnClickListener(v -> showFilterDialog());

        btnProfile.setOnClickListener(v -> {
            // Placeholder: Intent ke Profil jika ada
        });

        setupBottomNavigation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDataKeuangan();
    }

    // --- LOGIC FILTER ---

    private void showFilterDialog() {
        String[] options = {"Semua", "Pilih Bulan & Tahun"};

        new AlertDialog.Builder(this)
                .setTitle("Filter Transaksi")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        // Reset ke Semua
                        currentFilter = null;
                        tvFilterDate.setText("Filter: Semua");
                        loadDataKeuangan();
                    } else {
                        // Buka Picker
                        showMonthYearPicker();
                    }
                })
                .show();
    }

    private void showMonthYearPicker() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // --- PERBAIKAN: Membuat Layout Secara Programmatic (Tanpa XML) ---
        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.HORIZONTAL);
        ll.setGravity(Gravity.CENTER);
        ll.setPadding(32, 32, 32, 32);

        // Setup Month Picker
        final NumberPicker monthPicker = new NumberPicker(this);
        monthPicker.setMinValue(1);
        monthPicker.setMaxValue(12);
        monthPicker.setDisplayedValues(new String[]{"Jan", "Feb", "Mar", "Apr", "Mei", "Jun", "Jul", "Ags", "Sep", "Okt", "Nov", "Des"});

        Calendar cal = Calendar.getInstance();
        monthPicker.setValue(cal.get(Calendar.MONTH) + 1);

        // Setup Year Picker
        final NumberPicker yearPicker = new NumberPicker(this);
        int currentYear = cal.get(Calendar.YEAR);
        yearPicker.setMinValue(currentYear - 5); // 5 tahun ke belakang
        yearPicker.setMaxValue(currentYear + 5); // 5 tahun ke depan
        yearPicker.setValue(currentYear);

        // Tambahkan ke Layout
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(16, 0, 16, 0);

        ll.addView(monthPicker, params);
        ll.addView(yearPicker, params);

        builder.setView(ll)
                .setTitle("Pilih Periode")
                .setPositiveButton("Terapkan", (dialog, id) -> {
                    int selectedMonth = monthPicker.getValue();
                    int selectedYear = yearPicker.getValue();

                    // Format filter: MM-yyyy (contoh: 12-2025 atau 01-2025)
                    String monthStr = selectedMonth < 10 ? "0" + selectedMonth : String.valueOf(selectedMonth);
                    currentFilter = monthStr + "-" + selectedYear;

                    // Update Text UI
                    String[] months = {"Jan", "Feb", "Mar", "Apr", "Mei", "Jun", "Jul", "Ags", "Sep", "Okt", "Nov", "Des"};
                    tvFilterDate.setText("Filter: " + months[selectedMonth - 1] + " " + selectedYear);

                    // Reload Data
                    loadDataKeuangan();
                })
                .setNegativeButton("Batal", null)
                .create()
                .show();
    }

    // --- LOGIC LOAD DATA ---

    private void loadDataKeuangan() {
        List<Keuangan> allData = dbHelper.getAllKeuangan();
        List<Keuangan> filteredList = new ArrayList<>();

        double totalMasuk = 0;
        double totalKeluar = 0;

        // Filter Logic
        for (Keuangan k : allData) {
            boolean include = false;

            if (currentFilter == null) {
                // Tampilkan Semua
                include = true;
            } else {
                // Cek tanggal (Format DB: dd-MM-yyyy)
                if (k.getTanggal() != null && k.getTanggal().endsWith(currentFilter)) {
                    include = true;
                }
            }

            if (include) {
                filteredList.add(k);
                // Hitung Total HANYA untuk yang lolos filter
                if ("Pemasukan".equalsIgnoreCase(k.getTipe())) {
                    totalMasuk += k.getNominal();
                } else if ("Pengeluaran".equalsIgnoreCase(k.getTipe())) {
                    totalKeluar += k.getNominal();
                }
            }
        }

        // Balik urutan (Terbaru diatas)
        Collections.reverse(filteredList);

        // Update UI Total
        if (tvMasuk != null) tvMasuk.setText(formatRupiah(totalMasuk));
        if (tvKeluar != null) tvKeluar.setText(formatRupiah(totalKeluar));

        // Update Adapter
        if (filteredList.isEmpty()) {
            rvTransaksi.setVisibility(View.GONE);
            tvEmptyState.setVisibility(View.VISIBLE);
        } else {
            rvTransaksi.setVisibility(View.VISIBLE);
            tvEmptyState.setVisibility(View.GONE);
        }

        adapter = new KeuanganAdapter(filteredList);
        rvTransaksi.setAdapter(adapter);
    }

    private String formatRupiah(double number) {
        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        formatRupiah.setMaximumFractionDigits(0);
        return formatRupiah.format(number);
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setSelectedItemId(R.id.nav_keuangan);

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_keuangan) return true;
            if (id == R.id.nav_dashboard) {
                startActivity(new Intent(this, MainActivity.class));
            } else if (id == R.id.nav_kamar) {
                startActivity(new Intent(this, ListKamarActivity.class));
            } else if (id == R.id.nav_penyewa) {
                startActivity(new Intent(this, ListPenyewaActivity.class));
            }
            overridePendingTransition(0, 0);
            finish();
            return true;
        });
    }

    // ================== ADAPTER ==================
    class KeuanganAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private static final int TYPE_PEMASUKAN = 0;
        private static final int TYPE_PENGELUARAN = 1;
        private List<Keuangan> data;

        public KeuanganAdapter(List<Keuangan> data) { this.data = data; }

        @Override
        public int getItemViewType(int position) {
            if ("Pengeluaran".equalsIgnoreCase(data.get(position).getTipe())) {
                return TYPE_PENGELUARAN;
            }
            return TYPE_PEMASUKAN;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            if (viewType == TYPE_PENGELUARAN) {
                return new PengeluaranViewHolder(inflater.inflate(R.layout.item_pengeluaran, parent, false));
            } else {
                return new PemasukanViewHolder(inflater.inflate(R.layout.item_pemasukan, parent, false));
            }
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            Keuangan k = data.get(position);
            String rupiah = formatRupiah(k.getNominal());

            if (getItemViewType(position) == TYPE_PENGELUARAN) {
                PengeluaranViewHolder h = (PengeluaranViewHolder) holder;
                h.tvDeskripsi.setText(k.getDeskripsi());
                h.tvTanggal.setText(k.getTanggal());
                h.tvNominal.setText(rupiah);
            } else {
                PemasukanViewHolder h = (PemasukanViewHolder) holder;
                h.tvDeskripsi.setText(k.getDeskripsi());
                h.tvTanggal.setText(k.getTanggal());
                h.tvNominal.setText(rupiah);
            }
        }

        @Override
        public int getItemCount() { return data.size(); }

        class PemasukanViewHolder extends RecyclerView.ViewHolder {
            TextView tvDeskripsi, tvTanggal, tvNominal;
            public PemasukanViewHolder(@NonNull View itemView) {
                super(itemView);
                tvDeskripsi = itemView.findViewById(R.id.tv_pemasukan_deskripsi);
                tvTanggal = itemView.findViewById(R.id.tv_pemasukan_tanggal);
                tvNominal = itemView.findViewById(R.id.tv_pemasukan_nominal);
            }
        }

        class PengeluaranViewHolder extends RecyclerView.ViewHolder {
            TextView tvDeskripsi, tvTanggal, tvNominal;
            public PengeluaranViewHolder(@NonNull View itemView) {
                super(itemView);
                tvDeskripsi = itemView.findViewById(R.id.tv_pengeluaran_deskripsi);
                tvTanggal = itemView.findViewById(R.id.tv_pengeluaran_tanggal);
                tvNominal = itemView.findViewById(R.id.tv_pengeluaran_nominal);
            }
        }
    }
}