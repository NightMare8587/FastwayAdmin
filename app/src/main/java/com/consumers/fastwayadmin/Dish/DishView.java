package com.consumers.fastwayadmin.Dish;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.consumers.fastwayadmin.MenuActivities.EditMenu;
import com.consumers.fastwayadmin.R;
import com.developer.kalert.KAlertDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Objects;

public class DishView extends RecyclerView.Adapter<DishView.DishAdapter> {
    List<String> names;
    List<String> fullPrice;
    List<String> half;
    List<String> image;
    List<String> before;
    List<String> after;
    SharedPreferences sharedPreferences;
    List<String> discount;
    FirebaseAuth auth;
    DatabaseReference ref;
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
    public void onBindViewHolder(@NonNull DishAdapter holder, @SuppressLint("RecyclerView") int position) {
        holder.name.setText(names.get(position));
//        notifyDataSetChanged();
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                    builder.setTitle("Image")
                            .setMessage("Add/Upload new image for existing dish")
                            .setPositiveButton("Add New Image", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                    Intent intent = new Intent(v.getContext(),AddImageToDish.class);
                                    intent.putExtra("type",type);
                                    intent.putExtra("dishName",names.get(position));
                                    v.getContext().startActivity(intent);

                                }
                            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    }).create();

                    builder.show();

            }
        });

        holder.removeOffers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth auth = FirebaseAuth.getInstance();
                SharedPreferences sharedPreferences = v.getContext().getSharedPreferences("loginInfo", Context.MODE_PRIVATE);
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(sharedPreferences.getString("state","")).child(Objects.requireNonNull(auth.getUid())).child("List of Dish").child(type).child(names.get(position));
                databaseReference.child("Discount").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String before = String.valueOf(snapshot.child(names.get(position)).child("before").getValue());
                            String beforeHalf = String.valueOf(snapshot.child(names.get(position)).child("beforeHalf").getValue());
                            Log.d("before",before);
                            databaseReference.child("Discount").removeValue();
                            databaseReference.child("full").setValue(before);
                            databaseReference.child("half").setValue(beforeHalf);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
        holder.price.setText(fullPrice.get(position));
        if(!before.get(position).equals("")){
            holder.price.setText("\u20B9" + before.get(position));
            holder.price.setPaintFlags(holder.price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.discountPrice.setText("\u20B9" + after.get(position));
            holder.discountPrice.setVisibility(View.VISIBLE);
            holder.removeOffers.setVisibility(View.VISIBLE);
        }else{
            holder.price.setText("\u20B9" + fullPrice.get(position));
            holder.discountPrice.setVisibility(View.INVISIBLE);
            holder.removeOffers.setVisibility(View.INVISIBLE);
        }
        if(!image.get(position).equals(""))
        Picasso.get().load(image.get(position)).centerCrop().resize(100,100).into(holder.imageView);
        else
            Picasso.get().load("https://image.shutterstock.com/image-vector/no-image-vector-isolated-on-600w-1481369594.jpg").centerCrop().resize(100, 100).into(holder.imageView);

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

        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sharedPreferences = buttonView.getContext().getSharedPreferences("loginInfo",Context.MODE_PRIVATE);
                FirebaseAuth auth = FirebaseAuth.getInstance();
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Restaurants").child(sharedPreferences.getString("state","")).child(Objects.requireNonNull(auth.getUid())).child("List of Dish").child(type);
                if(isChecked){
                    databaseReference.child(names.get(position)).child("enable").setValue("yes");
                    holder.checkBox.setText("Enabled");
                }else{
                    databaseReference.child(names.get(position)).child("enable").setValue("no");
                    holder.checkBox.setText("Disabled");
                }
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(view.getContext(), EditMenu.class);
//                intent.putExtra("type",type);
//                intent.putExtra("dish",names.get(position));
//                view.getContext().startActivity(intent);
                sharedPreferences = view.getContext().getSharedPreferences("loginInfo",Context.MODE_PRIVATE);
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle("Important");
                builder.setMessage("Choose one option");
                if(!half.get(position).isEmpty()){
                    builder.setNeutralButton("Remove Half Plate", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            FirebaseAuth auth = FirebaseAuth.getInstance();
                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(sharedPreferences.getString("state","")).child(Objects.requireNonNull(auth.getUid())).child("List of Dish").child(type).child(names.get(position));
                            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @SuppressLint("SetTextI18n")
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(snapshot.hasChild("half")){
                                        databaseReference.child("half").setValue("");
                                        holder.available.setText("Half plate not available");
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    });
                }
                builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("Change Price", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new KAlertDialog(view.getContext(),KAlertDialog.ERROR_TYPE)
                                .setTitleText("Important")
                                .setContentText("Changing price/Name will remove all offers applied on dish")
                                .setConfirmText("Ok")
                                .setCancelText("Cancel")
                                .setConfirmClickListener(new KAlertDialog.KAlertClickListener() {
                                    @Override
                                    public void onClick(KAlertDialog kAlertDialog) {
                                            Intent intent = new Intent(view.getContext(), EditMenu.class);
                                            intent.putExtra("type",type);
                                            intent.putExtra("dish",names.get(position));
                                            view.getContext().startActivity(intent);
                                            kAlertDialog.dismissWithAnimation();
                                    }
                                })
                                .setCancelClickListener(new KAlertDialog.KAlertClickListener() {
                                    @Override
                                    public void onClick(KAlertDialog kAlertDialog) {
                                        kAlertDialog.dismissWithAnimation();
                                    }
                                }).show();
                    }
                });

                builder.create().show();

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
                                sharedPreferences = view.getContext().getSharedPreferences("loginInfo",Context.MODE_PRIVATE);
                               ref = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(sharedPreferences.getString("state","")).child(Objects.requireNonNull(auth.getUid()));
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
        CardView cardView;
        Button removeOffers;
        CheckBox checkBox;
        public DishAdapter(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.displayDishImage);
            name = itemView.findViewById(R.id.DishName);
            price = itemView.findViewById(R.id.pricePfDish);
            cardView = itemView.findViewById(R.id.myCard);
            available = itemView.findViewById(R.id.availableOr);
            discountPrice = itemView.findViewById(R.id.discountedPrice);
            checkBox = itemView.findViewById(R.id.enableDisableCheckBox);
            removeOffers = itemView.findViewById(R.id.removeOffersButtomDish);
        }
    }


}
