package com.consumers.fastwayadmin.NavFrags.homeFrag;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.consumers.fastwayadmin.R;
import com.consumers.fastwayadmin.Tables.ChatWithCustomer;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class homeFragClass extends RecyclerView.Adapter<homeFragClass.ViewHolder> {
    List<String> tableNum = new ArrayList<>();
    List<String> seats = new ArrayList<>();
    List<String> resId = new ArrayList<>();
    List<String> amountPending = new ArrayList<>();
    List<String> isCurrentOrder = new ArrayList<>();

    public homeFragClass(List<String> tableNum,List<String> seats,List<String> resId,List<String> isCurrentOrder,List<String> amountPending){
        this.seats = seats;
        this.resId = resId;
        this.tableNum = tableNum;
        this.amountPending = amountPending;
        this.isCurrentOrder = isCurrentOrder;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.home_frag_recycler_adapter,parent,false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.tables.setText("Table Num: " + tableNum.get(position));
        if(amountPending.get(position).equals("1"))
        holder.seats.setText("Seats: " + seats.get(position) + " (Payment Pending)");
        else {
            holder.seats.setText("Seats: " + seats.get(position));
            SharedPreferences sharedPreferences = holder.cardView.getContext().getSharedPreferences("StoreDataForPayInEnd",Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove(tableNum.get(position)).apply();
        }
        holder.chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), ChatWithCustomer.class);
                intent.putExtra("id",resId.get(position));
                view.getContext().startActivity(intent);
            }
        });

        holder.isCurrentOrderMade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = v.getContext().getSharedPreferences("loginInfo", Context.MODE_PRIVATE);
                Intent intent = new Intent(v.getContext(),ApproveCurrentOrder.class);
                intent.putExtra("table",tableNum.get(position));
                intent.putExtra("state",sharedPreferences.getString("state",""));
                intent.putExtra("id",resId.get(position));
                v.getContext().startActivity(intent);
            }
        });

        if(isCurrentOrder.get(position).equals("1"))
            holder.isCurrentOrderMade.setVisibility(View.VISIBLE);
        else
            holder.isCurrentOrderMade.setVisibility(View.INVISIBLE);

        holder.cardView.setOnClickListener(view -> {
            if(amountPending.get(position).equals("1")){
                FirebaseAuth auth = FirebaseAuth.getInstance();
                SharedPreferences sharedPreferences = view.getContext().getSharedPreferences("loginInfo", Context.MODE_PRIVATE);
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(sharedPreferences.getString("state", "")).child(sharedPreferences.getString("locality", "")).child(Objects.requireNonNull(auth.getUid())).child("Tables").child(tableNum.get(position)).child("StoreOrdersCheckOut");
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
//                            List<String> time = new ArrayList<>();
                            List<String> dishName = new ArrayList<>();
                            List<String> dishQ = new ArrayList<>();
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
//                                time.add(dataSnapshot.getKey());
                                dishName.add(dataSnapshot.getKey());
                                dishQ.add(dataSnapshot.child("count").getValue(String.class));
                            }
                            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                            builder.setTitle("Orders Made");
                            LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                            View view = inflater.inflate(R.layout.res_info_dialog_layout, null);

                            ArrayAdapter<String> arrayAdapter1 = new ArrayAdapter<>(view.getContext(), android.R.layout.simple_list_item_1, dishName);
                            ArrayAdapter<String> arrayAdapter2 = new ArrayAdapter<>(view.getContext(), android.R.layout.simple_list_item_1, dishQ);
                            ListView listView1 = view.findViewById(R.id.listDishNamesResInfo);
                            listView1.setAdapter(arrayAdapter1);
                            ListView listView2 = view.findViewById(R.id.listDishNamesQuantityInfo);
                            listView2.setAdapter(arrayAdapter2);
                            builder.setView(view);
//                builder.setItems(array, (dialogInterface, i) -> Log.i("list",array.toString()));
                            builder.setPositiveButton("exit", (dialogInterface, i) -> dialogInterface.dismiss()).create().show();
                        }else
                            Toast.makeText(view.getContext(), "No Order's Made", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }else {
                FirebaseAuth auth = FirebaseAuth.getInstance();
                SharedPreferences sharedPreferences = view.getContext().getSharedPreferences("loginInfo", Context.MODE_PRIVATE);
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(sharedPreferences.getString("state", "")).child(sharedPreferences.getString("locality", "")).child(auth.getUid()).child("Tables").child(tableNum.get(position)).child("CurrentOrdersMade");
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            List<String> time = new ArrayList<>();
                            List<String> dishName = new ArrayList<>();
                            List<String> dishQ = new ArrayList<>();
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                time.add(dataSnapshot.getKey());
                                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                    dishName.add(dataSnapshot1.getKey());
                                    dishQ.add(dataSnapshot1.child("quantity").getValue(String.class));
                                }
                            }
                            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                            builder.setTitle("Orders Made");
                            LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                            View view = inflater.inflate(R.layout.res_info_dialog_layout, null);

                            ArrayAdapter<String> arrayAdapter1 = new ArrayAdapter<>(view.getContext(), android.R.layout.simple_list_item_1, dishName);
                            ArrayAdapter<String> arrayAdapter2 = new ArrayAdapter<>(view.getContext(), android.R.layout.simple_list_item_1, dishQ);
                            ListView listView1 = view.findViewById(R.id.listDishNamesResInfo);
                            listView1.setAdapter(arrayAdapter1);
                            ListView listView2 = view.findViewById(R.id.listDishNamesQuantityInfo);
                            listView2.setAdapter(arrayAdapter2);
                            builder.setView(view);
//                builder.setItems(array, (dialogInterface, i) -> Log.i("list",array.toString()));
                            builder.setPositiveButton("exit", (dialogInterface, i) -> dialogInterface.dismiss()).create().show();
                        }else
                            Toast.makeText(view.getContext(), "No Order's Made", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return tableNum.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView tables,seats;
        Button chat,isCurrentOrderMade;
        CardView cardView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tables = itemView.findViewById(R.id.homeFragRecyclerViewTables);
            seats = itemView.findViewById(R.id.homeFragRecyclerViewSeats);
            chat = itemView.findViewById(R.id.chat);
            isCurrentOrderMade = itemView.findViewById(R.id.isCurrentOrderMade);
            cardView = itemView.findViewById(R.id.homeFragClassCardView);
        }
    }
}
