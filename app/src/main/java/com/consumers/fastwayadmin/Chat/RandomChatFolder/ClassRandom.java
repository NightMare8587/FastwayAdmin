package com.consumers.fastwayadmin.Chat.RandomChatFolder;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.consumers.fastwayadmin.R;
import com.consumers.fastwayadmin.Tables.ChatWithCustomer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ClassRandom extends RecyclerView.Adapter<ClassRandom.Holder> {
    List<String> messages;
    List<String> userID;
    List<String> time;
    HashMap<String,String> map;
    List<String> leftOr;
    List<List<String>> finalMessage;
    List<List<String>> finalLeftOr;
    List<List<String>> finalTime;
    Context context;

    public ClassRandom(List<String> messages, List<String> userID, List<String> time, HashMap<String, String> map, List<String> leftOr, Context context,List<List<String>> finalMessage,List<List<String>> finalLeftOr,List<List<String>> finalTime) {
        this.messages = messages;
        this.finalMessage = finalMessage;
        this.finalLeftOr = finalLeftOr;
        this.finalTime = finalTime;
        this.userID = userID;
        this.time = time;
        this.map = map;
        this.leftOr = leftOr;
        this.context = context;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.random_chat_user_card,parent,false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        List<String> list = new ArrayList<>(finalMessage.get(position));
        holder.userName.setText(map.get(userID.get(position)));
        holder.lastMessage.setText(list.get(list.size() - 1));
        holder.cardView.setOnClickListener(click -> {
            Intent intent = new Intent(click.getContext(), ChatWithCustomer.class);

            intent.putExtra("id",userID.get(position));
            click.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return userID.size();
    }
    public class Holder extends RecyclerView.ViewHolder{
        TextView userName,lastMessage;
        CardView cardView;
        public Holder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.randomChatCardViewID);
            userName = itemView.findViewById(R.id.randomChatUserName);
            lastMessage = itemView.findViewById(R.id.latestMessageInChat);
        }
    }
}
