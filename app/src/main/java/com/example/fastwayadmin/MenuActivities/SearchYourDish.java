package com.example.fastwayadmin.MenuActivities;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.fastwayadmin.R;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

//import com.fatsecret.platform.services.android.Request;

public class SearchYourDish extends AppCompatActivity {
    List<String> names  = new ArrayList<>();
    List<String> image= new ArrayList<>();
    ProgressBar progressBar;
    RecyclerView recyclerView;
    ImageButton imageButton;
    EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_your_dish);
        initialise();
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(editText.length() == 0){
                    editText.setError("Field cant be Empty");
                    editText.requestFocus();
                    return;
                }

                try {
                    progressBar.setVisibility(View.VISIBLE);
                    final String RECIPE_BASE_URL = "https://api.edamam.com/search";
                    final String APP_ID_PARAM = "app_id";
                    final String APP_ID = "04c24719";
                    final String APP_KEY_PARAM = "app_key";
                    final String APP_KEY = "a195329edc4e34a3765c756828657bcc"; // PUT YOUR API KEY HERE !!!
                    final String APP_INGREDIENTS_PARAM = "q";
                    RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                    Uri uri = Uri.parse(RECIPE_BASE_URL).buildUpon()
                            .appendQueryParameter(APP_INGREDIENTS_PARAM,editText.getText().toString())
                            .appendQueryParameter(APP_ID_PARAM,APP_ID)
                            .appendQueryParameter(APP_KEY_PARAM,APP_KEY).build();

                    URL url = new URL(uri.toString());
                    Log.i("info",url.toString());

                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url.toString(), null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                           Log.i("info", String.valueOf(response.names()));
                            try {
                                names.clear();
                                image.clear();
                                JSONArray jsonObject = response.getJSONArray("hits");
                                Log.i("info", String.valueOf(jsonObject.length()));
                                for(int i=0;i<jsonObject.length();i++){
                                    JSONObject object = jsonObject.getJSONObject(i);
                                    JSONObject object1 = object.getJSONObject("recipe");
                                    Log.i("dish",object1.getString("label"));
                                    names.add(object1.getString("label"));
                                    image.add(object1.getString("image"));
                                }

                                progressBar.setVisibility(View.INVISIBLE);
                                recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                                recyclerView.setAdapter(new DisplayDish(names,image));

                            } catch (JSONException e) {
                                Toast.makeText(SearchYourDish.this, e.getLocalizedMessage()+"", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(SearchYourDish.this, error.getLocalizedMessage()+"", Toast.LENGTH_SHORT).show();
                        }
                    });

                    requestQueue.add(jsonObjectRequest);
                }catch (Exception e){
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(SearchYourDish.this, e.getLocalizedMessage()+"", Toast.LENGTH_SHORT).show();
                }

            }
        });


    }



    private void initialise() {
        recyclerView = findViewById(R.id.searchActivityRecyclerView);
        imageButton = findViewById(R.id.searchDatabaseButton);
        editText = findViewById(R.id.DishNameToSearch);
        progressBar = findViewById(R.id.searchDishLoading);
    }
}