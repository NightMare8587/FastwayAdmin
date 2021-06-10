package com.consumers.fastwayadmin.DiscountCombo;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.consumers.fastwayadmin.R;
import com.developer.kalert.KAlertDialog;
import com.example.flatdialoglibrary.dialog.FlatDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.thecode.aestheticdialogs.AestheticDialog;
import com.thecode.aestheticdialogs.DialogAnimation;
import com.thecode.aestheticdialogs.DialogStyle;
import com.thecode.aestheticdialogs.DialogType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DiscountRecycler extends RecyclerView.Adapter<DiscountRecycler.Holder> {
    List<String> dishName = new ArrayList<>();
    Context context;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    DatabaseReference addToDB = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(Objects.requireNonNull(auth.getUid()));;
    DatabaseReference dis = FirebaseDatabase.getInstance().getReference().getRoot().child("Admin").child(Objects.requireNonNull(auth.getUid()));;
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(Objects.requireNonNull(auth.getUid()));;

    public DiscountRecycler(List<String> dishName,Context context){
        this.dishName = dishName;
        this.context = context;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.discount_card,parent,false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.textView.setText(dishName.get(position));
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new KAlertDialog(v.getContext(),KAlertDialog.NORMAL_TYPE)
                        .setTitleText("Confirmation")
                        .setContentText("Do you sure wanna apply offer's to this dish??")
                        .setConfirmText("Yes")
                        .setConfirmClickListener(new KAlertDialog.KAlertClickListener() {
                            @Override
                            public void onClick(KAlertDialog kAlertDialog) {
                                FlatDialog flatDialog1 = new FlatDialog(v.getContext());
                                flatDialog1.setCanceledOnTouchOutside(true);
                                flatDialog1.setOnCancelListener(new DialogInterface.OnCancelListener() {
                                    @Override
                                    public void onCancel(DialogInterface dialogInterface) {
                                        Toast.makeText(v.getContext(), "Cancelled", Toast.LENGTH_SHORT).show();
                                        dialogInterface.dismiss();
                                    }
                                });
                                flatDialog1.setTitle("Choose One Option")
                                        .setSubtitle("Only Applicable for items above 149")
                                        .setSubtitleColor(Color.BLACK)
                                        .setTitleColor(Color.BLACK)
                                        .setBackgroundColor(Color.parseColor("#f9fce1"))
                                        .setFirstButtonColor(Color.parseColor("#d3f6f3"))
                                        .setFirstButtonTextColor(Color.parseColor("#000000"))
                                        .setFirstButtonText("50% OFF")
                                        .setSecondButtonColor(Color.parseColor("#fee9b2"))
                                        .setSecondButtonTextColor(Color.parseColor("#000000"))
                                        .setSecondButtonText("40% OFF")
                                        .setThirdButtonText("ADD YOUR OWN")
                                        .setThirdButtonColor(Color.parseColor("#fbd1b7"))
                                        .setThirdButtonTextColor(Color.parseColor("#000000"))
                                        .setFirstTextFieldHint("Enter How much discount!!")
                                        .setFirstTextFieldBorderColor(Color.BLACK)
                                        .setFirstTextFieldHintColor(Color.BLACK)
                                        .withFirstButtonListner(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                fiftyDiscount();
                                                flatDialog1.dismiss();
                                            }
                                        })
                                        .withSecondButtonListner(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                fourtyDiscount();
                                                flatDialog1.dismiss();
                                            }
                                        })
                                        .withThirdButtonListner(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                if (flatDialog1.getFirstTextField().equals("")) {

                                                    Toast.makeText(v.getContext(), "Field Can't be Empty", Toast.LENGTH_SHORT).show();
                                                    return;
                                                } else {
                                                    customDiscount(flatDialog1.getFirstTextField());
                                                    flatDialog1.dismiss();
                                                }
                                            }
                                        }).show();
                                kAlertDialog.dismissWithAnimation();
                            }
                        }).setCancelText("No")
                        .setCancelClickListener(new KAlertDialog.KAlertClickListener() {
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
        return dishName.size();
    }

    public static class Holder extends RecyclerView.ViewHolder{
        TextView textView;
        CardView cardView;
        public Holder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.discountViewDishName);
            cardView = itemView.findViewById(R.id.discountCard);
        }
    }

    private void customDiscount(String firstTextField) {
        auth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(Objects.requireNonNull(auth.getUid()));
        reference.child("List of Dish").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        if (String.valueOf(dataSnapshot1.child("mrp").getValue()).equals("no")) {
                            String type = String.valueOf(dataSnapshot.getKey());
                            String dishName = String.valueOf(dataSnapshot1.child("name").getValue());
                            if (Integer.parseInt(Objects.requireNonNull(String.valueOf(dataSnapshot1.child("full").getValue()))) >= 149) {
                                int price = Integer.parseInt(Objects.requireNonNull(String.valueOf(dataSnapshot1.child("full").getValue())));
                                int discount = Integer.parseInt(firstTextField);
                                int afterDis = price - (price * discount / 100);
                                beforeDiscount(price,afterDis,discount,type,dishName);
                                addToDiscountDatabase("yes");
                                auth = FirebaseAuth.getInstance();
                                Log.i("type",type);
                                Log.i("name",dishName);
//                                reference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(auth.getUid()).child("List of Dish");
                                reference.child("List of Dish").child(type).child(dishName).child("full").setValue(afterDis);
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void fourtyDiscount() {
        auth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(Objects.requireNonNull(auth.getUid()));
        reference.child("List of Dish").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        if (String.valueOf(dataSnapshot1.child("mrp").getValue()).equals("no")) {
                            String type = String.valueOf(dataSnapshot.getKey());
                            String dishName = String.valueOf(dataSnapshot1.child("name").getValue());
                            if (Integer.parseInt(Objects.requireNonNull(String.valueOf(dataSnapshot1.child("full").getValue()))) >= 149) {
                                int price = Integer.parseInt(Objects.requireNonNull(String.valueOf(dataSnapshot1.child("full").getValue())));
                                int discount = 40;
                                int afterDis = price - (price * discount / 100);
                                beforeDiscount(price,afterDis,discount,type,dishName);
                                addToDiscountDatabase("yes");
                                auth = FirebaseAuth.getInstance();
                                Log.i("type",type);
                                Log.i("name",dishName);
                                reference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(Objects.requireNonNull(auth.getUid()));
                                reference.child("List of Dish").child(type).child(dishName).child("full").setValue(afterDis);
                            }
                        }
                    }
                }
                AestheticDialog.Builder builder = new AestheticDialog.Builder((Activity) context, DialogStyle.FLAT, DialogType.SUCCESS);
                builder.setTitle("Applying Discount")
                        .setMessage("Wait while we are applying discount :)")
                        .setCancelable(false)
                        .setDuration(3000)
                        .setAnimation(DialogAnimation.SHRINK)
                        .setDarkMode(true);

                builder.show();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        builder.dismiss();
                    }
                },3000);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void addToDiscountDatabase(String discount) {
        auth = FirebaseAuth.getInstance();
        addToDB = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(Objects.requireNonNull(auth.getUid()));
        addToDB.child("Discount").child("available").setValue("yes");
    }

    private void beforeDiscount(int price,int after, int discount,String type,String name) {
        DisInfo disInfo = new DisInfo(String.valueOf(price),String.valueOf(after),String.valueOf(discount));
        dis = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(Objects.requireNonNull(auth.getUid()));
        dis.child("List of Dish").child(type).child(name).child("Discount").child(name).setValue(disInfo);
    }


    private void fiftyDiscount() {
        auth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(Objects.requireNonNull(auth.getUid()));
        reference.child("List of Dish").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        if (String.valueOf(dataSnapshot1.child("mrp").getValue()).equals("no")) {
                            String type = String.valueOf(dataSnapshot.getKey());
                            String dishName = String.valueOf(dataSnapshot1.child("name").getValue());
                            if (Integer.parseInt(Objects.requireNonNull(String.valueOf(dataSnapshot1.child("full").getValue()))) >= 149) {
                                int price = Integer.parseInt(Objects.requireNonNull(String.valueOf(dataSnapshot1.child("full").getValue())));
                                int discount = 50;
                                int afterDis = price - (price * discount / 100);
                                beforeDiscount(price,afterDis,discount,type,dishName);
                                addToDiscountDatabase("yes");
                                auth = FirebaseAuth.getInstance();
                                Log.i("type",type);
                                Log.i("name",dishName);
//                                reference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(auth.getUid()).child("List of Dish");
                                reference.child("List of Dish").child(type).child(dishName).child("full").setValue(afterDis);
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
