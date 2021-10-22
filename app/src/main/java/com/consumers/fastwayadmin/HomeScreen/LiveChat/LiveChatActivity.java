package com.consumers.fastwayadmin.HomeScreen.LiveChat;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
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
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import com.consumers.fastwayadmin.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class LiveChatActivity extends AppCompatActivity {
    RequestQueue requestQueue;
    String URL = "https://intercellular-stabi.000webhostapp.com/fastwayadminbot.php";
    RecyclerView recyclerView;
    List<String> message = new ArrayList<>();
    FirebaseAuth auth;
    DatabaseReference reference;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    LinearLayoutManager linearLayoutManager;
    List<String> time = new ArrayList<>();
    List<String> leftOrRight = new ArrayList<>();
    EditText editText;
    Button sendME;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_chat);
        initialise();
        linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        reference = FirebaseDatabase.getInstance().getReference().getRoot().child("Admin").child(Objects.requireNonNull(auth.getUid()));
        botReply();
        reference.child("Live Chat").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    message.clear();
                    time.clear();
                    leftOrRight.clear();

                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                        message.add(String.valueOf(dataSnapshot.child("message").getValue()));
                        time.add(String.valueOf(dataSnapshot.child("time").getValue()));
                        leftOrRight.add(String.valueOf(dataSnapshot.child("leftOr").getValue()));
                    }

                    recyclerView.setLayoutManager(linearLayoutManager);
                    recyclerView.setAdapter(new LiveAdapter(message,time,leftOrRight));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        sendME.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editText.getText().toString().equals("")){
                    Toast.makeText(LiveChatActivity.this, "Enter Some Text :)", Toast.LENGTH_SHORT).show();
                    return;
                }else{
                    String messa = editText.getText().toString();
                    String time = String.valueOf(System.currentTimeMillis());

                    liveChatClass liveChatClass = new liveChatClass(messa,time,"0");
                    reference.child("Live Chat").child(System.currentTimeMillis() + "").setValue(liveChatClass);
                    editText.setText("");

                    StringRequest jsonObjectRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try{
//                                Toast.makeText(LiveChatActivity.this, ""+response.toString(), Toast.LENGTH_SHORT).show();

                                liveChatClass liveChatClass = new liveChatClass(response.toString() + "","" + System.currentTimeMillis(),"1");

                                reference.child("Live Chat").child(System.currentTimeMillis() + "").setValue(liveChatClass);
                                if(response.equals("Wrong Input"))
                                    botReply();
                            }catch (Exception e){
                                Toast.makeText(LiveChatActivity.this, "" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(LiveChatActivity.this, "" + error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }){
                        @Nullable
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String,String> params = new HashMap<>();
                            params.put("response",messa);
                            return params;
                        }
                    };
                    jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                            5000,
                            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                    requestQueue.add(jsonObjectRequest);
                }
            }
        });

        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                updateChild();
            }

            @Override
            public void onChildChanged(@NonNull @NotNull DataSnapshot snapshot, @Nullable String previousChildName) {
                updateChild();
            }

            @Override
            public void onChildRemoved(@NonNull @NotNull DataSnapshot snapshot) {
                updateChild();
            }

            @Override
            public void onChildMoved(@NonNull @NotNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

    }

    private void botReply() {

        liveChatClass liveChatClass = new liveChatClass("Hi I am Fastway Bot",System.currentTimeMillis() + "","1");
        reference.child("Live Chat").child(System.currentTimeMillis() + "").setValue(liveChatClass);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                liveChatClass liveChatClass = new liveChatClass("Choose One Option\n1.Refund Status\n2.Other Options\n\n Enter number as input",System.currentTimeMillis() + "","1");
                reference.child("Live Chat").child(System.currentTimeMillis() + "").setValue(liveChatClass);
            }
        },500);
    }

    private void updateChild() {
        reference.child("Live Chat").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    message.clear();
                    time.clear();
                    leftOrRight.clear();

                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                        message.add(String.valueOf(dataSnapshot.child("message").getValue()));
                        time.add(String.valueOf(dataSnapshot.child("time").getValue()));
                        leftOrRight.add(String.valueOf(dataSnapshot.child("leftOr").getValue()));
                    }

                    recyclerView.setLayoutManager(linearLayoutManager);
                    recyclerView.setAdapter(new LiveAdapter(message,time,leftOrRight));
                }else{
                    message.clear();
                    time.clear();
                    leftOrRight.clear();
                    recyclerView.setLayoutManager(linearLayoutManager);
                    recyclerView.setAdapter(new LiveAdapter(message,time,leftOrRight));
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private void initialise() {
        requestQueue = Volley.newRequestQueue(this);
        sendME = findViewById(R.id.liveChatSendMessage);
        recyclerView = findViewById(R.id.liveChatRecyclerView);
        editText = findViewById(R.id.liveChatEditText);
        auth = FirebaseAuth.getInstance();
        sharedPreferences = getSharedPreferences("chatInfo",MODE_PRIVATE);
        editor = sharedPreferences.edit();


    }
}