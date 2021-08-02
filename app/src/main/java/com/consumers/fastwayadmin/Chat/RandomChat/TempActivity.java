package com.consumers.fastwayadmin.Chat.RandomChat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.consumers.fastwayadmin.Chat.chat;
import com.consumers.fastwayadmin.Chat.chatAdapter;
import com.consumers.fastwayadmin.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TempActivity extends AppCompatActivity {
    List<String> message = new ArrayList<>();
    List<String> id = new ArrayList<>();
    ProgressBar progressBar;
    EditText editText;
    FirebaseAuth auth;
    DatabaseReference reference;
    String userId;
    Button button;
    List<String> time = new ArrayList<>();
    LinearLayoutManager linearLayoutManager;
    RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp);
        message = getIntent().getStringArrayListExtra("message");
        editText = findViewById(R.id.tempAcitvityEdittext);
        button = findViewById(R.id.tempActivityButton);
        userId = getIntent().getStringExtra("userId");
        recyclerView = findViewById(R.id.tempRecyclerView);
        id = getIntent().getStringArrayListExtra("id");
        time = getIntent().getStringArrayListExtra("time");
        linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
//                    recyclerView.smoothScrollToPosition(message.size()-1);
        recyclerView.setAdapter(new chatAdapter(message,time,id));
        auth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference().getRoot().child("Admin").child(Objects.requireNonNull(auth.getUid())).child("messages").child(userId);
        button.setOnClickListener(v -> {
            if(editText.getText().toString().equals("")){
                Toast.makeText(TempActivity.this, "Field can't be empty", Toast.LENGTH_SHORT).show();
            }else{
                String currentTime = String.valueOf(System.currentTimeMillis());

                chat chat = new chat(editText.getText().toString(),auth.getUid(),currentTime,"1","BHANGY");
                reference.child(currentTime).setValue(chat);
                editText.setText("");
            }
        });

        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                updateChild();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                updateChild();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                updateChild();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                updateChild();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void updateChild() {
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                message.clear();
                id.clear();
                time.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    message.add(String.valueOf(dataSnapshot.child("message").getValue()));
                    time.add(String.valueOf(dataSnapshot.child("time").getValue()));
                    id.add(String.valueOf(dataSnapshot.child("id").getValue()));
                }
                linearLayoutManager = new LinearLayoutManager(getApplicationContext());
                linearLayoutManager.setStackFromEnd(true);
                recyclerView.setLayoutManager(linearLayoutManager);
//                    recyclerView.smoothScrollToPosition(message.size()-1);
                recyclerView.setAdapter(new chatAdapter(message,time,id));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}