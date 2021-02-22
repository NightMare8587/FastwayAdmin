package com.example.fastwayadmin.MenuActivities;

import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fastwayadmin.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class DisplayDish extends RecyclerView.Adapter<DisplayDish.Adapter> {

    List<String> names = new ArrayList<>();
    List<String> image = new ArrayList<>();

    public DisplayDish(List<String> names,List<String> image){
        this.image = image;
        this.names = names;
    }

    @NonNull
    @Override
    public Adapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.display_dish,parent,false);
        return new Adapter(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Adapter holder, int position) {
        
        holder.name.setText(names.get(position));
        Picasso.get().load(image.get(position)).centerCrop().resize(100,100).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return names.size();
    }

    public static class Adapter extends RecyclerView.ViewHolder{
        TextView name;
        ImageView imageView;
        public Adapter(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.displayDishName);
            imageView = itemView.findViewById(R.id.displayImage);
        }
    }
}
