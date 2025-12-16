package com.example.ropikos.model;

public class Perawatan {
    private int id;
    private String namaPerawatan;
    private String tanggal;
    private int idKamar;
    private double biayaSparepart;
    private double biayaJasa;
    private double ongkir;
    private String catatan;

    public Perawatan() {
    }

    // Constructor tanpa ID
    public Perawatan(String namaPerawatan, String tanggal, int idKamar, double biayaSparepart, double biayaJasa, double ongkir, String catatan) {
        this.namaPerawatan = namaPerawatan;
        this.tanggal = tanggal;
        this.idKamar = idKamar;
        this.biayaSparepart = biayaSparepart;
        this.biayaJasa = biayaJasa;
        this.ongkir = ongkir;
        this.catatan = catatan;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNamaPerawatan() { return namaPerawatan; }
    public void setNamaPerawatan(String namaPerawatan) { this.namaPerawatan = namaPerawatan; }

    public String getTanggal() { return tanggal; }
    public void setTanggal(String tanggal) { this.tanggal = tanggal; }

    public int getIdKamar() { return idKamar; }
    public void setIdKamar(int idKamar) { this.idKamar = idKamar; }

    public double getBiayaSparepart() { return biayaSparepart; }
    public void setBiayaSparepart(double biayaSparepart) { this.biayaSparepart = biayaSparepart; }

    public double getBiayaJasa() { return biayaJasa; }
    public void setBiayaJasa(double biayaJasa) { this.biayaJasa = biayaJasa; }

    public double getOngkir() { return ongkir; }
    public void setOngkir(double ongkir) { this.ongkir = ongkir; }

    public String getCatatan() { return catatan; }
    public void setCatatan(String catatan) { this.catatan = catatan; }
}