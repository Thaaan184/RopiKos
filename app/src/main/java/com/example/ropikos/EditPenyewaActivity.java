package com.example.ropikos;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ropikos.db.DBHelper;
import com.example.ropikos.model.Penyewa;
import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class EditPenyewaActivity extends AppCompatActivity {

    private DBHelper dbHelper;
    private int penyewaId;
    private Penyewa currentPenyewa;

    private TextInputEditText etNama, etWhatsapp, etDeskripsi;
    private AutoCompleteTextView etJenisKelamin;
    private ImageView ivProfile, ivKtp, btnBack;
    private Button btnUploadKtp, btnSimpan;

    private String pathFotoProfil = null;
    private String pathKtp = null;

    // Launcher Galeri (Sama seperti Tambah)
    private final ActivityResultLauncher<Intent> launcherProfile = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri uri = result.getData().getData();
                    ivProfile.setImageURI(uri);
                    pathFotoProfil = saveImage(uri, "profile_" + System.currentTimeMillis());
                }
            }
    );

    private final ActivityResultLauncher<Intent> launcherKtp = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri uri = result.getData().getData();
                    ivKtp.setImageURI(uri);
                    pathKtp = saveImage(uri, "ktp_" + System.currentTimeMillis());
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_penyewa);

        dbHelper = new DBHelper(this);
        penyewaId = getIntent().getIntExtra("PENYEWA_ID", -1);

        initViews();
        loadData();

        btnSimpan.setOnClickListener(v -> simpanPerubahan());
        btnBack.setOnClickListener(v -> finish());

        ivProfile.setOnClickListener(v -> pickImage(launcherProfile));
        btnUploadKtp.setOnClickListener(v -> pickImage(launcherKtp));
    }

    private void initViews() {
        etNama = findViewById(R.id.et_nama_penyewa); // Pastikan ID di XML et_nama_penyewa (bukan TextInputLayout)
        etWhatsapp = findViewById(R.id.et_whatsapp);
        etDeskripsi = findViewById(R.id.et_deskripsi);
        etJenisKelamin = findViewById(R.id.et_jenis_kelamin); // AutoComplete
        ivProfile = findViewById(R.id.iv_profile_pic);
        ivKtp = findViewById(R.id.iv_ktp_preview);
        btnUploadKtp = findViewById(R.id.btn_upload_ktp);
        btnSimpan = findViewById(R.id.btn_simpan);
        btnBack = findViewById(R.id.btn_back);

        String[] jk = {"Laki-laki", "Perempuan"};
        etJenisKelamin.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, jk));
    }

    private void loadData() {
        currentPenyewa = dbHelper.getPenyewa(penyewaId);
        if (currentPenyewa == null) {
            Toast.makeText(this, "Error load data", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        etNama.setText(currentPenyewa.getNama());
        etWhatsapp.setText(currentPenyewa.getWhatsapp());
        etDeskripsi.setText(currentPenyewa.getDeskripsi());
        etJenisKelamin.setText(currentPenyewa.getJenisKelamin(), false);

        pathFotoProfil = currentPenyewa.getFotoProfil();
        if (pathFotoProfil != null && !pathFotoProfil.isEmpty()) {
            ivProfile.setImageURI(Uri.fromFile(new File(pathFotoProfil)));
        }

        pathKtp = currentPenyewa.getKtp();
        if (pathKtp != null && !pathKtp.isEmpty() && pathKtp.contains("/")) {
            ivKtp.setImageURI(Uri.fromFile(new File(pathKtp)));
        }
    }

    private void simpanPerubahan() {
        currentPenyewa.setNama(etNama.getText().toString());
        currentPenyewa.setWhatsapp(etWhatsapp.getText().toString());
        currentPenyewa.setDeskripsi(etDeskripsi.getText().toString());
        currentPenyewa.setJenisKelamin(etJenisKelamin.getText().toString());

        if (pathFotoProfil != null) currentPenyewa.setFotoProfil(pathFotoProfil);
        if (pathKtp != null) currentPenyewa.setKtp(pathKtp);

        int res = dbHelper.updatePenyewa(currentPenyewa);
        if (res > 0) {
            Toast.makeText(this, "Data Berhasil Diupdate", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Gagal Update", Toast.LENGTH_SHORT).show();
        }
    }

    private void pickImage(ActivityResultLauncher<Intent> launcher) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        launcher.launch(intent);
    }

    private String saveImage(Uri uri, String name) {
        try {
            InputStream in = getContentResolver().openInputStream(uri);
            File file = new File(getFilesDir(), name + ".jpg");
            OutputStream out = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) out.write(buf, 0, len);
            out.close();
            in.close();
            return file.getAbsolutePath();
        } catch (Exception e) { return null; }
    }
}