package com.consumers.fastwayadmin.HomeScreen;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.aspose.cells.SaveFormat;
import com.aspose.cells.Workbook;
import com.consumers.fastwayadmin.Chat.RandomChatFolder.RandomChatWithUsers;
import com.consumers.fastwayadmin.Info.RestaurantDocuments.ReUploadDocumentsAgain;
import com.consumers.fastwayadmin.NavFrags.AccountFrag;
import com.consumers.fastwayadmin.NavFrags.CashCommission.CashTransactionCommissionActivity;
import com.consumers.fastwayadmin.NavFrags.HomeFrag;
import com.consumers.fastwayadmin.NavFrags.MenuFrag;
import com.consumers.fastwayadmin.NavFrags.TablesFrag;
import com.consumers.fastwayadmin.R;
import com.consumers.fastwayadmin.RandomChatNoww;
import com.gauravk.bubblenavigation.BubbleNavigationConstraintView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class HomeScreen extends AppCompatActivity {

    BubbleNavigationConstraintView bubble;
    String URL = "https://fcm.googleapis.com/fcm/send";
    FragmentManager manager;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor myEditor;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    long currentTime = System.currentTimeMillis();
    Timer timer = new Timer();
    DatabaseReference resRef;
    String UID;
    DatabaseReference reference;
    @SuppressLint("CommitPrefEdits")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        initialise();
        sharedPreferences = getSharedPreferences("loginInfo",MODE_PRIVATE);
        myEditor = sharedPreferences.edit();
        manager.beginTransaction().replace(R.id.homescreen,new HomeFrag()).commit();
        ConnectivityManager cm =
                (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);


      new BackgroundWork().execute();

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
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        Workbook workbook;
        if(!sharedPreferences.contains("workbookCreated")) {
            try {
                workbook = new Workbook();
                workbook.getWorksheets().get(0).getCells().get("A1").putValue("Hello World My Name!");
                workbook.getWorksheets().get(0).getCells().get("A2").putValue("Pulli Oya!");
                try {
                    workbook.save(path + "/ResTransactions.xlsx", SaveFormat.XLSX);
                    Log.i("info","FILE SAVED");
                    Toast.makeText(this, "File saved", Toast.LENGTH_SHORT).show();
                    myEditor.putString("workbookCreated","yes");
                    myEditor.apply();
                } catch (Exception e) {
                    Log.i("info",e.getLocalizedMessage());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference().getRoot().child("Admin").child(UID).child("Restaurant Documents");
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(sharedPreferences.getString("state","")).child(Objects.requireNonNull(UID)).child("Tables");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists() && dataSnapshot.hasChild("verified")){
                                if(Objects.equals(dataSnapshot.child("verified").getValue(String.class), "no")){
                                     AlertDialog.Builder alert = new AlertDialog.Builder(HomeScreen.this);
                                     alert.setTitle("Error")
                                             .setMessage("Your restaurant is not yet verified so you can't accept orders until verified")
                                             .setPositiveButton("Exit", (dialogInterface, i) -> {
                                                 dialogInterface.dismiss();
                                             }).setNegativeButton("Contact Fastway", (dialogInterface, i) -> {
                                                 dialogInterface.dismiss();

                                             }).create();
                                     alert.setCancelable(false);
                                     alert.show();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                boolean isConnected = activeNetwork != null &&
                        activeNetwork.isConnectedOrConnecting();

                if (!isConnected) {
                    View parentLayout = findViewById(android.R.id.content);
                    Snackbar.make(parentLayout, "Please connect to internet :)", Snackbar.LENGTH_SHORT)
                            .setAction("CLOSE", view -> {

                            })
                            .setActionTextColor(getResources().getColor(android.R.color.holo_red_light))
                            .show();
                } else {
                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
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
                                            databaseReference.child(tableNum).child("timeOfBooking").removeValue();
                                            databaseReference.child(tableNum).child("status").setValue("available");
                                            DatabaseReference removeFromUser = FirebaseDatabase.getInstance().getReference().getRoot().child("Users").child(id).child("Reserve Tables").child(UID);
                                            removeFromUser.child(Objects.requireNonNull(dataSnapshot.getKey())).removeValue();
                                            RequestQueue requestQueue = Volley.newRequestQueue(HomeScreen.this);
                                            JSONObject main = new JSONObject();
                                            try {
                                                main.put("to", "/topics/" + id + "");
                                                JSONObject notification = new JSONObject();
                                                notification.put("title", "Cancelled");
                                                notification.put("click_action", "Table Frag");
                                                notification.put("body", "Your Reserved Tables is cancelled because you didn't make it on time");
                                                main.put("notification", notification);

                                                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, main, response -> {

                                                }, error -> {
//                                               Toast.makeText(, error.getLocalizedMessage()+"null", Toast.LENGTH_SHORT).show();
                                                }) {
                                                    @Override
                                                    public Map<String, String> getHeaders() {
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
            }
        },0,10000);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.notification,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.notification)
            startActivity(new Intent(HomeScreen.this, SupportActivity.class));
        else if(id == R.id.randomChatMessagesUsers){
            startActivity(new Intent(HomeScreen.this, RandomChatWithUsers.class));
        }
        else
          startActivity(new Intent(HomeScreen.this, RandomChatNoww.class));
        return super.onOptionsItemSelected(item);
    }

    private void initialise() {
        UID = auth.getUid() + "";
        auth = FirebaseAuth.getInstance();
        bubble = findViewById(R.id.top_navigation_constraint);
        manager = getSupportFragmentManager();
        FirebaseMessaging.getInstance().subscribeToTopic(Objects.requireNonNull(UID));
        reference = FirebaseDatabase.getInstance().getReference().getRoot().child("Admin").child(UID);
    }

    public class BackgroundWork extends AsyncTask<Void,Void,Void>{
        @Override
        protected Void doInBackground(Void... voids) {
            resRef = FirebaseDatabase.getInstance().getReference().getRoot().child("Admin").child(Objects.requireNonNull(UID));
            resRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.hasChild("fastwayReply")){
                        AlertDialog.Builder alert = new AlertDialog.Builder(HomeScreen.this);
                        alert.setTitle("Reply")
                                .setMessage("You have a new reply from fastway")
                                .setCancelable(false)
                                .setPositiveButton("Exit", (dialog, which) -> {
                                    resRef.child("fastwayReply").removeValue();
                                    dialog.dismiss();
                                }).setNegativeButton("See Message", (dialog, which) -> {
                            resRef.child("fastwayReply").removeValue();
                            startActivity(new Intent(HomeScreen.this, RandomChatNoww.class));
                            dialog.dismiss();
                        }).create();
                        alert.setOnCancelListener(dialog -> {
                            dialog.dismiss();
                            resRef.child("fastwayReply").removeValue();
                        });
                        alert.show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            resRef = FirebaseDatabase.getInstance().getReference().getRoot().child("Admin").child(Objects.requireNonNull(UID)).child("Restaurant Documents");
            resRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists() && snapshot.hasChild("reasonForCancel")){
                        AlertDialog.Builder alert = new AlertDialog.Builder(HomeScreen.this);
                        String reason = snapshot.child("reasonForCancel").getValue(String.class);
                        alert.setTitle("Error").setMessage("Your restaurant registration is denied by fastway for following reason's:\n\n" + reason + "\n\nYou can submit another response for restaurant registration")
                                .setPositiveButton("Re-Submit", (dialogInterface, i) -> {
                                    startActivity(new Intent(HomeScreen.this, ReUploadDocumentsAgain.class));
                                    dialogInterface.dismiss();
                                }).setNegativeButton("Skip", (dialogInterface, i) -> dialogInterface.dismiss()).create();

                        alert.show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            DatabaseReference checkIfCommissionNeeded = FirebaseDatabase.getInstance().getReference().getRoot().child("Admin").child(Objects.requireNonNull(UID));
            checkIfCommissionNeeded.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.hasChild("lastCommissionPaid")){
                            long lastPaidDate = Long.parseLong(Objects.requireNonNull(dataSnapshot.child("lastCommissionPaid").getValue(String.class)));
                        if(currentTime - lastPaidDate >= 2678400000L){
                            SharedPreferences sharedPreferences = getSharedPreferences("CashCommission",MODE_PRIVATE);
                             SharedPreferences.Editor editor = sharedPreferences.edit();
                             editor.putString("fine","10");
                             editor.apply();
                            AlertDialog.Builder alert = new AlertDialog.Builder(HomeScreen.this);
                            alert.setTitle("Cash Transaction").setMessage("It's time for payment of cash transaction commission, since you have exceeded time limit fine of 10% will be applied\nDo you wanna pay now or you can pay later!")
                                    .setPositiveButton("Pay Now", (dialogInterface, i) -> {
                                        dialogInterface.dismiss();
                                        startActivity(new Intent(HomeScreen.this, CashTransactionCommissionActivity.class));
                                    }).setNegativeButton("Later", (dialogInterface, i) -> dialogInterface.dismiss()).create();
                            alert.setCancelable(false);
                            alert.show();
                        } else if(currentTime - lastPaidDate >= 2592000000L){

                            AlertDialog.Builder alert = new AlertDialog.Builder(HomeScreen.this);
                            alert.setTitle("Cash Transaction").setMessage("It's time for payment of cash transaction commission, today is last day for else fine of 10% will be applied\nDo you wanna pay now or you can pay later!")
                                    .setPositiveButton("Pay Now", (dialogInterface, i) -> {
                                        dialogInterface.dismiss();
                                        startActivity(new Intent(HomeScreen.this, CashTransactionCommissionActivity.class));
                                    }).setNegativeButton("Later", (dialogInterface, i) -> dialogInterface.dismiss()).create();
                            alert.setCancelable(false);
                            alert.show();
                        }
                        else if(currentTime - lastPaidDate >= 2505600000L){

                            AlertDialog.Builder alert = new AlertDialog.Builder(HomeScreen.this);
                            alert.setTitle("Cash Transaction").setMessage("It's time for payment of cash transaction commission, tomorrow is last day after that fine will be applied\nDo you wanna pay now or you can pay later!")
                                    .setPositiveButton("Pay Now", (dialogInterface, i) -> {
                                        dialogInterface.dismiss();
                                        startActivity(new Intent(HomeScreen.this, CashTransactionCommissionActivity.class));
                                    }).setNegativeButton("Later", (dialogInterface, i) -> dialogInterface.dismiss()).create();
                            alert.setCancelable(false);
                            alert.show();
                        }
                        else if(currentTime - lastPaidDate >= 2419200000L){

                            AlertDialog.Builder alert = new AlertDialog.Builder(HomeScreen.this);
                            alert.setTitle("Cash Transaction").setMessage("It's time for payment of cash transaction commission\nDo you wanna pay now or you can pay later!")
                                    .setPositiveButton("Pay Now", (dialogInterface, i) -> {
                                        dialogInterface.dismiss();
                                        startActivity(new Intent(HomeScreen.this, CashTransactionCommissionActivity.class));
                                    }).setNegativeButton("Later", (dialogInterface, i) -> dialogInterface.dismiss()).create();
                            alert.setCancelable(false);
                            alert.show();
                        }
                    }else{
                        if(dataSnapshot.hasChild("registrationDate")){
                            long registerTime = Long.parseLong(Objects.requireNonNull(dataSnapshot.child("registrationDate").getValue(String.class)));

                            if(currentTime - registerTime >= 2678400000L){
                                SharedPreferences sharedPreferences = getSharedPreferences("CashCommission",MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("fine","10");
                                editor.apply();
                                AlertDialog.Builder alert = new AlertDialog.Builder(HomeScreen.this);
                                alert.setTitle("Cash Transaction").setMessage("It's time for payment of cash transaction commission, since you have exceeded time limit fine of 10% will be applied\nDo you wanna pay now or you can pay later!")
                                        .setPositiveButton("Pay Now", (dialogInterface, i) -> {
                                            dialogInterface.dismiss();
                                            startActivity(new Intent(HomeScreen.this, CashTransactionCommissionActivity.class));
                                        }).setNegativeButton("Later", (dialogInterface, i) -> dialogInterface.dismiss()).create();
                                alert.setCancelable(false);
                                alert.show();
                            }
                           else if(currentTime - registerTime >= 2592000000L){

                                AlertDialog.Builder alert = new AlertDialog.Builder(HomeScreen.this);
                                alert.setTitle("Cash Transaction").setMessage("It's time for payment of cash transaction commission, today is last day for else fine of 10% will be applied\nDo you wanna pay now or you can pay later!")
                                        .setPositiveButton("Pay Now", (dialogInterface, i) -> {
                                            dialogInterface.dismiss();
                                            startActivity(new Intent(HomeScreen.this, CashTransactionCommissionActivity.class));
                                        }).setNegativeButton("Later", (dialogInterface, i) -> dialogInterface.dismiss()).create();
                                alert.setCancelable(false);
                                alert.show();
                            }
                            else if(currentTime - registerTime >= 2505600000L){

                                AlertDialog.Builder alert = new AlertDialog.Builder(HomeScreen.this);
                                alert.setTitle("Cash Transaction").setMessage("It's time for payment of cash transaction commission, tomorrow is last day after that fine will be applied\nDo you wanna pay now or you can pay later!")
                                        .setPositiveButton("Pay Now", (dialogInterface, i) -> {
                                            dialogInterface.dismiss();
                                            startActivity(new Intent(HomeScreen.this, CashTransactionCommissionActivity.class));
                                        }).setNegativeButton("Later", (dialogInterface, i) -> dialogInterface.dismiss()).create();
                                alert.setCancelable(false);
                                alert.show();
                            }
                            else if(currentTime - registerTime >= 2419200000L){

                                AlertDialog.Builder alert = new AlertDialog.Builder(HomeScreen.this);
                                alert.setTitle("Cash Transaction").setMessage("It's time for payment of cash transaction commission\nDo you wanna pay now or you can pay later!")
                                        .setPositiveButton("Pay Now", (dialogInterface, i) -> {
                                            dialogInterface.dismiss();
                                            startActivity(new Intent(HomeScreen.this, CashTransactionCommissionActivity.class));
                                        }).setNegativeButton("Later", (dialogInterface, i) -> dialogInterface.dismiss()).create();
                                alert.setCancelable(false);
                                alert.show();
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            return null;
        }
    }
}