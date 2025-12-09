package com.example.ropikos.model;

public class Keuangan {
    private int id;
    private String tipe;      // "Pemasukan" atau "Pengeluaran"
    private String deskripsi;
    private double nominal;
    private String tanggal;

    public Keuangan() {
    }

    // Constructor tanpa ID
    public Keuangan(String tipe, String deskripsi, double nominal, String tanggal) {
        this.tipe = tipe;
        this.deskripsi = deskripsi;
        this.nominal = nominal;
        this.tanggal = tanggal;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTipe() { return tipe; }
    public void setTipe(String tipe) { this.tipe = tipe; }

    public String getDeskripsi() { return deskripsi; }
    public void setDeskripsi(String deskripsi) { this.deskripsi = deskripsi; }

    public double getNominal() { return nominal; }
    public void setNominal(double nominal) { this.nominal = nominal; }

    public String getTanggal() { return tanggal; }
    public void setTanggal(String tanggal) { this.tanggal = tanggal; }
}