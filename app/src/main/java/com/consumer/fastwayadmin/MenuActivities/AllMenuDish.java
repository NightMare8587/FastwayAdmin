package com.consumer.fastwayadmin.MenuActivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.consumer.fastwayadmin.Dish.DishView;
import com.consumer.fastwayadmin.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AllMenuDish extends AppCompatActivity {
    DatabaseReference allMenu;
    FloatingActionButton search;
    SwipeRefreshLayout swipeRefreshLayout;
    FirebaseAuth menuAuth;
    DatabaseReference menuRef;
    RecyclerView recyclerView;
    String dish;
    ProgressBar loading;
    List<String> names = new ArrayList<String>();
    List<String> image = new ArrayList<>();
    List<String> fullPrice = new ArrayList<String>();
    List<String> halfPrice = new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_menu_dish);
        initialise();
        names.clear();
        halfPrice.clear();
        image.clear();
        fullPrice.clear();
        menuRef.child(Objects.requireNonNull(menuAuth.getUid())).child("List of Dish").child(dish).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()){
                    Toast.makeText(AllMenuDish.this, "Empty!! Add Some Dish", Toast.LENGTH_SHORT).show();
                    loading.setVisibility(View.INVISIBLE);
                }else{
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                        names.add(dataSnapshot.child("name").getValue().toString());
                        halfPrice.add(dataSnapshot.child("half").getValue().toString());
                        fullPrice.add(dataSnapshot.child("full").getValue().toString());
                        image.add(dataSnapshot.child("image").getValue().toString());
                    }
                }
                loading.setVisibility(View.INVISIBLE);
                recyclerView.setAdapter(new DishView(names,fullPrice,halfPrice,dish,image));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),CreateDish.class);
                intent.putExtra("Dish",getIntent().getStringExtra("Dish"));
                startActivity(intent);
            }
        });

        Toast.makeText(this, "Swipe down to refresh if new dish added", Toast.LENGTH_SHORT).show();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                names.clear();
                halfPrice.clear();
                fullPrice.clear();
                image.clear();
                menuRef.child(menuAuth.getUid()).child("List of Dish").child(dish).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(!snapshot.exists()){
                            Toast.makeText(AllMenuDish.this, "Empty!! Add Some Dish", Toast.LENGTH_SHORT).show();
                            loading.setVisibility(View.INVISIBLE);
                        }else{
                            for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                                names.add(dataSnapshot.child("name").getValue().toString());
                                halfPrice.add(dataSnapshot.child("half").getValue().toString());
                                fullPrice.add(dataSnapshot.child("full").getValue().toString());
                                image.add(dataSnapshot.child("image").getValue().toString());
                            }
                        }
                        loading.setVisibility(View.INVISIBLE);
                        swipeRefreshLayout.setRefreshing(false);
                        recyclerView.setAdapter(new DishView(names,fullPrice,halfPrice,dish,image));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });

    }

    private void initialise() {
        allMenu = FirebaseDatabase.getInstance().getReference().getRoot();
        search = (FloatingActionButton)findViewById(R.id.floatingActionButton2);
        loading = findViewById(R.id.loading);
        menuAuth = FirebaseAuth.getInstance();
        menuRef = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants");
        recyclerView = findViewById(R.id.dishRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        dish = getIntent().getStringExtra("Dish");
        swipeRefreshLayout = findViewById(R.id.swipe);
    }
}