package com.consumers.fastwayadmin.MenuActivities.Combo;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.consumers.fastwayadmin.Dish.AddImageToDish;
import com.consumers.fastwayadmin.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ComboRecyclerView extends RecyclerView.Adapter<ComboRecyclerView.Holder> {
    List<String> comboName;
    List<List<String>> dishName;
    List<String> price;
    List<String> comboImage;
    Context context;

    public ComboRecyclerView(List<String> comboName, List<List<String>> dishName, List<String> price, Context context,List<String> comboImage) {
        this.comboName = comboName;
        this.comboImage = comboImage;
        this.dishName = dishName;
        this.price = price;
        this.context = context;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.combo_menu_layout,parent,false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, @SuppressLint("RecyclerView") int position) {
        holder.comboName.setText(comboName.get(position));
        holder.price.setText(price.get(position));
        List<String> current = new ArrayList<>(dishName.get(position));
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1,current);
        holder.listView.setAdapter(arrayAdapter);
        ViewGroup.LayoutParams param = holder.listView.getLayoutParams();
        param.height = 150*current.size();
        holder.listView.setLayoutParams(param);
        holder.imageView.setOnClickListener(click -> {
            AlertDialog.Builder alert = new AlertDialog.Builder(click.getContext());
            alert.setTitle("Image");
            alert.setMessage("Choose one option from below");
            alert.setPositiveButton("Add/Upload New Image", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    Intent intent = new Intent(click.getContext(), AddImageToDish.class);
                    intent.putExtra("type","Combo");
                    intent.putExtra("dishName",comboName.get(position));
                    click.getContext().startActivity(intent);

                }
            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
dialogInterface.dismiss();
                }
            });
            alert.create().show();
        });
        if(!comboImage.get(position).equals(""))
        Picasso.get().load(comboImage.get(position)).resize(200,200).centerCrop().into(holder.imageView);
        else
            Picasso.get().load("https://image.shutterstock.com/image-vector/no-image-vector-isolated-on-600w-1481369594.jpg").centerCrop().resize(100, 100).into(holder.imageView);
        holder.listView.requestLayout();
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(v.getContext());
                alert.setTitle("Choose one option");
                alert.setPositiveButton("Add/Remove Items", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(v.getContext(),AddRemoveItemCombo.class);
                        intent.putExtra("name",comboName.get(position));
                        intent.putStringArrayListExtra("dishName", (ArrayList<String>) dishName.get(position));
                        v.getContext().startActivity(intent);
                        dialog.dismiss();
                    }
                }).setNegativeButton("Change Price", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create().show();
            }
        });
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                FirebaseAuth auth = FirebaseAuth.getInstance();
                SharedPreferences sharedPreferences = buttonView.getContext().getSharedPreferences("loginInfo",Context.MODE_PRIVATE);
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Restaurants").child(sharedPreferences.getString("state","")).child(Objects.requireNonNull(auth.getUid())).child("List of Dish").child("Combo");
                if(isChecked){
                    databaseReference.child(comboName.get(position)).child("enable").setValue("yes");
                    holder.checkBox.setText("Enabled");
                }else{
                    databaseReference.child(comboName.get(position)).child("enable").setValue("no");
                    holder.checkBox.setText("Disabled");
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return comboName.size();
    }

    protected class Holder extends RecyclerView.ViewHolder{
        TextView comboName,price;
        ListView listView;
        CheckBox checkBox;
        ImageView imageView;
        CardView cardView;
        public Holder(@NonNull View itemView) {
            super(itemView);
            comboName = itemView.findViewById(R.id.comboMenuDishName);
            price = itemView.findViewById(R.id.comboMenuPriceView);
            listView = itemView.findViewById(R.id.comboDishListViewMenu);
            checkBox = itemView.findViewById(R.id.checkBox);
            imageView = itemView.findViewById(R.id.ComboImageView);
            cardView = itemView.findViewById(R.id.comboAdCard);
        }
    }
}
