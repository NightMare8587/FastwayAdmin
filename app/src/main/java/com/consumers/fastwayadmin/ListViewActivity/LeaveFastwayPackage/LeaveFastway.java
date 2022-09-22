package com.consumers.fastwayadmin.ListViewActivity.LeaveFastwayPackage;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialog;

import com.consumers.fastwayadmin.ListViewActivity.InitiatePayoutForAdminNEFT;
import com.consumers.fastwayadmin.NavFrags.CashCommission.CashFreeGateway;
import com.consumers.fastwayadmin.R;
import com.consumers.fastwayadmin.SplashAndIntro.SplashScreen;
import com.developer.kalert.KAlertDialog;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.Objects;

public class LeaveFastway extends AppCompatActivity {
    DatabaseReference databaseReference;
    EditText editText;
    DecimalFormat decimalFormat = new DecimalFormat("0.00");
    GoogleSignInClient googleSignInClient;
    Button submitAndLeave;
    boolean pendingDue = false;
    double totalCash,totalMonth,platformFee,totalPayoutAmount;
    RadioButton radioButton;
    boolean stopCode = false;
    String UID;
    String reasonForLeave = "Not Finding Ordinalo Usefull";
    RadioGroup radioGroup;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leave_fastway);
        initialise();
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                DatabaseReference getAllAmountInfo = FirebaseDatabase.getInstance().getReference().getRoot().child("Admin").child(UID);
                getAllAmountInfo.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.hasChild("totalPayoutAmount")){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    new KAlertDialog(LeaveFastway.this,KAlertDialog.WARNING_TYPE)
                                            .setTitleText("Payout").setContentText("You have payout amount left. Withdraw that first")
                                            .setConfirmText("Withdraw").setCancelText("Exit")
                                            .setConfirmClickListener(click -> {
                                                click.dismissWithAnimation();
                                                startActivity(new Intent(LeaveFastway.this, InitiatePayoutForAdminNEFT.class));
                                            }).setCancelClickListener(click -> {
                                                click.dismissWithAnimation();
                                                finish();
                                            }).show();
                                    stopCode = true;
                                }
                            });
                        }

                        if(snapshot.hasChild("totalCashTakeAway")){
                            pendingDue = true;
                            totalCash = Double.parseDouble(String.valueOf(snapshot.child("totalCashTakeAway").getValue()));
                        }

                        if(snapshot.hasChild("totalMonthAmount")){
                            pendingDue = true;
                            totalMonth = Double.parseDouble(String.valueOf(snapshot.child("totalMonthAmount").getValue()));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        radioGroup.setOnCheckedChangeListener((radioGroup, i) -> {
          if(radioButton.getId() == i)
              reasonForLeave = "Not Finding Ordinalo Usefull";
          else
              reasonForLeave = "Others";

        });
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(LeaveFastway.this,gso);
        submitAndLeave.setOnClickListener(view -> {
            DatabaseReference checkPendingCommission = FirebaseDatabase.getInstance().getReference().getRoot().child("Admin").child(UID);
            if(!stopCode){
                AlertDialog.Builder builder = new AlertDialog.Builder(LeaveFastway.this);
                builder.setTitle("Pending Payout").setMessage("You need to withdraw your pending payout first")
                        .setPositiveButton("Withdraw Now", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startActivity(new Intent(LeaveFastway.this,InitiatePayoutForAdminNEFT.class));
                                finish();
                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create().show();
            }else{
                if(pendingDue) {
                    double commAmount = (totalCash * 3) / 100;
                    double gst = (totalCash * 5) / 100;
                    double platformFee = (totalMonth * 4) / 100;
                    AlertDialog.Builder builder = new AlertDialog.Builder(LeaveFastway.this);
                    builder.setTitle("Pending Due's").setMessage("You need to pay off pending amount to leave Ordinalo\n\nCash Commission: \u20b9" + commAmount + "\nGST: \u20b9" + gst + "\nPlatform Fee: " + platformFee + "\nTotal: \u20b9" + decimalFormat.format(gst + commAmount + platformFee));
                    builder.setPositiveButton("Pay Now", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            SharedPreferences sharedPreferences = getSharedPreferences("loginInfo",MODE_PRIVATE);
                            if(sharedPreferences.contains("number")) {
                                Intent intent = new Intent(LeaveFastway.this, CashFreeGateway.class);
                                intent.putExtra("amount", "" + decimalFormat.format((commAmount + gst + platformFee)));
                                startActivityForResult(intent, 2);
                            }else{
                                AlertDialog.Builder getNum = new AlertDialog.Builder(LeaveFastway.this);
                                getNum.setTitle("Contact Number").setMessage("Please add you phone number for payment");
                                LinearLayout linearLayout = new LinearLayout(LeaveFastway.this);
                                linearLayout.setOrientation(LinearLayout.VERTICAL);
                                EditText editText = new EditText(LeaveFastway.this);
                                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                                editText.setHint("Enter Num Here");
                                linearLayout.addView(editText);
                                getNum.setView(linearLayout);

                                getNum.setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if(editText.length() == 10){
                                            SharedPreferences.Editor editor = sharedPreferences.edit();
                                            editor.putString("number",editText.getText().toString());
                                            editor.apply();
                                            Intent intent = new Intent(LeaveFastway.this, CashFreeGateway.class);
                                            intent.putExtra("amount", "" + decimalFormat.format((commAmount + gst + platformFee)));
                                            startActivityForResult(intent, 2);
                                        }else
                                            Toast.makeText(LeaveFastway.this, "Invalid Number", Toast.LENGTH_SHORT).show();
                                    }
                                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                }).create();
                                getNum.show();
                            }
                        }
                    }).setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create();
                    builder.setCancelable(false);
                    builder.show();
                }
            }
