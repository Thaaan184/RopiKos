package com.example.ropikos.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.ropikos.model.Kamar;
import com.example.ropikos.model.Keuangan;
import com.example.ropikos.model.Penyewa;
import com.example.ropikos.model.Perawatan;
import com.example.ropikos.model.User;

import java.util.ArrayList;
import java.util.List;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "ropikos.db";
    // NAIKKAN VERSI karena ada perubahan struktur (tambah id_penyewa di keuangan)
    private static final int DATABASE_VERSION = 4;

    // --- TABEL USER ---
    public static final String TABLE_USER = "users";
    public static final String COLUMN_USER_ID = "id";
    public static final String COLUMN_USER_FULLNAME = "fullname";
    public static final String COLUMN_USER_USERNAME = "username";
    public static final String COLUMN_USER_PASSWORD = "password";
    public static final String COLUMN_USER_PHONE = "phone";
    public static final String COLUMN_USER_ADDRESS = "address";

    // --- TABEL KAMAR ---
    public static final String TABLE_KAMAR = "kamar";
    public static final String COLUMN_KAMAR_ID = "id";
    public static final String COLUMN_KAMAR_JENIS_UNIT = "jenis_unit";
    public static final String COLUMN_KAMAR_NOMOR_UNIT = "nomor_unit";
    public static final String COLUMN_KAMAR_KETERANGAN = "keterangan";
    public static final String COLUMN_KAMAR_MAKS_PENYEWA = "maks_penyewa";
    public static final String COLUMN_KAMAR_HARGA_1BULAN = "harga_1bulan";
    public static final String COLUMN_KAMAR_HARGA_3BULAN = "harga_3bulan";
    public static final String COLUMN_KAMAR_HARGA_6BULAN = "harga_6bulan";
    public static final String COLUMN_KAMAR_STATUS = "status"; // 0=Kosong, 1=Terisi

    // --- TABEL PENYEWA ---
    public static final String TABLE_PENYEWA = "penyewa";
    public static final String COLUMN_PENYEWA_ID = "id";
    public static final String COLUMN_PENYEWA_NAMA = "nama";
    public static final String COLUMN_PENYEWA_WHATSAPP = "whatsapp";
    public static final String COLUMN_PENYEWA_JENIS_KELAMIN = "jenis_kelamin";
    public static final String COLUMN_PENYEWA_DESKRIPSI = "deskripsi";
    public static final String COLUMN_PENYEWA_FOTO_PROFIL = "foto_profil";
    public static final String COLUMN_PENYEWA_KTP = "ktp";
    public static final String COLUMN_PENYEWA_ID_KAMAR = "id_kamar";
    public static final String COLUMN_PENYEWA_DURASI_SEWA = "durasi_sewa";
    public static final String COLUMN_PENYEWA_TGL_MULAI = "tgl_mulai";
    public static final String COLUMN_PENYEWA_TGL_PEMBAYARAN_BERIKUTNYA = "tgl_pembayaran_berikutnya";

    // --- TABEL KEUANGAN ---
    public static final String TABLE_KEUANGAN = "keuangan";
    public static final String COLUMN_KEUANGAN_ID = "id";
    public static final String COLUMN_KEUANGAN_ID_PENYEWA = "id_penyewa"; // BARU
    public static final String COLUMN_KEUANGAN_TIPE = "tipe";
    public static final String COLUMN_KEUANGAN_DESKRIPSI = "deskripsi";
    public static final String COLUMN_KEUANGAN_NOMINAL = "nominal";
    public static final String COLUMN_KEUANGAN_TANGGAL = "tanggal";

    // --- TABEL PERAWATAN ---
    public static final String TABLE_PERAWATAN = "perawatan";
    public static final String COLUMN_PERAWATAN_ID = "id";
    public static final String COLUMN_PERAWATAN_NAMA = "nama_perawatan";
    public static final String COLUMN_PERAWATAN_TANGGAL = "tanggal";
    public static final String COLUMN_PERAWATAN_ID_KAMAR = "id_kamar";
    public static final String COLUMN_PERAWATAN_BIAYA_SPAREPART = "biaya_sparepart";
    public static final String COLUMN_PERAWATAN_BIAYA_JASA = "biaya_jasa";
    public static final String COLUMN_PERAWATAN_ONGKIR = "ongkir";
    public static final String COLUMN_PERAWATAN_CATATAN = "catatan";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_USER + " ("
                + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_USER_FULLNAME + " TEXT, "
                + COLUMN_USER_USERNAME + " TEXT UNIQUE, "
                + COLUMN_USER_PASSWORD + " TEXT, "
                + COLUMN_USER_PHONE + " TEXT, "
                + COLUMN_USER_ADDRESS + " TEXT);");

        db.execSQL("CREATE TABLE " + TABLE_KAMAR + " ("
                + COLUMN_KAMAR_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_KAMAR_JENIS_UNIT + " TEXT, "
                + COLUMN_KAMAR_NOMOR_UNIT + " TEXT, "
                + COLUMN_KAMAR_KETERANGAN + " TEXT, "
                + COLUMN_KAMAR_MAKS_PENYEWA + " INTEGER, "
                + COLUMN_KAMAR_HARGA_1BULAN + " REAL, "
                + COLUMN_KAMAR_HARGA_3BULAN + " REAL, "
                + COLUMN_KAMAR_HARGA_6BULAN + " REAL, "
                + COLUMN_KAMAR_STATUS + " INTEGER DEFAULT 0);");

        db.execSQL("CREATE TABLE " + TABLE_PENYEWA + " ("
                + COLUMN_PENYEWA_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_PENYEWA_NAMA + " TEXT, "
                + COLUMN_PENYEWA_WHATSAPP + " TEXT, "
                + COLUMN_PENYEWA_JENIS_KELAMIN + " TEXT, "
                + COLUMN_PENYEWA_DESKRIPSI + " TEXT, "
                + COLUMN_PENYEWA_FOTO_PROFIL + " TEXT, "
                + COLUMN_PENYEWA_KTP + " TEXT, "
                + COLUMN_PENYEWA_ID_KAMAR + " INTEGER, "
                + COLUMN_PENYEWA_DURASI_SEWA + " INTEGER, "
                + COLUMN_PENYEWA_TGL_MULAI + " TEXT, "
                + COLUMN_PENYEWA_TGL_PEMBAYARAN_BERIKUTNYA + " TEXT, "
                + "FOREIGN KEY(" + COLUMN_PENYEWA_ID_KAMAR + ") REFERENCES "
                + TABLE_KAMAR + "(" + COLUMN_KAMAR_ID + "));");

        db.execSQL("CREATE TABLE " + TABLE_KEUANGAN + " ("
                + COLUMN_KEUANGAN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_KEUANGAN_ID_PENYEWA + " INTEGER, "
                + COLUMN_KEUANGAN_TIPE + " TEXT, "
                + COLUMN_KEUANGAN_DESKRIPSI + " TEXT, "
                + COLUMN_KEUANGAN_NOMINAL + " REAL, "
                + COLUMN_KEUANGAN_TANGGAL + " TEXT);");

        db.execSQL("CREATE TABLE " + TABLE_PERAWATAN + " ("
                + COLUMN_PERAWATAN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_PERAWATAN_NAMA + " TEXT, "
                + COLUMN_PERAWATAN_TANGGAL + " TEXT, "
                + COLUMN_PERAWATAN_ID_KAMAR + " INTEGER, "
                + COLUMN_PERAWATAN_BIAYA_SPAREPART + " REAL, "
                + COLUMN_PERAWATAN_BIAYA_JASA + " REAL, "
                + COLUMN_PERAWATAN_ONGKIR + " REAL, "
                + COLUMN_PERAWATAN_CATATAN + " TEXT, "
                + "FOREIGN KEY(" + COLUMN_PERAWATAN_ID_KAMAR + ") REFERENCES "
                + TABLE_KAMAR + "(" + COLUMN_KAMAR_ID + "));");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // WARNING: semua data akan terhapus saat upgrade
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_KAMAR);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PENYEWA);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_KEUANGAN);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PERAWATAN);
        onCreate(db);
    }

    // ================= USER =================
    public User getUser(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USER, null, COLUMN_USER_USERNAME + "=?",
                new String[]{username}, null, null, null);
        User user = null;
        if (cursor != null && cursor.moveToFirst()) {
            user = new User();
            user.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USER_ID)));
            user.setFullname(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_FULLNAME)));
            user.setUsername(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_USERNAME)));
            user.setPassword(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_PASSWORD)));
            user.setPhoneNumber(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_PHONE)));
            user.setAddress(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_ADDRESS)));
            cursor.close();
        }
        return user;
    }

    // ================= KAMAR =================
    public long insertKamar(Kamar kamar) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(COLUMN_KAMAR_JENIS_UNIT, kamar.getJenisUnit());
        v.put(COLUMN_KAMAR_NOMOR_UNIT, kamar.getNomorUnit());
        v.put(COLUMN_KAMAR_KETERANGAN, kamar.getKeterangan());
        v.put(COLUMN_KAMAR_MAKS_PENYEWA, kamar.getMaksPenyewa());
        v.put(COLUMN_KAMAR_HARGA_1BULAN, kamar.getHarga1Bulan());
        v.put(COLUMN_KAMAR_HARGA_3BULAN, kamar.getHarga3Bulan());
        v.put(COLUMN_KAMAR_HARGA_6BULAN, kamar.getHarga6Bulan());
        v.put(COLUMN_KAMAR_STATUS, kamar.getStatus());
        return db.insert(TABLE_KAMAR, null, v);
    }

    public List<Kamar> getAllKamar() {
        List<Kamar> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.query(TABLE_KAMAR, null, null, null, null, null, null);
        if (c.moveToFirst()) {
            do {
                Kamar k = new Kamar();
                k.setId(c.getInt(c.getColumnIndexOrThrow(COLUMN_KAMAR_ID)));
                k.setJenisUnit(c.getString(c.getColumnIndexOrThrow(COLUMN_KAMAR_JENIS_UNIT)));
                k.setNomorUnit(c.getString(c.getColumnIndexOrThrow(COLUMN_KAMAR_NOMOR_UNIT)));
                k.setKeterangan(c.getString(c.getColumnIndexOrThrow(COLUMN_KAMAR_KETERANGAN)));
                k.setMaksPenyewa(c.getInt(c.getColumnIndexOrThrow(COLUMN_KAMAR_MAKS_PENYEWA)));
                k.setHarga1Bulan(c.getDouble(c.getColumnIndexOrThrow(COLUMN_KAMAR_HARGA_1BULAN)));
                k.setHarga3Bulan(c.getDouble(c.getColumnIndexOrThrow(COLUMN_KAMAR_HARGA_3BULAN)));
                k.setHarga6Bulan(c.getDouble(c.getColumnIndexOrThrow(COLUMN_KAMAR_HARGA_6BULAN)));
                k.setStatus(c.getInt(c.getColumnIndexOrThrow(COLUMN_KAMAR_STATUS)));
                list.add(k);
            } while (c.moveToNext());
        }
        c.close();
        return list;
    }

    public Kamar getKamar(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.query(TABLE_KAMAR, null, COLUMN_KAMAR_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null);
        if (c != null && c.moveToFirst()) {
            Kamar k = new Kamar();
            k.setId(c.getInt(c.getColumnIndexOrThrow(COLUMN_KAMAR_ID)));
            k.setJenisUnit(c.getString(c.getColumnIndexOrThrow(COLUMN_KAMAR_JENIS_UNIT)));
            k.setNomorUnit(c.getString(c.getColumnIndexOrThrow(COLUMN_KAMAR_NOMOR_UNIT)));
            k.setKeterangan(c.getString(c.getColumnIndexOrThrow(COLUMN_KAMAR_KETERANGAN)));
            k.setMaksPenyewa(c.getInt(c.getColumnIndexOrThrow(COLUMN_KAMAR_MAKS_PENYEWA)));
            k.setHarga1Bulan(c.getDouble(c.getColumnIndexOrThrow(COLUMN_KAMAR_HARGA_1BULAN)));
            k.setHarga3Bulan(c.getDouble(c.getColumnIndexOrThrow(COLUMN_KAMAR_HARGA_3BULAN)));
            k.setHarga6Bulan(c.getDouble(c.getColumnIndexOrThrow(COLUMN_KAMAR_HARGA_6BULAN)));
            k.setStatus(c.getInt(c.getColumnIndexOrThrow(COLUMN_KAMAR_STATUS)));
            c.close();
            return k;
        }
        return null;
    }

    public List<Kamar> getKamarTersedia() {
        List<Kamar> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.query(TABLE_KAMAR, null, COLUMN_KAMAR_STATUS + "=?",
                new String[]{"0"}, null, null, null);
        if (c.moveToFirst()) {
            do {
                Kamar k = new Kamar();
                k.setId(c.getInt(c.getColumnIndexOrThrow(COLUMN_KAMAR_ID)));
                k.setJenisUnit(c.getString(c.getColumnIndexOrThrow(COLUMN_KAMAR_JENIS_UNIT)));
                k.setNomorUnit(c.getString(c.getColumnIndexOrThrow(COLUMN_KAMAR_NOMOR_UNIT)));
                k.setKeterangan(c.getString(c.getColumnIndexOrThrow(COLUMN_KAMAR_KETERANGAN)));
                k.setMaksPenyewa(c.getInt(c.getColumnIndexOrThrow(COLUMN_KAMAR_MAKS_PENYEWA)));
                k.setHarga1Bulan(c.getDouble(c.getColumnIndexOrThrow(COLUMN_KAMAR_HARGA_1BULAN)));
                k.setHarga3Bulan(c.getDouble(c.getColumnIndexOrThrow(COLUMN_KAMAR_HARGA_3BULAN)));
                k.setHarga6Bulan(c.getDouble(c.getColumnIndexOrThrow(COLUMN_KAMAR_HARGA_6BULAN)));
                k.setStatus(c.getInt(c.getColumnIndexOrThrow(COLUMN_KAMAR_STATUS)));
                list.add(k);
            } while (c.moveToNext());
        }
        c.close();
        return list;
    }

    public int updateKamar(Kamar kamar) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(COLUMN_KAMAR_JENIS_UNIT, kamar.getJenisUnit());
        v.put(COLUMN_KAMAR_NOMOR_UNIT, kamar.getNomorUnit());
        v.put(COLUMN_KAMAR_KETERANGAN, kamar.getKeterangan());
        v.put(COLUMN_KAMAR_MAKS_PENYEWA, kamar.getMaksPenyewa());
        v.put(COLUMN_KAMAR_HARGA_1BULAN, kamar.getHarga1Bulan());
        v.put(COLUMN_KAMAR_HARGA_3BULAN, kamar.getHarga3Bulan());
        v.put(COLUMN_KAMAR_HARGA_6BULAN, kamar.getHarga6Bulan());
        v.put(COLUMN_KAMAR_STATUS, kamar.getStatus());
        return db.update(TABLE_KAMAR, v, COLUMN_KAMAR_ID + "=?",
                new String[]{String.valueOf(kamar.getId())});
    }

    public void deleteKamar(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_KAMAR, COLUMN_KAMAR_ID + "=?",
                new String[]{String.valueOf(id)});
    }

    // ================= PENYEWA =================
    public long insertPenyewa(Penyewa p) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(COLUMN_PENYEWA_NAMA, p.getNama());
        v.put(COLUMN_PENYEWA_WHATSAPP, p.getWhatsapp());
        v.put(COLUMN_PENYEWA_JENIS_KELAMIN, p.getJenisKelamin());
        v.put(COLUMN_PENYEWA_DESKRIPSI, p.getDeskripsi());
        v.put(COLUMN_PENYEWA_FOTO_PROFIL, p.getFotoProfil());
        v.put(COLUMN_PENYEWA_KTP, p.getKtp());
        v.put(COLUMN_PENYEWA_ID_KAMAR, p.getIdKamar());
        v.put(COLUMN_PENYEWA_DURASI_SEWA, p.getDurasiSewa());
        v.put(COLUMN_PENYEWA_TGL_MULAI, p.getTglMulai());
        v.put(COLUMN_PENYEWA_TGL_PEMBAYARAN_BERIKUTNYA, p.getTglPembayaranBerikutnya());
        return db.insert(TABLE_PENYEWA, null, v);
    }

    public List<Penyewa> getAllPenyewa() {
        List<Penyewa> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.query(TABLE_PENYEWA, null, null, null, null, null, null);
        if (c.moveToFirst()) {
            do {
                Penyewa p = new Penyewa();
                p.setId(c.getInt(c.getColumnIndexOrThrow(COLUMN_PENYEWA_ID)));
                p.setNama(c.getString(c.getColumnIndexOrThrow(COLUMN_PENYEWA_NAMA)));
                p.setWhatsapp(c.getString(c.getColumnIndexOrThrow(COLUMN_PENYEWA_WHATSAPP)));
                p.setFotoProfil(c.getString(c.getColumnIndexOrThrow(COLUMN_PENYEWA_FOTO_PROFIL)));
                p.setKtp(c.getString(c.getColumnIndexOrThrow(COLUMN_PENYEWA_KTP)));
                p.setIdKamar(c.getInt(c.getColumnIndexOrThrow(COLUMN_PENYEWA_ID_KAMAR)));
                p.setDurasiSewa(c.getInt(c.getColumnIndexOrThrow(COLUMN_PENYEWA_DURASI_SEWA)));
                p.setTglMulai(c.getString(c.getColumnIndexOrThrow(COLUMN_PENYEWA_TGL_MULAI)));
                p.setTglPembayaranBerikutnya(c.getString(c.getColumnIndexOrThrow(COLUMN_PENYEWA_TGL_PEMBAYARAN_BERIKUTNYA)));
                list.add(p);
            } while (c.moveToNext());
        }
        c.close();
        return list;
    }

    public Penyewa getPenyewa(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.query(TABLE_PENYEWA, null, COLUMN_PENYEWA_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null);
        if (c != null && c.moveToFirst()) {
            Penyewa p = new Penyewa();
            p.setId(c.getInt(c.getColumnIndexOrThrow(COLUMN_PENYEWA_ID)));
            p.setNama(c.getString(c.getColumnIndexOrThrow(COLUMN_PENYEWA_NAMA)));
            p.setWhatsapp(c.getString(c.getColumnIndexOrThrow(COLUMN_PENYEWA_WHATSAPP)));
            p.setFotoProfil(c.getString(c.getColumnIndexOrThrow(COLUMN_PENYEWA_FOTO_PROFIL)));
            p.setKtp(c.getString(c.getColumnIndexOrThrow(COLUMN_PENYEWA_KTP)));
            p.setIdKamar(c.getInt(c.getColumnIndexOrThrow(COLUMN_PENYEWA_ID_KAMAR)));
            p.setDurasiSewa(c.getInt(c.getColumnIndexOrThrow(COLUMN_PENYEWA_DURASI_SEWA)));
            p.setTglMulai(c.getString(c.getColumnIndexOrThrow(COLUMN_PENYEWA_TGL_MULAI)));
            p.setTglPembayaranBerikutnya(c.getString(c.getColumnIndexOrThrow(COLUMN_PENYEWA_TGL_PEMBAYARAN_BERIKUTNYA)));
            c.close();
            return p;
        }
        return null;
    }

    public int updatePenyewa(Penyewa p) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(COLUMN_PENYEWA_NAMA, p.getNama());
        v.put(COLUMN_PENYEWA_WHATSAPP, p.getWhatsapp());
        v.put(COLUMN_PENYEWA_JENIS_KELAMIN, p.getJenisKelamin());
        v.put(COLUMN_PENYEWA_DESKRIPSI, p.getDeskripsi());
        v.put(COLUMN_PENYEWA_FOTO_PROFIL, p.getFotoProfil());
        v.put(COLUMN_PENYEWA_KTP, p.getKtp());
        // Update ID Kamar juga (PENTING untuk fitur Pindah Kamar)
        v.put(COLUMN_PENYEWA_ID_KAMAR, p.getIdKamar());
        v.put(COLUMN_PENYEWA_TGL_PEMBAYARAN_BERIKUTNYA, p.getTglPembayaranBerikutnya());

        return db.update(TABLE_PENYEWA, v, COLUMN_PENYEWA_ID + "=?",
                new String[]{String.valueOf(p.getId())});
    }

    public void deletePenyewa(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PENYEWA, COLUMN_PENYEWA_ID + "=?",
                new String[]{String.valueOf(id)});
    }

    // ================= KEUANGAN =================
    public long insertKeuangan(Keuangan k) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(COLUMN_KEUANGAN_ID_PENYEWA, k.getIdPenyewa());
        v.put(COLUMN_KEUANGAN_TIPE, k.getTipe());
        v.put(COLUMN_KEUANGAN_DESKRIPSI, k.getDeskripsi());
        v.put(COLUMN_KEUANGAN_NOMINAL, k.getNominal());
        v.put(COLUMN_KEUANGAN_TANGGAL, k.getTanggal());
        return db.insert(TABLE_KEUANGAN, null, v);
    }

    public List<Keuangan> getAllKeuangan() {
        List<Keuangan> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.query(TABLE_KEUANGAN, null, null, null, null, null, null);
        if (c.moveToFirst()) {
            do {
                Keuangan k = new Keuangan();
                k.setId(c.getInt(c.getColumnIndexOrThrow(COLUMN_KEUANGAN_ID)));
                if (c.getColumnIndex(COLUMN_KEUANGAN_ID_PENYEWA) != -1) {
                    k.setIdPenyewa(c.getInt(c.getColumnIndexOrThrow(COLUMN_KEUANGAN_ID_PENYEWA)));
                }
                k.setTipe(c.getString(c.getColumnIndexOrThrow(COLUMN_KEUANGAN_TIPE)));
                k.setDeskripsi(c.getString(c.getColumnIndexOrThrow(COLUMN_KEUANGAN_DESKRIPSI)));
                k.setNominal(c.getDouble(c.getColumnIndexOrThrow(COLUMN_KEUANGAN_NOMINAL)));
                k.setTanggal(c.getString(c.getColumnIndexOrThrow(COLUMN_KEUANGAN_TANGGAL)));
                list.add(k);
            } while (c.moveToNext());
        }
        c.close();
        return list;
    }

    // ================= STATISTIK =================
    public int getTotalKamar() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_KAMAR, null);
        int count = 0;
        if (c.moveToFirst()) count = c.getInt(0);
        c.close();
        return count;
    }

    public int getTotalPenyewa() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_PENYEWA, null);
        int count = 0;
        if (c.moveToFirst()) count = c.getInt(0);
        c.close();
        return count;
    }

    public int getTotalLunas() {
        return getTotalPenyewa(); // dummy logic
    }

    public int getTotalPerbaikan() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_PERAWATAN, null);
        int count = 0;
        if (c.moveToFirst()) count = c.getInt(0);
        c.close();
        return count;
    }

    // ================= STATISTIK =================
    public double getPendapatanBulanIni() {
        double totalPendapatan = 0;
        SQLiteDatabase db = this.getReadableDatabase();

        // Format tanggal di DB: dd-MM-yyyy
        // Ambil bulan & tahun saat ini -> "-MM-yyyy"
        SimpleDateFormat sdf = new SimpleDateFormat("-MM-yyyy", Locale.US);
        String currentMonthYear = sdf.format(new Date());

        String query = "SELECT " + COLUMN_KEUANGAN_NOMINAL + ", " + COLUMN_KEUANGAN_TANGGAL +
                " FROM " + TABLE_KEUANGAN +
                " WHERE " + COLUMN_KEUANGAN_TIPE + " = 'Pemasukan'";

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                String tanggalDB = cursor.getString(
                        cursor.getColumnIndexOrThrow(COLUMN_KEUANGAN_TANGGAL)
                );
                double nominal = cursor.getDouble(
                        cursor.getColumnIndexOrThrow(COLUMN_KEUANGAN_NOMINAL)
                );

                // Cek apakah tanggal termasuk bulan ini
                if (tanggalDB != null && tanggalDB.endsWith(currentMonthYear)) {
                    totalPendapatan += nominal;
                }
            } while (cursor.moveToNext());
        }

        cursor.close();
        return totalPendapatan;
    }

    // Mengambil daftar keuangan berdasarkan ID Penyewa (untuk halaman Detail Penyewa)
    public List<Keuangan> getKeuanganByPenyewa(int idPenyewa) {
        List<Keuangan> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Urutkan dari data terbaru
        String query = "SELECT * FROM " + TABLE_KEUANGAN +
                " WHERE " + COLUMN_KEUANGAN_ID_PENYEWA + " = ?" +
                " ORDER BY " + COLUMN_KEUANGAN_ID + " DESC";

        Cursor c = db.rawQuery(query, new String[]{String.valueOf(idPenyewa)});

        if (c.moveToFirst()) {
            do {
                Keuangan k = new Keuangan();
                k.setId(c.getInt(c.getColumnIndexOrThrow(COLUMN_KEUANGAN_ID)));
                k.setIdPenyewa(c.getInt(c.getColumnIndexOrThrow(COLUMN_KEUANGAN_ID_PENYEWA)));
                k.setTipe(c.getString(c.getColumnIndexOrThrow(COLUMN_KEUANGAN_TIPE)));
                k.setDeskripsi(c.getString(c.getColumnIndexOrThrow(COLUMN_KEUANGAN_DESKRIPSI)));
                k.setNominal(c.getDouble(c.getColumnIndexOrThrow(COLUMN_KEUANGAN_NOMINAL)));
                k.setTanggal(c.getString(c.getColumnIndexOrThrow(COLUMN_KEUANGAN_TANGGAL)));
                list.add(k);
            } while (c.moveToNext());
        }

        c.close();
        return list;
    }

    // --- TAMBAHAN UNTUK EDIT & HAPUS KEUANGAN ---

    // Ambil 1 data keuangan berdasarkan ID (untuk Edit)
    public Keuangan getKeuangan(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.query(TABLE_KEUANGAN, null, COLUMN_KEUANGAN_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null);
        if (c != null && c.moveToFirst()) {
            Keuangan k = new Keuangan();
            k.setId(c.getInt(c.getColumnIndexOrThrow(COLUMN_KEUANGAN_ID)));
            if (c.getColumnIndex(COLUMN_KEUANGAN_ID_PENYEWA) != -1) {
                k.setIdPenyewa(c.getInt(c.getColumnIndexOrThrow(COLUMN_KEUANGAN_ID_PENYEWA)));
            }
            k.setTipe(c.getString(c.getColumnIndexOrThrow(COLUMN_KEUANGAN_TIPE)));
            k.setDeskripsi(c.getString(c.getColumnIndexOrThrow(COLUMN_KEUANGAN_DESKRIPSI)));
            k.setNominal(c.getDouble(c.getColumnIndexOrThrow(COLUMN_KEUANGAN_NOMINAL)));
            k.setTanggal(c.getString(c.getColumnIndexOrThrow(COLUMN_KEUANGAN_TANGGAL)));
            c.close();
            return k;
        }
        return null;
    }

    // Update Data Keuangan
    public int updateKeuangan(Keuangan k) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(COLUMN_KEUANGAN_TIPE, k.getTipe());
        v.put(COLUMN_KEUANGAN_DESKRIPSI, k.getDeskripsi());
        v.put(COLUMN_KEUANGAN_NOMINAL, k.getNominal());
        v.put(COLUMN_KEUANGAN_TANGGAL, k.getTanggal());
        // id_penyewa biasanya tidak diubah saat edit transaksi manual, tapi bisa ditambahkan jika perlu
        return db.update(TABLE_KEUANGAN, v, COLUMN_KEUANGAN_ID + "=?",
                new String[]{String.valueOf(k.getId())});
    }

    // Hapus Data Keuangan
    public void deleteKeuangan(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_KEUANGAN, COLUMN_KEUANGAN_ID + "=?",
                new String[]{String.valueOf(id)});
    }

    // --- TAMBAHAN BARU: METHOD INSERT PERAWATAN ---
    public long insertPerawatan(Perawatan p) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(COLUMN_PERAWATAN_NAMA, p.getNamaPerawatan());
        v.put(COLUMN_PERAWATAN_TANGGAL, p.getTanggal());
        v.put(COLUMN_PERAWATAN_ID_KAMAR, p.getIdKamar());
        v.put(COLUMN_PERAWATAN_BIAYA_SPAREPART, p.getBiayaSparepart());
        v.put(COLUMN_PERAWATAN_BIAYA_JASA, p.getBiayaJasa());
        v.put(COLUMN_PERAWATAN_ONGKIR, p.getOngkir());
        v.put(COLUMN_PERAWATAN_CATATAN, p.getCatatan());
        return db.insert(TABLE_PERAWATAN, null, v);
    }

}
