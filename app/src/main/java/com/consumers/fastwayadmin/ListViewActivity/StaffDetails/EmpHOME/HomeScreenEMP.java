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
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.consumers.fastwayadmin.ListViewActivity.StaffDetails.CurrentTakeAwayEmp;
import com.consumers.fastwayadmin.ListViewActivity.StaffDetails.homeFragClassEmp;
import com.consumers.fastwayadmin.NavFrags.CurrentTakeAwayOrders.CurrentTakeAway;
import com.consumers.fastwayadmin.NavFrags.HomeFrag;
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
    String usernameOfTakeAway,currentTime;
    List<String> finalUserNames = new ArrayList<>();
    List<String> customisationList = new ArrayList<>();
    DatabaseReference checkIfAdminLive;
    List<String> halfOr = new ArrayList<>();

    List<String> currentTakeAwayAuth = new ArrayList<>();
    List<String> dishNameCurrentTakeAway = new ArrayList<>();
    List<String> dishQuantityCurrentTakeAway = new ArrayList<>();
    List<String> userNameTakeAway = new ArrayList<>();
    List<String> resId = new ArrayList<>();
    List<String> tableNum = new ArrayList<>();
    List<String> amountPaymentPending = new ArrayList<>();
    int currentOrderCount = 0;
    List<String> seats = new ArrayList<>();
    DatabaseReference reference;
    LinearLayoutManager horizonatl,anotherHori;
    String UID;
    RecyclerView table,homeFragTakeAwayRecucler;
    Toolbar toolbar;
    List<String> image = new ArrayList<>();
    List<String> half = new ArrayList<>();
    List<String> type = new ArrayList<>();
    List<String> orderAndPayment = new ArrayList<>();
    List<String> price = new ArrayList<>();
    List<String> time = new ArrayList<>();
    List<List<String>> finalDishNames = new ArrayList<>();
    List<List<String>> finalImages = new ArrayList<>();
    String orderId,orderAmount,paymentMode;

    List<List<String>> finalTypes = new ArrayList<>();
    List<List<String>> finalDishPrices = new ArrayList<>();
    List<List<String>> finalOrderAndPayments = new ArrayList<>();
    List<List<String>> finalDishQuantity = new ArrayList<>();
    List<List<String>> finalHalfOr = new ArrayList<>();
    List<String> finalPayment = new ArrayList<>();
    List<String> orderIDs = new ArrayList<>();
    List<String> orderAmounts = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen_emp);
        toolbar = findViewById(R.id.toolbarHomeEmp);
        setSupportActionBar(toolbar);
        table = findViewById(R.id.recyclerViewEmpTable);
        homeFragTakeAwayRecucler = findViewById(R.id.recyclerViewEmpTakeAway);
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

        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                new TakeAwayClass().execute();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                new TakeAwayClass().execute();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                new TakeAwayClass().execute();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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
                    table.setAdapter(new homeFragClassEmp(tableNum,seats,resId,isCurrentOrder,amountPaymentPending));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    public class TakeAwayClass extends AsyncTask<Void,Void,Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            SharedPreferences login = getSharedPreferences("loginInfo",MODE_PRIVATE);
            FirebaseAuth auth = FirebaseAuth.getInstance();
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(login.getString("state","")).child(login.getString("locality","")).child(Objects.requireNonNull(UID)).child("Current TakeAway");
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        String cus = "";
                        currentTakeAwayAuth.clear();
                        dishNameCurrentTakeAway.clear();
                        userNameTakeAway.clear();
                        halfOr.clear();
                        orderAndPayment.clear();
                        dishQuantityCurrentTakeAway.clear();
                        finalDishQuantity.clear();
                        image.clear();
                        type.clear();
                        finalImages.clear();
                        finalTypes.clear();
                        customisationList.clear();
                        finalPayment.clear();
                        price.clear();
                        time.clear();
                        finalUserNames.clear();
                        finalHalfOr.clear();
                        finalDishNames.clear();
                        for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                            currentTakeAwayAuth.add(String.valueOf(dataSnapshot.getKey()));
                            for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                                dishNameCurrentTakeAway.add(String.valueOf(dataSnapshot1.child("name").getValue()));
                                halfOr.add(String.valueOf(dataSnapshot1.child("halfOr").getValue()));
                                orderAmount = String.valueOf(dataSnapshot1.child("orderAmount").getValue());
                                orderId = String.valueOf(dataSnapshot1.child("orderID").getValue());
                                paymentMode = String.valueOf(dataSnapshot1.child("paymmentMode").getValue());
                                dishQuantityCurrentTakeAway.add(String.valueOf(dataSnapshot1.child("timesOrdered").getValue()));
                                image.add(String.valueOf(dataSnapshot1.child("image").getValue()));
                                type.add(String.valueOf(dataSnapshot1.child("type").getValue()));
                                price.add(String.valueOf(dataSnapshot1.child("price").getValue()));
                                userNameTakeAway.add(String.valueOf(dataSnapshot1.child("nameOfUser").getValue()));
                                orderAndPayment.add(String.valueOf(dataSnapshot1.child("orderAndPayment").getValue()));
                                usernameOfTakeAway = String.valueOf(dataSnapshot1.child("nameOfUser").getValue());
                                currentTime = String.valueOf(dataSnapshot1.child("time").getValue());
                                cus = String.valueOf(dataSnapshot1.child("customisation").getValue());
                            }
                            customisationList.add(cus);
                            time.add(currentTime);
                            finalDishNames.add(new ArrayList<>(dishNameCurrentTakeAway));
                            finalDishQuantity.add(new ArrayList<>(dishQuantityCurrentTakeAway));
                            finalHalfOr.add(new ArrayList<>(halfOr));
                            finalPayment.add(paymentMode);
                            finalUserNames.add(usernameOfTakeAway);
                            finalImages.add(new ArrayList<>(image));
                            finalTypes.add(new ArrayList<>(type));
                            image.clear();
                            type.clear();
                            finalOrderAndPayments.add(new ArrayList<>(orderAndPayment));
                            finalDishPrices.add(new ArrayList<>(price));
                            price.clear();
                            orderAndPayment.clear();

                            dishNameCurrentTakeAway.clear();
                            dishQuantityCurrentTakeAway.clear();
                            orderIDs.add(orderId);
                            orderAmounts.add(orderAmount);
                            halfOr.clear();
                        }
                        Log.i("time",time.toString());
