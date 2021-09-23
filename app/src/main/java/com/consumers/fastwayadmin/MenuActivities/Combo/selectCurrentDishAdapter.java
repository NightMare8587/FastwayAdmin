package com.consumers.fastwayadmin.MenuActivities.Combo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.consumers.fastwayadmin.R;
import com.developer.kalert.KAlertDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class selectCurrentDishAdapter extends RecyclerView.Adapter<selectCurrentDishAdapter.holder> {
        List<String> name = new ArrayList<>();
        List<String> image = new ArrayList<>();
        Context context;
        String comboName;
        String state;

public selectCurrentDishAdapter(List<String> name, List<String> image,Context context,String comboName,String state) {
        this.name = name;
        this.image = image;
        this.context = context;
        this.state = state;
        this.comboName = comboName;
        }

@NonNull
@Override
public holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.select_dish_card,parent,false);
        return new holder(view);
        }

@Override
public void onBindViewHolder(@NonNull holder holder, @SuppressLint("RecyclerView") int position) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(state).child(Objects.requireNonNull(auth.getUid())).child("List of Dish");
        holder.nameOfDish.setText(name.get(position));
        if(!image.get(position).equals("")){
        Picasso.get().load(image.get(position)).centerCrop().resize(100,100).into(holder.imageView);
        }
        holder.nameOfDish.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
                new KAlertDialog(context,KAlertDialog.WARNING_TYPE)
                        .setTitleText("Add To Combo")
                        .setContentText("You sure wanna add this to combo")
                        .setConfirmText("Yes, Add it")
                        .setConfirmClickListener(new KAlertDialog.KAlertClickListener() {
        @Override
        public void onClick(KAlertDialog kAlertDialog) {
                reference.child("Combo").child(comboName).child(name.get(position)).child("name").child("name").setValue(name.get(position));
                kAlertDialog.dismissWithAnimation();
                }
                }).setCancelText("No, Wait").setCancelClickListener(new KAlertDialog.KAlertClickListener() {
        @Override
        public void onClick(KAlertDialog kAlertDialog) {
                kAlertDialog.dismissWithAnimation();
        }
                }).show();
                 }
                     });
                }

        @Override
        public int getItemCount() {
                return name.size();
                }

public static class holder extends RecyclerView.ViewHolder{
    TextView nameOfDish;
    ImageView imageView;
    public holder(@NonNull View itemView) {
        super(itemView);
        nameOfDish = itemView.findViewById(R.id.comboTextView);
        imageView = itemView.findViewById(R.id.comboImgaeView);
    }
}
}

