package com.consumer.fastwayadmin.MenuActivities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.consumer.fastwayadmin.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class EditMenu extends AppCompatActivity {

    EditText fullPlate,halfPlate,name;
    Button saveChanges;
    FirebaseAuth editAuth;
    DatabaseReference editRef;
    String type;
    String dish;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_menu);
        initialise();

        saveChanges.setOnClickListener(new View.OnClickListener() {
            final Map<String,Object> update = new HashMap<String,Object>();
            @Override
            public void onClick(View view) {
                if(name.length() != 0){
                    update.put("name",name.getText().toString());
                }

                if(fullPlate.length() != 0){
                  update.put("full",fullPlate.getText().toString());
                }

                if(halfPlate.length() != 0){
                   update.put("half",halfPlate.getText().toString());
                }

                editRef.updateChildren(update);
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
        editRef = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(editAuth.getUid()).child("List of Dish")
                                .child(type).child(dish);
    }
}