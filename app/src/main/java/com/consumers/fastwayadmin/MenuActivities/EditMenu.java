package com.consumers.fastwayadmin.MenuActivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.consumers.fastwayadmin.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class EditMenu extends AppCompatActivity {

    EditText fullPlate,halfPlate,name;
    Button saveChanges;
    FirebaseAuth editAuth;
    SharedPreferences sharedPreferences;
    DatabaseReference editRef;
    String type;
    String change;
    String dish;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_menu);
        sharedPreferences = getSharedPreferences("loginInfo",MODE_PRIVATE);
        initialise();

        saveChanges.setOnClickListener(new View.OnClickListener() {
            final Map<String,Object> update = new HashMap<String,Object>();
            @Override
            public void onClick(View view) {

                if(fullPlate.length() != 0){
                 editRef.addListenerForSingleValueEvent(new ValueEventListener() {
                     @Override
                     public void onDataChange(@NonNull  DataSnapshot snapshot) {
                         if(snapshot.child("Discount").child(dish).child("dis").exists()){
                             editRef.child("Discount").removeValue();
                         }
                         editRef.child("full").setValue(fullPlate.getText().toString());
                     }

                     @Override
                     public void onCancelled(@NonNull  DatabaseError error) {

                     }
                 });
                }

                if(halfPlate.length() != 0){
                    editRef.child("half").setValue(halfPlate.getText().toString());
                }
                Toast.makeText(EditMenu.this, "Changes saved successfully", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

    }

    private void initialise() {
        fullPlate = findViewById(R.id.fullPlateNewPrice);
        halfPlate = findViewById(R.id.newHalfPlatePrice);
        saveChanges = findViewById(R.id.updateNewChanges);
        name = findViewById(R.id.editDishName);
        editAuth = FirebaseAuth.getInstance();
        type = getIntent().getStringExtra("type");
        dish = getIntent().getStringExtra("dish");
//        change = getIntent().getStringExtra("change");
//        if(change.equals("price")){
//            fullPlate.setVisibility(View.VISIBLE);
//            halfPlate.setVisibility(View.VISIBLE);
//        }else{
//            name.setVisibility(View.VISIBLE);
//        }
        editRef = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(sharedPreferences.getString("state","")).child(Objects.requireNonNull(editAuth.getUid())).child("List of Dish")
                                .child(type).child(dish);
    }
}