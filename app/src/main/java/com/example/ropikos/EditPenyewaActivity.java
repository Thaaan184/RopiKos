package com.example.ropikos;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.ropikos.db.DBHelper;
import com.example.ropikos.model.Penyewa;

public class EditPenyewaActivity extends AppCompatActivity {

    private DBHelper dbHelper;
    private int penyewaId;
    private Button btnSimpan;
    private ImageView btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_penyewa);

        dbHelper = new DBHelper(this);
        penyewaId = getIntent().getIntExtra("PENYEWA_ID", -1);

        btnSimpan = findViewById(R.id.btn_simpan);
        btnBack = findViewById(R.id.btn_back);

        if(btnBack != null) btnBack.setOnClickListener(v -> finish());

        btnSimpan.setOnClickListener(v -> {
            Penyewa p = new Penyewa();
            p.setId(penyewaId);
            p.setNama("Nama Update"); // Ambil dari EditText
            // ... set data lain

            int res = dbHelper.updatePenyewa(p);
            if (res > 0) {
                Toast.makeText(this, "Data Penyewa Diupdate", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}