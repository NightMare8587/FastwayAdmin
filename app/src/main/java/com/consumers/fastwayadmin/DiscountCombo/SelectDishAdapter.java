package com.consumers.fastwayadmin.DiscountCombo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.consumers.fastwayadmin.R;
import com.developer.kalert.KAlertDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import karpuzoglu.enes.com.fastdialog.Animations;
import karpuzoglu.enes.com.fastdialog.FastDialog;
import karpuzoglu.enes.com.fastdialog.FastDialogBuilder;
import karpuzoglu.enes.com.fastdialog.NegativeClick;
import karpuzoglu.enes.com.fastdialog.PositiveClick;
import karpuzoglu.enes.com.fastdialog.Type;

public class SelectDishAdapter extends RecyclerView.Adapter<SelectDishAdapter.holder> {
    List<String> name = new ArrayList<>();
    List<String> image = new ArrayList<>();
    List<String> price = new ArrayList<>();
    String state;
    String locality;
    Context context;

    public SelectDishAdapter(List<String> name, List<String> image,Context context,String state,String locality,List<String> price) {
        this.name = name;
        this.price = price;
        this.image = image;
        this.locality = locality;
        this.context = context;
        this.state = state;
    }

    @NonNull
    @Override
    public holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.select_dish_card,parent,false);
        return new holder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull holder holder, @SuppressLint("RecyclerView") int position) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(state).child(locality).child(Objects.requireNonNull(auth.getUid()));
        holder.nameOfDish.setText(name.get(position));
        holder.price.setText("\u20b9" + price.get(position));
        if(!image.get(position).equals("")){
            Picasso.get().load(image.get(position)).into(holder.imageView);
        }
        holder.cardView.setOnClickListener(view -> {
            FastDialog fastDialog = new FastDialogBuilder(view.getContext(), Type.DIALOG)
                    .setTitleText("Enter Quantity")
                    .setText("Enter quantity of dish")
                    .setHint("Enter here")
                    .positiveText("Proceed")
                    .negativeText("Cancel")
                    .setAnimation(Animations.SLIDE_TOP)
                    .create();

            fastDialog.show();

            fastDialog.positiveClickListener(new PositiveClick() {
                @Override
                public void onClick(View view) {
                    if(fastDialog.getInputText().equals("")){
                        Toast.makeText(view.getContext(), "Field can't be empty", Toast.LENGTH_SHORT).show();
                    }else{
                        if(TextUtils.isDigitsOnly(fastDialog.getInputText())) {
                            fastDialog.dismiss();
                            String inputText = fastDialog.getInputText();
                            new KAlertDialog(context, KAlertDialog.WARNING_TYPE)
                                    .setTitleText("Add To Combo")
                                    .setContentText("You sure wanna add this to combo")
                                    .setConfirmText("Yes, Add it")
                                    .setConfirmClickListener(new KAlertDialog.KAlertClickListener() {
                                        @Override
                                        public void onClick(KAlertDialog kAlertDialog) {
                                            reference.child("Current combo").child(name.get(position)).child("name").setValue(name.get(position));
                                            reference.child("Current combo").child(name.get(position)).child("quantity").setValue(inputText);
                                            kAlertDialog.dismissWithAnimation();
                                        }
                                    }).setCancelText("No, Wait").setCancelClickListener(new KAlertDialog.KAlertClickListener() {
                                @Override
                                public void onClick(KAlertDialog kAlertDialog) {
                                    kAlertDialog.dismissWithAnimation();
                                }
                            }).show();
                        }else{
                            Toast.makeText(view.getContext(), "Wrong Input", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                }
            });

            fastDialog.negativeClickListener(new NegativeClick() {
                @Override
                public void onClick(View view) {
                    fastDialog.dismiss();
                }
            });

        });
    }

    @Override
    public int getItemCount() {
        return name.size();
    }

    public static class holder extends RecyclerView.ViewHolder{
        TextView nameOfDish,price;
        ImageView imageView;
        CardView cardView;
        public holder(@NonNull View itemView) {
            super(itemView);
            nameOfDish = itemView.findViewById(R.id.comboTextView);
            imageView = itemView.findViewById(R.id.comboImgaeView);
            price = itemView.findViewById(R.id.comboPriceTextViewDishCard);
            cardView = itemView.findViewById(R.id.selectDishCardViewRecycler);
        }
    }
}
