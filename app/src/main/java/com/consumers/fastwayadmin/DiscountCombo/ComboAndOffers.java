package com.consumers.fastwayadmin.DiscountCombo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.consumers.fastwayadmin.R;

import mehdi.sakout.fancybuttons.FancyButton;

public class ComboAndOffers extends AppCompatActivity {
    FancyButton mainCourse,breads,snacks,deserts,drinks;
    Toolbar menuBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_combo_and_offers);
        initialise();
        mainCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ComboAndOffers.this,SelectDishForCombo.class);
                intent.putExtra("dishType","Main Course");
                startActivity(intent);
            }
        });
        breads.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ComboAndOffers.this,SelectDishForCombo.class);
                intent.putExtra("dishType","Breads");
                startActivity(intent);
            }
        });
        deserts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ComboAndOffers.this,SelectDishForCombo.class);
                intent.putExtra("dishType","Deserts");
                startActivity(intent);
            }
        });
        snacks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ComboAndOffers.this,SelectDishForCombo.class);
                intent.putExtra("dishType","Snacks");
                startActivity(intent);
            }
        });
        drinks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ComboAndOffers.this,SelectDishForCombo.class);
                intent.putExtra("dishType","Drinks");
                startActivity(intent);
            }
        });
    }

    private void initialise() {
        mainCourse = findViewById(R.id.comboMainCourse);
        breads = findViewById(R.id.comboBreads);
        snacks = findViewById(R.id.comboSnacks);
        deserts = findViewById(R.id.comboDeserts);
        drinks = findViewById(R.id.comboDrinks);
        menuBar = findViewById(R.id.comboFragBar);
    }
}