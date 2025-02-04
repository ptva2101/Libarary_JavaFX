package com.example.librarymanagementsystem2;

public class User {
    private String msv;
    private String name;
    private String phoneNumber;
    private String email;
    private String image;

    public User(String msv) {
        this.msv = msv;
    }

    public User(String msv, String name, String phoneNumber, String email, String image) {
        this.msv = msv;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.image = image;
    }

    public String getMsv() {
        return msv;
    }

    public void setMsv(String msv) {
        this.msv = msv;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}