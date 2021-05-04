package com.consumers.fastwayadmin.Tables;

import android.app.Activity;
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

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
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

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ChatWithCustomer extends AppCompatActivity {
    DatabaseReference reference;
    FirebaseAuth auth;
    String id;
    String URL = "https://fcm.googleapis.com/fcm/send";
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
                    RequestQueue requestQueue = Volley.newRequestQueue(ChatWithCustomer.this);
                    JSONObject main = new JSONObject();
                    try{
                        main.put("to","/topics/"+id+"");
                        JSONObject notification = new JSONObject();
                        notification.put("title","Restaurant Owner");
                        notification.put("body",""+editText.getText().toString().trim());
                        notification.put("click_action","Chat");
                        main.put("notification",notification);

                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, main, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {

                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(getApplicationContext(), error.getLocalizedMessage()+"null", Toast.LENGTH_SHORT).show();
                            }
                        }){
                            @Override
                            public Map<String, String> getHeaders() throws AuthFailureError {
                                Map<String,String> header = new HashMap<>();
                                header.put("content-type","application/json");
                                header.put("authorization","key=AAAAsigSEMs:APA91bEUF9ZFwIu84Jctci56DQd0TQOepztGOIKIBhoqf7N3ueQrkClw0xBTlWZEWyvwprXZmZgW2MNywF1pNBFpq1jFBr0CmlrJ0wygbZIBOnoZ0jP1zZC6nPxqF2MAP6iF3wuBHD2R");
                                return header;
                            }
                        };

                        requestQueue.add(jsonObjectRequest);
                    }
                    catch (Exception e){
                        Toast.makeText(getApplicationContext(), e.getLocalizedMessage()+"null", Toast.LENGTH_SHORT).show();
                    }
                    SharedPreferences sharedPreferences = getSharedPreferences("loginInfo",MODE_PRIVATE);
                    chat chat = new chat(editText.getText().toString(),auth.getUid()+"",System.currentTimeMillis()+"","1",sharedPreferences.getString("name",""));
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