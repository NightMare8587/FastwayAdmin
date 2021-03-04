package com.example.fastwayadmin.ListViewActivity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;

import com.example.fastwayadmin.R;

public class MyAccount extends AppCompatActivity {

    EditText infoName,infoEmail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);
    }
}