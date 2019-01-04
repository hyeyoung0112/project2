package com.cs496.week2application;

import android.graphics.Bitmap;

public class ContactServerModel {
    private String name, phone;
    private String iconBase64;

    public String getIcon() {
        return this.iconBase64 ;
    }

    public void setIcon(String iconStr) {
        this.iconBase64 = iconStr;
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
