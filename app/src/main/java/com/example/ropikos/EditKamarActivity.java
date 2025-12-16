package com.example.ropikos;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.ropikos.db.DBHelper;
import com.example.ropikos.model.Kamar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Locale;

public class EditKamarActivity extends AppCompatActivity {

    private DBHelper dbHelper;
    private int kamarId;
    private AutoCompleteTextView actvJenis, actvMaks;
    private TextInputEditText etAwalan, etNomor, etKeterangan, etHarga1, etHarga3, etHarga6;
    private TextInputLayout tilAwalan, tilNomor, tilHarga1, tilHarga3, tilHarga6, tilKeterangan;
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
        actvMaks = findViewById(R.id.actv_maksimal_penghuni);
        etAwalan = findViewById(R.id.et_awalan_unit);
        etNomor = findViewById(R.id.et_nomor_unit);
        etKeterangan = findViewById(R.id.et_keterangan);
        etHarga1 = findViewById(R.id.et_harga_1_bulan);
        etHarga3 = findViewById(R.id.et_harga_3_bulan);
        etHarga6 = findViewById(R.id.et_harga_6_bulan);

        tilKeterangan = findViewById(R.id.til_keterangan);
        tilAwalan = findViewById(R.id.til_awalan_unit);
        tilNomor = findViewById(R.id.til_nomor_unit);
        tilHarga1 = findViewById(R.id.til_harga_1_bulan);
        tilHarga3 = findViewById(R.id.til_harga_3_bulan);
        tilHarga6 = findViewById(R.id.til_harga_6_bulan);

        if (btnBack != null) btnBack.setOnClickListener(v -> finish());

        setupDropdowns();

        // Load Data Lama
        loadCurrentData();

        btnUpdate.setOnClickListener(v -> updateKamar());
    }

    private void setupDropdowns() {
        // Setup Dropdown Maksimal Penghuni
        String[] maksOptions = {"1 Orang", "2 Orang"};
        ArrayAdapter<String> adapterMaks = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, maksOptions);
        actvMaks.setAdapter(adapterMaks);

        // Setup Dropdown Jenis Unit
        String[] jenisOptions = {"Kamar AC", "Kamar Non AC"};
        ArrayAdapter<String> adapterJenis = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, jenisOptions);
        actvJenis.setAdapter(adapterJenis);

        // Listener saat Owner memilih Jenis Unit (Jika ingin ganti tipe kamar)
        actvJenis.setOnItemClickListener((parent, view, position, id) -> {
            String selected = adapterJenis.getItem(position);

            // Set Harga Otomatis jika user mengubah jenis
            if ("Kamar AC".equals(selected)) {
                autoFillHarga(1500000, 4200000, 8000000);
                etKeterangan.setText("Fasilitas: AC, Kasur, Lemari, Kamar Mandi Dalam");
                actvMaks.setText("1 Orang", false); // Default 1 orang
            } else if ("Kamar Non AC".equals(selected)) {
                autoFillHarga(800000, 2200000, 4000000);
                etKeterangan.setText("Fasilitas: Kipas Angin, Kasur, Lemari, Kamar Mandi Luar");
                actvMaks.setText("1 Orang", false);
            }
        });
    }

    private void autoFillHarga(double h1, double h3, double h6) {
        etHarga1.setText(String.format(Locale.US, "%.0f", h1));
        etHarga3.setText(String.format(Locale.US, "%.0f", h3));
        etHarga6.setText(String.format(Locale.US, "%.0f", h6));
    }

    private void loadCurrentData() {
        Kamar k = dbHelper.getKamar(kamarId);
        if(k != null) {
            // Gunakan 'false' agar dropdown tidak muncul otomatis saat set text
            actvJenis.setText(k.getJenisUnit(), false);
            etKeterangan.setText(k.getKeterangan());
            actvMaks.setText(k.getMaksPenyewa() + " Orang", false);

            String fullNomor = k.getNomorUnit();
            if (fullNomor != null) {
                String awalan = fullNomor.replaceAll("[0-9]", "");
                etAwalan.setText(awalan);
                String nomor = fullNomor.replaceAll("[^0-9]", "");
                etNomor.setText(nomor);
            }

            etHarga1.setText(String.format(Locale.US, "%.0f", k.getHarga1Bulan()));
            etHarga3.setText(String.format(Locale.US, "%.0f", k.getHarga3Bulan()));
            etHarga6.setText(String.format(Locale.US, "%.0f", k.getHarga6Bulan()));
        }
    }

    private boolean validateInputs() {
        boolean isValid = true;

        // Validasi Awalan
        if (etAwalan.getText().toString().trim().isEmpty()) {
            tilAwalan.setError("Wajib diisi");
            isValid = false;
        } else {
            tilAwalan.setError(null);
        }

        // Validasi Nomor
        if (etNomor.getText().toString().trim().isEmpty()) {
            tilNomor.setError("Wajib diisi");
            isValid = false;
        } else {
            tilNomor.setError(null);
        }

        // Validasi Keterangan
        if (etKeterangan.getText().toString().trim().isEmpty()) {
            tilKeterangan.setError("Wajib diisi");
            isValid = false;
        } else {
            tilKeterangan.setError(null);
        }

        // Validasi Harga
        if (etHarga1.getText().toString().trim().isEmpty()) {
            tilHarga1.setError("Harga tidak boleh kosong");
            isValid = false;
        } else {
            tilHarga1.setError(null);
        }

        if (etHarga3.getText().toString().trim().isEmpty()) {
            tilHarga3.setError("Harga tidak boleh kosong");
            isValid = false;
        } else {
            tilHarga3.setError(null);
        }

        if (etHarga6.getText().toString().trim().isEmpty()) {
            tilHarga6.setError("Harga tidak boleh kosong");
            isValid = false;
        } else {
            tilHarga6.setError(null);
        }

        return isValid;
    }

    private void updateKamar() {
        // 1. Validasi Input
        if (!validateInputs()) {
            Toast.makeText(this, "Mohon lengkapi data yang merah!", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // 2. Ambil Data Lama untuk Status
            Kamar dataLama = dbHelper.getKamar(kamarId);
            int statusSaatIni = (dataLama != null) ? dataLama.getStatus() : 0;

            Kamar k = new Kamar();
            k.setId(kamarId);
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

            double h1 = Double.parseDouble(h1Str);
            double h3 = Double.parseDouble(h3Str);
            double h6 = Double.parseDouble(h6Str);

            k.setHarga1Bulan(h1);
            k.setHarga3Bulan(h3);
            k.setHarga6Bulan(h6);

            // 3. Set Status Lama (Penting!)
            k.setStatus(statusSaatIni);

            int result = dbHelper.updateKamar(k);

            if (result > 0) {
                Toast.makeText(this, "Data Kamar Diupdate", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Gagal Mengupdate Kamar", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error input: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}