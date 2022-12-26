package com.consumers.fastwayadmin.ListViewActivity.StaffDetails.EmpHOME;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.consumers.fastwayadmin.NavFrags.homeFrag.homeFragClass;
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

public class HomeScreenEMP extends AppCompatActivity {
    FirebaseAuth auth = FirebaseAuth.getInstance();
    DatabaseReference databaseReference;
    List<String> isCurrentOrder = new ArrayList<>();
    DatabaseReference checkIfAdminLive;
    List<String> resId = new ArrayList<>();
    List<String> tableNum = new ArrayList<>();
    List<String> amountPaymentPending = new ArrayList<>();
    int currentOrderCount = 0;
    List<String> seats = new ArrayList<>();
    DatabaseReference reference;
    LinearLayoutManager horizonatl,anotherHori;
    String UID;
    RecyclerView table,takeAway;
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen_emp);
        toolbar = findViewById(R.id.toolbarHomeEmp);
        setSupportActionBar(toolbar);
        table = findViewById(R.id.recyclerViewEmpTable);
        takeAway = findViewById(R.id.recyclerViewEmpTakeAway);
        horizonatl = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        anotherHori = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);;
        SharedPreferences login = getSharedPreferences("loginInfo",MODE_PRIVATE);
        UID = login.getString("resID","");
        checkIfAdminLive = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(login.getString("state","")).child(login.getString("state","")).child(UID);
        reference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(login.getString("state","")).child(login.getString("locality","")).child(UID);
        databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("EmployeeDB").child(auth.getUid()).child("ResDetails");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()){
                    SharedPreferences loginInfo = getSharedPreferences("loginInfo",MODE_PRIVATE);
                    SharedPreferences.Editor editor = loginInfo.edit();
                    editor.remove("resDetails").apply();
                    AlertDialog.Builder builder = new AlertDialog.Builder(HomeScreenEMP.this);
                    builder.setTitle("Removed").setMessage("You have been removed from the restaurant staff. Contact restaurant for more info")
                            .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            }).create();
                    builder.show();
                    return;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        checkIfAdminLive.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChild("isAdminLive") && Objects.equals(snapshot.child("isAdminLive").getValue(String.class), "yes"))
                {
                    Toast.makeText(HomeScreenEMP.this, "Admin is Live", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


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
                    currentOrderCount = 0;
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
                                currentOrderCount++;
                                isCurrentOrder.add("1");
                            }else
                                isCurrentOrder.add("0");
                        }
                    }
//                    if(currentOrderCount != 0)
//                        currentTablesText.setText("CURRENT TABLES (" + currentOrderCount + ")");
//                    else
//                        currentTablesText.setText("CURRENT TABLES");
//
//                    if(currentOrderCount > 2)
//                    {
//                        refershRecyclerView.setText("MORE");
//                        refershRecyclerView.setOnClickListener(click -> {
//
//                        });
//                    }

                    table.setLayoutManager(horizonatl);
//                    Toast.makeText(view.getContext(), ""+seats.toString(), Toast.LENGTH_SHORT).show();
                    table.setAdapter(new homeFragClass(tableNum,seats,resId,isCurrentOrder,amountPaymentPending));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}