package com.consumers.fastwayadmin.Chat.RandomChat;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.consumers.fastwayadmin.R;
import com.consumers.fastwayadmin.Tables.ChatWithCustomer;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RandomChatView extends RecyclerView.Adapter<RandomChatView.RecyclerViewHolder> {
    List<String> message = new ArrayList<>();
    List<String> time = new ArrayList<>();
    List<String> id = new ArrayList<>();

    public RandomChatView(List<String> message, List<String> time, List<String> id) {
        this.message = message;
        this.time = time;
        this.id = id;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.random_chat_layout,parent,false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
        holder.meesages.setText(message.get(position));
        holder.cardView.setOnClickListener(v -> {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            DatabaseReference getChat = FirebaseDatabase.getInstance().getReference().getRoot().child("Admin").child(Objects.requireNonNull(auth.getUid())).child("messages").child(id.get(position));
            getChat.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                   List<String> messages = new ArrayList<>();
                   List<String> timing = new ArrayList<>();
                   List<String> leftRight = new ArrayList<>();
                   for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                       messages.add(String.valueOf(dataSnapshot.child("message").getValue()));
                       timing.add(String.valueOf(dataSnapshot.child("time").getValue()));
                       leftRight.add(String.valueOf(dataSnapshot.child("id").getValue()));
                   }
                    Intent intent = new Intent(v.getContext(), TempActivity.class);
                   intent.putStringArrayListExtra("message", (ArrayList<String>) messages);
                    intent.putStringArrayListExtra("time", (ArrayList<String>) timing);
                    intent.putStringArrayListExtra("id", (ArrayList<String>) leftRight);
                    v.getContext().startActivity(intent);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        });
    }

    @Override
    public int getItemCount() {
        return message.size();
    }
    public static class RecyclerViewHolder extends RecyclerView.ViewHolder{
        TextView meesages;
        CardView cardView;
        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            meesages = itemView.findViewById(R.id.recentMessageTextRandomChat);
            cardView = itemView.findViewById(R.id.randomChatCardID);
        }
    }
}
