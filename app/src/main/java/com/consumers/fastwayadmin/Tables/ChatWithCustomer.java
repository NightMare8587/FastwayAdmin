package com.consumers.fastwayadmin.Tables;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.itextpdf.text.pdf.parser.Line;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ChatWithCustomer extends AppCompatActivity {
    DatabaseReference reference;
    FirebaseAuth auth;
    String id;
    RecyclerView recyclerView;
    List<String> message = new ArrayList<>();
    List<String> time = new ArrayList<>();
    LinearLayoutManager linearLayoutManager;
    List<String> leftOrRight = new ArrayList<>();
    EditText editText;
    Button sendME;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_with_customer);
        auth = FirebaseAuth.getInstance();
        editText = findViewById(R.id.sendMessageEditText);
        sendME = findViewById(R.id.sendMessageButton);
        linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        reference = FirebaseDatabase.getInstance().getReference().getRoot().child("Admin").child(Objects.requireNonNull(auth.getUid()));
        recyclerView = findViewById(R.id.messageRecyclerView);
         id = getIntent().getStringExtra("id");

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        reference.child("messages").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    message.clear();
                    time.clear();
                    leftOrRight.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        message.add(String.valueOf(dataSnapshot.child("message").getValue()));
                        time.add(String.valueOf(dataSnapshot.child("time").getValue()));
                        leftOrRight.add(String.valueOf(dataSnapshot.child("id").getValue()));
                    }
                    linearLayoutManager.setStackFromEnd(true);
                    recyclerView.setLayoutManager(linearLayoutManager);
//                    recyclerView.smoothScrollToPosition(message.size()-1);
                    recyclerView.setAdapter(new chatAdapter(message,time,leftOrRight));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        reference.child("messages").child(id).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                updateChat();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                updateChat();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                updateChat();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                updateChat();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        sendME.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(editText.getText().toString().length() == 0){
                    Toast.makeText(ChatWithCustomer.this, "Enter Some Text", Toast.LENGTH_SHORT).show();
                    return;
                }else{
                    chat chat = new chat(editText.getText().toString(),auth.getUid()+"",System.currentTimeMillis()+"","1");
                  reference.child("messages").child(id).child(System.currentTimeMillis()+"").setValue(chat);
                  editText.setText("");
                  updateChat();
                }
            }
        });
    }

    private void updateChat() {
        reference.child("messages").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    message.clear();
                    time.clear();
                    leftOrRight.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        message.add(String.valueOf(dataSnapshot.child("message").getValue()));
                        time.add(String.valueOf(dataSnapshot.child("time").getValue()));
                        leftOrRight.add(String.valueOf(dataSnapshot.child("id").getValue()));
                    }
                    linearLayoutManager.setStackFromEnd(true);
                    recyclerView.setLayoutManager(linearLayoutManager);
//                    recyclerView.smoothScrollToPosition(message.size()-1);
                    recyclerView.setAdapter(new chatAdapter(message,time,leftOrRight));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}