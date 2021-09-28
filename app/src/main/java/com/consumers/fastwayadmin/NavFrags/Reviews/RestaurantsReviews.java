package com.consumers.fastwayadmin.NavFrags.Reviews;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

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

public class RestaurantsReviews extends AppCompatActivity {
    RecyclerView recyclerView;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    DatabaseReference reference;
    List<String> customerID = new ArrayList<>();
    ProgressBar progressBar;
    List<String> rating = new ArrayList<>();
    List<String> customerName = new ArrayList<>();
    List<String> customerReview = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurants_reviews);
        initialise();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    customerID.clear();
                    rating.clear();
                    customerName.clear();
                    customerReview.clear();

                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                        customerID.add(String.valueOf(dataSnapshot.getKey()));
                        customerReview.add(String.valueOf(dataSnapshot.child("reviews").getValue()));
                        customerName.add(String.valueOf(dataSnapshot.child("name").getValue()));
                        rating.add(String.valueOf(dataSnapshot.child("rating").getValue()));
                    }
                    progressBar.setVisibility(View.INVISIBLE);
                    recyclerView.setAdapter(new ReviewAdapter(customerID,rating,customerName,customerReview,RestaurantsReviews.this));
                }else
                    progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void initialise() {
        recyclerView = findViewById(R.id.restaurantsReviewRecyclerView);
        SharedPreferences sharedPreferences = getSharedPreferences("loginInfo",MODE_PRIVATE);
        reference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(sharedPreferences.getString("state","")).child(Objects.requireNonNull(auth.getUid())).child("Reviews");
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        progressBar = findViewById(R.id.restaurantReviewProgressBar);
    }
}