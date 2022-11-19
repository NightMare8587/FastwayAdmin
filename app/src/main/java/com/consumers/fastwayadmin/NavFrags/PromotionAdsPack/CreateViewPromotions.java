package com.consumers.fastwayadmin.NavFrags.PromotionAdsPack;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.consumers.fastwayadmin.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class CreateViewPromotions extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Button create;
    boolean coolDownActive = false;
    DatabaseReference databaseReference;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    TextView coolDown;
    RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_view_promotions);
        sharedPreferences = getSharedPreferences("loginInfo",MODE_PRIVATE);
        editor = sharedPreferences.edit();
        recyclerView = findViewById(R.id.previousPromotionsRecyclerView);
        coolDown = findViewById(R.id.coolDownPeriodTextPromotion);
        create = findViewById(R.id.createPromotionButtonActivity);
        if(!sharedPreferences.contains("promotionActivityShown")){
            AlertDialog.Builder builder = new AlertDialog.Builder(CreateViewPromotions.this);
            LayoutInflater inflater = (LayoutInflater) getSystemService( Context.LAYOUT_INFLATER_SERVICE );
            View view = inflater.inflate(R.layout.promotion_dialog,null);
            builder.setView(view);

            builder.setPositiveButton("I Agree", (dialogInterface, i) -> {
                editor.putString("promotionActivityShown","yes");
                editor.apply();
            });
            builder.setCancelable(false);
            builder.setNegativeButton("Exit", (dialogInterface, i) -> dialogInterface.dismiss());
            builder.create().show();
        }

        databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Admin").child(Objects.requireNonNull(auth.getUid())).child("PromotionsAds");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChild("coolDownTime")){
                    coolDown.setVisibility(View.VISIBLE);
                    long coolDownTime = Long.parseLong(snapshot.child("coolDownTime").getValue(String.class));
                    coolDown.setText("CoolDown Period Ends: " + TimeUnit.MILLISECONDS.toHours(coolDownTime));
                    coolDownActive = true;
                }else if(snapshot.hasChild("Current Promotion")){
                    coolDown.setVisibility(View.VISIBLE);
                    coolDown.setText("Current Promotion Active ");
                    coolDownActive = true;
                }



                create.setOnClickListener(click -> {
                    if(coolDownActive){
                        Toast.makeText(CreateViewPromotions.this, "CoolDown/Promotion Period Active", Toast.LENGTH_SHORT).show();
                    }else{
                        AlertDialog.Builder builder = new AlertDialog.Builder(CreateViewPromotions.this);
                        builder.setTitle("Create Promotion").setMessage("Once to click create, Ordinalo will contact you for required details and pricing")
                                .setPositiveButton("Create", (dialogInterface, i) -> {
                                    DatabaseReference ordinaloRef = FirebaseDatabase.getInstance().getReference().getRoot().child("Complaints").child("PromotionRequest").child(sharedPreferences.getString("state","")).child(auth.getUid());

                                    ordinaloRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot1) {
                                            if(snapshot1.exists()){
                                                Toast.makeText(CreateViewPromotions.this, "Request Already Sent!", Toast.LENGTH_SHORT).show();
                                            }else
                                            {
                                                HashMap<String,String>map = new HashMap<>();
                                                SharedPreferences resInfo = getSharedPreferences("RestaurantInfo",MODE_PRIVATE);
                                                map.put("resName",resInfo.getString("hotelName",""));
                                                map.put("resNumber",resInfo.getString("hotelNumber",""));
                                                map.put("resAddress",resInfo.getString("hotelAddress",""));
                                                map.put("locality",sharedPreferences.getString("locality",""));

                                                ordinaloRef.setValue(map);
                                                Toast.makeText(CreateViewPromotions.this, "Request Sent Successfully", Toast.LENGTH_SHORT).show();
                                                finish();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });


                                }).setNegativeButton("Wait", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                }).create();
                        builder.show();
                    }


                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
}