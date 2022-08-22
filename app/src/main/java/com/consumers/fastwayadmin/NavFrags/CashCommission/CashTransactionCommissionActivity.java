package com.consumers.fastwayadmin.NavFrags.CashCommission;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialog;

import com.consumers.fastwayadmin.NavFrags.FastwayPremiumActivites.FastwayPremiums;
import com.consumers.fastwayadmin.R;
import com.developer.kalert.KAlertDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.Format;
import java.util.Objects;

public class CashTransactionCommissionActivity extends AppCompatActivity {
    DatabaseReference databaseReference;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    TextView totalCashTransaction,totalCommission,platformFee;
    Button payCommissionNow;
    TextView seeBreakDown;
    boolean platformFeeBool = false;
    Double platformFeeAmount;
    double gstToBePaid;
    double commissionAmount;
    DecimalFormat df = new DecimalFormat("0.00");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cash_transaction_commission);
        totalCashTransaction = findViewById(R.id.totalCashTakeAwayCommission);
        totalCommission = findViewById(R.id.totalCommissionToBePaidByAdmin);
        seeBreakDown = findViewById(R.id.seeFeesBreakdownAdminAppCash);
        platformFee = findViewById(R.id.platformFeeToBePaid);
        payCommissionNow = findViewById(R.id.PayCommissionNowButton);
        databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Admin").child(Objects.requireNonNull(auth.getUid()));
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChild("totalCashTakeAway")){
                    if(snapshot.hasChild("lastCommissionPaid")){
                        long date = Long.parseLong(Objects.requireNonNull(snapshot.child("lastCommissionPaid").getValue(String.class)));
                        if(System.currentTimeMillis() - date >= 2073600000L){
                            totalCashTransaction.setText("Total Cash Transactions " + "\u20B9" + snapshot.child("totalCashTakeAway").getValue(String.class));
                            double totalCash = Double.parseDouble(Objects.requireNonNull(snapshot.child("totalCashTakeAway").getValue(String.class)));
                            commissionAmount = (totalCash * 3)/100;
                            gstToBePaid = (totalCash * 5)/100;
                            if(snapshot.hasChild("totalMonthAmount")){
                                platformFeeAmount = Double.parseDouble(Objects.requireNonNull(snapshot.child("totalMonthAmount").getValue(String.class)));
                                if(platformFeeAmount == 0D){
                                    platformFeeAmount = 0D;
                                }else if(platformFeeAmount >= 400000L){
                                    platformFeeAmount = (platformFeeAmount / 100) * 4;
                                }else if(platformFeeAmount >= 300000L){
                                    platformFeeAmount = 12000D;
                                }
                                else if(platformFeeAmount >= 200000L){
                                    platformFeeAmount = 8000D;
                                }else if(platformFeeAmount >= 100000){
                                    platformFeeAmount = 4000D;
                                }else if(platformFeeAmount >= 50000){
                                    platformFeeAmount = 2000D;
                                }else
                                    platformFeeAmount = 1000D;
                                platformFee.setText("Platform Fee: " + "\u20B9" + df.format(platformFeeAmount));
                                platformFeeBool = true;
                            }else{
                                platformFeeAmount = 1000D;
                                platformFee.setText("Platform Fee: " + "\u20B91000");
                            }
                            totalCommission.setText("Commission to be paid " + "\u20B9" + df.format(commissionAmount + gstToBePaid));
                            payCommissionNow.setText("Pay \u20B9" + df.format(commissionAmount + platformFeeAmount + gstToBePaid) + " Now");
                            seeBreakDown.setOnClickListener(click -> {
                                AlertDialog.Builder dialog = new AlertDialog.Builder(CashTransactionCommissionActivity.this);
                                dialog.setTitle("Breakdown").setMessage("Below you can see breakdown of fees and gst to be paid");
                                LinearLayout linearLayout = new LinearLayout(CashTransactionCommissionActivity.this);
                                linearLayout.setOrientation(LinearLayout.VERTICAL);
                                TextView commissionAmountText = new TextView(CashTransactionCommissionActivity.this);
                                commissionAmountText.setGravity(View.TEXT_ALIGNMENT_CENTER);
                                commissionAmountText.setTextSize(18);
                                TextView gstAmount = new TextView(CashTransactionCommissionActivity.this);
                                gstAmount.setGravity(View.TEXT_ALIGNMENT_CENTER);
                                gstAmount.setTextSize(18);
                                TextView platformFee = new TextView(CashTransactionCommissionActivity.this);
                                platformFee.setGravity(View.TEXT_ALIGNMENT_CENTER);
                                platformFee.setTextSize(18);
                                commissionAmountText.setText("Cash Commission Amount: " +  df.format(commissionAmount));
                                gstAmount.setText("5% Gst: " + df.format(gstToBePaid) + "");
                                platformFee.setText("Platform Fee: " + df.format(platformFeeAmount) + "");

                                TextView totalSummary = new TextView(CashTransactionCommissionActivity.this);
                                totalSummary.setGravity(View.TEXT_ALIGNMENT_CENTER);
                                totalSummary.setTextSize(18);
                                totalSummary.setTextColor(Color.BLACK);
                                totalSummary.setText("Final Amount: " + df.format(commissionAmount + platformFeeAmount + gstToBePaid) + "");

                                linearLayout.addView(commissionAmountText);
                                linearLayout.addView(gstAmount);
                                linearLayout.addView(platformFee);
                                linearLayout.addView(totalSummary);

                                dialog.setView(linearLayout);
                                dialog.setPositiveButton("Exit", (dialog1, which) -> dialog1.dismiss()).create();
                                dialog.create();
                                dialog.show();
                            });
                            payCommissionNow.setOnClickListener(view -> {
                                SharedPreferences acc = getSharedPreferences("loginInfo",MODE_PRIVATE);
                                SharedPreferences.Editor acEdit = acc.edit();
                                if(!acc.contains("number")) {
                                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(CashTransactionCommissionActivity.this);
                                    builder.setTitle("Contact").setMessage("You need to provide contact number for payment. No need to add +91");
                                    LinearLayout linearLayout = new LinearLayout(CashTransactionCommissionActivity.this);
                                    linearLayout.setOrientation(LinearLayout.VERTICAL);
                                    EditText editText = new EditText(CashTransactionCommissionActivity.this);
                                    editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                                    editText.setHint("Enter Number");
                                    editText.setMaxLines(10);
                                    linearLayout.addView(editText);
                                    builder.setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            if(editText.length() == 10)
                                            {
                                                Toast.makeText(CashTransactionCommissionActivity.this, "Number Saved Successfully", Toast.LENGTH_SHORT).show();
                                                acEdit.putString("number",editText.getText().toString());
                                                acEdit.apply();

                                                Intent intent = new Intent(CashTransactionCommissionActivity.this, CashFreeGateway.class);
                                                intent.putExtra("amount","599.00");
                                                startActivityForResult(intent,2);
                                            }else
                                                Toast.makeText(CashTransactionCommissionActivity.this, "Invalid Number", Toast.LENGTH_SHORT).show();
                                        }
                                    }).setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss()).create();
                                    builder.setCancelable(false);
                                    builder.setView(linearLayout);
                                    builder.show();
                                    return;
                                }

                                if(commissionAmount != 0) {
                                    SharedPreferences cash = getSharedPreferences("CashCommission",MODE_PRIVATE);
                                    if(cash.contains("fine")) {
                                        String fine = cash.getString("fine","");
                                        AlertDialog.Builder builder = new AlertDialog.Builder(CashTransactionCommissionActivity.this);
                                        builder.setTitle("Pay Commission").setMessage("Do you sure wanna proceed to pay commission\nFine of " + fine + "% will be applied")
                                                .setNegativeButton("No", (dialogInterface, i) -> dialogInterface.dismiss()).setPositiveButton("Yes", (dialogInterface, i) -> {
                                                    dialogInterface.dismiss();
                                                    double fineAmount = (commissionAmount * 10)/100;
                                                    commissionAmount = commissionAmount + fineAmount;
                                                    Intent intent = new Intent(CashTransactionCommissionActivity.this, CashFreeGateway.class);
                                                    intent.putExtra("amount", df.format(commissionAmount + platformFeeAmount) + "");
                                                    startActivityForResult(intent, 2);
                                                }).create();
                                        builder.show();
                                    }else{
                                        AlertDialog.Builder builder = new AlertDialog.Builder(CashTransactionCommissionActivity.this);
                                        builder.setTitle("Pay Commission").setMessage("Do you sure wanna proceed to pay commission")
                                                .setNegativeButton("No", (dialogInterface, i) -> dialogInterface.dismiss()).setPositiveButton("Yes", (dialogInterface, i) -> {
                                                    dialogInterface.dismiss();
                                                    Intent intent = new Intent(CashTransactionCommissionActivity.this, CashFreeGateway.class);

                                                    intent.putExtra("amount", (commissionAmount + platformFeeAmount) + "");
                                                    startActivityForResult(intent, 2);
                                                }).create();
                                        builder.show();
                                    }
                                }else
                                    Toast.makeText(CashTransactionCommissionActivity.this, "No commission amount pending", Toast.LENGTH_SHORT).show();
                            });
                        }else{
                            KAlertDialog kAlertDialog = new KAlertDialog(CashTransactionCommissionActivity.this,KAlertDialog.ERROR_TYPE)
                                    .setTitleText("Too early")
                                    .setContentText("No current amount due")
                                    .setConfirmText("Exit")
                                    .setConfirmClickListener(click -> {
                                        click.dismiss();
                                        finish();
                                    })
                                            .setCancelText("Leaving Fastway").setCancelClickListener(click -> {
                                                click.dismissWithAnimation();
                                        totalCashTransaction.setText("Total Cash Transactions " + "\u20B9" + snapshot.child("totalCashTakeAway").getValue(String.class));
                                        double totalCash = Double.parseDouble(Objects.requireNonNull(snapshot.child("totalCashTakeAway").getValue(String.class)));
                                        commissionAmount = (totalCash * 7)/100;
                                        if(snapshot.hasChild("totalMonthAmount")){
                                            platformFeeAmount = Double.parseDouble(Objects.requireNonNull(snapshot.child("totalMonthAmount").getValue(String.class)));
                                            if(platformFeeAmount == 0D){
                                                platformFeeAmount = 0D;
                                            }else if(platformFeeAmount >= 400000L){
                                                platformFeeAmount = (platformFeeAmount / 100) * 4;
                                            }else if(platformFeeAmount >= 300000L){
                                                platformFeeAmount = 12000D;
                                            }
                                            else if(platformFeeAmount >= 200000L){
                                                platformFeeAmount = 8000D;
                                            }else if(platformFeeAmount >= 100000){
                                                platformFeeAmount = 4000D;
                                            }else if(platformFeeAmount >= 50000){
                                                platformFeeAmount = 2000D;
                                            }else
                                                platformFeeAmount = 1000D;
                                            platformFee.setText("Platform Fee: " + "\u20B9" + df.format(platformFeeAmount));
                                            platformFeeBool = true;
                                        }else{
                                            platformFeeAmount = 1000D;
                                            platformFee.setText("Platform Fee: " + "\u20B91000");
                                        }
                                        totalCommission.setText("Commission to be paid " + "\u20B9" + df.format(commissionAmount));
                                        payCommissionNow.setText("Pay \u20B9" + df.format(commissionAmount + platformFeeAmount) + " Now");
                                        payCommissionNow.setOnClickListener(view -> {
                                            SharedPreferences acc = getSharedPreferences("loginInfo",MODE_PRIVATE);
                                            SharedPreferences.Editor acEdit = acc.edit();
                                            if(!acc.contains("number")) {
                                                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(CashTransactionCommissionActivity.this);
                                                builder.setTitle("Contact").setMessage("You need to provide contact number for payment. No need to add +91");
                                                LinearLayout linearLayout = new LinearLayout(CashTransactionCommissionActivity.this);
                                                linearLayout.setOrientation(LinearLayout.VERTICAL);
                                                EditText editText = new EditText(CashTransactionCommissionActivity.this);
                                                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                                                editText.setHint("Enter Number");
                                                editText.setMaxLines(10);
                                                linearLayout.addView(editText);
                                                builder.setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.dismiss();
                                                        if(editText.length() == 10)
                                                        {
                                                            Toast.makeText(CashTransactionCommissionActivity.this, "Number Saved Successfully", Toast.LENGTH_SHORT).show();
                                                            acEdit.putString("number",editText.getText().toString());
                                                            acEdit.apply();

                                                            Intent intent = new Intent(CashTransactionCommissionActivity.this, CashFreeGateway.class);
                                                            intent.putExtra("amount","599.00");
                                                            startActivityForResult(intent,2);
                                                        }else
                                                            Toast.makeText(CashTransactionCommissionActivity.this, "Invalid Number", Toast.LENGTH_SHORT).show();
                                                    }
                                                }).setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss()).create();
                                                builder.setCancelable(false);
                                                builder.setView(linearLayout);
                                                builder.show();
                                                return;
                                            }
                                            if(commissionAmount != 0) {
                                                SharedPreferences cash = getSharedPreferences("CashCommission",MODE_PRIVATE);
                                                if(cash.contains("fine")) {
                                                    String fine = cash.getString("fine","");
                                                    AlertDialog.Builder builder = new AlertDialog.Builder(CashTransactionCommissionActivity.this);
                                                    builder.setTitle("Pay Commission").setMessage("Do you sure wanna proceed to pay commission\nFine of " + fine + "% will be applied")
                                                            .setNegativeButton("No", (dialogInterface, i) -> dialogInterface.dismiss()).setPositiveButton("Yes", (dialogInterface, i) -> {
                                                                dialogInterface.dismiss();
                                                                double fineAmount = (commissionAmount * 10)/100;
                                                                commissionAmount = commissionAmount + fineAmount;
                                                                Intent intent = new Intent(CashTransactionCommissionActivity.this, CashFreeGateway.class);
                                                                intent.putExtra("amount", df.format(commissionAmount + platformFeeAmount) + "");
                                                                startActivityForResult(intent, 2);
                                                            }).create();
                                                    builder.show();
                                                }else{
                                                    AlertDialog.Builder builder = new AlertDialog.Builder(CashTransactionCommissionActivity.this);
                                                    builder.setTitle("Pay Commission").setMessage("Do you sure wanna proceed to pay commission")
                                                            .setNegativeButton("No", (dialogInterface, i) -> dialogInterface.dismiss()).setPositiveButton("Yes", (dialogInterface, i) -> {
                                                                dialogInterface.dismiss();
                                                                Intent intent = new Intent(CashTransactionCommissionActivity.this, CashFreeGateway.class);

                                                                intent.putExtra("amount", (commissionAmount + platformFeeAmount) + "");
                                                                startActivityForResult(intent, 2);
                                                            }).create();
                                                    builder.show();
                                                }
                                            }else
                                                Toast.makeText(CashTransactionCommissionActivity.this, "No commission amount pending", Toast.LENGTH_SHORT).show();
                                        });
                                    });

                            kAlertDialog.setCancelable(false);
                            kAlertDialog.show();
                        }
                        }else if(snapshot.hasChild("registrationDate")){
                        long date = Long.parseLong(Objects.requireNonNull(snapshot.child("registrationDate").getValue(String.class)));
                        if(System.currentTimeMillis() - date >= 2073600000L){
                            totalCashTransaction.setText("Total Cash Transactions " + "\u20B9" + snapshot.child("totalCashTakeAway").getValue(String.class));
                            double totalCash = Double.parseDouble(Objects.requireNonNull(snapshot.child("totalCashTakeAway").getValue(String.class)));
                            commissionAmount = (totalCash * 7)/100;
                            if(snapshot.hasChild("totalMonthAmount")){
                                platformFeeAmount = Double.parseDouble(Objects.requireNonNull(snapshot.child("totalMonthAmount").getValue(String.class)));
                                if(platformFeeAmount == 0D){
                                    platformFeeAmount = 0D;
                                }else if(platformFeeAmount >= 500000L){
                                    platformFeeAmount = 5000D;
                                }else if(platformFeeAmount >= 350000L){
                                    platformFeeAmount = 3500D;
                                }else if(platformFeeAmount >= 100000){
                                    platformFeeAmount = 2000D;
                                }else{
                                    platformFeeAmount = 1000D;
                                }
                                platformFee.setText("Platform Fee: " + "\u20B9" + df.format(platformFeeAmount));
                                platformFeeBool = true;
                            }else{
                                platformFeeAmount = 1000D;
                                platformFee.setText("Platform Fee: " + "\u20B91000");
                            }
                            totalCommission.setText("Commission to be paid " + "\u20B9" + df.format(commissionAmount));
                            payCommissionNow.setText("Pay \u20B9" + df.format(commissionAmount + platformFeeAmount) + " Now");
                            payCommissionNow.setOnClickListener(view -> {
                                SharedPreferences acc = getSharedPreferences("loginInfo",MODE_PRIVATE);
                                SharedPreferences.Editor acEdit = acc.edit();
                                if(!acc.contains("number")) {
                                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(CashTransactionCommissionActivity.this);
                                    builder.setTitle("Contact").setMessage("You need to provide contact number for payment. No need to add +91");
                                    LinearLayout linearLayout = new LinearLayout(CashTransactionCommissionActivity.this);
                                    linearLayout.setOrientation(LinearLayout.VERTICAL);
                                    EditText editText = new EditText(CashTransactionCommissionActivity.this);
                                    editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                                    editText.setHint("Enter Number");
                                    editText.setMaxLines(10);
                                    linearLayout.addView(editText);
                                    builder.setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            if(editText.length() == 10)
                                            {
                                                Toast.makeText(CashTransactionCommissionActivity.this, "Number Saved Successfully", Toast.LENGTH_SHORT).show();
                                                acEdit.putString("number",editText.getText().toString());
                                                acEdit.apply();

                                                Intent intent = new Intent(CashTransactionCommissionActivity.this, CashFreeGateway.class);
                                                intent.putExtra("amount","599.00");
                                                startActivityForResult(intent,2);
                                            }else
                                                Toast.makeText(CashTransactionCommissionActivity.this, "Invalid Number", Toast.LENGTH_SHORT).show();
                                        }
                                    }).setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss()).create();
                                    builder.setCancelable(false);
                                    builder.setView(linearLayout);
                                    builder.show();
                                    return;
                                }
                                if(commissionAmount != 0) {
                                    SharedPreferences cash = getSharedPreferences("CashCommission",MODE_PRIVATE);
                                    if(cash.contains("fine")) {
                                        String fine = cash.getString("fine","");
                                        AlertDialog.Builder builder = new AlertDialog.Builder(CashTransactionCommissionActivity.this);
                                        builder.setTitle("Pay Commission").setMessage("Do you sure wanna proceed to pay commission\nFine of " + fine + "% will be applied")
                                                .setNegativeButton("No", (dialogInterface, i) -> dialogInterface.dismiss()).setPositiveButton("Yes", (dialogInterface, i) -> {
                                                    dialogInterface.dismiss();
                                                    double fineAmount = (commissionAmount * 10)/100;
                                                    commissionAmount = commissionAmount + fineAmount;
                                                    Intent intent = new Intent(CashTransactionCommissionActivity.this, CashFreeGateway.class);
                                                    intent.putExtra("amount", df.format(commissionAmount + platformFeeAmount) + "");
                                                    startActivityForResult(intent, 2);
                                                }).create();
                                        builder.show();
                                    }else{
                                        AlertDialog.Builder builder = new AlertDialog.Builder(CashTransactionCommissionActivity.this);
                                        builder.setTitle("Pay Commission").setMessage("Do you sure wanna proceed to pay commission")
                                                .setNegativeButton("No", (dialogInterface, i) -> dialogInterface.dismiss()).setPositiveButton("Yes", (dialogInterface, i) -> {
                                                    dialogInterface.dismiss();
                                                    Intent intent = new Intent(CashTransactionCommissionActivity.this, CashFreeGateway.class);

                                                    intent.putExtra("amount", (commissionAmount + platformFeeAmount) + "");
                                                    startActivityForResult(intent, 2);
                                                }).create();
                                        builder.show();
                                    }
                                }else
                                    Toast.makeText(CashTransactionCommissionActivity.this, "No commission amount pending", Toast.LENGTH_SHORT).show();
                            });
                        }else{
                            KAlertDialog kAlertDialog = new KAlertDialog(CashTransactionCommissionActivity.this,KAlertDialog.ERROR_TYPE)
                                    .setTitleText("Too early")
                                    .setContentText("No current amount due")
                                    .setConfirmText("Exit")
                                    .setConfirmClickListener(click -> {
                                        click.dismiss();
                                        finish();
                                    })
                                    .setCancelText("Leaving Fastway").setCancelClickListener(click -> {
                                        click.dismissWithAnimation();
                                        totalCashTransaction.setText("Total Cash Transactions " + "\u20B9" + snapshot.child("totalCashTakeAway").getValue(String.class));
                                        double totalCash = Double.parseDouble(Objects.requireNonNull(snapshot.child("totalCashTakeAway").getValue(String.class)));
                                        commissionAmount = (totalCash * 7)/100;
                                        if(snapshot.hasChild("totalMonthAmount")){
                                            platformFeeAmount = Double.parseDouble(Objects.requireNonNull(snapshot.child("totalMonthAmount").getValue(String.class)));
                                            if(platformFeeAmount == 0D){
                                                platformFeeAmount = 0D;
                                            }else if(platformFeeAmount >= 400000L){
                                                platformFeeAmount = (platformFeeAmount / 100) * 4;
                                            }else if(platformFeeAmount >= 300000L){
                                                platformFeeAmount = 12000D;
                                            }
                                            else if(platformFeeAmount >= 200000L){
                                                platformFeeAmount = 8000D;
                                            }else if(platformFeeAmount >= 100000){
                                                platformFeeAmount = 4000D;
                                            }else if(platformFeeAmount >= 50000){
                                                platformFeeAmount = 2000D;
                                            }else
                                                platformFeeAmount = 1000D;
                                            platformFee.setText("Platform Fee: " + "\u20B9" + df.format(platformFeeAmount));
                                            platformFeeBool = true;
                                        }else{
                                            platformFeeAmount = 1000D;
                                            platformFee.setText("Platform Fee: " + "\u20B91000");
                                        }
                                        totalCommission.setText("Commission to be paid " + "\u20B9" + df.format(commissionAmount));
                                        payCommissionNow.setText("Pay \u20B9" + df.format(commissionAmount + platformFeeAmount) + " Now");
                                        payCommissionNow.setOnClickListener(view -> {
                                            SharedPreferences acc = getSharedPreferences("loginInfo",MODE_PRIVATE);
                                            SharedPreferences.Editor acEdit = acc.edit();
                                            if(!acc.contains("number")) {
                                                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(CashTransactionCommissionActivity.this);
                                                builder.setTitle("Contact").setMessage("You need to provide contact number for payment. No need to add +91");
                                                LinearLayout linearLayout = new LinearLayout(CashTransactionCommissionActivity.this);
                                                linearLayout.setOrientation(LinearLayout.VERTICAL);
                                                EditText editText = new EditText(CashTransactionCommissionActivity.this);
                                                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                                                editText.setHint("Enter Number");
                                                editText.setMaxLines(10);
                                                linearLayout.addView(editText);
                                                builder.setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.dismiss();
                                                        if(editText.length() == 10)
                                                        {
                                                            Toast.makeText(CashTransactionCommissionActivity.this, "Number Saved Successfully", Toast.LENGTH_SHORT).show();
                                                            acEdit.putString("number",editText.getText().toString());
                                                            acEdit.apply();

                                                            Intent intent = new Intent(CashTransactionCommissionActivity.this, CashFreeGateway.class);
                                                            intent.putExtra("amount","599.00");
                                                            startActivityForResult(intent,2);
                                                        }else
                                                            Toast.makeText(CashTransactionCommissionActivity.this, "Invalid Number", Toast.LENGTH_SHORT).show();
                                                    }
                                                }).setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss()).create();
                                                builder.setCancelable(false);
                                                builder.setView(linearLayout);
                                                builder.show();
                                                return;
                                            }
                                            if(commissionAmount != 0) {
                                                SharedPreferences cash = getSharedPreferences("CashCommission",MODE_PRIVATE);
                                                if(cash.contains("fine")) {
                                                    String fine = cash.getString("fine","");
                                                    AlertDialog.Builder builder = new AlertDialog.Builder(CashTransactionCommissionActivity.this);
                                                    builder.setTitle("Pay Commission").setMessage("Do you sure wanna proceed to pay commission\nFine of " + fine + "% will be applied")
                                                            .setNegativeButton("No", (dialogInterface, i) -> dialogInterface.dismiss()).setPositiveButton("Yes", (dialogInterface, i) -> {
                                                                dialogInterface.dismiss();
                                                                double fineAmount = (commissionAmount * 10)/100;
                                                                commissionAmount = commissionAmount + fineAmount;
                                                                Intent intent = new Intent(CashTransactionCommissionActivity.this, CashFreeGateway.class);
                                                                intent.putExtra("amount", df.format(commissionAmount + platformFeeAmount) + "");
                                                                startActivityForResult(intent, 2);
                                                            }).create();
                                                    builder.show();
                                                }else{
                                                    AlertDialog.Builder builder = new AlertDialog.Builder(CashTransactionCommissionActivity.this);
                                                    builder.setTitle("Pay Commission").setMessage("Do you sure wanna proceed to pay commission")
                                                            .setNegativeButton("No", (dialogInterface, i) -> dialogInterface.dismiss()).setPositiveButton("Yes", (dialogInterface, i) -> {
                                                                dialogInterface.dismiss();
                                                                Intent intent = new Intent(CashTransactionCommissionActivity.this, CashFreeGateway.class);

                                                                intent.putExtra("amount", (commissionAmount + platformFeeAmount) + "");
                                                                startActivityForResult(intent, 2);
                                                            }).create();
                                                    builder.show();
                                                }
                                            }else
                                                Toast.makeText(CashTransactionCommissionActivity.this, "No commission amount pending", Toast.LENGTH_SHORT).show();
                                        });
                                    });

                            kAlertDialog.setCancelable(false);
                            kAlertDialog.show();
                        }
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
            auth = FirebaseAuth.getInstance();
            DatabaseReference fineQuery = FirebaseDatabase.getInstance().getReference().getRoot().child("Complaints").child("Fines").child(Objects.requireNonNull(auth.getUid()));
            fineQuery.removeValue();
            SharedPreferences sharedPreferences = getSharedPreferences("CashCommission",MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove("fine");
            editor.apply();
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Admin").child(Objects.requireNonNull(auth.getUid()));
            databaseReference.child("totalCashTakeAway").setValue("0");
            databaseReference.child("totalMonthAmount").setValue("0");
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