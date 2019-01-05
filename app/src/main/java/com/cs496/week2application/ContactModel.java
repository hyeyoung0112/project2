package com.cs496.week2application;

import android.graphics.Bitmap;
import android.net.Uri;

public class ContactModel{
    private String userId;
    private String name, phone;
    private Bitmap icon;
    private String imageUri;

    public String getImageUri() {return this.imageUri;}

    public void setImageUri(String newUri) {this.imageUri = newUri;}

    public void setId(String _id) {this.userId = _id;}

    public String getId() {return this.userId;}

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