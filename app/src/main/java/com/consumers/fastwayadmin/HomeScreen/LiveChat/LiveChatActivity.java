package com.consumers.fastwayadmin.HomeScreen.LiveChat;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
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

import org.json.JSONObject;

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
    DatabaseReference reference; boolean connectedWithFastway = false;
    SharedPreferences sharedPreferences;
    DatabaseReference liveTalkWithAdmin;
    SharedPreferences.Editor editor;
    String FURL  = "https://fcm.googleapis.com/fcm/send";
    SharedPreferences saveInfo;
    SharedPreferences.Editor saveEdit;
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
        saveInfo = getSharedPreferences("loginInfo",MODE_PRIVATE);
        saveEdit = saveInfo.edit();
        linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        liveTalkWithAdmin = FirebaseDatabase.getInstance().getReference().getRoot().child("Complaints").child("LiveChat");
        liveTalkWithAdmin.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    connectedWithFastway = true;
                    saveEdit.putString("liveChat","yes");
                    saveEdit.apply();
                }else {
                    saveEdit.remove("liveChat");
                    saveEdit.apply();
                    botReply();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        linearLayoutManager.setStackFromEnd(true);
        reference = FirebaseDatabase.getInstance().getReference().getRoot().child("Admin").child(Objects.requireNonNull(auth.getUid()));

        reference.child("Live Chat").limitToLast(15).addListenerForSingleValueEvent(new ValueEventListener() {
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
                }else{
                    String messa = editText.getText().toString();
                    if(connectedWithFastway){
                        String time = String.valueOf(System.currentTimeMillis());

                        liveChatClass liveChatClass = new liveChatClass(messa,time,"0");
                        reference.child("Live Chat").child(time + "").setValue(liveChatClass);
                        editText.setText("");
                        return;
                    }
                    if(messa.equals("2")){
                        modiFiedBotReply();
                        String time = String.valueOf(System.currentTimeMillis());

                        liveChatClass liveChatClass = new liveChatClass(messa,time,"0");
                        reference.child("Live Chat").child(System.currentTimeMillis() + "").setValue(liveChatClass);
                        editText.setText("");
                        return;
                    }

                    if(messa.equals("4")){
                        String time = String.valueOf(System.currentTimeMillis());

                        liveChatClass liveChatClass = new liveChatClass(messa,time,"0");
                        reference.child("Live Chat").child(System.currentTimeMillis() + "").setValue(liveChatClass);

                        new Handler().postDelayed(() -> {
                            String stime = String.valueOf(System.currentTimeMillis());
                            liveTalkWithAdmin.child("id").setValue(auth.getUid());
                            liveTalkWithAdmin.child("type").setValue("Admin");
                            liveChatClass liveChatClass1 = new liveChatClass("Connecting you with an agent. Avg wait time 2 min",stime,"1");
                            reference.child("Live Chat").child(System.currentTimeMillis() + "").setValue(liveChatClass1);
                        },500);
                        editText.setText("");
                        RequestQueue requestQueue = Volley.newRequestQueue(LiveChatActivity.this);
                        JSONObject main = new JSONObject();
                        try {
                            main.put("to", "/topics/" + "FastwayLiveChat");
                            JSONObject notification = new JSONObject();
                            notification.put("title", "Live Talk Request");
                            notification.put("body", "A user wants to talk right now... " + editText.getText().toString());
                            main.put("notification", notification);

                            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, FURL, main, response -> {

                            }, error -> {
                                Log.i("info",error.getLocalizedMessage() + " s");
                                Toast.makeText(getApplicationContext(), error.getLocalizedMessage() + "null", Toast.LENGTH_SHORT).show();
                            }) {
                                @Override
                                public Map<String, String> getHeaders() {
                                    Map<String, String> header = new HashMap<>();
                                    header.put("content-type", "application/json");
                                    header.put("authorization", "key=AAAAsigSEMs:APA91bEUF9ZFwIu84Jctci56DQd0TQOepztGOIKIBhoqf7N3ueQrkClw0xBTlWZEWyvwprXZmZgW2MNywF1pNBFpq1jFBr0CmlrJ0wygbZIBOnoZ0jP1zZC6nPxqF2MAP6iF3wuBHD2R");
                                    return header;
                                }
                            };

                            requestQueue.add(jsonObjectRequest);
                        } catch (Exception e) {
                            Log.i("info",e.getLocalizedMessage() + " s");
                            Toast.makeText(getApplicationContext(), e.getLocalizedMessage() + "null", Toast.LENGTH_SHORT).show();
                        }
                        return;
                    }

                    if(messa.equals("5")){
                        String time = String.valueOf(System.currentTimeMillis());

                        liveChatClass liveChatClass = new liveChatClass(messa,time,"0");
                        reference.child("Live Chat").child(System.currentTimeMillis() + "").setValue(liveChatClass);

                        new Handler().postDelayed(() -> {
                            String stime = String.valueOf(System.currentTimeMillis());
                            liveChatClass liveChatClass1 = new liveChatClass("Please Enter the number for callback\nNo need to add +91",stime,"1");
                            reference.child("Live Chat").child(System.currentTimeMillis() + "").setValue(liveChatClass1);
                        },500);

                        RequestQueue requestQueue = Volley.newRequestQueue(LiveChatActivity.this);
                        JSONObject main = new JSONObject();
                        try {
                            main.put("to", "/topics/" + "FastwayLiveChat");
                            JSONObject notification = new JSONObject();
                            notification.put("title", "CallBack Request");
                            notification.put("body", "Callback request from number " + editText.getText().toString());
                            main.put("notification", notification);

                            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, FURL, main, response -> {

                            }, error -> {
                                Log.i("info",error.getLocalizedMessage() + " s");
                                Toast.makeText(getApplicationContext(), error.getLocalizedMessage() + "null", Toast.LENGTH_SHORT).show();
                            }) {
                                @Override
                                public Map<String, String> getHeaders() {
                                    Map<String, String> header = new HashMap<>();
                                    header.put("content-type", "application/json");
                                    header.put("authorization", "key=AAAAsigSEMs:APA91bEUF9ZFwIu84Jctci56DQd0TQOepztGOIKIBhoqf7N3ueQrkClw0xBTlWZEWyvwprXZmZgW2MNywF1pNBFpq1jFBr0CmlrJ0wygbZIBOnoZ0jP1zZC6nPxqF2MAP6iF3wuBHD2R");
                                    return header;
                                }
                            };

                            requestQueue.add(jsonObjectRequest);
                        } catch (Exception e) {
                            Log.i("info",e.getLocalizedMessage() + " s");
                            Toast.makeText(getApplicationContext(), e.getLocalizedMessage() + "null", Toast.LENGTH_SHORT).show();
                        }
                        editText.setText("");
                        return;
                    }

                    if(messa.length() == 10){
                        String time = String.valueOf(System.currentTimeMillis());

                        liveChatClass liveChatClass = new liveChatClass(messa,time,"0");
                        reference.child("Live Chat").child(System.currentTimeMillis() + "").setValue(liveChatClass);

                        new Handler().postDelayed(() -> {
                            String stime = String.valueOf(System.currentTimeMillis());
                            liveChatClass liveChatClass1 = new liveChatClass("You will get callback from our fastway team\nAvg waiting time 2 minutes",stime,"1");
                            reference.child("Live Chat").child(System.currentTimeMillis() + "").setValue(liveChatClass1);
                        },500);

                        RequestQueue requestQueue = Volley.newRequestQueue(LiveChatActivity.this);
                        JSONObject main = new JSONObject();
                        try {
                            main.put("to", "/topics/" + "FastwayLiveChat");
                            JSONObject notification = new JSONObject();
                            notification.put("title", "CallBack Request");
                            notification.put("body", "Callback request from number " + editText.getText().toString());
                            main.put("notification", notification);

                            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, FURL, main, response -> {

                            }, error -> {
                                Log.i("info",error.getLocalizedMessage() + " s");
                                Toast.makeText(getApplicationContext(), error.getLocalizedMessage() + "null", Toast.LENGTH_SHORT).show();
                            }) {
                                @Override
                                public Map<String, String> getHeaders() {
                                    Map<String, String> header = new HashMap<>();
                                    header.put("content-type", "application/json");
                                    header.put("authorization", "key=AAAAsigSEMs:APA91bEUF9ZFwIu84Jctci56DQd0TQOepztGOIKIBhoqf7N3ueQrkClw0xBTlWZEWyvwprXZmZgW2MNywF1pNBFpq1jFBr0CmlrJ0wygbZIBOnoZ0jP1zZC6nPxqF2MAP6iF3wuBHD2R");
                                    return header;
                                }
                            };

                            requestQueue.add(jsonObjectRequest);
                        } catch (Exception e) {
                            Log.i("info",e.getLocalizedMessage() + " s");
                            Toast.makeText(getApplicationContext(), e.getLocalizedMessage() + "null", Toast.LENGTH_SHORT).show();
                        }
                        editText.setText("");
                        return;
                    }

                    String time = String.valueOf(System.currentTimeMillis());

                    liveChatClass liveChatClass = new liveChatClass(messa,time,"0");
                    reference.child("Live Chat").child(System.currentTimeMillis() + "").setValue(liveChatClass);
                    editText.setText("");

                    StringRequest jsonObjectRequest = new StringRequest(Request.Method.POST, URL, response -> {
                        try{
//                                Toast.makeText(LiveChatActivity.this, ""+response.toString(), Toast.LENGTH_SHORT).show();

                            liveChatClass liveChatClass12 = new liveChatClass(response + "","" + System.currentTimeMillis(),"1");

                            reference.child("Live Chat").child(System.currentTimeMillis() + "").setValue(liveChatClass12);
                            if(response.equals("Wrong Input"))
                                botReply();
                        }catch (Exception e){
                            Toast.makeText(LiveChatActivity.this, "" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }, error -> Toast.makeText(LiveChatActivity.this, "" + error.getLocalizedMessage(), Toast.LENGTH_SHORT).show()){
                        @NonNull
                        @Override
                        protected Map<String, String> getParams() {
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

        new Handler().postDelayed(() -> {
            liveChatClass liveChatClass1 = new liveChatClass("Choose One Option\n1.Refund Status\n2.Other Options\n\n Enter number as input",System.currentTimeMillis() + "","1");
            reference.child("Live Chat").child(System.currentTimeMillis() + "").setValue(liveChatClass1);
        },500);
    }

    private void updateChild() {
        reference.child("Live Chat").limitToLast(15).addListenerForSingleValueEvent(new ValueEventListener() {
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

                    if(message.get(message.size() - 1).equals("Connected with Fastway Agent")) {
                        connectedWithFastway = true;
                        saveEdit.putString("liveChat","yes");
                        saveEdit.apply();
                    }else if(message.get(message.size()-1).equals("Fastway Agent has left the chat")) {
                        connectedWithFastway = false;
                        saveEdit.remove("liveChat");
                        saveEdit.apply();
                        botReply();
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
    private void modiFiedBotReply() {
        new Handler().postDelayed(() -> {
            liveChatClass liveChatClass = new liveChatClass("Choose One Option\n3.Fastway Website\n4.Live Chat With Customer Support\n5.Get A Call Back from Fastway\n\n Enter number as input",System.currentTimeMillis() + "","1");
            reference.child("Live Chat").child(System.currentTimeMillis() + "").setValue(liveChatClass);
        },500);
    }
}