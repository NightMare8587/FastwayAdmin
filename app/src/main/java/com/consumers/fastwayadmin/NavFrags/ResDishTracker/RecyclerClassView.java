package com.consumers.fastwayadmin.NavFrags.ResDishTracker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
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

public class RecyclerClassView extends RecyclerView.Adapter<RecyclerClassView.holder> {
    List<String> dishNames;
    List<Integer> dishTotalCount;
    Context context;

    public RecyclerClassView(List<String> dishNames, List<Integer> dishTotalCount,Context context) {
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
            Log.i("infose",sharedPreferences.getString(dishNames.get(position),""));
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
        }else{
            holder.progressBar.setVisibility(View.INVISIBLE);
        }

        holder.dishName.setText(dishNames.get(position));
        holder.totalCount.setText("Times Ordered: " + dishTotalCount.get(position));
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
