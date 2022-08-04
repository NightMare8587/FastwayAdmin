package com.consumers.fastwayadmin.ListViewActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.consumers.fastwayadmin.NavFrags.BankVerification.SelectPayoutMethodType;
import com.consumers.fastwayadmin.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.Objects;

public class InitiatePayoutForAdminNEFT extends AppCompatActivity {
    DatabaseReference databaseReference;
    double amount;
    boolean availableForPayout = false;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    TextView textView,changeMethod;
    DecimalFormat decimalFormat = new DecimalFormat("0.00");
    Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initiate_payout_for_admin_neft);
        textView = findViewById(R.id.totalPayoutAmountAvailableToBeInitiated);
        button = findViewById(R.id.initiatePaymentForPayoutAdmin);
        changeMethod = findViewById(R.id.changePayoutMethodNeftorAdmin);
        databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Admin").child(Objects.requireNonNull(auth.getUid()));
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChild("totalPayoutAmount")){
                    availableForPayout = true;
                     amount = Double.parseDouble(String.valueOf(snapshot.child("totalPayoutAmount").getValue()));
                     textView.setText(decimalFormat.format(amount));
                }else
                {
                    button.setVisibility(View.INVISIBLE);
                    Toast.makeText(InitiatePayoutForAdminNEFT.this, "No Amount For Payout Available", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        changeMethod.setOnClickListener(click -> {
            startActivity(new Intent(InitiatePayoutForAdminNEFT.this, SelectPayoutMethodType.class));
        });

        button.setOnClickListener(click -> {
            if(availableForPayout){
                AlertDialog.Builder builder = new AlertDialog.Builder(InitiatePayoutForAdminNEFT.this);
                builder.setTitle("Choose one option").setMessage("Choose one payout option\nGet payout after 4 to 5 hours (NEFT)\nGet Instant Payout (IMPS)")
                        .setPositiveButton("Choose NEFT", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).setNegativeButton("Choose IMPS", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).setNeutralButton("Exit", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create().show();
            }
        });
    }
}