package com.consumers.fastwayadmin.DiscountCombo;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.consumers.fastwayadmin.R;
import com.example.flatdialoglibrary.dialog.FlatDialog;
import com.google.firebase.auth.FirebaseAuth;
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

public class DiscountActivity extends AppCompatActivity {
    DatabaseReference reference;
    DatabaseReference dis;
    DatabaseReference addToDB;
    FirebaseAuth auth;
    List<String> name = new ArrayList<>();
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discount);
        initialise();
        name.clear();
        reference.child("List of Dish").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        if (dataSnapshot1.exists() && String.valueOf(dataSnapshot1.child("mrp").getValue()).equals("no"))
                            name.add(dataSnapshot1.child("name").getValue(String.class));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        FlatDialog flatDialog = new FlatDialog(DiscountActivity.this);
        flatDialog.setTitle("Choose One Option")
                .setTitleColor(Color.BLACK)
                .setBackgroundColor(Color.parseColor("#f9fce1"))
                .setFirstButtonColor(Color.parseColor("#d3f6f3"))
                .setFirstButtonTextColor(Color.parseColor("#000000"))
                .setFirstButtonText("DISCOUNT ON ALL DISH")
                .setSecondButtonColor(Color.parseColor("#fee9b2"))
                .setSecondButtonTextColor(Color.parseColor("#000000"))
                .setSecondButtonText("LET ME CHOOSE")
                .withFirstButtonListner(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        FlatDialog flatDialog1 = new FlatDialog(DiscountActivity.this);
                        flatDialog1.setCanceledOnTouchOutside(true);
                        flatDialog1.setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialogInterface) {
                                finish();
                                Toast.makeText(DiscountActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
                                dialogInterface.dismiss();
                            }
                        });
                        flatDialog1.setTitle("Choose One Option")
                                .setSubtitle("Only Applicable for items above 149")
                                .setSubtitleColor(Color.BLACK)
                                .setTitleColor(Color.BLACK)
                                .setBackgroundColor(Color.parseColor("#f9fce1"))
                                .setFirstButtonColor(Color.parseColor("#d3f6f3"))
                                .setFirstButtonTextColor(Color.parseColor("#000000"))
                                .setFirstButtonText("50% OFF ON ALL")
                                .setSecondButtonColor(Color.parseColor("#fee9b2"))
                                .setSecondButtonTextColor(Color.parseColor("#000000"))
                                .setSecondButtonText("40% OFF ON ALL")
                                .setThirdButtonText("ADD YOUR OWN")
                                .setThirdButtonColor(Color.parseColor("#fbd1b7"))
                                .setThirdButtonTextColor(Color.parseColor("#000000"))
                                .setFirstTextFieldHint("Enter How much discount!!")
                                .setFirstTextFieldBorderColor(Color.BLACK)
                                .setFirstTextFieldHintColor(Color.BLACK)
                                .withFirstButtonListner(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        fiftyDiscount();
                                        flatDialog1.dismiss();
                                    }
                                })
                                .withSecondButtonListner(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        fourtyDiscount();
                                        flatDialog1.dismiss();
                                    }
                                })
                                .withThirdButtonListner(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        if (flatDialog1.getFirstTextField().equals("")) {

                                            Toast.makeText(DiscountActivity.this, "Field Can't be Empty", Toast.LENGTH_SHORT).show();
                                            return;
                                        } else {
                                            customDiscount(flatDialog1.getFirstTextField());
                                            flatDialog1.dismiss();
                                        }
                                    }
                                }).show();
                        flatDialog.dismiss();
                    }
                })
                .withSecondButtonListner(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        recyclerView.setLayoutManager(new LinearLayoutManager(DiscountActivity.this));
                        recyclerView.setAdapter(new DiscountRecycler(name,DiscountActivity.this));
                        flatDialog.dismiss();
                    }
                }).show();
    }

    private void customDiscount(String firstTextField) {
        auth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(Objects.requireNonNull(auth.getUid()));
        reference.child("List of Dish").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        if (String.valueOf(dataSnapshot1.child("mrp").getValue()).equals("no")) {
                            String type = String.valueOf(dataSnapshot.getKey());
                            String dishName = String.valueOf(dataSnapshot1.child("name").getValue());
                            if (Integer.parseInt(Objects.requireNonNull(String.valueOf(dataSnapshot1.child("full").getValue()))) >= 149) {
                                int price = Integer.parseInt(Objects.requireNonNull(String.valueOf(dataSnapshot1.child("full").getValue())));
                                int discount = Integer.parseInt(firstTextField);
                                int afterDis = price - (price * discount / 100);
                                beforeDiscount(price,afterDis,discount,type,dishName);
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

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void fourtyDiscount() {
        auth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(Objects.requireNonNull(auth.getUid()));
        reference.child("List of Dish").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        if (String.valueOf(dataSnapshot1.child("mrp").getValue()).equals("no")) {
                            String type = String.valueOf(dataSnapshot.getKey());
                            String dishName = String.valueOf(dataSnapshot1.child("name").getValue());
                            if (Integer.parseInt(Objects.requireNonNull(String.valueOf(dataSnapshot1.child("full").getValue()))) >= 149) {
                                int price = Integer.parseInt(Objects.requireNonNull(String.valueOf(dataSnapshot1.child("full").getValue())));
                                int discount = 40;
                                int afterDis = price - (price * discount / 100);
                                beforeDiscount(price,afterDis,discount,type,dishName);
                                addToDiscountDatabase("yes");
                                auth = FirebaseAuth.getInstance();
                                Log.i("type",type);
                                Log.i("name",dishName);
                                reference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(Objects.requireNonNull(auth.getUid()));
                                reference.child("List of Dish").child(type).child(dishName).child("full").setValue(afterDis);
                             }
                          }
                       }
                    }
                AestheticDialog.Builder builder = new AestheticDialog.Builder(DiscountActivity.this, DialogStyle.FLAT, DialogType.SUCCESS);
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
                },3000);
                }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void addToDiscountDatabase(String discount) {
        auth = FirebaseAuth.getInstance();
        addToDB = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(Objects.requireNonNull(auth.getUid()));
        addToDB.child("Discount").child("available").setValue("yes");
    }

    private void beforeDiscount(int price,int after, int discount,String type,String name) {
        DisInfo disInfo = new DisInfo(String.valueOf(price),String.valueOf(after),String.valueOf(discount));
        dis = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(Objects.requireNonNull(auth.getUid()));
        dis.child("List of Dish").child(type).child(name).child("Discount").child(name).setValue(disInfo);
    }


    private void fiftyDiscount() {
        auth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(Objects.requireNonNull(auth.getUid()));
        reference.child("List of Dish").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        if (String.valueOf(dataSnapshot1.child("mrp").getValue()).equals("no")) {
                            String type = String.valueOf(dataSnapshot.getKey());
                            String dishName = String.valueOf(dataSnapshot1.child("name").getValue());
                            if (Integer.parseInt(Objects.requireNonNull(String.valueOf(dataSnapshot1.child("full").getValue()))) >= 149) {
                                int price = Integer.parseInt(Objects.requireNonNull(String.valueOf(dataSnapshot1.child("full").getValue())));
                                int discount = 50;
                                int afterDis = price - (price * discount / 100);
                                beforeDiscount(price,afterDis,discount,type,dishName);
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

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void initialise() {
        recyclerView = findViewById(R.id.discountActivityRecyclerView);
        auth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(Objects.requireNonNull(auth.getUid()));
        dis = FirebaseDatabase.getInstance().getReference().getRoot().child("Admin").child(Objects.requireNonNull(auth.getUid()));

    }
}