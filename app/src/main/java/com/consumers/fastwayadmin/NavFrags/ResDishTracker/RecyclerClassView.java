package com.consumers.fastwayadmin.NavFrags.ResDishTracker;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.consumers.fastwayadmin.R;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        SharedPreferences dishShared = context.getSharedPreferences("DishOrderedWithOthers",MODE_PRIVATE);
        SharedPreferences sharedPreferences = context.getSharedPreferences("storeImages", MODE_PRIVATE);
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
            Picasso.get().load("https://image.shutterstock.com/image-vector/no-image-vector-isolated-on-600w-1481369594.jpg").into(holder.imageView);
        }

        if(dishShared.contains(dishNames.get(position))) {
            holder.clickToShow.setVisibility(View.VISIBLE);
        }

        holder.cardView.setOnClickListener(click -> {
            if(dishShared.contains(dishNames.get(position))) {
                java.lang.reflect.Type type1 = new TypeToken<HashMap<String, Integer>>() {
                }.getType();
                Gson gson = new Gson();
                HashMap<String, Integer> myMap = gson.fromJson(dishShared.getString(dishNames.get(position), ""), type1);
                List<String> dishNamesList = new ArrayList<>();
                List<String> dishQuanList = new ArrayList<>();

                for (Map.Entry<String, Integer> map : myMap.entrySet()) {
                    if (!map.getKey().equals(dishNames.get(position))) {
                        dishNamesList.add(map.getKey());
                        dishQuanList.add(String.valueOf(map.getValue()));
                    }
                }

                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view = inflater.inflate(R.layout.res_info_dialog_layout, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Detailed Info").setMessage("Showing dish ordered with this together ( " + dishNames.get(position) + " )");


                ListView listView = view.findViewById(R.id.listDishNamesResInfo);

                ListView listView1 = view.findViewById(R.id.listDishNamesQuantityInfo);

                ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, dishNamesList);
                ArrayAdapter<String> adapter1 = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, dishQuanList);

                listView1.setAdapter(adapter1);
                listView.setAdapter(adapter);

                builder.setView(view);
//                builder.setItems(array, (dialogInterface, i) -> Log.i("list",array.toString()));
                builder.setPositiveButton("exit", (dialogInterface, i) -> dialogInterface.dismiss()).create().show();
            }
        });


        holder.dishName.setText(dishNames.get(position));
        holder.totalCount.setText("Times Ordered: " + dishTotalCount.get(position));
    }

    @Override
    public int getItemCount() {
        return Math.min(dishNames.size(), 2);
    }
    public class holder extends RecyclerView.ViewHolder{
        ImageView imageView;
        TextView dishName,totalCount,clickToShow;
        ProgressBar progressBar;
        CardView cardView;
        public holder(@NonNull View itemView) {
            super(itemView);
            dishName = itemView.findViewById(R.id.dishNameTrackerAnalysisRecycler);
            totalCount = itemView.findViewById(R.id.totalPurchaseCountDishRecycler);
            imageView = itemView.findViewById(R.id.dishTrackerCardImageView);
            clickToShow = itemView.findViewById(R.id.clickToShowMoreDetailed);
            progressBar = itemView.findViewById(R.id.reccyclerResDishProgress);
            cardView = itemView.findViewById(R.id.dishTrackerCardViewLayout);
        }
    }
}
