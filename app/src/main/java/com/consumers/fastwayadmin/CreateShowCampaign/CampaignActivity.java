package com.consumers.fastwayadmin.CreateShowCampaign;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.consumers.fastwayadmin.NavFrags.homeFrag.ApproveCurrentOrder;
import com.consumers.fastwayadmin.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class CampaignActivity extends AppCompatActivity {
    DatabaseReference databaseReference;
    TextView currentCampText;
    String locality,resName;
    List<String> timeMillis = new ArrayList<>();
    List<String> campNamesList = new ArrayList<>();
    String URL = "https://fcm.googleapis.com/fcm/send";
    List<String> campCustomersList = new ArrayList<>();
    List<String> campOrdersList = new ArrayList<>();
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    List<String> campTransList = new ArrayList<>();
    boolean isCurr = false;
    Map<String,String> currentMap = new HashMap<>();
    TextView campCurrentName,campCurrentCustomers,campCurrentOrders,campCurrentTransactions;
    Button removeCampButton,createNewCamp;
    LinearLayout linearLayout;
    DatabaseReference checkPrevCamp;
    RecyclerView recyclerView;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_campaign);
        databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Admin").child(Objects.requireNonNull(auth.getUid())).child("Current Campaign");
        checkPrevCamp = FirebaseDatabase.getInstance().getReference().getRoot().child("Admin").child(Objects.requireNonNull(auth.getUid())).child("Previous Camp");
        currentCampText = findViewById(R.id.currentCampaignTextViewAboveLinearLayout);
        linearLayout = findViewById(R.id.linearLayout8);
        SharedPreferences resInfo = getSharedPreferences("RestaurantInfo",MODE_PRIVATE);
        resName = resInfo.getString("hotelName","");
        sharedPreferences = getSharedPreferences("loginInfo",MODE_PRIVATE);
        editor = sharedPreferences.edit();
        locality = sharedPreferences.getString("locality","");
        if(!sharedPreferences.contains("shownCampaignDialog")){
            AlertDialog.Builder builder = new AlertDialog.Builder(CampaignActivity.this);
            builder.setTitle("Important").setMessage("Every campaign you create costs \u20b915. This amount is added to your monthly platform fee payment.\n\nFor any queries contact Foodine")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            editor.putString("shownCampaignDialog","yes");
                            editor.apply();
                        }
                    }).create().show();
            builder.setCancelable(false);
        }
        createNewCamp = findViewById(R.id.createAddNewCampaign);
        campCurrentCustomers = findViewById(R.id.campCurrentTotalCust);
        campCurrentName = findViewById(R.id.campCurrentName);
        recyclerView = findViewById(R.id.recyclerViewPrevAdap);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        campCurrentOrders = findViewById(R.id.campCurrentTotalOrders);
        campCurrentTransactions = findViewById(R.id.campCurrentTotalTransAmount);
        removeCampButton = findViewById(R.id.deleteCurrentCampButton);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    isCurr = true;
                    createNewCamp.setVisibility(View.VISIBLE);
                    currentCampText.setVisibility(View.VISIBLE);
                    linearLayout.setVisibility(View.VISIBLE);
                    currentMap.put("campName",snapshot.child("campName").getValue(String.class));
                    currentMap.put("totalCustomers",snapshot.child("totalCustomers").getValue(String.class));
                    currentMap.put("totalOrders",snapshot.child("totalOrders").getValue(String.class));
                    currentMap.put("totalTransAmount",snapshot.child("totalTransAmount").getValue(String.class));

                    campCurrentName.setText("Campaign Name: " + snapshot.child("campName").getValue(String.class));
                    campCurrentOrders.setText("Total Orders: " + snapshot.child("totalOrders").getValue(String.class));
                    campCurrentTransactions.setText("Total Transactions: \u20b9" + snapshot.child("totalTransAmount").getValue(String.class));
                    campCurrentCustomers.setText("Total Customers: " + snapshot.child("totalCustomers").getValue(String.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        AsyncTask.execute(() -> checkPrevCamp.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                        timeMillis.add(dataSnapshot.getKey());
                        campCustomersList.add(dataSnapshot.child("totalCustomers").getValue(String.class));
                        campTransList.add(dataSnapshot.child("totalTransAmount").getValue(String.class));
                        campOrdersList.add(dataSnapshot.child("totalOrders").getValue(String.class));
                        campNamesList.add(dataSnapshot.child("campName").getValue(String.class));
                    }
                    recyclerView.setAdapter(new PrevCampAdap(timeMillis,campNamesList,campCustomersList,campOrdersList,campTransList));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        }));

        createNewCamp.setOnClickListener(click -> {
            if(isCurr)
                Toast.makeText(this, "You already have a campaign", Toast.LENGTH_SHORT).show();
            else{
                AlertDialog.Builder builder = new AlertDialog.Builder(CampaignActivity.this);
                builder.setTitle("Campaign Create").setMessage("Enter details in below fields to create a campaign");
                LinearLayout linearLayoutCamp = new LinearLayout(this);
                linearLayoutCamp.setOrientation(LinearLayout.VERTICAL);
                EditText campName = new EditText(this);
                campName.setHint("Enter Campaign Name");
                campName.setMaxLines(30);
                EditText campDiscount = new EditText(this);
                campDiscount.setHint("Enter Discount Number");
                campDiscount.setInputType(InputType.TYPE_CLASS_NUMBER);
                linearLayoutCamp.addView(campName);
                linearLayoutCamp.addView(campDiscount);

                builder.setView(linearLayoutCamp);
                builder.setPositiveButton("Create Campaign", (dialog, which) -> {
                    if(campName.length() == 0)
                        return;
                    if(campDiscount.length() == 0)
                        return;
                    if(campDiscount.getText().toString().equals("0"))
                        return;
                    DatabaseReference addCamp = FirebaseDatabase.getInstance().getReference().getRoot().child("Admin").child(auth.getUid());
                    Map<String,String> map = new HashMap<>();
                    map.put("campName",campName.getText().toString());
                    map.put("campDiscount",campDiscount.getText().toString());
                    map.put("totalCustomers","0");
                    map.put("totalOrders","0");
                    map.put("totalTransAmount","0");

                    currentMap.put("campName",campName.getText().toString());
//                        currentMap.put("campDiscount",campDiscount.getText().toString());
                    currentMap.put("totalCustomers","0");
                    currentMap.put("totalOrders","0");
                    currentMap.put("totalTransAmount","0");
                    addCamp.child("Current Campaign").setValue(map);
                    Toast.makeText(CampaignActivity.this, "Campaign Created Successfully", Toast.LENGTH_SHORT).show();
                    createNewCamp.setVisibility(View.VISIBLE);
                    currentCampText.setVisibility(View.VISIBLE);
                    linearLayout.setVisibility(View.VISIBLE);


                    campCurrentName.setText(campName.getText().toString());
                    campCurrentCustomers.setText("Total Customers: 0");
                    campCurrentOrders.setText("Total Orders: 0");
                    campCurrentTransactions.setText("Total Transaction: \u20b90");
                    AsyncTask.execute(() -> {
                        DatabaseReference addAmount = FirebaseDatabase.getInstance().getReference().getRoot().child("Admin").child(auth.getUid());
                        addAmount.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.hasChild("totalMonthAmount")){
                                    double val = Double.parseDouble(String.valueOf(snapshot.child("totalMonthAmount").getValue()));
                                    val += 15;
                                    addAmount.child("totalMonthAmount").setValue(String.valueOf(val));
                                }else {
                                    double val = 15D;
                                    addAmount.child("totalMonthAmount").setValue(String.valueOf(val));
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        try {
                            RequestQueue requestQueue = Volley.newRequestQueue(CampaignActivity.this);
                            JSONObject main = new JSONObject();
                            main.put("to", "/topics/" + locality.replaceAll("\\s+","") + "");
                            JSONObject notification = new JSONObject();
                            notification.put("title", resName);
                            notification.put("click_action", "Table Frag");
                            notification.put("body", "Use Code " + campName.getText().toString() + " at " + resName + " and get discount up-to " + campDiscount.getText().toString() + "%.\nLimited Time period offer");
                            main.put("notification", notification);

                            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, main, response -> {

                            }, error -> Toast.makeText(CampaignActivity.this, error.getLocalizedMessage() + "null", Toast.LENGTH_SHORT).show()) {
                                @Override
                                public Map<String, String> getHeaders() {
                                    Map<String, String> header = new HashMap<>();
                                    header.put("content-type", "application/json");
                                    header.put("authorization", "key=AAAAjq_WsHs:APA91bGZV-uH-NJddxIniy8h1tDGDHqxhgvFdyNRDaV_raxjvSM_FkKu7JSwtSp4Q_iSmPuTKGGIB2M_07c9rKgPXUH43-RzpK6zkaSaIaNgmeiwUO40rYxYUZAkKoLAQQVeVJ7mXboD");
                                    return header;
                                }
                            };
//                            reference.child("Current Order").removeValue();
                            requestQueue.add(jsonObjectRequest);
                        } catch (Exception e) {
                            Toast.makeText(CampaignActivity.this, e.getLocalizedMessage() + "null", Toast.LENGTH_SHORT).show();
                        }
                    });

                    isCurr = true;

                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Toast.makeText(CampaignActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
                    }
                }).create();
                builder.show();
            }
        });

        removeCampButton.setOnClickListener(click -> {
            Toast.makeText(this, "Campaign Removed Successfully", Toast.LENGTH_SHORT).show();
            isCurr = false;
            linearLayout.setVisibility(View.INVISIBLE);
            currentCampText.setVisibility(View.INVISIBLE);
            databaseReference.removeValue();
            DatabaseReference addCampToPrev = FirebaseDatabase.getInstance().getReference().getRoot().child("Admin").child(auth.getUid());
            addCampToPrev.child("Previous Camp").child(System.currentTimeMillis() + "").setValue(currentMap);

            AsyncTask.execute(() -> checkPrevCamp.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        timeMillis.clear();
                        campCustomersList.clear();
                        campTransList.clear();
                        campOrdersList.clear();
                        campNamesList.clear();
                        for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                            timeMillis.add(dataSnapshot.getKey());
                            campCustomersList.add(dataSnapshot.child("totalCustomers").getValue(String.class));
                            campTransList.add(dataSnapshot.child("totalTransAmount").getValue(String.class));
                            campOrdersList.add(dataSnapshot.child("totalOrders").getValue(String.class));
                            campNamesList.add(dataSnapshot.child("campName").getValue(String.class));
                        }
                        recyclerView.setAdapter(new PrevCampAdap(timeMillis,campNamesList,campCustomersList,campOrdersList,campTransList));
                    }else{
                        timeMillis.clear();
                        campCustomersList.clear();
                        campTransList.clear();
                        campOrdersList.clear();
                        campNamesList.clear();
                        recyclerView.setAdapter(new PrevCampAdap(timeMillis,campNamesList,campCustomersList,campOrdersList,campTransList));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            }));
        });
    }
}