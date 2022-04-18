package com.consumers.fastwayadmin.NavFrags.ResDishTracker;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.consumers.fastwayadmin.R;

import java.util.List;

public class RecyclerClassView extends RecyclerView.Adapter<RecyclerClassView.holder> {
    List<String> dishNames;
    List<String> dishTotalCount;

    public RecyclerClassView(List<String> dishNames, List<String> dishTotalCount) {
        this.dishNames = dishNames;
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
        holder.dishName.setText(dishNames.get(position));
        holder.totalCount.setText("Times Ordered\n" + dishTotalCount.get(position));
    }

    @Override
    public int getItemCount() {
        if(dishNames.size() >= 2)
            return 2;
        else
            return dishNames.size();
    }
    public class holder extends RecyclerView.ViewHolder{
        ImageView imageView;
        TextView dishName,totalCount;
        public holder(@NonNull View itemView) {
            super(itemView);
            dishName = itemView.findViewById(R.id.dishNameTrackerAnalysisRecycler);
            totalCount = itemView.findViewById(R.id.totalPurchaseCountDishRecycler);
            imageView = itemView.findViewById(R.id.dishTrackerCardImageView);
        }
    }
}
