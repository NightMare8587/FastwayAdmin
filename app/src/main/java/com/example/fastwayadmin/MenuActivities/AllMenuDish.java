package com.example.fastwayadmin.MenuActivities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import com.example.fastwayadmin.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AllMenuDish extends AppCompatActivity {
    DatabaseReference allMenu;
    FloatingActionButton search;
    ProgressBar loading;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_menu_dish);
        initialise();

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), CreateDish.class));
            }
        });
    }

    private void initialise() {
        allMenu = FirebaseDatabase.getInstance().getReference().getRoot();
        search = (FloatingActionButton)findViewById(R.id.floatingActionButton2);
        loading = findViewById(R.id.loading);
    }
}