package com.consumers.fastwayadmin.NavFrags.CashCommission;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.consumers.fastwayadmin.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

public class CashTransactionCommissionActivity extends AppCompatActivity {
    DatabaseReference databaseReference;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cash_transaction_commission);
    }
}