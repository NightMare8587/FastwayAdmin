package com.consumers.fastwayadmin.NavFrags.CashCommission;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import com.consumers.fastwayadmin.NavFrags.generateSignature;
import com.consumers.fastwayadmin.R;
import com.razorpay.Checkout;
import com.razorpay.Order;
import com.razorpay.PaymentData;
import com.razorpay.PaymentResultWithDataListener;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.SignatureException;
import java.text.DecimalFormat;
import java.util.Objects;

public class CommissionGWordinalo extends AppCompatActivity implements PaymentResultWithDataListener {
    String urls = "https://intercellular-stabi.000webhostapp.com/hash.php";
    String test = "https://intercellular-stabi.000webhostapp.com/testhash.php";
    String token;
    String amount;
    String orderID;
    String testKeys;
    String platformFee;
    DecimalFormat df = new DecimalFormat("0.00");
    int ran;
    String orderId;
    SharedPreferences acInfo;
    String universalOrderID;
    String name,email,number;
    String appID,stage,finalUrl;
    String ranV;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commission_gwordinalo);

        amount = getIntent().getStringExtra("amount");
        acInfo = getSharedPreferences("loginInfo",MODE_PRIVATE);
        editor = acInfo.edit();
        editor.putString("testAdmin","112233");
        editor.apply();
        name = acInfo.getString("name","");
        email = acInfo.getString("email","");
        number = "8076531395";
        testKeys = getIntent().getStringExtra("keys");
        String[] arr = testKeys.split(",");
        Checkout checkout = new Checkout();
        checkout.setKeyID(arr[0]);


        SharedPreferences acc = getSharedPreferences("loginInfo",MODE_PRIVATE);

        amount = amount.replace(".","");
        int amt = Integer.parseInt(amount);
        new Thread(() -> {
            try {
                RazorpayClient razorpay = new RazorpayClient(arr[0], arr[1]);

                JSONObject orderRequest = new JSONObject();
                orderRequest.put("amount", amt); // amount in the smallest currency unit
                orderRequest.put("currency", "INR");
                orderRequest.put("receipt", "order_rcptid_11");

                Order order = razorpay.orders.create(orderRequest);
                Log.i("infoOrder",order.toString() + "");
                Log.i("infoOrder",order.get("id") + "");
                orderID = order.get("id");


                try {
                    JSONObject options = new JSONObject();

                    options.put("name", "" + acc.getString("name",""));
                    options.put("description", "Reference No. #123456");
                    options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.jpg");
                    options.put("order_id", orderID);//from response of step 3.
                    options.put("theme.color", "#3399cc");
                    options.put("currency", "INR");
                    options.put("amount", amt);//pass amount in currency subunits
                    options.put("prefill.email", "" + acc.getString("email",""));
                    options.put("prefill.contact","" + acc.getString("number",""));
                    JSONObject retryObj = new JSONObject();
                    retryObj.put("enabled", true);
                    retryObj.put("max_count", 4);
                    options.put("retry", retryObj);

                    runOnUiThread(() -> {
                        checkout.open(CommissionGWordinalo.this, options);
                    });



                } catch(Exception e) {
                    Log.i("TAG", "Error in starting Razorpay Checkout", e);
                }
            } catch (JSONException | RazorpayException e) {
                e.printStackTrace();
            }
        }).start();

    }

    @Override
    public void onPaymentSuccess(String s, PaymentData paymentData) {
        String razorpaySign = paymentData.getSignature();
        String razorpayPayID = paymentData.getPaymentId();
        Log.i("WhatS",razorpaySign + "," + s);
        Log.i("WhatP",razorpayPayID);
        Log.i("WhatO",orderID);
        Log.i("WhatOO",paymentData.getOrderId());
        try {
            String[] arr = testKeys.split(",");
//            generateSignature generateSignature = new generateSignature(orderID,razorpayPayID,razorpaySign,arr[1]);

            String result = generateSignature.calculateRFC2104HMAC(orderID + "|" + razorpayPayID,arr[1]);
            Log.i("WhatR",result);
            if(Objects.equals(result, razorpaySign)){

                Log.i("WhatH","Signature Success");
                Intent sendData = new Intent();
                sendData.putExtra("orderId",s);
//        sendData.putExtra("referID",bundle.getString("referenceId"));
                setResult(2,sendData);
                finish();
            }else{
                Log.i("WhatH","Signature Failed");
            }

        } catch (SignatureException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPaymentError(int i, String s, PaymentData paymentData) {
        setResult(3);
        finish();
    }
}