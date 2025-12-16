package com.example.ropikos;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.ropikos.db.DBHelper;
import com.example.ropikos.model.Keuangan;
import com.google.android.material.textfield.TextInputEditText;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class EditKeuanganActivity extends AppCompatActivity {

    private DBHelper dbHelper;
    private int keuanganId;
    private Keuangan currentKeuangan;

    private TextInputEditText etNominal, etDeskripsi, etTanggal;
    private AutoCompleteTextView etTipe;
    private Button btnSimpan;
    private ImageView btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_keuangan); // Pastikan layout ini dibuat

        dbHelper = new DBHelper(this);
        keuanganId = getIntent().getIntExtra("KEUANGAN_ID", -1);

        etNominal = findViewById(R.id.et_nominal);
        etDeskripsi = findViewById(R.id.et_deskripsi);
        etTanggal = findViewById(R.id.et_tanggal);
        etTipe = findViewById(R.id.et_tipe);
        btnSimpan = findViewById(R.id.btn_simpan);
        btnBack = findViewById(R.id.btn_back);

        btnBack.setOnClickListener(v -> finish());

        // Setup Picker Tanggal
        etTanggal.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            new DatePickerDialog(this, (view, year, month, day) -> {
                String date = String.format(Locale.US, "%02d-%02d-%d", day, month + 1, year);
                etTanggal.setText(date);
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
        });

        // Setup Dropdown Tipe
        String[] tipes = {"Pemasukan", "Pengeluaran"};
        etTipe.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, tipes));

        loadData();

        btnSimpan.setOnClickListener(v -> simpanPerubahan());
    }

    private void loadData() {
        currentKeuangan = dbHelper.getKeuangan(keuanganId);
        if (currentKeuangan != null) {
            etNominal.setText(String.format(Locale.US, "%.0f", currentKeuangan.getNominal()));
            etDeskripsi.setText(currentKeuangan.getDeskripsi());
            etTanggal.setText(currentKeuangan.getTanggal());
            etTipe.setText(currentKeuangan.getTipe(), false);
        } else {
            Toast.makeText(this, "Data tidak ditemukan", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void simpanPerubahan() {
        if(currentKeuangan == null) return;

        try {
            double nominal = Double.parseDouble(etNominal.getText().toString());
            String deskripsi = etDeskripsi.getText().toString();
            String tanggal = etTanggal.getText().toString();
            String tipe = etTipe.getText().toString();

            currentKeuangan.setNominal(nominal);
            currentKeuangan.setDeskripsi(deskripsi);
            currentKeuangan.setTanggal(tanggal);
            currentKeuangan.setTipe(tipe);

            int result = dbHelper.updateKeuangan(currentKeuangan);
            if(result > 0) {
                Toast.makeText(this, "Data Berhasil Diubah", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Gagal Mengubah Data", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error input data", Toast.LENGTH_SHORT).show();
        }
    }
}