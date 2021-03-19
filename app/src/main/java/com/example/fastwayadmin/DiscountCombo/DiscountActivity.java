package com.example.fastwayadmin.DiscountCombo;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fastwayadmin.R;
import com.example.flatdialoglibrary.dialog.FlatDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DiscountActivity extends AppCompatActivity {
    DatabaseReference reference;
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
                                        } else
                                            flatDialog1.dismiss();

                                    }
                                }).show();
                        flatDialog.dismiss();
                    }
                })
                .withSecondButtonListner(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        recyclerView.setLayoutManager(new LinearLayoutManager(DiscountActivity.this));
                        recyclerView.setAdapter(new DiscountRecycler(name));
                        flatDialog.dismiss();
                    }
                }).show();
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
                                int price = Integer.parseInt(Objects.requireNonNull(dataSnapshot1.child("full").getValue(String.class)));
                                int discount = 50;
                                int afterDis = price - (price * discount / 100);
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

    private void fiftyDiscount() {
        auth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(Objects.requireNonNull(auth.getUid()));
        reference.child("List of Dish").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                        if(String.valueOf(dataSnapshot1.child("mrp").getValue()).equals("no"))
                            if(Integer.parseInt(Objects.requireNonNull(dataSnapshot1.child("full").getValue(String.class))) >= 149)
                                Log.i("infoo",dataSnapshot1.child("name").getValue(String.class));

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
    }
}