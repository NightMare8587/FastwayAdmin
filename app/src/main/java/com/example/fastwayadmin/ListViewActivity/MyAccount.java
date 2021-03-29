package com.example.fastwayadmin.ListViewActivity;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fastwayadmin.R;

public class MyAccount extends AppCompatActivity {
    TextView textView;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);
        initialise();
        SharedPreferences sharedPreferences = getSharedPreferences("loginInfo",MODE_PRIVATE);
        textView.setText("Hi, " + String.valueOf(sharedPreferences.getString("name","")));
    }

    private void initialise() {
        textView = findViewById(R.id.account_activity_text);
    }


}