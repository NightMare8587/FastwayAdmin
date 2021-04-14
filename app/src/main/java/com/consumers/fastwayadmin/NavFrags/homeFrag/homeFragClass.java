package com.consumers.fastwayadmin.NavFrags.homeFrag;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.consumers.fastwayadmin.R;

import java.util.ArrayList;
import java.util.List;

public class homeFragClass extends RecyclerView.Adapter<homeFragClass.ViewHolder> {
    List<String> tableNum = new ArrayList<>();
    List<String> seats = new ArrayList<>();
    public homeFragClass(List<String> tableNum,List<String> seats){
        this.seats = seats;
        this.tableNum = tableNum;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.home_frag_recycler_adapter,parent,false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tables.setText("Table Num: " + tableNum.get(position));
        holder.seats.setText("Seats: " + seats.get(position));
    }

    @Override
    public int getItemCount() {
        return tableNum.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView tables,seats;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tables = itemView.findViewById(R.id.homeFragRecyclerViewTables);
            seats = itemView.findViewById(R.id.homeFragRecyclerViewSeats);
        }
    }
}
