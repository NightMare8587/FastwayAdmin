package com.consumers.fastwayadmin.HomeScreen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.consumers.fastwayadmin.Chat.DisplayAllAvaialbleChats;
import com.consumers.fastwayadmin.NavFrags.AccountFrag;
import com.consumers.fastwayadmin.NavFrags.HomeFrag;
import com.consumers.fastwayadmin.NavFrags.MenuFrag;
import com.consumers.fastwayadmin.NavFrags.TablesFrag;
import com.consumers.fastwayadmin.R;
import com.gauravk.bubblenavigation.BubbleNavigationConstraintView;
import com.gauravk.bubblenavigation.listener.BubbleNavigationChangeListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;

public class HomeScreen extends AppCompatActivity {

    BubbleNavigationConstraintView bubble;
    FragmentManager manager;
    FirebaseAuth auth;
    Fragment active;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        initialise();
        Fragment first = new HomeFrag();
        Fragment second = new MenuFrag();
        Fragment third = new TablesFrag();
        Fragment four = new AccountFrag();

         active = first;

//        manager.beginTransaction().replace(R.id.homescreen,new HomeFrag()).commit();
        manager.beginTransaction().add(R.id.homescreen, four, "4").hide(four).commit();
        manager.beginTransaction().add(R.id.homescreen, third, "3").hide(third).commit();
        manager.beginTransaction().add(R.id.homescreen, second, "2").hide(second).commit();
        manager.beginTransaction().add(R.id.homescreen, first, "1").commit();
        bubble.setNavigationChangeListener(new BubbleNavigationChangeListener() {
            @Override
            public void onNavigationChanged(View view, int position) {
                switch (position){
                    case 0:
                        manager.beginTransaction().hide(active).show(first).commit();
                        active = first;
                        break;
                    case 1:
                        manager.beginTransaction().hide(active).show(second).commit();
                        active = second;
                        break;
                    case 2:
                        manager.beginTransaction().hide(active).show(third).commit();
                        active = third;
                        break;
                    case 3:
                        manager.beginTransaction().hide(active).show(four).commit();
                        active = four;
                        break;
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.notification,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.notification)
            startActivity(new Intent(HomeScreen.this, DisplayAllAvaialbleChats.class));

        return super.onOptionsItemSelected(item);
    }

    private void initialise() {
        auth = FirebaseAuth.getInstance();
        bubble = findViewById(R.id.top_navigation_constraint);
        manager = getSupportFragmentManager();
        FirebaseMessaging.getInstance().subscribeToTopic(auth.getUid());
    }
}