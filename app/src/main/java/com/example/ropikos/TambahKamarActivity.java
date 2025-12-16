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

public class TambahKamarActivity extends AppCompatActivity {

    private DBHelper dbHelper;
    private AutoCompleteTextView actvJenis, actvMaks;
    private TextInputEditText etAwalan, etNomor, etKeterangan, etHarga1, etHarga3, etHarga6;
    private Button btnSimpan;
    private ImageView btnBack;
    private TextInputLayout tilAwalan, tilNomor, tilHarga1, tilHarga3, tilHarga6, tilKeterangan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_kamar);

        dbHelper = new DBHelper(this);

        actvJenis = findViewById(R.id.actv_jenis_unit);
        actvMaks = findViewById(R.id.actv_maksimal_penghuni);
        etAwalan = findViewById(R.id.et_awalan_unit);
        etNomor = findViewById(R.id.et_nomor_unit);
        etKeterangan = findViewById(R.id.et_keterangan);
        etHarga1 = findViewById(R.id.et_harga_1_bulan);
        etHarga3 = findViewById(R.id.et_harga_3_bulan);
        etHarga6 = findViewById(R.id.et_harga_6_bulan);
        btnSimpan = findViewById(R.id.btn_simpan);
        btnBack = findViewById(R.id.btn_back);

        tilKeterangan = findViewById(R.id.til_keterangan);
        tilAwalan = findViewById(R.id.til_awalan_unit);
        tilNomor = findViewById(R.id.til_nomor_unit);
        tilHarga1 = findViewById(R.id.til_harga_1_bulan);
        tilHarga3 = findViewById(R.id.til_harga_3_bulan);
        tilHarga6 = findViewById(R.id.til_harga_6_bulan);


        if(btnBack != null) btnBack.setOnClickListener(v -> finish());

        // Matikan semua input form di awal (Kecuali Jenis Unit)
        setFormEnabled(false);

        // Setup Dropdown Jenis Unit & Maksimal Penghuni
        setupDropdowns();

        // Logic Simpan
        btnSimpan.setOnClickListener(v -> simpanKamar());
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

        // Listener saat Owner memilih Jenis Unit
        actvJenis.setOnItemClickListener((parent, view, position, id) -> {
            String selected = adapterJenis.getItem(position);

            // Aktifkan form lain setelah memilih
            setFormEnabled(true);

            // Set Harga Otomatis & Default Data berdasarkan pilihan
            if ("Kamar AC".equals(selected)) {
                autoFillHarga(1500000, 4200000, 8000000);
                etKeterangan.setText("Fasilitas: AC, Kasur, Lemari, Kamar Mandi Dalam");
                actvMaks.setText("1 Orang", false); // Default 1 orang
            } else if ("Kamar Non AC".equals(selected)) {
                // Contoh harga Non AC (Sesuaikan dengan kebutuhan)
                autoFillHarga(800000, 2200000, 4000000);
                etKeterangan.setText("Fasilitas: Kipas Angin, Kasur, Lemari, Kamar Mandi Luar");
                actvMaks.setText("1 Orang", false);
            }
        });
    }

    // Method Helper untuk mengisi harga otomatis (Mengubah double ke String tanpa desimal)
    private void autoFillHarga(double h1, double h3, double h6) {
        etHarga1.setText(String.format(Locale.US, "%.0f", h1));
        etHarga3.setText(String.format(Locale.US, "%.0f", h3));
        etHarga6.setText(String.format(Locale.US, "%.0f", h6));
    }

    // Method Helper untuk Mengunci / Membuka Form
    private void setFormEnabled(boolean enabled) {
        etAwalan.setEnabled(enabled);
        etNomor.setEnabled(enabled);
        etKeterangan.setEnabled(enabled);
        actvMaks.setEnabled(enabled);
        etHarga1.setEnabled(enabled);
        etHarga3.setEnabled(enabled);
        etHarga6.setEnabled(enabled);
        btnSimpan.setEnabled(enabled); // Tombol simpan juga dimatikan jika belum pilih jenis
    }

    private boolean validateInputs() {
        boolean isValid = true;

        // Validasi Jenis Unit (Dropdown)
        if (actvJenis.getText().toString().trim().isEmpty()) {
            actvJenis.setError("Wajib dipilih"); // Khusus dropdown, error muncul di teksnya
            isValid = false;
        } else {
            actvJenis.setError(null);
        }

        // Validasi Awalan
        if (etAwalan.getText().toString().trim().isEmpty()) {
            tilAwalan.setError("Wajib diisi"); // Error muncul di bawah kotak
            isValid = false;
        } else {
            tilAwalan.setError(null); // Hapus error
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

    private void simpanKamar() {
        if (!validateInputs()) {
            Toast.makeText(this, "Silakan lengkapi datanya terlebih dahulu!", Toast.LENGTH_SHORT).show();
            return; // Berhenti, jangan simpan ke database
        }

            String nomorFull = etAwalan.getText().toString() + etNomor.getText().toString();
            String ket = etKeterangan.getText().toString();
            String jenis = actvJenis.getText().toString();

            String h1Str = etHarga1.getText().toString();
            String h3Str = etHarga3.getText().toString();
            String h6Str = etHarga6.getText().toString();

            double h1 = h1Str.isEmpty() ? 0 : Double.parseDouble(h1Str);
            double h3 = h3Str.isEmpty() ? 0 : Double.parseDouble(h3Str);
            double h6 = h6Str.isEmpty() ? 0 : Double.parseDouble(h6Str);

            String maksStr = actvMaks.getText().toString().replaceAll("[^0-9]", "");
            int maks = maksStr.isEmpty() ? 1 : Integer.parseInt(maksStr);

            Kamar kamarBaru = new Kamar(jenis, nomorFull, ket, maks, h1, h3, h6, 0);
            long res = dbHelper.insertKamar(kamarBaru);

            if (res > 0) {
                Toast.makeText(this, "Kamar Berhasil Ditambah", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Gagal menambah kamar", Toast.LENGTH_SHORT).show();
            }
    }
}