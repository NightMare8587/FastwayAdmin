package com.consumers.fastwayadmin.HomeScreen;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.consumers.fastwayadmin.HomeScreen.LiveChat.LiveChatActivity;
import com.consumers.fastwayadmin.HomeScreen.ReportSupport.ReportCustomer;
import com.consumers.fastwayadmin.R;

import java.util.ArrayList;
import java.util.List;

public class SupportActivity extends AppCompatActivity{
    ListView listView;
    ListView faqList;

    List<String> genarlQuery = new ArrayList<>();
    List<String> faQQuery = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support);
        genarlQuery.add("Live Chat With Us");
        genarlQuery.add("Email Us");
        initialise();
        listView = findViewById(R.id.genralQueryListView);
        faqList = findViewById(R.id.faqListView);
        ArrayAdapter<String> faqAdapter = new ArrayAdapter<>(this, R.layout.list, faQQuery);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.list, genarlQuery);
        faqList.setAdapter(faqAdapter);
        listView.setAdapter(arrayAdapter);


        faqList.setOnItemClickListener((parent, view, position, id) -> {
            switch (position){
                case 0:
                    startActivity(new Intent(SupportActivity.this, FaqActivity.class));
                    break;
                case 1:
                    startActivity(new Intent(SupportActivity.this, ReportCustomer.class));
                    break;
            }
        });
        listView.setOnItemClickListener((parent, view, position, id) -> {
            switch (position){
                case 0:
//                        Toast.makeText(SupportActivity.this, "Work in Progress :)", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(SupportActivity.this, LiveChatActivity.class));
                    break;
                case 1:
                    Intent emailIntent = new Intent(Intent.ACTION_SEND);
                    emailIntent.setType("plain/text");
                    emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] { "ordinalo.services@gmail.com" });
                    startActivity(emailIntent);
                    break;
            }
        });
    }

    private void initialise() {
        faQQuery.add("FAQ Queries");
        faQQuery.add("Report a customer/Add to block list");
    }
}