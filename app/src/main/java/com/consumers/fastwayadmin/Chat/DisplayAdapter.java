package com.consumers.fastwayadmin.Chat;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.consumers.fastwayadmin.R;

import java.util.ArrayList;
import java.util.List;

public class DisplayAdapter extends RecyclerView.Adapter<DisplayAdapter.Holder> {
    List<String> chatName = new ArrayList<>();
    List<List<String>> recentMessage = new ArrayList<>();
    Context context;

    public DisplayAdapter(List<String> chatName,List<List<String>> recentMessage,Context context){
        this.recentMessage = recentMessage;
        this.chatName = chatName;
        this.context = context;
    }
    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.display_chat_layout,parent,false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        List<String> messages = new ArrayList<>(recentMessage.get(position));
        Log.i("myMess",messages.toString());
        holder.name.setText(chatName.get(position));
        holder.message.setText(messages.get(messages.size()-1));
    }

    @Override
    public int getItemCount() {
        Toast.makeText(context, ""+chatName.size(), Toast.LENGTH_SHORT).show();
        return chatName.size();
    }

    public static class Holder extends RecyclerView.ViewHolder{
        TextView name,message;
        public Holder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.NameOfChat);
            message = itemView.findViewById(R.id.recentMessageChat);
        }
    }
}
