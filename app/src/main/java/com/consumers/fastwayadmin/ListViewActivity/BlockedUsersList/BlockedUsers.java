package com.consumers.fastwayadmin.ListViewActivity.BlockedUsersList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;

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

public class BlockedUsers extends AppCompatActivity {
    DatabaseReference databaseReference;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    List<String> userID = new ArrayList<>();
    RecyclerView recyclerView;
    List<String> timeReported = new ArrayList<>();
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blocked_users);
        recyclerView = findViewById(R.id.recyclerViewBlockedUsers);
        sharedPreferences = getSharedPreferences("loginInfo",MODE_PRIVATE);
        databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(sharedPreferences.getString("state","")).child(Objects.requireNonNull(auth.getUid())).child("Blocked List");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    userID.clear();
                    timeReported.clear();

                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                        userID.add(String.valueOf(dataSnapshot.getKey()));
                        timeReported.add(String.valueOf(dataSnapshot.child("time").getValue()));
                    }

                    recyclerView.setLayoutManager(new LinearLayoutManager(BlockedUsers.this));
                    recyclerView.setAdapter(new BlockedRecyclerView(userID,timeReported,sharedPreferences.getString("state",""),BlockedUsers.this));

                }else{
                    KAlertDialog kAlertDialog = new KAlertDialog(BlockedUsers.this,KAlertDialog.ERROR_TYPE);
                    kAlertDialog.setTitleText("No Users Blocked")
                            .setContentText("No Blocked user founded")
                            .setConfirmText("Exit")
                            .setConfirmClickListener(k -> {
                                k.dismissWithAnimation();
                                finish();
                            });

                    kAlertDialog.setCancelable(false);
                    kAlertDialog.show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        databaseReference.addChildEventListener(new ChildEventListener() {
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
    }

    private void updateChild() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    userID.clear();
                    timeReported.clear();

                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                        userID.add(String.valueOf(dataSnapshot.getKey()));
                        timeReported.add(String.valueOf(dataSnapshot.child("time").getValue()));
                    }

                    recyclerView.setLayoutManager(new LinearLayoutManager(BlockedUsers.this));
                    recyclerView.setAdapter(new BlockedRecyclerView(userID,timeReported,sharedPreferences.getString("state",""),BlockedUsers.this));

                }else{
                    userID.clear();
                    timeReported.clear();
                    recyclerView.setLayoutManager(new LinearLayoutManager(BlockedUsers.this));
                    recyclerView.setAdapter(new BlockedRecyclerView(userID,timeReported,sharedPreferences.getString("state",""),BlockedUsers.this));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}