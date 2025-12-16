package com.example.ropikos;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ropikos.db.DBHelper;
import com.example.ropikos.model.Kamar;
import com.example.ropikos.model.Keuangan;
import com.example.ropikos.model.Penyewa;

import java.io.File;
import java.util.List;
import java.util.Locale;

public class DetailPenyewaActivity extends AppCompatActivity {

    private DBHelper dbHelper;
    private int penyewaId;

    // UI Components
    private TextView tvNama, tvKamarInfo, tvDurasiInfo;
    private ImageView ivProfile, ivWhatsapp, ivFotoIdentitas, btnBack;
    private ImageButton btnEdit;
    private Button btnLainnya, btnPerpanjang;
    private LinearLayout llListTransaksi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_penyewa);

        dbHelper = new DBHelper(this);

        // Ambil ID Penyewa
        penyewaId = getIntent().getIntExtra("PENYEWA_ID", -1);
        if (penyewaId == -1) {
            Toast.makeText(this, "Data penyewa tidak valid", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupListeners();
        loadData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData(); // Refresh data setelah edit / pindah kamar / perpanjang
    }

    private void initViews() {
        tvNama = findViewById(R.id.tv_nama_penyewa);
        tvKamarInfo = findViewById(R.id.tv_kamar_info);
        tvDurasiInfo = findViewById(R.id.tv_durasi_info);

        ivProfile = findViewById(R.id.iv_profile);
        ivWhatsapp = findViewById(R.id.iv_whatsapp);
        ivFotoIdentitas = findViewById(R.id.iv_foto_identitas);

        btnBack = findViewById(R.id.btn_back_header);
        btnEdit = findViewById(R.id.btn_edit);
        btnLainnya = findViewById(R.id.btn_lainnya);
        btnPerpanjang = findViewById(R.id.btn_perpanjang);

        llListTransaksi = findViewById(R.id.ll_list_transaksi);
    }

    // =========================
    // SETUP LISTENER (REVISI)
    // =========================
    private void setupListeners() {

        btnBack.setOnClickListener(v -> finish());

        // USE CASE: EDIT PENYEWA
        btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(this, EditPenyewaActivity.class);
            intent.putExtra("PENYEWA_ID", penyewaId);
            startActivity(intent);
        });

        // USE CASE: PINDAH KAMAR & HAPUS
        btnLainnya.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(this, btnLainnya);
            popup.getMenu().add(0, 1, 0, "Pindah Kamar");
            popup.getMenu().add(0, 2, 0, "Hapus Penyewa");

            popup.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == 1) {
                    Intent intent = new Intent(this, PindahKamarActivity.class);
                    intent.putExtra("PENYEWA_ID", penyewaId);
                    startActivity(intent);
                    return true;
                }
                else if (item.getItemId() == 2) {
                    confirmDelete();
                    return true;
                }
                return false;
            });
            popup.show();
        });

        // USE CASE: PERPANJANG SEWA
        btnPerpanjang.setOnClickListener(v -> {
            Intent intent = new Intent(this, PerpanjangSewaActivity.class);
            intent.putExtra("PENYEWA_ID", penyewaId);
            startActivity(intent);
        });
    }

    private void loadData() {
        Penyewa p = dbHelper.getPenyewa(penyewaId);
        if (p == null) {
            Toast.makeText(this, "Penyewa tidak ditemukan", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Info Dasar
        tvNama.setText(p.getNama());
        tvDurasiInfo.setText("Durasi Sewa: " + p.getDurasiSewa() + " Bulan");

        // Info Kamar
        Kamar k = dbHelper.getKamar(p.getIdKamar());
        if (k != null) {
            tvKamarInfo.setText(k.getJenisUnit() + " - " + k.getNomorUnit());
        } else {
            tvKamarInfo.setText("Kamar tidak ditemukan");
        }

        // Foto Profil
        if (p.getFotoProfil() != null && !p.getFotoProfil().isEmpty()) {
            File imgFile = new File(p.getFotoProfil());
            if (imgFile.exists()) {
                ivProfile.setImageURI(Uri.fromFile(imgFile));
            } else {
                ivProfile.setImageResource(R.drawable.ic_person);
            }
        }

        // Foto KTP
        if (p.getKtp() != null && p.getKtp().contains("/")) {
            File ktpFile = new File(p.getKtp());
            if (ktpFile.exists()) {
                ivFotoIdentitas.setImageURI(Uri.fromFile(ktpFile));
            }
        }

        // WhatsApp
        ivWhatsapp.setOnClickListener(v -> {
            String phone = p.getWhatsapp();
            if (phone != null && !phone.isEmpty()) {
                if (phone.startsWith("0")) {
                    phone = "62" + phone.substring(1);
                }
                Intent intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://api.whatsapp.com/send?phone=" + phone));
                startActivity(intent);
            } else {
                Toast.makeText(this, "Nomor WhatsApp tidak tersedia", Toast.LENGTH_SHORT).show();
            }
        });

        // Riwayat Transaksi
        loadTransactionHistory(penyewaId);
    }

    private void loadTransactionHistory(int idPenyewa) {
        llListTransaksi.removeAllViews();

        List<Keuangan> listKeuangan = dbHelper.getKeuanganByPenyewa(idPenyewa);
        LayoutInflater inflater = LayoutInflater.from(this);

        if (listKeuangan.isEmpty()) {
            TextView tvEmpty = new TextView(this);
            tvEmpty.setText("Belum ada riwayat transaksi.");
            tvEmpty.setPadding(0, 20, 0, 20);
            llListTransaksi.addView(tvEmpty);
            return;
        }

        for (Keuangan k : listKeuangan) {
            View view;

            if ("Pengeluaran".equalsIgnoreCase(k.getTipe())) {
                view = inflater.inflate(R.layout.item_pengeluaran, llListTransaksi, false);
                ((TextView) view.findViewById(R.id.tv_pengeluaran_deskripsi)).setText(k.getDeskripsi());
                ((TextView) view.findViewById(R.id.tv_pengeluaran_tanggal)).setText(k.getTanggal());
                ((TextView) view.findViewById(R.id.tv_pengeluaran_nominal))
                        .setText(formatRupiah(k.getNominal()));
            } else {
                view = inflater.inflate(R.layout.item_pemasukan, llListTransaksi, false);
                ((TextView) view.findViewById(R.id.tv_pemasukan_deskripsi)).setText(k.getDeskripsi());
                ((TextView) view.findViewById(R.id.tv_pemasukan_tanggal)).setText(k.getTanggal());
                ((TextView) view.findViewById(R.id.tv_pemasukan_nominal))
                        .setText(formatRupiah(k.getNominal()));
            }

            llListTransaksi.addView(view);
        }
    }

    private String formatRupiah(double number) {
        return String.format(Locale.forLanguageTag("id"), "Rp %,.0f", number);
    }

    private void confirmDelete() {
        new AlertDialog.Builder(this)
                .setTitle("Hapus Penyewa")
                .setMessage("Data penyewa akan dihapus dan kamar menjadi kosong. Lanjutkan?")
                .setPositiveButton("Ya", (dialog, which) -> {

                    Penyewa p = dbHelper.getPenyewa(penyewaId);
                    if (p != null) {
                        Kamar k = dbHelper.getKamar(p.getIdKamar());
                        if (k != null) {
                            k.setStatus(0);
                            dbHelper.updateKamar(k);
                        }
                    }

                    dbHelper.deletePenyewa(penyewaId);
                    Toast.makeText(this, "Data berhasil dihapus", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .setNegativeButton("Batal", null)
                .show();
    }
}
