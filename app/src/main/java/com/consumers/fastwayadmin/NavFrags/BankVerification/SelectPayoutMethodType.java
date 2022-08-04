package com.consumers.fastwayadmin.NavFrags.BankVerification;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.consumers.fastwayadmin.R;

import java.util.Calendar;

public class SelectPayoutMethodType extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    RadioGroup group;
    String checkedMethod = "neft";
    Button proceedWithPayoutMethod;
    RadioButton imps,neft;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_payout_method_type);
        sharedPreferences = getSharedPreferences("loginInfo",MODE_PRIVATE);
        group = findViewById(R.id.radioGroupPayoutMethod);
        imps = findViewById(R.id.radioButtonIMPSPayoutMethod);
        neft = findViewById(R.id.radioButtonNEFTpaymentMethod);
        editor = sharedPreferences.edit();
        proceedWithPayoutMethod = findViewById(R.id.confirmPayoutMethodInSelect);
        if(sharedPreferences.contains("payoutMethodChoosen")){
            if(sharedPreferences.getString("payoutMethodChoosen","").equals("neft")) {
                neft.setChecked(true);
                checkedMethod = "neft";
            }
            else {
                imps.setChecked(true);
                checkedMethod = "imps";
            }
        }else {
            neft.setChecked(true);
        }


        proceedWithPayoutMethod.setOnClickListener(click -> {
            if(checkedMethod.equals("imps")) {
                editor.putString("payoutMethodChoosen", "imps");

//                PendingIntent pendingIntent =  PendingIntent.getService()
            }else{
                editor.putString("payoutMethodChoosen", "neft");
            }
            editor.apply();

        });

        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SelectPayoutMethodType.this);
                if (checkedId == R.id.radioButtonNEFTpaymentMethod) {
                    builder.setTitle("NEFT").setMessage("Get Payment whenever you want by initiating payout and will be credited within 4 hours or even 1 day depending upon traffic upon bank\nYou can still withdraw money instantly at any time by paying a nominal fee of \u20b98")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).create();
                    builder.show();
                    checkedMethod = "neft";
                }else if(checkedId == R.id.radioButtonIMPSPayoutMethod){
                    builder.setTitle("IMPS").setMessage("Payment will be automatically generated after every order approved in Online payment method\nSince payment will be instant you will be charged with some nominal fee depending upon amount of transaction\n\u20b95 for amount under \u20b91000\n\u20b98 for amount under \u20b910000")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).create();
                    builder.show();
                    checkedMethod = "imps";
                }
            }
        });
    }
}