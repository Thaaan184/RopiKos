package com.example.ropikos;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;

public class EditKamarActivity extends AppCompatActivity {

    private TextInputEditText etNomor, etHarga;
    private Button btnUpdate;
    private DatabaseHelper dbHelper;
    private int kamarId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_kamar);

        dbHelper = new DatabaseHelper(this);
        kamarId = getIntent().getIntExtra("KAMAR_ID", -1);

        etNomor = findViewById(R.id.et_nomor_unit);
        etHarga = findViewById(R.id.et_harga_1_bulan);
        btnUpdate = findViewById(R.id.btn_update); // Sesuai ID di XML activity_edit_kamar

        loadDataAwal();

        btnUpdate.setOnClickListener(v -> updateKamar());
    }

    private void loadDataAwal() {
        Cursor cursor = dbHelper.getKamarDetail(kamarId);
        if (cursor.moveToFirst()) {
            String nomor = cursor.getString(cursor.getColumnIndexOrThrow("nomor_kamar"));
            double harga = cursor.getDouble(cursor.getColumnIndexOrThrow("harga"));

            etNomor.setText(nomor);
            etHarga.setText(String.valueOf((int)harga));
        }
        cursor.close();
    }

    private void updateKamar() {
        String nomor = etNomor.getText().toString();
        String hargaStr = etHarga.getText().toString();

        if (dbHelper.updateKamar(kamarId, "Kamar Updated", nomor, Double.parseDouble(hargaStr))) {
            Toast.makeText(this, "Data berhasil diupdate", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Gagal update", Toast.LENGTH_SHORT).show();
        }
    }
}