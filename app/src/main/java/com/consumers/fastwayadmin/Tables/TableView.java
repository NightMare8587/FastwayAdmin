package com.consumers.fastwayadmin.Tables;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.consumers.fastwayadmin.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TableView extends RecyclerView.Adapter<TableView.TableAdapter> {
    List<String> tables = new ArrayList<>();
    List<String> status = new ArrayList<>();
    HashMap<String,List<String>> map = new HashMap<>();
    DatabaseReference reference;
    FirebaseAuth auth;
    public TableView(List<String> tables,List<String> status,HashMap<String,List<String>> map){
        this.status = status;
        this.map = map;
        this.tables = tables;
    }

    @NonNull
    @Override
    public TableAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.table_info,parent,false);
        return new TableAdapter(view);
    }



    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull TableAdapter holder, int position) {
        auth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(auth.getUid()).child("Tables");
        holder.tableNum.setText("Table Number : " + tables.get(position));
        holder.status.setText(status.get(position));
        if(status.get(position).equals("Reserved")){
            List<String> myList = map.get(""+tables.get(position));
            holder.chatWith.setVisibility(View.VISIBLE);
            holder.cancel.setVisibility(View.VISIBLE);
            holder.timeOfReserved.setVisibility(View.VISIBLE);
            holder.timeOfReserved.setText(myList.get(1)+"");


            holder.cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new K
                    reference.child(tables.get(position)).child("customerId").removeValue();
                    reference.child(tables.get(position)).child("status").setValue("available");
                    reference.child(tables.get(position)).child("time").removeValue();
                    holder.chatWith.setVisibility(View.INVISIBLE);
                    holder.cancel.setVisibility(View.INVISIBLE);
                    holder.timeOfReserved.setVisibility(View.INVISIBLE);

                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return tables.size();
    }

    public static class TableAdapter extends RecyclerView.ViewHolder{
        TextView tableNum,status,chatWith,cancel,timeOfReserved;
        public TableAdapter(@NonNull View itemView) {
            super(itemView);
            tableNum = itemView.findViewById(R.id.numberOfTable);
            status = itemView.findViewById(R.id.statusOfTable);
            chatWith = itemView.findViewById(R.id.chatWithCustomer);
            cancel = itemView.findViewById(R.id.cancelSeat);
            timeOfReserved = itemView.findViewById(R.id.timeOfReservedTable);
        }
    }
}
