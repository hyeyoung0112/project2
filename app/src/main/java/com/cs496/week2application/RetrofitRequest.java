package com.cs496.week2application;

import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.kakao.util.helper.log.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class RetrofitRequest {

    public RetrofitRequest() {}

    private String defaultURL = "http://143.248.140.251:9480";
    private Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(defaultURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    HttpService service = retrofit.create(HttpService.class);

    public Map<String, ContactServerModel> GetAllContacts(String userID) {
        Call<JsonArray> call = service.getUserContacts(userID);
        Map<String, ContactServerModel> result = new HashMap<String, ContactServerModel>();
        Thread getContactThread = new Thread() {
            @Override
            public void run() {
                try {
                    JsonArray arr = call.execute().body();
                    ArrayList<ContactServerModel> contacts = new ArrayList<ContactServerModel>();
                    //parse response body to map
                    Gson gson = new Gson();
                    Type type = new TypeToken<List<ContactServerModel>>() {
                    }.getType();
                    contacts = gson.fromJson(arr, type);
                    if (contacts != null) {
                        for (int i = 0; i < contacts.size(); i++) {
                            if (contacts.get(i).getName() != "null") {
                                result.put(contacts.get(i).getName(), contacts.get(i));
                            }
                        }
                    }
                    ContactServerModel contact = new ContactServerModel();
                    result.put("CS496_application_result_test", contact);
                } catch (IOException e) {
                    Log.d("RETROFIT>>>>>", "IOException in getting all contacts : " + e.getMessage());
                    ContactServerModel contact = new ContactServerModel();
                    result.put("CS496_application_result_test", contact);
                }
            }
        };
        getContactThread.start();
        while (!result.containsKey("CS496_application_result_test")){}
        result.remove("CS496_application_result_test");
        Log.d("Retrofit>>>>>", "Result: " + result.toString());
        return result;
    }

    public void PostContact(JsonObject obj) {

        Call<JsonObject> call = service.postUserContacts(obj);

        call.enqueue(new Callback<JsonObject>(){
            @Override public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Log.d("PostContact>>>>>", obj.toString() + "posted to server.");
                Log.d("Retrofit>>>>>", "PostContact response: " + response.message());
            }
            @Override public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("Retrofit>>>>>", "PostContact response: " + t.getMessage());
            }
        });
    }


    public Map<String, GalleryPic> GetAllImages(String userID){
        Call<JsonArray> call = service.getUserGallery(userID);
        Map<String, GalleryPic> result = new HashMap<>();
        Thread getGalleryThread = new Thread(){
            @Override public void run(){
                try {
                    JsonArray arr = call.execute().body();
                    ArrayList<GalleryPic> galleryPics = new ArrayList<GalleryPic>();
                    //parse response body to map
                    Gson gson = new Gson();
                    Type type = new TypeToken<List<GalleryPic>>() {}.getType();
                    galleryPics = gson.fromJson(arr, type);
                    if (galleryPics != null) {
                        for (int i = 0; i < galleryPics.size(); i++) {
                            result.put(galleryPics.get(i).getFilename(), galleryPics.get(i));
                        }
                    }
                    GalleryPic pic = new GalleryPic("", "EndOfInput");
                    result.put("CS496_application_result_test", pic);
                } catch (IOException e) {
                    Log.d("GetAllImages>>>>>", "IOException in getting all images : " + e.getMessage());
                    GalleryPic pic = new GalleryPic("", "EndOfInput");
                    result.put("CS496_application_result_test", pic);
                }
            }
        };
        getGalleryThread.start();
        while (!result.containsKey("CS496_application_result_test")){}
        result.remove("CS496_application_result_test");
        for (GalleryPic galleryPic: result.values()) {
            Log.d("Server Image>>>>>", "filename: " + galleryPic.getFilename());
            Bitmap bp = GetImageBitmap(userID, galleryPic.getFilename());
            galleryPic.setBitmap(bp);
        }
        return result;
    }

    public void PostImage(String userID, String filename, long lastModified) {
        JsonObject obj = new JsonObject();
        obj.addProperty("user_id", userID);
        obj.addProperty("imgname", filename);
        obj.addProperty("last_modified", lastModified);
        Call<JsonObject> call = service.postUserGallery(obj);
        call.enqueue(new Callback<JsonObject>(){
            @Override public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Log.d("PostGallery>>>>>", obj.toString() + "posted to server.");
                Log.d("Retrofit>>>>>", "PostGallery response: " + response.toString());
            }
            @Override public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("Retrofit>>>>>", "PostGallery response: " + t.toString());
            }
        });
    }


    public Bitmap GetImageBitmap(String userID, String filename) {
        ArrayList<Bitmap> bp = new ArrayList<>();
        Thread getBitmapThread = new Thread() {
            @Override
            public void run() {
                try {
                    URL imageURL = new URL(defaultURL + "/" + userID + "/" + filename);
                    InputStream is = (InputStream) imageURL.getContent();
                    BitmapDrawable bitmapDrawable = (BitmapDrawable) BitmapDrawable.createFromStream(is, userID+filename+"picture.png");
                    bp.add(bitmapDrawable.getBitmap());
                } catch(MalformedURLException e) {
                    Log.d("GetImage>>>>>","MalformedURLException: " + e.getMessage());
                } catch(IOException e){
                    Log.d("GetImage>>>>>","IOException: " + e.toString());
                }
            }
        };
        getBitmapThread.start();
        while (bp.size() == 0);
        return bp.get(0);
    }

    public void PostImageFile(String userID, String filename, String imageUri){
        File file = new File(imageUri);
        RequestBody requestFile = RequestBody.create(
                MediaType.parse("image/*"), file
        );

        // MultipartBody.Part is used to send also the actual file name
        MultipartBody.Part body = MultipartBody.Part.createFormData("image", filename, requestFile);

        Call<ResponseBody> call = service.postUserPhoto(userID, filename, body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d("Retrofit>>>>>", "PostImageFile response 1: " + response.toString());
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("Retrofit>>>>>", "PostImageFile response  2: " + t.getMessage());
            }
        });
    }
}
