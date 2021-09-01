package com.consumers.fastwayadmin.NavFrags.homeFrag;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
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

public class ApproveCurrentOrder extends AppCompatActivity {
    DatabaseReference databaseReference;
    FirebaseAuth auth;
    ListView listView,halfOrList;
    String URL = "https://fcm.googleapis.com/fcm/send";
    ProgressBar progressBar;
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
        halfOrList = findViewById(R.id.halfOrFullCurrentORder);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    dishNames.add(dataSnapshot.getKey().toString());
                    dishQuantity.add(String.valueOf(dataSnapshot.child("timesOrdered").getValue()));
                    dishHalfOr.add(String.valueOf(dataSnapshot.child("halfOr").getValue()));
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
}