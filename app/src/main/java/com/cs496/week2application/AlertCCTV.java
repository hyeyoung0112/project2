package com.cs496.week2application;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;

public class AlertCCTV extends AppCompatActivity {

    String STREAMING_SERVER_URL = "http://192.168.43.139:8081/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alertcctv);
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

    }

    public void startBlinkingAnimation(View view) {
        Animation startAnimation = AnimationUtils.loadAnimation(this, R.anim.blink_animation);
        view.startAnimation(startAnimation);
    }
}
