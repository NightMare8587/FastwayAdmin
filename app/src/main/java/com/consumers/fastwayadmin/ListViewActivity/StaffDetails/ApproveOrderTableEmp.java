package com.consumers.fastwayadmin.ListViewActivity.StaffDetails;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.consumers.fastwayadmin.HomeScreen.ReportSupport.RequestRefundClass;
import com.consumers.fastwayadmin.NavFrags.CurrentTakeAwayOrders.MyClass;
import com.consumers.fastwayadmin.NavFrags.homeFrag.ApproveCurrentOrder;
import com.consumers.fastwayadmin.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ApproveOrderTableEmp extends AppCompatActivity {
    DatabaseReference databaseReference;
    int totalPrice = 0;
    String time;
    DatabaseReference storeTotalAmountMonth;
    String state;
    ListView listView,halfOrList;

    String UID;
    SharedPreferences sharedPreferences;
    int veg = 0;
    String URL = "https://fcm.googleapis.com/fcm/send";
    ProgressBar progressBar;
    int nonVeg = 0;
    int vegan = 0;
    double amountToBeSend;
    String id;
    ListView dishQ;
    List<String> dishNames = new ArrayList<>();
    List<String> dishQuantity = new ArrayList<>();
    List<String> image = new ArrayList<>();
    List<String> dishPrices = new ArrayList<>();
    List<String> orderAndPayment = new ArrayList<>();
    Calendar calendar = Calendar.getInstance();
    List<String> dishHalfOr = new ArrayList<>();
    TextView textView;
    List<String> type = new ArrayList<>();
    Button approve,decline,showCustomisation;
    String orderID,orderAmount,paymentType,customisation;
    String table;
    File path;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approve_current_order);
        listView = findViewById(R.id.currentOrderListView);
        approve = findViewById(R.id.approveCurrentOrderButton);
        dishQ = findViewById(R.id.quantityCurrentOrder);
        id = getIntent().getStringExtra("id");
        state = getIntent().getStringExtra("state");
        decline = findViewById(R.id.declineCurrentOrderButton);
        progressBar = findViewById(R.id.currentOrderProgressBar);
        textView = findViewById(R.id.tabeNumApproveCurrentOrder);
        sharedPreferences = getSharedPreferences("loginInfo",MODE_PRIVATE);
        UID = sharedPreferences.getString("resID","");
        storeTotalAmountMonth = FirebaseDatabase.getInstance().getReference().getRoot().child("Admin").child(UID);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SuspiciousIndentation")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    dishNames.add(dataSnapshot.getKey());
                    dishQuantity.add(String.valueOf(dataSnapshot.child("timesOrdered").getValue()));
                    dishHalfOr.add(String.valueOf(dataSnapshot.child("halfOr").getValue()));
                    orderID = String.valueOf(dataSnapshot.child("orderID").getValue());
                    image.add(String.valueOf(dataSnapshot.child("image").getValue()));
                    dishPrices.add(String.valueOf(dataSnapshot.child("price").getValue()));
                    orderAndPayment.add(String.valueOf(dataSnapshot.child("orderAndPayment").getValue()));
                    paymentType = String.valueOf(dataSnapshot.child("orderAndPayment").getValue());
                    orderAmount = String.valueOf(dataSnapshot.child("orderAmount").getValue());
                    time = String.valueOf(dataSnapshot.child("time").getValue());
                    totalPrice = totalPrice + Integer.parseInt(String.valueOf(dataSnapshot.child("price").getValue()));
                    customisation = String.valueOf(dataSnapshot.child("customisation").getValue());
                    type.add(String.valueOf(dataSnapshot.child("type").getValue()));
                }
                String[] arr = paymentType.split(",");
                paymentType = arr[2];
                progressBar.setVisibility(View.INVISIBLE);
                uploadToArrayAdapter(dishNames,dishQuantity,dishHalfOr);


                if(!customisation.equals("")){
                    AlertDialog.Builder alert = new AlertDialog.Builder(ApproveOrderTableEmp.this);
                    alert.setTitle("Customisation").setMessage("User has added following customisation to his/her order made\n\n" + customisation).setPositiveButton("Exit", (dialogInterface, i) -> {
                        dialogInterface.dismiss();
                        showCustomisation.setVisibility(View.VISIBLE);
                    }).create();

                    alert.show();
                }

                if(System.currentTimeMillis() - Long.parseLong(time) >= 600000){
                    AlertDialog.Builder builder = new AlertDialog.Builder(ApproveOrderTableEmp.this);
                    RequestQueue requestQueue = Volley.newRequestQueue(ApproveOrderTableEmp.this);
                    JSONObject main = new JSONObject();
//                    new GenratePDF().execute();
                    FirebaseAuth auth = FirebaseAuth.getInstance();
                    String approveTime = String.valueOf(System.currentTimeMillis());
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Users").child(id).child("Recent Orders").child(time);
                    for(int i=0;i<dishNames.size();i++){
                        MyClass myClass = new MyClass(dishNames.get(i),dishPrices.get(i),image.get(i),type.get(i),""+approveTime,dishQuantity.get(i),dishHalfOr.get(i),state,String.valueOf(orderAmount),orderID,orderAndPayment.get(i),"Order Declined",sharedPreferences.getString("locality",""));
                        databaseReference.child(UID).child(dishNames.get(i)).setValue(myClass);
                    }


                    databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(state).child(sharedPreferences.getString("locality","")).child(UID);
                    for(int i=0;i<dishNames.size();i++){
                        MyClass myClass = new MyClass(dishNames.get(i),dishPrices.get(i),image.get(i),type.get(i),""+approveTime,dishQuantity.get(i),dishHalfOr.get(i),state,String.valueOf(orderAmount),orderID,orderAndPayment.get(i),"Order Declined",sharedPreferences.getString("locality",""));
                        databaseReference.child("Recent Orders").child("" + time).child(id).child(dishNames.get(i)).setValue(myClass);
                    }
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(state).child(sharedPreferences.getString("locality","")).child(UID).child("Tables").child(table);
                    if(!paymentType.equals("endCheckOut")) {
                        DatabaseReference requestRefundOrdinalo = FirebaseDatabase.getInstance().getReference().getRoot().child("Complaints").child("RefundRequest").child(UID);
                        RequestRefundClass requestRefundClass = new RequestRefundClass(orderID,orderAmount,time,"Order Cancelled because not approved/denied by restaurant");
                        requestRefundOrdinalo.setValue(requestRefundClass);

                        runOnUiThread(() -> {
                            Toast.makeText(ApproveOrderTableEmp.this, "Refund Request Initiated", Toast.LENGTH_SHORT).show();
                        });

//                        new InitiateRefund().execute();
                    }
                    try {
                        main.put("to", "/topics/" + id + "");
                        JSONObject notification = new JSONObject();
                        notification.put("title", "Order Cancelled");
                        notification.put("click_action", "Table Frag");
                        notification.put("body", "Your order is cancelled because it was neither approved not denied. Refund will be initiated Shortly");
                        main.put("notification", notification);

                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, main, response -> {

                        }, error -> Toast.makeText(ApproveOrderTableEmp.this, error.getLocalizedMessage() + "null", Toast.LENGTH_SHORT).show()) {
                            @Override
                            public Map<String, String> getHeaders() {
                                Map<String, String> header = new HashMap<>();
                                header.put("content-type", "application/json");
                                header.put("authorization", "key=AAAAjq_WsHs:APA91bGZV-uH-NJddxIniy8h1tDGDHqxhgvFdyNRDaV_raxjvSM_FkKu7JSwtSp4Q_iSmPuTKGGIB2M_07c9rKgPXUH43-RzpK6zkaSaIaNgmeiwUO40rYxYUZAkKoLAQQVeVJ7mXboD");
                                return header;
                            }
                        };
                        reference.child("Current Order").removeValue();
                        requestQueue.add(jsonObjectRequest);
                    } catch (Exception e) {
                        Toast.makeText(ApproveOrderTableEmp.this, e.getLocalizedMessage() + "null", Toast.LENGTH_SHORT).show();
                    }
                    builder.setTitle("Cancelled").setMessage("Order is cancelled automatically due to inactivity")
                            .setPositiveButton("Exit", (dialogInterface, i) -> new Handler().postDelayed(() -> finish(),300)).create();
                    builder.setCancelable(false);
                    builder.show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        approve.setOnClickListener(click -> {
            if (paymentType.equals("instantCheckOut")) {
                approve.setEnabled(false);
                DatabaseReference orderToBeSynced = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(state).child(state).child(UID);
                updateTotalAmount(orderAmount);
                RequestQueue requestQueue = Volley.newRequestQueue(ApproveOrderTableEmp.this);
                JSONObject main = new JSONObject();
                DatabaseReference totalOrders = FirebaseDatabase.getInstance().getReference().child("Restaurants").child(sharedPreferences.getString("state","")).child(sharedPreferences.getString("locality","")).child(UID);
                totalOrders.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.hasChild("totalOrdersMade")) {
                            int totalOrder = Integer.parseInt(String.valueOf(snapshot.child("totalOrdersMade").getValue()));
                            totalOrder = totalOrder + 1;
                            totalOrders.child("totalOrdersMade").setValue(totalOrder);
                        } else {
                            totalOrders.child("totalOrdersMade").setValue("1");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
    }
    private void uploadToArrayAdapter(List<String> dishNames,List<String> dishQuantity,List<String> dishHalfOr) {
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, dishNames);
        listView.setAdapter(arrayAdapter);

        ArrayAdapter<String> arrayAdapter1 = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, dishQuantity);
        dishQ.setAdapter(arrayAdapter1);

        ArrayAdapter<String> arrayAdapter2 = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, dishHalfOr);
        halfOrList.setAdapter(arrayAdapter2);
    }

    private void updateTotalAmount(String valueOf) {
        storeTotalAmountMonth.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChild("totalMonthAmount")){
                    Double current = Double.parseDouble(valueOf);
                    Double existingVal = Double.parseDouble(String.valueOf(snapshot.child("totalMonthAmount").getValue()));
                    Double finalVal = current + existingVal;
                    storeTotalAmountMonth.child("totalMonthAmount").setValue(String.valueOf(finalVal));
                }else
                    storeTotalAmountMonth.child("totalMonthAmount").setValue(valueOf);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}