package com.consumers.fastwayadmin.HomeScreen.ReportSupport;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
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
    String id;
    String channel_id = "notification_channel";
    EditText editText;
    String issueName,issueDetail,userName,userEmail;
    Button submitReport;
    DatabaseReference addToBlockList;
    DatabaseReference getUserDetails;
    DatabaseReference reportRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_options);
        initialise();
        editText = findViewById(R.id.specifyinDetailEditText);
        getUserDetails = FirebaseDatabase.getInstance().getReference().getRoot().child("Users").child(id);
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
            }else {
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
                                addToBlockList.child(Objects.requireNonNull(auth.getUid())).child("id").setValue(auth.getUid());
                                OtherReportClass otherReportClass = new OtherReportClass(issueName,issueDetail,userName,userEmail);
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
                                OtherReportClass otherReportClass = new OtherReportClass(issueName,issueDetail,userName,userEmail);
                                reportRef.child(Objects.requireNonNull(auth.getUid())).setValue(otherReportClass);
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
        id = getIntent().getStringExtra("ID");
        addToBlockList = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(Objects.requireNonNull(auth.getUid())).child("Blocked List");
        reportRef = FirebaseDatabase.getInstance().getReference().getRoot().child("Complaints").child("Admin");
    }
}