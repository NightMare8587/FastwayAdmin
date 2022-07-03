package com.consumers.fastwayadmin.NavFrags.CashCommission;

import static com.cashfree.pg.CFPaymentService.PARAM_APP_ID;
import static com.cashfree.pg.CFPaymentService.PARAM_CUSTOMER_EMAIL;
import static com.cashfree.pg.CFPaymentService.PARAM_CUSTOMER_NAME;
import static com.cashfree.pg.CFPaymentService.PARAM_CUSTOMER_PHONE;
import static com.cashfree.pg.CFPaymentService.PARAM_ORDER_AMOUNT;
import static com.cashfree.pg.CFPaymentService.PARAM_ORDER_CURRENCY;
import static com.cashfree.pg.CFPaymentService.PARAM_ORDER_ID;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.cashfree.pg.CFPaymentService;
import com.consumers.fastwayadmin.R;
import com.developer.kalert.KAlertDialog;
import com.google.firebase.auth.FirebaseAuth;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class CashFreeGateway extends AppCompatActivity {
    private static final String TAG = "CashFreeGateway";
    String urls = "https://intercellular-stabi.000webhostapp.com/hash.php";
    String test = "https://intercellular-stabi.000webhostapp.com/testhash.php";
    String token;
    String amount;
    String platformFee;
    DecimalFormat df = new DecimalFormat("0.00");
    int ran;
    String orderId;
    String universalOrderID;
    String ranV;

    enum SeamlessMode {
        CARD, WALLET, NET_BANKING, UPI_COLLECT, PAY_PAL
    }

    SeamlessMode currentMode = SeamlessMode.CARD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cash_free_gateway);
        amount = getIntent().getStringExtra("amount");
//        Double am = Double.parseDouble(amount);
//        platformFee = getIntent().getStringExtra("platformFee");
//        if(!platformFee.equals("0")){
//            Double pf = Double.parseDouble(platformFee);
//            amount = String.valueOf(am + pf);
//        }
        makePaymentRequest();
    }

    private void makePaymentRequest() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        Random random = new Random();
        ran = random.nextInt(100000 - 1) + 1;
        ranV = String.valueOf(ran);
        SharedPreferences sharedPreferences = getSharedPreferences("CashFreeToken", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        orderId = String.valueOf(auth.getUid()) + ranV;
        while (sharedPreferences.contains(orderId)) {
            ran = random.nextInt(100000 - 1) + 1;
            ranV = String.valueOf(ran);
            orderId = ranV + String.valueOf(auth.getUid());
        }

        editor.putString(orderId, orderId);
        editor.apply();
        universalOrderID = orderId;
//       JSONObject jsonObject = new JSONObject();
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        try {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, test, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    if (response == null)
                        Log.i("null", "response");
                    else {
                        Log.i("resposnse", response.toString());
                        token = response;
                        Map<String, String> map = new HashMap<>();

                        map.put(PARAM_APP_ID, "61532dad5cd9ca634ae8ca59d23516");
                        map.put(PARAM_ORDER_ID, orderId);
                        map.put(PARAM_ORDER_AMOUNT, "" + df.format(Double.parseDouble(amount)));
                        map.put(PARAM_ORDER_CURRENCY, "INR");
                        map.put(PARAM_CUSTOMER_NAME, "Pulkit Loya");
                        map.put(PARAM_CUSTOMER_EMAIL, "maheshwariloya@gmail.com");
                        map.put(PARAM_CUSTOMER_PHONE, "8076531395");
                        Log.i("here", "success");
                        CFPaymentService cfPaymentService = CFPaymentService.getCFPaymentServiceInstance();
                        cfPaymentService.doPayment(CashFreeGateway.this, map, token, "TEST");
                    }
                }
            }, error -> {
                Log.i("error", "error");
                KAlertDialog dialog = new KAlertDialog(CashFreeGateway.this, KAlertDialog.ERROR_TYPE)
                        .setTitleText("Error")
                        .setContentText("Payment can't be processed. try again later :(")
                        .setConfirmText("Exit")
                        .setConfirmClickListener(click -> {
                            click.dismissWithAnimation();
                            finish();
                        });

                dialog.setCancelable(false);
                dialog.show();
            }) {
                @NonNull
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("orderId", orderId);
                    params.put("orderAmount", "" + df.format(Double.parseDouble(amount)));
                    params.put("orderCurrency", "INR");
                    return params;
                }
            };

            requestQueue.add(stringRequest);
        } catch (Exception e) {
            Log.i("i am", "here");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Same request code for all payment APIs.
        Log.d(TAG, "ReqCode : " + CFPaymentService.REQ_CODE);
        int request = CFPaymentService.REQ_CODE;
        Log.d(TAG, "API Response : ");
        //Prints all extras. Replace with app logic.
        if (data != null) {
            Bundle bundle = data.getExtras();
            if (bundle != null)
                for (String key : bundle.keySet()) {
                    if (bundle.getString(key) != null) {
                        if (bundle.getString("txStatus").equals("SUCCESS")) {
//                            Toast.makeText(this, "Transaction Failed" , Toast.LENGTH_SHORT).show();
                            setResult(2);
                            finish();
                        }else
                            setResult(3);
                        finish();
                    }
                }
        }
    }
}