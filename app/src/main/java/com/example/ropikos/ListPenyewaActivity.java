package com.example.ropikos;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ropikos.db.DBHelper;
import com.example.ropikos.model.Kamar;
import com.example.ropikos.model.Penyewa;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class ListPenyewaActivity extends AppCompatActivity {

    private DBHelper dbHelper;
    private RecyclerView rvPenyewa;
    private FloatingActionButton fabAddRent;
    private PenyewaAdapter adapter;
    private ImageButton btnProfil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_penyewa);

        dbHelper = new DBHelper(this);
        rvPenyewa = findViewById(R.id.rv_penyewa);
        fabAddRent = findViewById(R.id.fab_add_rent);
        btnProfil = findViewById(R.id.btn_profile);

        rvPenyewa.setLayoutManager(new LinearLayoutManager(this));

        // Use Case 6 Trigger
        fabAddRent.setOnClickListener(v -> {
            startActivity(new Intent(ListPenyewaActivity.this, TambahPenyewaActivity.class));
        });

        // TODO: Buat dan arahkan ke ProfilActivity nanti
        btnProfil.setOnClickListener(v -> {
             startActivity(new Intent(ListPenyewaActivity.this, ProfilActivity.class));
        });

        setupBottomNavigation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDataPenyewa();
    }

    private void loadDataPenyewa() {
        List<Penyewa> listPenyewa = dbHelper.getAllPenyewa();
        adapter = new PenyewaAdapter(listPenyewa);
        rvPenyewa.setAdapter(adapter);
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setSelectedItemId(R.id.nav_penyewa);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_penyewa) return true;
            if (id == R.id.nav_dashboard) {
                startActivity(new Intent(this, MainActivity.class));
            } else if (id == R.id.nav_kamar) {
                startActivity(new Intent(this, ListKamarActivity.class));
            } else if (id == R.id.nav_keuangan) {
                startActivity(new Intent(this, KeuanganActivity.class));
            }
            overridePendingTransition(0, 0);
            finish();
            return true;
        });
    }

    class PenyewaAdapter extends RecyclerView.Adapter<PenyewaAdapter.ViewHolder> {
        private List<Penyewa> data;
        public PenyewaAdapter(List<Penyewa> data) { this.data = data; }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_penyewa, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Penyewa p = data.get(position);
            // Ambil data kamar dari database berdasarkan id_kamar milik penyewa
            Kamar k = dbHelper.getKamar(p.getIdKamar());

            // Logic menampilkan harga sewa
            double hargaFinal = 0;
            if (k != null) {
                // Cek durasi sewa si penyewa (1, 3, atau 6 bulan?)
                int durasi = p.getDurasiSewa();

                if (durasi == 3) {
                    hargaFinal = k.getHarga3Bulan();
                } else if (durasi == 6) {
                    hargaFinal = k.getHarga6Bulan();
                } else {
                    // Default ke 1 bulan jika durasi 1 atau data aneh
                    hargaFinal = k.getHarga1Bulan();
                }
            }

            // Format ke Rupiah (Contoh: Rp 1.000.000)
            String hargaFormatted = String.format(java.util.Locale.forLanguageTag("id"), "Rp %,.0f", hargaFinal);

            holder.tvNama.setText(p.getNama());
            holder.tvHarga.setText(hargaFormatted);

            // Klik item untuk Detail (Use Case 7 & 8 Trigger)
            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(ListPenyewaActivity.this, DetailPenyewaActivity.class);
                intent.putExtra("PENYEWA_ID", p.getId());
                startActivity(intent);
            });
        }

        @Override public int getItemCount() { return data.size(); }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvNama, tvHarga;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvNama = itemView.findViewById(R.id.tv_nama_penyewa);
                tvHarga = itemView.findViewById(R.id.tv_harga_sewa);
            }
        }
    }
}