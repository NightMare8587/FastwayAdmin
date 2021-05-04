package com.consumers.fastwayadmin.Chat;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

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

public class DisplayAdapter extends RecyclerView.Adapter<DisplayAdapter.Holder> {
    List<String> chatName = new ArrayList<>();
    List<String> id = new ArrayList<>();
    FirebaseAuth auth;
    DatabaseReference databaseReference;
    List<List<String>> recentMessage = new ArrayList<>();
    Context context;

    public DisplayAdapter(List<String> chatName,List<List<String>> recentMessage,Context context,List<String> id){
        this.recentMessage = recentMessage;
        this.id = id;
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
        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Admin").child(Objects.requireNonNull(auth.getUid())).child("messages");
        databaseReference.child(id.get(position)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    if(dataSnapshot.hasChild("senderId"))
//                        Toast.makeText(context, "Haaaaaaaaaaaaaaaaaaaaala", Toast.LENGTH_SHORT).show();
                    if(dataSnapshot.child("senderId").getValue(String.class).equals(id.get(position))) {
                        holder.name.setText(String.valueOf(dataSnapshot.child("name").getValue()));
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), ChatWithCustomer.class);
                intent.putExtra("id",id.get(position));
                view.getContext().startActivity(intent);
            }
        });
        List<String> messages = new ArrayList<>(recentMessage.get(position));
        Log.i("myMess",messages.toString()); holder.name.setText(chatName.get(position));
        holder.message.setText(messages.get(messages.size()-1));
    }

    @Override
    public int getItemCount() {
//        Toast.makeText(context, ""+chatName.size(), Toast.LENGTH_SHORT).show();
        return id.size();
    }

    public static class Holder extends RecyclerView.ViewHolder{
        TextView name,message;
        CardView cardView;
        public Holder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.NameOfChat);
            message = itemView.findViewById(R.id.recentMessageChat);
            cardView = itemView.findViewById(R.id.displayChatCardView);
        }
    }
}
