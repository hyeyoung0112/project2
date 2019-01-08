package com.cs496.week2application;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.PermissionChecker;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;


public class Tab2Images extends Fragment {
    EditText editText;
    GridView gridview;
    ImageAdapter adapter;
    boolean writePermission;
    final int REQ_CODE_SELECT_IMAGE = 100;
    private static int RESULT_LOAD_IMAGE = 1;

    private String userID;
    private RetrofitRequest retrofit = new RetrofitRequest();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab2images, container, false);
        gridview = (GridView) rootView.findViewById(R.id.gridView);
        Intent intent = getActivity().getIntent();
        userID = intent.getStringExtra("userAccount");

        if(ReadPermissioncheck()) loadPictures();
        else Log.d("Permission>>>>>", "Read permission not allowed.");

        return rootView;
    }

    @Override
    public void onResume(){
        super.onResume();
        writePermission = WritePermissioncheck();
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (ReadPermissioncheck()) {
                    Intent intent = new Intent(getActivity().getApplicationContext(), PicSelectActivity.class);
                    intent.putExtra("filePath", adapter.getItem(position).getFilePath());
                    intent.putExtra("userID", userID);
                    intent.putExtra("filename", adapter.getItem(position).getFilename());
                    intent.putExtra("writePermission", writePermission);
                    startActivity(intent);
                }
            }
        });
    }

    protected ArrayList<String> getImagesPath() {
        Uri uri;
        ArrayList<String> listOfAllImages = new ArrayList<String>();
        Cursor cursor;
        int column_index_data, column_index_folder_name;
        String PathOfImage = null;
        uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = { MediaStore.MediaColumns.DATA,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME };

        cursor = getActivity().getContentResolver().query(uri, projection, null,
                null, null);

        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        column_index_folder_name = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
        while (cursor.moveToNext()) {
            PathOfImage = cursor.getString(column_index_data);
            String dir = new File(PathOfImage).getParent();
            String contactDir = "/storage/emulated/0/contactPhotos";
            if (!dir.equals(contactDir)) {
                listOfAllImages.add(PathOfImage);
            }
        }
        return listOfAllImages;
    }

    public void loadPictures(){
        //get pictures from device
        Map<String, GalleryPic> devicePics = getDevicePictures();
        Log.d("LoadPics>>>>>", "Get images from device");
        //arraylist of pictures to display
        ArrayList<GalleryPic> galleryPics = new ArrayList<GalleryPic>(devicePics.values());

        if (InternetPermissioncheck() && !userID.equals("")) {
            //get pictures from server
            Map<String, GalleryPic> serverPics = retrofit.GetAllImages(userID);
            ArrayList<String> serverPicKeys = new ArrayList<String>(serverPics.keySet());
            Log.d("LoadPics>>>>>", "Get images from server");
            //add pictures that are not in device to galleryPics
            for (int i = 0; i < serverPics.size(); i++) {
                GalleryPic serverPic = serverPics.get(serverPicKeys.get(i));
                Log.d("LoadPics>>>>>", "server picture: " + serverPic.getLastModified());
                if (!devicePics.containsKey(serverPic.getFilename())) {
                    galleryPics.add(serverPic);
                    Log.d("LoadPics>>>>>", "Get images from server 2 "+ serverPic.getFilename());
                }
            }

            ArrayList<String> devicePicKeys = new ArrayList<String>(devicePics.keySet());
            //upload pictures that are not in server or has different modified date
            for (int i= 0; i<devicePics.size(); i++){
                GalleryPic devicePic = devicePics.get(devicePicKeys.get(i));
                if (!serverPics.containsKey(devicePic.getFilename())) {
                    Log.d("LoadPics>>>>>", "Upload new picture");
                    retrofit.PostImageFile(userID, devicePic.getFilename(), devicePic.getFilePath());
                    retrofit.PostImage(userID, devicePic.getFilename(), devicePic.getLastModified());
                }
                else if(serverPics.get(devicePic.getFilename()).getLastModified() != devicePic.getLastModified()){
                    Log.d("LoadPics>>>>>", "Upload modified picture");
                    retrofit.PostImageFile(userID, devicePic.getFilename(), devicePic.getFilePath());
                }
            }
        }

        //set gridview
        adapter = new ImageAdapter(getActivity().getApplicationContext(), R.layout.row, galleryPics);
        gridview.setAdapter(adapter);
    }

    public Map<String, GalleryPic> getDevicePictures(){
        ArrayList<String> ImagePaths = getImagesPath();
        Map<String, GalleryPic> result = new HashMap<>();
        for (int i = 0; i < ImagePaths.size(); i++) {
            Bitmap bitmap = BitmapFactory.decodeFile(ImagePaths.get(i));
            GalleryPic pic = new GalleryPic(userID, getEncodedFileName(ImagePaths.get(i)));
            pic.setLastModified(new File(ImagePaths.get(i)).lastModified());
            pic.setBitmap(bitmap);
            pic.setFilePath(ImagePaths.get(i));
            result.put(pic.getFilename(), pic);
        }
        return result;
    }

    public String getEncodedFileName(String path) {
        String fileName = java.util.Base64.getUrlEncoder().encodeToString(path.getBytes()) + ".png";
        return fileName;
    }

    public int checkselfpermission(String permission) {
        return PermissionChecker.checkSelfPermission(getContext(), permission);
    }

    public boolean ReadPermissioncheck() {
        if (checkselfpermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {


            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
            if (checkselfpermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                return false;
            }
        }
    }


    public boolean WritePermissioncheck() {
        if (checkselfpermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
            if (checkselfpermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                return false;
            }
        }
    }

    public boolean InternetPermissioncheck() {
        if (checkselfpermission(Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.INTERNET}, 100);
            if (checkselfpermission(Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                return false;
            }
        }
    }
}

