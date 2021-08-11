package com.consumers.fastwayadmin.MenuActivities;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.consumers.fastwayadmin.Dish.CreateDishClass;
import com.consumers.fastwayadmin.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DisplayDish extends RecyclerView.Adapter<DisplayDish.Adapter> {

    List<String> names = new ArrayList<>();
    List<String> image = new ArrayList<>();
    FirebaseAuth auth;
    DatabaseReference reference;
    AlertDialog dialog;

    public DisplayDish(List<String> names,List<String> image){
        this.image = image;
        this.names = names;
    }

    @NonNull
    @Override
    public Adapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.display_dish,parent,false);
        return new Adapter(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Adapter holder, int position) {
        auth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(Objects.requireNonNull(auth.getUid()));
        holder.name.setText(names.get(position));
        if(!image.get(position).equals(""))
        Picasso.get().load(image.get(position)).centerCrop().resize(100,100).into(holder.imageView);
        holder.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle("Add Dish");
                Context context = view.getContext();
                LinearLayout linearLayout = new LinearLayout(context);
                linearLayout.setPadding(8,8,8,8);
                linearLayout.setOrientation(LinearLayout.VERTICAL);

                final EditText halfPlate = new EditText(context);
                halfPlate.setHint("Enter Half Plate Price");
                linearLayout.addView(halfPlate);

                final TextView textView = new TextView(context);
                textView.setText("Keep field Empty is half plate not available");
                linearLayout.addView(textView);

                final EditText fullPlate = new EditText(context);
                fullPlate.setHint("Enter full Plate price");
                linearLayout.addView(fullPlate);

                final CheckBox checkBox = new CheckBox(context);
                checkBox.setText("Sell On MRP (Discount will not be applied on MRP products)");
                linearLayout.addView(checkBox);

                final Button button = new Button(context);
                button.setText("Add Dish");
                linearLayout.addView(button);

                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        SharedPreferences preferences = view.getContext().getSharedPreferences("DishType",Context.MODE_PRIVATE);
                        String type = preferences.getString("Type","");
                        String mrp;
                        if(checkBox.isChecked())
                            mrp = "yes";
                        else
                            mrp = "false";
                        CreateDishClass createDishClass = new CreateDishClass(holder.name.getText().toString(),image.get(position),halfPlate.getText().toString(),fullPlate.getText().toString(),mrp,"0","0","0","yes");
                        reference.child("List of Dish").child(type).child(holder.name.getText().toString()).setValue(createDishClass);
                        Toast.makeText(context, "Dish added Successfully", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });

                builder.setView(linearLayout);
                dialog = builder.create();
                builder.setCancelable(true);
                builder.show();

            }
        });
    }

    @Override
    public int getItemCount() {
        return names.size();
    }

    public static class Adapter extends RecyclerView.ViewHolder{
        TextView name;
        ImageView imageView;
        public Adapter(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.displayDishName);
            imageView = itemView.findViewById(R.id.displayImage);
        }
    }
}
