 package com.consumers.fastwayadmin.NavFrags.ResDishTracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.consumers.fastwayadmin.R;

import java.util.ArrayList;
import java.util.List;

 public class seeAllDishAnalysis extends AppCompatActivity {
    RecyclerView recyclerView;
    ArrayList<String> dishName = new ArrayList<>();
    ArrayList<String> dishValue = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_all_dish_analysis);
        dishName = (ArrayList<String>) getIntent().getSerializableExtra("dishName");
        dishValue = (ArrayList<String>) getIntent().getSerializableExtra("dishValue");
        recyclerView = findViewById(R.id.recyclerViewSeeAllDishAnalysis);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new recyclerAllDish(dishName,dishValue,seeAllDishAnalysis.this));
    }
}