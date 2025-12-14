package com.example.ropikos;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.ropikos.db.DBHelper;
import com.example.ropikos.model.Kamar;
import java.util.Locale;
import androidx.core.content.ContextCompat;

public class DetailKamarActivity extends AppCompatActivity {

    private DBHelper dbHelper;
    private int kamarId;
    private TextView tvJenisNomor, tvStatus, tvKeterangan;
    private TextView tvHarga1Bulan, tvHarga3Bulan, tvHarga6Bulan;
    private Button btnEdit, btnHapus;
    private ImageView btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_kamar);

        dbHelper = new DBHelper(this);
        kamarId = getIntent().getIntExtra("KAMAR_ID", -1);

        // ID Mapping (Sesuaikan dengan activity_detail_kamar.xml)
        btnEdit = findViewById(R.id.btn_edit_kamar);
        btnHapus = findViewById(R.id.btn_hapus_kamar);

        // Inisialisasi TextView data
        tvJenisNomor = findViewById(R.id.tv_jenis_nomor_kamar);
        tvStatus = findViewById(R.id.tv_status_kamar);
        tvKeterangan = findViewById(R.id.tv_keterangan_kamar);
        tvHarga1Bulan = findViewById(R.id.tv_harga_1_bulan);
        tvHarga3Bulan = findViewById(R.id.tv_harga_3_bulan);
        tvHarga6Bulan = findViewById(R.id.tv_harga_6_bulan);

        // Header
        // * Karena judul di layout sudah statis ("Detail Kamar"), maka inisialisasi variabel tvHeaderTitle tidak diperlukan.
        // TextView tvHeaderTitle = findViewById(R.id.header_detail_kamar).findViewById(R.id.tv_detail_kamar_title);

        btnBack = findViewById(R.id.header_detail_kamar).findViewById(R.id.btn_back); // ID dari include header biasanya
        if(btnBack == null) btnBack = findViewById(R.id.btn_back); // Fallback cari direct ID

        if(btnBack != null) btnBack.setOnClickListener(v -> finish());

        loadData();

        // Trigger Edit (Use Case 4)
        btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(this, EditKamarActivity.class);
            intent.putExtra("KAMAR_ID", kamarId);
            startActivity(intent);
        });

        // Trigger Hapus (Use Case 5)
        btnHapus.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Hapus Kamar")
                    .setMessage("Apakah Anda yakin ingin menghapus kamar ini secara permanen?")
                    .setPositiveButton("Ya", (dialog, which) -> {
                        dbHelper.deleteKamar(kamarId);
                        Toast.makeText(this, "Kamar dihapus", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .setNegativeButton("Batal", null)
                    .show();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    private void loadData() {
        Kamar k = dbHelper.getKamar(kamarId);
        if (k != null) {
            // Logic Menampilkan Data Dinamis
            tvJenisNomor.setText(k.getJenisUnit() + ", " + k.getNomorUnit());
            tvKeterangan.setText(k.getKeterangan());

            // Gunakan Locale Indonesia untuk format titik
            Locale localeID = Locale.forLanguageTag("id");

            // Set Harga
            tvHarga1Bulan.setText("Rp " + String.format(localeID, "%,.0f", k.getHarga1Bulan()));
            tvHarga3Bulan.setText("Rp " + String.format(localeID, "%,.0f", k.getHarga1Bulan()));
            tvHarga6Bulan.setText("Rp " + String.format(localeID, "%,.0f", k.getHarga1Bulan()));

            // Set Status
            if (k.getStatus() == 0) {
                tvStatus.setText("Kosong");
                tvStatus.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_dark));
            } else {
                tvStatus.setText("Terisi");
                tvStatus.setTextColor(ContextCompat.getColor(this, android.R.color.holo_green_dark));
            }
        }
    }
}