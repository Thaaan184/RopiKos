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
import android.widget.Toast;

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
    private String currentFilter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keuangan);

        dbHelper = new DBHelper(this);

        rvTransaksi = findViewById(R.id.rv_transaksi);
        tvMasuk = findViewById(R.id.tv_keuangan_masuk);
        tvKeluar = findViewById(R.id.tv_keuangan_keluar);
        tvFilterDate = findViewById(R.id.tv_filter_date);
        tvEmptyState = findViewById(R.id.tv_empty_state);
        btnProfile = findViewById(R.id.btn_profile);

        rvTransaksi.setLayoutManager(new LinearLayoutManager(this));

        tvFilterDate.setOnClickListener(v -> showFilterDialog());
        btnProfile.setOnClickListener(v -> {});

        setupBottomNavigation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDataKeuangan();
    }

    // --- LOGIC DIALOG OPSI & HAPUS BERLIPAT ---

    private void showOptionsDialog(Keuangan k) {
        String[] options = {"Ubah Data", "Hapus Data"};

        new AlertDialog.Builder(this)
                .setTitle("Pilih Aksi")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        // Opsi Ubah -> Buka Activity Edit
                        Intent intent = new Intent(KeuanganActivity.this, EditKeuanganActivity.class);
                        intent.putExtra("KEUANGAN_ID", k.getId());
                        startActivity(intent);
                    } else {
                        // Opsi Hapus -> Masuk Konfirmasi 1
                        showDeleteConfirmation1(k.getId());
                    }
                })
                .show();
    }

    // Konfirmasi Tahap 1
    private void showDeleteConfirmation1(int id) {
        new AlertDialog.Builder(this)
                .setTitle("Hapus Transaksi?")
                .setMessage("Apakah Anda yakin ingin menghapus transaksi ini?")
                .setPositiveButton("Ya, Hapus", (dialog, which) -> {
                    // Masuk Konfirmasi Tahap 2
                    showDeleteConfirmation2(id);
                })
                .setNegativeButton("Batal", null)
                .show();
    }

    // Konfirmasi Tahap 2 (Final)
    private void showDeleteConfirmation2(int id) {
        new AlertDialog.Builder(this)
                .setTitle("PERINGATAN TERAKHIR")
                .setMessage("Data yang dihapus TIDAK BISA dikembalikan. Anda benar-benar yakin?")
                .setPositiveButton("Hapus Sekarang", (dialog, which) -> {
                    // Eksekusi Hapus
                    dbHelper.deleteKeuangan(id);
                    Toast.makeText(this, "Transaksi berhasil dihapus", Toast.LENGTH_SHORT).show();
                    loadDataKeuangan(); // Reload list
                })
                .setNegativeButton("Batal", null)
                .show();
    }

    // --- LOGIC FILTER ---
    private void showFilterDialog() {
        String[] options = {"Semua", "Pilih Bulan & Tahun"};
        new AlertDialog.Builder(this)
                .setTitle("Filter Transaksi")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        currentFilter = null;
                        tvFilterDate.setText("Filter: Semua");
                        loadDataKeuangan();
                    } else {
                        showMonthYearPicker();
                    }
                })
                .show();
    }

    private void showMonthYearPicker() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.HORIZONTAL);
        ll.setGravity(Gravity.CENTER);
        ll.setPadding(32, 32, 32, 32);

        final NumberPicker monthPicker = new NumberPicker(this);
        monthPicker.setMinValue(1);
        monthPicker.setMaxValue(12);
        monthPicker.setDisplayedValues(new String[]{"Jan", "Feb", "Mar", "Apr", "Mei", "Jun", "Jul", "Ags", "Sep", "Okt", "Nov", "Des"});
        Calendar cal = Calendar.getInstance();
        monthPicker.setValue(cal.get(Calendar.MONTH) + 1);

        final NumberPicker yearPicker = new NumberPicker(this);
        int currentYear = cal.get(Calendar.YEAR);
        yearPicker.setMinValue(currentYear - 5);
        yearPicker.setMaxValue(currentYear + 5);
        yearPicker.setValue(currentYear);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(16, 0, 16, 0);
        ll.addView(monthPicker, params);
        ll.addView(yearPicker, params);

        builder.setView(ll)
                .setTitle("Pilih Periode")
                .setPositiveButton("Terapkan", (dialog, id) -> {
                    int selectedMonth = monthPicker.getValue();
                    int selectedYear = yearPicker.getValue();
                    String monthStr = selectedMonth < 10 ? "0" + selectedMonth : String.valueOf(selectedMonth);
                    currentFilter = monthStr + "-" + selectedYear;

                    String[] months = {"Jan", "Feb", "Mar", "Apr", "Mei", "Jun", "Jul", "Ags", "Sep", "Okt", "Nov", "Des"};
                    tvFilterDate.setText("Filter: " + months[selectedMonth - 1] + " " + selectedYear);
                    loadDataKeuangan();
                })
                .setNegativeButton("Batal", null)
                .create().show();
    }

    // --- LOAD DATA ---
    private void loadDataKeuangan() {
        List<Keuangan> allData = dbHelper.getAllKeuangan();
        List<Keuangan> filteredList = new ArrayList<>();
        double totalMasuk = 0;
        double totalKeluar = 0;

        for (Keuangan k : allData) {
            boolean include = false;
            if (currentFilter == null) {
                include = true;
            } else {
                if (k.getTanggal() != null && k.getTanggal().endsWith(currentFilter)) {
                    include = true;
                }
            }

            if (include) {
                filteredList.add(k);
                if ("Pemasukan".equalsIgnoreCase(k.getTipe())) {
                    totalMasuk += k.getNominal();
                } else if ("Pengeluaran".equalsIgnoreCase(k.getTipe())) {
                    totalKeluar += k.getNominal();
                }
            }
        }
        Collections.reverse(filteredList);

        if (tvMasuk != null) tvMasuk.setText(formatRupiah(totalMasuk));
        if (tvKeluar != null) tvKeluar.setText(formatRupiah(totalKeluar));

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

    // --- ADAPTER ---
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

            // --- LOGIKA KLIK ITEM (POPUP) ---
            holder.itemView.setOnClickListener(v -> {
                showOptionsDialog(k);
            });

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