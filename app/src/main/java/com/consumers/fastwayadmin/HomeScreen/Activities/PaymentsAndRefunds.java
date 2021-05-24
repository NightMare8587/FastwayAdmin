package com.consumers.fastwayadmin.HomeScreen.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.consumers.fastwayadmin.HomeScreen.PaymentQueries.PaymentFeeQuery;
import com.consumers.fastwayadmin.HomeScreen.PaymentQueries.PaymentOptionQuery;
import com.consumers.fastwayadmin.HomeScreen.RefundQueries.RefundCancellationQuery;
import com.consumers.fastwayadmin.R;

import java.util.ArrayList;
import java.util.List;

public class PaymentsAndRefunds extends AppCompatActivity {
    List<String> payment = new ArrayList<>();
    List<String> refunds = new ArrayList<>();
    ListView paymentList;
    ListView refundList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payments_and_refunds);
        initialise();
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this,R.layout.list,payment);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this,R.layout.list,refunds);

        paymentList.setAdapter(adapter1);
        refundList.setAdapter(adapter2);

        paymentList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        startActivity(new Intent(PaymentsAndRefunds.this, PaymentOptionQuery.class));
                        break;
                    case 1:
                        startActivity(new Intent(PaymentsAndRefunds.this, PaymentFeeQuery.class));
                        break;
                }
            }
        });
        refundList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        startActivity(new Intent(PaymentsAndRefunds.this, RefundCancellationQuery.class));
                        break;
                }
            }
        });
    }

    private void initialise() {
        payment.add("What are the payment options available to make payment");
        payment.add("What is the transaction fees on each transaction");

        refunds.add("How much refund on cancellation of subscription");
        paymentList = findViewById(R.id.paymentListView);
        refundList = findViewById(R.id.refundListView);


    }
}