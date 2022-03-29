package com.consumers.fastwayadmin.NavFrags.ResEarningTracker;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.consumers.fastwayadmin.R;

import java.util.List;

public class TackerAdapter extends RecyclerView.Adapter<TackerAdapter.Holder> {
    List<String> monthNames;

    public TackerAdapter(List<String> monthNames) {
        this.monthNames = monthNames;
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
        holder.monthName.setOnClickListener(click -> {
            Intent intent = new Intent("custom-message");
            //            intent.putExtra("quantity",Integer.parseInt(quantity.getText().toString()));
            intent.putExtra("month",monthNames.get(position));
            LocalBroadcastManager.getInstance(click.getContext()).sendBroadcast(intent);
        });
    }



    @Override
    public int getItemCount() {
        return monthNames.size();
    }
    public class Holder extends RecyclerView.ViewHolder{
        TextView monthName;
        public Holder(@NonNull View itemView) {
            super(itemView);
            monthName = itemView.findViewById(R.id.monthNameTrackerCard);
        }
    }
}
