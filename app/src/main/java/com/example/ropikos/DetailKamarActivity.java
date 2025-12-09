package com.example.ropikos;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class DetailKamarActivity extends AppCompatActivity {

    private TextView tvTitleKamar, tvStatus, tvDeskripsi, tvHarga1Bulan;
    private Button btnEdit, btnHapus;
    private DatabaseHelper dbHelper;
    private int kamarId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_kamar);

        dbHelper = new DatabaseHelper(this);
        kamarId = getIntent().getIntExtra("KAMAR_ID", -1);

        // Init Views (Sesuaikan ID dengan activity_detail_kamar.xml)
        // Catatan: XML Anda menggunakan CardView statis, kita perlu ID dinamis di XML untuk TextView isinya.
        // Di sini saya asumsikan TextView di dalam CardView bisa diakses.
        // *Saran*: Tambahkan ID pada TextView nilai di XML Anda (misal: tv_val_nama, tv_val_harga)

        // Contoh binding sederhana (Anda perlu update XML agar TextView punya ID yang unik)
        tvTitleKamar = findViewById(R.id.tv_detail_kamar_title); // Ini judul header, bukan konten
        // Mari kita asumsikan Anda sudah menambah ID di XML konten:
        // TextView tvNamaKamarContent = findViewById(R.id.tv_content_nama);

        btnEdit = findViewById(R.id.btn_edit_kamar);
        btnHapus = findViewById(R.id.btn_hapus_kamar);

        loadDetailData();

        btnHapus.setOnClickListener(v -> confirmHapus());

        btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(this, EditKamarActivity.class);
            intent.putExtra("KAMAR_ID", kamarId);
            startActivity(intent);
        });
    }

    private void loadDetailData() {
        Cursor cursor = dbHelper.getKamarDetail(kamarId);
        if (cursor.moveToFirst()) {
            String nama = cursor.getString(cursor.getColumnIndexOrThrow("nama_kamar"));
            String nomor = cursor.getString(cursor.getColumnIndexOrThrow("nomor_kamar"));
            // Set text ke view...
            // tvTitleKamar.setText(nama + ", " + nomor);
        }
        cursor.close();
    }

    private void confirmHapus() {
        new AlertDialog.Builder(this)
                .setTitle("Hapus Kamar")
                .setMessage("Apakah Anda yakin ingin menghapus kamar ini?")
                .setPositiveButton("Ya", (dialog, which) -> {
                    if (dbHelper.deleteKamar(kamarId)) {
                        Toast.makeText(this, "Kamar dihapus", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .setNegativeButton("Batal", null)
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDetailData(); // Refresh data setelah edit
    }
}