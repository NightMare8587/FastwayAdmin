package com.consumers.fastwayadmin.DiscountCombo;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.consumers.fastwayadmin.Dish.AddImageToDish;
import com.consumers.fastwayadmin.R;
import com.developer.kalert.KAlertDialog;
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

import karpuzoglu.enes.com.fastdialog.Animations;
import karpuzoglu.enes.com.fastdialog.FastDialog;
import karpuzoglu.enes.com.fastdialog.FastDialogBuilder;
import karpuzoglu.enes.com.fastdialog.PositiveClick;
import karpuzoglu.enes.com.fastdialog.Type;
import mehdi.sakout.fancybuttons.FancyButton;

public class ComboAndOffers extends AppCompatActivity {
    FancyButton mainCourse,breads,snacks,deserts,drinks;
    RecyclerView recyclerView;
    List<String> dishQuantity = new ArrayList<>();
    DatabaseReference reference;
    Button createCombo;
    SharedPreferences sharedPreferences;
    List<String> name = new ArrayList<>();
    FirebaseAuth auth;
    String state;
    LinearLayoutManager horizonatl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_combo_and_offers);
        sharedPreferences = getSharedPreferences("loginInfo",MODE_PRIVATE);
        state = sharedPreferences.getString("state","");
        initialise();
        horizonatl = new LinearLayoutManager(ComboAndOffers.this,LinearLayoutManager.HORIZONTAL,false);
        mainCourse.setOnClickListener(view -> new KAlertDialog(ComboAndOffers.this,KAlertDialog.NORMAL_TYPE)
                .setTitleText("Info")
                .setContentText("Click On Dish Name to select for combo")
                .setConfirmText("Ok, Got it")
                .setConfirmClickListener(kAlertDialog -> {
                    kAlertDialog.dismissWithAnimation();
                    Intent intent = new Intent(ComboAndOffers.this,SelectDishForCombo.class);
                    intent.putExtra("dishType","Main Course");
                    intent.putExtra("state",state);
                    startActivity(intent);
                }).show());
        createCombo.setOnClickListener(view -> new KAlertDialog(ComboAndOffers.this,KAlertDialog.WARNING_TYPE)
                .setTitleText("Make Combo")
                .setContentText("Do you sure wanna make this combo")
                .setConfirmText("Yes")
                .setCancelText("No")
                .setConfirmClickListener(kAlertDialog -> {
                   kAlertDialog.dismissWithAnimation();
                    FastDialog dialog = new FastDialogBuilder(ComboAndOffers.this, Type.DIALOG)
                            .setTitleText("Name Of Combo")
                            .setText("Enter Name Of Combo below")
                            .setHint("Enter Name Of Combo")
                            .positiveText("Create")
                            .setAnimation(Animations.GROW_IN)
                            .cancelable(false)
                            .negativeText("Cancel").create();

                    dialog.show();

                    dialog.positiveClickListener(view12 -> {
                        if(dialog.getInputText().length() == 0){
                            Toast.makeText(ComboAndOffers.this, "Enter some name", Toast.LENGTH_SHORT).show();
                        }else {
                            String comboName = dialog.getInputText();
                            dialog.dismiss();
                            auth = FirebaseAuth.getInstance();
                            FastDialog priceDialog = new FastDialogBuilder(ComboAndOffers.this, Type.DIALOG)
                                    .setTitleText("Price Of Combo")
                                    .setText("Enter Price Of Combo below")
                                    .setHint("Enter Price Of Combo")
                                    .positiveText("Confirm")
                                    .setAnimation(Animations.GROW_IN)
                                    .cancelable(false)
                                    .negativeText("Cancel").create();

                            priceDialog.show();

                            priceDialog.positiveClickListener(view121 -> {
                                if(!(priceDialog.getInputText().length() == 0)) {

                                    reference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(state).child(Objects.requireNonNull(auth.getUid())).child("Current combo");
                                    reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.exists()) {
                                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                                    name.add(dataSnapshot.child("name").getValue(String.class));
                                                    dishQuantity.add(dataSnapshot.child("quantity").getValue(String.class));
                                                }
                                                reference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(state).child(Objects.requireNonNull(auth.getUid()));
                                                reference.child("Current combo").removeValue();
                                                reference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(state).child(Objects.requireNonNull(auth.getUid())).child("List of Dish");
                                                for (int i = 0; i < name.size(); i++) {
                                                    combo combo = new combo(name.get(i),dishQuantity.get(i));
                                                    reference.child("Combo").child(comboName).child(name.get(i)).child("name").setValue(combo);
                                                }
                                                reference.child("Combo").child(comboName).child("price").setValue(priceDialog.getInputText());
                                                reference.child("Combo").child(comboName).child("count").setValue("0");
                                                reference.child("Combo").child(comboName).child("enable").setValue("yes");
                                                reference.child("Combo").child(comboName).child("rating").setValue("0");
                                                reference.child("Combo").child(comboName).child("totalRate").setValue("0");
                                                Toast.makeText(ComboAndOffers.this, "You can add image to combo later", Toast.LENGTH_SHORT).show();

                                                name.clear();
                                                dishQuantity.clear();
                                                recyclerView.setAdapter(new comboAdapter(name));
                                                new KAlertDialog(ComboAndOffers.this, KAlertDialog.SUCCESS_TYPE)
                                                        .setTitleText("Success")
                                                        .setContentText("combo created successfully")
                                                        .setConfirmText("Ok, Great")
                                                        .setConfirmClickListener(new KAlertDialog.KAlertClickListener() {
                                                            @Override
                                                            public void onClick(KAlertDialog kAlertDialog1) {
                                                                AlertDialog.Builder alert = new AlertDialog.Builder(ComboAndOffers.this);
                                                                alert.setTitle("Add Image");
                                                                alert.setMessage("Do you wanna add image to your combo. You can skip this step for now if you want");
                                                                alert.setPositiveButton("Skip", new DialogInterface.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                                        dialogInterface.dismiss();
                                                                        finish();
                                                                    }
                                                                }).setNegativeButton("Add Image", new DialogInterface.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                                        dialogInterface.dismiss();
                                                                        Intent intent = new Intent(ComboAndOffers.this, AddImageToDish.class);
                                                                        intent.putExtra("type","Combo");
                                                                        intent.putExtra("dishName",comboName);
                                                                        startActivity(intent);
                                                                        finish();
                                                                    }
                                                                });

                                                                alert.create().show();
                                                                kAlertDialog1.dismissWithAnimation();
                                                                priceDialog.dismiss();

                                                            }
                                                        }).show();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                }
                            });


                        }
                    });

                    dialog.negativeClickListener(view1 -> dialog.dismiss());
                })
                .setCancelClickListener(KAlertDialog::dismissWithAnimation).show());
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
                    name.clear();
