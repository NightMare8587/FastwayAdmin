package com.consumers.fastwayadmin.Chat.RandomChat;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.consumers.fastwayadmin.R;

import java.util.ArrayList;
import java.util.List;

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
