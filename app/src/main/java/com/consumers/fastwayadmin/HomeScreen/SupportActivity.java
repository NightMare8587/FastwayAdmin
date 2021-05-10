package com.consumers.fastwayadmin.HomeScreen;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ExpandableListView;

import com.consumers.fastwayadmin.R;

import java.util.ArrayList;
import java.util.List;

public class SupportActivity extends AppCompatActivity {
   WebView view;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support);
        view = findViewById(R.id.webView);
        WebSettings settings = view.getSettings();
        view.getSettings().getJavaScriptEnabled();
        view.setInitialScale(1);
        view.getSettings().setUserAgentString("Android");
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        settings.setAllowFileAccess(true);
        settings.setAllowContentAccess(true);
        settings.setAllowFileAccessFromFileURLs(true);
        settings.setAllowUniversalAccessFromFileURLs(true);
        view.requestFocus();
        settings.setDomStorageEnabled(true);
        view.loadUrl("https://maheshwariloya.wixsite.com/my-site-2");
        view.setWebViewClient(new WebViewClient());
    }
}