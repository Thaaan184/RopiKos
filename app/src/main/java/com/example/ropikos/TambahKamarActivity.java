package com.example.ropikos;

import android.os.Bundle;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.ropikos.db.DBHelper;
import com.example.ropikos.model.Kamar;
import com.google.android.material.textfield.TextInputEditText;

public class TambahKamarActivity extends AppCompatActivity {

    private DBHelper dbHelper;
    private AutoCompleteTextView actvJenis, actvMaks;
    private TextInputEditText etAwalan, etNomor, etKeterangan;
    private TextInputEditText etHarga1, etHarga3, etHarga6;
    private Button btnSimpan;
    private ImageView btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_kamar);

        dbHelper = new DBHelper(this);

        actvJenis = findViewById(R.id.actv_jenis_unit);
        actvMaks = findViewById(R.id.actv_maksimal_penyewa);
        etAwalan = findViewById(R.id.et_awalan_unit);
        etNomor = findViewById(R.id.et_nomor_unit);
        etKeterangan = findViewById(R.id.et_keterangan);
        etHarga1 = findViewById(R.id.et_harga_1_bulan);
        etHarga3 = findViewById(R.id.et_harga_3_bulan);
        etHarga6 = findViewById(R.id.et_harga_6_bulan);
        btnSimpan = findViewById(R.id.btn_simpan);
        btnBack = findViewById(R.id.btn_back); // ImageView back di header

        if(btnBack != null) btnBack.setOnClickListener(v -> finish());

        // Logic Simpan (Use Case 3)
        btnSimpan.setOnClickListener(v -> {
            try {
                String jenis = actvJenis.getText().toString();
                String nomorFull = etAwalan.getText().toString() + etNomor.getText().toString();
                String ket = etKeterangan.getText().toString();

                String h1Str = etHarga1.getText().toString();
                String h3Str = etHarga3.getText().toString();
                String h6Str = etHarga6.getText().toString();

                // Memastikan harga tidak kosong jika user mengosongkan TextInputEditText
                double h1 = h1Str.isEmpty() ? 0 : Double.parseDouble(h1Str);
                double h3 = h3Str.isEmpty() ? 0 : Double.parseDouble(h3Str);
                double h6 = h6Str.isEmpty() ? 0 : Double.parseDouble(h6Str);

                String maksStr = actvMaks.getText().toString().replaceAll("[^0-9]", ""); // Ambil angka saja
                int maks = maksStr.isEmpty() ? 1 : Integer.parseInt(maksStr);

                Kamar kamarBaru = new Kamar(jenis, nomorFull, ket, maks, h1, h3, h6, 0);
                long res = dbHelper.insertKamar(kamarBaru);

                if (res > 0) {
                    Toast.makeText(this, "Kamar Berhasil Ditambah", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "Gagal menambah kamar", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Toast.makeText(this, "Error input: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}