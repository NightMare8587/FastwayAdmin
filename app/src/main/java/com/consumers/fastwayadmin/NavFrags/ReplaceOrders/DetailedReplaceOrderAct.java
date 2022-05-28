package com.consumers.fastwayadmin.NavFrags.ReplaceOrders;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Shader;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
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
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class DetailedReplaceOrderAct extends AppCompatActivity {
    TextView userName,reportTime,orderTime,detailedWhatHappened,tableOrTake;
    Button acceptOrder,declineOrder,checkOrders;
    ImageView imageView;
    String URL = "https://fcm.googleapis.com/fcm/send";
    ProgressBar progressBar;
    List<String> dishName = new ArrayList<>();
    List<String> timesOrdered = new ArrayList<>();
    SharedPreferences sharedPreferences;
    List<String> type = new ArrayList<>();
    DateFormat simple = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
    String userID;
    String orderTiming;
    String orderID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_replace_order);
        initialise();
        sharedPreferences = getSharedPreferences("loginInfo",MODE_PRIVATE);
        userName.setText(getIntent().getStringExtra("name"));
        userID = getIntent().getStringExtra("userID");
        if(getIntent().getStringExtra("tableNum").equals("TakeAway"))
        tableOrTake.setText(getIntent().getStringExtra("tableNum"));
        else
            tableOrTake.setText("Table Num: " + getIntent().getStringExtra("tableNum"));
        Date report = new Date(Long.parseLong(getIntent().getStringExtra("reportTime")));
        Date order = new Date(Long.parseLong(getIntent().getStringExtra("orderTime")));
        reportTime.setText("Report Time: " + simple.format(report));
        orderTime.setText("Order Time: " + simple.format(order));
        orderTiming = getIntent().getStringExtra("orderTime");
        detailedWhatHappened.setText(getIntent().getStringExtra("details"));
        new GetOrdersDetails().execute();
        Picasso.get().load(getIntent().getStringExtra("imageUri")).into(imageView, new Callback() {
            @Override
            public void onSuccess() {
                progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onError(Exception e) {
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
        checkOrders.setOnClickListener(click -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(DetailedReplaceOrderAct.this);
            builder.setTitle("Details").setMessage("Orders to be replaced");
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.res_info_dialog_layout,null);
            ArrayAdapter<String> arrayAdapter1 = new ArrayAdapter<>(DetailedReplaceOrderAct.this, android.R.layout.simple_list_item_1, dishName);
            ArrayAdapter<String> arrayAdapter2 = new ArrayAdapter<>(DetailedReplaceOrderAct.this, android.R.layout.simple_list_item_1, timesOrdered);
            ListView listView1 = view.findViewById(R.id.listDishNamesResInfo);
            listView1.setAdapter(arrayAdapter1);
            ListView listView2 = view.findViewById(R.id.listDishNamesQuantityInfo);
            listView2.setAdapter(arrayAdapter2);
            builder.setView(view);
            builder.setPositiveButton("exit", (dialogInterface, i) -> dialogInterface.dismiss()).create().show();
        });

        acceptOrder.setOnClickListener(click -> {

        });

        declineOrder.setOnClickListener(click -> {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            DatabaseReference admin = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(sharedPreferences.getString("state","")).child(auth.getUid()).child("ReplaceOrderRequests");
            admin.child(getIntent().getStringExtra("reportTime")).removeValue();
            RequestQueue requestQueue = Volley.newRequestQueue(DetailedReplaceOrderAct.this);
            JSONObject main = new JSONObject();
            try {
                main.put("to", "/topics/" + userID + "");
                JSONObject notification = new JSONObject();
                notification.put("title", "Replace Order Request Declined");
                notification.put("click_action", "Table Frag");
                notification.put("body", "Your Replace Order Request is declined by the restaurant owner");
                main.put("notification", notification);

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, main, response -> {

                }, error -> Toast.makeText(DetailedReplaceOrderAct.this, error.getLocalizedMessage() + "null", Toast.LENGTH_SHORT).show()) {
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
                Toast.makeText(DetailedReplaceOrderAct.this, e.getLocalizedMessage() + "null", Toast.LENGTH_SHORT).show();
            }

            finish();
        });

    }

    private void initialise() {
        userName = findViewById(R.id.detailedReplaceUserName);
        orderTime = findViewById(R.id.detailedReplaceOrderTime);
        reportTime = findViewById(R.id.detailedReplaceReprtingTime);
        tableOrTake = findViewById(R.id.detailedReplaceTableOrTake);
        detailedWhatHappened = findViewById(R.id.detailedReplaceWhatHappened);
        acceptOrder = findViewById(R.id.detailedReplaceApproveButton);
        declineOrder = findViewById(R.id.detailedReplaceCancelRequestButton);
        checkOrders = findViewById(R.id.detailedReplaceCheckOrdersButton);
        imageView = findViewById(R.id.detailedReplaceImageView);
        progressBar = findViewById(R.id.progressBardetailedReplace);
        orderID = getIntent().getStringExtra("orderId");
    }
    public class GetOrdersDetails extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Users").child(userID).child("Recent Orders").child(orderTiming).child(Objects.requireNonNull(auth.getUid()));
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                            dishName.add(dataSnapshot.child("name").getValue(String.class));
                            type.add(dataSnapshot.child("type").getValue(String.class));
                            timesOrdered.add(dataSnapshot.child("timesOrdered").getValue(String.class));
                        }
                        Log.i("infoses",dishName.toString() + "\n" + type.toString() + "\n" + timesOrdered.toString());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            return null;
        }
    }
}