package com.consumers.fastwayadmin.Chat.RandomChatFolder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.consumers.fastwayadmin.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class RandomChatWithUsers extends AppCompatActivity {
    DatabaseReference databaseReference;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    int count = 1;
    List<String> messages = new ArrayList<>();
    List<String> userID = new ArrayList<>();
    List<String> time = new ArrayList<>();
    List<List<String>> finaLListMeesage = new ArrayList<>();
    List<List<String>> finaLListLeftOr= new ArrayList<>();
    List<List<String>> finaLListTime= new ArrayList<>();
    RecyclerView recyclerView;
    HashMap<String,String> map = new HashMap<>();
    List<String> leftOr = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_random_chat_with_users);
        recyclerView = findViewById(R.id.randomChatWithUserRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Admin").child(Objects.requireNonNull(auth.getUid())).child("messages");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    messages.clear();
                    userID.clear();
                    leftOr.clear();
                    time.clear();
                    map.clear();
                    count = 1;
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                        count = 1;
                        userID.add(String.valueOf(dataSnapshot.getKey()));
                        for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                            time.add(String.valueOf(dataSnapshot1.getKey()));
                            leftOr.add(String.valueOf(dataSnapshot1.child("id").getValue()));
                            messages.add(String.valueOf(dataSnapshot1.child("message").getValue()));
                            if(count == 1 && Integer.parseInt(String.valueOf(dataSnapshot1.child("id").getValue())) == 0) {
                                map.put(dataSnapshot.getKey(), String.valueOf(dataSnapshot1.child("name").getValue()));
                                count++;
                            }
                        }
                        finaLListMeesage.add(new ArrayList<>(messages));
                        finaLListLeftOr.add(new ArrayList<>(leftOr));
                        finaLListTime.add(new ArrayList<>(time));
                        time.clear();
                        leftOr.clear();
                        messages.clear();
                    }
                    recyclerView.setAdapter(new ClassRandom(messages,userID,time,map,leftOr,RandomChatWithUsers.this,finaLListMeesage,finaLListLeftOr,finaLListTime));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}