package com.consumers.fastwayadmin.Dish.SearchDishFastway;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.consumers.fastwayadmin.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class SearchFastwayDatabase extends AppCompatActivity {
    RecyclerView recyclerView;
    List<String> dishName = new ArrayList<>();
    EditText editText;

    Button button;
    List<String> dishImage = new ArrayList<>();
    String dish;
    DatabaseReference reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_fastway_database);
        recyclerView = findViewById(R.id.searchFastwayRecyclerView);
        editText = findViewById(R.id.searchFastwayEditText);
        button = findViewById(R.id.searchFastwayButton);
        dish = getIntent().getStringExtra("dish");
        reference = FirebaseDatabase.getInstance().getReference().getRoot().child("Global Dish").child(dish);

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

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length() == 0){
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

                String search = charSequence.toString();
                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            dishName.clear();
                            dishImage.clear();
                            for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                                String dis = String.valueOf(dataSnapshot.getKey());
                                if(dis.contains(search)) {
                                    dishImage.add(String.valueOf(dataSnapshot.child("dishImage").getValue()));
                                    dishName.add(String.valueOf(dataSnapshot.getKey()));
                                }
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

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }
}