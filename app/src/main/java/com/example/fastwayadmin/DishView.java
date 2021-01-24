package com.example.fastwayadmin;

import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class DishView extends RecyclerView.Adapter<DishView.DishAdapter> {
    List<String> names = new ArrayList<String>();
    List<String> fullPrice = new ArrayList<String>();
    List<String> half = new ArrayList<String>();
    FirebaseAuth auth;
    DatabaseReference ref;
    String type;
    public DishView(List<String> names,List<String> full,List<String> half,String type){
        this.fullPrice = full;
        this.half = half;
        this.names = names;
        this.type = type;
    }
    @NonNull
    @Override
    public DishAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.card,parent,false);
        return new DishAdapter(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DishAdapter holder, int position) {
        holder.name.setText(names.get(position));
        holder.price.setText(fullPrice.get(position));
        if(!half.get(position).isEmpty()){
            holder.available.setText("Half Plate Available");
        }else{
            holder.available.setText("Half Plate Not Available");
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(), "Item Clicked", Toast.LENGTH_SHORT).show();
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle("Important").setMessage("Do you want to delete this item??")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                               auth = FirebaseAuth.getInstance();
                               ref = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(auth.getUid());
                               ref.child("List of Dish").child(type).child(names.get(position)).removeValue();
                                fullPrice.remove(position);
                                names.remove(position);
                                auth = FirebaseAuth.getInstance();
                                notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }).create();

                builder.show();
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return names.size();
    }

    public static class DishAdapter extends RecyclerView.ViewHolder {
        TextView name,price,available;
        public DishAdapter(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.DishName);
            price = itemView.findViewById(R.id.pricePfDish);
            available = itemView.findViewById(R.id.availableOr);
        }
    }
}