//                    Toast.makeText(ComboAndOffers.this, "Yes", Toast.LENGTH_SHORT).show();
                   for(DataSnapshot dataSnapshot : snapshot.getChildren()){
//                       Toast.makeText(ComboAndOffers.this, ""+dataSnapshot.child("name").getValue(), Toast.LENGTH_SHORT).show();
                        name.add(String.valueOf(dataSnapshot.child("name").getValue()));
                   }
                   createCombo.setVisibility(View.VISIBLE);
                    Log.i("log",name.toString());
                    recyclerView.setLayoutManager(horizonatl);
                   recyclerView.setAdapter(new comboAdapter(name));
                }else {
//                    Toast.makeText(ComboAndOffers.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                    createCombo.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        breads.setOnClickListener(view -> new KAlertDialog(ComboAndOffers.this,KAlertDialog.NORMAL_TYPE)
                .setTitleText("Info")
                .setContentText("Click On Dish Name to select for combo")
                .setConfirmText("Ok, Got it")
                .setConfirmClickListener(kAlertDialog -> {
                    kAlertDialog.dismissWithAnimation();
                    Intent intent = new Intent(ComboAndOffers.this,SelectDishForCombo.class);
                    intent.putExtra("dishType","Breads");
                    intent.putExtra("state",state);
                    startActivity(intent);
//                                kAlertDialog.dismissWithAnimation();
                }).show());
        deserts.setOnClickListener(view -> new KAlertDialog(ComboAndOffers.this,KAlertDialog.NORMAL_TYPE)
                .setTitleText("Info")
                .setContentText("Click On Dish Name to select for combo")
                .setConfirmText("Ok, Got it")
                .setConfirmClickListener(kAlertDialog -> {
                    kAlertDialog.dismissWithAnimation();
                    Intent intent = new Intent(ComboAndOffers.this,SelectDishForCombo.class);
                    intent.putExtra("dishType","Deserts");
                    intent.putExtra("state",state);
                    startActivity(intent);
//                                kAlertDialog.dismissWithAnimation();
                }).show());
        snacks.setOnClickListener(view -> new KAlertDialog(ComboAndOffers.this,KAlertDialog.NORMAL_TYPE)
                .setTitleText("Info")
                .setContentText("Click On Dish Name to select for combo")
                .setConfirmText("Ok, Got it")
                .setConfirmClickListener(kAlertDialog -> {
                    kAlertDialog.dismissWithAnimation();
                    Intent intent = new Intent(ComboAndOffers.this,SelectDishForCombo.class);
                    intent.putExtra("dishType","Snacks");
                    intent.putExtra("state",state);
                    startActivity(intent);
//                                kAlertDialog.dismissWithAnimation();
                }).show());
        drinks.setOnClickListener(view -> new KAlertDialog(ComboAndOffers.this,KAlertDialog.NORMAL_TYPE)
                .setTitleText("Info")
                .setContentText("Click On Dish Name to select for combo")
                .setConfirmText("Ok, Got it")
                .setConfirmClickListener(kAlertDialog -> {
                    kAlertDialog.dismissWithAnimation();
                    Intent intent = new Intent(ComboAndOffers.this,SelectDishForCombo.class);
                    intent.putExtra("dishType","Drinks");
                    intent.putExtra("state",state);
                    startActivity(intent);
//                                kAlertDialog.dismissWithAnimation();
                }).show());
    }

    public void updateChild(){
        reference.child("Current combo").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    name.clear();
                    dishQuantity.clear();
//                    Toast.makeText(ComboAndOffers.this, "Yes", Toast.LENGTH_SHORT).show();
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
//                       Toast.makeText(ComboAndOffers.this, ""+dataSnapshot.child("name").getValue(), Toast.LENGTH_SHORT).show();
                        name.add(String.valueOf(dataSnapshot.child("name").getValue()));
                        dishQuantity.add(String.valueOf(dataSnapshot.child("quantity").getValue()));
                    }
                    createCombo.setVisibility(View.VISIBLE);
                    Log.i("log",name.toString());
                    Log.i("log",dishQuantity.toString());
                    recyclerView.setLayoutManager(horizonatl);
                    recyclerView.setAdapter(new comboAdapter(name));
                }else {
//                    Toast.makeText(ComboAndOffers.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                    createCombo.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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
        createCombo = findViewById(R.id.addToComboButton);
        auth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(state).child(Objects.requireNonNull(auth.getUid()));

    }
}