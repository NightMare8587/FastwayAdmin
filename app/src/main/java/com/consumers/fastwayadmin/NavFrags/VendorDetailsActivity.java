package com.consumers.fastwayadmin.NavFrags;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.consumers.fastwayadmin.R;

public class VendorDetailsActivity extends AppCompatActivity {
    String name,email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_details);
        initialise();
    }

    private void initialise() {
        name = getIntent().getStringExtra("name");
        email = getIntent().getStringExtra("email");
    }
}