package com.consumers.fastwayadmin.NavFrags.CurrentTakeAwayOrders;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.consumers.fastwayadmin.CancelClass;
import com.consumers.fastwayadmin.NavFrags.homeFrag.ApproveCurrentOrder;
import com.consumers.fastwayadmin.R;
import com.developer.kalert.KAlertDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import karpuzoglu.enes.com.fastdialog.Animations;
import karpuzoglu.enes.com.fastdialog.FastDialog;
import karpuzoglu.enes.com.fastdialog.FastDialogBuilder;
import karpuzoglu.enes.com.fastdialog.NegativeClick;
import karpuzoglu.enes.com.fastdialog.PositiveClick;
import karpuzoglu.enes.com.fastdialog.Type;

public class ApproveCurrentTakeAway extends AppCompatActivity {
    List<String> quantity;
    List<String> dishName;
    String paymentMode;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    DatabaseReference databaseReference;
    List<String> halfOr;
    String url = "https://intercellular-stabi.000webhostapp.com/refunds/initiateRefund.php";
    DatabaseReference saveRefundInfo;
    Button decline,approve;
    SharedPreferences sharedPreferences;
    String digitCode;
    ListView listView,dishNames,halfOrList;
    String id,orderId,orderAmount;
    String URL = "https://fcm.googleapis.com/fcm/send";
    String state;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approve_current_take_away);
        id = getIntent().getStringExtra("id");
        sharedPreferences = getSharedPreferences("loginInfo",MODE_PRIVATE);
        state = sharedPreferences.getString("state","");
        saveRefundInfo = FirebaseDatabase.getInstance().getReference().getRoot().child("Users").child(id);
        orderAmount = getIntent().getStringExtra("orderAmount");
        orderId = getIntent().getStringExtra("orderID");
        paymentMode = getIntent().getStringExtra("payment");
        dishName = new ArrayList<>(getIntent().getStringArrayListExtra("dishName"));
        quantity = new ArrayList<>(getIntent().getStringArrayListExtra("DishQ"));
        halfOr = new ArrayList<>(getIntent().getStringArrayListExtra("halfOr"));
        Log.i("name",dishName.toString());
        decline = findViewById(R.id.declineTakeAwayButton);
        listView = findViewById(R.id.quantityTakeAwayListView);
        halfOrList = findViewById(R.id.halfOrTakeAwayListView);
        dishNames = findViewById(R.id.DishNamesTakeAwayListView);
        approve = findViewById(R.id.approveTakeAwayButton);
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().getRoot().child("Users").child(id);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                digitCode = String.valueOf(snapshot.child("digitCode").getValue());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,quantity);
        listView.setAdapter(arrayAdapter);

        ArrayAdapter<String> arrayAdapter1 = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,dishName);
        dishNames.setAdapter(arrayAdapter1);

        ArrayAdapter<String> arrayAdapter2 = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,halfOr);
        halfOrList.setAdapter(arrayAdapter2);

        approve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(paymentMode.equals("cash")){
                    new KAlertDialog(ApproveCurrentTakeAway.this,KAlertDialog.WARNING_TYPE)
                            .setTitleText("Warning")
                            .setContentText("Approve order only after you received cash payment")
                            .setConfirmText("Confirm Order")
                            .setConfirmClickListener(new KAlertDialog.KAlertClickListener() {
                                @Override
                                public void onClick(KAlertDialog kAlertDialog) {
                                    kAlertDialog.dismissWithAnimation();
                                    Toast.makeText(ApproveCurrentTakeAway.this, "Order Confirmed", Toast.LENGTH_SHORT).show();
                                    RequestQueue requestQueue = Volley.newRequestQueue(ApproveCurrentTakeAway.this);
                                    JSONObject main = new JSONObject();
                                    FirebaseAuth auth = FirebaseAuth.getInstance();
                                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(state).child(Objects.requireNonNull(auth.getUid())).child("Current TakeAway").child(id);

                                    try {
                                        main.put("to", "/topics/" + id + "");
                                        JSONObject notification = new JSONObject();
                                        notification.put("title", "Order Confirmed");
                                        notification.put("click_action", "Table Frag");
                                        notification.put("body", "Your order is confirmed by the owner");
                                        main.put("notification", notification);

                                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, main, new Response.Listener<JSONObject>() {
                                            @Override
                                            public void onResponse(JSONObject response) {

                                            }
                                        }, new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                Toast.makeText(ApproveCurrentTakeAway.this, error.getLocalizedMessage() + "null", Toast.LENGTH_SHORT).show();
                                            }
                                        }) {
                                            @Override
                                            public Map<String, String> getHeaders() throws AuthFailureError {
                                                Map<String, String> header = new HashMap<>();
                                                header.put("content-type", "application/json");
                                                header.put("authorization", "key=AAAAsigSEMs:APA91bEUF9ZFwIu84Jctci56DQd0TQOepztGOIKIBhoqf7N3ueQrkClw0xBTlWZEWyvwprXZmZgW2MNywF1pNBFpq1jFBr0CmlrJ0wygbZIBOnoZ0jP1zZC6nPxqF2MAP6iF3wuBHD2R");
                                                return header;
                                            }
                                        };
                                        reference.removeValue();
                                        requestQueue.add(jsonObjectRequest);
                                    } catch (Exception e) {
                                        Toast.makeText(ApproveCurrentTakeAway.this, e.getLocalizedMessage() + "null", Toast.LENGTH_SHORT).show();
                                    }
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            finish();
                                        }
                                    }, 1500);
                                }
                            }).setCancelText("No Wait")
                            .setCancelClickListener(new KAlertDialog.KAlertClickListener() {
                                @Override
                                public void onClick(KAlertDialog kAlertDialog) {
                                    kAlertDialog.dismissWithAnimation();
                                }
                            }).show();
                }else {
                    FastDialog fastDialog = new FastDialogBuilder(ApproveCurrentTakeAway.this, Type.DIALOG)
                            .setTitleText("OTP Code")
                            .setText("Enter 6 digit code below provided by user")
                            .setHint("Enter Code here")
                            .positiveText("Confirm")
                            .negativeText("Cancel")
                            .setAnimation(Animations.SLIDE_TOP)
                            .create();

                    fastDialog.show();

                    fastDialog.positiveClickListener(new PositiveClick() {
                        @Override
                        public void onClick(View view) {
                            if (fastDialog.getInputText().equals("")) {
                                Toast.makeText(ApproveCurrentTakeAway.this, "Field can't be empty", Toast.LENGTH_SHORT).show();
                                fastDialog.dismiss();
                            } else if (fastDialog.getInputText().equals(digitCode.trim())) {
                                Toast.makeText(ApproveCurrentTakeAway.this, "Order Confirmed", Toast.LENGTH_SHORT).show();
                                RequestQueue requestQueue = Volley.newRequestQueue(ApproveCurrentTakeAway.this);
                                fastDialog.dismiss();
                                JSONObject main = new JSONObject();
                                FirebaseAuth auth = FirebaseAuth.getInstance();
                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(state).child(Objects.requireNonNull(auth.getUid())).child("Current TakeAway").child(id);

                                try {
                                    main.put("to", "/topics/" + id + "");
                                    JSONObject notification = new JSONObject();
                                    notification.put("title", "Order Confirmed");
                                    notification.put("click_action", "Table Frag");
                                    notification.put("body", "Your order is confirmed by the owner");
                                    main.put("notification", notification);

                                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, main, new Response.Listener<JSONObject>() {
                                        @Override
                                        public void onResponse(JSONObject response) {

                                        }
                                    }, new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            Toast.makeText(ApproveCurrentTakeAway.this, error.getLocalizedMessage() + "null", Toast.LENGTH_SHORT).show();
                                        }
                                    }) {
                                        @Override
                                        public Map<String, String> getHeaders() throws AuthFailureError {
                                            Map<String, String> header = new HashMap<>();
                                            header.put("content-type", "application/json");
                                            header.put("authorization", "key=AAAAsigSEMs:APA91bEUF9ZFwIu84Jctci56DQd0TQOepztGOIKIBhoqf7N3ueQrkClw0xBTlWZEWyvwprXZmZgW2MNywF1pNBFpq1jFBr0CmlrJ0wygbZIBOnoZ0jP1zZC6nPxqF2MAP6iF3wuBHD2R");
                                            return header;
                                        }
                                    };
                                    reference.removeValue();
                                    userRef.child("digitCode").removeValue();
                                    requestQueue.add(jsonObjectRequest);
                                } catch (Exception e) {
                                    Toast.makeText(ApproveCurrentTakeAway.this, e.getLocalizedMessage() + "null", Toast.LENGTH_SHORT).show();
                                }
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        finish();
                                    }
                                }, 1500);
                            } else {
                                Toast.makeText(ApproveCurrentTakeAway.this, "Wrong Code", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    fastDialog.negativeClickListener(new NegativeClick() {
                        @Override
                        public void onClick(View view) {
                            fastDialog.dismiss();
                        }
                    });
                }
            }
        });

        decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RequestQueue requestQueue = Volley.newRequestQueue(ApproveCurrentTakeAway.this);
                JSONObject main = new JSONObject();
                FirebaseAuth auth = FirebaseAuth.getInstance();
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(state).child(Objects.requireNonNull(auth.getUid())).child("Current TakeAway").child(id);
                if (paymentMode.equals("online")) {
                    new InitiateRefund().execute();
                    try {
                        main.put("to", "/topics/" + id + "");
                        JSONObject notification = new JSONObject();
                        notification.put("title", "Order Declined");
                        notification.put("click_action", "Table Frag");
                        notification.put("body", "Your order is declined by the owner. Refund will be initiated Shortly\nContact Restaurant why order got cancelled");
                        main.put("notification", notification);

                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, main, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {

                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(ApproveCurrentTakeAway.this, error.getLocalizedMessage() + "null", Toast.LENGTH_SHORT).show();
                            }
                        }) {
                            @Override
                            public Map<String, String> getHeaders() throws AuthFailureError {
                                Map<String, String> header = new HashMap<>();
                                header.put("content-type", "application/json");
                                header.put("authorization", "key=AAAAsigSEMs:APA91bEUF9ZFwIu84Jctci56DQd0TQOepztGOIKIBhoqf7N3ueQrkClw0xBTlWZEWyvwprXZmZgW2MNywF1pNBFpq1jFBr0CmlrJ0wygbZIBOnoZ0jP1zZC6nPxqF2MAP6iF3wuBHD2R");
                                return header;
                            }
                        };
                        reference.removeValue();
                        userRef.child("digitCode").removeValue();
                        requestQueue.add(jsonObjectRequest);
                    } catch (Exception e) {
                        Toast.makeText(ApproveCurrentTakeAway.this, e.getLocalizedMessage() + "null", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    try {
                        main.put("to", "/topics/" + id + "");
                        JSONObject notification = new JSONObject();
                        notification.put("title", "Order Declined");
                        notification.put("click_action", "Table Frag");
                        notification.put("body", "Your order is declined by the owner. If you paid cash already then ask owner to refund it");
                        main.put("notification", notification);

                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, main, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {

                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(ApproveCurrentTakeAway.this, error.getLocalizedMessage() + "null", Toast.LENGTH_SHORT).show();
                            }
                        }) {
                            @Override
                            public Map<String, String> getHeaders() throws AuthFailureError {
                                Map<String, String> header = new HashMap<>();
                                header.put("content-type", "application/json");
                                header.put("authorization", "key=AAAAsigSEMs:APA91bEUF9ZFwIu84Jctci56DQd0TQOepztGOIKIBhoqf7N3ueQrkClw0xBTlWZEWyvwprXZmZgW2MNywF1pNBFpq1jFBr0CmlrJ0wygbZIBOnoZ0jP1zZC6nPxqF2MAP6iF3wuBHD2R");
                                return header;
                            }
                        };
                        reference.removeValue();
                        requestQueue.add(jsonObjectRequest);
                    } catch (Exception e) {
                        Toast.makeText(ApproveCurrentTakeAway.this, e.getLocalizedMessage() + "null", Toast.LENGTH_SHORT).show();
                    }
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                }, 1500);
            }
        });
    }
    public class InitiateRefund extends AsyncTask<Void,Void,Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            RequestQueue requestQueue = Volley.newRequestQueue(ApproveCurrentTakeAway.this);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.i("res",response.toString());

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }){
                @NonNull
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String> params = new HashMap<>();
                    FirebaseAuth auth = FirebaseAuth.getInstance();
                    String referid = orderId;
                    String finalReferIDForInfo = "refund_" + referid + "s";
                    Log.i("refundID",referid);
                    String time = String.valueOf(System.currentTimeMillis());
                    CancelClass cancelClass = new CancelClass(finalReferIDForInfo,orderAmount + "",orderId + "");
                    saveRefundInfo.child("Refunds").child(time).setValue(cancelClass);
                    params.put("referID",referid + "");
                    params.put("refundAmount",orderAmount);
                    params.put("orderID",orderId);
                    return params;
                }
            };
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                    0,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(stringRequest);
            return null;
        }
    }
}