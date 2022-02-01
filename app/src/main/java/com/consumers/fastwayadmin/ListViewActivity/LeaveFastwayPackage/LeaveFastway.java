package com.consumers.fastwayadmin.ListViewActivity.LeaveFastwayPackage;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.consumers.fastwayadmin.R;
import com.consumers.fastwayadmin.SplashAndIntro.SplashScreen;
import com.developer.kalert.KAlertDialog;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class LeaveFastway extends AppCompatActivity {
    DatabaseReference databaseReference;
    EditText editText;
    GoogleSignInClient googleSignInClient;
    Button submitAndLeave;
    RadioButton radioButton,radioButton1;
    String UID;
    String reasonForLeave = "Not Finding Fastway Usefull";
    RadioGroup radioGroup;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leave_fastway);
        initialise();

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
              if(radioButton.getId() == i)
                  reasonForLeave = "Not Finding Fastway Usefull";
              else
                  reasonForLeave = "Others";

            }
        });
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(LeaveFastway.this,gso);
        submitAndLeave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(LeaveFastway.this);
                builder.setTitle("Leave Fastway").setMessage("Do you sure wanna leave fastway ?\nYour data will be stored for 30 days in case you wanna return to fastway")
                        .setPositiveButton("Leave Fastway", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                        databaseReference.child("status").setValue("offline");
                                databaseReference.child("acceptingOrders").setValue("no");
                                databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Admin").child(UID).child("Restaurant Documents");
                                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if(snapshot.exists()){
                                            databaseReference.child("verified").setValue("no");
                                            databaseReference.child("leftFastway").setValue(String.valueOf(System.currentTimeMillis()));
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                                auth.signOut();
                                googleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                    }
                                });
                                SharedPreferences settings = getSharedPreferences("loginInfo", MODE_PRIVATE);
                                settings.edit().clear().commit();

                                SharedPreferences res = getSharedPreferences("RestaurantInfo", MODE_PRIVATE);
                                res.edit().clear().commit();

                                SharedPreferences intro = getSharedPreferences("IntroAct", MODE_PRIVATE);
                                intro.edit().clear().commit();

                                SharedPreferences location = getSharedPreferences("LocationMaps", MODE_PRIVATE);
                                location.edit().clear().commit();

                                SharedPreferences cashCommission = getSharedPreferences("CashCommission", MODE_PRIVATE);
                                cashCommission.edit().clear().commit();

                                KAlertDialog kAlertDialog = new KAlertDialog(LeaveFastway.this,KAlertDialog.SUCCESS_TYPE)
                                        .setTitleText("Success")
                                        .setContentText("Thanks for using Fastway :)")
                                        .setConfirmText("Exit")
                                        .setConfirmClickListener(click -> {
                                            click.dismiss();
                                            startActivity(new Intent(LeaveFastway.this, SplashScreen.class));
                                            finish();
                                        });

                                kAlertDialog.setCancelable(false);
                                kAlertDialog.show();
                            }
                        }).setNegativeButton("No, Wait", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).create();
                builder.show();
            }
        });
    }

    private void initialise() {
        submitAndLeave = findViewById(R.id.submitAndLeaveFastway);
        radioButton = findViewById(R.id.radioButtonHygiene);
        radioButton = findViewById(R.id.radioButtonStaff);
        editText = findViewById(R.id.editTextReportRestaurant);
        radioGroup = findViewById(R.id.radioGroupReportRestaurant);
        UID = auth.getUid() + "";
        SharedPreferences sharedPreferences = getSharedPreferences("loginInfo",MODE_PRIVATE);
        databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(sharedPreferences.getString("state","")).child(Objects.requireNonNull(UID));

    }
}