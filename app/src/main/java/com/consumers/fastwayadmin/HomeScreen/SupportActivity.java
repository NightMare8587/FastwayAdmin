package com.consumers.fastwayadmin.HomeScreen;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ExpandableListView;

import com.consumers.fastwayadmin.R;

public class SupportActivity extends AppCompatActivity {

    ExpandableListView expandableListView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support);
        expandableListView = findViewById(R.id.expandableListView);


    }
}