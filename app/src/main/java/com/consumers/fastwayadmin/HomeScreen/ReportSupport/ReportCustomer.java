package com.consumers.fastwayadmin.HomeScreen.ReportSupport;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.consumers.fastwayadmin.R;
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

public class ReportCustomer extends AppCompatActivity {
    List<List<String>> finalListPrice = new ArrayList<>();
    List<String> dishName = new ArrayList<>();
    List<List<String>> finalList = new ArrayList<>();
    List<List<String>> finalResId = new ArrayList<>();
    List<List<String>> finalType = new ArrayList<>();
    List<String> dishImage = new ArrayList<>();
    List<String> dishPrice = new ArrayList<>();
    List<String> type = new ArrayList<>();
    List<String> resId = new ArrayList<>();
    List<String> timeOfOrder = new ArrayList<>();
    List<String> foodOrderTime = new ArrayList<>();
    int count = 1;
    DatabaseReference reference;
    RecyclerView recyclerView;
    FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_customer);
        initialise();
        reference.child("Recent Orders").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()){
                    Toast.makeText(ReportCustomer.this, "No Order Made :)", Toast.LENGTH_SHORT).show();
                }else{
//                    Toast.makeText(ReportCustomer.this, "Click on your orders to download their invoice, If needed.", Toast.LENGTH_SHORT).show();
                    timeOfOrder.clear();
                    foodOrderTime.clear();
                    finalResId.clear();
                    type.clear();
                    finalList.clear();
                    finalType.clear();
                    finalListPrice.clear();
                    dishPrice.clear();
                    dishName.clear();
                    dishImage.clear();
                    resId.clear();
                    count = 1;
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                        timeOfOrder.add(String.valueOf(dataSnapshot.getKey()));
                        for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                            resId.add(String.valueOf(dataSnapshot1.getKey()));
                            for(DataSnapshot dataSnapshot2 : dataSnapshot1.getChildren()){
                                dishImage.add(dataSnapshot2.child("image").getValue(String.class));
                                dishName.add(dataSnapshot2.child("name").getValue(String.class));
                                dishPrice.add(dataSnapshot2.child("price").getValue(String.class));
                                type.add(dataSnapshot2.child("type").getValue(String.class));
                                foodOrderTime.add(dataSnapshot2.child("time").getValue(String.class));

                            }

                            finalList.add(new ArrayList<>(dishName));
                            finalListPrice.add(new ArrayList<>(dishPrice));
                            finalType.add(new ArrayList<>(type));
                            type.clear();
                            dishName.clear();
                            dishPrice.clear();
                        }

                    }
//                    Toast.makeText(MyOrders.this, ""+restaurantAddress.toString(), Toast.LENGTH_SHORT).show();
                    recyclerView.setAdapter(new MyOrderView(finalType,ReportCustomer.this,resId,
                            type,timeOfOrder,foodOrderTime,finalList,finalListPrice));
                    Log.i("info",finalList.toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        reference.child("Recent Orders").addChildEventListener(new ChildEventListener() {
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
                updateChild();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void updateChild(){
        reference.child("Recent Orders").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()){
                    Toast.makeText(ReportCustomer.this, "No Order Made :)", Toast.LENGTH_SHORT).show();
                }else{
                    timeOfOrder.clear();
                    foodOrderTime.clear();
                    type.clear();
                    finalList.clear();
                    finalListPrice.clear();
                    dishPrice.clear();
                    dishName.clear();
                    dishImage.clear();
                    resId.clear();
                    finalType.clear();
                    count = 1;
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                        timeOfOrder.add(String.valueOf(dataSnapshot.getKey()));
                        for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                            resId.add(String.valueOf(dataSnapshot1.getKey()));
                            for(DataSnapshot dataSnapshot2 : dataSnapshot1.getChildren()){
                                dishImage.add(dataSnapshot2.child("image").getValue(String.class));
                                dishName.add(dataSnapshot2.child("name").getValue(String.class));
                                dishPrice.add(dataSnapshot2.child("price").getValue(String.class));
                                type.add(dataSnapshot2.child("type").getValue(String.class));
                                foodOrderTime.add(dataSnapshot2.child("time").getValue(String.class));

                            }
                            finalType.add(new ArrayList<>(type));
                            type.clear();
                            finalList.add(new ArrayList<>(dishName));
                            finalListPrice.add(new ArrayList<>(dishPrice));
                            dishName.clear();
                            dishPrice.clear();
                        }

                    }
//                    Toast.makeText(MyOrders.this, ""+restaurantAddress.toString(), Toast.LENGTH_SHORT).show();
                    recyclerView.setAdapter(new MyOrderView(finalType,ReportCustomer.this,resId,
                            type,timeOfOrder,foodOrderTime,finalList,finalListPrice));
                    Log.i("info",finalList.toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void initialise() {
        auth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(Objects.requireNonNull(auth.getUid()));
        recyclerView = findViewById(R.id.myOrderRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
    }
}