package com.example.ropikos;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.ropikos.db.DBHelper;
import com.example.ropikos.model.Kamar;
import com.example.ropikos.model.Keuangan;
import com.example.ropikos.model.Penyewa;
import com.google.android.material.textfield.TextInputEditText;
import java.text.SimpleDateFormat;
import java.util.*;

public class PindahKamarActivity extends AppCompatActivity {

    private DBHelper dbHelper;
    private int penyewaId;
    private Penyewa currentPenyewa;
    private Kamar oldKamar;
    private Kamar newKamar;

    private AutoCompleteTextView etJenis, etNomor;
    private CheckBox cbBiaya;
    private LinearLayout layoutBiayaContainer;
    private TextInputEditText etBiaya, etCatatan;
    private RadioButton rbPemasukan, rbPengeluaran;

    // Ringkasan Views
    private TextView tvDetailPindah, tvNominalBiaya;
    private Button btnSimpan;

    private List<Kamar> availableList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pindah_kamar);

        dbHelper = new DBHelper(this);
        penyewaId = getIntent().getIntExtra("PENYEWA_ID", -1);

        currentPenyewa = dbHelper.getPenyewa(penyewaId);
        oldKamar = dbHelper.getKamar(currentPenyewa.getIdKamar());

        initViews();
        setupDropdowns();
        setupListeners();

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
        btnSimpan.setOnClickListener(v -> prosesPindah());
    }

    private void initViews() {
        etJenis = findViewById(R.id.et_jenis_unit);
        etNomor = findViewById(R.id.et_nomor_unit);
        cbBiaya = findViewById(R.id.cb_ada_biaya);
        layoutBiayaContainer = findViewById(R.id.layout_biaya_container);
        etBiaya = findViewById(R.id.et_biaya);
        etCatatan = findViewById(R.id.et_catatan);
        rbPemasukan = findViewById(R.id.rb_pemasukan);
        rbPengeluaran = findViewById(R.id.rb_pengeluaran);

        tvDetailPindah = findViewById(R.id.tv_detail_pindah);
        tvNominalBiaya = findViewById(R.id.tv_nominal_biaya);

        btnSimpan = findViewById(R.id.btn_simpan);

        updateSummary();
    }

    private void setupListeners() {
        cbBiaya.setOnCheckedChangeListener((btn, isChecked) -> {
            layoutBiayaContainer.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            updateSummary();
        });

        // Listener agar Ringkasan Update saat ngetik biaya
        etBiaya.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { updateSummary(); }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void setupDropdowns() {
        availableList = dbHelper.getKamarTersedia();
        Set<String> jenisSet = new HashSet<>();
        for (Kamar k : availableList) jenisSet.add(k.getJenisUnit());

        ArrayAdapter<String> adapterJenis = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, new ArrayList<>(jenisSet));
        etJenis.setAdapter(adapterJenis);

        etJenis.setOnItemClickListener((parent, view, position, id) -> {
            String jenis = adapterJenis.getItem(position);
            List<String> nomorList = new ArrayList<>();
            final List<Kamar> filtered = new ArrayList<>();
            for (Kamar k : availableList) {
                if(k.getJenisUnit().equals(jenis)) {
                    nomorList.add(k.getNomorUnit());
                    filtered.add(k);
                }
            }
            ArrayAdapter<String> adapterNomor = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, nomorList);
            etNomor.setAdapter(adapterNomor);
            etNomor.setText("");
            newKamar = null;
            updateSummary();

            etNomor.setOnItemClickListener((p, v, pos, i) -> {
                newKamar = filtered.get(pos);
                updateSummary();
            });
        });
    }

    private void updateSummary() {
        String oldKamarStr = (oldKamar != null) ? oldKamar.getNomorUnit() : "-";
        String newKamarStr = (newKamar != null) ? newKamar.getNomorUnit() : "(Belum pilih)";

        tvDetailPindah.setText("Pindah: " + oldKamarStr + " -> " + newKamarStr);

        if (cbBiaya.isChecked()) {
            String biayaStr = etBiaya.getText().toString();
            if(!biayaStr.isEmpty()) {
                double val = Double.parseDouble(biayaStr);
                tvNominalBiaya.setText(String.format(Locale.US, "Rp %,.0f", val));
            } else {
                tvNominalBiaya.setText("Rp 0");
            }
        } else {
            tvNominalBiaya.setText("Rp 0");
        }
    }

    private void prosesPindah() {
        if (newKamar == null) {
            Toast.makeText(this, "Pilih kamar baru!", Toast.LENGTH_SHORT).show();
            return;
        }

        // 1. Update Kamar Lama (Kosongkan)
        if (oldKamar != null) {
            oldKamar.setStatus(0);
            dbHelper.updateKamar(oldKamar);
        }

        // 2. Update Kamar Baru (Isi)
        newKamar.setStatus(1);
        dbHelper.updateKamar(newKamar);

        // 3. Update Penyewa
        currentPenyewa.setIdKamar(newKamar.getId());
        dbHelper.updatePenyewa(currentPenyewa);

        // 4. Catat Keuangan (Jika ada biaya)
        if (cbBiaya.isChecked()) {
            try {
                String nominalStr = etBiaya.getText().toString();
                if (!nominalStr.isEmpty()) {
                    double nominal = Double.parseDouble(nominalStr);
                    String cat = etCatatan.getText().toString();
                    String tipe = rbPemasukan.isChecked() ? "Pemasukan" : "Pengeluaran";

                    Keuangan k = new Keuangan();
                    k.setIdPenyewa(penyewaId);
                    k.setTipe(tipe);
                    k.setNominal(nominal);
                    k.setDeskripsi("Pindah Kamar (" + oldKamar.getNomorUnit() + "->" + newKamar.getNomorUnit() + "): " + cat);
                    k.setTanggal(new SimpleDateFormat("dd-MM-yyyy", Locale.US).format(new Date()));

                    dbHelper.insertKeuangan(k);
                }
            } catch (Exception e) {}
        }

        Toast.makeText(this, "Berhasil Pindah Kamar!", Toast.LENGTH_SHORT).show();
        finish();
    }
}