package com.consumers.fastwayadmin.MenuActivities.Combo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.consumers.fastwayadmin.DiscountCombo.SelectDishAdapter;
import com.consumers.fastwayadmin.DiscountCombo.SelectDishForCombo;
import com.consumers.fastwayadmin.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SelectDishForCurrentCombo extends AppCompatActivity {
    RecyclerView recyclerView;
    FirebaseAuth auth;
    Toolbar toolbar;
    List<String> name = new ArrayList<>();
    String state;
    List<String> image = new ArrayList<>();
    String comboName;
    DatabaseReference reference;
    String type;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_dish_for_current_combo);
        comboName = getIntent().getStringExtra("comboName");
        toolbar = findViewById(R.id.recyclerViewCurrentComboToolBar);
        setSupportActionBar(toolbar);
        recyclerView = findViewById(R.id.selectDishCurrentComboRecyclerView);
        auth = FirebaseAuth.getInstance();
        type = getIntent().getStringExtra("dishType");
        state = getIntent().getStringExtra("state");
        reference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(state).child(Objects.requireNonNull(auth.getUid())).child("List of Dish");
        reference.child(type).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                        name.add(String.valueOf(dataSnapshot.child("name").getValue()));
                        image.add(String.valueOf(dataSnapshot.child("image").getValue()));
                    }
                    recyclerView.setLayoutManager(new LinearLayoutManager(SelectDishForCurrentCombo.this));
                    recyclerView.setAdapter(new selectCurrentDishAdapter(name,image,SelectDishForCurrentCombo.this,comboName,state));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}