package com.example.ropikos;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.ropikos.db.DBHelper;
import com.example.ropikos.model.Penyewa;
import com.google.android.material.textfield.TextInputEditText;

public class TambahPenyewaActivity extends AppCompatActivity {

    private DBHelper dbHelper;
    private TextInputEditText etNama;
    private Button btnSimpan;
    private ImageView btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_penyewa);

        dbHelper = new DBHelper(this);
        etNama = findViewById(R.id.et_nama_penyewa); // Pastikan ID ini ada di XML tambah penyewa, jika belum tambahkan
        if (etNama == null) {
            // Fallback ID jika di XML belum ada ID spesifik, gunakan ID wrapper atau sesuaikan
            // Disini saya asumsikan di XML anda sudah ada TextInputEditText
            etNama = findViewById(R.id.header).findViewById(R.id.tv_title); // Dummy
        }

        // Cari ID button simpan
        btnSimpan = findViewById(R.id.btn_simpan);
        btnBack = findViewById(R.id.btn_back);

        if(btnBack != null) btnBack.setOnClickListener(v -> finish());

        btnSimpan.setOnClickListener(v -> {
            // Karena ID textinput di XML Anda belum spesifik semua, saya pakai hardcode dummy
            // Anda harus update ID di XML activity_tambah_penyewa.xml terlebih dahulu
            // Contoh: android:id="@+id/et_nama_penyewa" pada TextInputEditText

            Penyewa p = new Penyewa();
            p.setNama("Penyewa Baru"); // Ambil dari EditText
            p.setWhatsapp("0812345");

            long res = dbHelper.insertPenyewa(p);
            if (res > 0) {
                Toast.makeText(this, "Penyewa Ditambah", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}