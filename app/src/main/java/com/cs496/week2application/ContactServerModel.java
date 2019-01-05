package com.cs496.week2application;

import android.graphics.Bitmap;
import android.net.Uri;

public class ContactServerModel {
    private String userID;
    private String name, phone;
    private String fileName;
    private Uri imageUri;

    public Uri getImageUri() {return this.imageUri;}

    public void setImageUri(Uri newUri) {this.imageUri = newUri;}

    public String getUserID() {
        return this.userID;
    }

    public void setUserID(String id) {
        this.userID = id;
    }

    public String getIcon() {
        return this.fileName ;
    }

    public void setIcon(String iconFile) {
        this.fileName = iconFile;
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
