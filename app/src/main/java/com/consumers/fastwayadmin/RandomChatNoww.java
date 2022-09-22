package com.consumers.fastwayadmin;

import android.content.SharedPreferences;
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
import com.developer.kalert.KAlertDialog;
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

public class RandomChatNoww extends AppCompatActivity {
    FirebaseAuth auth;
    DatabaseReference databaseReference;
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    List<String> messages = new ArrayList<>();
    boolean containsBad = false;
    List<String> badWords = new ArrayList<>();
    int count = 1;
    List<String> time = new ArrayList<>();
    List<String> leftOr = new ArrayList<>();
    Button sendME;
    EditText editText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_random_chat_noww);
        auth = FirebaseAuth.getInstance();
        linearLayoutManager = new LinearLayoutManager(RandomChatNoww.this);
        databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Complaints").child("messages").child("Admin").child(Objects.requireNonNull(auth.getUid()));
        recyclerView = findViewById(R.id.randomMessageRecyclerView);
        sendME = findViewById(R.id.sendMessageButtonRandom);
        editText = findViewById(R.id.sendMessageEditTextRandom);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    messages.clear();
                    time.clear();
                    leftOr.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            messages.add(String.valueOf(dataSnapshot.child("message").getValue()));
                            time.add(String.valueOf(dataSnapshot.child("time").getValue()));
                            leftOr.add(String.valueOf(dataSnapshot.child("id").getValue()));
                        }
                        linearLayoutManager.setStackFromEnd(true);
                        recyclerView.setLayoutManager(linearLayoutManager);
//                    recyclerView.smoothScrollToPosition(message.size()-1);
                        recyclerView.setAdapter(new RandomChatAdapter(messages,time,leftOr));
                }else{
                    count++;
                    KAlertDialog kAlertDialog = new KAlertDialog(RandomChatNoww.this,KAlertDialog.ERROR_TYPE);
                    kAlertDialog.setTitleText("No Message From Ordinalo")
                            .setContentText("We will notify you as soon as you got reply from Ordinalo")
                            .setConfirmText("Exit")
                            .setConfirmClickListener(click -> {
                                click.dismissWithAnimation();
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

        sendME.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(editText.getText().toString().length() == 0){
                    Toast.makeText(RandomChatNoww.this, "Enter Some Text", Toast.LENGTH_SHORT).show();
                    return;
                }else{
                    containsBad = false;
                    String text = editText.getText().toString().trim().toLowerCase();
                    for (int i = 0; i < badWords.size(); i++) {
                        if (text.contains(badWords.get(i).toLowerCase()))
                            containsBad = true;
                    }

                    if(!containsBad) {
                        SharedPreferences preferences = getSharedPreferences("AccountInfo", MODE_PRIVATE);
                        chat chat = new chat(editText.getText().toString().trim(), auth.getUid() + "", System.currentTimeMillis() + "", "0", preferences.getString("name", ""),"message");
                        databaseReference.child(System.currentTimeMillis() + "").setValue(chat);
                        editText.setText("");
                        updateChat();
                    }
                    else
                        Toast.makeText(RandomChatNoww.this, "We don't allow to use bad words in our app", Toast.LENGTH_SHORT).show();
                }


            }
        });
        databaseReference.addChildEventListener(new ChildEventListener() {
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
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void updateChat() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    messages.clear();
                    time.clear();
                    leftOr.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        messages.add(String.valueOf(dataSnapshot.child("message").getValue()));
                        time.add(String.valueOf(dataSnapshot.child("time").getValue()));
                        leftOr.add(String.valueOf(dataSnapshot.child("id").getValue()));
                    }
                    linearLayoutManager.setStackFromEnd(true);
                    recyclerView.setLayoutManager(linearLayoutManager);
//                    recyclerView.smoothScrollToPosition(message.size()-1);
                    recyclerView.setAdapter(new RandomChatAdapter(messages,time,leftOr));
                }else if(count == 1){
                    count++;
                    KAlertDialog kAlertDialog = new KAlertDialog(RandomChatNoww.this,KAlertDialog.ERROR_TYPE);
                    kAlertDialog.setTitleText("No Message From Fastway")
                            .setContentText("We will notify you as soon as you got reply from fastway")
                            .setConfirmText("Exit")
                            .setConfirmClickListener(click -> {
                                click.dismissWithAnimation();
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
    }
    private void addBadWords() {
        badWords.add("chutiya");
        badWords.add("gandu");
        badWords.add("lodu");
        badWords.add("kutta");
        badWords.add("kutti");
        badWords.add("saale");
        badWords.add("madarchod");
        badWords.add("bc");
        badWords.add("mc");
        badWords.add("bkl");
        badWords.add("randi");
        badWords.add("fuck");
        badWords.add("bitch");
        badWords.add("asshole");
        badWords.add("choda");
    }
}