package com.consumers.fastwayadmin.MenuActivities.Combo;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.consumers.fastwayadmin.Dish.AddImageToDish;
import com.consumers.fastwayadmin.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ComboRecyclerView extends RecyclerView.Adapter<ComboRecyclerView.Holder> {
    List<String> comboName;
    List<List<String>> dishName;
    List<List<String>> dishQuan;
    List<String> price;
    List<String> enabled;
    HashMap<String,HashMap<String,String>> outerMap;
    List<String> comboImage;
    List<String> description;
    Context context;
    public ComboRecyclerView(List<String> comboName, List<List<String>> dishName, List<String> price, Context context,
                             List<String> comboImage,List<String> description,List<String> enabled,List<List<String>> dishQuan,HashMap<String,HashMap<String,String>> outerMap) {
        this.comboName = comboName;
        this.description = description;
        this.outerMap = outerMap;
        this.comboImage = comboImage;
        this.dishQuan = dishQuan;
        this.enabled = enabled;
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

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull Holder holder, @SuppressLint("RecyclerView") int position) {
        holder.comboName.setText(comboName.get(position));
        holder.price.setText("\u20b9" + price.get(position));
        HashMap<String,String> map = new HashMap<>(outerMap.get(comboName.get(position)));
//        String[] arr = map.keySet().toArray(new String[0]);
        List<String> current = new ArrayList<>();
        List<String> currentQ = new ArrayList<>();

        for(Map.Entry<String,String> mapE : map.entrySet()){
            current.add(mapE.getKey());
            currentQ.add(mapE.getValue());
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, current);
        ArrayAdapter<String> arrayAdapter1 = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, currentQ);
        holder.listView.setAdapter(arrayAdapter);
        holder.listView2.setAdapter(arrayAdapter1);
        ViewGroup.LayoutParams param = holder.listView.getLayoutParams();
        param.height = 150 * current.size();
        holder.listView.setLayoutParams(param);

        ViewGroup.LayoutParams params = holder.listView2.getLayoutParams();
        params.height = 150 * current.size();
        params.width= 150;
        holder.listView2.setLayoutParams(params);
        if(enabled.get(position).equals("yes")){
            holder.checkBox.setText("Enabled");
            holder.checkBox.setChecked(true);
        }else{
            holder.checkBox.setText("Disabled");
            holder.checkBox.setChecked(false);
        }
        holder.imageView.setOnClickListener(click -> {
            AlertDialog.Builder alert = new AlertDialog.Builder(click.getContext());
            alert.setTitle("Image");
            alert.setMessage("Choose one option from below");
            alert.setPositiveButton("Add/Upload New Image", (dialogInterface, i) -> {
                dialogInterface.dismiss();
                Intent intent = new Intent(click.getContext(), AddImageToDish.class);
                intent.putExtra("type","Combo");
                intent.putExtra("dishName",comboName.get(position));
                click.getContext().startActivity(intent);

            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
dialogInterface.dismiss();
                }
            });
            alert.create().show();
        });
        if(description.get(position).equals("")) {
            holder.description.setText("Add Description");
        }else{
            holder.description.setText(description.get(position));
        }

        holder.description.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
            builder.setTitle("New Description").setMessage("Enter new description in below field");
            LinearLayout linearLayout = new LinearLayout(v.getContext());
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            EditText editText = new EditText(v.getContext());
            editText.setHint("Enter description here");
            editText.setMaxLines(200);
            editText.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setPositiveButton("Change Description", (dialogInterface1, i1) -> {
                if(editText.getText().toString().equals("")){
                    Toast.makeText(v.getContext(), "Invalid Input", Toast.LENGTH_SHORT).show();
                    return;
                }
                SharedPreferences sharedPreferences = v.getContext().getSharedPreferences("loginInfo",Context.MODE_PRIVATE);
                FirebaseAuth auth = FirebaseAuth.getInstance();
                FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                firestore.collection(sharedPreferences.getString("state","")).document("Restaurants").collection(sharedPreferences.getString("locality","")).document(auth.getUid()).collection("List of Dish").document(comboName.get(position))
                        .update("description",editText.getText().toString());
                DatabaseReference reference =  FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(sharedPreferences.getString("state","")).child(sharedPreferences.getString("locality","")).child(Objects.requireNonNull(auth.getUid())).child("List of Dish").child("Combo").child(comboName.get(position));
                reference.child("description").setValue(editText.getText().toString());
                Toast.makeText(v.getContext(), "Description Changed Successfully", Toast.LENGTH_SHORT).show();
                dialogInterface1.dismiss();
            }).setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss()).create();
            linearLayout.addView(editText);
            builder.setView(linearLayout);
            builder.setCancelable(false);
            builder.create().show();
        });
        if(!comboImage.get(position).equals(""))
        Picasso.get().load(comboImage.get(position)).into(holder.imageView);
        else
            Picasso.get().load("https://image.shutterstock.com/image-vector/no-image-vector-isolated-on-600w-1481369594.jpg").into(holder.imageView);
        holder.listView.requestLayout();
        holder.cardView.setOnClickListener(v -> {
            AlertDialog.Builder alert = new AlertDialog.Builder(v.getContext());
            alert.setTitle("Choose one option");
            alert.setPositiveButton("Add/Remove Items", (dialog, which) -> {
                Intent intent = new Intent(v.getContext(),AddRemoveItemCombo.class);
                intent.putExtra("name",comboName.get(position));
                dialog.dismiss();
//                intent.putStringArrayListExtra("dishName", (ArrayList<String>) dishName.get(position));
//                intent.putStringArrayListExtra("dishQuan", (ArrayList<String>) dishQuan.get(position));
                intent.putExtra("comboDishInfo",outerMap.get(comboName.get(position)));
                v.getContext().startActivity(intent);

            }).setNeutralButton("Cancel", (dialog, which) -> dialog.dismiss()).setNegativeButton("Change Price", (dialogInterface, i) -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("New Price").setMessage("Enter new price in below field");
                LinearLayout linearLayout = new LinearLayout(v.getContext());
                linearLayout.setOrientation(LinearLayout.VERTICAL);
                EditText editText = new EditText(v.getContext());
                editText.setHint("Enter price here");
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                builder.setPositiveButton("Change Price", (dialogInterface1, i1) -> {
                    if(editText.getText().toString().equals("") || editText.getText().toString().equals("0")){
                        Toast.makeText(v.getContext(), "Invalid Input", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    SharedPreferences sharedPreferences = v.getContext().getSharedPreferences("loginInfo",Context.MODE_PRIVATE);
                    FirebaseAuth auth = FirebaseAuth.getInstance();
                    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                    firestore.collection(sharedPreferences.getString("state","")).document("Restaurants").collection(sharedPreferences.getString("locality","")).document(auth.getUid()).collection("List of Dish").document(comboName.get(position))
                            .update("price",editText.getText().toString());
//                    DatabaseReference reference =  FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(sharedPreferences.getString("state","")).child(sharedPreferences.getString("locality","")).child(Objects.requireNonNull(auth.getUid())).child("List of Dish").child("Combo").child(comboName.get(position));
//                    reference.child("price").setValue(editText.getText().toString());
                    Toast.makeText(v.getContext(), "Price Changed Successfully", Toast.LENGTH_SHORT).show();
                    dialogInterface1.dismiss();
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).create();
                linearLayout.addView(editText);
                builder.setView(linearLayout);
                builder.setCancelable(false);
                builder.create().show();
                dialogInterface.dismiss();
            }).create().show();
        });
        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            SharedPreferences sharedPreferences = buttonView.getContext().getSharedPreferences("loginInfo",Context.MODE_PRIVATE);
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Restaurants").child(sharedPreferences.getString("state","")).child(sharedPreferences.getString("locality","")).child(Objects.requireNonNull(auth.getUid())).child("List of Dish").child("Combo");
            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
            if(isChecked){
//                databaseReference.child(comboName.get(position)).child("enable").setValue("yes");
                firestore.collection(sharedPreferences.getString("state","")).document("Restaurants").collection(sharedPreferences.getString("locality","")).document(auth.getUid()).collection("List of Dish").document(comboName.get(position))
                        .update("enable","yes");
                holder.checkBox.setText("Enabled");
            }else{
//                databaseReference.child(comboName.get(position)).child("enable").setValue("no");
                firestore.collection(sharedPreferences.getString("state","")).document("Restaurants").collection(sharedPreferences.getString("locality","")).document(auth.getUid()).collection("List of Dish").document(comboName.get(position))
                        .update("enable","no");
                holder.checkBox.setText("Disabled");
            }
        });
    }

    @Override
    public int getItemCount() {
        return comboName.size();
    }

    protected class Holder extends RecyclerView.ViewHolder{
        TextView comboName,price,description;
        ListView listView,listView2;
        CheckBox checkBox;
        ImageView imageView;
        CardView cardView;
        public Holder(@NonNull View itemView) {
            super(itemView);
            comboName = itemView.findViewById(R.id.comboMenuDishName);
            price = itemView.findViewById(R.id.comboMenuPriceView);
            listView = itemView.findViewById(R.id.comboDishListViewMenu);
            listView2 = itemView.findViewById(R.id.comboDishListViewQuantity);
            checkBox = itemView.findViewById(R.id.checkBox);
            description = itemView.findViewById(R.id.descriptionOfComboMenuFrag);
            imageView = itemView.findViewById(R.id.ComboImageView);
            cardView = itemView.findViewById(R.id.comboAdCard);
        }
    }
}
