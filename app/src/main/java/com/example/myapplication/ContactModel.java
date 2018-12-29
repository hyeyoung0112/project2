package com.example.myapplication;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;

public class ContactModel{
    private String name, number;
    private Bitmap iconbp ;

    public Bitmap getIcon() {
        return this.iconbp ;
    }

    public void setIcon(Bitmap icon) {
        this.iconbp = icon ;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}