//                        Log.i("Current",currentTakeAwayAuth.toString() + " " + dishNameCurrentTakeAway.toString() + " " + userNameTakeAway.toString());
                        homeFragTakeAwayRecucler.setLayoutManager(anotherHori);
                        Log.i("message",finalDishNames.toString() + "\n" + finalPayment.toString() + "\n" + finalDishQuantity.toString());
//                        homeFragTakeAwayRecucler.setAdapter(new CurrentTakeAway(currentTakeAwayAuth,dishNameCurrentTakeAway,dishQuantityCurrentTakeAway,userNameTakeAway,halfOr,paymentMode));
                    }else
                    {
                        currentTakeAwayAuth.clear();
                        finalDishNames.clear();
                        dishNameCurrentTakeAway.clear();
                        userNameTakeAway.clear();
                        halfOr.clear();
                        finalDishQuantity.clear();
                        customisationList.clear();
                        finalDishPrices.clear();
                        finalPayment.clear();
                        finalUserNames.clear();
                        orderIDs.clear();
                        finalImages.clear();
                        finalTypes.clear();
                        orderAmounts.clear();
                        finalOrderAndPayments.clear();
                        time.clear();
                        finalHalfOr.clear();
                        dishQuantityCurrentTakeAway.clear();
                        homeFragTakeAwayRecucler.setLayoutManager(anotherHori);

                    }
                    homeFragTakeAwayRecucler.setAdapter(new CurrentTakeAwayEmp(finalDishNames,finalDishQuantity,finalHalfOr,finalUserNames,finalPayment,orderIDs,orderAmounts,currentTakeAwayAuth,time,customisationList,finalOrderAndPayments,finalDishPrices,finalImages,finalTypes));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            return null;
        }
    }
}