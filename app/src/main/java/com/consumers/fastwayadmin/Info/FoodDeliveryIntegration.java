package com.consumers.fastwayadmin.Info;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.consumers.fastwayadmin.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class FoodDeliveryIntegration extends AppCompatActivity {
    LinearLayout linearLayout;
    RadioGroup one,two;
    boolean own = true;
    EditText distance,price;
    Button proceed;
    SharedPreferences resInfo;
    SharedPreferences.Editor editor;
    String default1 = "yes", default2 = "own";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_delivery_integration);
        linearLayout = findViewById(R.id.linearLayoutAskForPricing);
        one = findViewById(R.id.radioGroupYesNoDelivery);
        two = findViewById(R.id.radioGroupServiceDelivery);
        distance = findViewById(R.id.editTextDistanceDelivery);
        price = findViewById(R.id.editTextDeliveryPrice);
        proceed = findViewById(R.id.proceedWithDeliverySettings);

        resInfo = getSharedPreferences("RestaurantInfo",MODE_PRIVATE);
        editor = resInfo.edit();

        one.setOnCheckedChangeListener((radioGroup, i) -> {
            switch (i){
                case R.id.noRadioButton:
                    default1 = "no";
                    break;
                case R.id.yesRadioButton:
                    default1 = "yes";
                    break;
            }
        });

        two.setOnCheckedChangeListener((radioGroup, i) -> {
            switch (i){
                case R.id.ownRadioButton:
                    default2 = "own";
                    own = true;
                    linearLayout.setVisibility(View.VISIBLE);
                    break;
                case R.id.thirdPartyRadioButton:
                    Toast.makeText(FoodDeliveryIntegration.this, "Not Available", Toast.LENGTH_SHORT).show();
                    break;
            }
        });
        

        proceed.setOnClickListener(click -> {
            if(default1.equals("no")){
                editor.putString("foodDelivery","no");
                editor.apply();
                FirebaseAuth auth = FirebaseAuth.getInstance();
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Admin").child(Objects.requireNonNull(auth.getUid())).child("FoodDelivery");
                databaseReference.removeValue();
                finish();
                return;
            }


            if(own){
                if(distance.getText().toString().equals("0") || distance.getText().toString().equals("")){
                    Toast.makeText(FoodDeliveryIntegration.this, "Invalid Input", Toast.LENGTH_SHORT).show();
                    distance.requestFocus();
                    return;
                }

                String distanceFood = distance.getText().toString();
                String priceFood;
                if(price.getText().toString().equals(""))
                    priceFood = "0";
                else
                    priceFood = price.getText().toString();
                FirebaseAuth auth = FirebaseAuth.getInstance();
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Admin").child(Objects.requireNonNull(auth.getUid())).child("FoodDelivery");
                databaseReference.child("foodDelivery").setValue("yes");
                databaseReference.child("foodDeliveryType").setValue("own");
                databaseReference.child("distance").setValue(distanceFood);
                databaseReference.child("price").setValue(priceFood);


                editor.putString("foodDelivery","yes");
                editor.putString("price",priceFood);
                editor.putString("distance",distanceFood);
                editor.putString("foodDeliveryType","own");
                editor.apply();
            }else{
                Toast.makeText(this, "Nothing Selected", Toast.LENGTH_SHORT).show();
//                FirebaseAuth auth = FirebaseAuth.getInstance();
//                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Admin").child(Objects.requireNonNull(auth.getUid())).child("FoodDelivery");
//                databaseReference.child("foodDelivery").setValue("yes");
//                databaseReference.child("foodDeliveryType").setValue("thirdParty");
//
//                editor.putString("foodDelivery","yes");
//                editor.putString("foodDeliveryType","thirdParty");
//                editor.apply();
            }

            Toast.makeText(this, "Completed", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}