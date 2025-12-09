package com.example.ropikos;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ropikos.db.DBHelper;
import com.example.ropikos.model.Keuangan;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class KeuanganActivity extends AppCompatActivity {

    private DBHelper dbHelper;
    private RecyclerView rvTransaksi;
    private TextView tvMasuk, tvKeluar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keuangan);

        dbHelper = new DBHelper(this);
        rvTransaksi = findViewById(R.id.rv_transaksi);

        tvMasuk = findViewById(R.id.tv_keuangan_masuk);
        tvKeluar = findViewById(R.id.tv_keuangan_keluar);

        rvTransaksi.setLayoutManager(new LinearLayoutManager(this));

        setupBottomNavigation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDataKeuangan();
    }

    private void loadDataKeuangan() {
        List<Keuangan> list = dbHelper.getAllKeuangan();

        double totalMasuk = 0;
        double totalKeluar = 0;

        for (Keuangan k : list) {
            if ("Pemasukan".equalsIgnoreCase(k.getTipe())) {
                totalMasuk += k.getNominal();
            } else {
                totalKeluar += k.getNominal();
            }
        }

        // Jika ID belum ditambahkan di XML, kode ini akan error (NullPointer)
        if (tvMasuk != null) tvMasuk.setText(formatRupiah(totalMasuk));
        if (tvKeluar != null) tvKeluar.setText(formatRupiah(totalKeluar));

        rvTransaksi.setAdapter(new KeuanganAdapter(list));
    }

    private String formatRupiah(double number) {
        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
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

    // Adapter Keuangan
    class KeuanganAdapter extends RecyclerView.Adapter<KeuanganAdapter.ViewHolder> {
        private List<Keuangan> data;
        public KeuanganAdapter(List<Keuangan> data) { this.data = data; }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(android.R.layout.simple_list_item_2, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Keuangan k = data.get(position);
            holder.text1.setText(k.getDeskripsi() + " (" + k.getTanggal() + ")");

            String formatted = formatRupiah(k.getNominal());
            holder.text2.setText(formatted);

            if ("Pengeluaran".equalsIgnoreCase(k.getTipe())) {
                holder.text2.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.red_color));
            } else {
                holder.text2.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.green_color));
            }
        }

        @Override
        public int getItemCount() { return data.size(); }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView text1, text2;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                text1 = itemView.findViewById(android.R.id.text1);
                text2 = itemView.findViewById(android.R.id.text2);
            }
        }
    }
}