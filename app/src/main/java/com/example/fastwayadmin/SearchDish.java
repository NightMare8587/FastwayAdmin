package com.example.fastwayadmin;

import androidx.appcompat.app.AppCompatActivity;

import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class SearchDish extends AppCompatActivity {
    EditText searchEdit;
    ImageButton searchButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_dish);
        initialise();

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (searchEdit.length() == 0) {
                    searchEdit.requestFocus();
                    searchEdit.setError("Field can't be Empty");
                    return;
                }
                HttpURLConnection httpURLConnection = null;
                BufferedReader reader = null;

                try {
                    final String RECIPE_BASE_URL = "https://api.edamam.com/search";
                    final String APP_ID_PARAM = "app_id";
                    final String APP_ID = "a4cf91e6";
                    final String APP_INGREDIENTS_PARAM = "q";
                    final String APP_FROM_PARAM = "from";
                    final String APP_TO_PARAM = "to";
                    final String APP_KEY_PARAM = "app_key";
                    final String APP_KEY = "f877ca4aaf3c173f3960a109091bb007"; // PUT YOUR API KEY HERE !!!
                    Uri builtUri = Uri.parse(RECIPE_BASE_URL).buildUpon()
                            .appendQueryParameter(APP_INGREDIENTS_PARAM, searchEdit.getText().toString())
                            .appendQueryParameter(APP_ID_PARAM, APP_ID)
                            .appendQueryParameter(APP_KEY_PARAM, APP_KEY)
                            .appendQueryParameter(APP_FROM_PARAM, 0+"")
                            .appendQueryParameter(APP_TO_PARAM, 4+"").build();

                    URL url = new URL(builtUri.toString());

                    Log.i("info", String.valueOf(url));
                }catch (Exception e){

                }
            }
        });
    }

    private void initialise() {
        searchButton = (ImageButton)findViewById(R.id.imageButton);
        searchEdit = (EditText)findViewById(R.id.searchDish);
    }
}