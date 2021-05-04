package com.consumers.fastwayadmin.Chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.consumers.fastwayadmin.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DisplayAllAvaialbleChats extends AppCompatActivity {
    FirebaseAuth auth;
    List<String> id = new ArrayList<>();
    DatabaseReference getUserName;
    List<String> name = new ArrayList<>();
    List<List<String>> allMesages = new ArrayList<>();

    List<String> recentMessage = new ArrayList<>();
    DatabaseReference reference;
    RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_all_avaialble_chats);
        initialise();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    id.clear();
                    name.clear();
                    recentMessage.clear();

                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                        id.add(String.valueOf(dataSnapshot.getKey()));
                        getUserName = FirebaseDatabase.getInstance().getReference().getRoot().child("Users").child(String.valueOf(dataSnapshot.getKey()));
                        getUserName.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists()){
                                    Log.i("namesAll",String.valueOf(snapshot.child("name").getValue()));
                                    name.add(String.valueOf(snapshot.child("name").getValue()));
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                            recentMessage.add(String.valueOf(dataSnapshot1.child("message").getValue()));
                        }
                        allMesages.add(new ArrayList<>(recentMessage));
                        recentMessage.clear();
                    }
                    Log.i("mess",allMesages.toString());
                    Log.i("all",name.toString());
                    recyclerView.setLayoutManager(new LinearLayoutManager(DisplayAllAvaialbleChats.this));
                    recyclerView.setAdapter(new DisplayAdapter(name,allMesages,DisplayAllAvaialbleChats.this));
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void initialise() {
        auth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference().getRoot().child("Admin").child(auth.getUid()).child("messages");
        recyclerView = findViewById(R.id.DisplayChatRecyclerView);
    }
}