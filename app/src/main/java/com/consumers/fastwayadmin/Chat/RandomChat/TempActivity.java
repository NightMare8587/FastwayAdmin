package com.consumers.fastwayadmin.Chat.RandomChat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

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
import com.consumers.fastwayadmin.Tables.ChatWithCustomer;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class TempActivity extends AppCompatActivity {
    List<String> message = new ArrayList<>();
    List<String> id = new ArrayList<>();
    String URL = "https://fcm.googleapis.com/fcm/send";
    String restaurantName;
    DatabaseReference resName;
    EditText editText;
    FirebaseAuth auth;
    SharedPreferences sharedPreferences;
    DatabaseReference reference;
    String userId;
    Button button;
    List<String> time = new ArrayList<>();
    List<String> typeOfMessage = new ArrayList<>();
    LinearLayoutManager linearLayoutManager;
    RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp);
        message = getIntent().getStringArrayListExtra("message");
        editText = findViewById(R.id.tempAcitvityEdittext);
        sharedPreferences = getSharedPreferences("loginInfo",MODE_PRIVATE);
        button = findViewById(R.id.tempActivityButton);
        userId = getIntent().getStringExtra("userId");
        recyclerView = findViewById(R.id.tempRecyclerView);
        id = getIntent().getStringArrayListExtra("id");
        time = getIntent().getStringArrayListExtra("time");
        linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
//                    recyclerView.smoothScrollToPosition(message.size()-1);
        recyclerView.setAdapter(new chatAdapter(message,time,id,typeOfMessage));
        auth = FirebaseAuth.getInstance();
        resName = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(sharedPreferences.getString("state","")).child(sharedPreferences.getString("locality","")).child(auth.getUid());
        resName.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull  DataSnapshot snapshot) {
                if(snapshot.exists())
                    restaurantName = String.valueOf(snapshot.child("name").getValue());
            }

            @Override
            public void onCancelled(@NonNull  DatabaseError error) {

            }
        });
        reference = FirebaseDatabase.getInstance().getReference().getRoot().child("Admin").child(Objects.requireNonNull(auth.getUid())).child("messages").child(userId);
        button.setOnClickListener(v -> {
            if(editText.getText().toString().equals("")){
                Toast.makeText(TempActivity.this, "Field can't be empty", Toast.LENGTH_SHORT).show();
            }else{
                String currentTime = String.valueOf(System.currentTimeMillis());
                RequestQueue requestQueue = Volley.newRequestQueue(TempActivity.this);
                JSONObject main = new JSONObject();
                try{
                    main.put("to","/topics/"+userId+"");
                    JSONObject notification = new JSONObject();
                    notification.put("title","Restaurant Owner" + "(" + restaurantName + ")");
                    notification.put("body",""+editText.getText().toString().trim());
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
                chat chat = new chat(editText.getText().toString(),auth.getUid(),currentTime,"1","BHANGY","message");
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
                typeOfMessage.clear();
                time.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    message.add(String.valueOf(dataSnapshot.child("message").getValue()));
                    time.add(String.valueOf(dataSnapshot.child("time").getValue()));
                    typeOfMessage.add(String.valueOf(dataSnapshot.child("typeOfMessage").getValue()));
                    id.add(String.valueOf(dataSnapshot.child("id").getValue()));
                }
                linearLayoutManager = new LinearLayoutManager(getApplicationContext());
                linearLayoutManager.setStackFromEnd(true);
                recyclerView.setLayoutManager(linearLayoutManager);
//                    recyclerView.smoothScrollToPosition(message.size()-1);
                recyclerView.setAdapter(new chatAdapter(message,time,id,typeOfMessage));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}