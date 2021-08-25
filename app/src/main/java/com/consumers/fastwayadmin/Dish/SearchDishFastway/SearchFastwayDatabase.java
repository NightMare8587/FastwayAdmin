package com.consumers.fastwayadmin.Dish.SearchDishFastway;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import com.consumers.fastwayadmin.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SearchFastwayDatabase extends AppCompatActivity {
    RecyclerView recyclerView;
    List<String> dishName = new ArrayList<>();
    List<String> dishImage = new ArrayList<>();
    String dish;
    DatabaseReference reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_fastway_database);
        recyclerView = findViewById(R.id.searchFastwayRecyclerView);
        reference = FirebaseDatabase.getInstance().getReference().getRoot().child("Global Dish");
        dish = getIntent().getStringExtra("dish");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    dishName.clear();
                    dishImage.clear();
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                        dishImage.add(String.valueOf(dataSnapshot.child("dishImage").getValue()));
                        dishName.add(String.valueOf(dataSnapshot.getKey()));
                    }

                    recyclerView.setLayoutManager(new LinearLayoutManager(SearchFastwayDatabase.this));
                    recyclerView.setAdapter(new SearchFastwayClass(dishName,dishImage,dish));

                }else
                    Toast.makeText(SearchFastwayDatabase.this, "No Dish Available :)", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}