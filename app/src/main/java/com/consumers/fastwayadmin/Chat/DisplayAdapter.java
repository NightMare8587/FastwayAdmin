package com.consumers.fastwayadmin.Chat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.consumers.fastwayadmin.R;

import java.util.ArrayList;
import java.util.List;

public class DisplayAdapter extends RecyclerView.Adapter<DisplayAdapter.Holder> {
    List<String> chatName = new ArrayList<>();
    List<String> recentMessage = new ArrayList<>();

    public DisplayAdapter(List<String> chatName,List<String> recentMessage){
        this.recentMessage = recentMessage;
        this.chatName = chatName;
    }
    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.display_chat_layout,null);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.name.setText(chatName.get(position));
        holder.message.setText(recentMessage.get(position));
    }

    @Override
    public int getItemCount() {
        return chatName.size();
    }

    public static class Holder extends RecyclerView.ViewHolder{
        TextView name,message;
        public Holder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.chatName);
            message = itemView.findViewById(R.id.recentMessage);
        }
    }
}
