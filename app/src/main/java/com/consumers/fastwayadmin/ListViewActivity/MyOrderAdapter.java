package com.consumers.fastwayadmin.ListViewActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.consumers.fastwayadmin.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MyOrderAdapter extends RecyclerView.Adapter<MyOrderAdapter.Holder> {
    List<String> amount;
    List<String> time;
    List<String> transID;
    List<String> status;
    Context context;

    public MyOrderAdapter(List<String> amount, List<String> time, List<String> transID, List<String> status,Context context) {
        this.amount = amount;
        this.time = time;
        this.transID = transID;
        this.status = status;
        this.context = context;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.order_trans_card,parent,false);
        return new Holder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        if(status.get(position).equals("SUCCESS")) {
            holder.statusTransaction.setTextColor(Color.GREEN);
            holder.statusTransaction.setText(status.get(position));
        }else{
            holder.statusTransaction.setTextColor(Color.RED);
            holder.statusTransaction.setText(status.get(position));
        }
        holder.date.setText(DateFormat.getInstance().format(Long.parseLong(time.get(position))));
        holder.orderAmount.setText("\u20B9" + amount.get(position));
    }

    @Override
    public int getItemCount() {
        return transID.size();
    }
    public class Holder extends RecyclerView.ViewHolder{
        TextView orderAmount,date,statusTransaction;
        public Holder(@NonNull View itemView) {
            super(itemView);
            orderAmount = itemView.findViewById(R.id.orderAmountTransactionCardView);
            date = itemView.findViewById(R.id.dateOfTransactionCardView);
            statusTransaction = itemView.findViewById(R.id.statusOrderTransCard);
        }
    }
}
