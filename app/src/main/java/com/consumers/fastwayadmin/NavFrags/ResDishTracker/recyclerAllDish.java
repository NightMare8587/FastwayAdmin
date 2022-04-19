package com.consumers.fastwayadmin.NavFrags.ResDishTracker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.consumers.fastwayadmin.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

public class recyclerAllDish extends RecyclerView.Adapter<recyclerAllDish.holder> {
    List<String> dishNames;
    List<String> dishTotalCount;
    Context context;

    public recyclerAllDish(List<String> dishNames, List<String> dishTotalCount,Context context) {
        this.dishNames = dishNames;
        this.context = context;
        this.dishTotalCount = dishTotalCount;
    }

    @NonNull
    @Override
    public holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.dish_tracker_card_view,parent,false);
        return new holder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull holder holder, int position) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("storeImages",Context.MODE_PRIVATE);
        if(sharedPreferences.contains(dishNames.get(position))){
            Picasso.get().load(sharedPreferences.getString(dishNames.get(position),"")).into(holder.imageView, new Callback() {
                @Override
                public void onSuccess() {
                    holder.progressBar.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onError(Exception e) {
                    holder.progressBar.setVisibility(View.INVISIBLE);
                }
            });
        }else
            holder.progressBar.setVisibility(View.INVISIBLE);
        holder.dishName.setText(dishNames.get(position));
        holder.totalCount.setText("Times Ordered\n" + dishTotalCount.get(position));
    }

    @Override
    public int getItemCount() {
        return dishNames.size();
    }
    public class holder extends RecyclerView.ViewHolder{
        ImageView imageView;
        TextView dishName,totalCount;
        ProgressBar progressBar;
        public holder(@NonNull View itemView) {
            super(itemView);
            dishName = itemView.findViewById(R.id.dishNameTrackerAnalysisRecycler);
            totalCount = itemView.findViewById(R.id.totalPurchaseCountDishRecycler);
            imageView = itemView.findViewById(R.id.dishTrackerCardImageView);
            progressBar = itemView.findViewById(R.id.reccyclerResDishProgress);
        }
    }
}
