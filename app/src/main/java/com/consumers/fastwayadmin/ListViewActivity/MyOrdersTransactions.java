package com.consumers.fastwayadmin.ListViewActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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
    String getBenStatus = "https://intercellular-stabi.000webhostapp.com/benStatusFolder/benStatus.php";
    String neftUrl = "https://intercellular-stabi.000webhostapp.com/CheckoutPayouts/payoutNeft.php";
    String singleBenStatus = "https://intercellular-stabi.000webhostapp.com/benStatusFolder/singleStatus.php";
    String genratedToken = "";
    List<String> status = new ArrayList<>();
    int count = 0;
    List<String> amount = new ArrayList<>();
    List<String> time = new ArrayList<>();
    String currentTransID;
    List<String> transID = new ArrayList<>();
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
        fastDialog = new FastDialogBuilder(MyOrdersTransactions.this, Type.PROGRESS)
                .progressText("Fetching Details...")
                .setAnimation(Animations.SLIDE_TOP)
                .create();
        fastDialog.show();
        reference = FirebaseDatabase.getInstance().getReference().getRoot().child("Admin").child(Objects.requireNonNull(auth.getUid()));
        reference.child("Transactions").limitToLast(4).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    time.clear();
                    amount.clear();
                    transID.clear();
                    status.clear();

                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                        time.add(String.valueOf(dataSnapshot.getKey()));
                        transID.add(String.valueOf(dataSnapshot.child("transID").getValue()));
                    }

                    new MakePayout().execute();

                }else{
                    new KAlertDialog(MyOrdersTransactions.this,KAlertDialog.ERROR_TYPE)
                            .setTitleText("Error")
                            .setContentText("No transaction found")
                            .setConfirmText("Exit")
                            .setConfirmClickListener(KAlertDialog::dismissWithAnimation).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
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
            RequestQueue requestQueue = Volley.newRequestQueue(MyOrdersTransactions.this);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, testBearerToken, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.i("response",response);
                    if(response.trim().equals("Token is valid")){
                        for(int i=0;i<time.size();i++){
                            currentTransID = transID.get(i);
                            Log.i("id",currentTransID);
                            count++;
//                            new fetchBenDetails().execute();
                            RequestQueue requestQueue = Volley.newRequestQueue(MyOrdersTransactions.this);
                            int finalI = i;
                            StringRequest stringRequest = new StringRequest(Request.Method.POST, singleBenStatus, new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    Log.i("resp", response);

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
                                    params.put("benID",transID.get(finalI));
                                    Log.i("count",count + "");
                                    return params;

                                }
                            };
                            requestQueue.add(stringRequest);
                            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                                    0,
                                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                        }
                        Log.i("value",transID.toString());
                        fastDialog.dismiss();
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

    public class fetchBenDetails extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... voids) {
//            RequestQueue requestQueue = Volley.newRequestQueue(MyOrdersTransactions.this);
//            StringRequest stringRequest = new StringRequest(Request.Method.POST, singleBenStatus, new Response.Listener<String>() {
//                @Override
//                public void onResponse(String response) {
//                    Log.i("resp", response);
//
//                }
//            }, new Response.ErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError error) {
//
//                }
//            }){
//                @Nullable
//                @Override
//                protected Map<String, String> getParams() {
//                    Map<String,String> params = new HashMap<>();
//                    FirebaseAuth auth = FirebaseAuth.getInstance();
//                    params.put("token",genratedToken);
//                    params.put("benID",currentTransID);
//                    Log.i("count",count + "");
//                    return params;
//
//                }
//            };
//            requestQueue.add(stringRequest);
//            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
//                    0,
//                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            return null;
        }
    }
}