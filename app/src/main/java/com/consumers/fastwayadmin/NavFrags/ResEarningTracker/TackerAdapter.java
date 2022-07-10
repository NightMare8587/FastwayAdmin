package com.consumers.fastwayadmin.NavFrags.ResEarningTracker;

import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.consumers.fastwayadmin.R;

import java.util.List;

public class TackerAdapter extends RecyclerView.Adapter<TackerAdapter.Holder> {
    List<String> monthNames;
    String month;
    int oldPosition;
    String prevMonthName;

    public TackerAdapter(List<String> monthNames,String month) {
        this.monthNames = monthNames;
        this.month = month;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.tracker_card_file,parent,false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.monthName.setText(monthNames.get(position));
        if(monthNames.get(position).equals(month)) {
            holder.monthName.setBackgroundColor(Color.CYAN);
            oldPosition = holder.getAbsoluteAdapterPosition();
        }
        holder.monthName.setOnClickListener(click -> {
            Intent intent = new Intent("custom-message");
            //            intent.putExtra("quantity",Integer.parseInt(quantity.getText().toString()));
            intent.putExtra("month",monthNames.get(position));

            month = monthNames.get(position);
//            for(int i=0;i<monthNames.size();i++) {
//                if(monthNames.get(i).equals(month))
//                holder.monthName.setBackgroundColor(Color.CYAN);
//                else
//                    holder.monthName.setBackgroundColor(Color.WHITE);
//            }
//            notifyDataSetChanged();
            LocalBroadcastManager.getInstance(click.getContext()).sendBroadcast(intent);
        });
    }



    @Override
    public int getItemCount() {
        return monthNames.size();
    }
    public class Holder extends RecyclerView.ViewHolder{
        TextView monthName;
        CardView cardView;
        public Holder(@NonNull View itemView) {
            super(itemView);
            monthName = itemView.findViewById(R.id.monthNameTrackerCard);
            cardView = itemView.findViewById(R.id.trackerMonthCardView);
        }
    }
}
