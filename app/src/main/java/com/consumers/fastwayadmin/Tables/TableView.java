package com.consumers.fastwayadmin.Tables;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.consumers.fastwayadmin.R;
import com.developer.kalert.KAlertDialog;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TableView extends RecyclerView.Adapter<TableView.TableAdapter> {
    List<String> tables = new ArrayList<>();
    List<String> status = new ArrayList<>();
    HashMap<String,List<String>> map = new HashMap<>();
    String URL = "https://fcm.googleapis.com/fcm/send";
    DatabaseReference reference;
    Context context;
    FirebaseAuth auth;
    public TableView(List<String> tables,List<String> status,HashMap<String,List<String>> map,Context context){
        this.status = status;
        this.map = map;
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
    public void onBindViewHolder(@NonNull TableAdapter holder, int position) {
        auth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(auth.getUid()).child("Tables");
        holder.tableNum.setText("Table Number : " + tables.get(position));
        holder.status.setText(status.get(position));
        if(status.get(position).equals("Reserved")){
            List<String> myList = map.get(""+tables.get(position));
            holder.chatWith.setVisibility(View.VISIBLE);
            holder.cancel.setVisibility(View.VISIBLE);
            holder.timeOfReserved.setVisibility(View.VISIBLE);
            holder.timeOfReserved.setText(myList.get(1)+"");
            holder.chatWith.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(view.getContext(),ChatWithCustomer.class);
                    intent.putExtra("id",myList.get(0));
                    view.getContext().startActivity(intent);
                }
            });

            holder.cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new KAlertDialog(view.getContext(),KAlertDialog.WARNING_TYPE)
                            .setTitleText("Warning!!!")
                            .setContentText("Do you sure wanna remove this reserved table??")
                            .setCancelText("No")
                            .setCancelClickListener(new KAlertDialog.KAlertClickListener() {
                                @Override
                                public void onClick(KAlertDialog kAlertDialog) {
                                    kAlertDialog.dismissWithAnimation();
                                }
                            })
                            .setConfirmText("Yes")
                            .setConfirmClickListener(new KAlertDialog.KAlertClickListener() {
                                @Override
                                public void onClick(KAlertDialog kAlertDialog) {
                                    RequestQueue requestQueue = Volley.newRequestQueue(view.getContext());
                                    JSONObject main = new JSONObject();
                                    try{
                                        main.put("to","/topics/"+myList.get(0)+"");
                                        JSONObject notification = new JSONObject();
                                        notification.put("title","Cancelled" );
                                        notification.put("click_action","Table Frag");
                                        notification.put("body","Your Reserved Tables is cancelled by the owner");
                                        main.put("notification",notification);

                                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, main, new Response.Listener<JSONObject>() {
                                            @Override
                                            public void onResponse(JSONObject response) {

                                            }
                                        }, new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                Toast.makeText(view.getContext(), error.getLocalizedMessage()+"null", Toast.LENGTH_SHORT).show();
                                            }
                                        }){
                                            @Override
                                            public Map<String, String> getHeaders() throws AuthFailureError {
                                                Map<String,String> header = new HashMap<>();
                                                header.put("content-type","application/json");
                                                header.put("authorization","key=AAAAsigSEMs:APA91bEUF9ZFwIu84Jctci56DQd0TQOepztGOIKIBhoqf7N3ueQrkClw0xBTlWZEWyvwprXZmZgW2MNywF1pNBFpq1jFBr0CmlrJ0wygbZIBOnoZ0jP1zZC6nPxqF2MAP6iF3wuBHD2R");
                                                return header;
                                            }
                                        };

                                        requestQueue.add(jsonObjectRequest);
                                    }
                                    catch (Exception e){
                                        Toast.makeText(view.getContext(), e.getLocalizedMessage()+"null", Toast.LENGTH_SHORT).show();
                                    }
                                    reference.child(tables.get(position)).child("customerId").removeValue();
                                    reference.child(tables.get(position)).child("status").setValue("available");
                                    reference.child(tables.get(position)).child("time").removeValue();
                                    holder.chatWith.setVisibility(View.INVISIBLE);
                                    holder.cancel.setVisibility(View.INVISIBLE);
                                    holder.status.setText("available");
                                    holder.timeOfReserved.setVisibility(View.INVISIBLE);
                                    kAlertDialog.dismissWithAnimation();

                                }
                            }).show();



                }
            });
        }
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                alertDialog.setPositiveButton("Get QR Code", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Picasso.get()
                                        .load("" + uri)
                                        .into(new Target() {
                                                  @Override
                                                  public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                                      try {
                                                          String root = Environment.getExternalStorageDirectory().toString();
                                                          File myDir = new File(root);

                                                          if (!myDir.exists()) {
                                                              myDir.mkdirs();
                                                          }

                                                          String name = new Date().toString() + ".jpg";
                                                          myDir = new File(myDir, name);
                                                          FileOutputStream out = new FileOutputStream(myDir);
                                                          bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);

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
                                        );
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(context, "Failed :) " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                });
                alertDialog.setNegativeButton("Delete Table", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                alertDialog.create().show();
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
        public TableAdapter(@NonNull View itemView) {
            super(itemView);
            tableNum = itemView.findViewById(R.id.numberOfTable);
            status = itemView.findViewById(R.id.statusOfTable);
            chatWith = itemView.findViewById(R.id.chatWithCustomer);
            cardView = itemView.findViewById(R.id.adminTableCardView);
            cancel = itemView.findViewById(R.id.cancelSeat);
            timeOfReserved = itemView.findViewById(R.id.timeOfReservedTable);
        }
    }
}
