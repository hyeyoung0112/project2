package com.cs496.week2application;

import android.graphics.Bitmap;

public class ContactModel{
    private String name, phone;
    private Bitmap icon;

    public Bitmap getIcon() {
        return this.icon ;
    }

    public void setIcon(Bitmap icon) {
        this.icon = icon ;
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