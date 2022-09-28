package com.consumers.fastwayadmin.NavFrags.Events;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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

public class OrganiseEvents extends AppCompatActivity {
    DatabaseReference databaseReference;
    DatabaseReference currentEvent;
    TextView currentEventName,seats,filled,price,artistName,dateAndTime;
    SharedPreferences sharedPreferences;
    Button cancelEvent,organiseEvent;
    RecyclerView recyclerView;
    List<String> eventNames = new ArrayList<>();
    List<String> ticketsSold = new ArrayList<>();
    List<String> dateAndTimeList = new ArrayList<>();
    List<String> artistNameList = new ArrayList<>();
    DatabaseReference previousEvents;
    boolean currentEventAvailable = false;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organise_events);
        sharedPreferences = getSharedPreferences("loginInfo",MODE_PRIVATE);
        databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Complaints").child("Registered Restaurants")
                .child(sharedPreferences.getString("state","")).child(Objects.requireNonNull(auth.getUid()));
        initialise();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        previousEvents = FirebaseDatabase.getInstance().getReference().getRoot().child("Admin").child(auth.getUid()).child("Previous Events");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.hasChild("allowEvents")){
                    KAlertDialog kAlertDialog = new KAlertDialog(OrganiseEvents.this,KAlertDialog.ERROR_TYPE)
                            .setTitleText("Error")
                            .setContentText("Your restaurant/cafe is not allowed to organise events. To be allowed contact Ordinalo")
                            .setConfirmText("Exit")
                            .setConfirmClickListener(clcik -> {
                                clcik.dismiss();
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

        cancelEvent.setOnClickListener(click -> {

        });

        AsyncTask.execute(() -> previousEvents.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                        eventNames.add(dataSnapshot.getKey());
                        dateAndTimeList.add(dataSnapshot.child("dateAndTimeString").getValue(String.class));
                        artistNameList.add(dataSnapshot.child("artistName").getValue(String.class));
                        ticketsSold.add(dataSnapshot.child("ticketSold").getValue(String.class));
                    }
                    recyclerView.setAdapter(new PreviousEventADP(eventNames,ticketsSold,dateAndTimeList,artistNameList));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        }));

        currentEvent = FirebaseDatabase.getInstance().getReference().getRoot().child("Admin").child(auth.getUid()).child("Current Event");
        currentEvent.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    currentEventName.setText("Event Name: " + snapshot.child("eventName").getValue(String.class));
                    price.setText("Price: \u20b9" + snapshot.child("price").getValue(String.class));
                    dateAndTime.setText("Date And Time: " + snapshot.child("dateAndTimeString").getValue(String.class));
                    filled.setText("Filled : " + snapshot.child("filled").getValue(String.class));
                    seats.setText("Seats: " + snapshot.child("seats").getValue(String.class));
                    artistName.setText("Artist Name: " + snapshot.child("artistNameComing").getValue(String.class));
                    cancelEvent.setVisibility(View.VISIBLE);
                    currentEventAvailable = true;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        organiseEvent.setOnClickListener(click -> {
            if(currentEventAvailable){
                KAlertDialog kAlertDialog = new KAlertDialog(OrganiseEvents.this,KAlertDialog.ERROR_TYPE)
                        .setTitleText("Error")
                        .setContentText("Your restaurant/cafe has already planned an event. You can organise only one at a time")
                        .setConfirmText("Exit")
                        .setConfirmClickListener(AppCompatDialog::dismiss);
                kAlertDialog.setCancelable(false);
                kAlertDialog.show();
            }else{
                startActivityForResult(new Intent(OrganiseEvents.this,CreateEventOrganise.class),20);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == 22){
            currentEvent.addListenerForSingleValueEvent(new ValueEventListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()) {
                        currentEventName.setText("Event Name: " + snapshot.child("eventName").getValue(String.class));
                        price.setText("Price: \u20b9" + snapshot.child("price").getValue(String.class));
                        dateAndTime.setText("Date And Time: " + snapshot.child("dateAndTimeString").getValue(String.class));
                        filled.setText("Filled : " + snapshot.child("filled").getValue(String.class));
                        seats.setText("Seats: " + snapshot.child("seats").getValue(String.class));
                        artistName.setText("Artist Name: " + snapshot.child("artistNameComing").getValue(String.class));
                        cancelEvent.setVisibility(View.VISIBLE);
                        currentEventAvailable = true;
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    private void initialise() {
        currentEventName = findViewById(R.id.nameOfEventOrganise);
        price = findViewById(R.id.priceOfPassORganiseEvent);
        dateAndTime = findViewById(R.id.dateAndTimeEventOrganise);
        filled = findViewById(R.id.filledSeatsOrganiseEvents);
        seats = findViewById(R.id.totalSeatsOrganiseEvent);
        recyclerView = findViewById(R.id.recyclerViewPreviousEventsList);
        artistName = findViewById(R.id.nameOfArtistComingOrganise);
        cancelEvent = findViewById(R.id.cancelCurrentEventButtonOrganised);
        organiseEvent = findViewById(R.id.organiseEventButton);
    }
}