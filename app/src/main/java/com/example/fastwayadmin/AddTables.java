package com.example.fastwayadmin;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.net.URI;

public class AddTables extends AppCompatActivity {

    EditText tableNumber;
    EditText numberOfSeats;
    Button generateQrCode;
    FirebaseAuth tableAuth;
    DatabaseReference tableRef;
    String url = "https://www.qrcode-monkey.com/qr/custom/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_tables);
        initialise();

        generateQrCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("data",tableNumber.getText().toString())
                        .appendQueryParameter("size","300")
                        .appendQueryParameter("download", "true")
                        .appendQueryParameter("file","png");

                builder.build();
                Log.i("info",url+builder.toString());
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