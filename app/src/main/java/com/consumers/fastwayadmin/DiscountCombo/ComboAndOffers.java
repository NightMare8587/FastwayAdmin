package com.consumers.fastwayadmin.DiscountCombo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.consumers.fastwayadmin.R;
import com.developer.kalert.KAlertDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import mehdi.sakout.fancybuttons.FancyButton;

public class ComboAndOffers extends AppCompatActivity {
    FancyButton mainCourse,breads,snacks,deserts,drinks;
    RecyclerView recyclerView;
    DatabaseReference reference;
    List<String> name = new ArrayList<>();
    FirebaseAuth auth;
    LinearLayoutManager horizonatl;
    Toolbar menuBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_combo_and_offers);
        initialise();
        mainCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new KAlertDialog(ComboAndOffers.this,KAlertDialog.NORMAL_TYPE)
                        .setTitleText("Info")
                        .setContentText("Click On Dish Name to select for combo")
                        .setConfirmText("Ok, Got it")
                        .setConfirmClickListener(new KAlertDialog.KAlertClickListener() {
                            @Override
                            public void onClick(KAlertDialog kAlertDialog) {
                                kAlertDialog.dismissWithAnimation();
                                Intent intent = new Intent(ComboAndOffers.this,SelectDishForCombo.class);
                                intent.putExtra("dishType","Main Course");
                                startActivity(intent);
                            }
                        }).show();
            }
        });
        reference.child("Current Combo").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                   for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                        name.add(String.valueOf(dataSnapshot.child("name").getValue()));
                   }
                   recyclerView.setAdapter(new comboAdapter(name));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        breads.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new KAlertDialog(ComboAndOffers.this,KAlertDialog.NORMAL_TYPE)
                        .setTitleText("Info")
                        .setContentText("Click On Dish Name to select for combo")
                        .setConfirmText("Ok, Got it")
                        .setConfirmClickListener(new KAlertDialog.KAlertClickListener() {
                            @Override
                            public void onClick(KAlertDialog kAlertDialog) {
                                kAlertDialog.dismissWithAnimation();
                                Intent intent = new Intent(ComboAndOffers.this,SelectDishForCombo.class);
                                intent.putExtra("dishType","Breads");
                                startActivity(intent);
//                                kAlertDialog.dismissWithAnimation();
                            }
                        }).show();

            }
        });
        deserts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new KAlertDialog(ComboAndOffers.this,KAlertDialog.NORMAL_TYPE)
                        .setTitleText("Info")
                        .setContentText("Click On Dish Name to select for combo")
                        .setConfirmText("Ok, Got it")
                        .setConfirmClickListener(new KAlertDialog.KAlertClickListener() {
                            @Override
                            public void onClick(KAlertDialog kAlertDialog) {
                                kAlertDialog.dismissWithAnimation();
                                Intent intent = new Intent(ComboAndOffers.this,SelectDishForCombo.class);
                                intent.putExtra("dishType","Deserts");
                                startActivity(intent);
//                                kAlertDialog.dismissWithAnimation();
                            }
                        }).show();
            }
        });
        snacks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new KAlertDialog(ComboAndOffers.this,KAlertDialog.NORMAL_TYPE)
                        .setTitleText("Info")
                        .setContentText("Click On Dish Name to select for combo")
                        .setConfirmText("Ok, Got it")
                        .setConfirmClickListener(new KAlertDialog.KAlertClickListener() {
                            @Override
                            public void onClick(KAlertDialog kAlertDialog) {
                                kAlertDialog.dismissWithAnimation();
                                Intent intent = new Intent(ComboAndOffers.this,SelectDishForCombo.class);
                                intent.putExtra("dishType","Snacks");
                                startActivity(intent);
//                                kAlertDialog.dismissWithAnimation();
                            }
                        }).show();
            }
        });
        drinks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new KAlertDialog(ComboAndOffers.this,KAlertDialog.NORMAL_TYPE)
                        .setTitleText("Info")
                        .setContentText("Click On Dish Name to select for combo")
                        .setConfirmText("Ok, Got it")
                        .setConfirmClickListener(new KAlertDialog.KAlertClickListener() {
                            @Override
                            public void onClick(KAlertDialog kAlertDialog) {
                                kAlertDialog.dismissWithAnimation();
                                Intent intent = new Intent(ComboAndOffers.this,SelectDishForCombo.class);
                                intent.putExtra("dishType","Drinks");
                                startActivity(intent);
//                                kAlertDialog.dismissWithAnimation();
                            }
                        }).show();
            }
        });
    }

    private void initialise() {
        mainCourse = findViewById(R.id.comboMainCourse);
        breads = findViewById(R.id.comboBreads);
        snacks = findViewById(R.id.comboSnacks);
        deserts = findViewById(R.id.comboDeserts);
        drinks = findViewById(R.id.comboDrinks);
        recyclerView = findViewById(R.id.comboAndOfferRecyclerView);
        horizonatl = new LinearLayoutManager(ComboAndOffers.this,LinearLayoutManager.HORIZONTAL,false);
        auth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(Objects.requireNonNull(auth.getUid()));
        recyclerView.setLayoutManager(horizonatl);
    }
}