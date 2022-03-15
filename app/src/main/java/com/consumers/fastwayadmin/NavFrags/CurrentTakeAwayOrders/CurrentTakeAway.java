package com.consumers.fastwayadmin.NavFrags.CurrentTakeAwayOrders;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.consumers.fastwayadmin.R;
import com.consumers.fastwayadmin.Tables.ChatWithCustomer;

import java.util.ArrayList;
import java.util.List;

public class CurrentTakeAway extends RecyclerView.Adapter<CurrentTakeAway.Holder> {
    List<List<String>> finalDishNames = new ArrayList<>();
    List<List<String>> finalDishQuantity = new ArrayList<>();
    List<List<String>> finalHalfOr = new ArrayList<>();
    List<String> currentTakeAwayAuth = new ArrayList<>();
    List<String> finalUserNames = new ArrayList<>();
    List<String> customisationList = new ArrayList<>();
    List<String> orderId = new ArrayList<>();
    List<String> orderAmount = new ArrayList<>();
    List<String> time;

    public CurrentTakeAway(List<List<String>> finalDishNames, List<List<String>> finalDishQuantity, List<List<String>> finalHalfOr, List<String> finalUserNames, List<String> finalPayment,List<String> orderId,List<String> orderAmount,List<String> currentTakeAwayAuth,List<String> time,List<String> customisationList) {
        this.finalDishNames = finalDishNames;
        this.finalDishQuantity = finalDishQuantity;
        this.finalHalfOr = finalHalfOr;
        this.customisationList = customisationList;
        this.time = time;
        this.finalUserNames = finalUserNames;
        this.orderAmount = orderAmount;
        this.currentTakeAwayAuth = currentTakeAwayAuth;
        this.orderId = orderId;
        this.finalPayment = finalPayment;
    }

    List<String> finalPayment = new ArrayList<>();
    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.takeaway_recycler_adapter,parent,false);
        return new Holder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull Holder holder, @SuppressLint("RecyclerView") int position) {
        holder.userName.setText("Name: " + finalUserNames.get(position));
        if(finalPayment.get(position).equals("online"))
        holder.paymentMode.setText("Mode: " + finalPayment.get(position));
        else{
            holder.paymentMode.setText("Mode: " + finalPayment.get(position) + " (\u20B9" + orderAmount.get(position) + ")");
        }
        holder.chatWithCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), ChatWithCustomer.class);
                intent.putExtra("id",currentTakeAwayAuth.get(position));
                view.getContext().startActivity(intent);
            }
        });
        holder.checkOrder.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(),ApproveCurrentTakeAway.class);
            List<String> dishQuantityCurrentTakeAway = new ArrayList<>(finalDishQuantity.get(position));
            intent.putExtra("id",currentTakeAwayAuth.get(position));
            List<String> dishNameCurrentTakeAway = new ArrayList<>(finalDishNames.get(position));
            List<String> halfOr = new ArrayList<>(finalHalfOr.get(position));
            intent.putStringArrayListExtra("dishName", (ArrayList<String>) dishNameCurrentTakeAway);
            intent.putStringArrayListExtra("DishQ",(ArrayList<String>) dishQuantityCurrentTakeAway);
            intent.putStringArrayListExtra("halfOr",(ArrayList<String>) halfOr);
            intent.putExtra("orderID",orderId.get(position));
            intent.putExtra("payment",finalPayment.get(position));
            intent.putExtra("time",time.get(position));
            intent.putExtra("orderAmount",orderAmount.get(position));
            intent.putExtra("customisation",customisationList.get(position));
            Log.i("log",customisationList.get(position));
            view.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return currentTakeAwayAuth.size();
    }

    public class Holder extends RecyclerView.ViewHolder{
        TextView userName,paymentMode;
        Button checkOrder,chatWithCustomer;
        public Holder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.nameTakeAwayCustomer);
            checkOrder = itemView.findViewById(R.id.takeAwayCurrentOrder);
            paymentMode = itemView.findViewById(R.id.paymentModeTEakeaway);
            chatWithCustomer = itemView.findViewById(R.id.chatTakeAwayUser);
        }
    }
}
