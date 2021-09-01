package com.consumers.fastwayadmin.NavFrags.CurrentTakeAwayOrders;

import android.annotation.SuppressLint;
import android.text.Layout;
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

public class CurrentTakeAway extends RecyclerView.Adapter<CurrentTakeAway.Holder> {
    List<String> currentTakeAwayAuth = new ArrayList<>();
    List<String> dishNameCurrentTakeAway = new ArrayList<>();
    List<String> dishQuantityCurrentTakeAway = new ArrayList<>();
    List<String> userNameTakeAway = new ArrayList<>();

    public CurrentTakeAway(List<String> currentTakeAwayAuth, List<String> dishNameCurrentTakeAway, List<String> dishQuantityCurrentTakeAway,List<String> userNameTakeAway) {
        this.currentTakeAwayAuth = currentTakeAwayAuth;
        this.dishNameCurrentTakeAway = dishNameCurrentTakeAway;
        this.userNameTakeAway = userNameTakeAway;
        this.dishQuantityCurrentTakeAway = dishQuantityCurrentTakeAway;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.takeaway_recycler_adapter,parent,false);
        return new Holder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.userName.setText("Name: " + userNameTakeAway.get(position));
        holder.checkOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return currentTakeAwayAuth.size();
    }

    public class Holder extends RecyclerView.ViewHolder{
        TextView userName;
        Button checkOrder;
        public Holder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.nameTakeAwayCustomer);
            checkOrder = itemView.findViewById(R.id.takeAwayCurrentOrder);
        }
    }
}
