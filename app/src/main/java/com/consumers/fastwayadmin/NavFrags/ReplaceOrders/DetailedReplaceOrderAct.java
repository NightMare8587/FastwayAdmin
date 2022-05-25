package com.consumers.fastwayadmin.NavFrags.ReplaceOrders;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.consumers.fastwayadmin.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class DetailedReplaceOrderAct extends AppCompatActivity {
    TextView userName,reportTime,orderTime,detailedWhatHappened,tableOrTake;
    Button acceptOrder,declineOrder,checkOrders;
    ImageView imageView;
    ProgressBar progressBar;
    List<String> dishName = new ArrayList<>();
    List<String> timesOrdered = new ArrayList<>();
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