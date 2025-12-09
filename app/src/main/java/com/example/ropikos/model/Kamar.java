package com.example.ropikos.model;

public class Kamar {
    private int id;
    private String jenisUnit;
    private String nomorUnit;
    private String keterangan;
    private int maksPenyewa;
    private double harga1Bulan;
    private double harga3Bulan;
    private double harga6Bulan;
    private int status; // 0 = Kosong, 1 = Terisi

    public Kamar() {
    }

    // Constructor tanpa ID (untuk Insert)
    public Kamar(String jenisUnit, String nomorUnit, String keterangan, int maksPenyewa, double harga1Bulan, double harga3Bulan, double harga6Bulan, int status) {
        this.jenisUnit = jenisUnit;
        this.nomorUnit = nomorUnit;
        this.keterangan = keterangan;
        this.maksPenyewa = maksPenyewa;
        this.harga1Bulan = harga1Bulan;
        this.harga3Bulan = harga3Bulan;
        this.harga6Bulan = harga6Bulan;
        this.status = status;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getJenisUnit() { return jenisUnit; }
    public void setJenisUnit(String jenisUnit) { this.jenisUnit = jenisUnit; }

    public String getNomorUnit() { return nomorUnit; }
    public void setNomorUnit(String nomorUnit) { this.nomorUnit = nomorUnit; }

    public String getKeterangan() { return keterangan; }
    public void setKeterangan(String keterangan) { this.keterangan = keterangan; }

    public int getMaksPenyewa() { return maksPenyewa; }
    public void setMaksPenyewa(int maksPenyewa) { this.maksPenyewa = maksPenyewa; }

    public double getHarga1Bulan() { return harga1Bulan; }
    public void setHarga1Bulan(double harga1Bulan) { this.harga1Bulan = harga1Bulan; }

    public double getHarga3Bulan() { return harga3Bulan; }
    public void setHarga3Bulan(double harga3Bulan) { this.harga3Bulan = harga3Bulan; }

    public double getHarga6Bulan() { return harga6Bulan; }
    public void setHarga6Bulan(double harga6Bulan) { this.harga6Bulan = harga6Bulan; }

    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }
}