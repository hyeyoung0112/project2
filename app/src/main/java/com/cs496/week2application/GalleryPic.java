package com.cs496.week2application;

import android.graphics.Bitmap;

public class GalleryPic {
    private String userID;
    private String imgname;
    private long last_modified;
    private Bitmap bitmap;
    private String filePath;

    public String getFilePath(){return this.filePath;}
    public void setFilePath(String filePath1) {this.filePath=filePath1;}

    public Bitmap getBitmap(){return this.bitmap;}
    public void setBitmap(Bitmap bitmap1){this.bitmap=bitmap1;}

    public long getLastModified(){return this.last_modified;}
    public void setLastModified(long lastModified){this.last_modified = lastModified;}

    public String getUserID(){return this.userID;}
    public void setUserID(String newID){this.userID=newID;}

    public String getFilename(){return this.imgname;}
    public void setFilename(String newFilename){this.imgname=newFilename;}

    public GalleryPic(String userID, String filename) {
        this.userID= userID;
        this.imgname = filename;
    }
}
