package com.cs496.week2application;

import android.graphics.Bitmap;

import java.util.Base64;

public class ContactModel{
    private String name, phone;
    private String iconbase64;

    public String getIcon() {
        return this.iconbase64 ;
    }

    public void setIcon(String icon) {
        this.iconbase64 = icon ;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return phone;
    }

    public void setNumber(String number) {
        this.phone = number;
    }
}