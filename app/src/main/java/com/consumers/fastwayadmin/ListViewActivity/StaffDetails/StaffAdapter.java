package com.consumers.fastwayadmin.ListViewActivity.StaffDetails;

import android.text.Layout;
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

public class StaffAdapter extends RecyclerView.Adapter<StaffAdapter.Holder> {
    List<String> name;
    List<String> image;
    public StaffAdapter(List<String> name, List<String> image) {
        this.name = name;
        this.image = image;
    }


    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.restaurant_staff_card_view,parent,false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.name.setText(name.get(position));
        if(!image.get(position).equals("")) {
            Picasso.get().load(image.get(position)).into(holder.imageView, new Callback() {
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
    }

    @Override
    public int getItemCount() {
        return name.size();
    }
    public class Holder extends RecyclerView.ViewHolder{
        TextView name;
        ProgressBar progressBar;
        ImageView imageView;
        public Holder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.restaurantStaffCardName);
            progressBar = itemView.findViewById(R.id.progressBarRestaurantStaffCard);
            imageView = itemView.findViewById(R.id.restaurantStaffCardImageView);
        }
    }
}
