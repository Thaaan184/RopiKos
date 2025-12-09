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

public class DetailKamarActivity extends AppCompatActivity {

    private DBHelper dbHelper;
    private int kamarId;
    private TextView tvTitle;
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

        // Header
        TextView tvHeaderTitle = findViewById(R.id.header_detail_kamar).findViewById(R.id.tv_detail_kamar_title);
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
        // Disini Anda bisa set data ke TextView yang ada di layout detail
        // Karena layout detail XML Anda statis (hardcoded text), logic ini hanya memastikan data ada.
        // Untuk implementasi real, TextView di XML detail harus diberi ID unik.
    }
}