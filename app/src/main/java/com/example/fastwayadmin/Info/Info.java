package com.example.fastwayadmin.Info;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.fastwayadmin.GetLocation;
import com.example.fastwayadmin.HomeScreen;
import com.example.fastwayadmin.R;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hbb20.CountryCodePicker;

import java.util.Objects;

public class Info extends AppCompatActivity {

    EditText nameOfRestaurant,AddressOfRestaurant,nearbyPlace,pinCode,contactNumber;
    Button proceed;
    CountryCodePicker codePicker;
    FirebaseAuth infoAuth;
    DatabaseReference infoRef;
    String name,address,nearby,pin,number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        initialise();

        infoRef.child("Restaurants").addListenerForSingleValueEvent(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot snapshot) {
               if(snapshot.child(Objects.requireNonNull(infoAuth.getUid())).exists()){
                   startActivity(new Intent(Info.this, HomeScreen.class));

                   finish();
               }
           }
           @Override
           public void onCancelled(@NonNull DatabaseError error) {

           }
       });


        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(nameOfRestaurant.length() == 0){
                    nameOfRestaurant.requestFocus();
                    nameOfRestaurant.setError("Field can't be Empty");
                    return;
                }else if(AddressOfRestaurant.length() == 0){
                    AddressOfRestaurant.requestFocus();
                    AddressOfRestaurant.setError("Field cant be Empty");
                    return;
                }else if(pinCode.length() <= 5){
                    pinCode.requestFocus();
                    pinCode.setError("Invalid PinCode");
                    return;
                }else if(nearbyPlace.length() == 0){
                    nearbyPlace.requestFocus();
                    nearbyPlace.setError("Field can't be Empty");
                    return;
                }else if(contactNumber.length() <= 9){
                    contactNumber.requestFocus();
                    contactNumber.setError("Invalid Number");
                    return;
                }
                name = nameOfRestaurant.getText().toString();
                address = AddressOfRestaurant.getText().toString();
                pin = pinCode.getText().toString();
                nearby = nearbyPlace.getText().toString();
                number = codePicker.getSelectedCountryCodeWithPlus() +  contactNumber.getText().toString() + "";

                createChildForRestaurant();
            }
        });
    }

    private void createChildForRestaurant() {
        InfoRestaurant infoRestaurant = new InfoRestaurant(name,address,pin,number,nearby);
        infoRef.child("Restaurants").child(infoAuth.getUid()).setValue(infoRestaurant);
        startActivity(new Intent(Info.this, HomeScreen.class));
        finish();
    }

    private void initialise() {
        infoAuth = FirebaseAuth.getInstance();
        infoRef = FirebaseDatabase.getInstance().getReference().getRoot();
        nameOfRestaurant = findViewById(R.id.nameOfRestaurant);
        AddressOfRestaurant = findViewById(R.id.AddressOfRestaurant);
        nearbyPlace = findViewById(R.id.nearbyPlace);
        pinCode = findViewById(R.id.pincodeRestaurant);
        proceed = findViewById(R.id.proceed);
        codePicker = findViewById(R.id.codePicker);
        contactNumber = findViewById(R.id.contactNumber);

    }
}