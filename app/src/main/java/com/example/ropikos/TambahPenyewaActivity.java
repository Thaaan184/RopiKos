package com.example.ropikos;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.ropikos.db.DBHelper;
import com.example.ropikos.model.Kamar;
import com.example.ropikos.model.Keuangan; // MODIFIKASI: Import Model Keuangan
import com.example.ropikos.model.Penyewa;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class TambahPenyewaActivity extends AppCompatActivity {

    private DBHelper dbHelper;
    private TextInputEditText etNamaPenyewa, etWhatsapp, etDeskripsi, etTglBayar, etTglMulaiSewa;
    private AutoCompleteTextView etJenisKelamin, etJenisUnit, etNoUnit, etDurasiSewa;
    private Button btnSimpan, btnKtp;
    private ImageView btnBack, ivProfilePic, ivKtpPreview;
    private TextView tvUnitSummary, tvHargaSummary, tvTotalHarga;
    private TextInputLayout tilNama, tilWhatsapp, tilJenisUnit, tilNoUnit, tilJenisKelamin;

    private String pathFotoProfil = null;
    private String pathKtp = null;

    private int selectedKamarId = -1;
    private Kamar selectedKamar = null;
    private List<Kamar> availableKamarList;
    private final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("dd-MM-yyyy", Locale.US);

    private final ActivityResultLauncher<Intent> launcherProfile = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri selectedUri = result.getData().getData();
                    if (selectedUri != null) {
                        ivProfilePic.setImageURI(selectedUri);
                        ivProfilePic.setImageTintList(null);
                        pathFotoProfil = saveImageToInternalStorage(selectedUri, "profile_" + System.currentTimeMillis());
                    }
                }
            }
    );

    private final ActivityResultLauncher<Intent> launcherKtp = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri selectedUri = result.getData().getData();
                    if (selectedUri != null) {
                        ivKtpPreview.setImageURI(selectedUri);
                        ivKtpPreview.setImageTintList(null);
                        pathKtp = saveImageToInternalStorage(selectedUri, "ktp_" + System.currentTimeMillis());
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_penyewa);

        dbHelper = new DBHelper(this);

        etNamaPenyewa = findViewById(R.id.et_nama_penyewa);
        etWhatsapp = findViewById(R.id.et_whatsapp);
        etJenisKelamin = findViewById(R.id.et_jenis_kelamin);
        etDeskripsi = findViewById(R.id.et_deskripsi);
        etNoUnit = findViewById(R.id.et_nomor_unit);
        etJenisUnit = findViewById(R.id.et_jenis_unit);
        ivProfilePic = findViewById(R.id.iv_profile_pic);
        ivKtpPreview = findViewById(R.id.iv_ktp_preview);
        etTglMulaiSewa = findViewById(R.id.et_tgl_mulai_sewa);
        etDurasiSewa = findViewById(R.id.et_durasi_sewa);
        etTglBayar = findViewById(R.id.et_tgl_pembayaran_berikutnya);
        tvUnitSummary = findViewById(R.id.tv_unit_sewa_summary);
        tvHargaSummary = findViewById(R.id.tv_harga_sewa_summary);
        tvTotalHarga = findViewById(R.id.tv_total_harga);

        tilNama = findViewById(R.id.til_nama_penyewa);
        tilWhatsapp = findViewById(R.id.til_whatsapp);
        tilJenisUnit = findViewById(R.id.til_jenis_unit);
        tilNoUnit = findViewById(R.id.til_nomor_unit);
        tilJenisKelamin = findViewById(R.id.til_jenis_kelamin);


        btnSimpan = findViewById(R.id.btn_simpan);
        btnBack = findViewById(R.id.btn_back);
        btnKtp = findViewById(R.id.btn_upload_ktp);

        ivProfilePic.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            launcherProfile.launch(intent);
        });

        btnKtp.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            launcherKtp.launch(intent);
        });

        if (btnBack != null) btnBack.setOnClickListener(v -> finish());

        setupDateAndDurationLogic();

        if (getIntent().hasExtra("PRESELECTED_KAMAR_ID")) {
            selectedKamarId = getIntent().getIntExtra("PRESELECTED_KAMAR_ID", -1);
            if (selectedKamarId != -1) {
                fillKamarData(selectedKamarId);
                etJenisUnit.setEnabled(false);
                etNoUnit.setEnabled(false);
                tilJenisUnit.setEnabled(false);
                tilNoUnit.setEnabled(false);
            }
        } else {
            setupDropdownKamarAvailable();
        }

        setupStaticDropdowns();

        btnSimpan.setOnClickListener(v -> simpanPenyewa());
    }

    private void fillKamarData(int kamarId) {
        selectedKamar = dbHelper.getKamar(kamarId);
        if (selectedKamar != null) {
            etJenisUnit.setText(selectedKamar.getJenisUnit());
            etNoUnit.setText(selectedKamar.getNomorUnit());
            updateSummary();
        }
    }

    private void setupDropdownKamarAvailable() {
        availableKamarList = dbHelper.getKamarTersedia();

        Set<String> jenisSet = new HashSet<>();
        for (Kamar k : availableKamarList) {
            jenisSet.add(k.getJenisUnit());
        }
        List<String> listJenis = new ArrayList<>(jenisSet);
        ArrayAdapter<String> adapterJenis = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, listJenis);
        etJenisUnit.setAdapter(adapterJenis);

        etJenisUnit.setOnItemClickListener((parent, view, position, id) -> {
            String selectedJenis = adapterJenis.getItem(position);
            etNoUnit.setText("");
            selectedKamarId = -1;
            selectedKamar = null;
            setupDropdownNomor(selectedJenis);
        });
    }

    private void setupDropdownNomor(String jenisUnit) {
        List<String> listNomor = new ArrayList<>();
        final List<Kamar> filteredKamar = new ArrayList<>();

        for (Kamar k : availableKamarList) {
            if (k.getJenisUnit().equals(jenisUnit)) {
                listNomor.add(k.getNomorUnit());
                filteredKamar.add(k);
            }
        }

        ArrayAdapter<String> adapterNomor = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, listNomor);
        etNoUnit.setAdapter(adapterNomor);

        etNoUnit.setOnItemClickListener((parent, view, position, id) -> {
            selectedKamar = filteredKamar.get(position);
            selectedKamarId = selectedKamar.getId();
            updateSummary();
        });
    }

    private void setupDateAndDurationLogic() {
        etTglMulaiSewa.setOnClickListener(v -> showDatePicker());
        etTglMulaiSewa.setText(DATE_FORMATTER.format(Calendar.getInstance().getTime()));

        String[] durasiOptions = {"1 Bulan", "3 Bulan", "6 Bulan"};
        ArrayAdapter<String> durasiAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, durasiOptions);
        etDurasiSewa.setAdapter(durasiAdapter);
        etDurasiSewa.setText("1 Bulan", false);

        etDurasiSewa.setOnItemClickListener((parent, view, position, id) -> updateSummary());

        calculateEndDate();
    }

    private void showDatePicker() {
        Calendar kalender = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, monthOfYear, dayOfMonth) -> {
                    kalender.set(year, monthOfYear, dayOfMonth);
                    etTglMulaiSewa.setText(DATE_FORMATTER.format(kalender.getTime()));
                    updateSummary();
                },
                kalender.get(Calendar.YEAR),
                kalender.get(Calendar.MONTH),
                kalender.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis()); // agar tidak bisa pilih tgl sebelumnya
        datePickerDialog.show();
    }

    private void calculateEndDate() {
        String tglMulaiStr = etTglMulaiSewa.getText().toString();
        int durasiBulan = getDurasiInt();

        try {
            Calendar kalender = Calendar.getInstance();
            kalender.setTime(DATE_FORMATTER.parse(tglMulaiStr));
            kalender.add(Calendar.MONTH, durasiBulan);
            kalender.add(Calendar.DAY_OF_MONTH, -1);
            etTglBayar.setText(DATE_FORMATTER.format(kalender.getTime()));
        } catch (Exception e) {
            etTglBayar.setText("-");
        }
    }

    private int getDurasiInt() {
        String durasiStr = etDurasiSewa.getText().toString();
        try {
            return Integer.parseInt(durasiStr.replaceAll("[^0-9]", "").trim());
        } catch (Exception e) {
            return 1;
        }
    }

    private void updateSummary() {
        calculateEndDate();

        if (selectedKamar == null) return;

        int durasi = getDurasiInt();
        double hargaFinal = 0;

        if (durasi == 3) {
            hargaFinal = selectedKamar.getHarga3Bulan();
        } else if (durasi == 6) {
            hargaFinal = selectedKamar.getHarga6Bulan();
        } else {
            hargaFinal = selectedKamar.getHarga1Bulan();
        }

        String hargaFormatted = String.format(Locale.forLanguageTag("id"), "Rp %,.0f", hargaFinal);

        String tglMulai = etTglMulaiSewa.getText().toString();
        String tglAkhir = etTglBayar.getText().toString();

        tvUnitSummary.setText(String.format("%s %s, %d bulan\n(%s s.d. %s)",
                selectedKamar.getJenisUnit(), selectedKamar.getNomorUnit(), durasi, tglMulai, tglAkhir));

        tvHargaSummary.setText(hargaFormatted);
        tvTotalHarga.setText(hargaFormatted);
    }

    private String saveImageToInternalStorage(Uri uri, String fileName) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            File file = new File(getFilesDir(), fileName + ".jpg");
            OutputStream outputStream = new FileOutputStream(file);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            outputStream.close();
            inputStream.close();

            return file.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Gagal menyimpan gambar", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    private void setupStaticDropdowns() {
        String[] jk = {"Laki-laki", "Perempuan"};
        etJenisKelamin.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, jk));
    }

    // Validasi Input
    private boolean validateInputs() {
        boolean isValid = true;

        if (etNamaPenyewa.getText().toString().trim().isEmpty()) {
            tilNama.setError("Nama wajib diisi");
            isValid = false;
        } else {
            tilNama.setError(null);
        }

        if (etWhatsapp.getText().toString().trim().isEmpty()) {
            tilWhatsapp.setError("Nomor WA wajib diisi");
            isValid = false;
        } else {
            tilWhatsapp.setError(null);
        }

        if (etJenisKelamin.getText().toString().trim().isEmpty()) {
            tilJenisKelamin.setError("Jenis kelamin wajib diisi");
            isValid = false;
        } else {
            etJenisKelamin.setError(null);
        }

        if (etJenisUnit.getText().toString().trim().isEmpty()) {
            tilJenisUnit.setError("Pilih jenis unit");
            isValid = false;
        } else {
            tilJenisUnit.setError(null);
        }

        if (etNoUnit.getText().toString().trim().isEmpty()) {
            tilNoUnit.setError("Pilih nomor unit");
            isValid = false;
        } else {
            tilNoUnit.setError(null);
        }

        // Cek apakah kamar (jenis dan nomor unit) sudah dipilih sebagai idKamar pada tabel penyewa
        if (selectedKamarId == -1) {
            Toast.makeText(this, "Silakan lengkapi datanya terlebih dahulu!", Toast.LENGTH_SHORT).show();
            isValid = false;
        }

        return isValid;
    }

    // MODIFIKASI: LOGIKA SIMPAN PENYEWA + AUTO INSERT KEUANGAN
    private void simpanPenyewa() {
        // Validasi Input terlebih dahulu
        if (!validateInputs()) {
            return;
        }

        int durasi = getDurasiInt();

        Penyewa p = new Penyewa();
        p.setNama(etNamaPenyewa.getText().toString());
        p.setWhatsapp(etWhatsapp.getText().toString());
        p.setJenisKelamin(etJenisKelamin.getText().toString());
        p.setDeskripsi(etDeskripsi.getText().toString());
        p.setIdKamar(selectedKamarId);
        p.setDurasiSewa(durasi);
        p.setTglMulai(etTglMulaiSewa.getText().toString());
        p.setTglPembayaranBerikutnya(etTglBayar.getText().toString());
        p.setFotoProfil(pathFotoProfil != null ? pathFotoProfil : "");
        p.setKtp(pathKtp != null ? pathKtp : "");

        // 1. Simpan Penyewa
        long resPenyewa = dbHelper.insertPenyewa(p);

        if (resPenyewa > 0) {
            // 2. Update status kamar jadi Terisi
            selectedKamar.setStatus(1);
            dbHelper.updateKamar(selectedKamar);

            // MODIFIKASI: 3. Simpan ke Tabel Keuangan (Pemasukan Otomatis)
            double hargaFinal = 0;
            if (durasi == 3) hargaFinal = selectedKamar.getHarga3Bulan();
            else if (durasi == 6) hargaFinal = selectedKamar.getHarga6Bulan();
            else hargaFinal = selectedKamar.getHarga1Bulan();

            Keuangan k = new Keuangan();
            k.setIdPenyewa((int) resPenyewa); // Menggunakan ID dari hasil insertPenyewa
            k.setTipe("Pemasukan");
            k.setDeskripsi("Pembayaran Awal Sewa - " + p.getNama() + " (Unit " + selectedKamar.getNomorUnit() + ")");
            k.setNominal(hargaFinal);
            k.setTanggal(p.getTglMulai()); // Menggunakan tanggal mulai sewa sebagai tanggal transaksi

            long resKeuangan = dbHelper.insertKeuangan(k);

            if(resKeuangan > 0) {
                Toast.makeText(this, "Penyewa & Data Keuangan Berhasil Disimpan", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Penyewa tersimpan, tapi Gagal simpan Keuangan", Toast.LENGTH_LONG).show();
            }

            finish();
        } else {
            Toast.makeText(this, "Gagal menyimpan", Toast.LENGTH_SHORT).show();
        }
    }
}