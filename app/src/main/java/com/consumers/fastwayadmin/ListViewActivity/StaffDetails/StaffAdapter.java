package com.consumers.fastwayadmin.ListViewActivity.StaffDetails;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.consumers.fastwayadmin.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class StaffAdapter extends RecyclerView.Adapter<StaffAdapter.Holder> {
    List<String> name;
    List<String> image;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    List<String> UUID;
    Context context;
    public StaffAdapter(List<String> name, List<String> image,Context context,List<String> UUID) {
        this.name = name;
        this.image = image;
        this.UUID = UUID;
        this.context = context;
    }


    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.restaurant_staff_card_view,parent,false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, @SuppressLint("RecyclerView") int position) {
        holder.name.setText(name.get(position));
        holder.cardView.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
            builder.setTitle("Dialog").setMessage("Approve this new staff ?")
                    .setPositiveButton("Yes Approve", (dialogInterface, i) -> {
                        dialogInterface.dismiss();
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Admin").child(auth.getUid()).child("Registered Staff").child(UUID.get(position));
                        databaseReference.child("name").setValue(name.get(position));
                         databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Admin").child(auth.getUid()).child("Restaurant Staff").child(UUID.get(position));
                         databaseReference.removeValue();

                        SharedPreferences resInfo = view.getContext().getSharedPreferences("RestaurantInfo",Context.MODE_PRIVATE);
                        HashMap<String,String> map = new HashMap<>();
                        map.put("resName",resInfo.getString("hotelName",""));
                        map.put("resAddress",resInfo.getString("hotelAddress",""));
                        map.put("resContact",resInfo.getString("hotelNumber",""));

                        databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("EmployeeDB").child(UUID.get(position)).child("ResDetails");
                        databaseReference.setValue(map);
                        Toast.makeText(context, "New Staff Added", Toast.LENGTH_SHORT).show();
                    }).setNeutralButton("No, Remove", (dialogInterface, i) -> {
                        dialogInterface.dismiss();
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Admin").child(auth.getUid()).child("Restaurant Staff").child(UUID.get(position));
                        databaseReference.removeValue();
                        Toast.makeText(context, "New Staff Removed", Toast.LENGTH_SHORT).show();
                    });
            builder.create().show();
        });
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
        CardView cardView;
        public Holder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.restaurantStaffCardName);
            progressBar = itemView.findViewById(R.id.progressBarRestaurantStaffCard);
            imageView = itemView.findViewById(R.id.restaurantStaffCardImageView);
            cardView = itemView.findViewById(R.id.restaurantStaffCardViewHolder);
        }
    }
    private void askForBank(){

    }
}
