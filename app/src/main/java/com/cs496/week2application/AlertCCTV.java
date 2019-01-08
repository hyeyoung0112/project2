package com.cs496.week2application;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;

public class AlertCCTV extends AppCompatActivity {
    public static Activity alertcctv;

    String STREAMING_SERVER_URL = "http://192.168.43.139:8081/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alertcctv);
        alertcctv = AlertCCTV.this;

        View view = findViewById(R.id.intruder);
        startBlinkingAnimation(view);
        //view = findViewById(R.id.cctvWebView2);
        //startBlinkingAnimation(view);

        WebView webView = (WebView) findViewById(R.id.cctvWebView2);
        webView.setPadding(0,0,0,0);
        //webView.setInitialScale(100);
        webView.getSettings().setBuiltInZoomControls(false);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        //webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);

        String url =STREAMING_SERVER_URL;
        webView.loadUrl(url);

        webView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (CallPermissioncheck()) {
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:112"));
                    startActivity(intent);
                    //Intent intent = new Intent(getApplicationContext(), JoyStickActivity.class);
                    //startActivity(intent);
                }
                return true;
            }
        });
    }
    public void startBlinkingAnimation(View view) {
        Animation startAnimation = AnimationUtils.loadAnimation(this, R.anim.blink_animation);
        view.startAnimation(startAnimation);
    }

    public int checkselfpermission(String permission) {
        return PermissionChecker.checkSelfPermission(getApplicationContext(), permission);
    }

    public boolean CallPermissioncheck() {
        if (checkselfpermission(Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, 100);
            if (checkselfpermission(Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                return false;
            }
        }
    }
}
