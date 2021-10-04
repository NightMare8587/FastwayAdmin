package com.consumers.fastwayadmin.DiscountCombo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.consumers.fastwayadmin.R;
import com.developer.kalert.KAlertDialog;
import com.example.flatdialoglibrary.dialog.FlatDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import mehdi.sakout.fancybuttons.FancyButton;

public class CustomOffer extends AppCompatActivity {
    List<String> dishName = new ArrayList<>();
    FirebaseAuth auth;
    FancyButton mainCourse,breads,snacks,deserts,drinks;
    Button proceed;
    RecyclerView recyclerView;
    List<String> dishQuantity = new ArrayList<>();
    SharedPreferences sharedPreferences;
    LinearLayoutManager horizonatl;
    DatabaseReference reference;
    DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_offer);
        auth = FirebaseAuth.getInstance();
        sharedPreferences = getSharedPreferences("loginInfo",MODE_PRIVATE);
        horizonatl = new LinearLayoutManager(CustomOffer.this,LinearLayoutManager.HORIZONTAL,false);
        databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(sharedPreferences.getString("state","")).child(Objects.requireNonNull(auth.getUid())).child("List of Dish");
        initialise();

        mainCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new KAlertDialog(CustomOffer.this,KAlertDialog.NORMAL_TYPE)
                        .setTitleText("Info")
                        .setContentText("Click On Dish Name to select for combo")
                        .setConfirmText("Ok, Got it")
                        .setConfirmClickListener(new KAlertDialog.KAlertClickListener() {
                            @Override
                            public void onClick(KAlertDialog kAlertDialog) {
                                kAlertDialog.dismissWithAnimation();
                                Intent intent = new Intent(CustomOffer.this,SelectDishForCombo.class);
                                intent.putExtra("dishType","Main Course");
                                intent.putExtra("state",sharedPreferences.getString("state",""));
                                startActivity(intent);
                            }
                        }).show();
            }
        });
        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final FlatDialog flatDialog = new FlatDialog(CustomOffer.this);
                flatDialog.setCanceledOnTouchOutside(true);
                flatDialog.setTitle("Choose One Option")
                        .setTitleColor(Color.BLACK)
                        .setBackgroundColor(Color.parseColor("#f9fce1"))
                        .setFirstButtonColor(Color.parseColor("#d3f6f3"))
                        .setFirstButtonTextColor(Color.parseColor("#000000"))
                        .setFirstButtonText("ADD CUSTOM DISCOUNT")
                        .setSecondButtonColor(Color.parseColor("#fee9b2"))
                        .setSecondButtonTextColor(Color.parseColor("#000000"))
                        .setSecondButtonText("ADD FREE DISH")
                        .withFirstButtonListner(view11 -> {
                            flatDialog.dismiss();
                        })
                        .withSecondButtonListner(view112 -> {
                            flatDialog.dismiss();
                        })
                        .show();
            }
        });
        reference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(sharedPreferences.getString("state","")).child(Objects.requireNonNull(auth.getUid()));
        reference.child("Current combo").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                updateChild();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                updateChild();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                updateChild();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        reference.child("Current combo").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    dishName.clear();
                    dishQuantity.clear();
