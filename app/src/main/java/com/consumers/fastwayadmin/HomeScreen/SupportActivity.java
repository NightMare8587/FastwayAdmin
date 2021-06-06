package com.consumers.fastwayadmin.HomeScreen;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.consumers.fastwayadmin.HomeScreen.LiveChat.LiveChatActivity;
import com.consumers.fastwayadmin.R;

import java.util.ArrayList;
import java.util.List;

public class SupportActivity extends AppCompatActivity{
    ListView listView;
    ListView faqList;

    List<String> genarlQuery = new ArrayList<String>();
    List<String> faQQuery = new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support);
        genarlQuery.add("Live Chat With Us");
        genarlQuery.add("Email Us");
        initialise();
        listView = findViewById(R.id.genralQueryListView);
        faqList = findViewById(R.id.faqListView);
        ArrayAdapter<String> faqAdapter = new ArrayAdapter<String>(this,R.layout.list,faQQuery);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.list,genarlQuery);
        faqList.setAdapter(faqAdapter);
        listView.setAdapter(arrayAdapter);


        faqList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        startActivity(new Intent(SupportActivity.this, FaqActivity.class));
                        break;
                }
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
//                        Toast.makeText(SupportActivity.this, "Work in Progress :)", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(SupportActivity.this, LiveChatActivity.class));
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

    private void initialise() {
        faQQuery.add("FAQ Queries");
    }
}