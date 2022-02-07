package com.consumers.fastwayadmin.ListViewActivity.ResTimingsPackage;

import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.consumers.fastwayadmin.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Objects;

public class AddRestaurantTimings extends AppCompatActivity {
    DatabaseReference databaseReference;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    SharedPreferences sharedPreferences;
    Button startButton,endButton;
    TextView resStart,resEnd;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_restaurant_timings);
        resStart = findViewById(R.id.resStartTimingTextView);
        resEnd = findViewById(R.id.resClosingTimeTextView);
        startButton = findViewById(R.id.setStartTimingButton);
        endButton = findViewById(R.id.resClosingButton);
        databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Admin").child(Objects.requireNonNull(auth.getUid())).child("Restaurant Timings");
        sharedPreferences = getSharedPreferences("loginInfo",MODE_PRIVATE);
        editor = sharedPreferences.edit();
        if(sharedPreferences.contains("resStartTime")) {
            resStart.setVisibility(View.VISIBLE);
            resStart.setText(sharedPreferences.getString("resStartTime", ""));
        }else{
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        if(snapshot.hasChild("startTime")) {
                            resStart.setText(String.valueOf(snapshot.child("startTime").getValue()));
                            editor.putString("resStartTime",String.valueOf(snapshot.child("startTime").getValue()));
                            editor.apply();
                        }
                        if(snapshot.hasChild("endTime")) {
                            resEnd.setText(String.valueOf(snapshot.child("endTime").getValue()));
                            editor.putString("resEndTime",String.valueOf(snapshot.child("endTime").getValue()));
                            editor.apply();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        if(sharedPreferences.contains("resEndTime")) {
            resStart.setVisibility(View.VISIBLE);
            resStart.setText(sharedPreferences.getString("resEndTime", ""));
        }

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(AddRestaurantTimings.this, new TimePickerDialog.OnTimeSetListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        resStart.setText( selectedHour + ":" + selectedMinute);

                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });

        endButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(AddRestaurantTimings.this, new TimePickerDialog.OnTimeSetListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        resEnd.setText( selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });
    }
}