package com.example.ropikos;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ropikos.db.DBHelper;
import com.example.ropikos.model.Kamar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class ListKamarActivity extends AppCompatActivity {

    private DBHelper dbHelper;
    private RecyclerView rvKamar;
    private FloatingActionButton fabAddRoom;
    private KamarAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_kamar);

        dbHelper = new DBHelper(this);
        rvKamar = findViewById(R.id.rv_kamar);
        fabAddRoom = findViewById(R.id.fab_add_room);

        rvKamar.setLayoutManager(new LinearLayoutManager(this));

        // Use Case 3: Tombol Tambah -> Pindah ke Activity Tambah
        fabAddRoom.setOnClickListener(v -> {
            startActivity(new Intent(ListKamarActivity.this, TambahKamarActivity.class));
        });

        setupBottomNavigation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDataKamar();
    }

    private void loadDataKamar() {
        List<Kamar> listKamar = dbHelper.getAllKamar();
        adapter = new KamarAdapter(listKamar);
        rvKamar.setAdapter(adapter);
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setSelectedItemId(R.id.nav_kamar);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_kamar) return true;
            if (id == R.id.nav_dashboard) {
                startActivity(new Intent(this, MainActivity.class));
            } else if (id == R.id.nav_penyewa) {
                startActivity(new Intent(this, ListPenyewaActivity.class));
            } else if (id == R.id.nav_keuangan) {
                startActivity(new Intent(this, KeuanganActivity.class));
            }
            overridePendingTransition(0, 0);
            finish();
            return true;
        });
    }

    // --- Adapter Inner Class ---
    class KamarAdapter extends RecyclerView.Adapter<KamarAdapter.ViewHolder> {
        private List<Kamar> data;

        // Parameter
        public KamarAdapter(List<Kamar> data) {
            this.data = data;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_kamar, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Kamar k = data.get(position);
            holder.tvNama.setText(k.getJenisUnit());
            holder.tvNomor.setText("Unit: " + k.getNomorUnit());

            // Klik item untuk lihat detail (Use Case 4 & 5 Trigger)
            // Mengarah ke layout activity_detail_kamar.xml
            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(ListKamarActivity.this, DetailKamarActivity.class);
                intent.putExtra("KAMAR_ID", k.getId());
                startActivity(intent);
            });

            // Klik icon tambah penyewa di card
            holder.btnAddPenyewa.setOnClickListener(v -> {
                Intent intent = new Intent(ListKamarActivity.this, TambahPenyewaActivity.class);
                intent.putExtra("PRESELECTED_KAMAR_ID", k.getId()); // Opsional
                startActivity(intent);
            });
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvNama, tvNomor;
            ImageView btnAddPenyewa;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvNama = itemView.findViewById(R.id.tv_nama_kamar);
                tvNomor = itemView.findViewById(R.id.tv_nomor_kamar);
                // Pastikan di item_kamar.xml, ImageView icon add penyewa diberi ID iv_add_penyewa_shortcut
                // Jika belum ada ID, tambahkan android:id="@+id/iv_add_penyewa_shortcut" di XML item_kamar
                btnAddPenyewa = itemView.findViewById(R.id.iv_add_penyewa_shortcut);

                //? Karena sudah ada id iv_add_penyewa_shortcut di item_kamar.xml, maybe bisa dihapus saja?
                if(btnAddPenyewa == null) {
                    // Fallback jika ID tidak ditemukan agar tidak crash saat testing
                    btnAddPenyewa = (ImageView) ((ViewGroup)itemView).getChildAt(0); // Dummy logic
                }
            }
        }
    }
}