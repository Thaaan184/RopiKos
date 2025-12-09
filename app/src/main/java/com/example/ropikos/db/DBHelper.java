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
    // Naikkan versi database karena ada penambahan tabel baru
    private static final int DATABASE_VERSION = 2;

    // --- TABEL AKUN / USER (BARU) ---
    public static final String TABLE_USER = "users";
    public static final String COLUMN_USER_ID = "id";
    public static final String COLUMN_USER_FULLNAME = "fullname"; // Dari register & edit profile
    public static final String COLUMN_USER_USERNAME = "username"; // Dari register & login
    public static final String COLUMN_USER_PASSWORD = "password"; // Dari register & login
    public static final String COLUMN_USER_PHONE = "phone";       // Dari edit profile
    public static final String COLUMN_USER_ADDRESS = "address";   // Dari edit profile

    // Tabel Kamar
    public static final String TABLE_KAMAR = "kamar";
    public static final String COLUMN_KAMAR_ID = "id";
    public static final String COLUMN_KAMAR_JENIS_UNIT = "jenis_unit";
    public static final String COLUMN_KAMAR_NOMOR_UNIT = "nomor_unit";
    public static final String COLUMN_KAMAR_KETERANGAN = "keterangan";
    public static final String COLUMN_KAMAR_MAKS_PENYEWA = "maks_penyewa";
    public static final String COLUMN_KAMAR_HARGA_1BULAN = "harga_1bulan";
    public static final String COLUMN_KAMAR_HARGA_3BULAN = "harga_3bulan";
    public static final String COLUMN_KAMAR_HARGA_6BULAN = "harga_6bulan";
    public static final String COLUMN_KAMAR_STATUS = "status";

    // Tabel Penyewa
    public static final String TABLE_PENYEWA = "penyewa";
    public static final String COLUMN_PENYEWA_ID = "id";
    public static final String COLUMN_PENYEWA_NAMA = "nama";
    public static final String COLUMN_PENYEWA_WHATSAPP = "whatsapp";
    public static final String COLUMN_PENYEWA_FOTO_PROFIL = "foto_profil";
    public static final String COLUMN_PENYEWA_KTP = "ktp";
    public static final String COLUMN_PENYEWA_ID_KAMAR = "id_kamar";
    public static final String COLUMN_PENYEWA_DURASI_SEWA = "durasi_sewa";
    public static final String COLUMN_PENYEWA_TGL_MULAI = "tgl_mulai";
    public static final String COLUMN_PENYEWA_TGL_PEMBAYARAN_BERIKUTNYA = "tgl_pembayaran_berikutnya";

    // Tabel Keuangan
    public static final String TABLE_KEUANGAN = "keuangan";
    public static final String COLUMN_KEUANGAN_ID = "id";
    public static final String COLUMN_KEUANGAN_TIPE = "tipe";
    public static final String COLUMN_KEUANGAN_DESKRIPSI = "deskripsi";
    public static final String COLUMN_KEUANGAN_NOMINAL = "nominal";
    public static final String COLUMN_KEUANGAN_TANGGAL = "tanggal";

    // Tabel Perawatan
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
        // Create User Table (NEW)
        db.execSQL("CREATE TABLE " + TABLE_USER + " ("
                + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_USER_FULLNAME + " TEXT, "
                + COLUMN_USER_USERNAME + " TEXT UNIQUE, " // Username harus unik
                + COLUMN_USER_PASSWORD + " TEXT, "
                + COLUMN_USER_PHONE + " TEXT, "
                + COLUMN_USER_ADDRESS + " TEXT"
                + ");");

        // Create Kamar table
        db.execSQL("CREATE TABLE " + TABLE_KAMAR + " ("
                + COLUMN_KAMAR_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_KAMAR_JENIS_UNIT + " TEXT, "
                + COLUMN_KAMAR_NOMOR_UNIT + " TEXT, "
                + COLUMN_KAMAR_KETERANGAN + " TEXT, "
                + COLUMN_KAMAR_MAKS_PENYEWA + " INTEGER, "
                + COLUMN_KAMAR_HARGA_1BULAN + " REAL, "
                + COLUMN_KAMAR_HARGA_3BULAN + " REAL, "
                + COLUMN_KAMAR_HARGA_6BULAN + " REAL, "
                + COLUMN_KAMAR_STATUS + " INTEGER DEFAULT 0"
                + ");");

        // Create Penyewa table
        db.execSQL("CREATE TABLE " + TABLE_PENYEWA + " ("
                + COLUMN_PENYEWA_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_PENYEWA_NAMA + " TEXT, "
                + COLUMN_PENYEWA_WHATSAPP + " TEXT, "
                + COLUMN_PENYEWA_FOTO_PROFIL + " TEXT, "
                + COLUMN_PENYEWA_KTP + " TEXT, "
                + COLUMN_PENYEWA_ID_KAMAR + " INTEGER, "
                + COLUMN_PENYEWA_DURASI_SEWA + " INTEGER, "
                + COLUMN_PENYEWA_TGL_MULAI + " TEXT, "
                + COLUMN_PENYEWA_TGL_PEMBAYARAN_BERIKUTNYA + " TEXT, "
                + "FOREIGN KEY(" + COLUMN_PENYEWA_ID_KAMAR + ") REFERENCES " + TABLE_KAMAR + "(" + COLUMN_KAMAR_ID + ")"
                + ");");

        // Create Keuangan table
        db.execSQL("CREATE TABLE " + TABLE_KEUANGAN + " ("
                + COLUMN_KEUANGAN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_KEUANGAN_TIPE + " TEXT, "
                + COLUMN_KEUANGAN_DESKRIPSI + " TEXT, "
                + COLUMN_KEUANGAN_NOMINAL + " REAL, "
                + COLUMN_KEUANGAN_TANGGAL + " TEXT"
                + ");");

        // Create Perawatan table
        db.execSQL("CREATE TABLE " + TABLE_PERAWATAN + " ("
                + COLUMN_PERAWATAN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_PERAWATAN_NAMA + " TEXT, "
                + COLUMN_PERAWATAN_TANGGAL + " TEXT, "
                + COLUMN_PERAWATAN_ID_KAMAR + " INTEGER, "
                + COLUMN_PERAWATAN_BIAYA_SPAREPART + " REAL, "
                + COLUMN_PERAWATAN_BIAYA_JASA + " REAL, "
                + COLUMN_PERAWATAN_ONGKIR + " REAL, "
                + COLUMN_PERAWATAN_CATATAN + " TEXT, "
                + "FOREIGN KEY(" + COLUMN_PERAWATAN_ID_KAMAR + ") REFERENCES " + TABLE_KAMAR + "(" + COLUMN_KAMAR_ID + ")"
                + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop existing tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_KAMAR);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PENYEWA);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_KEUANGAN);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PERAWATAN);
        onCreate(db);
    }

    // ==========================================
    // METHODS UNTUK AUTH & USER (LOGIN/REGISTER)
    // ==========================================

    /**
     * Register User Baru (Dari activity_register.xml)
     * Hanya menyimpan Fullname, Username, dan Password.
     * Phone dan Address dibiarkan kosong dulu (update di edit profile).
     */
    public long registerUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_FULLNAME, user.getFullname());
        values.put(COLUMN_USER_USERNAME, user.getUsername());
        values.put(COLUMN_USER_PASSWORD, user.getPassword());
        // Phone & Address default null atau kosong saat register awal
        values.put(COLUMN_USER_PHONE, "");
        values.put(COLUMN_USER_ADDRESS, "");

        long result = -1;
        try {
            result = db.insert(TABLE_USER, null, values);
        } catch (Exception e) {
            // Kemungkinan error duplicate username karena constraint UNIQUE
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Cek Login (Dari activity_login.xml)
     * Mengembalikan true jika username dan password cocok.
     */
    public boolean checkLogin(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USER,
                new String[]{COLUMN_USER_ID},
                COLUMN_USER_USERNAME + "=? AND " + COLUMN_USER_PASSWORD + "=?",
                new String[]{username, password},
                null, null, null);

        int count = cursor.getCount();
        cursor.close();
        return count > 0;
    }

    /**
     * Ambil Data User Berdasarkan Username
     * Digunakan untuk menampilkan data di ProfileActivity & EditProfileActivity
     */
    public User getUser(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USER, null,
                COLUMN_USER_USERNAME + "=?",
                new String[]{username}, null, null, null);

        User user = null;
        if (cursor != null && cursor.moveToFirst()) {
            user = new User();
            // GANTI getColumnIndex -> getColumnIndexOrThrow
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

    /**
     * Update Profil (Dari activity_edit_profile.xml)
     * Mengupdate Nama, Telepon, dan Alamat berdasarkan Username
     */
    public int updateProfile(String username, String newName, String newPhone, String newAddress) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_FULLNAME, newName);
        values.put(COLUMN_USER_PHONE, newPhone);
        values.put(COLUMN_USER_ADDRESS, newAddress);

        return db.update(TABLE_USER, values, COLUMN_USER_USERNAME + " = ?", new String[]{username});
    }

    /**
     * Ganti Password (Dari activity_edit_profile.xml)
     */
    public boolean changePassword(String username, String oldPassword, String newPassword) {
        // Cek dulu apakah password lama benar
        if (checkLogin(username, oldPassword)) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(COLUMN_USER_PASSWORD, newPassword);
            db.update(TABLE_USER, values, COLUMN_USER_USERNAME + " = ?", new String[]{username});
            return true;
        }
        return false;
    }

    // ==========================================
    // METHODS LAINNYA (KAMAR, PENYEWA, DLL)
    // ==========================================

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

    public List<Kamar> getAllKamar() {
        List<Kamar> kamarList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_KAMAR, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                Kamar kamar = new Kamar();
                // GANTI getColumnIndex -> getColumnIndexOrThrow
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

    public long insertPenyewa(Penyewa penyewa) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PENYEWA_NAMA, penyewa.getNama());
        values.put(COLUMN_PENYEWA_WHATSAPP, penyewa.getWhatsapp());
        values.put(COLUMN_PENYEWA_FOTO_PROFIL, penyewa.getFotoProfil());
        values.put(COLUMN_PENYEWA_KTP, penyewa.getKtp());
        values.put(COLUMN_PENYEWA_ID_KAMAR, penyewa.getIdKamar());
        values.put(COLUMN_PENYEWA_DURASI_SEWA, penyewa.getDurasiSewa());
        values.put(COLUMN_PENYEWA_TGL_MULAI, penyewa.getTglMulai());
        values.put(COLUMN_PENYEWA_TGL_PEMBAYARAN_BERIKUTNYA, penyewa.getTglPembayaranBerikutnya());
        return db.insert(TABLE_PENYEWA, null, values);
    }

    public List<Penyewa> getAllPenyewa() {
        List<Penyewa> penyewaList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_PENYEWA, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                Penyewa penyewa = new Penyewa();
                // GANTI getColumnIndex -> getColumnIndexOrThrow
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
    public long insertKeuangan(Keuangan keuangan) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_KEUANGAN_TIPE, keuangan.getTipe());
        values.put(COLUMN_KEUANGAN_DESKRIPSI, keuangan.getDeskripsi());
        values.put(COLUMN_KEUANGAN_NOMINAL, keuangan.getNominal());
        values.put(COLUMN_KEUANGAN_TANGGAL, keuangan.getTanggal());
        return db.insert(TABLE_KEUANGAN, null, values);
    }

    public List<Keuangan> getAllKeuangan() {
        List<Keuangan> keuanganList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_KEUANGAN, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                Keuangan keuangan = new Keuangan();
                // GANTI getColumnIndex -> getColumnIndexOrThrow
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

    public long insertPerawatan(Perawatan perawatan) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PERAWATAN_NAMA, perawatan.getNamaPerawatan());
        values.put(COLUMN_PERAWATAN_TANGGAL, perawatan.getTanggal());
        values.put(COLUMN_PERAWATAN_ID_KAMAR, perawatan.getIdKamar());
        values.put(COLUMN_PERAWATAN_BIAYA_SPAREPART, perawatan.getBiayaSparepart());
        values.put(COLUMN_PERAWATAN_BIAYA_JASA, perawatan.getBiayaJasa());
        values.put(COLUMN_PERAWATAN_ONGKIR, perawatan.getOngkir());
        values.put(COLUMN_PERAWATAN_CATATAN, perawatan.getCatatan());
        return db.insert(TABLE_PERAWATAN, null, values);
    }

    public List<Perawatan> getAllPerawatan() {
        List<Perawatan> perawatanList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_PERAWATAN, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                Perawatan perawatan = new Perawatan();
                // GANTI getColumnIndex -> getColumnIndexOrThrow
                perawatan.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PERAWATAN_ID)));
                perawatan.setNamaPerawatan(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PERAWATAN_NAMA)));
                perawatan.setTanggal(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PERAWATAN_TANGGAL)));
                perawatan.setIdKamar(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PERAWATAN_ID_KAMAR)));
                perawatan.setBiayaSparepart(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PERAWATAN_BIAYA_SPAREPART)));
                perawatan.setBiayaJasa(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PERAWATAN_BIAYA_JASA)));
                perawatan.setOngkir(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PERAWATAN_ONGKIR)));
                perawatan.setCatatan(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PERAWATAN_CATATAN)));
                perawatanList.add(perawatan);
            } while (cursor.moveToNext());
        }
        if (cursor != null) cursor.close();
        return perawatanList;
    }

    // Statistik Sederhana
    public int getTotalKamar() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_KAMAR, null);
        int count = 0;
        if(cursor.moveToFirst()){
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    public int getTotalPenyewa() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_PENYEWA, null);
        int count = 0;
        if(cursor.moveToFirst()){
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    public int getTotalLunas() {
        // Placeholder: Logika bisnis pelunasan bisa dikembangkan di sini
        return getTotalPenyewa();
    }

    public int getTotalPerbaikan() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_PERAWATAN, null);
        int count = 0;
        if(cursor.moveToFirst()){
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    public double getPendapatanBulanIni() {
        // Placeholder: Perlu filter tanggal (strftime) untuk implementasi real
        return 0;
    }
}