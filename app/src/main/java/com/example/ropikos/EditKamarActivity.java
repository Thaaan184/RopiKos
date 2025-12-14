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
import java.util.Locale;

public class EditKamarActivity extends AppCompatActivity {

    private DBHelper dbHelper;
    private int kamarId;
    private AutoCompleteTextView actvJenis, actvMaks;
    private TextInputEditText etAwalan, etNomor, etKeterangan;
    private TextInputEditText etHarga1, etHarga3, etHarga6;
    private Button btnUpdate;
    private ImageView btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_kamar);

        dbHelper = new DBHelper(this);
        kamarId = getIntent().getIntExtra("KAMAR_ID", -1);

        btnUpdate = findViewById(R.id.btn_update);
        btnBack = findViewById(R.id.btn_back);
        actvJenis = findViewById(R.id.actv_jenis_unit);
        actvMaks = findViewById(R.id.actv_maksimal_penyewa);
        etAwalan = findViewById(R.id.et_awalan_unit);
        etNomor = findViewById(R.id.et_nomor_unit);
        etKeterangan = findViewById(R.id.et_keterangan);
        etHarga1 = findViewById(R.id.et_harga_1_bulan);
        etHarga3 = findViewById(R.id.et_harga_3_bulan);
        etHarga6 = findViewById(R.id.et_harga_6_bulan);

        if(btnBack != null) btnBack.setOnClickListener(v -> finish());

        loadCurrentData();

        btnUpdate.setOnClickListener(v -> {
            try {
                Kamar k = new Kamar();
                k.setId(kamarId); // ID harus diset agar DB tahu mana yang diupdate
                k.setJenisUnit(actvJenis.getText().toString());
                k.setKeterangan(etKeterangan.getText().toString());

                String maksStr = actvMaks.getText().toString().replaceAll("[^0-9]", "");
                int maks = maksStr.isEmpty() ? 1 : Integer.parseInt(maksStr);
                k.setMaksPenyewa(maks);

                String nomorFull = etAwalan.getText().toString().trim() + etNomor.getText().toString().trim();
                k.setNomorUnit(nomorFull);

                String h1Str = etHarga1.getText().toString();
                String h3Str = etHarga3.getText().toString();
                String h6Str = etHarga6.getText().toString();

                // Memastikan harga tidak kosong jika user mengosongkan TextInputEditText
                double h1 = h1Str.isEmpty() ? 0 : Double.parseDouble(h1Str);
                double h3 = h3Str.isEmpty() ? 0 : Double.parseDouble(h3Str);
                double h6 = h6Str.isEmpty() ? 0 : Double.parseDouble(h6Str);

                k.setHarga1Bulan(h1);
                k.setHarga3Bulan(h3);
                k.setHarga6Bulan(h6);

                int result = dbHelper.updateKamar(k);
                if (result > 0) {
                    Toast.makeText(this, "Data Kamar Diupdate", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "Gagal Update", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadCurrentData() {
        Kamar k = dbHelper.getKamar(kamarId);
        if(k != null) {
            actvJenis.setText(k.getJenisUnit());
            etKeterangan.setText(k.getKeterangan());
            actvMaks.setText(k.getMaksPenyewa() + " Orang");

            String fullNomor = k.getNomorUnit(); // Contoh: "A01" atau "B-10"
            if (fullNomor != null) {
                // Ambil Awalan: Hapus semua angka (0-9) dari string (Contoh: "A01" -> "A")
                String awalan = fullNomor.replaceAll("[0-9]", "");
                etAwalan.setText(awalan);

                // Ambil Nomor: Hapus semua yang BUKAN angka (Contoh: "A01" -> "01")
                String nomor = fullNomor.replaceAll("[^0-9]", "");
                etNomor.setText(nomor);
            }

            // Format harga hapus desimal .0 jika ada (Contoh: 150000.0 -> 150000)
            etHarga1.setText(String.format(Locale.US, "%.0f", k.getHarga1Bulan()));
            etHarga3.setText(String.format(Locale.US, "%.0f", k.getHarga3Bulan()));
            etHarga6.setText(String.format(Locale.US, "%.0f", k.getHarga6Bulan()));
        }
    }
}