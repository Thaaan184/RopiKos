package com.example.ropikos;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.ArrayList;
import java.util.HashMap;

public class KamarActivity extends AppCompatActivity {

    private LinearLayout listKamarContainer;
    private FloatingActionButton fabAddRoom;
    private DatabaseHelper dbHelper;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_kamar);

        dbHelper = new DatabaseHelper(this);
        listKamarContainer = findViewById(R.id.list_kamar_container);
        fabAddRoom = findViewById(R.id.fab_add_room);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Aksi Tombol Tambah
        fabAddRoom.setOnClickListener(v -> {
            Intent intent = new Intent(KamarActivity.this, TambahKamarActivity.class);
            startActivity(intent);
        });

        // Setup Bottom Nav (Agar konsisten dengan MainActivity)
        bottomNavigationView.setSelectedItemId(R.id.nav_kamar);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_dashboard) {
                startActivity(new Intent(this, MainActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_kamar) {
                return true;
            }
            // Tambahkan case lain untuk Penyewa/Pendapatan
            return false;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDataKamar(); // Reload data saat kembali ke halaman ini
    }

    private void loadDataKamar() {
        listKamarContainer.removeAllViews(); // Bersihkan list lama/placeholder XML
        ArrayList<HashMap<String, String>> dataKamar = dbHelper.getAllKamar();

        LayoutInflater inflater = LayoutInflater.from(this);

        for (HashMap<String, String> kamar : dataKamar) {
            // Inflate layout item_kamar.xml
            View itemView = inflater.inflate(R.layout.item_kamar, listKamarContainer, false);

            TextView tvNama = itemView.findViewById(R.id.tv_nama_kamar);
            TextView tvNomor = itemView.findViewById(R.id.tv_nomor_kamar);

            String id = kamar.get("id");
            String nama = kamar.get("nama");
            String nomor = kamar.get("nomor");

            tvNama.setText(nama);
            tvNomor.setText(nomor); // Misal: "Kamar A - 01"

            // Aksi Klik Item -> Ke Detail
            itemView.setOnClickListener(v -> {
                Intent intent = new Intent(KamarActivity.this, DetailKamarActivity.class);
                intent.putExtra("KAMAR_ID", Integer.parseInt(id));
                startActivity(intent);
            });

            listKamarContainer.addView(itemView);
        }
    }
}