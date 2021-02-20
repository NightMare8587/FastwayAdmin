

package com.example.fastwayadmin.MenuActivities;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.fastwayadmin.R;

//import com.fatsecret.platform.services.android.Request;

public class SearchYourDish extends AppCompatActivity {

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
//                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
//                String URL = "https://api.spoonacular.com/recipes/search?query=" + editText.getText().toString() + "&number=30&apiKey=5a66f064be3c46a9af435708f3a56476";
//
//                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        Log.i("info",URL);
//                        Toast.makeText(SearchYourDish.this, URL+"", Toast.LENGTH_SHORT).show();
//                    }
//                }, new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        Toast.makeText(SearchYourDish.this, "error", Toast.LENGTH_SHORT).show();
//                    }
//                });

                String key = "e7b6015ed2d6410696ef9f3f84bbfc64";
                String secret = "49447fef5ef94ea2b23069c6b89f4147";
                String query = editText.getText().toString();
                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());


            }
        });


    }



    private void initialise() {
        recyclerView = findViewById(R.id.searchActivityRecyclerView);
        imageButton = findViewById(R.id.searchDatabaseButton);
        editText = findViewById(R.id.DishNameToSearch);
    }
}