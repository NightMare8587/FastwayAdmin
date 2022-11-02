package com.consumers.fastwayadmin.ListViewActivity.CashTrans;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.consumers.fastwayadmin.R;
import com.developer.kalert.KAlertDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CashTransactions extends AppCompatActivity {
    FirebaseAuth auth = FirebaseAuth.getInstance();
    DatabaseReference reference;
    RecyclerView recyclerView;
    List<String> orderID = new ArrayList<>();
    List<String> orderAmount = new ArrayList<>();
    List<String> time = new ArrayList<>();
    List<String> userID = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cash_transactions);
        recyclerView = findViewById(R.id.cashTransactionRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        reference = FirebaseDatabase.getInstance().getReference().getRoot().child("Admin").child(Objects.requireNonNull(auth.getUid())).child("Cash Transactions");
        reference.limitToLast(20).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()){
                    KAlertDialog kAlertDialog = new KAlertDialog(CashTransactions.this,KAlertDialog.ERROR_TYPE)
                            .setTitleText("No Transactions")
                            .setContentText("You have no cash transactions at this momment")
                            .setConfirmText("Exit")
                            .setConfirmClickListener(click -> {
                                click.dismissWithAnimation();
                                finish();
                            });

                    kAlertDialog.setCancelable(false);
                    kAlertDialog.show();
                }else{
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                        orderAmount.add(String.valueOf(dataSnapshot.child("orderAmount").getValue()));
                        orderID.add(String.valueOf(dataSnapshot.child("orderId").getValue()));
                        time.add(String.valueOf(dataSnapshot.child("time").getValue()));
                        userID.add(String.valueOf(dataSnapshot.child("userID").getValue()));
                    }
                recyclerView.setAdapter(new CashAdapter(orderID,orderAmount,time,userID,CashTransactions.this));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}