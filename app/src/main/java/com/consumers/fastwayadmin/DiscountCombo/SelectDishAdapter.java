package com.consumers.fastwayadmin.DiscountCombo;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class SelectDishAdapter extends RecyclerView.Adapter<SelectDishAdapter.holder> {
    List<String> name = new ArrayList<>();
    List<String> image = new ArrayList<>();

    public SelectDishAdapter(List<String> name, List<String> image) {
        this.name = name;
        this.image = image;
    }

    @NonNull
    @Override
    public holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull holder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return name.size();
    }

    public static class holder extends RecyclerView.ViewHolder{
        TextView nameOfDish;
        ImageView imageView;
        public holder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
