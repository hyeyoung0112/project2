package com.cs496.week2application;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.share.Sharer;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import net.alhazmy13.imagefilter.ImageFilter;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class PicSelectActivity extends FragmentActivity {
    Bitmap mainImage;

    ImageView selectView;
    RecyclerView recyclerView;
    boolean writePermission;
    View facebookBtn;
    View instagramBtn;
    View kakaotalkBtn;

    private LoginManager loginManager;
    private CallbackManager callbackManager;
    private ShareDialog shareDialog;

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picselect);

        // Hook up clicks on the thumbnail views.
        Intent intent = getIntent();
        writePermission = intent.getBooleanExtra("writePermission", false);
        selectView = findViewById(R.id.expanded_image);
        recyclerView = findViewById(R.id.filterThumbnails);
        facebookBtn = findViewById(R.id.facebookBtn);
        instagramBtn = findViewById(R.id.instagramBtn);
        kakaotalkBtn = findViewById(R.id.kakaotalkBtn);

        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);

        //Save button gets current image in selectView and save it to gallery
        final View savebutton = findViewById(R.id.saveButton);
        savebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (writePermission){
                    BitmapDrawable filteredDrawable = (BitmapDrawable) selectView.getDrawable();
                    Bitmap newBP = filteredDrawable.getBitmap();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
                    String title = sdf.format(new Date());
                    MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(), newBP ,title, "description");
                    Toast.makeText(getApplicationContext(), "Image saved",Toast.LENGTH_SHORT).show();
                }

                else {
                    Toast.makeText(getApplicationContext(), "Cannot save image.", Toast.LENGTH_SHORT).show();
                }
            }

        });

        //go back to tab2
        final View thumb1View = findViewById(R.id.thumb_button_1);
        thumb1View.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onBackPressed(); // Go back to previous fragment!
            }
        });

        //share buttons: facebook
        facebookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Target target = new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        SharePhoto sharePhoto = new SharePhoto.Builder()
                                .setBitmap(bitmap)
                                .build();
                        if (ShareDialog.canShow(SharePhotoContent.class)) {
                            SharePhotoContent content = new SharePhotoContent.Builder()
                                    .addPhoto(sharePhoto)
                                    .build();
                            shareDialog.show(content);
                        }
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {
                    }
                };

                shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
                    @Override
                    public void onSuccess(Sharer.Result result) {
                        Toast.makeText(getApplicationContext(), "Shared successfully.", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(getApplicationContext(), "Share cancelled.", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(FacebookException error) {
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

                Bitmap image = ((BitmapDrawable) selectView.getDrawable()).getBitmap();
                Uri bitmapuri = getImageUri(getApplicationContext(), image);
//
                //We will fetch photo from link and convert to bitmap
                Picasso.with(getBaseContext())
                        .load(bitmapuri)
                        .into(target);
            }
        });

        //share buttons: instagram
        instagramBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent instagramshare = new Intent(Intent.ACTION_SEND);
                instagramshare.setType("image/*");

                Bitmap tempimage2 = ((BitmapDrawable) selectView.getDrawable()).getBitmap();
                Uri bitmapUri = getImageUri(getApplicationContext(), tempimage2);

                try {
                    instagramshare.putExtra(Intent.EXTRA_STREAM, bitmapUri);

                    instagramshare.putExtra(Intent.EXTRA_TEXT, "텍스트는 지원하지 않음!");
                    instagramshare.setPackage("com.instagram.android");
                    startActivity(instagramshare);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(getApplicationContext(), "인스타그램이 설치되어 있지 않습니다.", Toast.LENGTH_SHORT).show();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        //share buttons: kakaotalk
        kakaotalkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("image/png");
                Bitmap tempimage3 = ((BitmapDrawable) selectView.getDrawable()).getBitmap();
                Uri bitmapUri = getImageUri(getApplicationContext(), tempimage3);

                intent.putExtra(Intent.EXTRA_STREAM, bitmapUri);
                intent.setPackage("com.kakao.talk");
                startActivity(intent);
            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();
        final String imgPath = getIntent().getStringExtra("imagePath");
        if (imgPath != ""){
            mainImage = BitmapFactory.decodeFile(imgPath);
            selectView.setImageBitmap(mainImage);
            LoadFilterThumbnails();
        }
        else {
            Toast.makeText(getApplicationContext(), "Cannot load image",Toast.LENGTH_LONG).show();
            onBackPressed();
        }
    }

    private void LoadFilterThumbnails(){
        // array for filter type names and thumbnails
        String[] filterTypes = {"ORIGINAL", "GRAY", "BLUR", "OIL", "NEON", "BLOCK", "OLD", "SHARPEN", "LOMO","HDR", "SOFTGLOW"};
        ArrayList<FilteredThumbnail> thumbnails = new ArrayList<>();

        //resize mainImage to smaller thumbnails
        int desiredWidth = 100;
        int desiredHeight = mainImage.getHeight() * 100 / mainImage.getWidth();
        Bitmap thumbImage = Bitmap.createScaledBitmap(mainImage, desiredWidth, desiredHeight, false);

        //make thumbnails and add it to thumnails ArrayList
        FilteredThumbnail original = new FilteredThumbnail();
        original.setFilterType("ORIGINAL");
        original.setImgBP(thumbImage);
        original.setFilterTypeIndex(0);
        thumbnails.add(original);
        for (int index = 1; index < filterTypes.length; index++){
            //filter images by type
            Bitmap filteredImg = ApplyFilterByIndex(thumbImage, index);
            //and save images and corresponding filters to thumbnails array
            FilteredThumbnail thumbnail = new FilteredThumbnail();
            thumbnail.setFilterTypeIndex(index);
            thumbnail.setFilterType(filterTypes[index]);
            thumbnail.setImgBP(filteredImg);
            thumbnails.add(thumbnail);
        }

        // Create an adapter and set onClickListener
        FilterThumbnailAdapter adapter = new FilterThumbnailAdapter(thumbnails, new FilterThumbnailAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(FilteredThumbnail item) {
                LoadPicture(item.getFilterTypeIndex());
            }
        });

        //use adapter to put images in recyclerview
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
    }

    private void LoadPicture(int index){
        if (index == 0) selectView.setImageBitmap(mainImage);
        else selectView.setImageBitmap(ApplyFilterByIndex(mainImage, index));
        return;
    }

    private Bitmap ApplyFilterByIndex(Bitmap bitmap, int value){
        int dstHeight = 400;
        int dstWidth = bitmap.getWidth() * dstHeight / bitmap.getHeight();
        bitmap = Bitmap.createScaledBitmap(bitmap, dstWidth, dstHeight, false);
        // "OLD", "SHARPEN", "LOMO","HDR"
        switch (value) {
            case 1:
                return ImageFilter.applyFilter(bitmap, ImageFilter.Filter.GRAY);
            case 2:
                return ImageFilter.applyFilter(bitmap, ImageFilter.Filter.AVERAGE_BLUR, 9);
            case 3:
                return ImageFilter.applyFilter(bitmap, ImageFilter.Filter.OIL,10);
            case 4:
                return ImageFilter.applyFilter(bitmap, ImageFilter.Filter.NEON,200, 50, 100);
            case 5:
                return ImageFilter.applyFilter(bitmap, ImageFilter.Filter.BLOCK);
            case 6:
                return ImageFilter.applyFilter(bitmap, ImageFilter.Filter.OLD);
            case 7:
                return ImageFilter.applyFilter(bitmap, ImageFilter.Filter.SHARPEN);
            case 8:
                return ImageFilter.applyFilter(bitmap, ImageFilter.Filter.LOMO);
            case 9:
                return ImageFilter.applyFilter(bitmap, ImageFilter.Filter.HDR);
            case 10:
                return ImageFilter.applyFilter(bitmap, ImageFilter.Filter.SOFT_GLOW);
            default:
                return bitmap;
        }
    }
}