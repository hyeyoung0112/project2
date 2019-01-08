package com.cs496.week2application;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;

public class JoyStickActivity extends AppCompatActivity implements JoystickView.JoystickListener{
    String STREAMING_SERVER_URL = "http://192.168.43.139:8081/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        JoystickView joystick = new JoystickView(this);
        setContentView(R.layout.activity_joystick);
        WebView webView = findViewById(R.id.joystickWebview);

        webView.getSettings().setBuiltInZoomControls(false);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        //webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);

        String url =STREAMING_SERVER_URL;
        webView.loadUrl(url);
    }

    @Override
    public void onJoystickMoved(float xPercent, float yPercent, int id) {
        Log.d("Joystick", "X percent: " + xPercent + " Y percent: " + yPercent);
    }
}