package com.example.fastwayadmin.ListViewActivity;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fastwayadmin.R;

public class MyAccount extends AppCompatActivity {
    TextView textView;
    ListView listView;
    LinearLayout linearLayout;
    String[] names = {"Change Credentials","My Transactions","Change Mobile Number","Delete Account"};
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);
        initialise();
        ArrayAdapter arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,names);
        listView.setAdapter(arrayAdapter);
        SharedPreferences sharedPreferences = getSharedPreferences("loginInfo",MODE_PRIVATE);
        textView.setText("Hi, " + String.valueOf(sharedPreferences.getString("name","")));

    }



    private void initialise() {
        textView = findViewById(R.id.account_activity_text);
        listView = findViewById(R.id.accountActivityListView);
        linearLayout = findViewById(R.id.linearLayout);
    }
}