//            checkPendingCommission.addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                    if(snapshot.hasChild("totalCashTakeAway")){
//                        int total = Integer.parseInt(Objects.requireNonNull(snapshot.child("totalCashTakeAway").getValue(String.class)));
//                        AlertDialog.Builder builder = new AlertDialog.Builder(LeaveFastway.this);
//                        if(total != 0){
//                            builder.setTitle("Pending Commission").setMessage("You need to pay off pending commission & platform fee than you can leave Foodine");
//                            builder.setPositiveButton("Pay Now", (dialogInterface, i) -> {
//                                Intent intent = new Intent(LeaveFastway.this, CashFreeGateway.class);
//                                intent.putExtra("amount", total + "");
//                                startActivityForResult(intent, 2);
//                            }).setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss()).create();
//                            builder.setCancelable(false);
//                        }else{
//                            builder.setTitle("Leave Foodine").setMessage("Do you sure wanna leave Foodine ?\nYour data will be stored for 30 days in case you wanna return to Foodine")
//                                    .setPositiveButton("Leave Foodine", (dialogInterface, i) -> {
//                                        dialogInterface.dismiss();
//                                        databaseReference.child("status").setValue("offline");
//                                        databaseReference.child("acceptingOrders").setValue("no");
//                                        databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Admin").child(UID).child("Restaurant Documents");
//                                        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//                                            @Override
//                                            public void onDataChange(@NonNull DataSnapshot snapshot1) {
//                                                if(snapshot1.exists()){
//                                                    databaseReference.child("verified").setValue("no");
//                                                    databaseReference.child("leftFastway").setValue(String.valueOf(System.currentTimeMillis()));
//                                                }
//                                            }
//
//                                            @Override
//                                            public void onCancelled(@NonNull DatabaseError error) {
//
//                                            }
//                                        });
//                                        auth.signOut();
//                                        googleSignInClient.signOut().addOnCompleteListener(task -> {
//                                        });
//                                        SharedPreferences settings = getSharedPreferences("loginInfo", MODE_PRIVATE);
//                                        settings.edit().clear().apply();
//
//                                        SharedPreferences res = getSharedPreferences("RestaurantInfo", MODE_PRIVATE);
//                                        res.edit().clear().apply();
//
//                                        SharedPreferences intro = getSharedPreferences("IntroAct", MODE_PRIVATE);
//                                        intro.edit().clear().apply();
//
//                                        SharedPreferences location = getSharedPreferences("LocationMaps", MODE_PRIVATE);
//                                        location.edit().clear().apply();
//
//                                        SharedPreferences cashCommission = getSharedPreferences("CashCommission", MODE_PRIVATE);
//                                        cashCommission.edit().clear().apply();
//
//                                        KAlertDialog kAlertDialog = new KAlertDialog(LeaveFastway.this,KAlertDialog.SUCCESS_TYPE)
//                                                .setTitleText("Success")
//                                                .setContentText("Thanks for using Fastway :)")
//                                                .setConfirmText("Exit")
//                                                .setConfirmClickListener(click -> {
//                                                    click.dismiss();
//                                                    startActivity(new Intent(LeaveFastway.this, SplashScreen.class));
//                                                    finish();
//                                                });
//
//                                        kAlertDialog.setCancelable(false);
//                                        kAlertDialog.show();
//                                    }).setNegativeButton("No, Wait", (dialogInterface, i) -> dialogInterface.dismiss()).create();
//                        }
//                        builder.show();
//                    }else
//                    {
//                        AlertDialog.Builder builder = new AlertDialog.Builder(LeaveFastway.this);
//                        builder.setTitle("Leave Foodine").setMessage("Do you sure wanna leave fastway ?\nYour data will be stored for 30 days in case you wanna return to fastway")
//                                .setPositiveButton("Leave Foodine", (dialogInterface, i) -> {
//                                    dialogInterface.dismiss();
//                                    databaseReference.child("status").setValue("offline");
//                                    databaseReference.child("acceptingOrders").setValue("no");
//                                    databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Admin").child(UID).child("Restaurant Documents");
//                                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//                                        @Override
//                                        public void onDataChange(@NonNull DataSnapshot snapshot12) {
//                                            if(snapshot12.exists()){
//                                                databaseReference.child("verified").setValue("no");
//                                                databaseReference.child("leftFastway").setValue(String.valueOf(System.currentTimeMillis()));
//                                            }
//                                        }
//
//                                        @Override
//                                        public void onCancelled(@NonNull DatabaseError error) {
//
//                                        }
//                                    });
//                                    auth.signOut();
//                                    googleSignInClient.signOut().addOnCompleteListener(task -> {
//                                    });
//                                    SharedPreferences settings = getSharedPreferences("loginInfo", MODE_PRIVATE);
//                                    settings.edit().clear().apply();
//
//                                    SharedPreferences res = getSharedPreferences("RestaurantInfo", MODE_PRIVATE);
//                                    res.edit().clear().apply();
//
//                                    SharedPreferences intro = getSharedPreferences("IntroAct", MODE_PRIVATE);
//                                    intro.edit().clear().apply();
//
//                                    SharedPreferences location = getSharedPreferences("LocationMaps", MODE_PRIVATE);
//                                    location.edit().clear().apply();
//
//                                    SharedPreferences cashCommission = getSharedPreferences("CashCommission", MODE_PRIVATE);
//                                    cashCommission.edit().clear().apply();
//
//                                    KAlertDialog kAlertDialog = new KAlertDialog(LeaveFastway.this,KAlertDialog.SUCCESS_TYPE)
//                                            .setTitleText("Success")
//                                            .setContentText("Thanks for using Fastway :)")
//                                            .setConfirmText("Exit")
//                                            .setConfirmClickListener(click -> {
//                                                click.dismiss();
//                                                startActivity(new Intent(LeaveFastway.this, SplashScreen.class));
//                                                finish();
//                                            });
//
//                                    kAlertDialog.setCancelable(false);
//                                    kAlertDialog.show();
//                                }).setNegativeButton("No, Wait", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//                                dialogInterface.dismiss();
//                            }
//                        }).create();
//                        builder.show();
//                    }
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError error) {
//
//                }
//            });

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == 2){
            KAlertDialog kAlertDialog = new KAlertDialog(LeaveFastway.this,KAlertDialog.SUCCESS_TYPE)
                    .setTitleText("Success")
                    .setContentText("Payment made successfully")
                    .setConfirmText("Exit")
                    .setConfirmClickListener(AppCompatDialog::dismiss);
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Admin").child(UID);
            databaseReference.child("totalCashTakeAway").setValue("0");
            databaseReference.child("totalMonthAmount").setValue("0");
            pendingDue = false;
            databaseReference.child("lastCommissionPaid").setValue(String.valueOf(System.currentTimeMillis()));

            databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Admin").child(UID).child("Restaurant Documents");
            databaseReference.child("verified").setValue("no");

            SharedPreferences sharedPreferences = getSharedPreferences("loginInfo",MODE_PRIVATE);
            databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(sharedPreferences.getString("state","")).child(sharedPreferences.getString("locality","")).child(UID);
            databaseReference.child("status").setValue("offline");
            databaseReference.child("acceptingOrders").setValue("no");

            databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Complaints").child("Registered Restaurants").child(sharedPreferences.getString("state",""));
            databaseReference.child(UID).removeValue();

            auth.signOut();
                                    googleSignInClient.signOut().addOnCompleteListener(task -> {
                                    });
                                    SharedPreferences settings = getSharedPreferences("loginInfo", MODE_PRIVATE);
                                    settings.edit().clear().apply();

            SharedPreferences res = getSharedPreferences("RestaurantInfo", MODE_PRIVATE);
            res.edit().clear().apply();

            SharedPreferences intro = getSharedPreferences("IntroAct", MODE_PRIVATE);
            intro.edit().clear().apply();

            SharedPreferences storeOrders = getSharedPreferences("StoreOrders", MODE_PRIVATE);
            storeOrders.edit().clear().apply();

            SharedPreferences location = getSharedPreferences("LocationMaps", MODE_PRIVATE);
            location.edit().clear().apply();

            SharedPreferences cashCommission = getSharedPreferences("CashCommission", MODE_PRIVATE);
            cashCommission.edit().clear().apply();

            SharedPreferences RestaurantTrackingDaily = getSharedPreferences("RestaurantTrackingDaily", MODE_PRIVATE);
            RestaurantTrackingDaily.edit().clear().apply();

            SharedPreferences RestaurantTrackRecords = getSharedPreferences("RestaurantTrackRecords", MODE_PRIVATE);
            RestaurantTrackRecords.edit().clear().apply();

            SharedPreferences DishAnalysis = getSharedPreferences("DishAnalysis", MODE_PRIVATE);
            DishAnalysis.edit().clear().apply();

                                    new Handler().postDelayed(() -> {
                                        kAlertDialog.dismissWithAnimation();
                                        startActivity(new Intent(LeaveFastway.this,SplashScreen.class));
                                        finish();
                                    },2000);

            kAlertDialog.setCancelable(false);
            kAlertDialog.show();
        }else if(resultCode == 3){
            KAlertDialog kAlertDialog = new KAlertDialog(LeaveFastway.this,KAlertDialog.ERROR_TYPE)
                    .setTitleText("Error")
                    .setContentText("Payment Failed\nIf amount deducted if will get refunded in 2-3 business days")
                    .setConfirmText("Exit")
                    .setConfirmClickListener(AppCompatDialog::dismiss);
            kAlertDialog.setCancelable(false);
            kAlertDialog.show();
        }
    }
}