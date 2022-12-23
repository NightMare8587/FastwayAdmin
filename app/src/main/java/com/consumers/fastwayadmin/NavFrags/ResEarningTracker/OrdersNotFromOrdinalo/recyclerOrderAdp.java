package com.consumers.fastwayadmin.NavFrags.ResEarningTracker.OrdersNotFromOrdinalo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.consumers.fastwayadmin.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class recyclerOrderAdp extends RecyclerView.Adapter<recyclerOrderAdp.Holder> {
    List<String> dishName = new ArrayList<>();
    List<String> dishImage = new ArrayList<>();
    List<String> dishType = new ArrayList<>();
    List<String> menuType = new ArrayList<>();

    public recyclerOrderAdp(List<String> dishName, List<String> dishImage, List<String> dishType, List<String> menuType) {
        this.dishName = dishName;
        this.dishImage = dishImage;
        this.dishType = dishType;
        this.menuType = menuType;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_orders_cardview,parent,false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        Picasso.get().load(dishImage.get(position)).into(holder.imageView);
        holder.dishName.setText(dishName.get(position));
        holder.dishType.setText(menuType.get(position) + " " + dishType.get(position));

        holder.cardView.setOnClickListener(click -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(click.getContext());
            builder.setTitle("Add").setMessage("Add this dish ?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    Intent intent = new Intent("sendback-dishdetails");
                    intent.putExtra("dishName",dishName.get(position));
                    intent.putExtra("dishImage",dishImage.get(position));
                    intent.putExtra("dishType",dishType.get(position));
                    intent.putExtra("menuType",menuType.get(position));

                    LocalBroadcastManager.getInstance(click.getContext()).sendBroadcast(intent);
                }
            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            }).create();
            builder.show();

        });
    }

    @Override
    public int getItemCount() {
        return dishName.size();
    }

    public class Holder extends RecyclerView.ViewHolder{
        ImageView imageView;
        TextView dishName,dishType;
        CardView cardView;
        public Holder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageViewAddOrdersCard);
            dishName = itemView.findViewById(R.id.dishNameAddOrders);
            cardView = itemView.findViewById(R.id.addOrdersCardViewID);
            dishType = itemView.findViewById(R.id.dishMenuTypeAddOrders);
        }
    }
}
