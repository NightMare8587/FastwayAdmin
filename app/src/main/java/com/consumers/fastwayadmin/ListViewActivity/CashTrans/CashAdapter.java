package com.consumers.fastwayadmin.ListViewActivity.CashTrans;

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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CashAdapter extends RecyclerView.Adapter<CashAdapter.holder> {
    List<String> orderID = new ArrayList<>();
    List<String> orderAmount = new ArrayList<>();
    List<String> time = new ArrayList<>();
    List<String> userID = new ArrayList<>();
    Context context;

    public CashAdapter(List<String> orderID, List<String> orderAmount, List<String> time, List<String> userID, Context context) {
        this.orderID = orderID;
        this.orderAmount = orderAmount;
        this.time = time;
        this.userID = userID;
        this.context = context;
    }

    @NonNull
    @Override
    public holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.cash_transactions_layout_card,parent,false);
        return new holder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull holder holder, int position) {
        holder.status.setTextColor(Color.GREEN);
        holder.timeofOrder.setText(DateFormat.getInstance().format(Long.parseLong(Objects.requireNonNull(time.get(position)))));
        holder.orderAmount.setText("\u20B9" + orderAmount.get(position));

    }

    @Override
    public int getItemCount() {
        return time.size();
    }
    public class holder extends RecyclerView.ViewHolder{
        TextView status,orderAmount,timeofOrder;
        public holder(@NonNull View itemView) {
            super(itemView);
            status = itemView.findViewById(R.id.successCashTransactionCard);
            orderAmount = itemView.findViewById(R.id.orderAmountCashTransaction);
            timeofOrder = itemView.findViewById(R.id.timeOfOrderCashTrasanctinb);
        }
    }
}
