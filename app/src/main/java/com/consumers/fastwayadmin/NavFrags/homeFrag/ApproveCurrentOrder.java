package com.consumers.fastwayadmin.NavFrags.homeFrag;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.consumers.fastwayadmin.CancelClass;
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
import java.util.Random;

public class ApproveCurrentOrder extends AppCompatActivity {
    DatabaseReference databaseReference;
    FirebaseAuth auth;
    ListView listView,halfOrList;
    String URL = "https://fcm.googleapis.com/fcm/send";
    String url = "https://intercellular-stabi.000webhostapp.com/refunds/initiateRefund.php";
    ProgressBar progressBar;
    DatabaseReference saveRefundInfo;
    String orderID,orderAmount;
    String table;
    String id;
    ListView dishQ;
    List<String> dishNames = new ArrayList<>();
    List<String> dishQuantity = new ArrayList<>();
    List<String> dishHalfOr = new ArrayList<>();
    Button approve,decline;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approve_current_order);
        table = getIntent().getStringExtra("table");
        id = getIntent().getStringExtra("id");
        initialise();
        saveRefundInfo = FirebaseDatabase.getInstance().getReference().getRoot().child("Users").child(id);
        halfOrList = findViewById(R.id.halfOrFullCurrentORder);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    dishNames.add(dataSnapshot.getKey().toString());
                    dishQuantity.add(String.valueOf(dataSnapshot.child("timesOrdered").getValue()));
                    dishHalfOr.add(String.valueOf(dataSnapshot.child("halfOr").getValue()));
                    orderID = String.valueOf(dataSnapshot.child("orderID").getValue());
                    orderAmount = String.valueOf(dataSnapshot.child("orderAmount").getValue());
                }
                progressBar.setVisibility(View.INVISIBLE);
                uploadToArrayAdapter(dishNames,dishQuantity,dishHalfOr);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        approve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestQueue requestQueue = Volley.newRequestQueue(ApproveCurrentOrder.this);
                JSONObject main = new JSONObject();
                FirebaseAuth auth = FirebaseAuth.getInstance();
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(Objects.requireNonNull(auth.getUid())).child("Tables").child(table);

                try{
                    main.put("to","/topics/"+id+"");
                    JSONObject notification = new JSONObject();
                    notification.put("title","Order Approved" );
                    notification.put("click_action","Table Frag");
                    notification.put("body","Your order is approved by the owner");
                    main.put("notification",notification);

                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, main, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(ApproveCurrentOrder.this, error.getLocalizedMessage()+"null", Toast.LENGTH_SHORT).show();
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
                    reference.child("Current Order").removeValue();
                    requestQueue.add(jsonObjectRequest);
                }
                catch (Exception e){
                    Toast.makeText(ApproveCurrentOrder.this, e.getLocalizedMessage()+"null", Toast.LENGTH_SHORT).show();
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                },1500);

            }
        });

        decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestQueue requestQueue = Volley.newRequestQueue(ApproveCurrentOrder.this);
                JSONObject main = new JSONObject();
                FirebaseAuth auth = FirebaseAuth.getInstance();
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(Objects.requireNonNull(auth.getUid())).child("Tables").child(table);
                new InitiateRefund().execute();
                try{
                    main.put("to","/topics/"+id+"");
                    JSONObject notification = new JSONObject();
                    notification.put("title","Order Declined" );
                    notification.put("click_action","Table Frag");
                    notification.put("body","Your order is declined by the owner. Refund will be initiated Shortly");
                    main.put("notification",notification);

                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, main, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(ApproveCurrentOrder.this, error.getLocalizedMessage()+"null", Toast.LENGTH_SHORT).show();
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
                    reference.child("Current Order").removeValue();
                    requestQueue.add(jsonObjectRequest);
                }
                catch (Exception e){
                    Toast.makeText(ApproveCurrentOrder.this, e.getLocalizedMessage()+"null", Toast.LENGTH_SHORT).show();
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                },1500);
            }


        });


    }

    private void uploadToArrayAdapter(List<String> dishNames,List<String> dishQuantity,List<String> dishHalfOr) {
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,dishNames);
        listView.setAdapter(arrayAdapter);

        ArrayAdapter<String> arrayAdapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,dishQuantity);
        dishQ.setAdapter(arrayAdapter1);

        ArrayAdapter<String> arrayAdapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,dishHalfOr);
        halfOrList.setAdapter(arrayAdapter2);
    }

    private void initialise() {
        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(Objects.requireNonNull(auth.getUid())).child("Tables").child(table).child("Current Order");
        listView = findViewById(R.id.currentOrderListView);
        approve = findViewById(R.id.approveCurrentOrderButton);
        dishQ = findViewById(R.id.quantityCurrentOrder);
        decline = findViewById(R.id.declineCurrentOrderButton);
        progressBar = findViewById(R.id.currentOrderProgressBar);
    }
    public class InitiateRefund extends AsyncTask<Void,Void,Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            RequestQueue requestQueue = Volley.newRequestQueue(ApproveCurrentOrder.this);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.i("res",response.toString());

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }){
                @NonNull
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String> params = new HashMap<>();
                    FirebaseAuth auth = FirebaseAuth.getInstance();
                    String referid = id;
                    int value = Integer.parseInt(orderAmount);
                    int finalValue = (int) (value * 0.2);
                    value = value - finalValue;
                    orderAmount = String.valueOf(value);
                    Random random = new Random();
                    referid = referid + String.valueOf(random.nextInt(1000 - 1) + 1);
                    String finalReferIDForInfo = "refund_" + referid + "s";
                    Log.i("refundID",referid);
                    String time = String.valueOf(System.currentTimeMillis());
                    CancelClass cancelClass = new CancelClass(finalReferIDForInfo,orderAmount + "",orderID + "");
                    saveRefundInfo.child("Refunds").child(time).setValue(cancelClass);
                    params.put("referID",referid + "");
                    params.put("refundAmount",orderAmount);
                    params.put("orderID",orderID);
                    return params;
                }
            };
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                    0,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(stringRequest);
            return null;
        }
    }
}