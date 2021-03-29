package com.example.fastwayadmin.ListViewActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
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

import com.crowdfire.cfalertdialog.CFAlertDialog;
import com.example.fastwayadmin.R;

public class MyAccount extends AppCompatActivity {
    ListView listView;
    TextView textView;
    String[] names = {"Change Credentials","Change Mobile Number","Delete Account"};
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);
        initialise();
        ArrayAdapter arrayAdapter = new ArrayAdapter<String>(this, R.layout.list,names);
        listView.setAdapter(arrayAdapter);
        SharedPreferences sharedPreferences = getSharedPreferences("loginInfo",MODE_PRIVATE);
        textView.setText("Hi, " + sharedPreferences.getString("name",""));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i){
                    case 0:
                        break;
                    case 1:
                        break;
                }
            }
        });
    }
    private void initialise() {
        textView = findViewById(R.id.account_activity_text);
        listView = findViewById(R.id.accountActivityListView);

    }
}