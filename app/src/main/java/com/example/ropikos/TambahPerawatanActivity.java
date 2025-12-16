package com.example.ropikos;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ropikos.db.DBHelper;
import com.example.ropikos.model.Kamar;
import com.example.ropikos.model.Keuangan;
import com.example.ropikos.model.Perawatan;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TambahPerawatanActivity extends AppCompatActivity {

    private DBHelper dbHelper;
    private int kamarId;
    private Kamar currentKamar;

    // UI Components
    private TextInputEditText etNama, etTanggal, etBiayaPokok, etNamaBiayaLain, etHargaLain, etCatatan;
    private TextView tvPerawatanSummary, tvTotalBiaya, tvTotalRingkasan;
    private Button btnSimpan;
    private ImageView btnBack;

    private double totalBiaya = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_perawatan);

        dbHelper = new DBHelper(this);
        kamarId = getIntent().getIntExtra("KAMAR_ID", -1);
        currentKamar = dbHelper.getKamar(kamarId);

        initViews();
        setupListeners();

        // --- LOGIC TANGGAL DEFAULT ---
        // Set otomatis ke hari ini saat dibuka
        String today = new SimpleDateFormat("dd-MM-yyyy", Locale.US).format(new Date());
        etTanggal.setText(today);

        calculateTotal(); // Hitung awal (akan Rp 0 karena input kosong)
    }

    private void initViews() {
        etNama = findViewById(R.id.et_nama_perawatan);
        etTanggal = findViewById(R.id.et_tanggal);
        etBiayaPokok = findViewById(R.id.et_biaya_pokok);
        etNamaBiayaLain = findViewById(R.id.et_biaya_lain);
        etHargaLain = findViewById(R.id.et_harga_lain);
        etCatatan = findViewById(R.id.et_catatan);

        tvPerawatanSummary = findViewById(R.id.tv_perawatan_summary);
        tvTotalBiaya = findViewById(R.id.tv_total_biaya);
        tvTotalRingkasan = findViewById(R.id.tv_total_ringkasan);

        btnSimpan = findViewById(R.id.btn_simpan);
        btnBack = findViewById(R.id.btn_back);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        // Date Picker (Hanya lewat klik karena focusable=false di XML)
        etTanggal.setOnClickListener(v -> showDatePicker());

        // Real-time calculation listeners
        TextWatcher calculatorWatcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { calculateTotal(); }
            @Override public void afterTextChanged(Editable s) {}
        };

        etBiayaPokok.addTextChangedListener(calculatorWatcher);
        etHargaLain.addTextChangedListener(calculatorWatcher);
        etNama.addTextChangedListener(calculatorWatcher);
        etNamaBiayaLain.addTextChangedListener(calculatorWatcher);

        btnSimpan.setOnClickListener(v -> simpanData());
    }

    private void showDatePicker() {
        Calendar c = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, day) -> {
            String date = String.format(Locale.US, "%02d-%02d-%d", day, month + 1, year);
            etTanggal.setText(date);
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void calculateTotal() {
        // Gunakan fungsi helper parseDouble agar tidak crash jika kosong
        double biayaPokok = parseDouble(etBiayaPokok.getText().toString());
        double biayaLain = parseDouble(etHargaLain.getText().toString());

        totalBiaya = biayaPokok + biayaLain;
        String totalStr = String.format(Locale.US, "Rp %,.0f", totalBiaya);

        // Update UI Ringkasan
        String namaKegiatan = etNama.getText().toString();
        String namaLain = etNamaBiayaLain.getText().toString();

        if(namaKegiatan.isEmpty()) namaKegiatan = "-";

        String summaryText = namaKegiatan;
        if(!namaLain.isEmpty()) {
            summaryText += " + " + namaLain;
        }

        tvPerawatanSummary.setText(summaryText);
        tvTotalBiaya.setText(totalStr);
        tvTotalRingkasan.setText(totalStr);
    }

    private void simpanData() {
        if (etNama.getText().toString().isEmpty()) {
            etNama.setError("Wajib diisi");
            return;
        }

        String nama = etNama.getText().toString();
        String tanggal = etTanggal.getText().toString();
        double biayaPokok = parseDouble(etBiayaPokok.getText().toString());
        double biayaLain = parseDouble(etHargaLain.getText().toString());
        String catatan = etCatatan.getText().toString();

        // 1. Simpan ke Tabel Perawatan
        Perawatan p = new Perawatan();
        p.setNamaPerawatan(nama);
        p.setTanggal(tanggal);
        p.setIdKamar(kamarId);
        p.setBiayaJasa(biayaPokok);
        p.setBiayaSparepart(biayaLain);
        p.setOngkir(0);

        String detailLain = etNamaBiayaLain.getText().toString();
        if(!detailLain.isEmpty()) {
            p.setCatatan(catatan + " (" + detailLain + ")");
        } else {
            p.setCatatan(catatan);
        }

        long resPerawatan = dbHelper.insertPerawatan(p);

        if (resPerawatan > 0) {
            // 2. Simpan ke Tabel Keuangan (PENGELUARAN OTOMATIS)
            Keuangan k = new Keuangan();
            k.setIdPenyewa(0);
            k.setTipe("Pengeluaran");

            String unitInfo = (currentKamar != null) ? currentKamar.getNomorUnit() : "";
            k.setDeskripsi("Perawatan " + unitInfo + ": " + nama);

            k.setNominal(totalBiaya);
            k.setTanggal(tanggal);

            dbHelper.insertKeuangan(k);

            Toast.makeText(this, "Perawatan & Pengeluaran Berhasil Disimpan", Toast.LENGTH_LONG).show();
            finish();
        } else {
            Toast.makeText(this, "Gagal menyimpan data", Toast.LENGTH_SHORT).show();
        }
    }

    // Helper mencegah error NumberFormatException saat field kosong
    private double parseDouble(String s) {
        if (s == null || s.isEmpty()) return 0;
        try {
            return Double.parseDouble(s);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}