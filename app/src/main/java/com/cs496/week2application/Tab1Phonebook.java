package com.cs496.week2application;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.provider.Telephony;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.PermissionChecker;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Tab1Phonebook extends Fragment implements ActivityCompat.OnRequestPermissionsResultCallback {
    private ListView contactsListView;
    private Tab1ContactViewAdapter adapter;
    private Map<String, ContactModel> contactModelMap;
    private ArrayList<JSONObject> jsonArr = new ArrayList<JSONObject>();
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    String[] REQUIRED_PERMISSIONS = {Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS};
    public int sel_pos = -1;

    private FloatingActionButton msgButton;
    private FloatingActionButton addButton;

    private String userID;

    private RetrofitRequest retrofit = new RetrofitRequest();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i("전화번호부 fragment", "onCreateView()");
        View rootView = inflater.inflate(R.layout.tab1phonebook, container, false);

        contactsListView = rootView.findViewById(R.id.contactLV);
        msgButton = (FloatingActionButton) rootView.findViewById(R.id.messageButton);
        addButton = rootView.findViewById(R.id.addContactButton);

        contactModelMap = new HashMap<String, ContactModel>();

        Intent intent = getActivity().getIntent();
        userID = intent.getStringExtra("userAccount");

        if (Permissioncheck()) {
            LoadContacts(contactsListView);
        }

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i("전화번호부 fragment", "onResume()");

        WritePermissioncheck();

        contactsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (sel_pos == position) {
                    contactsListView.setAdapter(adapter);
                    sel_pos = -1;
                } else {
                    sel_pos = position;
                }
            }
        });
        msgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (contactsListView.getCheckedItemCount() != 0 && sel_pos!=-1) {
                    Uri smsUri;
                    Log.d("fatal cause>>>>>", "Selected Position: " + sel_pos);
                    String temp = ((ContactModel) contactsListView.getItemAtPosition(sel_pos)).getNumber();
                    String phone[] = new String[1];
                    if (temp.contains("-")) {
                        String phonenumbers[] = temp.split("-");
                        StringBuilder sb = new StringBuilder("010");
                        sb.append(phonenumbers[1]);
                        sb.append(phonenumbers[2]);

                        phone[0] = sb.toString();
                    } else {
                        phone[0] = temp;
                    }

                    smsUri = Uri.parse("smsto:" + Uri.encode(TextUtils.join(",", phone)));

                    Intent intent;

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        intent = new Intent(Intent.ACTION_SENDTO, smsUri);
                        intent.setPackage(Telephony.Sms.getDefaultSmsPackage(getActivity().getApplicationContext()));
                    } else {
                        intent = new Intent(Intent.ACTION_VIEW, smsUri);
                    }

                    contactsListView.clearChoices();
                    startActivity(intent);

                } else {
                    String defaultApplication = Settings.Secure.getString(getContext().getContentResolver(), "sms_default_application");
                    PackageManager pm = getContext().getPackageManager();
                    Intent intent = pm.getLaunchIntentForPackage(defaultApplication);

                    startActivity(intent);
                }
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (WritePermissioncheck()) {
                    Intent intent = new Intent(getActivity().getApplicationContext(), AddContactActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(getContext(), "Cannot add contact.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    public int checkselfpermission(String permission) {
        return PermissionChecker.checkSelfPermission(getContext(), permission);
    }

    public boolean Permissioncheck() {
        if (checkselfpermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_CONTACTS}, 100);
            if (checkselfpermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
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

    public boolean WritePermissioncheck() {
        if (checkselfpermission(Manifest.permission.WRITE_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_CONTACTS}, 100);
            if (checkselfpermission(Manifest.permission.WRITE_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                return false;
            }
        }
    }

    private Map<String, ContactModel> loadContactsFromDevice(String userID){
        Map<String, ContactModel> deviceContactModelMap = new HashMap<String, ContactModel>();
        //Get contacts from device
        Cursor phones = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
        while (phones.moveToNext()) {

            //get name, number, and image uri from contact info
            String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            String uriStr = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI));
            Uri image_uri;
            if (uriStr != null) image_uri = Uri.parse(uriStr);
            else image_uri = Uri.parse("android.resource://" + getContext().getPackageName() + "/drawable/default_contact_photo");
            File file = null;
            Bitmap bp;
            try {
                bp = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), image_uri);
                //make image files of bitmaps
                File storeDir = new File(Environment.getExternalStorageDirectory(), "contactPhotos");
                if (!storeDir.exists()) {
                    if (!storeDir.mkdirs()) {
                        Log.d("MAKEDIRECTORY>>>>>", "failed to create directory");
                    }
                }
                String filename = java.util.Base64.getUrlEncoder().encodeToString((userID + name).getBytes()) + ".png";
                file = new File(storeDir.getPath() + File.separator + filename);

                OutputStream os = new BufferedOutputStream(new FileOutputStream(file));
                bp.compress(Bitmap.CompressFormat.PNG, 100, os);
                os.close();
            } catch(IOException e) {
                Log.d("MAKEIMGFILE>>>>>", "failed to create image file: " + e.getMessage());
                bp = null;
            }

            ContactModel contactModel = new ContactModel();
            contactModel.setId(userID);
            contactModel.setName(name);
            contactModel.setNumber(phoneNumber);
            contactModel.setIcon(bp);
            if (file != null) {
                contactModel.setImageUri(file.getAbsolutePath());
            }
            deviceContactModelMap.put(name, contactModel);
        }
        phones.close();
        return deviceContactModelMap;
    }

    private void LoadContacts(ListView LV) {
        //Load contacts from device
        contactModelMap = loadContactsFromDevice(userID);

        //Get contacts from server if account exists
        Set<String> ServerContactNames = new HashSet<String>();
        if (!userID.equals("") && InternetPermissioncheck()) {
            Map<String, ContactServerModel> map = retrofit.GetAllContacts(userID);
            for (ContactServerModel serverModel: map.values()) {
                ContactModel serverContact = new ContactModel();
                serverContact.setName(serverModel.getName());
                serverContact.setNumber(serverModel.getNumber());
                Bitmap bitmap = retrofit.GetImageBitmap(userID, java.util.Base64.getUrlEncoder().encodeToString((userID + serverContact.getName()).getBytes()) + ".png");
                serverContact.setIcon(bitmap);
                contactModelMap.put(serverContact.getName(), serverContact);
                Log.d("Load Contacts>>>>>", "name: " + serverContact.getName() + " number: " + serverContact.getNumber());
                ServerContactNames.add(serverModel.getName());
            }

            for (ContactModel contact: contactModelMap.values()) {
                String name = contact.getName();
                String phoneNumber = contact.getNumber();
                String filename = java.util.Base64.getUrlEncoder().encodeToString((userID + name).getBytes()) + ".png";
                String imageUri = contact.getImageUri();

                //add contact information to server if server doesn't have such contact
                JsonObject obj = new JsonObject();
                obj.addProperty("user_id", userID);
                obj.addProperty("name", name);
                obj.addProperty("phone", phoneNumber);
                obj.addProperty("filename", filename);
                //obj.put("photo", getStringFromBitmap(bp));

                retrofit.PostContact(obj);
                retrofit.PostImageFile(userID, filename, imageUri);
            }
        }

        adapter = new Tab1ContactViewAdapter(getActivity().getApplicationContext(), contactModelMap);
        if (!(adapter.isEmpty())) {
            LV.setAdapter(adapter);
        }
        return;
    }
}

