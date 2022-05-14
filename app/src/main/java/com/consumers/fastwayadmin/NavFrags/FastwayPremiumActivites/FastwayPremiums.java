package com.consumers.fastwayadmin.NavFrags.FastwayPremiumActivites;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;

import com.consumers.fastwayadmin.R;

public class FastwayPremiums extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Button subscribePrem;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = getSharedPreferences("loginInfo",MODE_PRIVATE);
        if(sharedPreferences.contains("FastwayAdminPrem")){
            setContentView(R.layout.activity_fastway_premiums);
        }else{
            setContentView(R.layout.subscribe_fastway_prem);
            subscribePrem = findViewById(R.id.subscribeFastwayPremButton);
            subscribePrem.setOnClickListener(click -> {
                
            });
        }
    }
}