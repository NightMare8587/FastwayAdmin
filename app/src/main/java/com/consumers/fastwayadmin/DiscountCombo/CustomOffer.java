package com.consumers.fastwayadmin.DiscountCombo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
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
import com.thecode.aestheticdialogs.AestheticDialog;
import com.thecode.aestheticdialogs.DialogAnimation;
import com.thecode.aestheticdialogs.DialogStyle;
import com.thecode.aestheticdialogs.DialogType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import karpuzoglu.enes.com.fastdialog.Animations;
import karpuzoglu.enes.com.fastdialog.FastDialog;
import karpuzoglu.enes.com.fastdialog.FastDialogBuilder;
import karpuzoglu.enes.com.fastdialog.Type;
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
    DatabaseReference dis;
    DatabaseReference addToDB;
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
                            FastDialog fastDialog = new FastDialogBuilder(view.getContext(), Type.DIALOG)
                                    .setTitleText("Enter Quantity")
                                    .setText("Enter how much discount on dish")
                                    .setHint("Enter here")
                                    .positiveText("Proceed")
                                    .negativeText("Cancel")
                                    .setAnimation(Animations.SLIDE_TOP)
                                    .create();

                            fastDialog.show();

                            fastDialog.positiveClickListener(click -> {
                                if(!fastDialog.getInputText().equals("") && !fastDialog.getInputText().equals("0")) {
                                    for (int i = 0; i < dishName.size(); i++) {
                                        auth = FirebaseAuth.getInstance();
                                        reference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(sharedPreferences.getString("state", "")).child(Objects.requireNonNull(auth.getUid()));
                                        reference.child("List of Dish").addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                                        if (String.valueOf(dataSnapshot1.child("mrp").getValue()).equals("no")) {
                                                            String type = String.valueOf(dataSnapshot.getKey());
                                                            String dishName = String.valueOf(dataSnapshot1.child("name").getValue());
                                                            if (Integer.parseInt(Objects.requireNonNull(String.valueOf(dataSnapshot1.child("full").getValue()))) >= 149) {
                                                                if(!String.valueOf(dataSnapshot1.child("half").getValue()).equals("")) {
                                                                    int price = Integer.parseInt(Objects.requireNonNull(String.valueOf(dataSnapshot1.child("full").getValue())));
                                                                    int halfPrice = Integer.parseInt(Objects.requireNonNull(String.valueOf(dataSnapshot1.child("half").getValue())));
                                                                    int discount = Integer.parseInt(fastDialog.getInputText());
                                                                    int afterDis = price - (price * discount / 100);
                                                                    int afterDisHalf = halfPrice - (halfPrice * discount / 100);
                                                                    beforeDiscount(price, afterDis, discount, type, dishName, halfPrice);
                                                                    addToDiscountDatabase("yes");
                                                                    auth = FirebaseAuth.getInstance();
                                                                    Log.i("type", type);
                                                                    Log.i("name", dishName);
//                                reference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(auth.getUid()).child("List of Dish");
                                                                    reference.child("List of Dish").child(type).child(dishName).child("full").setValue(afterDis);
                                                                    reference.child("List of Dish").child(type).child(dishName).child("half").setValue(afterDisHalf);
                                                                }else{
                                                                    int price = Integer.parseInt(Objects.requireNonNull(String.valueOf(dataSnapshot1.child("full").getValue())));
                                                                    int discount = Integer.parseInt(fastDialog.getInputText());
                                                                    int afterDis = price - (price * discount / 100);
                                                                    beforeDiscount(price,afterDis,discount,type,dishName,0);
                                                                    addToDiscountDatabase("yes");
                                                                    auth = FirebaseAuth.getInstance();
                                                                    Log.i("type",type);
                                                                    Log.i("name",dishName);
//                                reference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(auth.getUid()).child("List of Dish");
                                                                    reference.child("List of Dish").child(type).child(dishName).child("full").setValue(afterDis);
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                                AestheticDialog.Builder builder = new AestheticDialog.Builder(CustomOffer.this, DialogStyle.FLAT, DialogType.SUCCESS);
                                                builder.setTitle("Applying Discount")
                                                        .setMessage("Wait while we are applying discount :)")
                                                        .setCancelable(false)
                                                        .setDuration(3000)
                                                        .setAnimation(DialogAnimation.SHRINK)
                                                        .setDarkMode(true);

                                                builder.show();

                                                new Handler().postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        builder.dismiss();
                                                        finish();
                                                    }
                                                }, 3000);
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                    }
                                }else Toast.makeText(CustomOffer.this, "Field can't be empty", Toast.LENGTH_SHORT).show();
                            });


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
                }else {
//                    Toast.makeText(CustomOffer.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                    proceed.setVisibility(View.INVISIBLE);
                    dishName.clear();
                }
                recyclerView.setLayoutManager(horizonatl);
                recyclerView.setAdapter(new comboAdapter(dishName));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void beforeDiscount(int price,int after, int discount,String type,String name,int halfPrice) {
        DisInfo disInfo;
        if(halfPrice == 0)
            disInfo = new DisInfo(String.valueOf(price),String.valueOf(after),String.valueOf(discount),"");
        else
            disInfo = new DisInfo(String.valueOf(price),String.valueOf(after),String.valueOf(discount),""+ halfPrice);

        dis = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(sharedPreferences.getString("state","")).child(Objects.requireNonNull(auth.getUid()));
        dis.child("List of Dish").child(type).child(name).child("Discount").child(name).setValue(disInfo);
    }
    private void addToDiscountDatabase(String discount) {
        auth = FirebaseAuth.getInstance();
        addToDB = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(sharedPreferences.getString("state","")).child(Objects.requireNonNull(auth.getUid()));
        addToDB.child("Discount").child("available").setValue("yes");
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