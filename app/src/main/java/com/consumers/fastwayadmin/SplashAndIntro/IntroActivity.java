package com.consumers.fastwayadmin.SplashAndIntro;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.consumers.fastwayadmin.Login.MainActivity;
import com.consumers.fastwayadmin.R;
import com.consumers.fastwayadmin.SplashAndIntro.IntroFrags.FragOne;
import com.consumers.fastwayadmin.SplashAndIntro.IntroFrags.FragThree;
import com.consumers.fastwayadmin.SplashAndIntro.IntroFrags.FragTwo;

public class IntroActivity extends AppCompatActivity {
    FragmentManager manager;
    Fragment active;
    Button next,back;
    int count = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        manager = getSupportFragmentManager();
        FragOne fragOne = new FragOne();
        FragThree fragThree = new FragThree();
        FragTwo fragTwo = new FragTwo();
        next = findViewById(R.id.showNextIntro);
        back = findViewById(R.id.showPreviousIntro);
        active = fragOne;
        manager.beginTransaction().add(R.id.myIntroAct, fragThree, "3").hide(fragThree).commit();
        manager.beginTransaction().add(R.id.myIntroAct, fragTwo, "2").hide(fragTwo).commit();
        manager.beginTransaction().add(R.id.myIntroAct, fragOne, "1").commit();

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count++;
                if(count > 1)
                    back.setVisibility(View.VISIBLE);
                else
                    back.setVisibility(View.INVISIBLE);

                if(count == 3){
                    next.setText("DONE");
                }else
                    next.setText("NEXT");

                if(count > 3){
                    startActivity(new Intent(IntroActivity.this, MainActivity.class));
                    finish();
                }

               switch (count){
                   case 1:
                       manager.beginTransaction().hide(active).show(fragOne).commit();
                       active = fragOne;
                       break;
                   case 2:
                       manager.beginTransaction().hide(active).show(fragTwo).commit();
                       active = fragTwo;
                       break;
                   case 3:
                       manager.beginTransaction().hide(active).show(fragThree).commit();
                       active = fragThree;
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

                if(count == 3){
                    next.setText("DONE");
                }else
                    next.setText("NEXT");

                switch (count){
                    case 1:
                        manager.beginTransaction().hide(active).show(fragOne).commit();
                        active = fragOne;
                        break;
                    case 2:
                        manager.beginTransaction().hide(active).show(fragTwo).commit();
                        active = fragTwo;
                        break;
                    case 3:
                        manager.beginTransaction().hide(active).show(fragThree).commit();
                        active = fragThree;
                        break;
                }
            }
        });

    }
}