package com.example.fastwayadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.fastwayadmin.NavFrags.AccountFrag;
import com.example.fastwayadmin.NavFrags.HomeFrag;
import com.example.fastwayadmin.NavFrags.MenuFrag;
import com.example.fastwayadmin.NavFrags.TablesFrag;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeScreen extends AppCompatActivity {
    Toolbar toolbar;
    BottomNavigationView bottomNavigationView;
    FragmentManager manager;
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

    private void initialise() {
        bottomNavigationView = (BottomNavigationView)findViewById(R.id.bottomNav);
        manager = getSupportFragmentManager();
    }
}