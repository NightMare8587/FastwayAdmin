package com.consumers.fastwayadmin.Tables;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.consumers.fastwayadmin.Chat.chat;
import com.consumers.fastwayadmin.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ChatWithCustomer extends AppCompatActivity {
    DatabaseReference reference;
    FirebaseAuth auth;
    RecyclerView recyclerView;
    EditText editText;
    Button sendME;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_with_customer);
        auth = FirebaseAuth.getInstance();
        List<String> messages = new ArrayList<>();
        List<String> time = new ArrayList<>();
        editText = findViewById(R.id.sendMessageEditText);
        sendME = findViewById(R.id.sendMessageButton);
        reference = FirebaseDatabase.getInstance().getReference().getRoot().child("Admin").child(Objects.requireNonNull(auth.getUid()));
        recyclerView = findViewById(R.id.messageRecyclerView);
        String id = getIntent().getStringExtra("id");

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        reference.child("messages").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){

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
                    Toast.makeText(ChatWithCustomer.this, "Enter Some Text", Toast.LENGTH_SHORT).show();
                    return;
                }else{
                    chat chat = new chat(editText.getText().toString(),auth.getUid()+"",System.currentTimeMillis()+"");
                  reference.child("messages").child(System.currentTimeMillis()+"").setValue(chat);
                }
            }
        });
    }

}