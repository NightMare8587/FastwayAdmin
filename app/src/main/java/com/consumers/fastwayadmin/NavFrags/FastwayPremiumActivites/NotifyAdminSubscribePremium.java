package com.consumers.fastwayadmin.NavFrags.FastwayPremiumActivites;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;

import com.consumers.fastwayadmin.R;

public class NotifyAdminSubscribePremium extends AppCompatActivity {
    Button doItLater,subscribe;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notify_admin_subscribe_premium);
        doItLater = findViewById(R.id.DoItLaterButtonActivity);
        subscribe = findViewById(R.id.SubscribeNowserActivityButton);
        sharedPreferences = getSharedPreferences("loginInfo",MODE_PRIVATE);
        editor = sharedPreferences.edit();
        doItLater.setOnClickListener(click -> {
            editor.putString("lastPremNotified",String.valueOf(System.currentTimeMillis()));
            editor.apply();
            finish();
        });

        subscribe.setOnClickListener(click -> {
            startActivity(new Intent(NotifyAdminSubscribePremium.this,FastwayPremiums.class));
            finish();
        });
    }
}