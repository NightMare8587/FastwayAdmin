package com.consumers.fastwayadmin.HomeScreen.LiveChat;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.consumers.fastwayadmin.HomeScreen.HomeScreen;
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

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class LiveChatActivity extends AppCompatActivity {
    RequestQueue requestQueue;
    String timeOfJoining;
    String URL = "https://intercellular-stabi.000webhostapp.com/fastwayadminbot.php";
    RecyclerView recyclerView;
    boolean talkWithAgent = false;
    List<String> message = new ArrayList<>();
    FirebaseAuth auth;
    DatabaseReference reference; boolean connectedWithFastway = false;
    SharedPreferences sharedPreferences;
    DatabaseReference liveTalkWithAdmin;
    DatabaseReference callBackFromAdmin;
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
        linearLayoutManager.setStackFromEnd(true);
        deleteFirstFifty();
        reference = FirebaseDatabase.getInstance().getReference().getRoot().child("Admin").child(Objects.requireNonNull(auth.getUid()));
        botReply();
        liveTalkWithAdmin = FirebaseDatabase.getInstance().getReference().getRoot().child("Complaints").child("LiveChat").child(saveInfo.getString("state",""));
        callBackFromAdmin = FirebaseDatabase.getInstance().getReference().getRoot().child("Complaints").child("CallBack").child(saveInfo.getString("state",""));
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


        sendME.setOnClickListener(v -> {
            if(editText.getText().toString().equals("")){
                Toast.makeText(LiveChatActivity.this, "Enter Some Text :)", Toast.LENGTH_SHORT).show();
            }else{
                String messa = editText.getText().toString().trim().toLowerCase();
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
                        timeOfJoining = stime;
                        talkWithAgent = true;
                        liveTalkWithAdmin.child(stime).child("id").setValue(auth.getUid());
                        SharedPreferences sharedPreferences = getSharedPreferences("RestaurantInfo",MODE_PRIVATE);
                        liveTalkWithAdmin.child(stime).child("name").setValue(sharedPreferences.getString("hotelName",""));
                        liveTalkWithAdmin.child(stime).child("type").setValue("Admin");
                        liveChatClass liveChatClass1 = new liveChatClass("Connecting you with an agent. Avg waiting time 2 min",stime,"1");
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
                                header.put("authorization", "key=AAAAjq_WsHs:APA91bGZV-uH-NJddxIniy8h1tDGDHqxhgvFdyNRDaV_raxjvSM_FkKu7JSwtSp4Q_iSmPuTKGGIB2M_07c9rKgPXUH43-RzpK6zkaSaIaNgmeiwUO40rYxYUZAkKoLAQQVeVJ7mXboD");
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

                    editText.setText("");
                    return;
                }

                if(messa.equals("1")){
                    SharedPreferences sharedPreferences = getSharedPreferences("AdminPremiumDetails",MODE_PRIVATE);
                    String time = String.valueOf(System.currentTimeMillis());

                    liveChatClass liveChatClass = new liveChatClass(messa,time,"0");
                    reference.child("Live Chat").child(System.currentTimeMillis() + "").setValue(liveChatClass);

                    new Handler().postDelayed(() -> {
                        if(sharedPreferences.contains("status") && sharedPreferences.getString("status","").equals("active")) {
                            String stime = String.valueOf(System.currentTimeMillis());
                            liveChatClass liveChatClass1 = new liveChatClass("Opening", stime, "1");
                            reference.child("Live Chat").child(System.currentTimeMillis() + "").setValue(liveChatClass1);
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            File file = new File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS) + "/RestaurantEarningTracker.xlsx");
                            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            intent.setDataAndType(FileProvider.getUriForFile(LiveChatActivity.this, getPackageName() + ".provider",file), "application/vnd.ms-excel");
//            intent.setDataAndType(Uri.parse(file.getPath().toString()), "application/vnd.ms-excel");
                            startActivity(intent);
                        }else{
                            String stime = String.valueOf(System.currentTimeMillis());
                            liveChatClass liveChatClass1 = new liveChatClass("You need to subscribe premium", stime, "1");
                            reference.child("Live Chat").child(System.currentTimeMillis() + "").setValue(liveChatClass1);
                        }

                    },250);
                    return;
                }

                if(messa.equals("7")){
                    editText.setText("");
                    String time = String.valueOf(System.currentTimeMillis());

                    liveChatClass liveChatClass = new liveChatClass(messa,time,"0");
                    reference.child("Live Chat").child(System.currentTimeMillis() + "").setValue(liveChatClass);

                    AlertDialog.Builder builder = new AlertDialog.Builder(LiveChatActivity.this);
                    builder.setTitle("Choose one").setMessage("Which file you wanna open?\nTouch outside to close this dialog")
                            .setPositiveButton("Open Monthly", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(Intent.ACTION_VIEW);
                                    File file12 = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) + "/MonthlyReportTracker.pdf");
                                    if(file12.exists()) {
                                        Toast.makeText(LiveChatActivity.this, "Opening...", Toast.LENGTH_SHORT).show();
                                        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                        intent.setDataAndType(FileProvider.getUriForFile(LiveChatActivity.this, getPackageName() + ".provider", file12), "application/pdf");
                                        startActivity(intent);
                                    }

                                }
                            }).setNegativeButton("Open Weekly", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(Intent.ACTION_VIEW);
                                    File file12 = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) + "/WeeklyReportTracker.pdf");
                                    if(file12.exists()) {
                                        Toast.makeText(LiveChatActivity.this, "Opening...", Toast.LENGTH_SHORT).show();
                                        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                        intent.setDataAndType(FileProvider.getUriForFile(LiveChatActivity.this, getPackageName() + ".provider", file12), "application/pdf");
                                        startActivity(intent);
                                    }

                                }
                            }).setNeutralButton("Open Daily", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(Intent.ACTION_VIEW);
                                    File file12 = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) + "/DailyReportInsights.pdf");
                                    if(file12.exists()) {
                                        Toast.makeText(LiveChatActivity.this, "Opening...", Toast.LENGTH_SHORT).show();
                                        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                        intent.setDataAndType(FileProvider.getUriForFile(LiveChatActivity.this, getPackageName() + ".provider", file12), "application/pdf");
                                        startActivity(intent);
                                    }

                                }
                            }).create();
                    builder.show();

                    return;
                }

                if(messa.length() == 10 && TextUtils.isDigitsOnly(messa)){
                    String time = String.valueOf(System.currentTimeMillis());

                    liveChatClass liveChatClass = new liveChatClass(messa,time,"0");
                    reference.child("Live Chat").child(System.currentTimeMillis() + "").setValue(liveChatClass);

                    new Handler().postDelayed(() -> {
                        SharedPreferences sharedPreferences = getSharedPreferences("RestaurantInfo",MODE_PRIVATE);
                        String stime = String.valueOf(System.currentTimeMillis());
                        callBackFromAdmin.child(stime).child("id").setValue(auth.getUid());
                        callBackFromAdmin.child(stime).child("number").setValue(messa);
                        callBackFromAdmin.child(stime).child("name").setValue(sharedPreferences.getString("hotelName",""));
                        callBackFromAdmin.child(stime).child("type").setValue("Admin");
                        liveChatClass liveChatClass1 = new liveChatClass("You will get callback from our Ordinalo team\nAvg waiting time 2 minutes",stime,"1");
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
                                header.put("authorization", "key=AAAAjq_WsHs:APA91bGZV-uH-NJddxIniy8h1tDGDHqxhgvFdyNRDaV_raxjvSM_FkKu7JSwtSp4Q_iSmPuTKGGIB2M_07c9rKgPXUH43-RzpK6zkaSaIaNgmeiwUO40rYxYUZAkKoLAQQVeVJ7mXboD");
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

    private void deleteFirstFifty() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Admin").child(Objects.requireNonNull(auth.getUid()));
        databaseReference.child("Live Chat").removeValue();
    }

    private void botReply() {

        liveChatClass liveChatClass = new liveChatClass("Hi I am Ordinalo Bot",System.currentTimeMillis() + "","1");
        reference.child("Live Chat").child(System.currentTimeMillis() + "").setValue(liveChatClass);

        new Handler().postDelayed(() -> {
            liveChatClass liveChatClass1 = new liveChatClass("Choose One Option\n1.View Excel Sheet\n2.Other Options\n7.View Reports\n\n Enter number as input",System.currentTimeMillis() + "","1");
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

                    if(message.get(message.size() - 1).equals("Connected with Ordinalo Agent")) {
                        connectedWithFastway = true;
                        saveEdit.putString("liveChat","yes");
                        saveEdit.apply();
                    }else if(message.get(message.size()-1).equals("Ordinalo Agent has left the chat")) {
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
            liveChatClass liveChatClass = new liveChatClass("Choose One Option\n3.Ordinalo Website\n4.Live Chat With Customer Support\n5.Get A Call Back from Ordinalo\n\n Enter number as input",System.currentTimeMillis() + "","1");
            reference.child("Live Chat").child(System.currentTimeMillis() + "").setValue(liveChatClass);
        },500);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(talkWithAgent)
        liveTalkWithAdmin.child(timeOfJoining).removeValue();
    }
}