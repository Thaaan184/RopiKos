package com.example.ropikos;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

        // Setup RecyclerView
        rvKamar.setLayoutManager(new LinearLayoutManager(this));

        // FAB Action
        fabAddRoom.setOnClickListener(v -> {
            // Ganti dengan Intent ke Activity Tambah Kamar
            Toast.makeText(this, "Fitur Tambah Kamar", Toast.LENGTH_SHORT).show();
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

    // --- SIMPLE ADAPTER INNER CLASS ---
    // Pastikan Anda sudah membuat layout item_kamar.xml secara terpisah
    class KamarAdapter extends RecyclerView.Adapter<KamarAdapter.ViewHolder> {
        private List<Kamar> data;

        public KamarAdapter(List<Kamar> data) {
            this.data = data;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            // Gunakan layout simple_list_item_1 bawaan Android jika belum buat custom layout
            // Atau ganti android.R.layout.simple_list_item_2 dengan R.layout.item_kamar
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(android.R.layout.simple_list_item_2, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Kamar k = data.get(position);
            holder.text1.setText("Unit " + k.getNomorUnit() + " (" + k.getJenisUnit() + ")");
            holder.text2.setText(k.getStatus() == 1 ? "Terisi" : "Kosong");
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

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