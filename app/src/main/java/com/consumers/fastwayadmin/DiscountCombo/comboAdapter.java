package com.consumers.fastwayadmin.DiscountCombo;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class comboAdapter extends RecyclerView.Adapter<comboAdapter.Holder> {
    List<String> name = new ArrayList<>();

    public comboAdapter(List<String> name) {
        this.name = name;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return name.size();
    }
    public static class Holder extends RecyclerView.ViewHolder{
        TextView name;
        public Holder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
