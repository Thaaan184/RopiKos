package com.example.ropikos.model;

public class Penyewa {
    private int id;
    private String nama;
    private String whatsapp;
    private String jenisKelamin;
    private String deskripsi;
    private String fotoProfil; // Path atau URI gambar
    private String ktp;        // Nomor KTP
    private int idKamar;
    private int durasiSewa;    // Dalam bulan
    private String tglMulai;
    private String tglPembayaranBerikutnya;

    public Penyewa() {
    }

    // Constructor tanpa ID
    public Penyewa(String nama, String whatsapp, String jenisKelamin, String deskripsi, String fotoProfil, String ktp, int idKamar, int durasiSewa, String tglMulai, String tglPembayaranBerikutnya) {
        this.nama = nama;
        this.whatsapp = whatsapp;
        this.jenisKelamin = jenisKelamin;
        this.deskripsi = deskripsi;
        this.fotoProfil = fotoProfil;
        this.ktp = ktp;
        this.idKamar = idKamar;
        this.durasiSewa = durasiSewa;
        this.tglMulai = tglMulai;
        this.tglPembayaranBerikutnya = tglPembayaranBerikutnya;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNama() { return nama; }
    public void setNama(String nama) { this.nama = nama; }

    public String getWhatsapp() { return whatsapp; }
    public void setWhatsapp(String whatsapp) { this.whatsapp = whatsapp; }

    public String getJenisKelamin() { return jenisKelamin; }
    public void setJenisKelamin(String jenisKelamin) { this.jenisKelamin = jenisKelamin; }

    public String getDeskripsi() { return deskripsi; }
    public void setDeskripsi(String deskripsi) { this.deskripsi = deskripsi; }

    public String getFotoProfil() { return fotoProfil; }
    public void setFotoProfil(String fotoProfil) { this.fotoProfil = fotoProfil; }

    public String getKtp() { return ktp; }
    public void setKtp(String ktp) { this.ktp = ktp; }

    public int getIdKamar() { return idKamar; }
    public void setIdKamar(int idKamar) { this.idKamar = idKamar; }

    public int getDurasiSewa() { return durasiSewa; }
    public void setDurasiSewa(int durasiSewa) { this.durasiSewa = durasiSewa; }

    public String getTglMulai() { return tglMulai; }
    public void setTglMulai(String tglMulai) { this.tglMulai = tglMulai; }

    public String getTglPembayaranBerikutnya() { return tglPembayaranBerikutnya; }
    public void setTglPembayaranBerikutnya(String tglPembayaranBerikutnya) { this.tglPembayaranBerikutnya = tglPembayaranBerikutnya; }
}