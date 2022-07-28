package com.consumers.fastwayadmin.HomeScreen;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.consumers.fastwayadmin.R;

public class FaqActivity extends AppCompatActivity {
    WebView webView;
    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);
////        WebSettings webSettings = webView.getSettings();
//        webView = findViewById(R.id.faqWebView);
//        WebSettings webSettings = webView.getSettings();
//        webView.loadUrl("https://intercellular-stabi.000webhostapp.com/faqweb/index.html");
//        webSettings.setJavaScriptEnabled(true);
//        webSettings.setDefaultZoom(WebSettings.ZoomDensity.FAR);

        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://intercellular-stabi.000webhostapp.com/faqweb/indexadmin.html"));
        startActivity(browserIntent);


    }

    @Override
    protected void onResume() {
        super.onResume();
        finish();
    }
}