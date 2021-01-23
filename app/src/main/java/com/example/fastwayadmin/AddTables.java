package com.example.fastwayadmin;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddTables extends AppCompatActivity {

    EditText tableNumber;
    EditText numberOfSeats;
    Button generateQrCode;
    FirebaseAuth tableAuth;
    DatabaseReference tableRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_tables);
        initialise();

        generateQrCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                
            }
        });
    }

    private void initialise() {
        tableNumber = findViewById(R.id.tableNumber);
        numberOfSeats = findViewById(R.id.numberOfSeats);
        generateQrCode = findViewById(R.id.genrateCode);
        tableAuth = FirebaseAuth.getInstance();
        tableRef = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(tableAuth.getUid());
    }
}