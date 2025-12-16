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

        // Ambil ID dari Intent
        penyewaId = getIntent().getIntExtra("PENYEWA_ID", -1);
        if (penyewaId == -1) {
            Toast.makeText(this, "Data penyewa tidak valid", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Inisialisasi Views
        initViews();

        // Load Data
        loadData();

        // Setup Listeners
        setupListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload data saat kembali dari Edit atau lainnya
        loadData();
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

        // Container untuk list transaksi
        llListTransaksi = findViewById(R.id.ll_list_transaksi);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        // Edit Button (Use Case 7)
        btnEdit.setOnClickListener(v -> {
            // Pastikan EditPenyewaActivity sudah dibuat. Jika belum, komen baris ini dulu.
            // Intent intent = new Intent(this, EditPenyewaActivity.class);
            // intent.putExtra("PENYEWA_ID", penyewaId);
            // startActivity(intent);
            Toast.makeText(this, "Fitur Edit akan segera hadir", Toast.LENGTH_SHORT).show();
        });

        // Hapus (Use Case 8)
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

        btnPerpanjang.setOnClickListener(v -> {
            Toast.makeText(this, "Fitur Perpanjang Sewa", Toast.LENGTH_SHORT).show();
            // Nanti bisa diarahkan ke activity pembayaran
        });
    }

    private void loadData() {
        Penyewa p = dbHelper.getPenyewa(penyewaId);

        if (p == null) {
            Toast.makeText(this, "Penyewa tidak ditemukan", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 1. Set Info Dasar
        tvNama.setText(p.getNama());
        tvDurasiInfo.setText("Durasi Sewa: " + p.getDurasiSewa() + " Bulan");

        // 2. Set Info Kamar
        Kamar k = dbHelper.getKamar(p.getIdKamar());
        if (k != null) {
            tvKamarInfo.setText(k.getJenisUnit() + " - " + k.getNomorUnit());
        } else {
            tvKamarInfo.setText("Kamar tidak ditemukan");
        }

        // 3. Set Foto Profil
        if (p.getFotoProfil() != null && !p.getFotoProfil().isEmpty()) {
            File imgFile = new File(p.getFotoProfil());
            if (imgFile.exists()) {
                ivProfile.setImageURI(Uri.fromFile(imgFile));
            } else {
                ivProfile.setImageResource(R.drawable.ic_person); // Default
            }
        }

        // 4. Set Foto KTP
        if (p.getKtp() != null && !p.getKtp().isEmpty()) {
            // Cek apakah p.getKtp() berisi path file gambar atau hanya nomor KTP string
            // Asumsi: Jika panjang string > 20 dan berisi "/", kemungkinan path file
            if (p.getKtp().contains("/")) {
                File ktpFile = new File(p.getKtp());
                if (ktpFile.exists()) {
                    ivFotoIdentitas.setImageURI(Uri.fromFile(ktpFile));
                }
            } else {
                // Jika hanya nomor, biarkan default atau tampilkan nomornya di TextView lain jika mau
            }
        }

        // 5. Logic WhatsApp Click
        ivWhatsapp.setOnClickListener(v -> {
            String phone = p.getWhatsapp();
            if (phone != null && !phone.isEmpty()) {
                // Normalisasi nomor (jika user input 08..., ubah ke 628...)
                if (phone.startsWith("0")) {
                    phone = "62" + phone.substring(1);
                }
                String url = "https://api.whatsapp.com/send?phone=" + phone;
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                try {
                    startActivity(i);
                } catch (Exception e) {
                    Toast.makeText(this, "WhatsApp tidak terinstall", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Nomor WhatsApp tidak tersedia", Toast.LENGTH_SHORT).show();
            }
        });

        // 6. Load Riwayat Transaksi (Dinamis)
        loadTransactionHistory(p.getId());
    }

    private void loadTransactionHistory(int idPenyewa) {
        llListTransaksi.removeAllViews(); // Bersihkan view lama

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
            View itemView;

            // Pilih layout berdasarkan tipe (Pemasukan/Pengeluaran)
            if ("Pengeluaran".equalsIgnoreCase(k.getTipe())) {
                itemView = inflater.inflate(R.layout.item_pengeluaran, llListTransaksi, false);

                TextView tvDesc = itemView.findViewById(R.id.tv_pengeluaran_deskripsi);
                TextView tvDate = itemView.findViewById(R.id.tv_pengeluaran_tanggal);
                TextView tvNominal = itemView.findViewById(R.id.tv_pengeluaran_nominal);

                tvDesc.setText(k.getDeskripsi());
                tvDate.setText(k.getTanggal());
                tvNominal.setText(formatRupiah(k.getNominal()));

            } else {
                // Default Pemasukan
                itemView = inflater.inflate(R.layout.item_pemasukan, llListTransaksi, false);

                TextView tvDesc = itemView.findViewById(R.id.tv_pemasukan_deskripsi);
                TextView tvDate = itemView.findViewById(R.id.tv_pemasukan_tanggal);
                TextView tvNominal = itemView.findViewById(R.id.tv_pemasukan_nominal);

                tvDesc.setText(k.getDeskripsi());
                tvDate.setText(k.getTanggal());
                tvNominal.setText(formatRupiah(k.getNominal()));
            }

            llListTransaksi.addView(itemView);
        }
    }

    private String formatRupiah(double number) {
        return String.format(Locale.forLanguageTag("id"), "Rp %,.0f", number);
    }

    private void confirmDelete() {
        new AlertDialog.Builder(this)
                .setTitle("Hapus Penyewa")
                .setMessage("Data penyewa dan riwayat kamar akan dihapus. Kamar akan menjadi 'Kosong'. Lanjutkan?")
                .setPositiveButton("Ya", (dialog, which) -> {
                    // Logic Hapus:
                    // 1. Ambil data penyewa untuk tahu ID Kamar
                    Penyewa p = dbHelper.getPenyewa(penyewaId);
                    if (p != null) {
                        // 2. Update status kamar jadi 0 (Kosong)
                        Kamar k = dbHelper.getKamar(p.getIdKamar());
                        if (k != null) {
                            k.setStatus(0);
                            dbHelper.updateKamar(k);
                        }
                    }

                    // 3. Hapus Penyewa
                    dbHelper.deletePenyewa(penyewaId);

                    Toast.makeText(this, "Data dihapus", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .setNegativeButton("Batal", null)
                .show();
    }
}