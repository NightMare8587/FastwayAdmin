package com.consumers.fastwayadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class NotificationActivity extends AppCompatActivity {

    String title,message;
    RecyclerView recyclerView;
    Button delete;
    DatabaseReference reference;
    FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        List<String> titleList = new ArrayList<>();
        List<String> messageList = new ArrayList<>();
        List<String> timeList = new ArrayList<>();
        messageList.clear();
        delete = findViewById(R.id.deleteAllNotifications);
        titleList.clear();
        recyclerView = findViewById(R.id.notificationRecycleView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        auth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference().getRoot().child("Admin").child(Objects.requireNonNull(auth.getUid()));
        reference.child("Notification").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists())
                    Toast.makeText(NotificationActivity.this, "No New Notification :)", Toast.LENGTH_SHORT).show();
                else{
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                        titleList.add(Objects.requireNonNull(dataSnapshot.child("title").getValue()).toString());
                        messageList.add(Objects.requireNonNull(dataSnapshot.child("message").getValue()).toString());
                        timeList.add(Objects.requireNonNull(dataSnapshot.getKey()));
                    }
//                    recyclerView.setAdapter(new NotificationView(titleList,messageList));
                    NotificationView notificationView = new NotificationView(titleList,messageList,timeList);
                    recyclerView.setAdapter(notificationView);
                    notificationView.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        delete.setOnClickListener(click -> {
            reference = FirebaseDatabase.getInstance().getReference().getRoot().child("Admin").child(Objects.requireNonNull(auth.getUid()));
            reference.child("Notification").removeValue();
            Toast.makeText(this, "Deleted Successfully", Toast.LENGTH_SHORT).show();
            finish();
        });

    }
}