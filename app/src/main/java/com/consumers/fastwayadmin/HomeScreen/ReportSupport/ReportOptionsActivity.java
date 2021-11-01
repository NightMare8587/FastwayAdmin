package com.consumers.fastwayadmin.HomeScreen.ReportSupport;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.consumers.fastwayadmin.R;
import com.developer.kalert.KAlertDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class ReportOptionsActivity extends AppCompatActivity {
    FirebaseAuth auth;
    RadioGroup radioGroup;
    String userID;
    String channel_id = "notification_channel";
    EditText editText;
    String state;
    DatabaseReference checkIfReportedAlready;
    String issueName,issueDetail,userName,userEmail;
    Button submitReport;
    DatabaseReference addToBlockList;
    DatabaseReference getUserDetails;
    SharedPreferences resLocation;
    DatabaseReference reportRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_options);
        initialise();
        resLocation = getSharedPreferences("loginInfo",MODE_PRIVATE);
        state = resLocation.getString("state","");
        checkIfReportedAlready = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(state).child(Objects.requireNonNull(auth.getUid())).child("Reported Users");
        checkIfReportedAlready.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChild(userID)){
                    KAlertDialog kAlertDialog = new KAlertDialog(ReportOptionsActivity.this,KAlertDialog.ERROR_TYPE);
                    kAlertDialog.setTitleText("Already Reported")
                            .setContentText("Looks like you have already reported this user")
                            .setConfirmText("Exit")
                            .setConfirmClickListener(k -> {
                                k.dismissWithAnimation();
                                finish();
                            });
                    kAlertDialog.setCancelable(false);
                    kAlertDialog.show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        editText = findViewById(R.id.specifyinDetailEditText);
        getUserDetails = FirebaseDatabase.getInstance().getReference().getRoot().child("Users").child(userID);
        getUserDetails.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    userName = String.valueOf(snapshot.child("name").getValue());
                    userEmail = String.valueOf(snapshot.child("email").getValue());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        submitReport.setOnClickListener(v -> {
            if(editText.getText().toString().equals("")){
                Toast.makeText(ReportOptionsActivity.this, "Field can't be empty :)", Toast.LENGTH_SHORT).show();
            }
            else{
                reportRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.hasChild(Objects.requireNonNull(auth.getUid()))){
                            KAlertDialog kAlertDialog = new KAlertDialog(ReportOptionsActivity.this,KAlertDialog.ERROR_TYPE)
                                    .setTitleText("Error")
                                    .setContentText("there is an ongoing report from this account")
                                    .setConfirmText("Exit")
                                    .setConfirmClickListener(k -> {
                                        k.dismissWithAnimation();
                                        finish();
                                    });
                            kAlertDialog.create();
                            kAlertDialog.show();
                        }else{
                            KAlertDialog kAlertDialog = new KAlertDialog(ReportOptionsActivity.this,KAlertDialog.WARNING_TYPE)
                                    .setTitleText("Warning")
                                    .setContentText("Reporting a customer will permanently ban them from this restaurant")
                                    .setConfirmText("Report")
                                    .setCancelText("Don't ban just report")
                                    .setConfirmClickListener(new KAlertDialog.KAlertClickListener() {
                                        @Override
                                        public void onClick(KAlertDialog kAlertDialog) {
                                            int id = radioGroup.getCheckedRadioButtonId();
                                            switch (id) {
                                                case R.id.customerBehaviourRadioButton:
                                                    issueName = "Customer Behaviour";
                                                    issueDetail = editText.getText().toString();
                                                    break;
                                                case R.id.otherProblemRadioButton:
                                                    issueName = "Others";
                                                    issueDetail = editText.getText().toString();
                                                    break;
                                            }
                                            DatabaseReference reportUsers = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(state).child(auth.getUid());
                                            reportUsers.child("Reported Users").child(userID).child("authId").setValue(userID);
                                            SharedPreferences sharedPreferences = getSharedPreferences("RestaurantInfo",MODE_PRIVATE);
                                            addToBlockList.child("authId").setValue(userID);
                                            addToBlockList.child("time").setValue(String.valueOf(System.currentTimeMillis()));
                                            updateReportValue(userID);
                                            OtherReportClass otherReportClass = new OtherReportClass(issueName,issueDetail,userName,userEmail,userID,sharedPreferences.getString("hotelName",""),state);
                                            reportRef.child(Objects.requireNonNull(auth.getUid())).setValue(otherReportClass);
                                            generateNotification();
                                            kAlertDialog.dismissWithAnimation();

                                            finish();
                                        }
                                    }).setCancelClickListener(new KAlertDialog.KAlertClickListener() {
                                        @Override
                                        public void onClick(KAlertDialog kAlertDialog) {
                                            int id = radioGroup.getCheckedRadioButtonId();
                                            switch (id) {
                                                case R.id.customerBehaviourRadioButton:
                                                    issueName = "Customer Behaviour";
                                                    issueDetail = editText.getText().toString();
                                                    break;
                                                case R.id.otherProblemRadioButton:
                                                    issueName = "Others";
                                                    issueDetail = editText.getText().toString();
                                                    break;
                                            }
                                            SharedPreferences sharedPreferences = getSharedPreferences("RestaurantInfo",MODE_PRIVATE);
                                            OtherReportClass otherReportClass = new OtherReportClass(issueName,issueDetail,userName,userEmail,userID,sharedPreferences.getString("hotelName",""),state);
                                            reportRef.child(Objects.requireNonNull(auth.getUid())).setValue(otherReportClass);
                                            updateReportValue(userID);
                                            DatabaseReference reportUsers = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(state).child(auth.getUid());
                                            reportUsers.child("Reported Users").child(userID).child("authId").setValue(userID);
                                            generateNotification();
                                            Toast.makeText(ReportOptionsActivity.this, "Report Submitted Successfully", Toast.LENGTH_SHORT).show();
                                            kAlertDialog.dismissWithAnimation();
                                            finish();
                                        }
                                    });
                            kAlertDialog.setCanceledOnTouchOutside(false);
                            kAlertDialog.create();
                            kAlertDialog.show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


            }
        });

    }

    private void updateReportValue(String userID) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Users").child(userID);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.hasChild("reports")){
                    databaseReference.child("reports").setValue("1");
                }else
                {
                    int num = Integer.parseInt(String.valueOf(snapshot.child("reports").getValue()));
                    num = num + 1;
                    databaseReference.child("reports").setValue(num + "");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void generateNotification() {
        NotificationCompat.Builder builder
                = new NotificationCompat
                .Builder(ReportOptionsActivity.this,
                channel_id)
                .setContentTitle("Ticket raised successfully")
                .setContentText("We will notify you as soon as your issue is resolved")
                .setPriority(NotificationManager.IMPORTANCE_MAX)
                .setSmallIcon(R.drawable.ic_baseline_home_24)
                .setAutoCancel(true)
                .setVibrate(new long[]{1000, 1000, 1000,
                        1000, 1000})
                .setOnlyAlertOnce(true);

        NotificationManager notificationManager
                = (NotificationManager) getSystemService(
                Context.NOTIFICATION_SERVICE);
        // Check if the Android Version is greater than Oreo
        if (Build.VERSION.SDK_INT
                >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel
                    = new NotificationChannel(
                    channel_id, "web_app",
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(
                    notificationChannel);
        }

        notificationManager.notify(15, builder.build());
    }

    private void initialise() {
        auth = FirebaseAuth.getInstance();
        radioGroup = findViewById(R.id.radioGroupOthers);
        submitReport = findViewById(R.id.SubmitOthersReport);
        userID = getIntent().getStringExtra("ID");
        Log.i("id",userID);
        SharedPreferences sharedPreferences = getSharedPreferences("loginInfo",MODE_PRIVATE);
        addToBlockList = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(sharedPreferences.getString("state","")).child(Objects.requireNonNull(auth.getUid())).child("Blocked List").child(userID);
        reportRef = FirebaseDatabase.getInstance().getReference().getRoot().child("Complaints").child("Admin");
    }
}