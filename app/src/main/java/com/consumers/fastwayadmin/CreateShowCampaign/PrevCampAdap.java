package com.consumers.fastwayadmin.CreateShowCampaign;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.consumers.fastwayadmin.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PrevCampAdap extends RecyclerView.Adapter<PrevCampAdap.Holder> {
    List<String> timeMillis = new ArrayList<>();
    List<String> campNamesList = new ArrayList<>();
    List<String> campCustomersList = new ArrayList<>();
    List<String> campOrdersList = new ArrayList<>();
    List<String> campTransList = new ArrayList<>();

    public PrevCampAdap(List<String> timeMillis, List<String> campNamesList, List<String> campCustomersList, List<String> campOrdersList, List<String> campTransList) {
        this.timeMillis = timeMillis;
        this.campNamesList = campNamesList;
        this.campCustomersList = campCustomersList;
        this.campOrdersList = campOrdersList;
        this.campTransList = campTransList;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.prev_campadap_layout,parent,false);
        return new Holder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.name.setText("Campaign Name: " + campNamesList.get(position));
        holder.cust.setText("Total Customers: " + campCustomersList.get(position));
        holder.orders.setText("Total Orders: " + campOrdersList.get(position));
        holder.trans.setText("Total Transactions: \u20b9" + campTransList.get(position));
        holder.time.setText("Date Ended: " + new SimpleDateFormat("dd/MM/yyyy").format(new Date(Long.parseLong(timeMillis.get(position)))));
    }

    @Override
    public int getItemCount() {
        return timeMillis.size();
    }
    public class Holder extends RecyclerView.ViewHolder{
        TextView name,cust,orders,trans,time;
        public Holder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.campCurrentNameRecycler);
            cust = itemView.findViewById(R.id.campCurrentTotalCustRecycler);
            orders = itemView.findViewById(R.id.campCurrentTotalOrdersRecycler);
            trans = itemView.findViewById(R.id.campCurrentTotalTransAmountRecycler);
            time = itemView.findViewById(R.id.campCurrentTextDateEndedRecycler);
        }
    }
}
