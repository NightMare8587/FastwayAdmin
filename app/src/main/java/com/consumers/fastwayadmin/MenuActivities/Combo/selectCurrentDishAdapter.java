package com.consumers.fastwayadmin.MenuActivities.Combo;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.InputType;
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

import com.consumers.fastwayadmin.R;
import com.developer.kalert.KAlertDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class selectCurrentDishAdapter extends RecyclerView.Adapter<selectCurrentDishAdapter.holder> {
        List<String> name = new ArrayList<>();
        List<String> image = new ArrayList<>();
        List<String> price = new ArrayList<>();
        List<String> dishNames = new ArrayList<>();
        List<String> dishQuan = new ArrayList<>();
        Context context;
        String comboName;
        String state;
        String locality;

public selectCurrentDishAdapter(List<String> name, List<String> image,Context context,String comboName,String state,String locality,List<String> price,List<String> dishNames,List<String> dishQuan) {
        this.name = name;
        this.dishQuan = dishQuan;
        this.image = image;
        this.dishNames = dishNames;
        this.price = price;
        this.context = context;
        this.locality = locality;
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
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(state).child(locality).child(Objects.requireNonNull(auth.getUid())).child("List of Dish");
        holder.nameOfDish.setText(name.get(position));
        holder.priceOfDish.setText("\u20b9" + price.get(position));
        if(!image.get(position).equals("")){
        Picasso.get().load(image.get(position)).into(holder.imageView);
        }
        holder.cardView.setOnClickListener(view -> new KAlertDialog(context,KAlertDialog.WARNING_TYPE)
                .setTitleText("Add To Combo")
                .setContentText("You sure wanna add this to combo")
                .setConfirmText("Yes, Add it")
                .setConfirmClickListener(kAlertDialog -> {
                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                    builder.setTitle("Add Quantity").setMessage("Enter new quantity in below field");
                    LinearLayout linearLayout = new LinearLayout(view.getContext());
                    linearLayout.setOrientation(LinearLayout.VERTICAL);
                    EditText editText = new EditText(view.getContext());
                    editText.setHint("Enter quantity here");
                    editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                    builder.setPositiveButton("Add Quantity", (dialogInterface1, i1) -> {
                        if(editText.getText().toString().equals("") || editText.getText().toString().equals("0")){
                            Toast.makeText(view.getContext(), "Invalid Input", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if(dishNames.contains(name.get(position))){
                            Toast.makeText(context, "Dish Already Exists", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        dishQuan.add(editText.getText().toString().trim());
                        dishNames.add(name.get(position).trim());

                        String dishArr = dishNames.toString().replace("[","").replace("]","").trim();
                        String quanArr = dishQuan.toString().replace("[","").replace("]","").trim();

                        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                        firestore.collection(state).document("Restaurants").collection(locality).document(auth.getUid()).collection("List of Dish").document(comboName)
                                .update("dishNamesArr",dishArr,"dishQuantityArr",quanArr);
//                        reference.child("Combo").child(comboName).child(name.get(position)).child("name").child("name").setValue(name.get(position));
//                        reference.child("Combo").child(comboName).child(name.get(position)).child("name").child("quantity").setValue(editText.getText().toString());
                        kAlertDialog.dismissWithAnimation();
                        Toast.makeText(view.getContext(), "Dish Added", Toast.LENGTH_SHORT).show();
                        dialogInterface1.dismiss();
                    }).setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss()).create();
                    linearLayout.addView(editText);
                    builder.setView(linearLayout);
                    builder.setCancelable(false);
                    builder.create().show();

                        }).setCancelText("No, Wait").setCancelClickListener(KAlertDialog::dismissWithAnimation).show());
                }

        @Override
        public int getItemCount() {
                return name.size();
                }

public static class holder extends RecyclerView.ViewHolder{
    TextView nameOfDish,priceOfDish;
    ImageView imageView;
    CardView cardView;
    public holder(@NonNull View itemView) {
        super(itemView);
        nameOfDish = itemView.findViewById(R.id.comboTextView);
        imageView = itemView.findViewById(R.id.comboImgaeView);
        priceOfDish = itemView.findViewById(R.id.comboPriceTextViewDishCard);
        cardView = itemView.findViewById(R.id.selectDishCardViewRecycler);
    }
}
}

