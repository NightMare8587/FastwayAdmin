package com.example.fastwayadmin.ListViewActivity;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fastwayadmin.R;

public class MyAccount extends AppCompatActivity {
    ListView listView;

    String[] names = {"Change Credentials","Change Mobile Number","Delete Account"};
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);
        initialise();
        ArrayAdapter arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,names);
        listView.setAdapter(arrayAdapter);
        SharedPreferences sharedPreferences = getSharedPreferences("loginInfo",MODE_PRIVATE);
        
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i){
                    case 0:
                }
            }
        });
    }
    private void initialise() {

        listView = findViewById(R.id.accountActivityListView);

    }
}