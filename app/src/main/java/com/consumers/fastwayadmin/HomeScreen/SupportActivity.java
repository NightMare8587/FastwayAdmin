package com.consumers.fastwayadmin.HomeScreen;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.Toast;

import com.consumers.fastwayadmin.R;

import java.util.ArrayList;
import java.util.List;

public class SupportActivity extends AppCompatActivity{
    ListView listView;
    List<String> genarlQuery = new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support);
        genarlQuery.add("Live Chat With Us");
        genarlQuery.add("Email Us");
        listView = findViewById(R.id.genralQueryListView);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.list,genarlQuery);
        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        Toast.makeText(SupportActivity.this, "Work in Progress :)", Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        Intent emailIntent = new Intent(Intent.ACTION_SEND);
                        emailIntent.setType("plain/text");
                        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] { "fastway8587@gmail.com" });
                        startActivity(emailIntent);
                        break;
                }
            }
        });
    }
}