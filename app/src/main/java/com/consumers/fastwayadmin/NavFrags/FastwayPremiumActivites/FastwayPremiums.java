package com.consumers.fastwayadmin.NavFrags.FastwayPremiumActivites;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.consumers.fastwayadmin.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class FastwayPremiums extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String subRefURL = "https://intercellular-stabi.000webhostapp.com/payouts/initialisedSub.php";
    String testUrl = "https://intercellular-stabi.000webhostapp.com/payouts/subscription.php";
    String prodUrl = "https://intercellular-stabi.000webhostapp.com/payouts/prodSub.php";
    String subRefProd = "https://intercellular-stabi.000webhostapp.com/payouts/prodInitialise.php";
    String cancelSubURL = "https://intercellular-stabi.000webhostapp.com/payouts/cancelSubs.php";
    Button subscribePrem;
    DatabaseReference databaseReference;
    DatabaseReference reference;
    DatabaseReference backgroudnRef;
    String subrefernceID;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Admin").child(Objects.requireNonNull(auth.getUid()));
        sharedPreferences = getSharedPreferences("AdminPremiumDetails",MODE_PRIVATE);
        editor = sharedPreferences.edit();
        if(sharedPreferences.contains("status") && sharedPreferences.getString("status","").equals("active")){
            setContentView(R.layout.activity_fastway_premiums);
        }else{
            if(sharedPreferences.contains("status")) {
                setContentView(R.layout.subscribe_fastway_prem);
                subscribePrem = findViewById(R.id.subscribeFastwayPremium);
                subscribePrem.setOnClickListener(click -> {
                    RequestQueue requestQueue = Volley.newRequestQueue(FastwayPremiums.this);
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, prodUrl, response -> {
                        Log.i("resp", response);

                        if (response != null) {
                            String[] url = response.split(",");
//                                                Log.i("resp",url.toString());
                            String paymentURL = url[0];
                            String subID = url[1];
                            Log.i("resp", paymentURL);
                            Log.i("resp", subID);
                            SharedPreferences sharedPreferences = getSharedPreferences("AdminPremiumDetails", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("url", paymentURL);
                            editor.putString("subID", subID);
                            editor.apply();
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(paymentURL));
                            startActivity(browserIntent);
                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().getRoot().child("Admin").child(auth.getUid());
                            reference.child("premium").setValue("INITIALIZE");
                            reference.child("subRefID").setValue(subID.trim());
                            reference.child("subRefURL").setValue(paymentURL.trim());
                        }

                    }, error -> {

                    }) {
                        @NonNull
                        @Override
                        protected Map<String, String> getParams() {
                            Map<String, String> params = new HashMap<>();
                            params.put("subscriptionID", auth.getUid() + "4");
                            params.put("planID", "FastwayAdminPremium");
                            params.put("email", "maheshwariloya@gmail.com");
                            params.put("phone", "8076531395");
                            return params;
                        }
                    };
                    requestQueue.add(stringRequest);
                });
            }
        }
    }
}