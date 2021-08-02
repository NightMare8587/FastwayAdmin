package com.consumers.fastwayadmin.HomeScreen.ReportSupport;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MyOrderView extends RecyclerView.Adapter<MyOrderView.holder>  {
   List<String> timeOfOrder;
   List<String> foodOrderTime;
   List<List<String>> finalList;
   List<List<String>> finalListPrice;
   List<List<String>> finalType;
   List<String> resId;
   Context context;
   List<String> type;
   public MyOrderView(List<List<String>> finalType, Context context, List<String> resId,
                      List<String> type, List<String> timeOfOrder, List<String> foodOrderTime,
                      List<List<String>> finalList, List<List<String>> finalListPrice) {

       this.finalListPrice = finalListPrice;
       this.finalList = finalList;
       this.finalType = finalType;
       this.foodOrderTime = foodOrderTime;
       this.context = context;
       this.resId = resId;
       this.type = type;
       this.timeOfOrder = timeOfOrder;
   }

   @NonNull
   @Override
   public holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
       View view = layoutInflater.inflate(R.layout.recent_orders_card,parent,false);
       return new holder(view);
   }

   @SuppressLint("SetTextI18n")
   @Override
   public void onBindViewHolder(@NonNull holder holder, int position) {
       DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Users").child(resId.get(position));
       databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot snapshot) {
           holder.name.setText(snapshot.child("name").getValue(String.class));
//               holder.price.setText(snapshot.child("email").getValue(String.class));
           }

           @Override
           public void onCancelled(@NonNull DatabaseError error) {

           }
       });

       @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
       long milis = Long.parseLong(timeOfOrder.get(position));
       Calendar calendar = Calendar.getInstance();
       calendar.setTimeInMillis(milis);
       holder.time.setText("" + dateFormat.format(calendar.getTime()));
       List<String> priceList = new ArrayList<>(finalListPrice.get(position));
       ArrayAdapter<String> priceAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1,priceList);
       List<String> myList = new ArrayList<>(finalList.get(position));
       ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, myList);
       holder.listView.setAdapter(adapter);
       holder.priceList.setAdapter(priceAdapter);
       ViewGroup.LayoutParams param = holder.listView.getLayoutParams();
       param.height = 150*myList.size();
       holder.listView.setLayoutParams(param);
       holder.listView.requestLayout();
       ViewGroup.LayoutParams params = holder.priceList.getLayoutParams();
       param.height = 150*priceList.size();
       holder.priceList.setLayoutParams(param);
       holder.priceList.requestLayout();


   }

   @Override
   public int getItemCount() {
       return finalList.size();
   }


   public static class holder extends RecyclerView.ViewHolder{
       TextView name;
       ImageView imageView;
       Button rate;
       ListView listView,priceList;
       TextView time;
       CardView cardView;
       public holder(@NonNull View itemView) {
           super(itemView);
           name = itemView.findViewById(R.id.recentOrderUserName);
           rate = itemView.findViewById(R.id.recentOrderReportButton);
           imageView = itemView.findViewById(R.id.myOrderDishImage);
           time = itemView.findViewById(R.id.timeOfOrderDate);
           listView = itemView.findViewById(R.id.myListViewOrderDish);
           priceList = itemView.findViewById(R.id.myListViewOrderPrice);
           cardView = itemView.findViewById(R.id.myOrdersCard);
       }
   }

}
