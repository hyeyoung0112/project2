
package com.cs496.week2application;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i("전화번호부 fragment", "onCreateView()");
        View rootView = inflater.inflate(R.layout.tab1phonebook, container, false);

        contactsListView = rootView.findViewById(R.id.contactLV);
        msgButton = (FloatingActionButton) rootView.findViewById(R.id.messageButton);
        addButton = rootView.findViewById(R.id.addContactButton);

        contactModelMap = new HashMap<String, ContactModel>();

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i("전화번호부 fragment", "onResume()");

        if (Permissioncheck()) {
            LoadContacts(contactsListView);
        }

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

    private void LoadContacts(ListView LV) {
        //Get contacts from server if account exists
        ArrayList<ContactServerModel> FromServerContacts;
        Set<String> ServerContactNames = new HashSet<String>();
        if (userID != "" && InternetPermissioncheck()) {
            String sb = HttpConnection.GetAllContacts(userID);
            Log.d("SERVER_CONNECTION>>>>>", "Got string: " + sb);
            Gson gson = new Gson();
            Type type = new TypeToken<List<ContactServerModel>>() {
            }.getType();
            FromServerContacts = gson.fromJson(sb, type);
            for (int i = 0; i < FromServerContacts.size(); i++) {
                ServerContactNames.add(FromServerContacts.get(i).getName());
                Log.d("ADDCONTACT>>>>>", "Names in server: " + FromServerContacts.get(i).getName());
            }

            //add server contacts which are not in device
            for (int i = 0; i<FromServerContacts.size(); i++) {
                ContactServerModel serverModel = FromServerContacts.get(i);
                if (contactModelMap.get(serverModel.getName()) == null) {
                    ContactModel serverContact = new ContactModel();
                    serverContact.setName(serverModel.getName());
                    serverContact.setNumber(serverModel.getNumber());
                    if (serverModel.getIcon() != null)
                        serverContact.setIcon(getBitmapFromString(serverModel.getIcon()));
                    else
                        serverContact.setIcon(BitmapFactory.decodeResource(getContext().getResources(), R.drawable.default_contact_photo));
                    contactModelMap.put(serverContact.getName(), serverContact);
                }
            }
        }
        //Get contacts from device
        Cursor phones = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
        while (phones.moveToNext()) {
            //default photo is in res/drawable folder
            Bitmap bp = BitmapFactory.decodeResource(getContext().getResources(),
                    R.drawable.default_contact_photo);

            //get name, number, and image uri from contact info
            String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            String image_uri = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI));

            //if image is not default
            if (image_uri != null) {
                try {
                    bp = MediaStore.Images.Media
                            .getBitmap(getContext().getContentResolver(),
                                    Uri.parse(image_uri));
                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            ContactModel contactModel = new ContactModel();
            contactModel.setId(userID);
            contactModel.setName(name);
            contactModel.setNumber(phoneNumber);
            contactModel.setIcon(bp);
            contactModelMap.put(name, contactModel);
            //Log.d("DEVICE CONTACT>>>>>", name + "  " + phoneNumber);

            //add contact information to server if server doesn't have such contact
            if (userID != "" && !ServerContactNames.contains(name)) {
                JSONObject obj = new JSONObject();
                try {
                    obj.put("user_id", userID);
                    obj.put("name", name);
                    obj.put("phone", phoneNumber);
                    obj.put("photo", getStringFromBitmap(bp));
                    Log.d("ADDCONTACT>>>>>", "Post to server: " + obj.toString());
                    jsonArr.add(obj);
                    HttpConnection.AddContactToServer(obj);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        phones.close();

        adapter = new Tab1ContactViewAdapter(getActivity().getApplicationContext(), contactModelMap);
        if (!(adapter.isEmpty())) {
            LV.setAdapter(adapter);
        }
        return;
    }

    private String getStringFromBitmap(Bitmap bitmapPicture) {
        /*
         * This functions converts Bitmap picture to a string which can be
         * JSONified.
         * */
        final int COMPRESSION_QUALITY = 100;
        String encodedImage;
        ByteArrayOutputStream byteArrayBitmapStream = new ByteArrayOutputStream();
        bitmapPicture.compress(Bitmap.CompressFormat.PNG, COMPRESSION_QUALITY,
                byteArrayBitmapStream);
        byte[] b = byteArrayBitmapStream.toByteArray();
        encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
        return encodedImage;
    }

    private Bitmap getBitmapFromString(String jsonString) {
        /*
         * This Function converts the String back to Bitmap
         * */
        byte[] decodedString = Base64.decode(jsonString, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return decodedByte;
    }
}