//                    Toast.makeText(ComboAndOffers.this, "Yes", Toast.LENGTH_SHORT).show();
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
//                       Toast.makeText(ComboAndOffers.this, ""+dataSnapshot.child("name").getValue(), Toast.LENGTH_SHORT).show();
                        dishName.add(String.valueOf(dataSnapshot.child("name").getValue()));
                        dishQuantity.add(String.valueOf(dataSnapshot.child("quantity").getValue()));
                    }
                    proceed.setVisibility(View.VISIBLE);
                    Log.i("log",dishName.toString());
                    Log.i("log",dishQuantity.toString());
                    recyclerView.setLayoutManager(horizonatl);
                    recyclerView.setAdapter(new comboAdapter(dishName));
                }else {
//                    Toast.makeText(ComboAndOffers.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                    proceed.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        breads.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new KAlertDialog(CustomOffer.this,KAlertDialog.NORMAL_TYPE)
                        .setTitleText("Info")
                        .setContentText("Click On Dish Name to select for combo")
                        .setConfirmText("Ok, Got it")
                        .setConfirmClickListener(new KAlertDialog.KAlertClickListener() {
                            @Override
                            public void onClick(KAlertDialog kAlertDialog) {
                                kAlertDialog.dismissWithAnimation();
                                Intent intent = new Intent(CustomOffer.this,SelectDishForCombo.class);
                                intent.putExtra("dishType","Breads");
                                intent.putExtra("state",sharedPreferences.getString("state",""));
                                startActivity(intent);
//                                kAlertDialog.dismissWithAnimation();
                            }
                        }).show();

            }
        });
        deserts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new KAlertDialog(CustomOffer.this,KAlertDialog.NORMAL_TYPE)
                        .setTitleText("Info")
                        .setContentText("Click On Dish Name to select for combo")
                        .setConfirmText("Ok, Got it")
                        .setConfirmClickListener(new KAlertDialog.KAlertClickListener() {
                            @Override
                            public void onClick(KAlertDialog kAlertDialog) {
                                kAlertDialog.dismissWithAnimation();
                                Intent intent = new Intent(CustomOffer.this,SelectDishForCombo.class);
                                intent.putExtra("dishType","Deserts");
                                intent.putExtra("state",sharedPreferences.getString("state",""));
                                startActivity(intent);
//                                kAlertDialog.dismissWithAnimation();
                            }
                        }).show();
            }
        });
        snacks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new KAlertDialog(CustomOffer.this,KAlertDialog.NORMAL_TYPE)
                        .setTitleText("Info")
                        .setContentText("Click On Dish Name to select for combo")
                        .setConfirmText("Ok, Got it")
                        .setConfirmClickListener(new KAlertDialog.KAlertClickListener() {
                            @Override
                            public void onClick(KAlertDialog kAlertDialog) {
                                kAlertDialog.dismissWithAnimation();
                                Intent intent = new Intent(CustomOffer.this,SelectDishForCombo.class);
                                intent.putExtra("dishType","Snacks");
                                intent.putExtra("state",sharedPreferences.getString("state",""));
                                startActivity(intent);
//                                kAlertDialog.dismissWithAnimation();
                            }
                        }).show();
            }
        });
        drinks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new KAlertDialog(CustomOffer.this,KAlertDialog.NORMAL_TYPE)
                        .setTitleText("Info")
                        .setContentText("Click On Dish Name to select for combo")
                        .setConfirmText("Ok, Got it")
                        .setConfirmClickListener(new KAlertDialog.KAlertClickListener() {
                            @Override
                            public void onClick(KAlertDialog kAlertDialog) {
                                kAlertDialog.dismissWithAnimation();
                                Intent intent = new Intent(CustomOffer.this,SelectDishForCombo.class);
                                intent.putExtra("dishType","Drinks");
                                intent.putExtra("state",sharedPreferences.getString("state",""));
                                startActivity(intent);
//                                kAlertDialog.dismissWithAnimation();
                            }
                        }).show();
            }
        });
    }

    public void updateChild(){
        reference.child("Current combo").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    dishName.clear();
//                    Toast.makeText(ComboAndOffers.this, "Yes", Toast.LENGTH_SHORT).show();
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
//                       Toast.makeText(ComboAndOffers.this, ""+dataSnapshot.child("name").getValue(), Toast.LENGTH_SHORT).show();
                        dishName.add(String.valueOf(dataSnapshot.child("name").getValue()));
                    }
                    proceed.setVisibility(View.VISIBLE);
                    Log.i("log",dishName.toString());
                    recyclerView.setLayoutManager(horizonatl);
                    recyclerView.setAdapter(new comboAdapter(dishName));
                }else {
                    Toast.makeText(CustomOffer.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                    proceed.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void initialise() {
        mainCourse = findViewById(R.id.MainCourseCustom);
        breads = findViewById(R.id.BreadsCustom);
        snacks = findViewById(R.id.SnacksCustom);
        deserts = findViewById(R.id.DesertsCustom);
        drinks = findViewById(R.id.DrinksCustom);
        recyclerView = findViewById(R.id.customOfferRecyclerView);
        proceed = findViewById(R.id.currentCustomCheckButton);
    }
}