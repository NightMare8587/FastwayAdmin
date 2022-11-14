package com.consumers.fastwayadmin.NavFrags.ResEarningTracker;

import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.consumers.fastwayadmin.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class TableCusRecycler extends RecyclerView.Adapter<TableCusRecycler.Holder> {
    List<String> tableNum;
    double totalVal;

    public TableCusRecycler(List<String> tableNum, double totalVal, List<String> customerAquisition) {
        this.tableNum = tableNum;
        this.totalVal = totalVal;
        this.customerAquisition = customerAquisition;
    }

    List<String> customerAquisition;
    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.table_customer_recycler_card,parent,false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.customer.setText("Total Customer's: " + new DecimalFormat("#").format(Double.parseDouble(customerAquisition.get(position))));
        holder.tableNum.setText("Table Num: " + tableNum.get(position));

        double finalRate = ( Double.parseDouble(customerAquisition.get(position)) / totalVal ) * 100;
        holder.rate.setText("Acquisition Rate: " + new DecimalFormat("#").format(finalRate) + "%");
    }

    @Override
    public int getItemCount() {
        return tableNum.size();
    }

    public class Holder extends RecyclerView.ViewHolder{
        TextView customer,rate,tableNum;
        public Holder(@NonNull View itemView) {
            super(itemView);
            customer= itemView.findViewById(R.id.totalCustomerRecyclerCardView);
            tableNum= itemView.findViewById(R.id.tableNumTotalCustomerRecyclerCard);
            rate= itemView.findViewById(R.id.totalCustomerRateRecyclerCardView);
        }
    }
}
