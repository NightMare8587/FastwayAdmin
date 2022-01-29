package com.consumers.fastwayadmin.ListViewActivity.StaffDetails;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle("Dialog").setMessage("Choose one option")
                        .setPositiveButton("Add/Update Bank Details", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                                alert.setTitle("Bank Details").setMessage("Do wanna add bank details of your staff\nIf customer wanna report something or give tip to your staff")
                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.dismiss();
                                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                                builder.setTitle("Add Details").setMessage("Add Details Below");
                                                LinearLayout linearLayout = new LinearLayout(context);
                                                linearLayout.setOrientation(LinearLayout.VERTICAL);
                                                EditText holderAccountNumber = new EditText(context);
                                                holderAccountNumber.setHint("Enter Account number here");
                                                holderAccountNumber.setMaxLines(100);
                                                holderAccountNumber.setInputType(InputType.TYPE_CLASS_NUMBER);

                                                EditText holderAccountIFSC = new EditText(context);
                                                holderAccountIFSC.setHint("Enter IFSC code here");
                                                holderAccountIFSC.setMaxLines(100);

                                                EditText holderName = new EditText(context);
                                                holderName.setHint("Enter Holder Name here");

                                                linearLayout.addView(holderAccountNumber);
                                                linearLayout.addView(holderAccountIFSC);
                                                linearLayout.addView(holderName);
                                                builder.setView(linearLayout);
                                                builder.setPositiveButton("Add Details", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                        dialogInterface.dismiss();
                                                        if(holderAccountNumber.getText().toString().equals("")){
                                                            holderAccountNumber.requestFocus();
                                                            holderAccountNumber.setError("Field can't be empty");
                                                            Toast.makeText(context, "Wrong Input", Toast.LENGTH_SHORT).show();
                                                            return;
                                                        }

                                                        if(holderAccountIFSC.getText().toString().equals("")){
                                                            holderAccountIFSC.requestFocus();
                                                            Toast.makeText(context, "Wrong Input", Toast.LENGTH_SHORT).show();
                                                            holderAccountIFSC.setError("Field can't be empty");
                                                            return;
                                                        }

                                                        if(holderAccountIFSC.getText().toString().length() != 11){
                                                            holderAccountIFSC.requestFocus();
                                                            Toast.makeText(context, "Wrong Input Add 11 digit IFSC", Toast.LENGTH_SHORT).show();
                                                            holderAccountIFSC.setError("Invalid IFSC code (11 digit)");
                                                            return;
                                                        }

                                                        if(holderName.getText().toString().equals("")){
                                                            holderName.requestFocus();
                                                            Toast.makeText(context, "Wrong Input", Toast.LENGTH_SHORT).show();
                                                            holderName.setError("Field can't be empty");
                                                            return;
                                                        }

                                                        DatabaseReference addBank = FirebaseDatabase.getInstance().getReference().getRoot().child("Admin").child(Objects.requireNonNull(auth.getUid())).child("Restaurant Staff").child(UUID.get(position));
                                                        addBank.child("Bank Details").child("accountNumber").setValue(holderAccountNumber.getText().toString());
                                                        addBank.child("Bank Details").child("accountIFSC").setValue(holderAccountIFSC.getText().toString());
                                                        addBank.child("Bank Details").child("accountName").setValue(holderName.getText().toString());

                                                        Toast.makeText(context, "Details Added Successfully", Toast.LENGTH_SHORT).show();

                                                    }
                                                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                        dialogInterface.dismiss();
                                                    }
                                                }).create();

                                            }
                                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                }).create();
                                alert.show();
                            }
                        }).setNegativeButton("Add/update image", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        Intent intent = new Intent(view.getContext(),UpdateImageRestaurantStaff.class);
                        intent.putExtra("uuid",UUID.get(position) + "");
                        intent.putExtra("name",name.get(position) + "");
                        view.getContext().startActivity(intent);
                    }
                }).setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.create().show();
            }
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
