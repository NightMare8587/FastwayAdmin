package com.consumers.fastwayadmin;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.consumers.fastwayadmin.Chat.RandomChat.RandomChatView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RandomChatNoww extends AppCompatActivity {
    FirebaseAuth auth;
    DatabaseReference databaseReference;
    RecyclerView recyclerView;
    List<String> senderId = new ArrayList<>();
    List<List<String>> allMessages = new ArrayList<>();
    List<String> messages = new ArrayList<>();
    List<String> time = new ArrayList<>();
    List<String> leftRight = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_random_chat_noww);
        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Admin").child(Objects.requireNonNull(auth.getUid())).child("messages");
        recyclerView = findViewById(R.id.randomChatNowRecyclerView);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    senderId.clear();
                    messages.clear();
                    time.clear();
                    leftRight.clear();
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                        Log.i("info",dataSnapshot.toString() + "");
                        String currentId = dataSnapshot.getKey().toString();
                        senderId.add(String.valueOf(dataSnapshot.getKey()));
                        DatabaseReference getLastChat = FirebaseDatabase.getInstance().getReference().getRoot().child("Admin").child(auth.getUid()).child("messages").child(currentId);
                        getLastChat.limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists()){
                                    for(DataSnapshot dataSnapshot1 : snapshot.getChildren()){
                                        Log.i("offo",dataSnapshot1.toString());
                                        messages.add(String.valueOf(dataSnapshot1.child("message").getValue()));
                                        time.add(String.valueOf(dataSnapshot1.child("time").getValue()));
                                    }
                                    Log.i("offo",messages.toString());
                                    recyclerView.setLayoutManager(new LinearLayoutManager(RandomChatNoww.this));
                                    recyclerView.setAdapter(new RandomChatView(messages,time,senderId));
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}