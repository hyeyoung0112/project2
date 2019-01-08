package com.cs496.week2application;

import android.util.Log;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

public class Tab3CCTV extends Fragment {
    String STREAMING_SERVER_URL = "http://192.168.43.139:8081/";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab3cctv, container, false);
        WebView webView = (WebView) rootView.findViewById(R.id.cctvWebView);
        webView.setPadding(0,0,0,0);
        //webView.setInitialScale(100);
        webView.getSettings().setBuiltInZoomControls(false);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        //webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);

        String url =STREAMING_SERVER_URL;
        webView.loadUrl(url);

        return rootView;
    }
}
