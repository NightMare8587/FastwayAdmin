package com.consumers.fastwayadmin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NotificationView extends RecyclerView.Adapter<NotificationView.DataView> {

    List<String> title = new ArrayList<String>();
    List<String> message = new ArrayList<String>();

    List<String> timeList = new ArrayList<String>();

    public NotificationView(List<String> title, List<String> message,List<String> timeList){
        this.message = message;
        this.timeList = timeList;
        this.title = title;
    }

    @NonNull
    @Override
    public DataView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater =  LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.noti_card,parent,false);
        return new DataView(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DataView holder, int position) {
        holder.title.setText(title.get(position));
        holder.message.setText(message.get(position));
        holder.time.setText("Time: " + new SimpleDateFormat("dd/MM/yy hh:mm:ss").format(new Date(Long.parseLong(timeList.get(position)))));
    }

    @Override
    public int getItemCount() {
        return title.size();
    }

 public static class DataView extends RecyclerView.ViewHolder{
    TextView title,message,time;
     public DataView(@NonNull View itemView) {
         super(itemView);
         title = itemView.findViewById(R.id.notiTitle);
         message = itemView.findViewById(R.id.notiMessage);
         time = itemView.findViewById(R.id.notiTimeNotification);

     }
 }
}
