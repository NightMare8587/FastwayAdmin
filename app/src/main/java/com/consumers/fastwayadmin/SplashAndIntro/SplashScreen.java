package com.consumers.fastwayadmin.SplashAndIntro;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import com.consumers.fastwayadmin.Login.MainActivity;
import com.consumers.fastwayadmin.R;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        SharedPreferences sharedPreferences = getSharedPreferences("IntroAct",MODE_PRIVATE);
        SharedPreferences stopServices = getSharedPreferences("Stop Services", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = stopServices.edit();
        editor.putString("online","true");
        editor.apply();
        //this starts new activity
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(sharedPreferences.contains("done")){
                    startActivity(new Intent(SplashScreen.this, MainActivity.class));
                }else {
                    startActivity(new Intent(SplashScreen.this, IntroActivity.class));
                }
                finish();
                overridePendingTransition(R.anim.slide_in, R.anim.fade_out);
            }
        },1650);
    }
}