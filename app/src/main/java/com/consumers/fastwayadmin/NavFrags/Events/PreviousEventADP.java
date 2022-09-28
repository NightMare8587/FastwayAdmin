package com.consumers.fastwayadmin.NavFrags.Events;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.consumers.fastwayadmin.R;

import java.util.ArrayList;
import java.util.List;

public class PreviousEventADP extends RecyclerView.Adapter<PreviousEventADP.Holder> {
    List<String> eventNames = new ArrayList<>();
    List<String> ticketsSold = new ArrayList<>();
    List<String> dateAndTimeList = new ArrayList<>();
    List<String> artistNameList = new ArrayList<>();

    public PreviousEventADP(List<String> eventNames, List<String> ticketsSold, List<String> dateAndTimeList, List<String> artistNameList) {
        this.eventNames = eventNames;
        this.ticketsSold = ticketsSold;
        this.dateAndTimeList = dateAndTimeList;
        this.artistNameList = artistNameList;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.prev_campadap_layout,parent,false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
    public class Holder extends RecyclerView.ViewHolder{
        TextView eventName,artist,ticketSold,dateAndTime;
        public Holder(@NonNull View itemView) {
            super(itemView);
            eventName = itemView.findViewById(R.id.cardViewEventNameOrganise);
            artist = itemView.findViewById(R.id.ArtistNameCardViewEvent);
            ticketSold = itemView.findViewById(R.id.ticketSoldCardViewEvent);
            dateAndTime = itemView.findViewById(R.id.dateAndTimeCardViewEvent);
        }
    }
}
