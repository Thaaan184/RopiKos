package com.example.ropikos;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.ropikos.db.DBHelper;
import com.example.ropikos.model.Penyewa;

public class DetailPenyewaActivity extends AppCompatActivity {

    private DBHelper dbHelper;
    private int penyewaId;
    private TextView tvNama;
    private ImageButton btnEdit;
    private Button btnLainnya;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_penyewa);

        dbHelper = new DBHelper(this);
        penyewaId = getIntent().getIntExtra("PENYEWA_ID", -1);

        tvNama = findViewById(R.id.tv_nama_penyewa);
        btnEdit = findViewById(R.id.btn_edit);
        btnLainnya = findViewById(R.id.btn_lainnya);

        loadData();

        // Use Case 7
        btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(this, EditPenyewaActivity.class);
            intent.putExtra("PENYEWA_ID", penyewaId);
            startActivity(intent);
        });

        // Use Case 8: Hapus lewat menu popup
        btnLainnya.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(DetailPenyewaActivity.this, btnLainnya);
            popup.getMenu().add(0, 1, 0, "Hapus Penyewa");
            popup.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == 1) {
                    confirmDelete();
                    return true;
                }
                return false;
            });
            popup.show();
        });
    }

    private void confirmDelete() {
        new AlertDialog.Builder(this)
                .setTitle("Hapus Penyewa")
                .setMessage("Data penyewa akan dihapus permanen. Lanjutkan?")
                .setPositiveButton("Ya", (dialog, which) -> {
                    dbHelper.deletePenyewa(penyewaId);
                    Toast.makeText(this, "Data dihapus", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .setNegativeButton("Batal", null)
                .show();
    }

    private void loadData() {
        Penyewa p = dbHelper.getPenyewa(penyewaId);
        if (p != null) {
            tvNama.setText(p.getNama());
        }
    }
}