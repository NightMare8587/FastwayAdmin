package com.consumers.fastwayadmin.NavFrags.ReplaceOrders;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.consumers.fastwayadmin.R;

import java.util.ArrayList;
import java.util.List;

public class ReplaceRecyclerView extends RecyclerView.Adapter<ReplaceRecyclerView.Holder> {
    List<String> name = new ArrayList<>();
    List<String> details = new ArrayList<>();
    List<String> userID = new ArrayList<>();
    List<String> orderTime = new ArrayList<>();
    List<String> reportingTime = new ArrayList<>();
    List<String> imageURI = new ArrayList<>();
    List<String> orderID = new ArrayList<>();
    List<String> tableNum = new ArrayList<>();

    public ReplaceRecyclerView(List<String> name, List<String> details, List<String> userID, List<String> orderTime, List<String> reportingTime, List<String> imageURI, List<String> orderID, List<String> tableNum) {
        this.name = name;
        this.details = details;
        this.userID = userID;
        this.orderTime = orderTime;
        this.reportingTime = reportingTime;
        this.imageURI = imageURI;
        this.orderID = orderID;
        this.tableNum = tableNum;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.replace_order_card_view,parent,false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.name.setText(name.get(position));
        holder.tableOrTake.setText("Table Num: " + tableNum.get(position));
        holder.cardView.setOnClickListener(click -> {
            Intent intent = new Intent(click.getContext(),DetailedReplaceOrderAct.class);
            intent.putExtra("name",name.get(position));
            intent.putExtra("tableNum",tableNum.get(position));
            intent.putExtra("orderId",orderID.get(position));
            intent.putExtra("orderTime",orderTime.get(position));
            intent.putExtra("timeOrdered",orderTime.get(position));
            intent.putExtra("imageUri",imageURI.get(position));
            intent.putExtra("details",details.get(position));
            intent.putExtra("userID",userID.get(position));
            click.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return reportingTime.size();
    }
    public class Holder extends RecyclerView.ViewHolder{
        TextView name,tableOrTake;
        CardView cardView;
        public Holder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.userNameReplaceOrderCard);
            tableOrTake = itemView.findViewById(R.id.tableNumOrTakeAwayReplaceCard);
            cardView = itemView.findViewById(R.id.replaceOrderCardID);
        }
    }
}
