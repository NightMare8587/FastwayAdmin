package com.consumers.fastwayadmin.ListViewActivity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.consumers.fastwayadmin.NavFrags.VendorDetailsActivity;
import com.consumers.fastwayadmin.R;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import karpuzoglu.enes.com.fastdialog.Animations;
import karpuzoglu.enes.com.fastdialog.FastDialog;
import karpuzoglu.enes.com.fastdialog.FastDialogBuilder;
import karpuzoglu.enes.com.fastdialog.Type;

public class MyOrdersTransactions extends AppCompatActivity {
    String getBenStatus = "https://intercellular-stabi.000webhostapp.com/benStatusFolder/benStatus.php";
    String neftUrl = "https://intercellular-stabi.000webhostapp.com/CheckoutPayouts/payoutNeft.php";
    String genratedToken = "";
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
        new MakePayout().execute();
    }

    public class MakePayout extends AsyncTask<Void,Void,Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            RequestQueue requestQueue = Volley.newRequestQueue(MyOrdersTransactions.this);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, neftUrl, new Response.Listener<String>() {
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
            StringRequest stringRequest = new StringRequest(Request.Method.POST, authToken, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.i("response",response);
                    if(response.trim().equals("Token is valid")){
                        new fetchBenDetails().execute();
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
            RequestQueue requestQueue = Volley.newRequestQueue(MyOrdersTransactions.this);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, getBenStatus, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.i("resp",response.toString());
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
                    FirebaseAuth auth = FirebaseAuth.getInstance();
                    params.put("token",genratedToken);
                    params.put("benID",String.valueOf(auth.getUid()));
                    return params;
                }
            };
            requestQueue.add(stringRequest);
//            JSONObject jsonObject = new JSONObject();
//            try {
//                FirebaseAuth auth =FirebaseAuth.getInstance();
//                jsonObject.put("token",String.valueOf(genratedToken));
//                jsonObject.put("benID",String.valueOf(auth.getUid()));
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, getBenStatus, jsonObject, new Response.Listener<JSONObject>() {
//                @Override
//                public void onResponse(JSONObject response) {
//                    Log.i("resp",response.toString());
//                }
//            }, new Response.ErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError error) {
//
//                }
//            });
//            requestQueue.add(jsonObjectRequest);
            return null;
        }
    }
}