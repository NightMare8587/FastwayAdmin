package com.example.fastwayadmin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class DishView extends RecyclerView.Adapter<DishView.DishAdapter> {
    List<String> names = new ArrayList<String>();
    List<String> fullPrice = new ArrayList<String>();
    List<String> half = new ArrayList<String>();
    public DishView(List<String> names,List<String> full,List<String> half){
        this.fullPrice = full;
        this.half = half;
        this.names = names;
    }
    @NonNull
    @Override
    public DishAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.card,parent,false);
        return new DishAdapter(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DishAdapter holder, int position) {
        holder.name.setText(names.get(position));
        holder.price.setText(fullPrice.get(position));
        if(!half.get(position).isEmpty()){
            holder.available.setText("Half Plate Available");
        }else{
            holder.available.setText("Half Plate Not Available");
        }
    }

    @Override
    public int getItemCount() {
        return names.size();
    }

    public static class DishAdapter extends RecyclerView.ViewHolder {
        TextView name,price,available;
        public DishAdapter(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.DishName);
            price = itemView.findViewById(R.id.pricePfDish);
            available = itemView.findViewById(R.id.availableOr);
        }
    }
}
