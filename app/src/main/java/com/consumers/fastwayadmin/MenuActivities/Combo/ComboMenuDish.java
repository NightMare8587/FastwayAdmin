package com.consumers.fastwayadmin.MenuActivities.Combo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.consumers.fastwayadmin.DiscountCombo.ComboAndOffers;
import com.consumers.fastwayadmin.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ComboMenuDish extends AppCompatActivity {
    List<String> comboNames = new ArrayList<String>();
    FirebaseAuth auth;
    RecyclerView recyclerView;
    FloatingActionButton add;
    String state;
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    List<String> comboImage = new ArrayList<>();
    List<String> dish = new ArrayList<>();
    List<String> enabled = new ArrayList<>();
    List<String> descriptionOfCombo = new ArrayList<>();
    List<String> price = new ArrayList<>();
    DatabaseReference reference;
    CollectionReference collectionReference;
    String locality;
    List<List<String>> dishNames = new ArrayList<>();
    List<List<String>> dishQuan = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_combo_menu_dish);
        auth = FirebaseAuth.getInstance();
        add = findViewById(R.id.addNewComboButton);
        state = getIntent().getStringExtra("state");
        locality = getIntent().getStringExtra("locality");
        recyclerView = findViewById(R.id.comboAdapterRecyclerView);
        collectionReference = firestore.collection(state).document("Restaurants").collection(locality).document(auth.getUid()).collection("List of Dish");
        Query query = collectionReference.whereEqualTo("menuType","Combo");
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot documentSnapshot : task.getResult()){
                        Map<String,Object> map = documentSnapshot.getData();
                        comboNames.add((String) map.get("comboName"));
                        price.add((String) map.get("price"));
                        comboImage.add((String) map.get("image"));
                        descriptionOfCombo.add((String) map.get("description"));
                        enabled.add((String) map.get("enable"));
                        String[] str = String.valueOf(map.get("dishNamesArr")).split(",");
                        String[] str1 = String.valueOf(map.get("dishQuantityArr")).split(",");
                        ArrayList<String> strList = new ArrayList<>(
                                Arrays.asList(str));
                        ArrayList<String> strList1 = new ArrayList<>(
                                Arrays.asList(str1));

                        dishNames.add(new ArrayList<>(strList));
                        dishQuan.add(new ArrayList<>(strList1));
                    }
                    recyclerView.setLayoutManager(new LinearLayoutManager(ComboMenuDish.this));
                    recyclerView.setAdapter(new ComboRecyclerView(comboNames,dishNames,price,ComboMenuDish.this,comboImage,descriptionOfCombo,enabled,dishQuan));

                }
            }
        });
//        reference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(state).child(locality).child(Objects.requireNonNull(auth.getUid())).child("List of Dish");
//        reference.child("Combo").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if(snapshot.exists()){
//                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
//                        comboNames.add(dataSnapshot.getKey().toString());
//                        price.add(String.valueOf(dataSnapshot.child("price").getValue()));
//                        if(dataSnapshot.hasChild("image"))
//                            comboImage.add(String.valueOf(dataSnapshot.child("image").getValue()));
//                        else
//                            comboImage.add("");
//                        for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
//                            dish.add(String.valueOf(dataSnapshot1.child("name").child("name").getValue()));
//                        }
//                        enabled.add(String.valueOf(dataSnapshot.child("enable").getValue()));
//                        if(dataSnapshot.hasChild("description"))
//                            descriptionOfCombo.add(dataSnapshot.child("description").getValue(String.class));
//                        else
//                            descriptionOfCombo.add("");
//                        dish.removeAll(Collections.singleton("null"));
//                        dishNames.add(new ArrayList<>(dish));
//                        dish.clear();
//                    }
//                    recyclerView.setLayoutManager(new LinearLayoutManager(ComboMenuDish.this));
//                    recyclerView.setAdapter(new ComboRecyclerView(comboNames,dishNames,price,ComboMenuDish.this,comboImage,descriptionOfCombo,enabled));
//
//                    Log.i("tag",comboNames.toString());
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//
//        reference.child("Combo").addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//                updateChild();
//            }
//
//            @Override
//            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//                updateChild();
//            }
//
//            @Override
//            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
//                updateChild();
//            }
//
//            @Override
//            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });

        add.setOnClickListener(v -> startActivity(new Intent(ComboMenuDish.this, ComboAndOffers.class)));
    }

//    private void updateChild() {
//        reference.child("Combo").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if(snapshot.exists()){
//                    comboNames.clear();
//                    enabled.clear();
//                    price.clear();
//                    comboImage.clear();
//                    descriptionOfCombo.clear();
//                    dishNames.clear();
//                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
//                        comboNames.add(dataSnapshot.getKey().toString());
//                        price.add(String.valueOf(dataSnapshot.child("price").getValue()));
//                        if(dataSnapshot.hasChild("image"))
//                            comboImage.add(String.valueOf(dataSnapshot.child("image").getValue()));
//                        else
//                            comboImage.add("");
//                        for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
//                            dish.add(String.valueOf(dataSnapshot1.child("name").child("name").getValue()));
//                        }
//                        enabled.add(String.valueOf(dataSnapshot.child("enable").getValue()));
//                        if(dataSnapshot.hasChild("description"))
//                            descriptionOfCombo.add(dataSnapshot.child("description").getValue(String.class));
//                        else
//                            descriptionOfCombo.add("");
//                        dish.removeAll(Collections.singleton("null"));
//                        dishNames.add(new ArrayList<>(dish));
//                        dish.clear();
//                    }
//                    recyclerView.setLayoutManager(new LinearLayoutManager(ComboMenuDish.this));
//                    recyclerView.setAdapter(new ComboRecyclerView(comboNames,dishNames,price,ComboMenuDish.this,comboImage,descriptionOfCombo,enabled));
//
//                    Log.i("tag",comboNames.toString());
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }
}