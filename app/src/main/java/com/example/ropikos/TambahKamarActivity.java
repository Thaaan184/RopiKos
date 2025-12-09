package com.example.ropikos;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;

public class TambahKamarActivity extends AppCompatActivity {

    private TextInputEditText etAwalan, etNomor, etHarga, etKeterangan;
    private Button btnSimpan;
    private ImageView btnBack;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_kamar);

        dbHelper = new DatabaseHelper(this);

        // Init Views
        etAwalan = findViewById(R.id.et_awalan_unit);
        etNomor = findViewById(R.id.et_nomor_unit);
        etHarga = findViewById(R.id.et_harga_1_bulan);
        etKeterangan = findViewById(R.id.et_keterangan);
        btnSimpan = findViewById(R.id.btn_simpan);
        btnBack = findViewById(R.id.header_edit_tambah_kamar).findViewById(R.id.btn_back); // Perbaiki ID di XML jika perlu

        // Tombol Kembali (Manual karena header custom)
        // Pastikan ImageView di header XML memiliki ID, misal android:id="@+id/btn_back_header"
        // Jika tidak, gunakan findViewById biasa jika ID unik.

        btnSimpan.setOnClickListener(v -> simpanKamar());
    }

    private void simpanKamar() {
        String awalan = etAwalan.getText().toString();
        String nomor = etNomor.getText().toString();
        String hargaStr = etHarga.getText().toString();
        String keterangan = etKeterangan.getText().toString();

        if (awalan.isEmpty() || nomor.isEmpty() || hargaStr.isEmpty()) {
            Toast.makeText(this, "Mohon lengkapi data utama", Toast.LENGTH_SHORT).show();
            return;
        }

        String namaKamar = "Kamar " + awalan; // Contoh format nama
        String nomorLengkap = awalan + nomor;
        double harga = Double.parseDouble(hargaStr);

        boolean sukses = dbHelper.addKamar(namaKamar, nomorLengkap, "AC", keterangan, harga, 0, 0);

        if (sukses) {
            Toast.makeText(this, "Berhasil menambah kamar", Toast.LENGTH_SHORT).show();
            finish(); // Kembali ke list
        } else {
            Toast.makeText(this, "Gagal menambah kamar", Toast.LENGTH_SHORT).show();
        }
    }
}
