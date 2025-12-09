package com.example.ropikos;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.ropikos.db.DBHelper;
import com.example.ropikos.model.Kamar;
import com.google.android.material.textfield.TextInputEditText;

public class EditKamarActivity extends AppCompatActivity {

    private DBHelper dbHelper;
    private int kamarId;
    private TextInputEditText etNomor, etHarga1;
    private Button btnUpdate;
    private ImageView btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_kamar);

        dbHelper = new DBHelper(this);
        kamarId = getIntent().getIntExtra("KAMAR_ID", -1);

        btnUpdate = findViewById(R.id.btn_update);
        etNomor = findViewById(R.id.et_nomor_unit);
        etHarga1 = findViewById(R.id.et_harga_1_bulan);
        btnBack = findViewById(R.id.btn_back);

        if(btnBack != null) btnBack.setOnClickListener(v -> finish());

        loadCurrentData();

        btnUpdate.setOnClickListener(v -> {
            Kamar k = new Kamar();
            k.setId(kamarId);
            // Ambil data dari inputan (logic sama seperti tambah)
            // Untuk contoh ini saya update nomor saja
            k.setNomorUnit(etNomor.getText().toString());
            k.setHarga1Bulan(Double.parseDouble(etHarga1.getText().toString()));
            // ... set field lainnya juga

            int result = dbHelper.updateKamar(k);
            if (result > 0) {
                Toast.makeText(this, "Data Kamar Diupdate", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Gagal Update", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadCurrentData() {
        Kamar k = dbHelper.getKamar(kamarId);
        if(k != null) {
            etNomor.setText(k.getNomorUnit());
            etHarga1.setText(String.valueOf(k.getHarga1Bulan()));
        }
    }
}