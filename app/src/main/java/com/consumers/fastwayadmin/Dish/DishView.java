package com.consumers.fastwayadmin.Dish;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.consumers.fastwayadmin.MenuActivities.EditMenu;
import com.consumers.fastwayadmin.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DishView extends RecyclerView.Adapter<DishView.DishAdapter> {
    List<String> names = new ArrayList<String>();
    List<String> fullPrice = new ArrayList<String>();
    List<String> half = new ArrayList<String>();
    List<String> image = new ArrayList<>();
    List<String> before = new ArrayList<>();
    List<String> after = new ArrayList<>();
    List<String> discount = new ArrayList<>();
    FirebaseAuth auth;
    DatabaseReference ref;
    String main = "https://pixabay.com/api/?";
    String type;
    public DishView(List<String> names,List<String> full,List<String> half,String type,List<String> image,List<String> before,List<String> after,List<String> discount){
        this.fullPrice = full;
        this.half = half;
        this.names = names;
        this.before = before;
        this.after = after;
        this.discount = discount;
        this.type = type;
        this.image = image;
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
        if(!before.get(position).equals("")){
            holder.price.setText("\u20B9" + before.get(position));
            holder.price.setPaintFlags(holder.price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.discountPrice.setText("\u20B9" + after.get(position));
            holder.discountPrice.setVisibility(View.VISIBLE);
        }else{
            holder.price.setText("\u20B9" + fullPrice.get(position));
            holder.discountPrice.setVisibility(View.INVISIBLE);
        }
        if(!image.get(position).equals(""))
        Picasso.get().load(image.get(position)).centerCrop().resize(100,100).into(holder.imageView);
        if(!half.get(position).isEmpty()){
            holder.available.setText("Half Plate Available");
        }else{
            holder.available.setText("Half Plate Not Available");
        }

//        Uri uri =  Uri.parse(main)
//                .buildUpon()
//                .appendQueryParameter("key","20026873-a33ddc46878d2d8c75280a432")
//                .appendQueryParameter("q",names.get(position))
//                .appendQueryParameter("image_type","photo")
//                .build();
//
//        Log.i("info",uri.toString());
//

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), EditMenu.class);
                intent.putExtra("type",type);
                intent.putExtra("dish",names.get(position));
                view.getContext().startActivity(intent);
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
                               ref = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(Objects.requireNonNull(auth.getUid()));
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
        TextView name,price,available,discountPrice;
        ImageView imageView;

        public DishAdapter(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.displayDishImage);
            name = itemView.findViewById(R.id.DishName);
            price = itemView.findViewById(R.id.pricePfDish);
            available = itemView.findViewById(R.id.availableOr);
            discountPrice = itemView.findViewById(R.id.discountedPrice);
        }
    }
}
