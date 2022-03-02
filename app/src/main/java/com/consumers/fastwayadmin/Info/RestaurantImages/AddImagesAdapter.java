package com.consumers.fastwayadmin.Info.RestaurantImages;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.consumers.fastwayadmin.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AddImagesAdapter extends RecyclerView.Adapter<AddImagesAdapter.Holder> {
    List<String> resImages;

    public AddImagesAdapter(List<String> resImages) {
        this.resImages = resImages;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.images_res_card,parent,false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        Picasso.get().load(resImages.get(position)).into(holder.imageView, new Callback() {
            @Override
            public void onSuccess() {
                holder.progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onError(Exception e) {
                holder.progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    public int getItemCount() {
        return resImages.size();
    }
    public class Holder extends RecyclerView.ViewHolder{
        ImageView imageView;
        ProgressBar progressBar;
        public Holder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageViewCardViewRes);
            progressBar = itemView.findViewById(R.id.progressBarResImages);
        }
    }
}
