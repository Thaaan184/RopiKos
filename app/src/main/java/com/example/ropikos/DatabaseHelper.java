package com.example.ropikos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "RopiKost.db";
    private static final int DATABASE_VERSION = 3; // dinaikkan karena update tabel

    // ------------------------- TABEL KAMAR -------------------------
    private static final String TABLE_KAMAR = "kamar";
    private static final String COL_ID = "id";
    private static final String COL_NOMOR_KAMAR = "nomor_kamar";
    private static final String COL_NAMA_KAMAR = "nama_kamar";
    private static final String COL_JENIS = "jenis_kamar";
    private static final String COL_KETERANGAN = "keterangan";
    private static final String COL_HARGA = "harga_1bln";
    private static final String COL_HARGA3 = "harga_3bln";
    private static final String COL_HARGA6 = "harga_6bln";
    private static final String COL_STATUS_KAMAR = "status";

    // ------------------------- TABEL PENYEWA ------------------------
    private static final String TABLE_PENYEWA = "penyewa";
    private static final String COL_NAMA_PENYEWA = "nama_penyewa";
    private static final String COL_HP = "no_hp";
    private static final String COL_KAMAR_ID = "kamar_id";

    // --------------------- TABEL PEMBAYARAN ------------------------
    private static final String TABLE_PEMBAYARAN = "pembayaran";
    private static final String COL_JUMLAH_BAYAR = "jumlah_bayar";
    private static final String COL_TANGGAL_BAYAR = "tanggal_bayar";
    private static final String COL_STATUS_BAYAR = "status_bayar";

    // --------------------- TABEL MAINTENANCE ------------------------
    private static final String TABLE_MAINTENANCE = "maintenance";
    private static final String COL_BIAYA_MAINTENANCE = "biaya";
    private static final String COL_TANGGAL_MAINTENANCE = "tanggal";
    private static final String COL_NAMA_MAINTENANCE = "nama_perawatan";
    private static final String COL_CATATAN = "catatan";

    // --------------------- TABEL USER ------------------------
    private static final String TABLE_USER = "users";
    private static final String COL_USER_ID = "id";
    private static final String COL_USER_FULLNAME = "fullname";
    private static final String COL_USER_USERNAME = "username";
    private static final String COL_USER_PASSWORD = "password";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // ========================= CREATE TABLES =========================
    @Override
    public void onCreate(SQLiteDatabase db) {

        // TABLE KAMAR
        String createKamar = "CREATE TABLE " + TABLE_KAMAR + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_NOMOR_KAMAR + " TEXT, " +
                COL_NAMA_KAMAR + " TEXT, " +
                COL_JENIS + " TEXT, " +
                COL_KETERANGAN + " TEXT, " +
                COL_HARGA + " REAL, " +
                COL_HARGA3 + " REAL, " +
                COL_HARGA6 + " REAL, " +
                COL_STATUS_KAMAR + " TEXT)";
        db.execSQL(createKamar);

        // TABLE PENYEWA
        String createPenyewa = "CREATE TABLE " + TABLE_PENYEWA + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_NAMA_PENYEWA + " TEXT, " +
                COL_HP + " TEXT, " +
                COL_KAMAR_ID + " INTEGER)";
        db.execSQL(createPenyewa);

        // TABLE PEMBAYARAN
        String createPembayaran = "CREATE TABLE " + TABLE_PEMBAYARAN + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_KAMAR_ID + " INTEGER, " +
                COL_JUMLAH_BAYAR + " REAL, " +
                COL_TANGGAL_BAYAR + " TEXT, " +
                COL_STATUS_BAYAR + " TEXT)";
        db.execSQL(createPembayaran);

        // TABLE MAINTENANCE
        String createMaintenance = "CREATE TABLE " + TABLE_MAINTENANCE + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_NAMA_MAINTENANCE + " TEXT, " +
                COL_TANGGAL_MAINTENANCE + " TEXT, " +
                COL_BIAYA_MAINTENANCE + " REAL, " +
                COL_CATATAN + " TEXT)";
        db.execSQL(createMaintenance);

        // TABLE USER
        String createUser = "CREATE TABLE " + TABLE_USER + " (" +
                COL_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_USER_FULLNAME + " TEXT, " +
                COL_USER_USERNAME + " TEXT, " +
                COL_USER_PASSWORD + " TEXT)";
        db.execSQL(createUser);
    }

    // ========================= ON UPGRADE =============================
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_KAMAR);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PENYEWA);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PEMBAYARAN);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MAINTENANCE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        onCreate(db);
    }

    // ====================== REGISTER ============================
    public boolean registerUser(String fullname, String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();

        if (checkUsername(username)) return false;

        ContentValues values = new ContentValues();
        values.put(COL_USER_FULLNAME, fullname);
        values.put(COL_USER_USERNAME, username);
        values.put(COL_USER_PASSWORD, password);

        return db.insert(TABLE_USER, null, values) != -1;
    }

    public boolean checkUsername(String username) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_USER + " WHERE " + COL_USER_USERNAME + " = ?",
                new String[]{username});

        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public boolean checkLogin(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_USER +
                        " WHERE " + COL_USER_USERNAME + " = ? AND " +
                        COL_USER_PASSWORD + " = ?",
                new String[]{username, password}
        );

        boolean success = cursor.getCount() > 0;
        cursor.close();
        return success;
    }

    // ====================== CRUD KAMAR ============================

    public boolean addKamar(String nama, String nomor, String jenis, String keterangan,
                            double harga1, double harga3, double harga6) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COL_NAMA_KAMAR, nama);
        values.put(COL_NOMOR_KAMAR, nomor);
        values.put(COL_JENIS, jenis);
        values.put(COL_KETERANGAN, keterangan);
        values.put(COL_HARGA, harga1);
        values.put(COL_HARGA3, harga3);
        values.put(COL_HARGA6, harga6);
        values.put(COL_STATUS_KAMAR, "Tersedia");

        return db.insert(TABLE_KAMAR, null, values) != -1;
    }

    public ArrayList<HashMap<String, String>> getAllKamar() {
        ArrayList<HashMap<String, String>> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_KAMAR, null);

        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<>();
                map.put("id", cursor.getString(cursor.getColumnIndexOrThrow(COL_ID)));
                map.put("nama", cursor.getString(cursor.getColumnIndexOrThrow(COL_NAMA_KAMAR)));
                map.put("nomor", cursor.getString(cursor.getColumnIndexOrThrow(COL_NOMOR_KAMAR)));
                map.put("status", cursor.getString(cursor.getColumnIndexOrThrow(COL_STATUS_KAMAR)));
                list.add(map);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public Cursor getKamarDetail(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(
                "SELECT * FROM " + TABLE_KAMAR + " WHERE " + COL_ID + "=?",
                new String[]{String.valueOf(id)}
        );
    }

    public boolean updateKamar(int id, String nama, String nomor, double harga) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COL_NAMA_KAMAR, nama);
        values.put(COL_NOMOR_KAMAR, nomor);
        values.put(COL_HARGA, harga);

        return db.update(TABLE_KAMAR, values, COL_ID + "=?",
                new String[]{String.valueOf(id)}) > 0;
    }

    // --------------------------- DELETE KAMAR (FR-08) ---------------------------
    public boolean deleteKamar(int id) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Hapus baris kamar berdasarkan ID
        int result = db.delete(TABLE_KAMAR, COL_ID + " = ?", new String[]{String.valueOf(id)});

        return result > 0;  // true jika ada baris yang terhapus
    }

    // ====================== MAINTENANCE ============================

    public boolean addMaintenance(String nama, String tanggal, double biaya, String catatan) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COL_NAMA_MAINTENANCE, nama);
        values.put(COL_TANGGAL_MAINTENANCE, tanggal);
        values.put(COL_BIAYA_MAINTENANCE, biaya);
        values.put(COL_CATATAN, catatan);

        return db.insert(TABLE_MAINTENANCE, null, values) != -1;
    }

    // ====================== DASHBOARD ============================

    public int getTotalKamar() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_KAMAR, null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        return count;
    }

    public int getTotalPenyewa() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_PENYEWA, null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        return count;
    }

    public int getTotalLunasBulanIni() {
        String currentMonth = new SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(new Date());
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM " + TABLE_PEMBAYARAN +
                        " WHERE " + COL_STATUS_BAYAR + "='Lunas' AND " +
                        COL_TANGGAL_BAYAR + " LIKE ?",
                new String[]{currentMonth + "%"}
        );

        cursor.moveToFirst();
        int total = cursor.getInt(0);
        cursor.close();
        return total;
    }

    public double getPendapatanBulanIni() {
        String currentMonth = new SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(new Date());
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT SUM(" + COL_JUMLAH_BAYAR + ") FROM " + TABLE_PEMBAYARAN +
                        " WHERE " + COL_STATUS_BAYAR + "='Lunas' AND " +
                        COL_TANGGAL_BAYAR + " LIKE ?",
                new String[]{currentMonth + "%"}
        );

        cursor.moveToFirst();
        double total = cursor.getDouble(0);
        cursor.close();
        return total;
    }

    public int getTotalPerbaikan() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_MAINTENANCE, null);
        cursor.moveToFirst();
        int total = cursor.getInt(0);
        cursor.close();
        return total;
    }
}
