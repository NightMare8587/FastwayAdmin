package com.consumers.fastwayadmin.Dish.SearchDishFastway;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.consumers.fastwayadmin.Dish.DishInfo;
import com.consumers.fastwayadmin.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SearchFastwayClass extends RecyclerView.Adapter<SearchFastwayClass.Holder> {
    List<String> dishName = new ArrayList<>();
    List<String> dishImage = new ArrayList<>();
    String dish;

    public SearchFastwayClass(List<String> dishName, List<String> dishImage,String dish) {
        this.dishName = dishName;
        this.dishImage = dishImage;
        this.dish = dish;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.display_dish,parent,false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, @SuppressLint("RecyclerView") int position) {
        holder.name.setText(dishName.get(position));
        Picasso.get().load(dishImage.get(position)).centerCrop().resize(100,100).into(holder.imageView);
        holder.cardView.setOnClickListener(v -> {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(Objects.requireNonNull(auth.getUid())).child("List of Dish").child(dish);
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(v.getContext());
            alertDialog.setTitle("Important");
            alertDialog.setMessage("Enter Amount of half and full\nIf half not available leave empty");
            LinearLayout linearLayout = new LinearLayout(v.getContext());
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            EditText halfPrice = new EditText(v.getContext());
            EditText fullPrice = new EditText(v.getContext());
            halfPrice.setHint("Enter Half price if available");
            fullPrice.setHint("Enter Full price (Mandatory)");
            alertDialog.setPositiveButton("Create", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if(fullPrice.getText().toString().equals(""))
                        Toast.makeText(v.getContext(), "Full price can't be empty", Toast.LENGTH_SHORT).show();
                    else{
                        String half = halfPrice.getText().toString();
                        String full = fullPrice.getText().toString();
                        DishInfo info = new DishInfo(dishName.get(position),half,full,dishImage.get(position),"false","0","0","0","yes");
                        reference.child(dishName.get(position)).setValue(info);
                        Toast.makeText(v.getContext(), "Dish Added successfully", Toast.LENGTH_SHORT).show();
                    }
                    dialogInterface.dismiss();
                }
            });

            alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });

            linearLayout.addView(halfPrice);
            linearLayout.addView(fullPrice);

            alertDialog.setView(linearLayout);

            alertDialog.create().show();
        });
    }

    @Override
    public int getItemCount() {
        return dishName.size();
    }

    public class Holder extends RecyclerView.ViewHolder{
        TextView name;
        ImageView imageView;
        CardView cardView;
        public Holder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.displayDishName);
            imageView = itemView.findViewById(R.id.displayImage);
            cardView = itemView.findViewById(R.id.displayDishCardView);
        }
    }
}
