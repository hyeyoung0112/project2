package com.cs496.week2application;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface HttpService {
    @GET("/api/contacts/user_id/{user}/")
    Call<JsonArray> getUserContacts(
            @Path("user") String userName
    );
    @POST("/api/contacts/")
    Call<JsonObject> postUserContacts(
            @Body JsonObject object
    );

    @GET("/api/images/user_id/{user}")
    Call<JsonArray> getUserGallery(
            @Path("user") String userName
    );
    @POST("/api/images/")
    Call<JsonObject> postUserGallery(
            @Body JsonObject object
    );
    /*
    @Multipart
    @GET("/{user}/{filename}")
    Call<ResponseBody> getUserPhoto(
            @Path("user") String userName,
            @Path("filename") String fileName
    );
    */

    @Multipart
    @POST("/{user}/{filename}")
    Call<ResponseBody> postUserPhoto(
            @Path("user") String user_id,
            @Path("filename") String filename,
            @Part MultipartBody.Part img
    );
}
