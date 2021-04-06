package com.consumer.fastwayadmin.MenuActivities;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.consumer.fastwayadmin.R;

import org.json.JSONObject;

import java.net.URL;

public class CustomDishImageSearch extends AppCompatActivity {
    String client_id = "na8l4saKdLlLGjndAI3aFi3saAdVwK9x";
    String client_secret = "VcOrhtbzJljxhDEN";
    String name;
    String url = "https://api.shutterstock.com/v2/images/search";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_dish_image_search);
        name = getIntent().getStringExtra("name");

        try {
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            Uri uri = Uri.parse(url).buildUpon()
                    .appendPath("v2/bmE4bDRzYUtkTGxMR2puZEFJM2FGaTNzYUFkVndLOXgvMjkxNzcwODQ1L2N1c3RvbWVyLzMvTEdINWJRTlppaF9jWUZVZnU5bVBaQjJLRjVJMWdLUXpxVVZGbjJRSzRJelVHRTBDbVN5Qko2OEt3SXhXVVhqSHJwWmdHNFZ5YVhJdFc2U3Y5U1hMeTlIMDNrMjBoSDkyVnFJTVhMTUh6b3VtbUJkQnlsOEpxT2xXTWo3M3N0aFVHdzA4NDByWlhWbTJfMF9xN0VNQ19ZY29rNm5jTVRXOTZ0ZHpjaDE2NjN1TGEzcDZmSW9QVHU5c3VyN1NLT0Q0ZXREbE93MlpzUHZFM3BYdTFZazliUQ")
                    .appendQueryParameter("query",name).build();


            URL main = new URL(uri.toString());
            Log.i("url",main.toString());

            JsonObjectRequest jsonObjectRequest  = new JsonObjectRequest(Request.Method.GET, main.toString(), null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Toast.makeText(CustomDishImageSearch.this, "request made", Toast.LENGTH_SHORT).show();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });

            requestQueue.add(jsonObjectRequest);
        }catch (Exception e){
            Toast.makeText(this, e.getLocalizedMessage()+"", Toast.LENGTH_SHORT).show();
        }
    }
}