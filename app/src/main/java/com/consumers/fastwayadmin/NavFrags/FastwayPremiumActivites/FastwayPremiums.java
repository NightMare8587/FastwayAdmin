package com.consumers.fastwayadmin.NavFrags.FastwayPremiumActivites;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.consumers.fastwayadmin.NavFrags.CashCommission.CashFreeGateway;
import com.consumers.fastwayadmin.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.razorpay.Checkout;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class FastwayPremiums extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    SharedPreferences logininfo;
    boolean gotKey = false;
    boolean freeTrial = false;
    String testKey;
    SharedPreferences.Editor premEdit;
    String getApiKeyTest = "https://intercellular-stabi.000webhostapp.com/razorpay/returnApiKey.php";
    String getApiKeyLive = "https://intercellular-stabi.000webhostapp.com/razorpay/returnLiveApiKey.php";
    FirebaseAuth auth = FirebaseAuth.getInstance();
    SharedPreferences allPremsIDS;
//    String subRefURL = "https://intercellular-stabi.000webhostapp.com/payouts/initialisedSub.php";
//    String prodOnHoldActivate = "https://intercellular-stabi.000webhostapp.com/payouts/prodOnHoldSubActivate.php";
//    String testUrl = "https://intercellular-stabi.000webhostapp.com/payouts/subscription.php";
//    String prodUrl = "https://intercellular-stabi.000webhostapp.com/payouts/prodSub.php";
//    String subRefProd = "https://intercellular-stabi.000webhostapp.com/payouts/prodInitialise.php";
//    String cancelSubURL = "https://intercellular-stabi.000webhostapp.com/payouts/cancelSubs.php";
    Button subscribePrem;
    DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getSharedPreferences("AdminPremiumDetails",MODE_PRIVATE);
        editor = sharedPreferences.edit();
        AsyncTask.execute(() -> {
            RequestQueue requestQueue = Volley.newRequestQueue(FastwayPremiums.this);
            StringRequest stringRequest = new StringRequest(Request.Method.GET, getApiKeyLive, response -> runOnUiThread(() -> {
                Checkout.preload(FastwayPremiums.this);
//                        Checkout checkout = new Checkout();
                // ...
                gotKey = true;
                testKey = response;
                String[] arr = response.split(",");
                Log.i("responseKey",response);
//                        checkout.setKeyID(arr[0]);
            }), new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.i("responseError",error.toString());
                }
            });
            requestQueue.add(stringRequest);
        });


        databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Admin").child(Objects.requireNonNull(auth.getUid()));
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChild("freeTrialDate")){
                    long time = Long.parseLong(String.valueOf(snapshot.child("freeTrialDate").getValue()));
                    if(System.currentTimeMillis() > time){
                        setContentView(R.layout.isfreetraialrunning);
                        TextView textView = findViewById(R.id.freeTrailUpdateTextView);
                        textView.setText("Your free trial is over. Subscribe now \u20b9599 per month");
                        Button button = findViewById(R.id.subscribePremFreeTrialEnded);
                        button.setVisibility(View.VISIBLE);
                        button.setText("Subscribe Now");
                        button.setOnClickListener(click -> {
                            if(!gotKey)
                            {
                                Toast.makeText(FastwayPremiums.this,"One Second....",Toast.LENGTH_SHORT).show();
                                return;
                            }
                            SharedPreferences acc = getSharedPreferences("loginInfo",MODE_PRIVATE);
                            SharedPreferences.Editor acEdit = acc.edit();
                            if(!acc.contains("number")) {
                                android.app.AlertDialog.Builder builder = new AlertDialog.Builder(FastwayPremiums.this);
                                builder.setTitle("Contact").setMessage("You need to provide contact number for payment. No need to add +91");
                                LinearLayout linearLayout = new LinearLayout(FastwayPremiums.this);
                                linearLayout.setOrientation(LinearLayout.VERTICAL);
                                EditText editText = new EditText(FastwayPremiums.this);
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
                                            Toast.makeText(FastwayPremiums.this, "Number Saved Successfully", Toast.LENGTH_SHORT).show();
                                            acEdit.putString("number",editText.getText().toString());
                                            acEdit.apply();
                                            freeTrial = true;
                                            Intent intent = new Intent(FastwayPremiums.this, CashFreeGateway.class);
                                            intent.putExtra("amount","59900");
                                            intent.putExtra("keys",testKey);
                                            startActivityForResult(intent,2);
                                        }else
                                            Toast.makeText(FastwayPremiums.this, "Invalid Number", Toast.LENGTH_SHORT).show();
                                    }
                                }).setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss()).create();
                                builder.setCancelable(false);
                                builder.setView(linearLayout);
                                builder.show();
                                return;
                            }
                            freeTrial = true;
                            Intent intent = new Intent(FastwayPremiums.this, CashFreeGateway.class);
                            intent.putExtra("amount","59900");
                            intent.putExtra("keys",testKey);
                            startActivityForResult(intent,2);
                        });
                    }else{
                        setContentView(R.layout.isfreetraialrunning);
                        TextView textView = findViewById(R.id.freeTrailUpdateTextView);
                        textView.setText("Your free trial is active. \nDays Remaining: " + TimeUnit.MILLISECONDS.toDays(time - System.currentTimeMillis()));
                    }
                }else{
                    if(!snapshot.hasChild("subscriptionStatus")) {
                        setContentView(R.layout.subscribe_fastway_prem);
                        Button button = findViewById(R.id.subscribeFastwayPremium);
                        button.setOnClickListener(click -> {
                            if(!gotKey)
                            {
                                Toast.makeText(FastwayPremiums.this,"One Second....",Toast.LENGTH_SHORT).show();
                                return;
                            }
                            Intent intent = new Intent(FastwayPremiums.this, CashFreeGateway.class);
                            intent.putExtra("amount","59900");
                            intent.putExtra("keys",testKey);
                            startActivityForResult(intent,2);
                        });
                    }else{
                        long subTime = Long.parseLong(String.valueOf(snapshot.child("subscriptionStatus").getValue()));
                        if(System.currentTimeMillis() > subTime){
                            setContentView(R.layout.isfreetraialrunning);
                            TextView textView = findViewById(R.id.freeTrailUpdateTextView);
                            textView.setText("Your subscription is over. Subscribe now \u20b9599 per month");
                            Button button = findViewById(R.id.subscribePremFreeTrialEnded);
                            button.setVisibility(View.VISIBLE);
                            button.setText("Subscribe Now");
                            button.setOnClickListener(click -> {
                                if(!gotKey)
                                {
                                    Toast.makeText(FastwayPremiums.this,"One Second....",Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                Intent intent = new Intent(FastwayPremiums.this, CashFreeGateway.class);
                                intent.putExtra("amount","59900");
                                intent.putExtra("keys",testKey);
                                startActivityForResult(intent,2);
                            });
                        }else{
                            setContentView(R.layout.isfreetraialrunning);
                            TextView textView = findViewById(R.id.freeTrailUpdateTextView);
                            textView.setText("Your subscription is active. \nDays Remaining: " + TimeUnit.MILLISECONDS.toDays(subTime - System.currentTimeMillis()));
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
//        allPremsIDS = getSharedPreferences("AllPremiumID",MODE_PRIVATE);
//        premEdit = allPremsIDS.edit();
//        logininfo = getSharedPreferences("loginInfo",MODE_PRIVATE);
//        databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Admin").child(Objects.requireNonNull(auth.getUid()));
//        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//            @SuppressLint("SetTextI18n")
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if(snapshot.hasChild("freeTrialDate")){
//                    setContentView(R.layout.isfreetraialrunning);
//                    long freeTime = Long.parseLong(String.valueOf(snapshot.child("freeTrialDate").getValue()));
//                    if(System.currentTimeMillis() > freeTime){
//                        TextView textView = findViewById(R.id.freeTrailUpdateTextView);
//                        Button button = findViewById(R.id.subscribePremFreeTrialEnded);
//                        textView.setText("Your free trail is already ended subscribe premium now\nStarting \u20b9599 per month");
//                        button.setVisibility(View.VISIBLE);
//
//                        button.setOnClickListener(click -> {
//                            if(!allPremsIDS.contains("contactNum")){
//                                AlertDialog.Builder builder = new AlertDialog.Builder(FastwayPremiums.this);
//                                builder.setTitle("Contact Info").setMessage("Enter your contact number below");
//                                LinearLayout linearLayout = new LinearLayout(FastwayPremiums.this);
//                                linearLayout.setOrientation(LinearLayout.VERTICAL);
//                                EditText editText = new EditText(FastwayPremiums.this);
//                                editText.setBackground(null);
//                                editText.setHint("Enter Number Here");
//                                editText.setMaxLines(10);
//                                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
//
//                                linearLayout.addView(editText);
//                                builder.setView(linearLayout);
//                                builder.setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        if(editText.getText().toString().length() == 10){
//                                            premEdit.putString("contactNum",editText.getText().toString());
//                                            premEdit.apply();
//                                            Toast.makeText(FastwayPremiums.this, "Number Saved Successfully", Toast.LENGTH_SHORT).show();
//                                            dialog.dismiss();
//                                        }else
//                                            Toast.makeText(FastwayPremiums.this, "Check your input", Toast.LENGTH_SHORT).show();
//                                    }
//                                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        dialog.dismiss();
//                                    }
//                                }).create();
//
//                                builder.show();
//                            }else{
//                                SharedPreferences sharedPreferences = getSharedPreferences("AdminPremiumDetails",MODE_PRIVATE);
//                                SharedPreferences.Editor editor = sharedPreferences.edit();
//                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Admin").child(auth.getUid());
//                                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//                                    @Override
//                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                        if(snapshot.hasChild("subRefID")){
//                                            String refID = snapshot.child("subRefID").getValue(String.class);
//                                            RequestQueue requestQueue = Volley.newRequestQueue(FastwayPremiums.this);
//                                            StringRequest stringRequest = new StringRequest(Request.Method.POST, subRefProd, response -> {
//                                                String resp = response.trim();
//                                                databaseReference.child("freeTrialDate").removeValue();
//                                                Log.i("resp", resp);
//                                                databaseReference.child("premium").setValue(resp);
//                                                switch (resp) {
//                                                    case "INITIALIZED":
//                                                        String paymentUrl = Objects.requireNonNull(snapshot.child("subRefURL").getValue(String.class)).trim();
//                                                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(paymentUrl));
//                                                        startActivity(browserIntent);
//                                                        break;
//                                                    case "BANK_APPROVAL_PENDING":
//                                                        new KAlertDialog(FastwayPremiums.this, KAlertDialog.NORMAL_TYPE)
//                                                                .setTitleText("Waiting")
//                                                                .setContentText("Waiting for bank to approve the transaction")
//                                                                .setConfirmText("Alright")
//                                                                .setConfirmClickListener(l -> {
//                                                                    l.dismissWithAnimation();
//                                                                    finish();
//                                                                }).show();
//                                                        break;
////                                                    case "ACTIVE":
////                                                        setContentView(R.layout.activity_fastway_premiums);
////                                                        Button button = findViewById(R.id.cancelSubscriptionButton);
////                                                        break;
////                                                    case "ON_HOLD":
////                                                        setContentView(R.layout.onhold_fastway_sub);
////                                                        break;
////                                                    case "COMPLETED":
////                                                        setContentView(R.layout.completed_fastway_sub);
////                                                        break;
////                                                    case "CANCELLED":
////                                                        setContentView(R.layout.cancelled_fastway_sub);
////                                                        break;
//                                                }
//
//                                            }, error -> {
//
//                                            }){
//                                                @NonNull
//                                                @Override
//                                                protected Map<String, String> getParams() {
//                                                    Map<String,String> params = new HashMap<>();
//                                                    params.put("subID", refID);
//                                                    return params;
//                                                }
//                                            };
//
//                                            requestQueue.add(stringRequest);
//                                        }else{
//
//                                            RequestQueue requestQueue = Volley.newRequestQueue(FastwayPremiums.this);
//                                            StringRequest stringRequest = new StringRequest(Request.Method.POST, prodUrl, response -> {
//                                                Log.i("resp", response);
//
//                                                if(response != null){
//                                                    String[] url = response.split(",");
////                                                Log.i("resp",url.toString());
//                                                    String paymentURL = url[0];
//                                                    String subID = url[1];
//                                                    Log.i("resp",paymentURL);
//                                                    Log.i("resp",subID);
//                                                    SharedPreferences sharedPreferences = getSharedPreferences("AdminPremiumDetails",MODE_PRIVATE);
//                                                    SharedPreferences.Editor editor = sharedPreferences.edit();
//                                                    editor.putString("url",paymentURL);
//                                                    editor.putString("subID",subID);
//                                                    editor.apply();
//                                                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(paymentURL));
//                                                    startActivity(browserIntent);
//                                                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().getRoot().child("Admin").child(auth.getUid());
//                                                    reference.child("freeTrialDate").removeValue();
//                                                    reference.child("premium").setValue("INITIALIZE");
//                                                    reference.child("subRefID").setValue(subID.trim());
//                                                    reference.child("subRefURL").setValue(paymentURL.trim());
//
//
//
//                                                }
//
//                                            }, new Response.ErrorListener() {
//                                                @Override
//                                                public void onErrorResponse(VolleyError error) {
//
//                                                }
//                                            }){
//                                                @NonNull
//                                                @Override
//                                                protected Map<String, String> getParams() {
//                                                    Map<String,String> params = new HashMap<>();
//                                                    params.put("subscriptionID",auth.getUid() + "1234");
//                                                    premEdit.putString(auth.getUid() + "1234","h");
//                                                    params.put("planID","FoodinePartnerSub");
//                                                    params.put("email",logininfo.getString("email",""));
//                                                    params.put("phone",allPremsIDS.getString("contactNum",""));
//                                                    return params;
//                                                }
//                                            };
//                                            requestQueue.add(stringRequest);
//                                        }
//                                    }
//
//                                    @Override
//                                    public void onCancelled(@NonNull DatabaseError error) {
//
//                                    }
//                                });
//                            }
//                        });
//                    }else{
//                        long remainingDays = freeTime - System.currentTimeMillis();
//                        TextView textView = findViewById(R.id.freeTrailUpdateTextView);
//                        textView.setText("Your free trial is active\nDays left: " + TimeUnit.MILLISECONDS.toDays(remainingDays));
//                    }
//                }else{
//                    SharedPreferences sharedPreferences = getSharedPreferences("AdminPremiumDetails",MODE_PRIVATE);
//                    SharedPreferences.Editor editor = sharedPreferences.edit();
//                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Admin").child(auth.getUid());
//                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot snapshot) {
//                            if(snapshot.hasChild("subRefID")){
//                                final TextView[] textView = new TextView[1];
//                                String refID = snapshot.child("subRefID").getValue(String.class);
//                                RequestQueue requestQueue = Volley.newRequestQueue(FastwayPremiums.this);
//                                StringRequest stringRequest = new StringRequest(Request.Method.POST, subRefProd, response -> {
//                                    String resp = response.trim();
//                                    Log.i("resp", resp);
//                                    databaseReference.child("premium").setValue(resp);
//                                    switch (resp) {
//                                        case "INITIALIZED":
//                                            String paymentUrl = Objects.requireNonNull(snapshot.child("subRefURL").getValue(String.class)).trim();
//                                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(paymentUrl));
//                                            startActivity(browserIntent);
//                                            break;
//                                        case "BANK_APPROVAL_PENDING":
//                                            new KAlertDialog(FastwayPremiums.this, KAlertDialog.NORMAL_TYPE)
//                                                    .setTitleText("Waiting")
//                                                    .setContentText("Waiting for bank to approve the transaction")
//                                                    .setConfirmText("Alright")
//                                                    .setConfirmClickListener(l -> {
//                                                        l.dismissWithAnimation();
//                                                        finish();
//                                                    }).show();
//                                            break;
//                                                    case "ACTIVE":
//                                                        setContentView(R.layout.isfreetraialrunning);
//                                                        Button button = findViewById(R.id.subscribePremFreeTrialEnded);
//                                                        button.setText("Cancel Subscription");
//                                                        editor.putString("status","active");
//                                                        break;
//                                                    case "ON_HOLD":
//                                                        setContentView(R.layout.isfreetraialrunning);
//                                                        textView[0] = findViewById(R.id.freeTrailUpdateTextView);
//                                                        textView[0].setText("Your subscription is ON-HOLD. Pay Subscription amount to activate subscription");
//                                                        Button myBOn = findViewById(R.id.subscribePremFreeTrialEnded);
//                                                        myBOn.setVisibility(View.VISIBLE);
//                                                        myBOn.setText("Renew Subs");
//                                                        break;
//                                                    case "COMPLETED":
//                                                        setContentView(R.layout.isfreetraialrunning);
//                                                        textView[0] = findViewById(R.id.freeTrailUpdateTextView);
//                                                        textView[0].setText("You have completed your subscription");
//                                                        Button myBs = findViewById(R.id.subscribePremFreeTrialEnded);
//                                                        myBs.setVisibility(View.VISIBLE);
//                                                        break;
//                                                    case "CANCELLED":
//                                                        setContentView(R.layout.isfreetraialrunning);
//                                                        textView[0] = findViewById(R.id.freeTrailUpdateTextView);
//                                                        textView[0].setText("You have cancelled your subscription\nOnce cancelled you can't create another one\nContact Fastway for more info");
//                                                        Button myB = findViewById(R.id.subscribePremFreeTrialEnded);
//                                                        myB.setVisibility(View.INVISIBLE);
//                                                        break;
//                                    }
//
//                                }, error -> {
//
//                                }){
//                                    @NonNull
//                                    @Override
//                                    protected Map<String, String> getParams() {
//                                        Map<String,String> params = new HashMap<>();
//                                        params.put("subID", refID);
//                                        return params;
//                                    }
//                                };
//
//                                requestQueue.add(stringRequest);
//                            }else{
//
//                                RequestQueue requestQueue = Volley.newRequestQueue(FastwayPremiums.this);
//                                StringRequest stringRequest = new StringRequest(Request.Method.POST, prodUrl, response -> {
//                                    Log.i("resp", response);
//
//                                    if(response != null){
//                                        String[] url = response.split(",");
////                                                Log.i("resp",url.toString());
//                                        String paymentURL = url[0];
//                                        String subID = url[1];
//                                        Log.i("resp",paymentURL);
//                                        Log.i("resp",subID);
//                                        SharedPreferences sharedPreferences = getSharedPreferences("AdminPremiumDetails",MODE_PRIVATE);
//                                        SharedPreferences.Editor editor = sharedPreferences.edit();
//                                        editor.putString("url",paymentURL);
//                                        editor.putString("subID",subID);
//                                        editor.apply();
//                                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(paymentURL));
//                                        startActivity(browserIntent);
//                                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().getRoot().child("Admin").child(auth.getUid());
//                                        reference.child("premium").setValue("INITIALIZE");
//                                        reference.child("subRefID").setValue(subID.trim());
//                                        reference.child("subRefURL").setValue(paymentURL.trim());
//                                    }
//
//                                }, new Response.ErrorListener() {
//                                    @Override
//                                    public void onErrorResponse(VolleyError error) {
//
//                                    }
//                                }){
//                                    @NonNull
//                                    @Override
//                                    protected Map<String, String> getParams() {
//                                        Map<String,String> params = new HashMap<>();
//                                        params.put("subscriptionID",auth.getUid() + "1234");
//                                        params.put("planID","FoodinePartnerSub");
//                                        params.put("email",logininfo.getString("email",""));
//                                        params.put("phone",allPremsIDS.getString("contactNum",""));
//                                        return params;
//                                    }
//                                };
//                                requestQueue.add(stringRequest);
//                            }
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError error) {
//
//                        }
//                    });
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });

    }

    @Override
    protected void onResume() {
        super.onResume();
//        Log.i("info","called");
//        FirebaseAuth auth = FirebaseAuth.getInstance();
//        SharedPreferences sharedPreferences = getSharedPreferences("AdminPremiumDetails",MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Admin").child(auth.getUid());
//        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if(snapshot.hasChild("subRefID")){
//                    String refID = snapshot.child("subRefID").getValue(String.class);
//                    RequestQueue requestQueue = Volley.newRequestQueue(FastwayPremiums.this);
//                    StringRequest stringRequest = new StringRequest(Request.Method.POST, subRefProd, response -> {
//                        String resp = response.trim();
//                        Log.i("resp", resp);
//                        databaseReference.child("premium").setValue(resp);
//                        switch (resp) {
//                            case "INITIALIZED":
//
//                                break;
//                            case "BANK_APPROVAL_PENDING":
//
//                                break;
////                                                    case "ACTIVE":
////                                                        setContentView(R.layout.active_fastway_premium);
////                                                        Button button = findViewById(R.id.cancelSubscriptionButton);
////                                                        break;
////                                                    case "ON_HOLD":
////                                                        setContentView(R.layout.onhold_fastway_sub);
////                                                        break;
////                                                    case "COMPLETED":
////                                                        setContentView(R.layout.completed_fastway_sub);
////                                                        break;
////                                                    case "CANCELLED":
////                                                        setContentView(R.layout.cancelled_fastway_sub);
////                                                        break;
//                        }
//
//                    }, error -> {
//
//                    }){
//                        @NonNull
//                        @Override
//                        protected Map<String, String> getParams() {
//                            Map<String,String> params = new HashMap<>();
//                            params.put("subID", refID);
//                            return params;
//                        }
//                    };
//
//                    requestQueue.add(stringRequest);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == 2){
            long subTime = System.currentTimeMillis() + 2592000000L;
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Admin").child(Objects.requireNonNull(auth.getUid()));
            databaseReference.child("subscriptionStatus").setValue(String.valueOf(subTime));
            editor.putString("status","active");
            editor.apply();
            if(freeTrial)
                databaseReference.child("freeTrialDate").removeValue();

            finish();
        }else {
            Toast.makeText(this, "Payment Failed... Try again", Toast.LENGTH_SHORT).show();
        }
    }
}