package com.consumers.fastwayadmin.ListViewActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Shader;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.consumers.fastwayadmin.HomeScreen.ReportSupport.OtherReportClass;
import com.consumers.fastwayadmin.R;
import com.google.firebase.auth.FirebaseAuth;
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
    String channel_id = "notification_channel";
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
    public void onBindViewHolder(@NonNull Holder holder, @SuppressLint("RecyclerView") int position) {
        if(status.get(position).equals("SUCCESS")) {
            holder.statusTransaction.setTextColor(Color.GREEN);
        }else{
            holder.statusTransaction.setTextColor(Color.RED);
        }
        holder.statusTransaction.setText(status.get(position));

        holder.cardView.setOnClickListener(click -> {
            if(status.get(position).equals("FAILED")){
                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                alert.setTitle("Failed");
                alert.setMessage("Transaction Failed\nDo you wanna raise a issue to Fastway about failed transaction");
                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        FirebaseAuth auth = FirebaseAuth.getInstance();
                        SharedPreferences resInfo = context.getSharedPreferences("RestaurantInfo",Context.MODE_PRIVATE);
                        SharedPreferences accountIfo = context.getSharedPreferences("loginInfo",Context.MODE_PRIVATE);
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Complaints").child("Payouts");
                        PayoutFailedClass payoutFailedClass = new PayoutFailedClass(accountIfo.getString("name",""),resInfo.getString("hotelName",""),transID.get(position),map.get(transID.get(position)),accountIfo.getString("email",""),auth.getUid(),accountIfo.getString("state",""));
                        databaseReference.child(Objects.requireNonNull(auth.getUid())).setValue(payoutFailedClass);
                        Toast.makeText(context, "Report Submitted Successfully", Toast.LENGTH_SHORT).show();
                        NotificationCompat.Builder builder
                                = new NotificationCompat
                                .Builder(context,
                                channel_id)
                                .setContentTitle("Ticket raised successfully")
                                .setContentText("We will notify you as soon as your issue is resolved")
                                .setPriority(NotificationManager.IMPORTANCE_MAX)
                                .setSmallIcon(R.drawable.ic_baseline_home_24)
                                .setAutoCancel(true)
                                .setVibrate(new long[]{1000, 1000, 1000,
                                        1000, 1000})
                                .setOnlyAlertOnce(true);

                        NotificationManager notificationManager
                                = (NotificationManager) context.getSystemService(
                                Context.NOTIFICATION_SERVICE);
                        // Check if the Android Version is greater than Oreo
                        if (Build.VERSION.SDK_INT
                                >= Build.VERSION_CODES.O) {
                            NotificationChannel notificationChannel
                                    = new NotificationChannel(
                                    channel_id, "web_app",
                                    NotificationManager.IMPORTANCE_HIGH);
                            notificationManager.createNotificationChannel(
                                    notificationChannel);
                        }

                        notificationManager.notify(15, builder.build());
                        dialogInterface.dismiss();
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                alert.create().show();
            }
        });
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
