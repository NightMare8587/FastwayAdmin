package com.consumers.fastwayadmin.DiscountCombo;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import karpuzoglu.enes.com.fastdialog.Type;
import mehdi.sakout.fancybuttons.FancyButton;

public class ComboAndOffers extends AppCompatActivity {
    FancyButton mainCourse,breads,snacks,deserts,drinks;
    RecyclerView recyclerView;
    List<String> dishQuantity = new ArrayList<>();
    DatabaseReference reference;
    Button createCombo;
    String dishType = "Veg";
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

                            AlertDialog.Builder builder = new AlertDialog.Builder(ComboAndOffers.this);
                            builder.setTitle("Dish Type").setMessage("Select Dish Type from below options");
                            RadioGroup radioGroup = new RadioGroup(ComboAndOffers.this);
                            radioGroup.setOrientation(LinearLayout.HORIZONTAL);
                            LinearLayout linearLayout = new LinearLayout(ComboAndOffers.this);
                            linearLayout.setOrientation(LinearLayout.VERTICAL);
                            RadioButton veg = new RadioButton(ComboAndOffers.this);
                            veg.setText("Veg");
                            veg.setId(0);
                            veg.setChecked(true);
                            radioGroup.addView(veg);
                            RadioButton vegan = new RadioButton(ComboAndOffers.this);
                            vegan.setText("Vegan");
                            vegan.setId(0 + 1);
                            radioGroup.addView(vegan);
                            RadioButton NonVeg = new RadioButton(ComboAndOffers.this);
                            NonVeg.setText("NonVeg");
                            NonVeg.setId(0 + 2);
                            radioGroup.addView(NonVeg);

                            radioGroup.setOnCheckedChangeListener((radioGroup1, i) -> {
                                if(i == 0)
                                    dishType = "Veg";
                                else if(i == 0 + 1)
                                    dishType = "Vegan";
                                else
                                    dishType = "NonVeg";
                            });

                            builder.setPositiveButton("Proceed", (dialogInterface, i) -> {
                                dialogInterface.dismiss();
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
                                            reference.child("Combo").child(comboName).child("dishType").setValue(dishType);
                                            reference.child("Combo").child(comboName).child("totalRate").setValue("0");
                                            Toast.makeText(ComboAndOffers.this, "You can add image to combo later", Toast.LENGTH_SHORT).show();

                                            name.clear();
                                            dishQuantity.clear();
                                            recyclerView.setAdapter(new comboAdapter(name));
                                            android.app.AlertDialog.Builder builder1 = new android.app.AlertDialog.Builder(ComboAndOffers.this);
                                            builder1.setTitle("New Description").setMessage("Enter new description in below field");
                                            LinearLayout linearLayout1 = new LinearLayout(ComboAndOffers.this);
                                            linearLayout1.setOrientation(LinearLayout.VERTICAL);
                                            EditText editText = new EditText(ComboAndOffers.this);
                                            editText.setHint("Enter description here");
                                            editText.setMaxLines(200);
                                            editText.setInputType(InputType.TYPE_CLASS_TEXT);
                                            builder1.setPositiveButton("Add Description", (dialogInterface1, i1) -> {
                                                if(editText.getText().toString().equals("")){
                                                    Toast.makeText(ComboAndOffers.this, "Invalid Input", Toast.LENGTH_SHORT).show();
                                                    return;
                                                }
                                                SharedPreferences sharedPreferences = getSharedPreferences("loginInfo", Context.MODE_PRIVATE);
                                                FirebaseAuth auth = FirebaseAuth.getInstance();
                                                DatabaseReference reference =  FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(sharedPreferences.getString("state","")).child(Objects.requireNonNull(auth.getUid())).child("List of Dish").child("Combo").child(comboName);
                                                reference.child("description").setValue(editText.getText().toString());
                                                Toast.makeText(ComboAndOffers.this, "Description Added Successfully", Toast.LENGTH_SHORT).show();
                                                dialogInterface1.dismiss();
                                                new KAlertDialog(ComboAndOffers.this, KAlertDialog.SUCCESS_TYPE)
                                                        .setTitleText("Success")
                                                        .setContentText("combo created successfully")
                                                        .setConfirmText("Ok, Great")
                                                        .setConfirmClickListener(kAlertDialog1 -> {
                                                            AlertDialog.Builder alert = new AlertDialog.Builder(ComboAndOffers.this);
                                                            alert.setTitle("Add Image");
                                                            alert.setMessage("Do you wanna add image to your combo. You can skip this step for now if you want");
                                                            alert.setPositiveButton("Skip", (dialogInterface, i) -> {
                                                                dialogInterface.dismiss();
                                                                finish();
                                                            }).setNegativeButton("Add Image", (dialogInterface, i) -> {
                                                                dialogInterface.dismiss();
                                                                Intent intent = new Intent(ComboAndOffers.this, AddImageToDish.class);
                                                                intent.putExtra("type","Combo");
                                                                intent.putExtra("dishName",comboName);
                                                                startActivity(intent);
                                                            });

                                                            alert.create().show();
                                                            kAlertDialog1.dismissWithAnimation();
                                                            priceDialog.dismiss();

                                                        }).show();
                                            }).setNegativeButton("Cancel", (dialogInterface, i) -> new KAlertDialog(ComboAndOffers.this, KAlertDialog.SUCCESS_TYPE)
                                                    .setTitleText("Success")
                                                    .setContentText("combo created successfully")
                                                    .setConfirmText("Ok, Great")
                                                    .setConfirmClickListener(kAlertDialog1 -> {
                                                        AlertDialog.Builder alert = new AlertDialog.Builder(ComboAndOffers.this);
                                                        alert.setTitle("Add Image");
                                                        alert.setMessage("Do you wanna add image to your combo. You can skip this step for now if you want");
                                                        alert.setPositiveButton("Skip", (dialogInterface12, i12) -> {
                                                            dialogInterface12.dismiss();
                                                            finish();
                                                        }).setNegativeButton("Add Image", (dialogInterface13, i13) -> {
                                                            dialogInterface13.dismiss();
                                                            Intent intent = new Intent(ComboAndOffers.this, AddImageToDish.class);
                                                            intent.putExtra("type","Combo");
                                                            intent.putExtra("dishName",comboName);
                                                            startActivity(intent);
                                                        });

                                                        alert.create().show();
                                                        kAlertDialog1.dismissWithAnimation();
                                                        priceDialog.dismiss();

                                                    }).show()).create();
                                            linearLayout1.addView(editText);
                                            builder1.setView(linearLayout1);
                                            builder1.setCancelable(false);
                                            builder1.create().show();

                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        });


                    });
                            linearLayout.addView(radioGroup);
                            builder.setView(linearLayout);
                            builder.create().show();

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