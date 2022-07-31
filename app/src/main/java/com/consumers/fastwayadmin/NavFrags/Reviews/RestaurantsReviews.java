package com.consumers.fastwayadmin.NavFrags.Reviews;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.consumers.fastwayadmin.NavFrags.FastwayPremiumActivites.FastwayPremiums;
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

public class RestaurantsReviews extends AppCompatActivity {
    RecyclerView recyclerView;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    DatabaseReference reference;
    DatabaseReference staffReff;
    int whichOne = 0;
    List<String> staffID = new ArrayList<>();
    List<String> customerID = new ArrayList<>();
    ProgressBar progressBar;
    List<String> rating = new ArrayList<>();
    Button resStaffReview;
    List<String> customerName = new ArrayList<>();
    SharedPreferences sharedPreferences;
    List<String> customerReview = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurants_reviews);
        initialise();
        sharedPreferences = getSharedPreferences("AdminPremiumDetails",MODE_PRIVATE);
        if(sharedPreferences.contains("status") && sharedPreferences.getString("status","").equals("active")) {
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        customerID.clear();
                        rating.clear();
                        customerName.clear();
                        customerReview.clear();

                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            customerID.add(String.valueOf(dataSnapshot.getKey()));
                            customerReview.add(String.valueOf(dataSnapshot.child("reviews").getValue()));
                            customerName.add(String.valueOf(dataSnapshot.child("name").getValue()));
                            rating.add(String.valueOf(dataSnapshot.child("rating").getValue()));
                        }
                        progressBar.setVisibility(View.INVISIBLE);
                        recyclerView.setAdapter(new ReviewAdapter(customerID, rating, customerName, customerReview, RestaurantsReviews.this));
                    } else
                        progressBar.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    progressBar.setVisibility(View.INVISIBLE);
                }
            });
        }else{
            progressBar.setVisibility(View.INVISIBLE);
            KAlertDialog kAlertDialog = new KAlertDialog(RestaurantsReviews.this,KAlertDialog.WARNING_TYPE)
                    .setTitleText("Premium").setContentText("Subscribe premium to see reviews about your restaurant")
                    .setConfirmText("Subscribe").setCancelText("Exit").setConfirmClickListener(click -> {
                        click.dismissWithAnimation();
                        startActivity(new Intent(RestaurantsReviews.this, FastwayPremiums.class));
                    }).setCancelClickListener(click -> {
                        click.dismissWithAnimation();
                        finish();
                    });

            kAlertDialog.setCancelable(false);
            kAlertDialog.show();
        }

//        resStaffReview.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                staffReff.addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        if(snapshot.exists()){
//                            for(DataSnapshot dataSnapshot : snapshot.getChildren()){
//
//                            }
//                        }else{
//                            Toast.makeText(RestaurantsReviews.this, "No Review yet on your staff", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });
//            }
//        });
    }

    private void initialise() {
        recyclerView = findViewById(R.id.restaurantsReviewRecyclerView);
        SharedPreferences sharedPreferences = getSharedPreferences("loginInfo",MODE_PRIVATE);
        reference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(sharedPreferences.getString("state","")).child(sharedPreferences.getString("locality","")).child(Objects.requireNonNull(auth.getUid())).child("Reviews");
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        progressBar = findViewById(R.id.restaurantReviewProgressBar);
        resStaffReview = findViewById(R.id.restaurantStaffReciewButton);

        staffReff = FirebaseDatabase.getInstance().getReference().getRoot().child("Admin").child(auth.getUid()).child("Restaurant Staff Reports");
    }
}