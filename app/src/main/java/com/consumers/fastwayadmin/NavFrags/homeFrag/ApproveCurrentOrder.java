package com.consumers.fastwayadmin.NavFrags.homeFrag;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.aspose.cells.SaveFormat;
import com.aspose.cells.Workbook;
import com.consumers.fastwayadmin.CancelClass;
import com.consumers.fastwayadmin.NavFrags.CurrentTakeAwayOrders.ApproveCurrentTakeAway;
import com.consumers.fastwayadmin.PaymentClass;
import com.consumers.fastwayadmin.R;
import com.google.common.reflect.TypeToken;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

public class ApproveCurrentOrder extends AppCompatActivity {
    List<String> dishNamePdf;
    List<String> type = new ArrayList<>();
    Gson gson;
    String json;
    SharedPreferences storeOrdersForAdminInfo;
    SharedPreferences.Editor storeEditor;
    String[] monthName = {"January", "February",
            "March", "April", "May", "June", "July",
            "August", "September", "October", "November",
            "December"};
    String transactionIdForExcel;
    DatabaseReference databaseReference;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    ListView listView,halfOrList;
    String genratedToken;
    FirebaseStorage storage;
    StorageReference storageReference;
    DatabaseReference totalOrders;
    String customisation = "";
    String userName,userEmail;
    SharedPreferences restaurantDailyTrack;
    SharedPreferences.Editor restaurantTrackEditor;
    SharedPreferences restaurantTrackRecords;
    SharedPreferences.Editor restaurantTrackRecordsEditor;
    Bitmap bmp,scaled,bmp1,scaled1;
    String URL = "https://fcm.googleapis.com/fcm/send";
    String url = "https://intercellular-stabi.000webhostapp.com/refunds/initiateRefund.php";
    String testPayoutToken = "https://intercellular-stabi.000webhostapp.com/CheckoutPayouts/testToken.php";
    String testBearerToken = "https://intercellular-stabi.000webhostapp.com/CheckoutPayouts/test/testBearerToken.php";
    String testPaymentToVendor = "https://intercellular-stabi.000webhostapp.com/CheckoutPayouts/test/testPayment.php";
    ProgressBar progressBar;
    DatabaseReference saveRefundInfo;
    MakePaymentToVendor makePaymentToVendor = new MakePaymentToVendor();
    String orderID,orderAmount;
    String table;
    File path;
    Workbook workbook;
    TextView textView;
    int totalPrice = 0;
    String time;
    String state;
    String id;
    ListView dishQ;
    List<String> dishNames = new ArrayList<>();
    List<String> dishQuantity = new ArrayList<>();
    Calendar calendar = Calendar.getInstance();
    List<String> dishHalfOr = new ArrayList<>();
    Button approve,decline,showCustomisation;
    SharedPreferences storeForDishAnalysis;
    SharedPreferences.Editor dishAnalysis;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approve_current_order);
        table = getIntent().getStringExtra("table");
        id = getIntent().getStringExtra("id");
        state = getIntent().getStringExtra("state");
        restaurantDailyTrack = getSharedPreferences("RestaurantTrackingDaily", Context.MODE_PRIVATE);
        restaurantTrackRecords = getSharedPreferences("RestaurantTrackRecords",Context.MODE_PRIVATE);
        storeForDishAnalysis = getSharedPreferences("DishAnalysis",MODE_PRIVATE);
        dishAnalysis = storeForDishAnalysis.edit();
        restaurantTrackRecordsEditor = restaurantTrackRecords.edit();
        restaurantTrackEditor = restaurantDailyTrack.edit();
        StrictMode.VmPolicy.Builder builders = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builders.build());
        storeOrdersForAdminInfo = getSharedPreferences("StoreOrders",MODE_PRIVATE);
        storeEditor = storeOrdersForAdminInfo.edit();
        showCustomisation = findViewById(R.id.showCustomisationButton);
        SharedPreferences sharedPreferences = getSharedPreferences("loginInfo",MODE_PRIVATE);
        bmp = BitmapFactory.decodeResource(getResources(), R.drawable.logo);
        scaled = Bitmap.createScaledBitmap(bmp,500,500,false);
        bmp1 = BitmapFactory.decodeResource(getResources(), R.drawable.orderdeclined);
        scaled1 = Bitmap.createScaledBitmap(bmp1,500,500,false);
        initialise();
        textView.setText("Table Number: " + table);
        path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        saveRefundInfo = FirebaseDatabase.getInstance().getReference().getRoot().child("Users").child(id);
        totalOrders = FirebaseDatabase.getInstance().getReference().child("Restaurants").child(sharedPreferences.getString("state","")).child(Objects.requireNonNull(auth.getUid()));
        saveRefundInfo.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userName = String.valueOf(snapshot.child("name").getValue());
                userEmail = String.valueOf(snapshot.child("email").getValue());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        halfOrList = findViewById(R.id.halfOrFullCurrentORder);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    dishNames.add(dataSnapshot.getKey());
                    dishQuantity.add(String.valueOf(dataSnapshot.child("timesOrdered").getValue()));
                    dishHalfOr.add(String.valueOf(dataSnapshot.child("halfOr").getValue()));
                    orderID = String.valueOf(dataSnapshot.child("orderID").getValue());
                    orderAmount = String.valueOf(dataSnapshot.child("orderAmount").getValue());
                    time = String.valueOf(dataSnapshot.child("time").getValue());
                    totalPrice = totalPrice + Integer.parseInt(String.valueOf(dataSnapshot.child("price").getValue()));
                    customisation = String.valueOf(dataSnapshot.child("customisation").getValue());
                    type.add(String.valueOf(dataSnapshot.child("type").getValue()));
                }
                progressBar.setVisibility(View.INVISIBLE);
                uploadToArrayAdapter(dishNames,dishQuantity,dishHalfOr);

                dishNamePdf = new ArrayList<>(dishNames);


                if(!customisation.equals("")){
                    AlertDialog.Builder alert = new AlertDialog.Builder(ApproveCurrentOrder.this);
                    alert.setTitle("Customisation").setMessage("User has added following customisation to his/her order made\n\n\n" + customisation).setPositiveButton("Exit", (dialogInterface, i) -> {
                        dialogInterface.dismiss();
                        showCustomisation.setVisibility(View.VISIBLE);
                    }).create();

                    alert.show();
                }

                if(System.currentTimeMillis() - Long.parseLong(time) >= 600000){
                    AlertDialog.Builder builder = new AlertDialog.Builder(ApproveCurrentOrder.this);
                    RequestQueue requestQueue = Volley.newRequestQueue(ApproveCurrentOrder.this);
                    JSONObject main = new JSONObject();
                    new GenratePDF().execute();
                    FirebaseAuth auth = FirebaseAuth.getInstance();
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(state).child(Objects.requireNonNull(auth.getUid())).child("Tables").child(table);
                    new InitiateRefund().execute();
                    try {
                        main.put("to", "/topics/" + id + "");
                        JSONObject notification = new JSONObject();
                        notification.put("title", "Order Cancelled");
                        notification.put("click_action", "Table Frag");
                        notification.put("body", "Your order is cancelled because it was neither approved not denied. Refund will be initiated Shortly");
                        main.put("notification", notification);

                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, main, response -> {

                        }, error -> Toast.makeText(ApproveCurrentOrder.this, error.getLocalizedMessage() + "null", Toast.LENGTH_SHORT).show()) {
                            @Override
                            public Map<String, String> getHeaders() {
                                Map<String, String> header = new HashMap<>();
                                header.put("content-type", "application/json");
                                header.put("authorization", "key=AAAAsigSEMs:APA91bEUF9ZFwIu84Jctci56DQd0TQOepztGOIKIBhoqf7N3ueQrkClw0xBTlWZEWyvwprXZmZgW2MNywF1pNBFpq1jFBr0CmlrJ0wygbZIBOnoZ0jP1zZC6nPxqF2MAP6iF3wuBHD2R");
                                return header;
                            }
                        };
                        reference.child("Current Order").removeValue();
                        requestQueue.add(jsonObjectRequest);
                    } catch (Exception e) {
                        Toast.makeText(ApproveCurrentOrder.this, e.getLocalizedMessage() + "null", Toast.LENGTH_SHORT).show();
                    }
                    builder.setTitle("Cancelled").setMessage("Order is cancelled automatically due to inactivity")
                            .setPositiveButton("Exit", (dialogInterface, i) -> new Handler().postDelayed(() -> finish(),300)).create();
                    builder.setCancelable(false);
                    builder.show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        showCustomisation.setOnClickListener(click -> {
            AlertDialog.Builder alert = new AlertDialog.Builder(ApproveCurrentOrder.this);
            alert.setTitle("Customisation").setMessage("User has added following customisation to his/her order made\n\n\n" + customisation).setPositiveButton("Exit", (dialogInterface, i) -> dialogInterface.dismiss()).create();

            alert.show();
        });

        approve.setOnClickListener(v -> {

                String month = monthName[calendar.get(Calendar.MONTH)];
                if(storeForDishAnalysis.contains("DishAnalysisMonthBasis")){
                    gson = new Gson();
                    Type type = new TypeToken<HashMap<String,HashMap<String,String>>>(){}.getType();
                    String storedHash = storeForDishAnalysis.getString("DishAnalysisMonthBasis","");
                    HashMap<String,HashMap<String,String>> myMap = gson.fromJson(storedHash,type);
                    if(myMap.containsKey(month)){
                        HashMap<String,String> map = new HashMap<>(myMap.get(month));
                        Log.i("checking",map.toString());
                        for(int k=0;k<dishNames.size();k++){
                            if(map.containsKey(dishNames.get(k))){
                                int val = Integer.parseInt(map.get(dishNames.get(k)));
                                val++;
                                map.put(dishNames.get(k),String.valueOf(val));
                            }else{
                                map.put(dishNames.get(k),"1");
                            }
                        }
                        myMap.put(month,map);
                        dishAnalysis.putString("DishAnalysisMonthBasis",gson.toJson(myMap));
                        dishAnalysis.apply();
                    }else{
                        HashMap<String,String> map = new HashMap<>();
                        for(int i=0;i<dishNames.size();i++){
                            map.put(dishNames.get(i),"1");
                        }
                        myMap.put(month,map);
                        dishAnalysis.putString("DishAnalysisMonthBasis",gson.toJson(myMap));
                        dishAnalysis.apply();
                    }
                }else{
                    HashMap<String,HashMap<String,String>> map = new HashMap<>();
                    HashMap<String,String> myMap = new HashMap<>();
                    for(int j=0;j<dishNames.size();j++){
                        myMap.put(dishNames.get(j),"1");
                    }
                    map.put(month,myMap);
                    gson = new Gson();
                    dishAnalysis.putString("DishAnalysisMonthBasis",gson.toJson(map));
                    dishAnalysis.apply();
                }


            RequestQueue requestQueue = Volley.newRequestQueue(ApproveCurrentOrder.this);
            JSONObject main = new JSONObject();
            FirebaseAuth auth = FirebaseAuth.getInstance();
            totalOrders.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.hasChild("totalOrdersMade")){
                        int totalOrder = Integer.parseInt(String.valueOf(snapshot.child("totalOrdersMade").getValue()));
                        totalOrder = totalOrder + 1;
                        totalOrders.child("totalOrdersMade").setValue(totalOrder);
                    }else{
                        totalOrders.child("totalOrdersMade").setValue("1");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });



            if(restaurantDailyTrack.contains("totalOrdersToday")){
                int val = Integer.parseInt(restaurantDailyTrack.getString("totalOrdersToday",""));
                val = val + 1;
                restaurantTrackEditor.putString("totalOrdersToday",String.valueOf(val));
            }else{
                restaurantTrackEditor.putString("totalOrdersToday",String.valueOf(1));
            }
            if(restaurantDailyTrack.contains("totalTransactionsToday")){
                int val = Integer.parseInt(restaurantDailyTrack.getString("totalTransactionsToday",""));
                val = val + Integer.parseInt(orderAmount);
                restaurantTrackEditor.putString("totalTransactionsToday",String.valueOf(val));
            }else{
                restaurantTrackEditor.putString("totalTransactionsToday",String.valueOf(orderAmount));
            }
            restaurantTrackEditor.apply();
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(state).child(Objects.requireNonNull(auth.getUid())).child("Tables").child(table);
            new MakePayout().execute();
            try{
                main.put("to","/topics/"+id+"");
                JSONObject notification = new JSONObject();
                notification.put("title","Order Approved" );
                notification.put("click_action","Table Frag");
                notification.put("body","Your order is approved by the owner");
                main.put("notification",notification);

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, main, response -> {

                }, error -> Toast.makeText(ApproveCurrentOrder.this, error.getLocalizedMessage()+"null", Toast.LENGTH_SHORT).show()){
                    @Override
                    public Map<String, String> getHeaders() {
                        Map<String,String> header = new HashMap<>();
                        header.put("content-type","application/json");
                        header.put("authorization","key=AAAAsigSEMs:APA91bEUF9ZFwIu84Jctci56DQd0TQOepztGOIKIBhoqf7N3ueQrkClw0xBTlWZEWyvwprXZmZgW2MNywF1pNBFpq1jFBr0CmlrJ0wygbZIBOnoZ0jP1zZC6nPxqF2MAP6iF3wuBHD2R");
                        return header;
                    }
                };
                reference.child("Current Order").removeValue();
                requestQueue.add(jsonObjectRequest);
            }
            catch (Exception e){
                Toast.makeText(ApproveCurrentOrder.this, e.getLocalizedMessage()+"null", Toast.LENGTH_SHORT).show();
            }
            new Handler().postDelayed(() -> finish(),1500);

        });

        decline.setOnClickListener(v -> {
            AlertDialog.Builder alert  = new AlertDialog.Builder(v.getContext());
            alert.setTitle("Reason");
            alert.setMessage("Enter reason for order cancellation below");
            EditText editText = new EditText(v.getContext());
            editText.setMaxLines(200);
            editText.setHint("Enter reason here");
            LinearLayout linearLayout = new LinearLayout(v.getContext());
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            linearLayout.addView(editText);
            alert.setView(linearLayout);

            alert.setPositiveButton("submit", (dialogInterface, i) -> {
                if(!editText.getText().toString().equals("")) {
                    dialogInterface.dismiss();
                    RequestQueue requestQueue = Volley.newRequestQueue(ApproveCurrentOrder.this);
                    JSONObject main = new JSONObject();
                    new GenratePDF().execute();
                    FirebaseAuth auth = FirebaseAuth.getInstance();

                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(state).child(Objects.requireNonNull(auth.getUid())).child("Tables").child(table);
                    new InitiateRefund().execute();
                    try {
                        main.put("to", "/topics/" + id + "");
                        JSONObject notification = new JSONObject();
                        notification.put("title", "Order Declined");
                        notification.put("click_action", "Table Frag");
                        notification.put("body", "Your order is declined by the owner. Refund will be initiated Shortly\n" + editText.getText().toString());
                        main.put("notification", notification);

                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, main, response -> {

                        }, error -> Toast.makeText(ApproveCurrentOrder.this, error.getLocalizedMessage() + "null", Toast.LENGTH_SHORT).show()) {
                            @Override
                            public Map<String, String> getHeaders() {
                                Map<String, String> header = new HashMap<>();
                                header.put("content-type", "application/json");
                                header.put("authorization", "key=AAAAsigSEMs:APA91bEUF9ZFwIu84Jctci56DQd0TQOepztGOIKIBhoqf7N3ueQrkClw0xBTlWZEWyvwprXZmZgW2MNywF1pNBFpq1jFBr0CmlrJ0wygbZIBOnoZ0jP1zZC6nPxqF2MAP6iF3wuBHD2R");
                                return header;
                            }
                        };
                        reference.child("Current Order").removeValue();
                        requestQueue.add(jsonObjectRequest);
                    } catch (Exception e) {
                        Toast.makeText(ApproveCurrentOrder.this, e.getLocalizedMessage() + "null", Toast.LENGTH_SHORT).show();
                    }
                    new Handler().postDelayed(this::finish, 1500);
                }else{
                    Toast.makeText(v.getContext(), "Enter reason", Toast.LENGTH_SHORT).show();
                }
            }).setNegativeButton("back", (dialogInterface, i) -> {

            }).create();
            alert.show();

        });


    }

    private void uploadToArrayAdapter(List<String> dishNames,List<String> dishQuantity,List<String> dishHalfOr) {
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, dishNames);
        listView.setAdapter(arrayAdapter);

        ArrayAdapter<String> arrayAdapter1 = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, dishQuantity);
        dishQ.setAdapter(arrayAdapter1);

        ArrayAdapter<String> arrayAdapter2 = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, dishHalfOr);
        halfOrList.setAdapter(arrayAdapter2);
    }

    private void initialise() {
        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(state).child(Objects.requireNonNull(auth.getUid())).child("Tables").child(table).child("Current Order");
        listView = findViewById(R.id.currentOrderListView);
        approve = findViewById(R.id.approveCurrentOrderButton);
        dishQ = findViewById(R.id.quantityCurrentOrder);
        decline = findViewById(R.id.declineCurrentOrderButton);
        progressBar = findViewById(R.id.currentOrderProgressBar);
        textView = findViewById(R.id.tabeNumApproveCurrentOrder);
    }
    public class InitiateRefund extends AsyncTask<Void,Void,Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            RequestQueue requestQueue = Volley.newRequestQueue(ApproveCurrentOrder.this);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, response -> Log.i("res", response), error -> {

            }){
                @NonNull
                @Override
                protected Map<String, String> getParams() {
                    Map<String,String> params = new HashMap<>();
                    FirebaseAuth auth = FirebaseAuth.getInstance();
                    String referid = id;
                    Random random = new Random();
                    referid = referid + (random.nextInt(1000 - 1) + 1);
                    String finalReferIDForInfo = "refund_" + referid + "s";
                    Log.i("refundID",referid);
                    String time = String.valueOf(System.currentTimeMillis());
                    CancelClass cancelClass = new CancelClass(finalReferIDForInfo,orderAmount + "",orderID + "");
                    saveRefundInfo.child("Refunds").child(time).setValue(cancelClass);
                    params.put("referID",referid + "");
                    params.put("refundAmount",orderAmount);
                    params.put("orderID",orderID);
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
    public class MakePayout extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            RequestQueue requestQueue = Volley.newRequestQueue(ApproveCurrentOrder.this);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, testPayoutToken, response -> {
                Log.i("response",response);
                genratedToken = response.trim();
                new AuthorizeToken().execute();
            }, error -> {

            });
            requestQueue.add(stringRequest);
            return null;
        }
    }

    public class AuthorizeToken extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            RequestQueue requestQueue = Volley.newRequestQueue(ApproveCurrentOrder.this);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, testBearerToken, response -> {
                Log.i("response",response);
                if(response.trim().equals("Token is valid")){

                  makePaymentToVendor.execute();
                }
            }, error -> {

            }){
                @NonNull
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

    public class MakePaymentToVendor extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            Log.i("statusTwo", String.valueOf(makePaymentToVendor.getStatus()));
            RequestQueue requestQueue = Volley.newRequestQueue(ApproveCurrentOrder.this);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, testPaymentToVendor, response -> {
                Log.i("response",response);
                String month = monthName[calendar.get(Calendar.MONTH)];
                if(storeOrdersForAdminInfo.contains(month)){
                    Type type = new TypeToken<List<List<String>>>() {
                    }.getType();
                    gson = new Gson();
                    json = storeOrdersForAdminInfo.getString(month,"");
                    List<List<String>> mainDataList = gson.fromJson(json, type);
                    List<String> date = new ArrayList<>(mainDataList.get(0));
                    List<String> transID = new ArrayList<>(mainDataList.get(1));
                    List<String> userID = new ArrayList<>(mainDataList.get(2));
                    List<String> orderAmountList = new ArrayList<>(mainDataList.get(3));

                    date.add(time);
                    transID.add(transactionIdForExcel);
                    userID.add(id);
                    orderAmountList.add(orderAmount + "");

                    List<List<String>> storeNewList = new ArrayList<>();
                    storeNewList.add(date);
                    storeNewList.add(transID);
                    storeNewList.add(userID);
                    storeNewList.add(orderAmountList);

                    json = gson.toJson(storeNewList);
                    storeEditor.putString( month,json);
                    storeEditor.apply();
                    Log.i("myInfo",storeNewList.toString());
                }else{
                    List<List<String>> mainDataList = new ArrayList<>();
                    List<String> date = new ArrayList<>();
                    List<String> transID = new ArrayList<>();
                    List<String> userID = new ArrayList<>();
                    List<String> orderAmountList = new ArrayList<>();

                    date.add(time);
                    transID.add(transactionIdForExcel);
                    userID.add(id);
                    orderAmountList.add(orderAmount + "");
                    mainDataList.add(date);
                    mainDataList.add(transID);
                    mainDataList.add(userID);
                    mainDataList.add(orderAmountList);

                    gson = new Gson();
                    json = gson.toJson(mainDataList);
                    storeEditor.putString( month,json);
                    storeEditor.apply();
                    Log.i("myInfo",mainDataList.toString());
                }
                try {
                    workbook = new Workbook(path + "/ResTransactions.xlsx");
                    int max = workbook.getWorksheets().get(0).getCells().getMaxDataRow();
                    max = max + 2;
                    DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    Date date = new Date(Long.parseLong(time));
                    workbook.getWorksheets().get(0).getCells().get("A" + max).putValue("" + dateFormat.format(date));
                    workbook.getWorksheets().get(0).getCells().get("B" + max).putValue("" + transactionIdForExcel);
                    workbook.getWorksheets().get(0).getCells().get("C" + max).putValue("" + id);
                    workbook.getWorksheets().get(0).getCells().get("D" + max).putValue("\u20B9" + orderAmount);
                    try {
                        workbook.save(path + "/ResTransactions.xlsx", SaveFormat.XLSX);
                        Log.i("info","FILE SAVED");
//                            Toast.makeText(ApproveCurrentOrder.this, "File saved successfully", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Log.i("info",e.getLocalizedMessage());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.i("statusOne", String.valueOf(makePaymentToVendor.getStatus()));
            }, error -> {

            }){
                @NonNull
                @Override
                protected Map<String, String> getParams() {
                    Map<String,String> params = new HashMap<>();
                    params.put("benID",auth.getUid());
                    String genratedID = ApproveCurrentOrder.RandomString
                            .getAlphaNumericString(8);
                    genratedID = genratedID + System.currentTimeMillis();
                    transactionIdForExcel = genratedID;
                    params.put("transID",genratedID);
                    params.put("token",genratedToken);
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Admin").child(Objects.requireNonNull(auth.getUid())).child("Transactions");
                    PaymentClass paymentClass = new PaymentClass(genratedID,id);
                    databaseReference.child(time).setValue(paymentClass);
                    params.put("amount", "1");
                    return params;
                }
            };
            requestQueue.add(stringRequest);
            return null;
        }
    }
    public static class RandomString {

        // function to generate a random string of length n
        static String getAlphaNumericString(int n) {

            // chose a Character random from this String
            String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                    + "0123456789"
                    + "abcdefghijklmnopqrstuvxyz";

            // create StringBuffer size of AlphaNumericString
            StringBuilder sb = new StringBuilder(n);

            for (int i = 0; i < n; i++) {

                // generate a random number between
                // 0 to AlphaNumericString variable length
                int index
                        = (int) (AlphaNumericString.length()
                        * Math.random());

                // add Character one by one in end of sb
                sb.append(AlphaNumericString
                        .charAt(index));
            }

            return sb.toString();
        }
    }
    private class GenratePDF extends AsyncTask<Void,Void,Void>{

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            PdfDocument pdfDocument = new PdfDocument();
            Paint myPaint = new Paint();
            PdfDocument.PageInfo myPage = new  PdfDocument.PageInfo.Builder(2080,2040,1).create();
            PdfDocument.Page page = pdfDocument.startPage(myPage);

            Paint text = new Paint();
            SharedPreferences sharedPreferences = getSharedPreferences("RestaurantInfo",MODE_PRIVATE);
            Canvas canvas = page.getCanvas();

            canvas.drawBitmap(scaled,735,0,myPaint);

            text.setTextAlign(Paint.Align.LEFT);
            text.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.BOLD));
            text.setTextSize(70);
            canvas.drawText("Restaurant Details",45,470,text);
            text.setTextSize(50);
            canvas.drawText(sharedPreferences.getString("hotelName",""),45,550,text);
            canvas.drawText(sharedPreferences.getString("hotelAddress",""),45,620,text);
            canvas.drawText(sharedPreferences.getString("hotelNumber",""),45,700,text);

            text.setTextSize(70);
