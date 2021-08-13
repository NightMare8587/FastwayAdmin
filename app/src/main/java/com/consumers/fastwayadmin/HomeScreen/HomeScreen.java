package com.consumers.fastwayadmin.HomeScreen;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.consumers.fastwayadmin.NavFrags.AccountFrag;
import com.consumers.fastwayadmin.NavFrags.HomeFrag;
import com.consumers.fastwayadmin.NavFrags.MenuFrag;
import com.consumers.fastwayadmin.NavFrags.TablesFrag;
import com.consumers.fastwayadmin.R;
import com.consumers.fastwayadmin.RandomChatNoww;
import com.gauravk.bubblenavigation.BubbleNavigationConstraintView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class HomeScreen extends AppCompatActivity {

    BubbleNavigationConstraintView bubble;
    String URL = "https://fcm.googleapis.com/fcm/send";
    FragmentManager manager;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        initialise();

        manager.beginTransaction().replace(R.id.homescreen,new HomeFrag()).commit();
        bubble.setNavigationChangeListener((view, position) -> {
            switch (position){
                case 0:
                    manager.beginTransaction().replace(R.id.homescreen,new HomeFrag()).commit();
                    break;
                case 1:
                    manager.beginTransaction().replace(R.id.homescreen,new MenuFrag()).commit();

                    break;
                case 2:
                    manager.beginTransaction().replace(R.id.homescreen,new TablesFrag()).commit();

                    break;
                case 3:
                    manager.beginTransaction().replace(R.id.homescreen,new AccountFrag()).commit();

                    break;
            }
        });
        FirebaseAuth auth = FirebaseAuth.getInstance();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(Objects.requireNonNull(auth.getUid())).child("Tables");
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                if (dataSnapshot.hasChild("timeInMillis")) {
                                    String tableNum = dataSnapshot.child("tableNum").getValue(String.class);
                                    String time = String.valueOf(dataSnapshot.child("timeInMillis").getValue());
                                    final String id = String.valueOf(dataSnapshot.child("customerId").getValue());
                                    int result = time.compareTo(String.valueOf(System.currentTimeMillis()));
                                    if (result < 0) {
                                        assert tableNum != null;
                                        databaseReference.child(tableNum).child("customerId").removeValue();
                                        databaseReference.child(tableNum).child("time").removeValue();
                                        databaseReference.child(tableNum).child("timeInMillis").removeValue();
                                        databaseReference.child(tableNum).child("status").setValue("available");
                                        RequestQueue requestQueue = Volley.newRequestQueue(HomeScreen.this);
                                        JSONObject main = new JSONObject();
                                        try {
                                            main.put("to", "/topics/" + id + "");
                                            JSONObject notification = new JSONObject();
                                            notification.put("title", "Cancelled");
                                            notification.put("click_action", "Table Frag");
                                            notification.put("body", "Your Reserved Tables is cancelled because you didn't make it on time");
                                            main.put("notification", notification);

                                            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, main, new Response.Listener<JSONObject>() {
                                                @Override
                                                public void onResponse(JSONObject response) {

                                                }
                                            }, new Response.ErrorListener() {
                                                @Override
                                                public void onErrorResponse(VolleyError error) {
//                                               Toast.makeText(, error.getLocalizedMessage()+"null", Toast.LENGTH_SHORT).show();
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

                                            requestQueue.add(jsonObjectRequest);
                                        } catch (Exception e) {
                                            Toast.makeText(HomeScreen.this, e.getLocalizedMessage() + "null", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        },0,5000);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.notification,menu);
        return true;
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        Log.i("called","after logout");
//        stopService(new Intent(this,MyService.class));
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        SharedPreferences sharedPreferences = getSharedPreferences("Stop Services",MODE_PRIVATE);
//        if(sharedPreferences.contains("online")){
//            if(sharedPreferences.getString("online","").equals("false")) {
//                stopService(new Intent(this,MyService.class));
//            }else
//                startService(new Intent(this,MyService.class));
//        }
//
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
////        ServiceInitiatorClass serviceInitiatorClass = new ServiceInitiatorClass();
////        Toast.makeText(serviceInitiatorClass, "Hi", Toast.LENGTH_SHORT).show();
//        Log.i("hello","I called");
//        stopService(new Intent(this,MyService.class));
//    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.notification)
            startActivity(new Intent(HomeScreen.this, SupportActivity.class));
        else
          startActivity(new Intent(HomeScreen.this, RandomChatNoww.class));
        return super.onOptionsItemSelected(item);
    }

    private void initialise() {
        auth = FirebaseAuth.getInstance();
        bubble = findViewById(R.id.top_navigation_constraint);
        manager = getSupportFragmentManager();
        FirebaseMessaging.getInstance().subscribeToTopic(Objects.requireNonNull(auth.getUid()));
    }
}