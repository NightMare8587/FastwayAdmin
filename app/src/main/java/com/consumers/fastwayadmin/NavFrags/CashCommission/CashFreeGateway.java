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
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.cashfree.pg.CFPaymentService;
import com.consumers.fastwayadmin.HomeScreen.ReportSupport.RequestRefundClass;
import com.consumers.fastwayadmin.NavFrags.generateSignature;
import com.consumers.fastwayadmin.R;
import com.developer.kalert.KAlertDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

public class CashFreeGateway extends AppCompatActivity implements PaymentResultWithDataListener {
    private static final String TAG = "CashFreeGateway";
    String urls = "https://intercellular-stabi.000webhostapp.com/hash.php";
    String test = "https://intercellular-stabi.000webhostapp.com/testhash.php";
    String token;
    String amount;
    String orderID;
    String URL = "https://fcm.googleapis.com/fcm/send";
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
//                Intent sendData = new Intent();
//                sendData.putExtra("orderId",s);
//        sendData.putExtra("referID",bundle.getString("referenceId"));
                setResult(2);
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

        AsyncTask.execute(() -> {

            RequestQueue requestQueue = Volley.newRequestQueue(CashFreeGateway.this);
            JSONObject main = new JSONObject();
            try{
                main.put("to","/topics/"+"RequestPayout");
                JSONObject notification = new JSONObject();
                notification.put("title","Refund Request");
                notification.put("body","You have a new refund request. Check now");
                main.put("notification",notification);

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, main, response -> {

                }, error -> Toast.makeText(CashFreeGateway.this, error.getLocalizedMessage()+"null", Toast.LENGTH_SHORT).show()){
                    @Override
                    public Map<String, String> getHeaders() {
                        Map<String,String> header = new HashMap<>();
                        header.put("content-type","application/json");
                        header.put("authorization","key=AAAAjq_WsHs:APA91bGZV-uH-NJddxIniy8h1tDGDHqxhgvFdyNRDaV_raxjvSM_FkKu7JSwtSp4Q_iSmPuTKGGIB2M_07c9rKgPXUH43-RzpK6zkaSaIaNgmeiwUO40rYxYUZAkKoLAQQVeVJ7mXboD");
                        return header;
                    }
                };

                requestQueue.add(jsonObjectRequest);

            }
            catch (Exception e){
                Toast.makeText(CashFreeGateway.this, e.getLocalizedMessage()+"null", Toast.LENGTH_SHORT).show();
            }
        });
        SharedPreferences sharedPreferences = getSharedPreferences("loginInfo",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        SharedPreferences userDetails = getSharedPreferences("RestaurantInfo",MODE_PRIVATE);
        DatabaseReference requestRefundOrdinalo = FirebaseDatabase.getInstance().getReference().getRoot().child("Complaints").child("RefundRequest").child(Objects.requireNonNull(auth.getUid()));
        RequestRefundClass requestRefundClass = new RequestRefundClass(sharedPreferences.getString("lastOrderId",""), amount, userDetails.getString("hotelName",""), "Commission payment got crashed issue refund");
        requestRefundOrdinalo.setValue(requestRefundClass);

        editor.remove("lastOrderId").apply();
        setResult(3);
        finish();
    }

    enum SeamlessMode {
        CARD, WALLET, NET_BANKING, UPI_COLLECT, PAY_PAL
    }

    SeamlessMode currentMode = SeamlessMode.CARD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cash_free_gateway);
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

        new Thread(() -> {
            try {
                RazorpayClient razorpay = new RazorpayClient(arr[0], arr[1]);

                JSONObject orderRequest = new JSONObject();
                orderRequest.put("amount", 59900); // amount in the smallest currency unit
                orderRequest.put("currency", "INR");
                orderRequest.put("receipt", "order_rcptid_11");

                Order order = razorpay.orders.create(orderRequest);
                Log.i("infoOrder",order.toString() + "");
                Log.i("infoOrder",order.get("id") + "");
                orderID = order.get("id");

                if(!acInfo.contains("lastOrderId")){
                    editor.putString("lastOrderId",orderID);
                    editor.apply();
                }

                try {
                    JSONObject options = new JSONObject();

                    options.put("name", "" + acc.getString("name",""));
                    options.put("description", "Reference No. #123456");
                    options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.jpg");
                    options.put("order_id", orderID);//from response of step 3.
                    options.put("theme.color", "#3399cc");
                    options.put("currency", "INR");
                    options.put("payment_capture", "1");
                    options.put("amount", 59900);//pass amount in currency subunits
                    options.put("prefill.email", "" + acc.getString("email",""));
                    options.put("prefill.contact","" + acc.getString("number",""));
                    JSONObject retryObj = new JSONObject();
                    retryObj.put("enabled", true);
                    retryObj.put("max_count", 4);
                    options.put("retry", retryObj);

                    runOnUiThread(() -> {
                        checkout.open(CashFreeGateway.this, options);
                    });



                } catch(Exception e) {
                    Log.i("TAG", "Error in starting Razorpay Checkout", e);
                }
            } catch (JSONException | RazorpayException e) {
                e.printStackTrace();
            }
        }).start();

//        if(acInfo.contains("testAdmin")){
//            appID = "61532dad5cd9ca634ae8ca59d23516";
//            finalUrl = test;
//            stage = "TEST";
//        }else{
//            finalUrl = urls;
//            appID = "107263678b9b7b22cd717e2519362701";
//            stage = "PROD";
//        }
//        Double am = Double.parseDouble(amount);
//        platformFee = getIntent().getStringExtra("platformFee");
//        if(!platformFee.equals("0")){
//            Double pf = Double.parseDouble(platformFee);
//            amount = String.valueOf(am + pf);
//        }
//        makePaymentRequest();
    }

    private void makePaymentRequest() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        Random random = new Random();
        ran = random.nextInt(100000 - 1) + 1;
        ranV = String.valueOf(ran);
        SharedPreferences sharedPreferences = getSharedPreferences("CashFreeToken", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        orderId = "ORDER_" + System.currentTimeMillis() + "_" + ranV;
//        while (sharedPreferences.contains(orderId)) {
//            ran = random.nextInt(100000 - 1) + 1;
//            ranV = String.valueOf(ran);
//            orderId = ranV + String.valueOf(auth.getUid());
//        }

        editor.putString(orderId, orderId);
        editor.apply();
        universalOrderID = orderId;
//       JSONObject jsonObject = new JSONObject();
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        try {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, finalUrl, response -> {
                if (response == null)
                    Log.i("null", "response");
                else {
                    Log.i("resposnse", response.toString());
                    token = response;
                    Map<String, String> map = new HashMap<>();

                    map.put(PARAM_APP_ID, appID);
                    map.put(PARAM_ORDER_ID, orderId);
                    map.put(PARAM_ORDER_AMOUNT, "" + df.format(Double.parseDouble(amount)));
                    map.put(PARAM_ORDER_CURRENCY, "INR");
                    map.put(PARAM_CUSTOMER_NAME, name);
                    map.put(PARAM_CUSTOMER_EMAIL, email);
                    map.put(PARAM_CUSTOMER_PHONE, number);
                    Log.i("here", "success");
                    CFPaymentService cfPaymentService = CFPaymentService.getCFPaymentServiceInstance();
                    cfPaymentService.doPayment(CashFreeGateway.this, map, token, stage);
                }
            }, error -> {
                Log.i("error", "error: " + error.toString());
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