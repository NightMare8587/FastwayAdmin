package com.consumers.fastwayadmin.Chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

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
    String currentName = "";
    List<String> name = new ArrayList<>();
    List<List<String>> allMesages = new ArrayList<>();
    List<List<String>> allusers = new ArrayList<>();

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
//                        update(String.valueOf(dataSnapshot.getKey()));

                        for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                            recentMessage.add(String.valueOf(dataSnapshot1.child("message").getValue()));
                            currentName = String.valueOf(dataSnapshot.child("name").getValue());
                        }
                        name.add(currentName);
                        Log.i("all",name.toString());
                        allMesages.add(new ArrayList<>(recentMessage));
                        allusers.add(new ArrayList<>(name));
                        recentMessage.clear();
                    }
                    Log.i("mess",allMesages.toString());
                    Log.i("all",allusers.toString());
                    recyclerView.setLayoutManager(new LinearLayoutManager(DisplayAllAvaialbleChats.this));
                    recyclerView.setAdapter(new DisplayAdapter(name,allMesages,DisplayAllAvaialbleChats.this,id));
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void update(String id) {
        getUserName = FirebaseDatabase.getInstance().getReference().getRoot().child("Users").child(id);
        getUserName.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    Log.i("namesAll",String.valueOf(snapshot.child("name").getValue()));
                    String names = snapshot.child("name").getValue(String.class);
                    Toast.makeText(DisplayAllAvaialbleChats.this, ""+names, Toast.LENGTH_SHORT).show();
                    name.add(names);
                    Log.i("current",name.toString());
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