package com.consumers.fastwayadmin.DiscountCombo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.consumers.fastwayadmin.R;

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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.combo_adapter_card,parent,false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.name.setText(name.get(position));
        holder.comboButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return name.size();
    }
    public static class Holder extends RecyclerView.ViewHolder{
        TextView name;
        Button comboButton;
        public Holder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.comboAdapterTextView);
            comboButton = itemView.findViewById(R.id.comboAdapterButton);
        }
    }
}
