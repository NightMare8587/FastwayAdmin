package com.consumers.fastwayadmin.Chat.RandomChat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.consumers.fastwayadmin.Chat.chatAdapter;
import com.consumers.fastwayadmin.R;

import java.util.ArrayList;
import java.util.List;

public class TempActivity extends AppCompatActivity {
    List<String> message = new ArrayList<>();
    List<String> id = new ArrayList<>();
    ProgressBar progressBar;
    EditText editText;
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
        recyclerView = findViewById(R.id.tempRecyclerView);
        id = getIntent().getStringArrayListExtra("id");
        time = getIntent().getStringArrayListExtra("time");
        linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
//                    recyclerView.smoothScrollToPosition(message.size()-1);
        recyclerView.setAdapter(new chatAdapter(message,time,id));

    }
}