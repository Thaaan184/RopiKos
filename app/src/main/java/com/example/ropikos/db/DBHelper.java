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

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "ropikos.db";
    private static final int DATABASE_VERSION = 3; // saat ini ke 3

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
                + COLUMN_USER_ADDRESS + " TEXT" + ");");

        db.execSQL("CREATE TABLE " + TABLE_KAMAR + " ("
                + COLUMN_KAMAR_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_KAMAR_JENIS_UNIT + " TEXT, "
                + COLUMN_KAMAR_NOMOR_UNIT + " TEXT, "
                + COLUMN_KAMAR_KETERANGAN + " TEXT, "
                + COLUMN_KAMAR_MAKS_PENYEWA + " INTEGER, "
                + COLUMN_KAMAR_HARGA_1BULAN + " REAL, "
                + COLUMN_KAMAR_HARGA_3BULAN + " REAL, "
                + COLUMN_KAMAR_HARGA_6BULAN + " REAL, "
                + COLUMN_KAMAR_STATUS + " INTEGER DEFAULT 0" + ");");

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
                + "FOREIGN KEY(" + COLUMN_PENYEWA_ID_KAMAR + ") REFERENCES " + TABLE_KAMAR + "(" + COLUMN_KAMAR_ID + ")" + ");");

        db.execSQL("CREATE TABLE " + TABLE_KEUANGAN + " ("
                + COLUMN_KEUANGAN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_KEUANGAN_TIPE + " TEXT, "
                + COLUMN_KEUANGAN_DESKRIPSI + " TEXT, "
                + COLUMN_KEUANGAN_NOMINAL + " REAL, "
                + COLUMN_KEUANGAN_TANGGAL + " TEXT" + ");");

        db.execSQL("CREATE TABLE " + TABLE_PERAWATAN + " ("
                + COLUMN_PERAWATAN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_PERAWATAN_NAMA + " TEXT, "
                + COLUMN_PERAWATAN_TANGGAL + " TEXT, "
                + COLUMN_PERAWATAN_ID_KAMAR + " INTEGER, "
                + COLUMN_PERAWATAN_BIAYA_SPAREPART + " REAL, "
                + COLUMN_PERAWATAN_BIAYA_JASA + " REAL, "
                + COLUMN_PERAWATAN_ONGKIR + " REAL, "
                + COLUMN_PERAWATAN_CATATAN + " TEXT, "
                + "FOREIGN KEY(" + COLUMN_PERAWATAN_ID_KAMAR + ") REFERENCES " + TABLE_KAMAR + "(" + COLUMN_KAMAR_ID + ")" + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_KAMAR);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PENYEWA);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_KEUANGAN);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PERAWATAN);
        onCreate(db);
    }

    // --- USER METHODS ---
    public User getUser(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USER, null, COLUMN_USER_USERNAME + "=?", new String[]{username}, null, null, null);
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

    // --- KAMAR METHODS ---

    // Insert Kamar
    public long insertKamar(Kamar kamar) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_KAMAR_JENIS_UNIT, kamar.getJenisUnit());
        values.put(COLUMN_KAMAR_NOMOR_UNIT, kamar.getNomorUnit());
        values.put(COLUMN_KAMAR_KETERANGAN, kamar.getKeterangan());
        values.put(COLUMN_KAMAR_MAKS_PENYEWA, kamar.getMaksPenyewa());
        values.put(COLUMN_KAMAR_HARGA_1BULAN, kamar.getHarga1Bulan());
        values.put(COLUMN_KAMAR_HARGA_3BULAN, kamar.getHarga3Bulan());
        values.put(COLUMN_KAMAR_HARGA_6BULAN, kamar.getHarga6Bulan());
        values.put(COLUMN_KAMAR_STATUS, kamar.getStatus());
        return db.insert(TABLE_KAMAR, null, values);
    }

    // Get All Kamar
    public List<Kamar> getAllKamar() {
        List<Kamar> kamarList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_KAMAR, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                Kamar kamar = new Kamar();
                kamar.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_KAMAR_ID)));
                kamar.setJenisUnit(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_KAMAR_JENIS_UNIT)));
                kamar.setNomorUnit(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_KAMAR_NOMOR_UNIT)));
                kamar.setKeterangan(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_KAMAR_KETERANGAN)));
                kamar.setMaksPenyewa(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_KAMAR_MAKS_PENYEWA)));
                kamar.setHarga1Bulan(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_KAMAR_HARGA_1BULAN)));
                kamar.setHarga3Bulan(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_KAMAR_HARGA_3BULAN)));
                kamar.setHarga6Bulan(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_KAMAR_HARGA_6BULAN)));
                kamar.setStatus(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_KAMAR_STATUS)));
                kamarList.add(kamar);
            } while (cursor.moveToNext());
        }
        if (cursor != null) cursor.close();
        return kamarList;
    }

    // Get 1 Kamar by ID
    public Kamar getKamar(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_KAMAR, null, COLUMN_KAMAR_ID + "=?", new String[]{String.valueOf(id)}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            Kamar kamar = new Kamar();
            kamar.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_KAMAR_ID)));
            kamar.setJenisUnit(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_KAMAR_JENIS_UNIT)));
            kamar.setNomorUnit(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_KAMAR_NOMOR_UNIT)));
            kamar.setKeterangan(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_KAMAR_KETERANGAN)));
            kamar.setMaksPenyewa(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_KAMAR_MAKS_PENYEWA)));
            kamar.setHarga1Bulan(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_KAMAR_HARGA_1BULAN)));
            kamar.setHarga3Bulan(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_KAMAR_HARGA_3BULAN)));
            kamar.setHarga6Bulan(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_KAMAR_HARGA_6BULAN)));
            kamar.setStatus(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_KAMAR_STATUS)));
            cursor.close();
            return kamar;
        }
        return null;
    }

    // Ambil semua kamar yang statusnya Kosong (0)
    public List<Kamar> getKamarTersedia() {
        List<Kamar> kamarList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        // Query: SELECT * FROM kamar WHERE status = 0
        Cursor cursor = db.query(TABLE_KAMAR, null, COLUMN_KAMAR_STATUS + "=?", new String[]{"0"}, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                Kamar kamar = new Kamar();
                kamar.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_KAMAR_ID)));
                kamar.setJenisUnit(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_KAMAR_JENIS_UNIT)));
                kamar.setNomorUnit(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_KAMAR_NOMOR_UNIT)));
                kamar.setKeterangan(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_KAMAR_KETERANGAN)));
                kamar.setMaksPenyewa(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_KAMAR_MAKS_PENYEWA)));
                kamar.setHarga1Bulan(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_KAMAR_HARGA_1BULAN)));
                kamar.setHarga3Bulan(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_KAMAR_HARGA_3BULAN)));
                kamar.setHarga6Bulan(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_KAMAR_HARGA_6BULAN)));
                kamar.setStatus(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_KAMAR_STATUS)));
                kamarList.add(kamar);
            } while (cursor.moveToNext());
        }
        if (cursor != null) cursor.close();
        return kamarList;
    }

    // Update Kamar
    public int updateKamar(Kamar kamar) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_KAMAR_JENIS_UNIT, kamar.getJenisUnit());
        values.put(COLUMN_KAMAR_NOMOR_UNIT, kamar.getNomorUnit());
        values.put(COLUMN_KAMAR_KETERANGAN, kamar.getKeterangan());
        values.put(COLUMN_KAMAR_MAKS_PENYEWA, kamar.getMaksPenyewa());
        values.put(COLUMN_KAMAR_HARGA_1BULAN, kamar.getHarga1Bulan());
        values.put(COLUMN_KAMAR_HARGA_3BULAN, kamar.getHarga3Bulan());
        values.put(COLUMN_KAMAR_HARGA_6BULAN, kamar.getHarga6Bulan());
        // Status biasanya diupdate otomatis oleh sistem sewa, tapi bisa manual juga
        return db.update(TABLE_KAMAR, values, COLUMN_KAMAR_ID + " = ?", new String[]{String.valueOf(kamar.getId())});
    }

    // Delete Kamar
    public void deleteKamar(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_KAMAR, COLUMN_KAMAR_ID + " = ?", new String[]{String.valueOf(id)});
    }

    // --- PENYEWA METHODS ---

    // Insert Penyewa
    public long insertPenyewa(Penyewa penyewa) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PENYEWA_NAMA, penyewa.getNama());
        values.put(COLUMN_PENYEWA_WHATSAPP, penyewa.getWhatsapp());
        values.put(COLUMN_PENYEWA_JENIS_KELAMIN, penyewa.getJenisKelamin());
        values.put(COLUMN_PENYEWA_DESKRIPSI, penyewa.getDeskripsi());
        values.put(COLUMN_PENYEWA_FOTO_PROFIL, penyewa.getFotoProfil());
        values.put(COLUMN_PENYEWA_KTP, penyewa.getKtp());
        values.put(COLUMN_PENYEWA_ID_KAMAR, penyewa.getIdKamar());
        values.put(COLUMN_PENYEWA_DURASI_SEWA, penyewa.getDurasiSewa());
        values.put(COLUMN_PENYEWA_TGL_MULAI, penyewa.getTglMulai());
        values.put(COLUMN_PENYEWA_TGL_PEMBAYARAN_BERIKUTNYA, penyewa.getTglPembayaranBerikutnya());
        return db.insert(TABLE_PENYEWA, null, values);
    }

    // Get All Penyewa
    public List<Penyewa> getAllPenyewa() {
        List<Penyewa> penyewaList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_PENYEWA, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                Penyewa penyewa = new Penyewa();
                penyewa.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PENYEWA_ID)));
                penyewa.setNama(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PENYEWA_NAMA)));
                penyewa.setWhatsapp(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PENYEWA_WHATSAPP)));
                penyewa.setFotoProfil(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PENYEWA_FOTO_PROFIL)));
                penyewa.setKtp(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PENYEWA_KTP)));
                penyewa.setIdKamar(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PENYEWA_ID_KAMAR)));
                penyewa.setDurasiSewa(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PENYEWA_DURASI_SEWA)));
                penyewa.setTglMulai(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PENYEWA_TGL_MULAI)));
                penyewa.setTglPembayaranBerikutnya(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PENYEWA_TGL_PEMBAYARAN_BERIKUTNYA)));
                penyewaList.add(penyewa);
            } while (cursor.moveToNext());
        }
        if (cursor != null) cursor.close();
        return penyewaList;
    }

    // Get 1 Penyewa by ID
    public Penyewa getPenyewa(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_PENYEWA, null, COLUMN_PENYEWA_ID + "=?", new String[]{String.valueOf(id)}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            Penyewa penyewa = new Penyewa();
            penyewa.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PENYEWA_ID)));
            penyewa.setNama(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PENYEWA_NAMA)));
            penyewa.setWhatsapp(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PENYEWA_WHATSAPP)));
            penyewa.setFotoProfil(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PENYEWA_FOTO_PROFIL)));
            penyewa.setKtp(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PENYEWA_KTP)));
            penyewa.setIdKamar(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PENYEWA_ID_KAMAR)));
            penyewa.setDurasiSewa(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PENYEWA_DURASI_SEWA)));
            penyewa.setTglMulai(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PENYEWA_TGL_MULAI)));
            penyewa.setTglPembayaranBerikutnya(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PENYEWA_TGL_PEMBAYARAN_BERIKUTNYA)));
            cursor.close();
            return penyewa;
        }
        return null;
    }

    // Update Penyewa
    public int updatePenyewa(Penyewa penyewa) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PENYEWA_NAMA, penyewa.getNama());
        values.put(COLUMN_PENYEWA_WHATSAPP, penyewa.getWhatsapp());
        // Tambahkan field lain jika diperlukan edit foto dll
        return db.update(TABLE_PENYEWA, values, COLUMN_PENYEWA_ID + " = ?", new String[]{String.valueOf(penyewa.getId())});
    }

    // Delete Penyewa
    public void deletePenyewa(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PENYEWA, COLUMN_PENYEWA_ID + " = ?", new String[]{String.valueOf(id)});
    }

    // --- KEUANGAN METHODS ---
    public List<Keuangan> getAllKeuangan() {
        List<Keuangan> keuanganList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_KEUANGAN, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                Keuangan keuangan = new Keuangan();
                keuangan.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_KEUANGAN_ID)));
                keuangan.setTipe(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_KEUANGAN_TIPE)));
                keuangan.setDeskripsi(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_KEUANGAN_DESKRIPSI)));
                keuangan.setNominal(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_KEUANGAN_NOMINAL)));
                keuangan.setTanggal(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_KEUANGAN_TANGGAL)));
                keuanganList.add(keuangan);
            } while (cursor.moveToNext());
        }
        if (cursor != null) cursor.close();
        return keuanganList;
    }

    // --- STATISTIK ---
    public int getTotalKamar() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_KAMAR, null);
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    public int getTotalPenyewa() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_PENYEWA, null);
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    public int getTotalLunas() { return getTotalPenyewa(); } // Logic dummy

    public int getTotalPerbaikan() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_PERAWATAN, null);
        int count = 0;
        if (cursor.moveToFirst()) count = cursor.getInt(0);
        cursor.close();
        return count;
    }

    public double getPendapatanBulanIni() { return 0; }
}