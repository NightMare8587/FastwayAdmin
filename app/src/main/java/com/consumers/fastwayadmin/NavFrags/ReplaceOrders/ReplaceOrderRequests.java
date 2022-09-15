package com.consumers.fastwayadmin.NavFrags.ReplaceOrders;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;

import com.consumers.fastwayadmin.R;
import com.developer.kalert.KAlertDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ReplaceOrderRequests extends AppCompatActivity {
    DatabaseReference databaseReference;
    List<String> name = new ArrayList<>();
    List<String> details = new ArrayList<>();
    List<String> userID = new ArrayList<>();
    List<String> orderTime = new ArrayList<>();
    List<String> reportingTime = new ArrayList<>();
    List<String> imageURI = new ArrayList<>();
    List<String> orderID = new ArrayList<>();
    List<String> tableNum = new ArrayList<>();
    SharedPreferences sharedPreferences;
    RecyclerView recyclerView;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_replace_order_requests);
        recyclerView = findViewById(R.id.ReplaceOrderRequestRecyclerView);
        sharedPreferences = getSharedPreferences("loginInfo",MODE_PRIVATE);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(sharedPreferences.getString("state","")).child(sharedPreferences.getString("locality","")).child(Objects.requireNonNull(auth.getUid())).child("ReplaceOrderRequests");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                        reportingTime.add(dataSnapshot.getKey());
                        details.add(dataSnapshot.child("details").getValue(String.class));
                        name.add(dataSnapshot.child("name").getValue(String.class));
                        orderID.add(dataSnapshot.child("orderID").getValue(String.class));
                        tableNum.add(dataSnapshot.child("resId").getValue(String.class));
                        userID.add(dataSnapshot.child("resName").getValue(String.class));
                        orderTime.add(dataSnapshot.child("email").getValue(String.class));
                        imageURI.add(dataSnapshot.child("imageUri").getValue(String.class));
                    }
                    recyclerView.setAdapter(new ReplaceRecyclerView(name,details,userID,orderTime,reportingTime,imageURI,orderID,tableNum));
                }else{
                    new KAlertDialog(ReplaceOrderRequests.this,KAlertDialog.ERROR_TYPE)
                            .setTitleText("Nothing").setContentText("No replace order request").setConfirmText("Exit")
                            .setConfirmClickListener(click -> {
                                click.dismissWithAnimation();
                                finish();
                            }).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}