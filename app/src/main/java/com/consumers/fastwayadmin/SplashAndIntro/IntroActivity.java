package com.consumers.fastwayadmin.SplashAndIntro;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.consumers.fastwayadmin.Login.MainActivity;
import com.consumers.fastwayadmin.R;
import com.consumers.fastwayadmin.SplashAndIntro.IntroFrags.FragFour;
import com.consumers.fastwayadmin.SplashAndIntro.IntroFrags.FragOne;
import com.consumers.fastwayadmin.SplashAndIntro.IntroFrags.FragThree;
import com.consumers.fastwayadmin.SplashAndIntro.IntroFrags.FragTwo;

public class IntroActivity extends AppCompatActivity {
    FragmentManager manager;
    Fragment active;
    Button next,back;
    SharedPreferences sharedPreferences;
    int count = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        manager = getSupportFragmentManager();
        FragOne fragOne = new FragOne();
        sharedPreferences = getSharedPreferences("IntroAct",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        FragThree fragThree = new FragThree();
        FragFour fragFour = new FragFour();
        FragTwo fragTwo = new FragTwo();
        next = findViewById(R.id.showNextIntro);
        back = findViewById(R.id.showPreviousIntro);
        active = fragOne;
        manager.beginTransaction().add(R.id.myIntroAct, fragFour, "4").hide(fragFour).commit();
        manager.beginTransaction().add(R.id.myIntroAct, fragThree, "3").hide(fragThree).commit();
        manager.beginTransaction().add(R.id.myIntroAct, fragTwo, "2").hide(fragTwo).commit();
        manager.beginTransaction().add(R.id.myIntroAct, fragOne, "1").commit();

        next.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                count++;
                if(count > 1)
                    back.setVisibility(View.VISIBLE);
                else
                    back.setVisibility(View.INVISIBLE);

                if(count > 3){
                    next.setText("DONE");
                }else
                    next.setText("NEXT");

                if(count > 4){
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(IntroActivity.this);
                    View myView = getLayoutInflater().inflate(R.layout.terms_condition_layout,null);
                    alertDialog.setCancelable(false);
                    Button accept = (Button) myView.findViewById(R.id.acceptTermsAndC);

                    accept.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(IntroActivity.this, MainActivity.class));
                            editor.putString("done","yes");
                            editor.apply();
                            finish();
                        }
                    });
                    alertDialog.setView(myView);
                    alertDialog.create().show();
                }

               switch (count){
                   case 1:
                       manager.beginTransaction().setCustomAnimations(
                               R.anim.slide_in,  // enter
                               R.anim.fade_in
                       ).hide(active).show(fragOne).commit();
                       active = fragOne;
                       break;
                   case 2:
                       manager.beginTransaction().hide(active).setCustomAnimations(
                               R.anim.slide_in,  // enter
                               R.anim.fade_in
                       ).show(fragTwo).commit();
                       active = fragTwo;
                       break;
                   case 3:
                       manager.beginTransaction().hide(active).setCustomAnimations(
                               R.anim.slide_in,  // enter
                               R.anim.fade_in
                       ).show(fragThree).commit();
                       active = fragThree;
                       break;
                   case 4:
                       manager.beginTransaction().hide(active).setCustomAnimations(
                               R.anim.slide_in,  // enter
                               R.anim.fade_in
                       ).show(fragFour).commit();
                       active = fragFour;
                       break;
               }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count--;
                if(count > 1)
                    back.setVisibility(View.VISIBLE);
                else
                    back.setVisibility(View.INVISIBLE);

                if(count > 3){
                    next.setText("DONE");
                }else
                    next.setText("NEXT");

                switch (count){
                    case 1:
                        manager.beginTransaction().hide(active).setCustomAnimations(
                                R.anim.slide_out,  // enter
                                R.anim.fade_in
                        ).show(fragOne).commit();
                        active = fragOne;
                        break;
                    case 2:
                        manager.beginTransaction().hide(active).setCustomAnimations(
                                R.anim.slide_out,  // enter
                                R.anim.fade_in
                        ).show(fragTwo).commit();
                        active = fragTwo;
                        break;
                    case 3:
                        manager.beginTransaction().hide(active).setCustomAnimations(
                                R.anim.slide_out,  // enter
                                R.anim.fade_in
                        ).show(fragThree).commit();
                        active = fragThree;
                        break;
                    case 4:
                        manager.beginTransaction().hide(active).setCustomAnimations(
                                R.anim.slide_out,  // enter
                                R.anim.fade_in
                        ).show(fragFour).commit();
                        active = fragFour;
                        break;

                }
            }
        });

    }
}