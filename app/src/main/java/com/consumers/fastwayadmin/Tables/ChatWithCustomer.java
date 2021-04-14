package com.consumers.fastwayadmin.Tables;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.consumers.fastwayadmin.R;
import com.google.firebase.database.DatabaseReference;

public class ChatWithCustomer extends AppCompatActivity {
    DatabaseReference reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_with_customer);
    }
}