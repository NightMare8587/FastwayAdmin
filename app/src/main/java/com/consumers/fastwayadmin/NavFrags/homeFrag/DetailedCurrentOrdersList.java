package com.consumers.fastwayadmin.NavFrags.homeFrag;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ProgressBar;

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

public class DetailedCurrentOrdersList extends AppCompatActivity {
    RecyclerView recyclerView;
    DatabaseReference reference;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    ProgressBar progressBar;
    List<String> seats = new ArrayList<>();
    List<String> isCurrentOrder = new ArrayList<>();
    List<String> resId = new ArrayList<>();
    List<String> tableNum = new ArrayList<>();
    List<String> amountPaymentPending = new ArrayList<>();
    SharedPreferences resInfoShared;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_current_orders_list);
        recyclerView = findViewById(R.id.detailedCurrentOrderRecyclerView);
        progressBar = findViewById(R.id.detailedCurrentOrderProgressBar);
        resInfoShared = getSharedPreferences("loginInfo",MODE_PRIVATE);
        reference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(resInfoShared.getString("state","")).child(resInfoShared.getString("locality","")).child(Objects.requireNonNull(auth.getUid()));


        reference.child("Tables").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                updateDatabase();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//                Toast.makeText(view.getContext(), ""+snapshot.child("status").getValue(), Toast.LENGTH_SHORT).show();
                updateDatabase();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                updateDatabase();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                updateDatabase();
            }
        });

    }

    private void updateDatabase() {
        reference.child("Tables").addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    tableNum.clear();
                    seats.clear();
                    isCurrentOrder.clear();
                    amountPaymentPending.clear();
                    resId.clear();
//                    Toast.makeText(view.getContext(), "I am invoked", Toast.LENGTH_SHORT).show();
//                    Toast.makeText(view.getContext(), ""+snapshot.getChildrenCount(), Toast.LENGTH_SHORT).show();
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                        if(Objects.equals(dataSnapshot.child("status").getValue(String.class), "unavailable")){

                            resId.add(dataSnapshot.child("customerId").getValue(String.class));
                            tableNum.add(dataSnapshot.child("tableNum").getValue(String.class));
                            seats.add(dataSnapshot.child("numSeats").getValue(String.class));
                            if(dataSnapshot.hasChild("amountToBePaid"))
                                amountPaymentPending.add("1");
                            else
                                amountPaymentPending.add("0");
                            if(dataSnapshot.hasChild("Current Order")){
                                isCurrentOrder.add("1");
                            }else
                                isCurrentOrder.add("0");
                        }
                    }
                    recyclerView.setLayoutManager(new LinearLayoutManager(DetailedCurrentOrdersList.this));
//                    Toast.makeText(view.getContext(), ""+seats.toString(), Toast.LENGTH_SHORT).show();
                    recyclerView.setAdapter(new homeFragClass(tableNum,seats,resId,isCurrentOrder,amountPaymentPending));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}