package com.consumers.fastwayadmin.Dish.SearchDishFastway;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.consumers.fastwayadmin.Dish.AddImageToDish;
import com.consumers.fastwayadmin.Dish.DishInfo;
import com.consumers.fastwayadmin.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SearchFastwayClass extends RecyclerView.Adapter<SearchFastwayClass.Holder> {
    List<String> dishName = new ArrayList<>();
    List<String> dishImage = new ArrayList<>();
    String dishType = "Veg";
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
            SharedPreferences sharedPreferences = v.getContext().getSharedPreferences("loginInfo", Context.MODE_PRIVATE);
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(sharedPreferences.getString("state","")).child(sharedPreferences.getString("locality","")).child(Objects.requireNonNull(auth.getUid())).child("List of Dish").child(dish);
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(v.getContext());
            alertDialog.setTitle("Important");
            alertDialog.setMessage("Enter Amount of half and full\nIf half not available leave empty");
            LinearLayout linearLayout = new LinearLayout(v.getContext());
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            EditText halfPrice = new EditText(v.getContext());
            EditText fullPrice = new EditText(v.getContext());
            EditText ownDishName = new EditText(v.getContext());
            EditText dishDescription = new EditText(v.getContext());
            RadioGroup radioGroup = new RadioGroup(v.getContext());
            radioGroup.setOrientation(LinearLayout.HORIZONTAL);
            RadioButton veg = new RadioButton(v.getContext());
            veg.setText("Veg");
            veg.setId(0);
            veg.setChecked(true);
            radioGroup.addView(veg);
            RadioButton vegan = new RadioButton(v.getContext());
            vegan.setText("Vegan");
            vegan.setId(0 + 1);
            radioGroup.addView(vegan);
            RadioButton NonVeg = new RadioButton(v.getContext());
            NonVeg.setText("NonVeg");
            NonVeg.setId(0 + 2);
            radioGroup.addView(NonVeg);

            radioGroup.setOnCheckedChangeListener((radioGroup1, i) -> {
                if(i == 0)
                    dishType = "Veg";
                else if(i == 0 + 1)
                    dishType = "Vegan";
                else
                    dishType = "NonVeg";
            });
            halfPrice.setHint("Enter Half price if available");
            fullPrice.setHint("Enter Full price (Mandatory)");
            ownDishName.setHint("Enter if you want your own dish name");
            dishDescription.setHint("Enter dish description");
            alertDialog.setPositiveButton("Create", (dialogInterface, i) -> {
                String dishNameToAdd = null;
                if(fullPrice.getText().toString().equals(""))
                    Toast.makeText(v.getContext(), "Full price can't be empty", Toast.LENGTH_SHORT).show();
                else{
                    String half = halfPrice.getText().toString();
                    String full = fullPrice.getText().toString();
                    if(ownDishName.getText().toString().equals("")) {
                        DishInfo info = new DishInfo(dishName.get(position), half, full, dishImage.get(position), "false", "0", "0", "0", "yes",dishDescription.getText().toString(),dishType);
                        reference.child(dishName.get(position)).setValue(info);
                        dishNameToAdd = dishName.get(position);
                    }else{
                        DishInfo info = new DishInfo(ownDishName.getText().toString(), half, full, dishImage.get(position), "false", "0", "0", "0", "yes",dishDescription.getText().toString(),dishType);
                        reference.child(ownDishName.getText().toString()).setValue(info);
                        dishNameToAdd = ownDishName.getText().toString();
                    }
                    Toast.makeText(v.getContext(), "Dish Added successfully", Toast.LENGTH_SHORT).show();
                }
                dialogInterface.dismiss();
                AlertDialog.Builder alert = new AlertDialog.Builder(v.getContext());
                alert.setTitle("Image");
                alert.setMessage("Choose one option from below");
                String finalDishNameToAdd = dishNameToAdd;
                alert.setPositiveButton("Add dish image", (dialogInterface12, i12) -> {
                    dialogInterface12.dismiss();
                    Intent intent = new Intent(v.getContext(), AddImageToDish.class);
                    intent.putExtra("type",dish);
                    intent.putExtra("dishName", finalDishNameToAdd);
                    v.getContext().startActivity(intent);
                }).setNegativeButton("Use Fastway Image", (dialogInterface1, i1) -> dialogInterface1.dismiss()).create();

                alert.show();
            });

            alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });

            linearLayout.addView(halfPrice);
            linearLayout.addView(fullPrice);
            linearLayout.addView(ownDishName);
            linearLayout.addView(radioGroup);
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