//            SharedPreferences sharedPreferences = getSharedPreferences("AccountInfo",MODE_PRIVATE);
            canvas.drawText("Customer Details",1440,470,text);
            text.setTextSize(50);
            canvas.drawText("" + userName,1440,550,text);
            canvas.drawText("" + userEmail,1440,620,text);

            text.setTextAlign(Paint.Align.LEFT);
            text.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.BOLD));
            text.setColor(Color.BLUE);
            text.setTextSize(50);

            canvas.drawText("Date: " + new SimpleDateFormat("yyyy/MM/dd").format(new Date()),45,780,text);
            canvas.drawText("Invoice Number: " + orderID,45,860,text);

            text.setStyle(Paint.Style.STROKE);
            text.setStrokeWidth(3);
            text.setColor(Color.BLACK);
            canvas.drawRect(35,940,1080-20,1020,text);
            text.setStyle(Paint.Style.FILL);
            text.setTextSize(40);
            canvas.drawText("Description",50,990,text);
            canvas.drawText("Amount",905,990,text);

            canvas.drawLine(855,940,855,1020,text);

            canvas.drawText("Ordered Food",50,1080,text);
            text.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.NORMAL));
            canvas.drawText("(Incl. GST)",50,1120,text);
            canvas.drawText("\u20B9"+orderAmount,902,1080,text);

