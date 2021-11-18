package com.consumers.fastwayadmin.MenuActivities.Combo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.consumers.fastwayadmin.DiscountCombo.ComboAndOffers;
import com.consumers.fastwayadmin.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class ComboMenuDish extends AppCompatActivity {
    List<String> comboNames = new ArrayList<String>();
    FirebaseAuth auth;
    RecyclerView recyclerView;
    FloatingActionButton add;
    String state;
    List<String> comboImage = new ArrayList<>();
    List<String> dish = new ArrayList<>();
    List<String> price = new ArrayList<>();
    DatabaseReference reference;
    List<List<String>> dishNames = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_combo_menu_dish);
        auth = FirebaseAuth.getInstance();
        add = findViewById(R.id.addNewComboButton);
        state = getIntent().getStringExtra("state");
        recyclerView = findViewById(R.id.comboAdapterRecyclerView);
        reference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(state).child(Objects.requireNonNull(auth.getUid())).child("List of Dish");
        reference.child("Combo").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                        comboNames.add(dataSnapshot.getKey().toString());
                        price.add(String.valueOf(dataSnapshot.child("price").getValue()));
                        if(dataSnapshot.hasChild("image"))
                            comboImage.add(String.valueOf(dataSnapshot.child("image").getValue()));
                        else
                            comboImage.add("");
                        for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                            dish.add(String.valueOf(dataSnapshot1.child("name").child("name").getValue()));
                        }
                        dish.removeAll(Collections.singleton("null"));
                        dishNames.add(new ArrayList<>(dish));
                        dish.clear();
                    }
                    recyclerView.setLayoutManager(new LinearLayoutManager(ComboMenuDish.this));
                    recyclerView.setAdapter(new ComboRecyclerView(comboNames,dishNames,price,ComboMenuDish.this,comboImage));

                    Log.i("tag",comboNames.toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ComboMenuDish.this, ComboAndOffers.class));
            }
        });
    }
}