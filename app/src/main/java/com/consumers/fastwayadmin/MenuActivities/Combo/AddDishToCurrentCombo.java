package com.consumers.fastwayadmin.MenuActivities.Combo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.consumers.fastwayadmin.DiscountCombo.ComboAndOffers;
import com.consumers.fastwayadmin.DiscountCombo.SelectDishForCombo;
import com.consumers.fastwayadmin.R;
import com.developer.kalert.KAlertDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import mehdi.sakout.fancybuttons.FancyButton;

public class AddDishToCurrentCombo extends AppCompatActivity {
    FancyButton mainCourse,breads,snacks,deserts,drinks;
    SharedPreferences sharedPreferences;
    DatabaseReference reference;
    FirebaseAuth auth;
    String comboName;
    LinearLayoutManager horizonatl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_dish_to_current_combo);
        comboName = getIntent().getStringExtra("name");
        auth = FirebaseAuth.getInstance();
        sharedPreferences = getSharedPreferences("loginInfo",MODE_PRIVATE);
        reference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(sharedPreferences.getString("state","")).child(sharedPreferences.getString("locality","")).child(Objects.requireNonNull(auth.getUid()));
        mainCourse = findViewById(R.id.comboMainCourse);
        breads = findViewById(R.id.comboBreads);
        snacks = findViewById(R.id.comboSnacks);
        deserts = findViewById(R.id.comboDeserts);
        drinks = findViewById(R.id.comboDrinks);
        mainCourse.setOnClickListener(view -> new KAlertDialog(AddDishToCurrentCombo.this,KAlertDialog.NORMAL_TYPE)
                .setTitleText("Info")
                .setContentText("Click On Dish Name to select for combo")
                .setConfirmText("Ok, Got it")
                .setConfirmClickListener(kAlertDialog -> {
                    kAlertDialog.dismissWithAnimation();
                    Intent intent = new Intent(AddDishToCurrentCombo.this,SelectDishForCurrentCombo.class);
                    intent.putExtra("dishType","Main Course");
                    intent.putExtra("comboName",comboName);
                    intent.putExtra("state",sharedPreferences.getString("state",""));
                    intent.putExtra("locality",sharedPreferences.getString("locality",""));
                    startActivity(intent);
                }).show());
        breads.setOnClickListener(view -> new KAlertDialog(AddDishToCurrentCombo.this,KAlertDialog.NORMAL_TYPE)
                .setTitleText("Info")
                .setContentText("Click On Dish Name to select for combo")
                .setConfirmText("Ok, Got it")
                .setConfirmClickListener(kAlertDialog -> {
                    kAlertDialog.dismissWithAnimation();
                    Intent intent = new Intent(AddDishToCurrentCombo.this, SelectDishForCurrentCombo.class);
                    intent.putExtra("dishType","Breads");
                    intent.putExtra("comboName",comboName);
                    intent.putExtra("state",sharedPreferences.getString("state",""));
                    intent.putExtra("locality",sharedPreferences.getString("locality",""));
                    startActivity(intent);

//                                kAlertDialog.dismissWithAnimation();
                }).show());
        deserts.setOnClickListener(view -> new KAlertDialog(AddDishToCurrentCombo.this,KAlertDialog.NORMAL_TYPE)
                .setTitleText("Info")
                .setContentText("Click On Dish Name to select for combo")
                .setConfirmText("Ok, Got it")
                .setConfirmClickListener(kAlertDialog -> {
                    kAlertDialog.dismissWithAnimation();
                    Intent intent = new Intent(AddDishToCurrentCombo.this,SelectDishForCurrentCombo.class);
                    intent.putExtra("dishType","Deserts");
                    intent.putExtra("comboName",comboName);
                    intent.putExtra("state",sharedPreferences.getString("state",""));
                    intent.putExtra("locality",sharedPreferences.getString("locality",""));
                    startActivity(intent);
//                                kAlertDialog.dismissWithAnimation();
                }).show());
        snacks.setOnClickListener(view -> new KAlertDialog(AddDishToCurrentCombo.this,KAlertDialog.NORMAL_TYPE)
                .setTitleText("Info")
                .setContentText("Click On Dish Name to select for combo")
                .setConfirmText("Ok, Got it")
                .setConfirmClickListener(kAlertDialog -> {
                    kAlertDialog.dismissWithAnimation();
                    Intent intent = new Intent(AddDishToCurrentCombo.this,SelectDishForCurrentCombo.class);
                    intent.putExtra("dishType","Snacks");
                    intent.putExtra("comboName",comboName);
                    intent.putExtra("state",sharedPreferences.getString("state",""));
                    intent.putExtra("locality",sharedPreferences.getString("locality",""));
                    startActivity(intent);
//                                kAlertDialog.dismissWithAnimation();
                }).show());
        drinks.setOnClickListener(view -> new KAlertDialog(AddDishToCurrentCombo.this,KAlertDialog.NORMAL_TYPE)
                .setTitleText("Info")
                .setContentText("Click On Dish Name to select for combo")
                .setConfirmText("Ok, Got it")
                .setConfirmClickListener(kAlertDialog -> {
                    kAlertDialog.dismissWithAnimation();
                    Intent intent = new Intent(AddDishToCurrentCombo.this,SelectDishForCurrentCombo.class);
                    intent.putExtra("dishType","Drinks");
                    intent.putExtra("comboName",comboName);
                    intent.putExtra("state",sharedPreferences.getString("state",""));
                    intent.putExtra("locality",sharedPreferences.getString("locality",""));
                    startActivity(intent);
//                                kAlertDialog.dismissWithAnimation();
                }).show());
    }
}