//            if(isCouponApplied){
//                canvas.drawText("Discount Applied (-)",35,1150,text);
//                text.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.NORMAL));
//                canvas.drawText("\u20B9"+discount,899,1150,text);
//            }
            text.setTextAlign(Paint.Align.LEFT);
            text.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.BOLD));
            text.setTextSize(55);
            canvas.drawText("Contact Fastway",50,1470,text);
            text.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.NORMAL));
            text.setTextSize(45);
            canvas.drawText("Contact Number:  +918076531395",50,1580,text);
            text.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.NORMAL));

            canvas.drawText("Email ID:  fastway8587@gmail.com",50,1640,text);
            canvas.drawBitmap(scaled1,900,1470,myPaint);

            pdfDocument.finishPage(page);

            String fileName = "/invoice" + time + ".pdf";
            File file = new File(Environment.getExternalStorageDirectory() + fileName);

            try{
                pdfDocument.writeTo(new FileOutputStream(file));
            } catch (IOException e) {
                e.printStackTrace();
            }
//            FastDialog fastDialog = new FastDialogBuilder(ApproveCurrentTakeAway.this, Type.PROGRESS)
//                    .progressText("Uploading invoice....")
//                    .cancelable(false)
//                    .setAnimation(Animations.FADE_IN)
//                    .create();
            storage = FirebaseStorage.getInstance();
            storageReference = storage.getReference();
            FirebaseAuth auth = FirebaseAuth.getInstance();
//            fastDialog.show();
            try {
                StorageReference reference = storageReference.child(id + "/" + "invoice" + "/"  + fileName);
                reference.putFile(Uri.fromFile(file)).addOnCompleteListener(task -> {
                    Toast.makeText(ApproveCurrentOrder.this, "Order cancelled successfully", Toast.LENGTH_SHORT).show();
                    String newString = fileName.replace("/","");
                    deleteFile(newString);
                    file.delete();
                }).addOnFailureListener(e -> {
                });
            }catch (Exception e){
                Toast.makeText(ApproveCurrentOrder.this, ""+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
            pdfDocument.close();
        }
        @Override
        protected Void doInBackground(Void... voids) {

            return null;
        }
    }
}