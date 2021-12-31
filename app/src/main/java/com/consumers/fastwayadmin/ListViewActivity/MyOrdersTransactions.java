package com.consumers.fastwayadmin.ListViewActivity;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.consumers.fastwayadmin.R;
import com.developer.kalert.KAlertDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import karpuzoglu.enes.com.fastdialog.Animations;
import karpuzoglu.enes.com.fastdialog.FastDialog;
import karpuzoglu.enes.com.fastdialog.FastDialogBuilder;
import karpuzoglu.enes.com.fastdialog.Type;

public class MyOrdersTransactions extends AppCompatActivity {
    String neftUrl = "https://intercellular-stabi.000webhostapp.com/CheckoutPayouts/payoutNeft.php";
    String singleBenStatus = "https://intercellular-stabi.000webhostapp.com/benStatusFolder/singleStatus.php";
    String genratedToken = "";
    List<String> status = new ArrayList<>();
    List<String> allTransID = new ArrayList<>();
    Button showAllTransactions;
    TextView textView;
    HashMap<String,String> timeMap = new HashMap<>();
    List<String> allTimeID = new ArrayList<>();
    int count = 0;
    List<Integer> amountList = new ArrayList<>();
    List<Integer> daysList = new ArrayList<>();
    List<String> amount = new ArrayList<>();
    int totalAmount=0;
    int pendingamount = 0;
    HashMap<String,String> map = new HashMap<>();
    TextView numberOfTransactions,earningAmount;
    int days = 1;
    RecyclerView recyclerView;
    int finalI;
    List<String> time = new ArrayList<>();
    SharedPreferences sharedPreferences;
    String currentTransID;
    List<String> transID = new ArrayList<>();
    final Handler handler = new Handler(Looper.getMainLooper());
    FirebaseAuth auth = FirebaseAuth.getInstance();
    DatabaseReference reference;
    String testPayoutToken = "https://intercellular-stabi.000webhostapp.com/CheckoutPayouts/testToken.php";
    String testBearerToken = "https://intercellular-stabi.000webhostapp.com/CheckoutPayouts/test/testBearerToken.php";
    FastDialog fastDialog;
    String authToken = "https://intercellular-stabi.000webhostapp.com/CheckoutPayouts/authBEarerToken.php";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_orders_transactions);
        sharedPreferences = getSharedPreferences("loginInfo",MODE_PRIVATE);
        fastDialog = new FastDialogBuilder(MyOrdersTransactions.this, Type.PROGRESS)
                .progressText("Fetching Details...")
                .setAnimation(Animations.SLIDE_TOP)
                .create();
        fastDialog.show();
        textView = findViewById(R.id.showingTrasacntionTimeDate);
        showAllTransactions = findViewById(R.id.showAllOnlineTransactions);
        numberOfTransactions = findViewById(R.id.totalTransactionDays);
        earningAmount = findViewById(R.id.totalEarningOnOrders);
        recyclerView = findViewById(R.id.orderTransRecyclerView);
        reference = FirebaseDatabase.getInstance().getReference().getRoot().child("Admin").child(Objects.requireNonNull(auth.getUid()));
        reference.child("Transactions").limitToLast(20).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    time.clear();
                    amount.clear();
                    daysList.clear();
                    amountList.clear();
                    transID.clear();
                    status.clear();
                    allTransID.clear();
                    map.clear();
                    allTimeID.clear();
                    totalAmount = 0;
                    days = 0;
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                        time.add(String.valueOf(dataSnapshot.getKey()));
                        transID.add(String.valueOf(dataSnapshot.child("transID").getValue()));
                        map.put(String.valueOf(dataSnapshot.child("transID").getValue()),String.valueOf(dataSnapshot.child("customerID").getValue()));
                        timeMap.put(String.valueOf(dataSnapshot.child("transID").getValue()),String.valueOf(dataSnapshot.getKey()));
                    }

                    new MakePayout().execute();

                }else{
                   KAlertDialog kAlertDialog =  new KAlertDialog(MyOrdersTransactions.this,KAlertDialog.ERROR_TYPE)
                            .setTitleText("Error")
                            .setContentText("No transaction found")
                            .setConfirmText("Exit")
                            .setConfirmClickListener(click -> {
                                fastDialog.dismiss();
                                click.dismissWithAnimation();
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


        showAllTransactions.setOnClickListener(click -> {

        });

    }


    public class MakePayout extends AsyncTask<Void,Void,Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            RequestQueue requestQueue = Volley.newRequestQueue(MyOrdersTransactions.this);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, testPayoutToken, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.i("response",response);
                    genratedToken = response.trim();
                    new AuthorizeToken().execute();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
            requestQueue.add(stringRequest);
            return null;
        }
    }

    public class AuthorizeToken extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    KAlertDialog kAlertDialog =  new KAlertDialog(MyOrdersTransactions.this,KAlertDialog.ERROR_TYPE)
                            .setTitleText("Error")
                            .setContentText("Server Time Out\nTry again later :)")
                            .setConfirmText("Exit")
                            .setConfirmClickListener(click -> {
                                fastDialog.dismiss();
                                click.dismissWithAnimation();
                                finish();
                            });

                    kAlertDialog.setCancelable(false);
                    kAlertDialog.show();
                }
            },15000);
            List<String> transactionID = new ArrayList<>();
            HashMap<String,String> amountMap = new HashMap<>();
            RequestQueue requestQueue = Volley.newRequestQueue(MyOrdersTransactions.this);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, testBearerToken, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    Log.i("response",response);
                    if(response.trim().equals("Token is valid")){
                        for(int i=0;i<time.size();i++){
                            currentTransID = transID.get(i);
                            Log.i("transID",currentTransID + "");
//                            new fetchBenDetails().execute();
                            RequestQueue requestQueue = Volley.newRequestQueue(MyOrdersTransactions.this);
                             finalI = i;
                            int finalI1 = i;
                            StringRequest stringRequest = new StringRequest(Request.Method.POST, singleBenStatus, new Response.Listener<String>() {
                                @SuppressLint("SetTextI18n")
                                @Override
                                public void onResponse(String response) {
                                    Log.i("resp", response);
                                    String[] resp = response.trim().split(",");

                                    status.add(resp[0]);
                                    days++;
                                    if(resp[0].equals("PENDING")) {

                                    }else{
                                        Double d = Double.parseDouble(resp[1]);
                                        totalAmount = totalAmount + d.intValue();
                                    }
                                    amount.add(resp[1]);
                                    transactionID.add(resp[2]);


                                    amountList.add(totalAmount);
                                    daysList.add(days);
                                    Log.i("value",totalAmount + "");
                                    count++;
                                    if(amountList.size() == time.size()){
                                        fastDialog.dismiss();
                                        Log.i("infoMap",transactionID.toString());
                                        Log.i("infoMap",amountMap.toString());
                                        Log.i("infoMap",timeMap.toString());
                                        showAllTransactions.setVisibility(View.VISIBLE);
                                        handler.removeCallbacksAndMessages(null);
                                        Log.i("infoMap",allTransID.toString());
                                        recyclerView.setLayoutManager(new LinearLayoutManager(MyOrdersTransactions.this));
                                        recyclerView.setAdapter(new MyOrderAdapter(amount,allTimeID,transactionID,status,MyOrdersTransactions.this,totalAmount,days,map,amountMap,timeMap));
                                    }

                                    if(amountList.size() == time.size()){
                                        numberOfTransactions.setText("Transactions: " + days);
                                        earningAmount.setText("\u20B9" + totalAmount);

                                    }
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {

                                }
                            }){
                                @Nullable
                                @Override
                                protected Map<String, String> getParams() {
                                    Map<String,String> params = new HashMap<>();
                                    FirebaseAuth auth = FirebaseAuth.getInstance();
                                    params.put("token",genratedToken);
                                    params.put("benID",transID.get(finalI1));
                                    allTransID.add(transID.get(finalI1));
                                    allTimeID.add(time.get(finalI1));
//                                    Log.i("customTrans",transID.get(finalI1) + "");
                                    return params;

                                }
                            };
                            requestQueue.add(stringRequest);
                            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                                    0,
                                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                        }
//                        Log.i("value",transID.toString());
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }){
                @Nullable
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String> params = new HashMap<>();
                    params.put("token",genratedToken);
                    return params;
                }
            };
            requestQueue.add(stringRequest);
            return null;
        }
    }
}