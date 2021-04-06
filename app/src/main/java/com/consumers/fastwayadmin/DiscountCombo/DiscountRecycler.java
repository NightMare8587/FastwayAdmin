package com.consumers.fastwayadmin.DiscountCombo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.consumers.fastwayadmin.R;

import java.util.ArrayList;
import java.util.List;

public class DiscountRecycler extends RecyclerView.Adapter<DiscountRecycler.Holder> {
    List<String> dishName = new ArrayList<>();
    public DiscountRecycler(List<String> dishName){
        this.dishName = dishName;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.discount_card,parent,false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.textView.setText(dishName.get(position));
    }

    @Override
    public int getItemCount() {
        return dishName.size();
    }

    public static class Holder extends RecyclerView.ViewHolder{
        TextView textView;
        CheckBox checkBox;
        public Holder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.discountViewDishName);
            checkBox = itemView.findViewById(R.id.recyclerDiscountCheckBox);
        }
    }
}
