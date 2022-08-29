package com.consumers.fastwayadmin.Tables;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Environment;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.consumers.fastwayadmin.R;
import com.developer.kalert.KAlertDialog;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class TableView extends RecyclerView.Adapter<TableView.TableAdapter> {
    List<String> tables;
    List<String> status;
    List<String> seats;
    List<String> timeOfBooking;
    List<String> timeOfUnavailability;
    HashMap<String,List<String>> map;
    List<String> timeInMillis;
    String URL = "https://fcm.googleapis.com/fcm/send";
    DatabaseReference reference;
    Context context;
    FirebaseAuth auth;
    public TableView(List<String> tables,List<String> status,HashMap<String,List<String>> map,Context context,List<String> timeInMillis,List<String> timeOfBooking,List<String> timeOfUnavailability,List<String> seats){
        this.status = status;
        this.map = map;
        this.seats = seats;
        this.timeOfUnavailability = timeOfUnavailability;
        this.timeInMillis = timeInMillis;
        this.timeOfBooking = timeOfBooking;
        this.tables = tables;
        this.context = context;
    }

    @NonNull
    @Override
    public TableAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.table_info,parent,false);
        return new TableAdapter(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull TableAdapter holder, @SuppressLint("RecyclerView") int position) {
        auth = FirebaseAuth.getInstance();
        StrictMode.VmPolicy.Builder builders = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builders.build());
        holder.tableNum.setText("Table Number : " + tables.get(position));
        holder.status.setText(status.get(position));
        if(status.get(position).equals("unavailable")){
            holder.checkBox.setVisibility(View.INVISIBLE);
            List<String> myList = map.get(""+tables.get(position));
            holder.chatWith.setVisibility(View.VISIBLE);
            holder.timeOfReserved.setVisibility(View.VISIBLE);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm:ss");
            Date date = new Date(Long.parseLong(timeOfUnavailability.get(position)));
            holder.timeOfReserved.setText(simpleDateFormat.format(date) + "");
            holder.cancel.setVisibility(View.VISIBLE);
//            holder.timeOfReserved.setVisibility(View.VISIBLE);
//            holder.timeOfReserved.setText(myList.get(1)+"");
            holder.chatWith.setOnClickListener(view -> {
                Intent intent = new Intent(view.getContext(),ChatWithCustomer.class);
                assert myList != null;
                intent.putExtra("id",myList.get(0));
                view.getContext().startActivity(intent);
            });
            holder.cancel.setOnClickListener(view -> {
                FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                SharedPreferences sharedPreferences = view.getContext().getSharedPreferences("loginInfo",Context.MODE_PRIVATE);
                reference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(sharedPreferences.getString("state","")).child(sharedPreferences.getString("locality","")).child(Objects.requireNonNull(auth.getUid())).child("Tables");
                        new KAlertDialog(view.getContext(),KAlertDialog.WARNING_TYPE)
                        .setTitleText("Warning!!!")
                        .setContentText("Do you sure wanna remove this reserved table??")
                        .setCancelText("No")
                        .setCancelClickListener(KAlertDialog::dismissWithAnimation)
                        .setConfirmText("Yes")
                        .setConfirmClickListener(kAlertDialog -> {
                            AlertDialog.Builder alertD = new AlertDialog.Builder(view.getContext());
                            alertD.setTitle("Important");
                            alertD.setMessage("Enter reason for cancellation of table below");
                            LinearLayout linearLayout = new LinearLayout(view.getContext());
                            EditText editText = new EditText(view.getContext());
                            editText.setMaxLines(200);
                            editText.setHint("Enter reason here");
                            linearLayout.setOrientation(LinearLayout.VERTICAL);
                            linearLayout.addView(editText);
                            alertD.setView(linearLayout);
                            alertD.setPositiveButton("Proceed", (dialogInterface, i) -> {
                                if(!editText.getText().toString().equals("")) {
                                    kAlertDialog.dismissWithAnimation();
                                    RequestQueue requestQueue = Volley.newRequestQueue(view.getContext());
                                    JSONObject main = new JSONObject();
                                    try {

                                        assert myList != null;
                                        main.put("to", "/topics/" + myList.get(0) + "");
                                        JSONObject notification = new JSONObject();
                                        notification.put("title", "Table Cancelled");
                                        notification.put("click_action", "Table Frag");
                                        notification.put("body", "Your Tables is cancelled by the owner\n" + editText.getText().toString() + "");
                                        main.put("notification", notification);
                                        dialogInterface.dismiss();
                                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, main, response -> {

                                        }, error -> Toast.makeText(view.getContext(), error.getLocalizedMessage() + "null", Toast.LENGTH_SHORT).show()) {
                                            @Override
                                            public Map<String, String> getHeaders() {
                                                Map<String, String> header = new HashMap<>();
                                                header.put("content-type", "application/json");
                                                header.put("authorization", "key=AAAAsigSEMs:APA91bEUF9ZFwIu84Jctci56DQd0TQOepztGOIKIBhoqf7N3ueQrkClw0xBTlWZEWyvwprXZmZgW2MNywF1pNBFpq1jFBr0CmlrJ0wygbZIBOnoZ0jP1zZC6nPxqF2MAP6iF3wuBHD2R");
                                                return header;
                                            }
                                        };
                                        requestQueue.add(jsonObjectRequest);
                                    } catch (Exception e) {
                                        Toast.makeText(view.getContext(), e.getLocalizedMessage() + "null", Toast.LENGTH_SHORT).show();
                                    }

                                    reference.child(tables.get(position)).child("customerId").removeValue();
                                    reference.child(tables.get(position)).child("timeOfUnavailability").removeValue();
                                    reference.child(tables.get(position)).child("CurrentOrdersMade").removeValue();
                                    reference.child(tables.get(position)).child("StoreOrdersCheckOut").removeValue();
                                    reference.child(tables.get(position)).child("foodOrdered").removeValue();
                                    reference.child(tables.get(position)).child("status").setValue("available");
                                    reference.child(tables.get(position)).child("time").removeValue();
                                    HashMap<String,String> myMap = new HashMap<>();
                                    myMap.put("status","available");
                                    myMap.put("tableNum",tables.get(position));
                                    myMap.put("seats",seats.get(position));
                                    firestore.collection(sharedPreferences.getString("state","")).document("Restaurants").collection(sharedPreferences.getString("locality","")).document(auth.getUid())
                                                    .collection("Tables").document(tables.get(position)).set(myMap);
                                    holder.chatWith.setVisibility(View.INVISIBLE);
                                    holder.cancel.setVisibility(View.INVISIBLE);
                                    holder.status.setText("available");
//                                    holder.timeOfReserved.setVisibility(View.INVISIBLE);
                                }
                            });
                            alertD.setNegativeButton("No, Wait", (dialogInterface, i) -> {

                            });

                            alertD.create().show();


                        }).show();



            });
        }


        if(status.get(position).equals("available")){
            holder.checkBox.setChecked(true);
            holder.checkBox.setText("Enabled");
            holder.checkBox.setVisibility(View.VISIBLE);

        }

        if(status.get(position).equals("NotAvailable")){
            holder.checkBox.setChecked(false);
            holder.checkBox.setText("Disabled");
            holder.checkBox.setVisibility(View.VISIBLE);
        }

        holder.checkBox.setOnCheckedChangeListener((compoundButton, b) -> {

            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
            SharedPreferences sharedPreferences = compoundButton.getContext().getSharedPreferences("loginInfo",Context.MODE_PRIVATE);
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(sharedPreferences.getString("state","")).child(sharedPreferences.getString("locality","")).child(Objects.requireNonNull(auth.getUid())).child("Tables").child(tables.get(position));
            if(b){
                firestore.collection(sharedPreferences.getString("state","")).document("Restaurants").collection(sharedPreferences.getString("locality","")).document(auth.getUid())
                                .collection("Tables").document(tables.get(position)).update("status","available");
                databaseReference.child("status").setValue("available");
                holder.checkBox.setText("Enabled");
            }else{
                databaseReference.child("status").setValue("NotAvailable");
                firestore.collection(sharedPreferences.getString("state","")).document("Restaurants").collection(sharedPreferences.getString("locality","")).document(auth.getUid())
                        .collection("Tables").document(tables.get(position)).update("status","NotAvailable");
                holder.checkBox.setText("Disabled");
            }
        });



        if(status.get(position).equals("Reserved")){
            holder.checkBox.setVisibility(View.INVISIBLE);
            List<String> myList = map.get(""+tables.get(position));
            holder.cardView.setOnLongClickListener(click -> {
                AlertDialog.Builder alert = new AlertDialog.Builder(click.getContext());
                alert.setTitle("Add Time").setMessage("Add 10 min to reserved table").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        FirebaseAuth auth = FirebaseAuth.getInstance();
                        long time = Long.parseLong(timeInMillis.get(position));
                        time = time + 600000;
                        SharedPreferences sharedPreferences = click.getContext().getSharedPreferences("loginInfo",Context.MODE_PRIVATE);
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(sharedPreferences.getString("state","")).child(sharedPreferences.getString("locality","")).child(Objects.requireNonNull(auth.getUid())).child("Tables").child(tables.get(position));
                        databaseReference.child("timeInMillis").setValue(time + "");

                        databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Users").child(myList.get(0)).child("Reserve Tables").child(auth.getUid()).child(tables.get(position));
                        databaseReference.child("timeInMillis").setValue(time + "");

                        Toast.makeText(click.getContext(), "10min added successfully", Toast.LENGTH_SHORT).show();

                    }
                }).setNegativeButton("No", (dialogInterface, i) -> dialogInterface.dismiss()).create();
                alert.show();
                return true;
            });


            holder.chatWith.setVisibility(View.VISIBLE);
            holder.cancel.setVisibility(View.VISIBLE);
            holder.timeOfReserved.setVisibility(View.VISIBLE);
            assert myList != null;
            holder.timeOfReserved.setText(myList.get(1)+"");
            holder.chatWith.setOnClickListener(view -> {
                Intent intent = new Intent(view.getContext(),ChatWithCustomer.class);
                intent.putExtra("id",myList.get(0));
                view.getContext().startActivity(intent);
            });

            holder.cancel.setOnClickListener(view -> {
                SharedPreferences sharedPreferences = view.getContext().getSharedPreferences("loginInfo",Context.MODE_PRIVATE);
                reference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(sharedPreferences.getString("state","")).child(sharedPreferences.getString("locality","")).child(Objects.requireNonNull(auth.getUid())).child("Tables");
                new KAlertDialog(view.getContext(),KAlertDialog.WARNING_TYPE)
                        .setTitleText("Warning!!!")
                        .setContentText("Do you sure wanna remove this reserved table??")
                        .setCancelText("No")
                        .setCancelClickListener(KAlertDialog::dismissWithAnimation)
                        .setConfirmText("Yes")
                        .setConfirmClickListener(kAlertDialog -> {
                            kAlertDialog.dismissWithAnimation();
                            AlertDialog.Builder alertD = new AlertDialog.Builder(view.getContext());
                            alertD.setTitle("Important");
                            alertD.setMessage("Enter reason for cancellation of table below");
                            LinearLayout linearLayout = new LinearLayout(view.getContext());
                            EditText editText = new EditText(view.getContext());
                            linearLayout.setOrientation(LinearLayout.VERTICAL);
                            editText.setHint("Enter reason here");
                            linearLayout.addView(editText);
                            alertD.setView(linearLayout);
                            alertD.setPositiveButton("Proceed", (dialogInterface, i) -> {
                                if(!editText.getText().toString().equals("")) {
                                    RequestQueue requestQueue = Volley.newRequestQueue(view.getContext());
                                    JSONObject main = new JSONObject();
                                    long currentTime = System.currentTimeMillis();
                                    long bookingTime = Long.parseLong(timeOfBooking.get(position));
                                    if(currentTime - bookingTime >= 3600000){

                                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Users").child(myList.get(0));
                                        databaseReference.child("CompensationCoupon").child("value").setValue("50");

                                        try {
                                            main.put("to", "/topics/" + myList.get(0) + "");
                                            JSONObject notification = new JSONObject();
                                            notification.put("title", "Reserved Table Cancelled");
                                            notification.put("click_action", "Table Frag");
                                            notification.put("body", "Your Reserved Tables is cancelled by the owner\n" + editText.getText().toString() + "\n" + "You have been provided with 70% discount coupon which you can use on your next order :)");
                                            main.put("notification", notification);
                                            dialogInterface.dismiss();
                                            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, main, new Response.Listener<JSONObject>() {
                                                @Override
                                                public void onResponse(JSONObject response) {

                                                }
                                            }, error -> Toast.makeText(view.getContext(), error.getLocalizedMessage() + "null", Toast.LENGTH_SHORT).show()) {
                                                @Override
                                                public Map<String, String> getHeaders() {
                                                    Map<String, String> header = new HashMap<>();
                                                    header.put("content-type", "application/json");
                                                    header.put("authorization", "key=AAAAsigSEMs:APA91bEUF9ZFwIu84Jctci56DQd0TQOepztGOIKIBhoqf7N3ueQrkClw0xBTlWZEWyvwprXZmZgW2MNywF1pNBFpq1jFBr0CmlrJ0wygbZIBOnoZ0jP1zZC6nPxqF2MAP6iF3wuBHD2R");
                                                    return header;
                                                }
                                            };

                                            requestQueue.add(jsonObjectRequest);
                                        } catch (Exception e) {
                                            Toast.makeText(view.getContext(), e.getLocalizedMessage() + "null", Toast.LENGTH_SHORT).show();
                                        }
                                    }else {
                                        try {
                                            main.put("to", "/topics/" + myList.get(0) + "");
                                            JSONObject notification = new JSONObject();
                                            notification.put("title", "Reserved Table Cancelled");
                                            notification.put("click_action", "Table Frag");
                                            notification.put("body", "Your Reserved Tables is cancelled by the owner\n" + editText.getText().toString() + "");
                                            main.put("notification", notification);
                                            dialogInterface.dismiss();
                                            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, main, new Response.Listener<JSONObject>() {
                                                @Override
                                                public void onResponse(JSONObject response) {

                                                }
                                            }, error -> Toast.makeText(view.getContext(), error.getLocalizedMessage() + "null", Toast.LENGTH_SHORT).show()) {
                                                @Override
                                                public Map<String, String> getHeaders() {
                                                    Map<String, String> header = new HashMap<>();
                                                    header.put("content-type", "application/json");
                                                    header.put("authorization", "key=AAAAsigSEMs:APA91bEUF9ZFwIu84Jctci56DQd0TQOepztGOIKIBhoqf7N3ueQrkClw0xBTlWZEWyvwprXZmZgW2MNywF1pNBFpq1jFBr0CmlrJ0wygbZIBOnoZ0jP1zZC6nPxqF2MAP6iF3wuBHD2R");
                                                    return header;
                                                }
                                            };

                                            requestQueue.add(jsonObjectRequest);
                                        } catch (Exception e) {
                                            Toast.makeText(view.getContext(), e.getLocalizedMessage() + "null", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    FirebaseAuth auth = FirebaseAuth.getInstance();

                                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Users").child(myList.get(0)).child("Reserve Tables").child(auth.getUid());
                                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if(snapshot.exists()){
                                                databaseReference.child(tables.get(position)).removeValue();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                                    HashMap<String,String> myMap = new HashMap<>();
                                    myMap.put("status","available");
                                    myMap.put("tableNum",tables.get(position));
                                    myMap.put("seats",seats.get(position));
                                    firestore.collection(sharedPreferences.getString("state","")).document("Restaurants").collection(sharedPreferences.getString("locality","")).document(auth.getUid())
                                            .collection("Tables").document(tables.get(position)).set(myMap);
                                    reference.child(tables.get(position)).child("customerId").removeValue();
                                    reference.child(tables.get(position)).child("status").setValue("available");
                                    reference.child(tables.get(position)).child("time").removeValue();
                                    reference.child(tables.get(position)).child("timeInMillis").removeValue();
                                    reference.child(tables.get(position)).child("timeOfBooking").removeValue();
                                    reference.child(tables.get(position)).child("time").removeValue();
                                    holder.chatWith.setVisibility(View.INVISIBLE);
                                    holder.cancel.setVisibility(View.INVISIBLE);
                                    holder.status.setText("available");
//                                    holder.timeOfReserved.setVisibility(View.INVISIBLE);

                                }
                            });
                            alertD.setNegativeButton("No, Wait", (dialogInterface, i) -> {
                                dialogInterface.dismiss();
                            });

                            alertD.create().show();



                        }).show();



            });
        }


        if(status.get(position).equals("MultipleReserved")){
            holder.checkBox.setVisibility(View.INVISIBLE);
            List<String> myList = map.get(""+tables.get(position));
            holder.chatWith.setVisibility(View.VISIBLE);
            holder.cancel.setVisibility(View.VISIBLE);
            holder.timeOfReserved.setVisibility(View.VISIBLE);
            assert myList != null;
            holder.timeOfReserved.setText(myList.get(1)+"");
            holder.chatWith.setOnClickListener(view -> {
                Intent intent = new Intent(view.getContext(),ChatWithCustomer.class);
                intent.putExtra("id",myList.get(0));
                view.getContext().startActivity(intent);
            });
            holder.cancel.setOnClickListener(view -> {
                SharedPreferences sharedPreferences = view.getContext().getSharedPreferences("loginInfo",Context.MODE_PRIVATE);
                reference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(sharedPreferences.getString("state","")).child(sharedPreferences.getString("locality","")).child(Objects.requireNonNull(auth.getUid())).child("Tables");
                new KAlertDialog(view.getContext(),KAlertDialog.WARNING_TYPE)
                        .setTitleText("Warning!!!")
                        .setContentText("Do you sure wanna remove this Multiple reserved table??")
                        .setCancelText("No")
                        .setCancelClickListener(KAlertDialog::dismissWithAnimation)
                        .setConfirmText("Yes")
                        .setConfirmClickListener(kAlertDialog -> {
                            kAlertDialog.dismissWithAnimation();
                            AlertDialog.Builder alertD = new AlertDialog.Builder(view.getContext());
                            alertD.setTitle("Important");
                            alertD.setMessage("Enter reason for cancellation of table below");
                            LinearLayout linearLayout = new LinearLayout(view.getContext());
                            EditText editText = new EditText(view.getContext());
                            linearLayout.setOrientation(LinearLayout.VERTICAL);
                            editText.setHint("Enter reason here");
                            linearLayout.addView(editText);
                            alertD.setView(linearLayout);
                            alertD.setPositiveButton("Proceed", (dialogInterface, i) -> {
                                if(!editText.getText().toString().equals("")) {
                                    RequestQueue requestQueue = Volley.newRequestQueue(view.getContext());
                                    JSONObject main = new JSONObject();
                                    long currentTime = System.currentTimeMillis();
                                    long bookingTime = Long.parseLong(timeOfBooking.get(position));
                                        try {
                                            main.put("to", "/topics/" + myList.get(0) + "");
                                            JSONObject notification = new JSONObject();
                                            notification.put("title", "Multiple Reserved Table Cancelled");
                                            notification.put("click_action", "Table Frag");
                                            notification.put("body", "Your Reserved Tables are cancelled by the owner\n" + editText.getText().toString() + "");
                                            main.put("notification", notification);
                                            dialogInterface.dismiss();
                                            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, main, new Response.Listener<JSONObject>() {
                                                @Override
                                                public void onResponse(JSONObject response) {

                                                }
                                            }, error -> Toast.makeText(view.getContext(), error.getLocalizedMessage() + "null", Toast.LENGTH_SHORT).show()) {
                                                @Override
                                                public Map<String, String> getHeaders() {
                                                    Map<String, String> header = new HashMap<>();
                                                    header.put("content-type", "application/json");
                                                    header.put("authorization", "key=AAAAsigSEMs:APA91bEUF9ZFwIu84Jctci56DQd0TQOepztGOIKIBhoqf7N3ueQrkClw0xBTlWZEWyvwprXZmZgW2MNywF1pNBFpq1jFBr0CmlrJ0wygbZIBOnoZ0jP1zZC6nPxqF2MAP6iF3wuBHD2R");
                                                    return header;
                                                }
                                            };

                                            requestQueue.add(jsonObjectRequest);
                                        } catch (Exception e) {
                                            Toast.makeText(view.getContext(), e.getLocalizedMessage() + "null", Toast.LENGTH_SHORT).show();
                                        }

                                    FirebaseAuth auth = FirebaseAuth.getInstance();

                                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(sharedPreferences.getString("state","")).child(sharedPreferences.getString("locality","")).child(Objects.requireNonNull(auth.getUid())).child("Tables");
                                        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                                                    List<String> myLists = map.get(""+tables.get(position));
                                                    if(dataSnapshot.hasChild("customerId") && dataSnapshot.child("customerId").getValue(String.class).equals(myLists.get(0)) && dataSnapshot.child("status").getValue(String.class).equals("MultipleReserved")){
                                                        databaseReference.child(dataSnapshot.getKey()).child("customerId").removeValue();
                                                        databaseReference.child(dataSnapshot.getKey()).child("timeOfUnavailability").removeValue();
                                                        databaseReference.child(dataSnapshot.getKey()).child("time").removeValue();
                                                        databaseReference.child(dataSnapshot.getKey()).child("timeInMillis").removeValue();
                                                        databaseReference.child(dataSnapshot.getKey()).child("timeOfBooking").removeValue();
                                                        databaseReference.child(dataSnapshot.getKey()).child("status").setValue("available");
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });


//                                        for(int i2=0;i2<status.size();i2++) {
//                                            if(status.get(position).equals("MultipleReserved")) {
//                                                reference.child(tables.get(i2)).child("customerId").removeValue();
//                                                reference.child(tables.get(i2)).child("status").setValue("available");
//                                                reference.child(tables.get(i2)).child("time").removeValue();
//                                                reference.child(tables.get(i2)).child("timeInMillis").removeValue();
//                                                reference.child(tables.get(i2)).child("timeOfBooking").removeValue();
////                                    reference.child(tables.get(position)).child("time").removeValue();
//                                                holder.chatWith.setVisibility(View.INVISIBLE);
//                                                holder.cancel.setVisibility(View.INVISIBLE);
//                                                holder.status.setText("available");
//                                            }
////                                    holder.timeOfReserved.setVisibility(View.INVISIBLE);
//                                        }
                                }
                            });
                            alertD.setNegativeButton("No, Wait", (dialogInterface, i) -> {
                                dialogInterface.dismiss();
                            });

                            alertD.create().show();



                        }).show();



            });

        }

        if(status.get(position).equals("MultipleUnavailable")){
            holder.checkBox.setVisibility(View.INVISIBLE);
            List<String> myList = map.get(""+tables.get(position));
            holder.chatWith.setVisibility(View.VISIBLE);
            holder.timeOfReserved.setVisibility(View.VISIBLE);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm:ss");
            Date date = new Date(Long.parseLong(timeOfUnavailability.get(position)));
            holder.timeOfReserved.setText(simpleDateFormat.format(date) + "");
            holder.cancel.setVisibility(View.VISIBLE);
//            holder.timeOfReserved.setVisibility(View.VISIBLE);
//            holder.timeOfReserved.setText(myList.get(1)+"");
            holder.chatWith.setOnClickListener(view -> {
                Intent intent = new Intent(view.getContext(),ChatWithCustomer.class);
                assert myList != null;
                intent.putExtra("id",myList.get(0));
                view.getContext().startActivity(intent);
            });

            holder.cancel.setOnClickListener(view -> {
                SharedPreferences sharedPreferences = view.getContext().getSharedPreferences("loginInfo",Context.MODE_PRIVATE);
                reference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(sharedPreferences.getString("state","")).child(sharedPreferences.getString("locality","")).child(Objects.requireNonNull(auth.getUid())).child("Tables");
                new KAlertDialog(view.getContext(),KAlertDialog.WARNING_TYPE)
                        .setTitleText("Warning!!!")
                        .setContentText("Do you sure wanna remove this reserved table??")
                        .setCancelText("No")
                        .setCancelClickListener(KAlertDialog::dismissWithAnimation)
                        .setConfirmText("Yes")
                        .setConfirmClickListener(kAlertDialog -> {
                            AlertDialog.Builder alertD = new AlertDialog.Builder(view.getContext());
                            alertD.setTitle("Important");
                            alertD.setMessage("Enter reason for cancellation of table below");
                            LinearLayout linearLayout = new LinearLayout(view.getContext());
                            EditText editText = new EditText(view.getContext());
                            editText.setMaxLines(200);
                            editText.setHint("Enter reason here");
                            linearLayout.setOrientation(LinearLayout.VERTICAL);
                            linearLayout.addView(editText);
                            alertD.setView(linearLayout);
                            alertD.setPositiveButton("Proceed", (dialogInterface, i) -> {
                                if(!editText.getText().toString().equals("")) {
                                    kAlertDialog.dismissWithAnimation();
                                    RequestQueue requestQueue = Volley.newRequestQueue(view.getContext());
                                    JSONObject main = new JSONObject();
                                    try {

                                        assert myList != null;
                                        main.put("to", "/topics/" + myList.get(0) + "");
                                        JSONObject notification = new JSONObject();
                                        notification.put("title", "Table Cancelled");
                                        notification.put("click_action", "Table Frag");
                                        notification.put("body", "Your Tables is cancelled by the owner\n" + editText.getText().toString() + "");
                                        main.put("notification", notification);
                                        dialogInterface.dismiss();
                                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, main, response -> {

                                        }, error -> Toast.makeText(view.getContext(), error.getLocalizedMessage() + "null", Toast.LENGTH_SHORT).show()) {
                                            @Override
                                            public Map<String, String> getHeaders() {
                                                Map<String, String> header = new HashMap<>();
                                                header.put("content-type", "application/json");
                                                header.put("authorization", "key=AAAAsigSEMs:APA91bEUF9ZFwIu84Jctci56DQd0TQOepztGOIKIBhoqf7N3ueQrkClw0xBTlWZEWyvwprXZmZgW2MNywF1pNBFpq1jFBr0CmlrJ0wygbZIBOnoZ0jP1zZC6nPxqF2MAP6iF3wuBHD2R");
                                                return header;
                                            }
                                        };

                                        requestQueue.add(jsonObjectRequest);
                                    } catch (Exception e) {
                                        Toast.makeText(view.getContext(), e.getLocalizedMessage() + "null", Toast.LENGTH_SHORT).show();
                                    }


                                    FirebaseAuth auth = FirebaseAuth.getInstance();

                                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(sharedPreferences.getString("state","")).child(sharedPreferences.getString("locality","")).child(auth.getUid()).child("Tables");
                                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                                                List<String> myLists = map.get(""+dataSnapshot.getKey());
                                                if(dataSnapshot.hasChild("customerId") && dataSnapshot.child("customerId").getValue(String.class).equals(myLists.get(0)) && dataSnapshot.child("status").getValue(String.class).equals("MultipleUnavailable")){
                                                    databaseReference.child(dataSnapshot.getKey()).child("customerId").removeValue();
                                                    databaseReference.child(dataSnapshot.getKey()).child("timeOfUnavailability").removeValue();
                                                    databaseReference.child(dataSnapshot.getKey()).child("status").setValue("available");
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                    reference.child(tables.get(position)).child("customerId").removeValue();
                                    reference.child(tables.get(position)).child("timeOfUnavailability").removeValue();
                                    reference.child(tables.get(position)).child("status").setValue("available");
                                    reference.child(tables.get(position)).child("time").removeValue();
                                    holder.chatWith.setVisibility(View.INVISIBLE);
                                    holder.cancel.setVisibility(View.INVISIBLE);
                                    holder.status.setText("available");
                                    holder.timeOfReserved.setVisibility(View.INVISIBLE);

                                }
                            });
                            alertD.setNegativeButton("No, Wait", (dialogInterface, i) -> {

                            });

                            alertD.create().show();


                        }).show();



            });
        }
        holder.cardView.setOnClickListener(v -> {
            SharedPreferences sharedPreferences = v.getContext().getSharedPreferences("loginInfo",Context.MODE_PRIVATE);
            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            FirebaseStorage storage;
            StorageReference storageReference;
            storage = FirebaseStorage.getInstance();
            storageReference = storage.getReference();
            StorageReference imageRef = storageReference.child(firebaseAuth.getUid() + "/" + tables.get(position));
//                StorageReference imageRef = storageReference.child(firebaseAuth.getUid() + "/" + )
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(v.getContext());
            alertDialog.setTitle("Important");
            alertDialog.setMessage("Choose one option");
            alertDialog.setCancelable(true);
            alertDialog.setPositiveButton("Get QR Code", (dialog, which) -> {
                dialog.dismiss();
                imageRef.getDownloadUrl().addOnSuccessListener(uri -> Picasso.get()
                        .load("" + uri)
                        .into(new Target() {
                                  @Override
                                  public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                      try {
                                          String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString();
                                          File myDir = new File(root);

                                          if (!myDir.exists()) {
                                              myDir.mkdirs();
                                          }

                                          String name = "Table " + tables.get(position) +  ".jpg";
                                          myDir = new File(myDir, name);
                                          FileOutputStream out = new FileOutputStream(myDir);
                                          bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                                          Toast.makeText(context, "Image Saved Successfully", Toast.LENGTH_SHORT).show();
                                          PdfDocument pdfDocument = new PdfDocument();
                                          Paint myPaint = new Paint();
                                          PdfDocument.PageInfo myPage = new  PdfDocument.PageInfo.Builder(1024,720,1).create();
                                          PdfDocument.Page page = pdfDocument.startPage(myPage);

                                          Paint text = new Paint();

                                          Canvas canvas = page.getCanvas();

                                          canvas.drawBitmap(bitmap,275,70,myPaint);
                                          text.setTextAlign(Paint.Align.LEFT);
                                          text.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.BOLD));
                                          text.setTextSize(70);
                                          canvas.drawText("Table Number: " + tables.get(position),275,85,text);
                                          canvas.drawText("Seats: " + seats.get(position),395,595,text);

                                          pdfDocument.finishPage(page);

                                          String fileNames = "/Table " + tables.get(position)+ ".pdf";
                                          File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) + fileNames);

                                          try{
                                              pdfDocument.writeTo(new FileOutputStream(file));
                                              Intent target = new Intent(Intent.ACTION_VIEW);
                                              target.setDataAndType(FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider",file), "application/pdf"); // here we set correct type for PDF

                                              target.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                              Intent intent = Intent.createChooser(target, "Open File");
                                              try {
                                                  v.getContext().startActivity(intent);
                                              } catch (ActivityNotFoundException e) {
                                                  // Instruct the user to install a PDF reader here, or something
                                              }
                                          } catch (IOException e) {
                                              e.printStackTrace();
                                          }

                                          pdfDocument.close();
                                          out.flush();
                                          out.close();
                                      } catch(Exception e){
                                          // some action
                                      }
                                  }

                                  @Override
                                  public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                                  }

                                  @Override
                                  public void onPrepareLoad(Drawable placeHolderDrawable) {

                                  }
                              }
                        )).addOnFailureListener(e -> Toast.makeText(context, "Failed :) " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show());
            });
            alertDialog.setNeutralButton("Open PDF", (dialogInterface, i) -> {
                String fileName = "/Table " + tables.get(position) + ".pdf";
                File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) + fileName);
                if(file.exists()){
                    Intent target = new Intent(Intent.ACTION_VIEW);
                    target.setDataAndType(FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider",file), "application/pdf"); // here we set correct type for PDF

                    target.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    Intent intent = Intent.createChooser(target, "Open File");
                    try {
                        v.getContext().startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        // Instruct the user to install a PDF reader here, or something
                    }
                }else{

                }
            });
            alertDialog.setNegativeButton("Delete Table", (dialog, which) -> {
                if(status.get(position).equals("available")) {
                    SharedPreferences sharedPreferences1 = v.getContext().getSharedPreferences("loginInfo", Context.MODE_PRIVATE);
                    reference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(sharedPreferences1.getString("state", "")).child(sharedPreferences1.getString("locality","")).child(Objects.requireNonNull(auth.getUid())).child("Tables");
                    reference.child(tables.get(position)).removeValue();
                    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                    firestore.collection(sharedPreferences.getString("state","")).document("Restaurants").collection(sharedPreferences.getString("locality","")).document(auth.getUid())
                            .collection("Tables").document(tables.get(position)).delete();
                    new KAlertDialog(v.getContext(),KAlertDialog.SUCCESS_TYPE)
                            .setTitleText("Success")
                            .setContentText("Table Deleted Successfully")
                            .setConfirmText("Exit")
                            .setConfirmClickListener(KAlertDialog::dismissWithAnimation).show();
                    dialog.dismiss();
                }else{
                    new KAlertDialog(v.getContext(),KAlertDialog.ERROR_TYPE)
                            .setTitleText("Error")
                            .setContentText("Cannot delete table while it's unavailable or reserved")
                            .setConfirmText("Exit")
                            .setConfirmClickListener(KAlertDialog::dismissWithAnimation).show();
                }
            });
            if(status.get(position).equals("unavailable") || status.get(position).equals("Reserved")){
                alertDialog.setNeutralButton("Get User Details", (dialogInterface, i) -> {
                    List<String> myList = map.get(""+tables.get(position));
                    assert myList != null;
                    reference = FirebaseDatabase.getInstance().getReference().getRoot().child("Users").child(myList.get(0));
                    reference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            AlertDialog.Builder alert = new AlertDialog.Builder(v.getContext());
                            alert.setTitle("Customer Details");
                            Toast.makeText(v.getContext(), "Long press to copy text", Toast.LENGTH_SHORT).show();
                            TextView contact = new TextView(v.getContext());
                            TextView email = new TextView(v.getContext());
                            TextView name = new TextView(v.getContext());
                            LinearLayout linearLayout = new LinearLayout(v.getContext());
                            linearLayout.setOrientation(LinearLayout.VERTICAL);
                            contact.setTextSize(18);
                            email.setTextSize(18);
                            name.setTextSize(18);
                            name.setPadding(6,6,6,6);
                            email.setPadding(6,6,6,6);
                            contact.setPadding(6,6,6,6);
                            name.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                            email.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                            contact.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

                            if(snapshot.hasChild("name")){
                                name.setText(String.valueOf(snapshot.child("name").getValue()));
                                linearLayout.addView(name);

                            }

                            if(snapshot.hasChild("email")){
                                email.setText(String.valueOf(snapshot.child("email").getValue()));
                                linearLayout.addView(email);

                            }

                            if(snapshot.hasChild("number")){
                                contact.setText(String.valueOf(snapshot.child("number").getValue()));
                                linearLayout.addView(contact);
                            }

                            name.setOnLongClickListener(click -> {
                                Toast.makeText(click.getContext(), "Text Copied", Toast.LENGTH_SHORT).show();
                                ClipboardManager clipboard = (ClipboardManager) click.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                                ClipData clip = ClipData.newPlainText("Text Copied", name.getText().toString());
                                clipboard.setPrimaryClip(clip);
                                return true;
                            });
                            email.setOnLongClickListener(click -> {
                                Toast.makeText(click.getContext(), "Text Copied", Toast.LENGTH_SHORT).show();
                                ClipboardManager clipboard = (ClipboardManager) click.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                                ClipData clip = ClipData.newPlainText("Text Copied", email.getText().toString());
                                clipboard.setPrimaryClip(clip);
                                return true;
                            });
                            contact.setOnLongClickListener(click -> {
                                Toast.makeText(click.getContext(), "Text Copied", Toast.LENGTH_SHORT).show();
                                ClipboardManager clipboard = (ClipboardManager) click.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                                ClipData clip = ClipData.newPlainText("Text Copied", contact.getText().toString());
                                clipboard.setPrimaryClip(clip);
                                return true;
                            });

                            alert.setView(linearLayout);
                            alert.setPositiveButton("Exit", (dialogInterface12, i12) -> dialogInterface12.dismiss());

                            alert.setNegativeButton("Report & Ban", (dialogInterface1, i1) -> {
                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(sharedPreferences.getString("state","")).child(sharedPreferences.getString("locality","")).child(Objects.requireNonNull(auth.getUid()));
                                databaseReference.child("Blocked List").child(myList.get(0)).child("authId").setValue(myList.get(0));
                                updateReportValue(myList.get(0));
                                dialogInterface1.dismiss();
                            }).setNeutralButton("Don't ban just report", (dialogInterface13, i13) -> {
                                dialogInterface13.dismiss();
                                updateReportValue(myList.get(0));
                            });



                            alert.create().show();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                });

            }

            alertDialog.create().show();
        });
    }

    private void updateReportValue(String userID) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Users").child(userID);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.hasChild("reports")){
                    databaseReference.child("reports").setValue("1");
                }else
                {
                    int num = Integer.parseInt(String.valueOf(snapshot.child("reports").getValue()));
                    num = num + 1;
                    databaseReference.child("reports").setValue(num + "");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return tables.size();
    }

    public static class TableAdapter extends RecyclerView.ViewHolder{
        TextView tableNum,status,chatWith,cancel,timeOfReserved;
        CardView cardView;
        CheckBox checkBox;
        public TableAdapter(@NonNull View itemView) {
            super(itemView);
            tableNum = itemView.findViewById(R.id.numberOfTable);
            status = itemView.findViewById(R.id.statusOfTable);
            chatWith = itemView.findViewById(R.id.chatWithCustomer);
            cardView = itemView.findViewById(R.id.adminTableCardView);
            cancel = itemView.findViewById(R.id.cancelSeat);
            timeOfReserved = itemView.findViewById(R.id.timeOfReservedTable);
            checkBox = itemView.findViewById(R.id.enabledTableCheckBox);
        }
    }
}
