package com.example.ropikos;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.ropikos.db.DBHelper;
import com.example.ropikos.model.Kamar;
import com.example.ropikos.model.Keuangan;
import com.example.ropikos.model.Penyewa;
import com.google.android.material.textfield.TextInputEditText;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class PerpanjangSewaActivity extends AppCompatActivity {

    private DBHelper dbHelper;
    private int penyewaId;
    private Penyewa currentPenyewa;
    private Kamar currentKamar;

    // UI sesuai XML baru
    private AutoCompleteTextView etJenisUnit, etNomorUnit, etDurasi;
    private TextInputEditText etJatuhTempo;
    private TextView tvUnitSummary, tvHargaSummary, tvTotalHarga;
    private Button btnSimpan;

    private double hargaFinal = 0;
    private String tanggalBaru = "";
    private int durasiBulan = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perpanjang_sewa);

        dbHelper = new DBHelper(this);
        penyewaId = getIntent().getIntExtra("PENYEWA_ID", -1);

        currentPenyewa = dbHelper.getPenyewa(penyewaId);
        currentKamar = dbHelper.getKamar(currentPenyewa.getIdKamar());

        initViews();
        setupLogic();

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
        btnSimpan.setOnClickListener(v -> simpanPerpanjangan());
    }

    private void initViews() {
        etJenisUnit = findViewById(R.id.et_jenis_unit);
        etNomorUnit = findViewById(R.id.et_nomor_unit);
        etDurasi = findViewById(R.id.et_durasi_sewa);
        etJatuhTempo = findViewById(R.id.et_tgl_pembayaran_berikutnya);

        // Ringkasan Views
        tvUnitSummary = findViewById(R.id.tv_unit_sewa_summary);
        tvHargaSummary = findViewById(R.id.tv_harga_sewa_summary);
        tvTotalHarga = findViewById(R.id.tv_total_harga);

        btnSimpan = findViewById(R.id.btn_simpan);

        if(currentKamar != null) {
            etJenisUnit.setText(currentKamar.getJenisUnit());
            etNomorUnit.setText(currentKamar.getNomorUnit());
        }
    }

    private void setupLogic() {
        String[] options = {"1 Bulan", "3 Bulan", "6 Bulan"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, options);
        etDurasi.setAdapter(adapter);

        // Default 1 Bulan
        etDurasi.setText("1 Bulan", false);
        calculate();

        etDurasi.setOnItemClickListener((p, v, pos, id) -> calculate());
    }

    private void calculate() {
        durasiBulan = 1;
        String s = etDurasi.getText().toString();
        if(s.contains("3")) durasiBulan = 3;
        else if(s.contains("6")) durasiBulan = 6;

        // 1. Hitung Harga berdasarkan Durasi
        if(currentKamar != null) {
            if(durasiBulan == 3) hargaFinal = currentKamar.getHarga3Bulan();
            else if(durasiBulan == 6) hargaFinal = currentKamar.getHarga6Bulan();
            else hargaFinal = currentKamar.getHarga1Bulan();
        }

        // 2. Format Rupiah
        String hargaStr = String.format(Locale.US, "Rp %,.0f", hargaFinal);
        tvHargaSummary.setText(hargaStr);
        tvTotalHarga.setText(hargaStr);

        // 3. Hitung Tanggal Baru (Berdasarkan Tgl Pembayaran Terakhir)
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
            // Parse tanggal terakhir bayar, jika error pakai tanggal hari ini
            Date oldDate;
            try {
                oldDate = sdf.parse(currentPenyewa.getTglPembayaranBerikutnya());
            } catch (Exception e) {
                oldDate = new Date();
            }

            Calendar cal = Calendar.getInstance();
            cal.setTime(oldDate);
            cal.add(Calendar.MONTH, durasiBulan);

            tanggalBaru = sdf.format(cal.getTime());
            etJatuhTempo.setText(tanggalBaru);

            // Update Text Summary
            tvUnitSummary.setText(String.format("%s %s, %d bulan\n(s.d. %s)",
                    currentKamar.getJenisUnit(), currentKamar.getNomorUnit(), durasiBulan, tanggalBaru));

        } catch (Exception e) {
            etJatuhTempo.setText("-");
        }
    }

    private void simpanPerpanjangan() {
        // 1. Update Penyewa (Tgl Berikutnya)
        currentPenyewa.setTglPembayaranBerikutnya(tanggalBaru);
        dbHelper.updatePenyewa(currentPenyewa);

        // 2. Masukkan Keuangan (Pasti Pemasukan)
        Keuangan k = new Keuangan();
        k.setIdPenyewa(penyewaId);
        k.setTipe("Pemasukan");
        k.setNominal(hargaFinal);
        k.setDeskripsi("Perpanjang Sewa " + currentKamar.getNomorUnit() + " (" + durasiBulan + " Bulan)");
        k.setTanggal(new SimpleDateFormat("dd-MM-yyyy", Locale.US).format(new Date()));

        dbHelper.insertKeuangan(k);

        Toast.makeText(this, "Sewa Berhasil Diperpanjang!", Toast.LENGTH_SHORT).show();
        finish();
    }
}