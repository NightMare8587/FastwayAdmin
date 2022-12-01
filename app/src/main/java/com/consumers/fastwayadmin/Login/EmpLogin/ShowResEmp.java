package com.consumers.fastwayadmin.Login.EmpLogin;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.consumers.fastwayadmin.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ShowResEmp extends RecyclerView.Adapter<ShowResEmp.Holder> {
    List<String> name = new ArrayList<>();
    List<String> address = new ArrayList<>();
    List<String> resID = new ArrayList<>();
    List<String> resImage = new ArrayList<>();

    public ShowResEmp(List<String> name, List<String> address, List<String> resID, List<String> resImage) {
        this.name = name;
        this.address = address;
        this.resID = resID;
        this.resImage = resImage;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.showres_emplayout,parent,false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        Picasso.get().load(resImage.get(position)).into(holder.imageView);
        holder.address.setText(address.get(position));
        holder.name.setText(name.get(position));
        holder.cardView.setOnClickListener(click -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(click.getContext());
            builder.setTitle("Dialog").setMessage("Do you sure wanna request to join this restaurant")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            FirebaseAuth auth = FirebaseAuth.getInstance();
                            SharedPreferences loginInfo = click.getContext().getSharedPreferences("loginInfo", Context.MODE_PRIVATE);
                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Admin").child(resID.get(position));
                            databaseReference.child("Restaurant Staff").child(auth.getUid()).child("name").setValue(loginInfo.getString("name",""));

                            Toast.makeText(click.getContext(), "Request Submitted Successfully", Toast.LENGTH_SHORT).show();
//                            databaseReference.child("Restaurant Staff").child(auth.getUid()).child("phone").setValue(loginInfo.getString("phone",""));
                        }
                    }).setNegativeButton("Wait", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    }).create();
            builder.show();
        });
    }

    @Override
    public int getItemCount() {
        return resID.size();
    }
    
    public class Holder extends RecyclerView.ViewHolder{
        ImageView imageView;
        TextView name,address;
        CardView cardView;
        public Holder(@NonNull View itemView) {
            super(itemView);
            
            imageView = itemView.findViewById(R.id.resAdpEmpImage);
            name = itemView.findViewById(R.id.resNameAdpEmp);
            address = itemView.findViewById(R.id.resAddressAdpEmp);
            cardView = itemView.findViewById(R.id.resAdpCardEmp);
        }
    }
}
