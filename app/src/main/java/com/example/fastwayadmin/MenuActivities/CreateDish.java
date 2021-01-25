package com.example.fastwayadmin.MenuActivities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.fastwayadmin.Dish.DishInfo;
import com.example.fastwayadmin.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CreateDish extends AppCompatActivity {
    EditText nameOfDish,halfPlate,fullPlate;
    FirebaseAuth dishAuth;
    DatabaseReference dish;
    Button createDish;
    String menuType;
    String name,half,full;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_dish);
        initialise();

        createDish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(nameOfDish.length() == 0){
                    nameOfDish.setError("Invalid Name");
                    nameOfDish.requestFocus();
                    return;
                }else if(fullPlate.length() == 0){
                    fullPlate.setError("Invalid input");
                    fullPlate.requestFocus();
                    return;
                }

                name = nameOfDish.getText().toString();
                half = halfPlate.getText().toString();
                full = fullPlate.getText().toString();

                addToDatabase(name,half,full);
            }
        });
    }

    private void addToDatabase(String name, String half, String full) {
        DishInfo info = new DishInfo(name,half,full);
        try {
            dish.child("Restaurants").child(dishAuth.getUid()).child("List of Dish").child(menuType).child(name).setValue(info);
            Toast.makeText(this, "Dish Added Successfully", Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            Toast.makeText(this, "Something went Wrong!!!!!", Toast.LENGTH_SHORT).show();
        }finally {
            finish();
        }
    }

    private void initialise() {
        nameOfDish = findViewById(R.id.dishName);
        halfPlate = findViewById(R.id.halfPlatePrice);
        fullPlate = findViewById(R.id.fullPlatePrice);
        createDish = findViewById(R.id.saveDishInfo);
        dishAuth = FirebaseAuth.getInstance();
        dish = FirebaseDatabase.getInstance().getReference().getRoot();
        menuType = getIntent().getStringExtra("Dish");
    }
}