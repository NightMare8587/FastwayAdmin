package com.consumers.fastwayadmin.DiscountCombo;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class DiscountRecycler extends RecyclerView.Adapter<DiscountRecycler.Holder> {
    List<String> name;
    Context context;

    SharedPreferences sharedPreferences;
    HashMap<String,String> dishName;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    DatabaseReference addToDB = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(Objects.requireNonNull(auth.getUid()));
    DatabaseReference dis = FirebaseDatabase.getInstance().getReference().getRoot().child("Admin").child(Objects.requireNonNull(auth.getUid()));
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(Objects.requireNonNull(auth.getUid()));
    public DiscountRecycler(HashMap<String,String> dishName,Context context,List<String> name){
        this.dishName = dishName;
        this.context = context;
        this.name = name;
    }
    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.discount_card,parent,false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, @SuppressLint("RecyclerView") int position) {
        holder.textView.setText(name.get(position));
        holder.cardView.setOnClickListener(v -> {
            Log.i("info",dishName.get(name.get(position)) + " " + name.get(position));
            sharedPreferences = v.getContext().getSharedPreferences("loginInfo",Context.MODE_PRIVATE);
            new KAlertDialog(v.getContext(),KAlertDialog.NORMAL_TYPE)
                    .setTitleText("Confirmation")
                    .setContentText("Do you sure wanna apply offer's to this dish??")
                    .setConfirmText("Yes")
                    .setConfirmClickListener(kAlertDialog -> {
                        FlatDialog flatDialog1 = new FlatDialog(v.getContext());
                        flatDialog1.setCanceledOnTouchOutside(true);
                        flatDialog1.setOnCancelListener(dialogInterface -> {
                            Toast.makeText(v.getContext(), "Cancelled", Toast.LENGTH_SHORT).show();
                            dialogInterface.dismiss();
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
                                .setFirstTextFieldTextColor(Color.BLACK)
                                .withFirstButtonListner(view -> {
                                    fiftyDiscount(dishName.get(name.get(position)),name.get(position));
                                    flatDialog1.dismiss();
                                })
                                .withSecondButtonListner(view -> {
                                    fourtyDiscount(dishName.get(name.get(position)),name.get(position));
                                    flatDialog1.dismiss();
                                })
                                .withThirdButtonListner(view -> {
                                    if (flatDialog1.getFirstTextField().equals("")) {

                                        Toast.makeText(v.getContext(), "Field Can't be Empty", Toast.LENGTH_SHORT).show();
                                    } else {
                                        customDiscount(dishName.get(name.get(position)),name.get(position),flatDialog1.getFirstTextField());
                                        flatDialog1.dismiss();
                                    }
                                }).show();
                        kAlertDialog.dismissWithAnimation();
                    }).setCancelText("No")
                    .setCancelClickListener(KAlertDialog::dismissWithAnimation).show();
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

    private void customDiscount(String field, String textField, String firstTextField) {
        auth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(sharedPreferences.getString("state","")).child(Objects.requireNonNull(auth.getUid()));
        reference.child("List of Dish").child(field).child(textField).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot1) {

                        if (String.valueOf(dataSnapshot1.child("mrp").getValue()).equals("no")) {
                            String type = String.valueOf(dataSnapshot1.getKey());
                            String dishName = String.valueOf(dataSnapshot1.child("name").getValue());
                            if (Integer.parseInt(Objects.requireNonNull(String.valueOf(dataSnapshot1.child("full").getValue()))) >= 149) {
                                if(!String.valueOf(dataSnapshot1.child("half").getValue()).equals("")) {
                                    int price = Integer.parseInt(Objects.requireNonNull(String.valueOf(dataSnapshot1.child("full").getValue())));
                                    int halfPrice = Integer.parseInt(Objects.requireNonNull(String.valueOf(dataSnapshot1.child("half").getValue())));
                                    int discount = Integer.parseInt(firstTextField);
                                    int afterDis = price - (price * discount / 100);
                                    int afterDisHalf = halfPrice - (halfPrice * discount / 100);
                                    beforeDiscount(price, afterDis, discount, field, textField, halfPrice);
                                    addToDiscountDatabase("" + discount);
                                    auth = FirebaseAuth.getInstance();
                                    Log.i("type", type);
                                    Log.i("name", dishName);
//                                reference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(auth.getUid()).child("List of Dish");
                                    reference.child("List of Dish").child(field).child(textField).child("full").setValue(afterDis);
                                    reference.child("List of Dish").child(field).child(textField).child("half").setValue(afterDisHalf);
                                }else{
                                    int price = Integer.parseInt(Objects.requireNonNull(String.valueOf(dataSnapshot1.child("full").getValue())));
                                    int discount = Integer.parseInt(firstTextField);
                                    int afterDis = price - (price * discount / 100);
                                    beforeDiscount(price,afterDis,discount,type,dishName,0);
                                    addToDiscountDatabase("" + discount);
                                    auth = FirebaseAuth.getInstance();
                                    Log.i("type",type);
                                    Log.i("name",dishName);
//                                reference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(auth.getUid()).child("List of Dish");
                                    reference.child("List of Dish").child(field).child(textField).child("full").setValue(afterDis);
                                }
                            }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void fourtyDiscount(String s1, String s) {
        auth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(sharedPreferences.getString("state","")).child(Objects.requireNonNull(auth.getUid()));
        reference.child("List of Dish").child(s1).child(s).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot1) {

                        if (String.valueOf(dataSnapshot1.child("mrp").getValue()).equals("no")) {
                            String type = String.valueOf(dataSnapshot1.getKey());
                            String dishName = String.valueOf(dataSnapshot1.child("name").getValue());
                            if (Integer.parseInt(Objects.requireNonNull(String.valueOf(dataSnapshot1.child("full").getValue()))) >= 149) {
                                if(!String.valueOf(dataSnapshot1.child("half").getValue()).equals("")) {
                                    int price = Integer.parseInt(Objects.requireNonNull(String.valueOf(dataSnapshot1.child("full").getValue())));
                                    int halfPrice = Integer.parseInt(Objects.requireNonNull(String.valueOf(dataSnapshot1.child("half").getValue())));
                                    int discount = 40;
                                    int afterDis = price - (price * discount / 100);
                                    int afterDisHalf = halfPrice - (halfPrice * discount / 100);
                                    beforeDiscount(price, afterDis, discount, s1, s, halfPrice);
                                    addToDiscountDatabase("" + discount);
                                    auth = FirebaseAuth.getInstance();
                                    Log.i("type", type);
                                    Log.i("name", dishName);
                                    reference.child("List of Dish").child(s1).child(s).child("full").setValue(afterDis);
                                    reference.child("List of Dish").child(s1).child(s).child("half").setValue(afterDisHalf);
                                }else{
                                    int price = Integer.parseInt(Objects.requireNonNull(String.valueOf(dataSnapshot1.child("full").getValue())));
                                    int discount = 40;
                                    int afterDis = price - (price * discount / 100);
                                    beforeDiscount(price,afterDis,discount,s1,s,0);
                                    addToDiscountDatabase("yes");
                                    auth = FirebaseAuth.getInstance();
                                    Log.i("type",type);
                                    Log.i("name",dishName);
                                    reference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(Objects.requireNonNull(auth.getUid()));
                                    reference.child("List of Dish").child(s1).child(s).child("full").setValue(afterDis);
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

                new Handler().postDelayed(builder::dismiss,1500);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void addToDiscountDatabase(String discount) {
        auth = FirebaseAuth.getInstance();
        addToDB = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(sharedPreferences.getString("state","")).child(Objects.requireNonNull(auth.getUid()));
        addToDB.child("Discount").child("available").setValue("yes");
        addToDB.child("Discount").child("dis").setValue(discount);
    }

    private void beforeDiscount(int price,int after, int discount,String type,String name,int halfPrice) {
        DisInfo disInfo;
        if(halfPrice == 0)
            disInfo = new DisInfo(String.valueOf(price),String.valueOf(after),String.valueOf(discount),"");
        else
            disInfo = new DisInfo(String.valueOf(price),String.valueOf(after),String.valueOf(discount),""+ halfPrice);

        dis = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(sharedPreferences.getString("state","")).child(Objects.requireNonNull(auth.getUid()));
        dis.child("List of Dish").child(type).child(name).child("Discount").child(name).setValue(disInfo);
    }


    private void fiftyDiscount(String s1, String s) {
        auth = FirebaseAuth.getInstance();

        reference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(sharedPreferences.getString("state","")).child(Objects.requireNonNull(auth.getUid()));
        reference.child("List of Dish").child(s1).child(s).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot1) {

                        if (String.valueOf(dataSnapshot1.child("mrp").getValue()).equals("no")) {
                            String type = String.valueOf(dataSnapshot1.getKey());
                            String dishName = String.valueOf(dataSnapshot1.child("name").getValue());
                            if (Integer.parseInt(Objects.requireNonNull(String.valueOf(dataSnapshot1.child("full").getValue()))) >= 149) {
                                if(!String.valueOf(dataSnapshot1.child("half").getValue()).equals("")) {
                                    int price = Integer.parseInt(Objects.requireNonNull(String.valueOf(dataSnapshot1.child("full").getValue())));
                                    int halfPrice = Integer.parseInt(Objects.requireNonNull(String.valueOf(dataSnapshot1.child("half").getValue())));

                                    int discount = 50;
                                    int afterDis = price - (price * discount / 100);
                                    int afterDisHalf = halfPrice - (halfPrice * discount / 100);
                                    beforeDiscount(price, afterDis, discount, s1, s, halfPrice);
                                    addToDiscountDatabase("" + discount);
                                    auth = FirebaseAuth.getInstance();
                                    Log.i("type", type);
                                    Log.i("name", dishName);
//                                reference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(auth.getUid()).child("List of Dish");
                                    reference.child("List of Dish").child(s1).child(s).child("full").setValue(afterDis);
                                    reference.child("List of Dish").child(s1).child(s).child("half").setValue(afterDisHalf);
                                }else{
                                    int price = Integer.parseInt(Objects.requireNonNull(String.valueOf(dataSnapshot1.child("full").getValue())));
                                    int discount = 50;
                                    int afterDis = price - (price * discount / 100);
                                    beforeDiscount(price,afterDis,discount,type,dishName,0);
                                    addToDiscountDatabase("yes");
                                    auth = FirebaseAuth.getInstance();
                                    Log.i("type",type);
                                    Log.i("name",dishName);
//                                reference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(auth.getUid()).child("List of Dish");
                                    reference.child("List of Dish").child(s1).child(s).child("full").setValue(afterDis);
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
