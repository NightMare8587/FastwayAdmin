package com.example.fastwayadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.fastwayadmin.NavFrags.AccountFrag;
import com.example.fastwayadmin.NavFrags.HomeFrag;
import com.example.fastwayadmin.NavFrags.MenuFrag;
import com.example.fastwayadmin.NavFrags.TablesFrag;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;

public class HomeScreen extends AppCompatActivity {
    Toolbar toolbar;
    BottomNavigationView bottomNavigationView;
    FragmentManager manager;
    FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initialise();

        manager.beginTransaction().replace(R.id.homescreen,new HomeFrag()).commit();
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.home:
                        manager.beginTransaction().replace(R.id.homescreen,new HomeFrag()).commit();
                        break;
                    case R.id.menu:
                        manager.beginTransaction().replace(R.id.homescreen,new MenuFrag()).commit();
                        break;
                    case R.id.account:
                        manager.beginTransaction().replace(R.id.homescreen, new AccountFrag()).commit();
                        break;
                    case R.id.tables:
                        manager.beginTransaction().replace(R.id.homescreen,new TablesFrag()).commit();
                        break;
                }
                return true;
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
            Toast.makeText(this, "Button Clicked", Toast.LENGTH_SHORT).show();

        return super.onOptionsItemSelected(item);
    }

    private void initialise() {
        auth = FirebaseAuth.getInstance();
        bottomNavigationView = (BottomNavigationView)findViewById(R.id.bottomNav);
        manager = getSupportFragmentManager();
        FirebaseMessaging.getInstance().subscribeToTopic(auth.getUid());
    }
}