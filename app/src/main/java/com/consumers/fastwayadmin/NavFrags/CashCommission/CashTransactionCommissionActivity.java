package com.consumers.fastwayadmin.NavFrags.CashCommission;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialog;

import com.consumers.fastwayadmin.R;
import com.developer.kalert.KAlertDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class CashTransactionCommissionActivity extends AppCompatActivity {
    DatabaseReference databaseReference;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    TextView totalCashTransaction,totalCommission;
    Button payCommissionNow;
    int commissionAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cash_transaction_commission);
        totalCashTransaction = findViewById(R.id.totalCashTakeAwayCommission);
        totalCommission = findViewById(R.id.totalCommissionToBePaidByAdmin);
        payCommissionNow = findViewById(R.id.PayCommissionNowButton);
        databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Admin").child(Objects.requireNonNull(auth.getUid()));
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChild("totalCashTakeAway")){
                    totalCashTransaction.setText("Total Cash Transactions " + "\u20B9" + snapshot.child("totalCashTakeAway").getValue(String.class));
                    int totalCash = Integer.parseInt(Objects.requireNonNull(snapshot.child("totalCashTakeAway").getValue(String.class)));
                     commissionAmount = (totalCash * 7)/100;
                    totalCommission.setText("Commission to be paid " + "\u20B9" + commissionAmount);
                    payCommissionNow.setText("Pay \u20B9" + commissionAmount + " Now");
                    payCommissionNow.setOnClickListener(view -> {
                        if(snapshot.hasChild("lastCommissionPaid")){
                            long lastCommission = Long.parseLong(String.valueOf(snapshot.child("lastCommissionPaid").getValue()));

                        }
                        AlertDialog.Builder builder = new AlertDialog.Builder(CashTransactionCommissionActivity.this);
                        builder.setTitle("Pay Commission").setMessage("Do you sure wanna proceed to pay commission")
                                .setNegativeButton("No", (dialogInterface, i) -> dialogInterface.dismiss()).setPositiveButton("Yes", (dialogInterface, i) -> {
                                    dialogInterface.dismiss();
                                    Intent intent = new Intent(CashTransactionCommissionActivity.this, CashFreeGateway.class);
                                    intent.putExtra("amount",commissionAmount + "");
                                    startActivityForResult(intent,2);
                                }).create();
                        builder.show();
                    });
                }else{
                    KAlertDialog kAlertDialog = new KAlertDialog(CashTransactionCommissionActivity.this,KAlertDialog.ERROR_TYPE)
                            .setTitleText("No Transaction Made")
                            .setContentText("No current amount due")
                            .setConfirmText("Exit")
                            .setConfirmClickListener(click -> {
                                click.dismiss();
                                finish();
                            });

                    kAlertDialog.setCancelable(false);
                    kAlertDialog.show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == 2){
            KAlertDialog kAlertDialog = new KAlertDialog(CashTransactionCommissionActivity.this,KAlertDialog.SUCCESS_TYPE)
                    .setTitleText("Success")
                    .setContentText("Payment made successfully")
                    .setConfirmText("Exit")
                    .setConfirmClickListener(kAlertDialog1 -> {
                        kAlertDialog1.dismiss();
                        finish();
                    });

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Admin").child(Objects.requireNonNull(auth.getUid()));
            databaseReference.child("totalCashTakeAway").setValue("0");
            databaseReference.child("lastCommissionPaid").setValue(String.valueOf(System.currentTimeMillis()));

            kAlertDialog.setCancelable(false);
            kAlertDialog.show();
        }else if(resultCode == 3){
            KAlertDialog kAlertDialog = new KAlertDialog(CashTransactionCommissionActivity.this,KAlertDialog.ERROR_TYPE)
                    .setTitleText("Error")
                    .setContentText("Payment Failed\nIf amount deducted if will get refunded in 2-3 business days")
                    .setConfirmText("Exit")
                    .setConfirmClickListener(AppCompatDialog::dismiss);
            kAlertDialog.setCancelable(false);
            kAlertDialog.show();
        }
    }
}