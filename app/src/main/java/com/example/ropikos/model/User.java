package com.example.ropikos.model;

public class User {
    private int id;
    private String fullname;
    private String username;
    private String password;
    private String phoneNumber; // Sesuai dengan cursor.getColumnIndex(COLUMN_USER_PHONE)
    private String address;

    // Constructor Kosong
    public User() {
    }

    // Constructor untuk Register (tanpa ID, Phone, Address)
    public User(String fullname, String username, String password) {
        this.fullname = fullname;
        this.username = username;
        this.password = password;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getFullname() { return fullname; }
    public void setFullname(String fullname) { this.fullname = fullname; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
}