package com.consumers.fastwayadmin.MenuActivities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.consumers.fastwayadmin.Dish.DishView;
import com.consumers.fastwayadmin.Dish.SearchDishFastway.SearchFastwayDatabase;
import com.consumers.fastwayadmin.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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

public class AllMenuDish extends AppCompatActivity {
    DatabaseReference allMenu;
    FloatingActionButton search;
    SwipeRefreshLayout swipeRefreshLayout;
    FirebaseAuth menuAuth;
    SharedPreferences sharedPreferences;
    DatabaseReference menuRef;
    RecyclerView recyclerView;
    String dish;
    String state;
    ProgressBar loading;
    List<String> names = new ArrayList<String>();
    List<String> before = new ArrayList<>();
    List<String> after = new ArrayList<>();
    List<String> discount = new ArrayList<>();
    List<String> image = new ArrayList<>();
    List<String> fullPrice = new ArrayList<String>();
    List<String> halfPrice = new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_menu_dish);
        initialise();
        menuRef.child(Objects.requireNonNull(menuAuth.getUid())).child("List of Dish").child(dish).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()){
                    Toast.makeText(AllMenuDish.this, "Empty!! Add Some Dish", Toast.LENGTH_SHORT).show();
                    loading.setVisibility(View.INVISIBLE);
                }else{
                    names.clear();
                    halfPrice.clear();
                    image.clear();
                    before.clear();
                    after.clear();
                    discount.clear();
                    fullPrice.clear();
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                        if(dataSnapshot.child("Discount").child(dataSnapshot.child("name").getValue().toString()).child("dis").exists()){
                            Log.d("hola","yes");
                            before.add(String.valueOf(dataSnapshot.child("Discount").child(Objects.requireNonNull(dataSnapshot.child("name").getValue(String.class))).child("before").getValue(String.class)));
                            after.add(String.valueOf(dataSnapshot.child("Discount").child(Objects.requireNonNull(dataSnapshot.child("name").getValue(String.class))).child("after").getValue(String.class)));
                            discount.add(String.valueOf(dataSnapshot.child("Discount").child(Objects.requireNonNull(dataSnapshot.child("name").getValue(String.class))).child("dis").getValue(String.class)));
                        }else {
                            Log.d("hola", "no");
                            before.add("");
                            after.add("");
                            discount.add("");
                        }
                        names.add(dataSnapshot.child("name").getValue().toString());
                        halfPrice.add(dataSnapshot.child("half").getValue().toString());
                        fullPrice.add(dataSnapshot.child("full").getValue().toString());
                        image.add(dataSnapshot.child("image").getValue().toString());
                    }
                }
                DishView dishView = new DishView(names,fullPrice,halfPrice,dish,image,before,after,discount);
                loading.setVisibility(View.INVISIBLE);
                recyclerView.setAdapter(dishView);
                dishView.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        DatabaseReference childref = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(sharedPreferences.getString("state","")).child(menuAuth.getUid()).child("List of Dish");
        menuRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull  DataSnapshot snapshot, @Nullable  String previousChildName) {
                updateChild();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                updateChild();
            }

            @Override
            public void onChildRemoved(@NonNull  DataSnapshot snapshot) {
                updateChild();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable  String previousChildName) {

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
                intent.putExtra("state",sharedPreferences.getString("state",""));
                startActivity(intent);

            }
        });

        Toast.makeText(this, "Swipe down to refresh if new dish added", Toast.LENGTH_SHORT).show();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                menuRef.child(menuAuth.getUid()).child("List of Dish").child(dish).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(!snapshot.exists()){
                            Toast.makeText(AllMenuDish.this, "Empty!! Add Some Dish", Toast.LENGTH_SHORT).show();
                            loading.setVisibility(View.INVISIBLE);
                        }else{
                            names.clear();
                            halfPrice.clear();
                            before.clear();
                            after.clear();
                            discount.clear();
                            fullPrice.clear();
                            image.clear();
                            for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                                if(dataSnapshot.child("Discount").child(dataSnapshot.child("name").getValue().toString()).child("dis").exists()){
                                    Log.d("hola","yes");
                                    before.add(String.valueOf(dataSnapshot.child("Discount").child(Objects.requireNonNull(dataSnapshot.child("name").getValue(String.class))).child("before").getValue(String.class)));
                                    after.add(String.valueOf(dataSnapshot.child("Discount").child(Objects.requireNonNull(dataSnapshot.child("name").getValue(String.class))).child("after").getValue(String.class)));
                                    discount.add(String.valueOf(dataSnapshot.child("Discount").child(Objects.requireNonNull(dataSnapshot.child("name").getValue(String.class))).child("dis").getValue(String.class)));
                                }else {
                                    Log.d("hola", "no");
                                    before.add("");
                                    after.add("");
                                    discount.add("");
                                }
                                names.add(dataSnapshot.child("name").getValue().toString());
                                halfPrice.add(dataSnapshot.child("half").getValue().toString());
                                fullPrice.add(dataSnapshot.child("full").getValue().toString());
                                image.add(dataSnapshot.child("image").getValue().toString());
                            }
                        }
                        loading.setVisibility(View.INVISIBLE);
                        swipeRefreshLayout.setRefreshing(false);
                        recyclerView.setAdapter(new DishView(names,fullPrice,halfPrice,dish,image,before,after,discount));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });

    }

    private void initialise() {
        sharedPreferences = getSharedPreferences("loginInfo",MODE_PRIVATE);
        allMenu = FirebaseDatabase.getInstance().getReference().getRoot();
        search = (FloatingActionButton)findViewById(R.id.floatingActionButton2);
        loading = findViewById(R.id.loading);
        menuAuth = FirebaseAuth.getInstance();
        menuRef = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(sharedPreferences.getString("state",""));
        recyclerView = findViewById(R.id.dishRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        dish = getIntent().getStringExtra("Dish");
        swipeRefreshLayout = findViewById(R.id.swipe);
    }

    private void updateChild(){

        menuRef.child(Objects.requireNonNull(menuAuth.getUid())).child("List of Dish").child(dish).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()){
                    loading.setVisibility(View.INVISIBLE);
                }else{
                    names.clear();
                    halfPrice.clear();
                    image.clear();
                    before.clear();
                    after.clear();
                    discount.clear();
                    fullPrice.clear();
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                        if(dataSnapshot.child("Discount").child(dataSnapshot.child("name").getValue().toString()).child("dis").exists()){
                            Log.d("hola","yes");
                            before.add(String.valueOf(dataSnapshot.child("Discount").child(Objects.requireNonNull(dataSnapshot.child("name").getValue(String.class))).child("before").getValue(String.class)));
                            after.add(String.valueOf(dataSnapshot.child("Discount").child(Objects.requireNonNull(dataSnapshot.child("name").getValue(String.class))).child("after").getValue(String.class)));
                            discount.add(String.valueOf(dataSnapshot.child("Discount").child(Objects.requireNonNull(dataSnapshot.child("name").getValue(String.class))).child("dis").getValue(String.class)));
                        }else {
                            Log.d("hola", "no");
                            before.add("");
                            after.add("");
                            discount.add("");
                        }
                        names.add(dataSnapshot.child("name").getValue().toString());
                        halfPrice.add(dataSnapshot.child("half").getValue().toString());
                        fullPrice.add(dataSnapshot.child("full").getValue().toString());
                        image.add(dataSnapshot.child("image").getValue().toString());
                    }
                }
                loading.setVisibility(View.INVISIBLE);
                recyclerView.setAdapter(new DishView(names,fullPrice,halfPrice,dish,image,before,after,discount));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}