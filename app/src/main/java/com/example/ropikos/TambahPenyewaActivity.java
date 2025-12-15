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
import com.example.ropikos.model.Penyewa;
import com.google.android.material.textfield.TextInputEditText;
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
    private TextInputEditText etNamaPenyewa, etWhatsapp, etDeskripsi, etTglBayar;
    private AutoCompleteTextView etJenisKelamin, etJenisUnit, etNoUnit, etTglMulaiSewa, etDurasiSewa;
    private Button btnSimpan, btnKtp;
    private ImageView btnBack, ivProfilePic, ivKtpPreview;
    private TextView tvUnitSummary, tvHargaSummary, tvTotalHarga;

    // Variabel untuk menyimpan path gambar
    private String pathFotoProfil = null;
    private String pathKtp = null;

    // Variable untuk menyimpan ID Kamar yang dipilih (jika ada)
    private int selectedKamarId = -1;
    private Kamar selectedKamar = null; // Menyimpan objek kamar yang dipilih
    private List<Kamar> availableKamarList; // List kamar kosong untuk dropdown
    private final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("dd-MM-yyyy", Locale.US);

    // Launcher untuk Foto Profil
    private final ActivityResultLauncher<Intent> launcherProfile = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri selectedUri = result.getData().getData();
                    if (selectedUri != null) {
                        // Tampilkan preview
                        ivProfilePic.setImageURI(selectedUri);
                        // Simpan ke internal storage & ambil path-nya
                        pathFotoProfil = saveImageToInternalStorage(selectedUri, "profile_" + System.currentTimeMillis());
                    }
                }
            }
    );

    // Launcher untuk KTP
    private final ActivityResultLauncher<Intent> launcherKtp = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri selectedUri = result.getData().getData();
                    if (selectedUri != null) {
                        // Tampilkan preview
                        ivKtpPreview.setImageURI(selectedUri);
                        // Simpan ke internal storage & ambil path-nya
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

        btnSimpan = findViewById(R.id.btn_simpan);
        btnBack = findViewById(R.id.btn_back);
        btnKtp = findViewById(R.id.btn_upload_ktp);

        // Klik Foto Profil untuk ganti gambar
        ivProfilePic.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            launcherProfile.launch(intent); // pemicu launcher
        });

        // Klik Tombol Upload KTP
        btnKtp.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            launcherKtp.launch(intent); // pemicu launcher
        });

        if (btnBack != null) btnBack.setOnClickListener(v -> finish());

        setupDateAndDurationLogic();

        // cek sumber data (Intent dari button tambah penyewa di layout item_kamar.xml atau Manual)
        if (getIntent().hasExtra("PRESELECTED_KAMAR_ID")) {
            // Case A: Dari ListKamarActivity (Autofill)
            selectedKamarId = getIntent().getIntExtra("PRESELECTED_KAMAR_ID", -1);
            if (selectedKamarId != -1) {
                fillKamarData(selectedKamarId);
                // Kunci dropdown agar tidak diubah
                etJenisUnit.setEnabled(false);
                etNoUnit.setEnabled(false);
            }
        } else {
            // Case B: Dari ListPenyewaActivity / FAB (Manual Dropdown)
            setupDropdownKamarAvailable();
        }

        setupStaticDropdowns(); // Isi dropdown Jenis Kelamin

        btnSimpan.setOnClickListener(v -> simpanPenyewa());
    }

    // Autofill Data Kamar dari item kamar yang dipilih
    private void fillKamarData(int kamarId) {
        selectedKamar = dbHelper.getKamar(kamarId);
        if (selectedKamar != null) {
            etJenisUnit.setText(selectedKamar.getJenisUnit());
            etNoUnit.setText(selectedKamar.getNomorUnit());
            updateSummary(); // Hitung harga langsung
        }
    }

    // Setup Dropdown Manual Kamar Kosong (Tersedia)
    private void setupDropdownKamarAvailable() {
        availableKamarList = dbHelper.getKamarTersedia(); // Mengambil data harga juga

        // Ambil Jenis Unit Unik
        Set<String> jenisSet = new HashSet<>();
        for (Kamar k : availableKamarList) {
            jenisSet.add(k.getJenisUnit());
        }
        List<String> listJenis = new ArrayList<>(jenisSet);
        ArrayAdapter<String> adapterJenis = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, listJenis);
        etJenisUnit.setAdapter(adapterJenis);

        // Listener saat Jenis Unit dipilih -> Filter Nomor Unit
        etJenisUnit.setOnItemClickListener((parent, view, position, id) -> {
            String selectedJenis = adapterJenis.getItem(position);
            etNoUnit.setText(""); // Reset nomor
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
            updateSummary(); // Hitung harga saat nomor dipilih
        });
    }

    // Logic Tanggal & Durasi
    private void setupDateAndDurationLogic() {
        // Setup Date Picker
        etTglMulaiSewa.setOnClickListener(v -> showDatePicker());
        etTglMulaiSewa.setText(DATE_FORMATTER.format(Calendar.getInstance().getTime())); // Default Hari Ini

        // Setup Dropdown Durasi
        String[] durasiOptions = {"1 Bulan", "3 Bulan", "6 Bulan"};
        ArrayAdapter<String> durasiAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, durasiOptions);
        etDurasiSewa.setAdapter(durasiAdapter);
        etDurasiSewa.setText("1 Bulan", false); // nilai default durasi menjadi 1 Bulan agar kolom tidak kosong saat awal dibuka

        // Trigger hitung ulang saat durasi berubah
        etDurasiSewa.setOnItemClickListener((parent, view, position, id) -> updateSummary());

        // Hitung awal
        calculateEndDate();
    }

    private void showDatePicker() {
        Calendar kalender = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, monthOfYear, dayOfMonth) -> {
                    kalender.set(year, monthOfYear, dayOfMonth);
                    etTglMulaiSewa.setText(DATE_FORMATTER.format(kalender.getTime()));
                    updateSummary(); // Recalculate dates
                },
                kalender.get(Calendar.YEAR),
                kalender.get(Calendar.MONTH),
                kalender.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    // Hitung tanggal berakhir saja
    private void calculateEndDate() {
        String tglMulaiStr = etTglMulaiSewa.getText().toString();
        int durasiBulan = getDurasiInt();

        try {
            Calendar kalender = Calendar.getInstance();
            kalender.setTime(DATE_FORMATTER.parse(tglMulaiStr));
            kalender.add(Calendar.MONTH, durasiBulan);
            kalender.add(Calendar.DAY_OF_MONTH, -1); // H-1
            etTglBayar.setText(DATE_FORMATTER.format(kalender.getTime()));
        } catch (Exception e) {
            etTglBayar.setText("-");
        }
    }

    // Helper ambil durasi integer
    private int getDurasiInt() {
        String durasiStr = etDurasiSewa.getText().toString();
        try {
            return Integer.parseInt(durasiStr.replaceAll("[^0-9]", "").trim());
        } catch (Exception e) {
            return 1;
        }
    }

    // Gabungan Harga & Tanggal
    private void updateSummary() {
        calculateEndDate(); // Pastikan tanggal akhir update dulu

        if (selectedKamar == null) return;

        int durasi = getDurasiInt();
        double hargaFinal = 0;

        // Cek harga berdasarkan durasi (DB getKamarTersedia)
        if (durasi == 3) {
            hargaFinal = selectedKamar.getHarga3Bulan();
        } else if (durasi == 6) {
            hargaFinal = selectedKamar.getHarga6Bulan();
        } else {
            hargaFinal = selectedKamar.getHarga1Bulan();
        }

        // Format Rupiah
        String hargaFormatted = String.format(Locale.forLanguageTag("id"), "Rp %,.0f", hargaFinal);

        // Update UI
        String tglMulai = etTglMulaiSewa.getText().toString();
        String tglAkhir = etTglBayar.getText().toString();

        /* TODO: format saat ini (Kamar AC A01, 3 bulan (15-12-2025 s.d. 14-03-2026)
         * mungkin format nya bisa disesuaikan lagi jika dperlukan
         */
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

            return file.getAbsolutePath(); // Mengembalikan path file lokal (contoh: /data/user/0/com.example.../files/ktp_123.jpg)
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

    // save logic
    private void simpanPenyewa() {
        if (etNamaPenyewa.getText().toString().isEmpty()) {
            Toast.makeText(this, "Nama wajib diisi", Toast.LENGTH_SHORT).show();
            return;
        }
        if (selectedKamarId == -1) {
            Toast.makeText(this, "Pilih Kamar terlebih dahulu", Toast.LENGTH_SHORT).show();
            return;
        }

        Penyewa p = new Penyewa();
        p.setNama(etNamaPenyewa.getText().toString());
        p.setWhatsapp(etWhatsapp.getText().toString());
        p.setJenisKelamin(etJenisKelamin.getText().toString());
        p.setDeskripsi(etDeskripsi.getText().toString());
        p.setIdKamar(selectedKamarId);
        p.setDurasiSewa(getDurasiInt());
        p.setTglMulai(etTglMulaiSewa.getText().toString());
        p.setTglPembayaranBerikutnya(etTglBayar.getText().toString());
        p.setFotoProfil(pathFotoProfil != null ? pathFotoProfil : "");
        p.setKtp(pathKtp != null ? pathKtp : "");

        long res = dbHelper.insertPenyewa(p);
        if (res > 0) {
            // Update status kamar jadi Terisi
            selectedKamar.setStatus(1);
            dbHelper.updateKamar(selectedKamar);

            Toast.makeText(this, "Penyewa Berhasil Ditambah", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Gagal menyimpan", Toast.LENGTH_SHORT).show();
        }
    }
}