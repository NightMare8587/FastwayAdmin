package com.consumers.fastwayadmin.ListViewActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.consumers.fastwayadmin.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class MyOrderAdapter extends RecyclerView.Adapter<MyOrderAdapter.Holder> {
    List<String> amount;
    List<String> time;
    List<String> transID;
    HashMap<String,String> map;
    List<String> status;
    int amounts,days;
    Context context;

    public MyOrderAdapter(List<String> amount, List<String> time, List<String> transID, List<String> status,Context context,int amounts,int days,HashMap<String,String> map) {
        this.amount = amount;
        this.time = time;
        this.amounts = amounts;
        this.days = days;
        this.transID = transID;
        this.map = map;
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
        holder.customerDetails.setOnClickListener(click -> {
//            Toast.makeText(click.getContext(), ""+ map.get(transID.get(position)), Toast.LENGTH_SHORT).show();
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Users").child(Objects.requireNonNull(map.get(transID.get(position))));
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Log.i("info",snapshot.child("name").getValue(String.class));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        });
    }

    @Override
    public int getItemCount() {
        return transID.size();
    }
    public class Holder extends RecyclerView.ViewHolder{
        TextView orderAmount,date,statusTransaction;
        CardView cardView;
        Button customerDetails;
        public Holder(@NonNull View itemView) {
            super(itemView);
            orderAmount = itemView.findViewById(R.id.orderAmountTransactionCardView);
            cardView = itemView.findViewById(R.id.orderTransCardID);
            date = itemView.findViewById(R.id.dateOfTransactionCardView);
            statusTransaction = itemView.findViewById(R.id.statusOrderTransCard);
            customerDetails = itemView.findViewById(R.id.customerDetailsTransCard);
        }
    }
}
