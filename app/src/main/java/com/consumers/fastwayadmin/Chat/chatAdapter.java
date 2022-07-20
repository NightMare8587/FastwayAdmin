package com.consumers.fastwayadmin.Chat;

import android.content.ClipboardManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.consumers.fastwayadmin.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class chatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    List<String> message = new ArrayList<>();
    List<String> time = new ArrayList<>();
    List<String> typeOfMessage = new ArrayList<>();
    List<String> leftOrRight = new ArrayList<>();
    int send = 1;

    public chatAdapter(List<String> message, List<String> time, List<String> leftOrRight,List<String> typeOfMessage) {
        this.message = message;
        this.typeOfMessage = typeOfMessage;
        this.time = time;
        this.leftOrRight = leftOrRight;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        if(viewType == send){
            view = layoutInflater.inflate(R.layout.card_message_revive,parent,false);
            return new ReciveHolder(view);
        }else if(viewType == 0){
            view = layoutInflater.inflate(R.layout.card_message_send,parent,false);
            return new SentViewHolder(view);
        }else if(viewType == 2){
            view = layoutInflater.inflate(R.layout.card_image_send,parent,false);
            return new SentViewImageHolder(view);
        }else{
            view = layoutInflater.inflate(R.layout.card_image_receive,parent,false);
            return new ReciveImageHolder(view);
        }
//        view = layoutInflater.inflate(R.layout.card_message_send,parent,false);
//        return new holder(view);
    }

    public class SentViewHolder extends RecyclerView.ViewHolder{
        TextView send;
        public SentViewHolder(@NonNull View itemView) {
            super(itemView);
            send = itemView.findViewById(R.id.textSend);
        }
    }
    public class SentViewImageHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        Button sendImage;
        public SentViewImageHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageViewCardImageSend);

        }
    }
    public class ReciveImageHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        public ReciveImageHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageViewCardImageReceive);
        }
    }
    public class ReciveHolder extends RecyclerView.ViewHolder{
        TextView recive;
        public ReciveHolder(@NonNull View itemView) {
            super(itemView);
            recive = itemView.findViewById(R.id.textRecive);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(Integer.parseInt(leftOrRight.get(position)) == send && typeOfMessage.get(position).equals("message"))
            return 1;
        else if(Integer.parseInt(leftOrRight.get(position)) == send && typeOfMessage.get(position).equals("image"))
            return 3;
        else if(Integer.parseInt(leftOrRight.get(position)) == 0 && typeOfMessage.get(position).equals("message"))
            return 0;
        else
            return 2;
//        return super.getItemViewType(position);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
//        holder.send.setText(message.get(position));
        if(holder.getClass() == SentViewHolder.class){
            SentViewHolder viewHolder = (SentViewHolder) holder;
            ((SentViewHolder) holder).send.setText(message.get(position));
            ((SentViewHolder) holder).send.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    ClipboardManager cm = (ClipboardManager)v.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                    cm.setText(((SentViewHolder) holder).send.getText().toString());
                    Toast.makeText(v.getContext(), "Copied to clipboard", Toast.LENGTH_SHORT).show();
                    return true;
                }
            });
        }else if(holder.getClass() == ReciveHolder.class){
            ReciveHolder reciveHolder = (ReciveHolder) holder;
            ((ReciveHolder) holder).recive.setText(message.get(position));
            ((ReciveHolder) holder).recive.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    ClipboardManager cm = (ClipboardManager)v.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                    cm.setText(((ReciveHolder) holder).recive.getText().toString());
                    Toast.makeText(v.getContext(), "Copied to clipboard", Toast.LENGTH_SHORT).show();
                    return true;
                }
            });
        }else if(holder.getClass() == SentViewImageHolder.class){
            SentViewImageHolder viewHolder = (SentViewImageHolder) holder;
//            ((SentViewImageHolder) holder).imageView.setText(message.get(position));
            Picasso.get().load(message.get(position)).into(((SentViewImageHolder) holder).imageView);
//            ((SentViewImageHolder) holder).imageView.setOnLongClickListener(v -> {
//                ClipboardManager cm = (ClipboardManager)v.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
//                cm.setText(((SentViewHolder) holder).send.getText().toString());
//                Toast.makeText(v.getContext(), "Copied to clipboard", Toast.LENGTH_SHORT).show();
//                return true;
//            });
        }else{
            ReciveImageHolder reciveImageHolder = (ReciveImageHolder) holder;
            Picasso.get().load(message.get(position)).into(((ReciveImageHolder) holder).imageView);
        }
    }

    @Override
    public int getItemCount() {
        return message.size();
    }
//    public static class holder extends RecyclerView.ViewHolder{
//        TextView send,receive;
//        public holder(@NonNull View itemView) {
//            super(itemView);
//            send = itemView.findViewById(R.id.textSend);
////            receive = itemView.findViewById(R.id.textRecive);
//        }
//    }
}
