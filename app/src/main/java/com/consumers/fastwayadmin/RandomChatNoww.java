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
        databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Complaints").child("Admin").child(Objects.requireNonNull(auth.getUid())).child("messages").child(auth.getUid());
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
                        recyclerView.setAdapter(new chatAdapter(messages,time,leftOr));
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
//                    RequestQueue requestQueue = Volley.newRequestQueue(RandomChatNoww.this);
//                    JSONObject main = new JSONObject();
//                    try{
//                        main.put("to","/topics/"++"");
//                        JSONObject notification = new JSONObject();
//                        notification.put("title","Restaurant Owner");
//                        notification.put("body",""+editText.getText().toString().trim());
//                        main.put("notification",notification);
//
//                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, main, new Response.Listener<JSONObject>() {
//                            @Override
//                            public void onResponse(JSONObject response) {
//
//                            }
//                        }, new Response.ErrorListener() {
//                            @Override
//                            public void onErrorResponse(VolleyError error) {
//                                Toast.makeText(getApplicationContext(), error.getLocalizedMessage()+"null", Toast.LENGTH_SHORT).show();
//                            }
//                        }){
//                            @Override
//                            public Map<String, String> getHeaders() throws AuthFailureError {
//                                Map<String,String> header = new HashMap<>();
//                                header.put("content-type","application/json");
//                                header.put("authorization","key=AAAAsigSEMs:APA91bEUF9ZFwIu84Jctci56DQd0TQOepztGOIKIBhoqf7N3ueQrkClw0xBTlWZEWyvwprXZmZgW2MNywF1pNBFpq1jFBr0CmlrJ0wygbZIBOnoZ0jP1zZC6nPxqF2MAP6iF3wuBHD2R");
//                                return header;
//                            }
//                        };
//
//                        requestQueue.add(jsonObjectRequest);
//                    }
//                    catch (Exception e){
//                        Toast.makeText(getApplicationContext(), e.getLocalizedMessage()+"null", Toast.LENGTH_SHORT).show();
//                    }
                    SharedPreferences preferences = getSharedPreferences("AccountInfo",MODE_PRIVATE);
                    chat chat = new chat(editText.getText().toString().trim(),auth.getUid()+"",System.currentTimeMillis()+"","1",preferences.getString("name",""));
                    databaseReference.child(System.currentTimeMillis()+"").setValue(chat);
                    editText.setText("");

                    updateChat();
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
                    recyclerView.setAdapter(new chatAdapter(messages,time,leftOr));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}