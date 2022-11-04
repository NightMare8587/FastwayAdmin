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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.consumers.fastwayadmin.CancelClass;
import com.consumers.fastwayadmin.NavFrags.CurrentTakeAwayOrders.ApproveCurrentTakeAway;
import com.consumers.fastwayadmin.NavFrags.CurrentTakeAwayOrders.MyClass;
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

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

public class ApproveCurrentOrder extends AppCompatActivity {
    List<String> dishNamePdf;
    List<String> type = new ArrayList<>();
    Gson gson;
    String json;
    SharedPreferences trackingOfTakeAway;
    SharedPreferences.Editor user7daysEdit;
    SharedPreferences dailyUserTrackingFor7days;
    SharedPreferences dailyAverageOrder;
    SharedPreferences.Editor averageEditor;
    SharedPreferences.Editor trackingDineAndWay;
    int veg = 0;
    int nonVeg = 0;
    int vegan = 0;
    double amountToBeSend;
    DatabaseReference storeTotalAmountMonth;
    SharedPreferences userFrequency;
    SharedPreferences storeOrdersForAdminInfo;
    SharedPreferences storeDailyTotalOrdersMade;
    String paymentType;
    SharedPreferences.Editor storeDailyEditor;
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
    String prodPayoutToken = "https://intercellular-stabi.000webhostapp.com/CheckoutPayouts/payoutIMPS.php";
    String testBearerToken = "https://intercellular-stabi.000webhostapp.com/CheckoutPayouts/test/testBearerToken.php";
    String prodBearerToken = "https://intercellular-stabi.000webhostapp.com/CheckoutPayouts/authBEarerToken.php";
    String testPaymentToVendor = "https://intercellular-stabi.000webhostapp.com/CheckoutPayouts/test/testPayment.php";
    String prodPaymentToVendor = "https://intercellular-stabi.000webhostapp.com/CheckoutPayouts/PaymentToVendor.php";
    ProgressBar progressBar;
    DatabaseReference saveRefundInfo;
    MakePaymentToVendor makePaymentToVendor = new MakePaymentToVendor();
    String orderID,orderAmount;
    String table;
    File path;
//    Workbook workbook;
    TextView textView;
    int totalPrice = 0;
    String time;
    String state;
    String id;
    ListView dishQ;
    List<String> dishNames = new ArrayList<>();
    List<String> dishQuantity = new ArrayList<>();
    List<String> image = new ArrayList<>();
    List<String> dishPrices = new ArrayList<>();
    List<String> orderAndPayment = new ArrayList<>();
    Calendar calendar = Calendar.getInstance();
    List<String> dishHalfOr = new ArrayList<>();
    Button approve,decline,showCustomisation;
    SharedPreferences.Editor userFedit;
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
        storeDailyTotalOrdersMade = getSharedPreferences("RestaurantDailyStoreForAnalysis",MODE_PRIVATE);
        storeDailyEditor = storeDailyTotalOrdersMade.edit();
        storeForDishAnalysis = getSharedPreferences("DishAnalysis",MODE_PRIVATE);
        dishAnalysis = storeForDishAnalysis.edit();
        userFrequency = getSharedPreferences("UsersFrequencyPerMonth",MODE_PRIVATE);
        userFedit = userFrequency.edit();
        trackingOfTakeAway = getSharedPreferences("TrackingOfFoodDining",MODE_PRIVATE);
        trackingDineAndWay = trackingOfTakeAway.edit();
        restaurantTrackRecordsEditor = restaurantTrackRecords.edit();
        restaurantTrackEditor = restaurantDailyTrack.edit();
        StrictMode.VmPolicy.Builder builders = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builders.build());
        storeOrdersForAdminInfo = getSharedPreferences("StoreOrders",MODE_PRIVATE);
        dailyAverageOrder = getSharedPreferences("DailyAverageOrderMonthly",MODE_PRIVATE);
        dailyUserTrackingFor7days = getSharedPreferences("DailyUserTrackingFor7days",MODE_PRIVATE);
        user7daysEdit = dailyUserTrackingFor7days.edit();
        averageEditor = dailyAverageOrder.edit();
        storeEditor = storeOrdersForAdminInfo.edit();
        showCustomisation = findViewById(R.id.showCustomisationButton);
        storeTotalAmountMonth = FirebaseDatabase.getInstance().getReference().getRoot().child("Admin").child(auth.getUid());
        SharedPreferences sharedPreferences = getSharedPreferences("loginInfo",MODE_PRIVATE);
        bmp = BitmapFactory.decodeResource(getResources(), R.drawable.logo);
        scaled = Bitmap.createScaledBitmap(bmp,500,500,false);
        bmp1 = BitmapFactory.decodeResource(getResources(), R.drawable.orderdeclined);
        scaled1 = Bitmap.createScaledBitmap(bmp1,500,500,false);
        initialise();
        textView.setText("Table Number: " + table);
        path = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        saveRefundInfo = FirebaseDatabase.getInstance().getReference().getRoot().child("Users").child(id);
        totalOrders = FirebaseDatabase.getInstance().getReference().child("Restaurants").child(sharedPreferences.getString("state","")).child(sharedPreferences.getString("locality","")).child(Objects.requireNonNull(auth.getUid()));
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
            @SuppressLint("SuspiciousIndentation")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    dishNames.add(dataSnapshot.getKey());
                    dishQuantity.add(String.valueOf(dataSnapshot.child("timesOrdered").getValue()));
                    dishHalfOr.add(String.valueOf(dataSnapshot.child("halfOr").getValue()));
                    orderID = String.valueOf(dataSnapshot.child("orderID").getValue());
                    image.add(String.valueOf(dataSnapshot.child("image").getValue()));
                    dishPrices.add(String.valueOf(dataSnapshot.child("price").getValue()));
                    orderAndPayment.add(String.valueOf(dataSnapshot.child("orderAndPayment").getValue()));
                    paymentType = String.valueOf(dataSnapshot.child("orderAndPayment").getValue());
                    orderAmount = String.valueOf(dataSnapshot.child("orderAmount").getValue());
                    time = String.valueOf(dataSnapshot.child("time").getValue());
                    totalPrice = totalPrice + Integer.parseInt(String.valueOf(dataSnapshot.child("price").getValue()));
                    customisation = String.valueOf(dataSnapshot.child("customisation").getValue());
                    type.add(String.valueOf(dataSnapshot.child("type").getValue()));
                }
                String[] arr = paymentType.split(",");
                paymentType = arr[2];
                progressBar.setVisibility(View.INVISIBLE);
                uploadToArrayAdapter(dishNames,dishQuantity,dishHalfOr);

                dishNamePdf = new ArrayList<>(dishNames);


                AsyncTask.execute(() -> {
                    for(int i=0;i<type.size();i++){

                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(state).child(sharedPreferences.getString("locality","")).child(auth.getUid()).child("List of Dish");
                        databaseReference.child(type.get(i)).child(dishNames.get(i)).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot1) {

//                                if(!snapshot1.hasChild("dishType"))
//                                    return;

                                if(snapshot1.child("dishType").getValue(String.class).equals("Veg"))
                                    veg++;
                                else if(snapshot1.child("dishType").getValue(String.class).equals("NonVeg"))
                                    nonVeg++;
                                else
                                    vegan++;
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                });

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
//                    new GenratePDF().execute();
                    FirebaseAuth auth = FirebaseAuth.getInstance();
                    String approveTime = String.valueOf(System.currentTimeMillis());
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Users").child(id).child("Recent Orders").child(time);
                    for(int i=0;i<dishNames.size();i++){
                        MyClass myClass = new MyClass(dishNames.get(i),dishPrices.get(i),image.get(i),type.get(i),""+approveTime,dishQuantity.get(i),dishHalfOr.get(i),state,String.valueOf(orderAmount),orderID,orderAndPayment.get(i),"Order Declined",sharedPreferences.getString("locality",""));
                        databaseReference.child(Objects.requireNonNull(auth.getUid())).child(dishNames.get(i)).setValue(myClass);
                    }


                    databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(state).child(sharedPreferences.getString("locality","")).child(Objects.requireNonNull(auth.getUid()));
                    for(int i=0;i<dishNames.size();i++){
                        MyClass myClass = new MyClass(dishNames.get(i),dishPrices.get(i),image.get(i),type.get(i),""+approveTime,dishQuantity.get(i),dishHalfOr.get(i),state,String.valueOf(orderAmount),orderID,orderAndPayment.get(i),"Order Declined",sharedPreferences.getString("locality",""));
                        databaseReference.child("Recent Orders").child("" + time).child(auth.getUid()).child(dishNames.get(i)).setValue(myClass);
                    }
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(state).child(sharedPreferences.getString("locality","")).child(Objects.requireNonNull(auth.getUid())).child("Tables").child(table);
                    if(!paymentType.equals("endCheckOut"))
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
                                header.put("authorization", "key=AAAAjq_WsHs:APA91bGZV-uH-NJddxIniy8h1tDGDHqxhgvFdyNRDaV_raxjvSM_FkKu7JSwtSp4Q_iSmPuTKGGIB2M_07c9rKgPXUH43-RzpK6zkaSaIaNgmeiwUO40rYxYUZAkKoLAQQVeVJ7mXboD");
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
            try {
                new hugeBackgroundWork().execute();
                AsyncTask.execute(() -> {
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Complaints").child("ResAnalysis").child(state).child(sharedPreferences.getString("locality", ""));
                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                if (snapshot.hasChild("veg")) {
                                    int vegVal = Integer.parseInt(String.valueOf(snapshot.child("veg").getValue()));
                                    vegVal += veg;
                                    databaseReference.child("veg").setValue(vegVal + "");
                                } else
                                    databaseReference.child("veg").setValue(veg + "");

                                if (snapshot.hasChild("NonVeg")) {
                                    int vegVal = Integer.parseInt(String.valueOf(snapshot.child("NonVeg").getValue()));
                                    vegVal += nonVeg;
                                    databaseReference.child("NonVeg").setValue(vegVal + "");
                                } else
                                    databaseReference.child("NonVeg").setValue(nonVeg + "");

                                if (snapshot.hasChild("vegan")) {
                                    int vegVal = Integer.parseInt(String.valueOf(snapshot.child("vegan").getValue()));
                                    vegVal += vegan;
                                    databaseReference.child("vegan").setValue(vegVal + "");
                                } else
                                    databaseReference.child("vegan").setValue(vegan + "");

                                DatabaseReference checkRestaurant = FirebaseDatabase.getInstance().getReference().getRoot().child("Complaints").child("ResAnalysis").child(state).child(sharedPreferences.getString("locality", "")).child(auth.getUid());
                                checkRestaurant.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()) {
                                            if (snapshot.hasChild("veg")) {
                                                int vegVal = Integer.parseInt(String.valueOf(snapshot.child("veg").getValue()));
                                                vegVal += veg;
                                                checkRestaurant.child("veg").setValue(vegVal + "");
                                            } else
                                                checkRestaurant.child("veg").setValue(veg + "");

                                            if (snapshot.hasChild("NonVeg")) {
                                                int vegVal = Integer.parseInt(String.valueOf(snapshot.child("NonVeg").getValue()));
                                                vegVal += nonVeg;
                                                checkRestaurant.child("NonVeg").setValue(vegVal + "");
                                            } else
                                                checkRestaurant.child("NonVeg").setValue(nonVeg + "");

                                            if (snapshot.hasChild("vegan")) {
                                                int vegVal = Integer.parseInt(String.valueOf(snapshot.child("vegan").getValue()));
                                                vegVal += vegan;
                                                checkRestaurant.child("vegan").setValue(vegVal + "");
                                            } else
                                                checkRestaurant.child("vegan").setValue(vegan + "");
                                        } else {
                                            if (!(veg == 0))
                                                checkRestaurant.child("veg").setValue(veg + "");

                                            if (!(vegan == 0))
                                                checkRestaurant.child("vegan").setValue(vegan + "");

                                            if (!(nonVeg == 0))
                                                checkRestaurant.child("NonVeg").setValue(nonVeg + "");
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            } else {

                                DatabaseReference checkRestaurant = FirebaseDatabase.getInstance().getReference().getRoot().child("Complaints").child("ResAnalysis").child(state).child(sharedPreferences.getString("locality", "")).child(auth.getUid());
                                checkRestaurant.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()) {
                                            if (snapshot.hasChild("veg")) {
                                                int vegVal = Integer.parseInt(String.valueOf(snapshot.child("veg").getValue()));
                                                vegVal += veg;
                                                checkRestaurant.child("veg").setValue(vegVal + "");
                                            } else
                                                checkRestaurant.child("veg").setValue(veg + "");

                                            if (snapshot.hasChild("NonVeg")) {
                                                int vegVal = Integer.parseInt(String.valueOf(snapshot.child("NonVeg").getValue()));
                                                vegVal += nonVeg;
                                                checkRestaurant.child("NonVeg").setValue(vegVal + "");
                                            } else
                                                checkRestaurant.child("NonVeg").setValue(nonVeg + "");

                                            if (snapshot.hasChild("vegan")) {
                                                int vegVal = Integer.parseInt(String.valueOf(snapshot.child("vegan").getValue()));
                                                vegVal += vegan;
                                                checkRestaurant.child("vegan").setValue(vegVal + "");
                                            } else
                                                checkRestaurant.child("vegan").setValue(vegan + "");
                                        } else {
                                            if (!(veg == 0))
                                                checkRestaurant.child("veg").setValue(veg + "");

                                            if (!(vegan == 0))
                                                checkRestaurant.child("vegan").setValue(vegan + "");

                                            if (!(nonVeg == 0))
                                                checkRestaurant.child("NonVeg").setValue(nonVeg + "");
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                                if (!(veg == 0))
                                    databaseReference.child("veg").setValue(veg + "");

                                if (!(vegan == 0))
                                    databaseReference.child("vegan").setValue(vegan + "");

                                if (!(nonVeg == 0))
                                    databaseReference.child("NonVeg").setValue(nonVeg + "");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference().getRoot().child("Complaints").child("ResAnalysis").child(state).child(sharedPreferences.getString("locality", ""));
                    databaseReference1.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                if (snapshot.hasChild("foodDining")) {
                                    int curVal = Integer.parseInt(Objects.requireNonNull(snapshot.child("foodDining").getValue(String.class)));
                                    curVal++;
                                    databaseReference1.child("foodDining").setValue(curVal + "");
                                } else
                                    databaseReference1.child("foodDining").setValue("1");
                            } else {
                                databaseReference1.child("foodDining").setValue("1");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                });
                if (paymentType.equals("instantCheckOut")) {
                    approve.setEnabled(false);

                    new addOrderToTableCurrent().execute();

                    SharedPreferences checkPrem = getSharedPreferences("AdminPremiumDetails", MODE_PRIVATE);
                    if (checkPrem.contains("status") && checkPrem.getString("status", "").equals("active")) {
                        String month = monthName[calendar.get(Calendar.MONTH)];
                        if (storeForDishAnalysis.contains("DishAnalysisMonthBasis")) {
                            try {
                                gson = new Gson();
                                Type type = new TypeToken<HashMap<String, HashMap<String, Integer>>>() {
                                }.getType();
                                String storedHash = storeForDishAnalysis.getString("DishAnalysisMonthBasis", "");
                                HashMap<String, HashMap<String, Integer>> myMap = gson.fromJson(storedHash, type);
                                HashMap<String, Integer> map;
                                if (myMap.containsKey(month)) {
                                    map = new HashMap<>(myMap.get(month));
                                    Log.i("checking", map.toString());
                                    for (int k = 0; k < dishNames.size(); k++) {
                                        if (map.containsKey(dishNames.get(k))) {
                                            String currDishName = dishNames.get(k);
                                            int val = map.get(dishNames.get(k));
                                            val++;
                                            map.put(dishNames.get(k), val);

                                        } else {
                                            map.put(dishNames.get(k), 1);
                                        }
                                    }
                                } else {
                                    map = new HashMap<>();
                                    for (int i = 0; i < dishNames.size(); i++) {
                                        map.put(dishNames.get(i), 1);
                                    }
                                }
                                myMap.put(month, map);
                                dishAnalysis.putString("DishAnalysisMonthBasis", gson.toJson(myMap));
                            }catch (Exception e){

                                gson = new Gson();
                                Type type = new TypeToken<HashMap<String, HashMap<String, Integer>>>() {
                                }.getType();
                                String storedHash = storeForDishAnalysis.getString("DishAnalysisMonthBasis", "");
                                HashMap<String, HashMap<String, Integer>> myMap = gson.fromJson(storedHash, type);
                                HashMap<String, Integer> map;
                                if (myMap.containsKey(month)) {
                                    map = new HashMap<>(myMap.get(month));
                                    Log.i("checking", map.toString());
                                    for (int k = 0; k < dishNames.size(); k++) {
                                        if (map.containsKey(dishNames.get(k))) {
                                            int val = map.get(dishNames.get(k));
                                            val++;
                                            map.put(dishNames.get(k), val);
                                        } else {
                                            map.put(dishNames.get(k), 1);
                                        }
                                    }
                                } else {
                                    map = new HashMap<>();
                                    for (int i = 0; i < dishNames.size(); i++) {
                                        map.put(dishNames.get(i), 1);
                                    }
                                }
                                myMap.put(month, map);
                                dishAnalysis.putString("DishAnalysisMonthBasis", gson.toJson(myMap));
                            }
                        } else {
                            HashMap<String, HashMap<String, Integer>> map = new HashMap<>();
                            HashMap<String, Integer> myMap = new HashMap<>();
                            for (int j = 0; j < dishNames.size(); j++) {
                                myMap.put(dishNames.get(j), 1);
                            }
                            map.put(month, myMap);
                            gson = new Gson();
                            dishAnalysis.putString("DishAnalysisMonthBasis", gson.toJson(map));
                        }
                        dishAnalysis.apply();


                        java.lang.reflect.Type type1 = new TypeToken<HashMap<String,HashMap<String,Integer>>>(){}.getType();

                        HashMap<String,HashMap<String,Integer>> mapDishMain;
                        //
                        SharedPreferences dishShared = getSharedPreferences("DishOrderedWithOthers",MODE_PRIVATE);
                        SharedPreferences.Editor dishSharedEdit = dishShared.edit();
                        if(dishShared.contains(month)){
                            mapDishMain = gson.fromJson(dishShared.getString(month,""),type1);
                            HashMap<String,Integer> innerMap;

                            for(int m=0;m<dishNames.size();m++){
                                if(mapDishMain.containsKey(dishNames.get(m))){
                                    innerMap = new HashMap<>(mapDishMain.get(dishNames.get(m)));
                                    for(int i=0;i<dishNames.size();i++){
                                        if(innerMap.containsKey(dishNames.get(i))){
                                            int prev = innerMap.get(dishNames.get(i));
                                            prev++;
                                            innerMap.put(dishNames.get(i),prev);
                                        }else
                                            innerMap.put(dishNames.get(i),1);
                                    }
                                }else{
                                    innerMap = new HashMap<>();
                                    for(int i=0;i<dishNames.size();i++)
                                        innerMap.put(dishNames.get(i),1);
                                }


                                mapDishMain.put(dishNames.get(m),innerMap);
                            }
//                                        if(mapDishMain.containsKey(currDishName)){
//                                            innerMap = new HashMap<>(mapDishMain.get(currDishName));
//                                            for(int l=0;l<dishName.size();l++){
//                                                if(!dishName.get(l).equals(currDishName)){
//                                                    if(innerMap.containsKey(dishName.get(l))){
//                                                        int prev = innerMap.get(dishName.get(l));
//                                                        prev++;
//                                                        innerMap.put(dishName.get(l),prev);
//                                                    }else
//                                                        innerMap.put(dishName.get(l),1);
//                                                }
//                                            }
//
//                                        }else{
//                                            innerMap = new HashMap<>();
//                                            for(int l=0;l<dishName.size();l++){
//                                                if(!dishName.get(l).equals(currDishName)){
//                                                    innerMap.put(dishName.get(l),1);
//                                                }
//                                            }
//
//                                        }

                        }else{
                            mapDishMain = new HashMap<>();
                            HashMap<String,Integer> innerMap = new HashMap<>();
                            for(int l=0;l<dishNames.size();l++){
                                for(int i=0;i<dishNames.size();i++)
                                    innerMap.put(dishNames.get(i),1);

                                mapDishMain.put(dishNames.get(l),innerMap);
                            }

                        }
                        dishSharedEdit.putString(month,gson.toJson(mapDishMain));
                        dishSharedEdit.apply();

                        SharedPreferences last7daysReport = getSharedPreferences("last7daysReport",MODE_PRIVATE);
                        SharedPreferences.Editor last7daysReportEdit = last7daysReport.edit();
                        SharedPreferences lastMonthReport = getSharedPreferences("lastMonthlyReport",MODE_PRIVATE);
                        SharedPreferences.Editor editorMonthly = lastMonthReport.edit();
                        new Thread(() -> {
                            if(last7daysReport.contains("currentMonth") && last7daysReport.getString("currentMonth","").equals(month)){
                                int day = calendar.get(Calendar.DAY_OF_MONTH);
                                if(last7daysReport.contains("daysTracked")){
                                    if(Integer.parseInt(last7daysReport.getString("currentDate","")) != day) {
                                        int prevData = Integer.parseInt(last7daysReport.getString("daysTracked", ""));
                                        prevData++;
                                        last7daysReportEdit.putString("daysTracked",prevData + "");
                                    }

                                    last7daysReportEdit.putString("currentDate",day + "");
                                    last7daysReportEdit.apply();
                                }else{
                                    int prevData = 1;
                                    last7daysReportEdit.putString("currentDate",day + "");
                                    last7daysReportEdit.putString("daysTracked",prevData + "");
                                    last7daysReportEdit.apply();
                                }
                            }else{
                                int prevData = 1;
                                int day = calendar.get(Calendar.DAY_OF_MONTH);
                                last7daysReportEdit.putString("currentDate",day + "");
                                last7daysReportEdit.putString("daysTracked",prevData + "");
                                last7daysReportEdit.putString("currentMonth",month + "");
                                last7daysReportEdit.apply();
                            }

                            if(lastMonthReport.contains("currentMonth") && lastMonthReport.getString("currentMonth","").equals(month)){

                            }else{
                                editorMonthly.putString("currentMonth",month);
                                editorMonthly.apply();
                            }
                        }).start();

                        if(dailyUserTrackingFor7days.contains(month)){
                            try {
                                gson = new Gson();
                                java.lang.reflect.Type type = new TypeToken<HashMap<String, HashMap<String, Integer>>>() {
                                }.getType();
                                HashMap<String, HashMap<String, Integer>> mainMap = gson.fromJson(dailyUserTrackingFor7days.getString(month, ""), type);
                                int day = calendar.get(Calendar.DAY_OF_MONTH);
                                HashMap<String, Integer> integerHashMap;
                                if (mainMap.containsKey(day + "")) {
                                    integerHashMap = new HashMap<>(mainMap.get(day + ""));
                                    if (integerHashMap.containsKey(id)) {
                                        int prev = integerHashMap.get(id);
                                        prev++;
                                        integerHashMap.put(id, prev);
                                    } else
                                        integerHashMap.put(id, 1);

                                } else {
                                    integerHashMap = new HashMap<>();
                                    integerHashMap.put(id, 1);
                                }
                                mainMap.put(day + "", integerHashMap);
                                user7daysEdit.putString(month, gson.toJson(mainMap));
                                user7daysEdit.apply();
                            }catch (Exception e){

                                gson = new Gson();
                                java.lang.reflect.Type type = new TypeToken<HashMap<String, HashMap<String, Integer>>>() {
                                }.getType();
                                HashMap<String, HashMap<String, Integer>> mainMap = gson.fromJson(dailyUserTrackingFor7days.getString(month, ""), type);
                                int day = calendar.get(Calendar.DAY_OF_MONTH);
                                HashMap<String, Integer> integerHashMap;
                                if (mainMap.containsKey(day + "")) {
                                    integerHashMap = new HashMap<>(mainMap.get(day + ""));
                                    if (integerHashMap.containsKey(id)) {
                                        int prev = integerHashMap.get(id);
                                        prev++;
                                        integerHashMap.put(id, prev);
                                    } else
                                        integerHashMap.put(id, 1);

                                } else {
                                    integerHashMap = new HashMap<>();
                                    integerHashMap.put(id, 1);
                                }
                                mainMap.put(day + "", integerHashMap);
                                user7daysEdit.putString(month, gson.toJson(mainMap));
                                user7daysEdit.apply();
                            }
                        }else{
                            gson = new Gson();
                            int day = calendar.get(Calendar.DAY_OF_MONTH);
                            HashMap<String,HashMap<String,Integer>> mainMap = new HashMap<>();
                            HashMap<String,Integer> integerHashMap = new HashMap<>();
                            integerHashMap.put(id,1);
                            mainMap.put(day + "",integerHashMap);

                            user7daysEdit.putString(month,gson.toJson(mainMap));
                            user7daysEdit.apply();
                        }

                        if (dailyAverageOrder.contains(month)) {
                            try {
                                gson = new Gson();
                                java.lang.reflect.Type type = new TypeToken<List<List<String>>>() {
                                }.getType();
                                List<List<String>> mainList = gson.fromJson(dailyAverageOrder.getString(month, ""), type);
                                List<String> day = new ArrayList<>(mainList.get(0));
                                List<String> times = new ArrayList<>(mainList.get(1));
                                List<String> amount = new ArrayList<>(mainList.get(2));
                                int dayCal = calendar.get(Calendar.DAY_OF_MONTH);
                                if (Integer.parseInt(day.get(day.size() - 1)) == dayCal) {
                                    int timesToday = Integer.parseInt(times.get(times.size() - 1));
                                    timesToday++;
                                    double prev = Double.parseDouble(amount.get(amount.size() - 1));
                                    prev += Double.parseDouble(orderAmount);

                                    double newVal = prev / timesToday;

                                    amount.set(amount.size() - 1, String.valueOf(newVal));
                                    times.set(times.size() - 1, timesToday + "");
                                } else {
                                    day.add(dayCal + "");
                                    amount.add(orderAmount);
                                    times.add("1");
                                }

                                List<List<String>> newList = new ArrayList<>();
                                newList.add(day);
                                newList.add(times);
                                newList.add(amount);

                                averageEditor.putString(month, gson.toJson(newList));
                            }catch (Exception e){

                                gson = new Gson();
                                java.lang.reflect.Type type = new TypeToken<List<List<String>>>() {
                                }.getType();
                                List<List<String>> mainList = gson.fromJson(dailyAverageOrder.getString(month, ""), type);
                                List<String> day = new ArrayList<>(mainList.get(0));
                                List<String> times = new ArrayList<>(mainList.get(1));
                                List<String> amount = new ArrayList<>(mainList.get(2));
                                int dayCal = calendar.get(Calendar.DAY_OF_MONTH);
                                if (Integer.parseInt(day.get(day.size() - 1)) == dayCal) {
                                    int timesToday = Integer.parseInt(times.get(times.size() - 1));
                                    timesToday++;
                                    double prev = Double.parseDouble(amount.get(amount.size() - 1));
                                    prev += Double.parseDouble(orderAmount);

                                    double newVal = prev / timesToday;

                                    amount.set(amount.size() - 1, String.valueOf(newVal));
                                    times.set(times.size() - 1, timesToday + "");
                                } else {
                                    day.add(dayCal + "");
                                    amount.add(orderAmount);
                                    times.add("1");
                                }

                                List<List<String>> newList = new ArrayList<>();
                                newList.add(day);
                                newList.add(times);
                                newList.add(amount);

                                averageEditor.putString(month, gson.toJson(newList));
                            }

                        } else {
                            int dayCal = calendar.get(Calendar.DAY_OF_MONTH);
                            List<List<String>> newList = new ArrayList<>();
                            List<String> day = new ArrayList<>();
                            day.add(dayCal + "");
                            List<String> times = new ArrayList<>();
                            times.add("1");
                            List<String> amount = new ArrayList<>();
                            amount.add(orderAmount);
                            newList.add(day);
                            newList.add(times);
                            newList.add(amount);

                            averageEditor.putString(month, gson.toJson(newList));
                        }
                        averageEditor.apply();

                        if (trackingOfTakeAway.contains(month)) {
                            try {
                                java.lang.reflect.Type type = new TypeToken<HashMap<String, String>>() {
                                }.getType();
                                gson = new Gson();
                                json = trackingOfTakeAway.getString(month, "");
                                HashMap<String, String> map = gson.fromJson(json, type);
                                if (map.containsKey(calendar.get(Calendar.DAY_OF_MONTH) + "")) {
                                    int currentVal = Integer.parseInt(Objects.requireNonNull(map.get(calendar.get(Calendar.DAY_OF_MONTH) + "")));
                                    currentVal++;
                                    map.put(calendar.get(Calendar.DAY_OF_MONTH) + "", currentVal + "");
                                } else {
                                    map.put(calendar.get(Calendar.DAY_OF_MONTH) + "", "1");
                                }
                                json = gson.toJson(map);
                            }catch (Exception e){
                                java.lang.reflect.Type type = new TypeToken<HashMap<String, String>>() {
                                }.getType();
                                gson = new Gson();
                                json = trackingOfTakeAway.getString(month, "");
                                HashMap<String, String> map = gson.fromJson(json, type);
                                if (map.containsKey(calendar.get(Calendar.DAY_OF_MONTH) + "")) {
                                    int currentVal = Integer.parseInt(Objects.requireNonNull(map.get(calendar.get(Calendar.DAY_OF_MONTH) + "")));
                                    currentVal++;
                                    map.put(calendar.get(Calendar.DAY_OF_MONTH) + "", currentVal + "");
                                } else {
                                    map.put(calendar.get(Calendar.DAY_OF_MONTH) + "", "1");
                                }
                                json = gson.toJson(map);
                            }
                        } else {
                            gson = new Gson();
                            HashMap<String, String> map = new HashMap<>();
                            map.put(calendar.get(Calendar.DAY_OF_MONTH) + "", "1");
                            json = gson.toJson(map);
                        }
                        trackingDineAndWay.putString(month, json);
                        trackingDineAndWay.apply();

                    }


                    updateTotalAmount(orderAmount);
                    RequestQueue requestQueue = Volley.newRequestQueue(ApproveCurrentOrder.this);
                    JSONObject main = new JSONObject();
                    FirebaseAuth auth = FirebaseAuth.getInstance();
                    totalOrders.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.hasChild("totalOrdersMade")) {
                                int totalOrder = Integer.parseInt(String.valueOf(snapshot.child("totalOrdersMade").getValue()));
                                totalOrder = totalOrder + 1;
                                totalOrders.child("totalOrdersMade").setValue(totalOrder);
                            } else {
                                totalOrders.child("totalOrdersMade").setValue("1");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });


                    if (restaurantDailyTrack.contains("totalOrdersToday")) {
                        int val = Integer.parseInt(restaurantDailyTrack.getString("totalOrdersToday", ""));
                        val = val + 1;
                        restaurantTrackEditor.putString("totalOrdersToday", String.valueOf(val));
                    } else {
                        restaurantTrackEditor.putString("totalOrdersToday", String.valueOf(1));
                    }
                    if (restaurantDailyTrack.contains("totalTransactionsToday")) {
                        double val = Double.parseDouble(restaurantDailyTrack.getString("totalTransactionsToday", ""));
                        val = val + Double.parseDouble(orderAmount);
                        restaurantTrackEditor.putString("totalTransactionsToday", String.valueOf(val));
                    } else {
                        restaurantTrackEditor.putString("totalTransactionsToday", String.valueOf(orderAmount));
                    }

                    String approveTime = String.valueOf(System.currentTimeMillis());
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Users").child(id).child("Recent Orders").child(time);
                    for (int i = 0; i < dishNames.size(); i++) {
                        MyClass myClass = new MyClass(dishNames.get(i), dishPrices.get(i), image.get(i), type.get(i), "" + approveTime, dishQuantity.get(i), dishHalfOr.get(i), state, String.valueOf(orderAmount), orderID, orderAndPayment.get(i), "Order Approved", sharedPreferences.getString("locality", ""));
                        databaseReference.child(Objects.requireNonNull(auth.getUid())).child(dishNames.get(i)).setValue(myClass);
                    }


                    databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(state).child(sharedPreferences.getString("locality", "")).child(Objects.requireNonNull(auth.getUid()));
                    for (int i = 0; i < dishNames.size(); i++) {

                        MyClass myClass = new MyClass(dishNames.get(i), dishPrices.get(i), image.get(i), type.get(i), "" + approveTime, dishQuantity.get(i), dishHalfOr.get(i), state, String.valueOf(orderAmount), orderID, orderAndPayment.get(i), "Order Approved", sharedPreferences.getString("locality", ""));
                        databaseReference.child("Recent Orders").child("" + time).child(id).child(dishNames.get(i)).setValue(myClass);
                    }
                    restaurantTrackEditor.apply();
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(state).child(sharedPreferences.getString("locality", "")).child(Objects.requireNonNull(auth.getUid())).child("Tables").child(table);
                    SharedPreferences loginInfo = getSharedPreferences("loginInfo", MODE_PRIVATE);
//                    if (loginInfo.contains("payoutMethodChoosen")) {
//                        if (loginInfo.getString("payoutMethodChoosen", "").equals("imps")) {
//                            amountToBeSend = Double.parseDouble(orderAmount);
//                            amountToBeSend = amountToBeSend - 2;
                            new MakePaymentToVendor().execute();
//                        } else {
                            DatabaseReference updatePayoutOrder = FirebaseDatabase.getInstance().getReference().getRoot().child("Admin").child(auth.getUid());
                            updatePayoutOrder.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.hasChild("totalPayoutAmount")) {
                                        double current = Double.parseDouble(String.valueOf(snapshot.child("totalPayoutAmount").getValue()));
                                        current += Double.parseDouble(orderAmount);
                                        updatePayoutOrder.child("totalPayoutAmount").setValue(String.valueOf(current));
                                    } else {
                                        updatePayoutOrder.child("totalPayoutAmount").setValue(String.valueOf(orderAmount));
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
//                        }
//                    } else {
////                            Toast.makeText(this, "Defau", Toast.LENGTH_SHORT).show();
//                        Toast.makeText(this, "No payout method choosen\nDefault Method will be applicable", Toast.LENGTH_LONG).show();
//                        DatabaseReference updatePayoutOrder = FirebaseDatabase.getInstance().getReference().getRoot().child("Admin").child(auth.getUid());
//                        updatePayoutOrder.addListenerForSingleValueEvent(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                if (snapshot.hasChild("totalPayoutAmount")) {
//                                    double current = Double.parseDouble(String.valueOf(snapshot.child("totalPayoutAmount").getValue()));
//                                    current += Double.parseDouble(orderAmount);
//                                    updatePayoutOrder.child("totalPayoutAmount").setValue(String.valueOf(current));
//                                } else {
//                                    updatePayoutOrder.child("totalPayoutAmount").setValue(String.valueOf(orderAmount));
//                                }
//                            }
//
//                            @Override
//                            public void onCancelled(@NonNull DatabaseError error) {
//
//                            }
//                        });
//                    }
                    try {
                        main.put("to", "/topics/" + id + "");
                        JSONObject notification = new JSONObject();
                        notification.put("title", "Order Approved");
                        notification.put("click_action", "Table Frag");
                        notification.put("body", "Your order is approved by the owner");
                        main.put("notification", notification);

                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, main, response -> {

                        }, error -> Toast.makeText(ApproveCurrentOrder.this, error.getLocalizedMessage() + "null", Toast.LENGTH_SHORT).show()) {
                            @Override
                            public Map<String, String> getHeaders() {
                                Map<String, String> header = new HashMap<>();
                                header.put("content-type", "application/json");
                                header.put("authorization", "key=AAAAjq_WsHs:APA91bGZV-uH-NJddxIniy8h1tDGDHqxhgvFdyNRDaV_raxjvSM_FkKu7JSwtSp4Q_iSmPuTKGGIB2M_07c9rKgPXUH43-RzpK6zkaSaIaNgmeiwUO40rYxYUZAkKoLAQQVeVJ7mXboD");
                                return header;
                            }
                        };
                        reference.child("Current Order").removeValue();
                        requestQueue.add(jsonObjectRequest);
                    } catch (Exception e) {
                        Toast.makeText(ApproveCurrentOrder.this, e.getLocalizedMessage() + "null", Toast.LENGTH_SHORT).show();
                    }
                    new Handler().postDelayed(this::finish, 1500);
                } else {
                    String month = monthName[calendar.get(Calendar.MONTH)];
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(state).child(sharedPreferences.getString("locality", "")).child(Objects.requireNonNull(auth.getUid())).child("Tables").child(table);
                    RequestQueue requestQueue = Volley.newRequestQueue(ApproveCurrentOrder.this);
                    JSONObject main = new JSONObject();
                    try {
                        main.put("to", "/topics/" + id + "");
                        JSONObject notification = new JSONObject();
                        notification.put("title", "Order Approved");
                        notification.put("click_action", "Table Frag");
                        notification.put("body", "Your order is approved by the owner");
                        main.put("notification", notification);

                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, main, response -> {

                        }, error -> Toast.makeText(ApproveCurrentOrder.this, error.getLocalizedMessage() + "null", Toast.LENGTH_SHORT).show()) {
                            @Override
                            public Map<String, String> getHeaders() {
                                Map<String, String> header = new HashMap<>();
                                header.put("content-type", "application/json");
                                header.put("authorization", "key=AAAAjq_WsHs:APA91bGZV-uH-NJddxIniy8h1tDGDHqxhgvFdyNRDaV_raxjvSM_FkKu7JSwtSp4Q_iSmPuTKGGIB2M_07c9rKgPXUH43-RzpK6zkaSaIaNgmeiwUO40rYxYUZAkKoLAQQVeVJ7mXboD");
                                return header;
                            }
                        };
                        reference.child("Current Order").removeValue();
                        requestQueue.add(jsonObjectRequest);
                    } catch (Exception e) {
                        Toast.makeText(ApproveCurrentOrder.this, e.getLocalizedMessage() + "null", Toast.LENGTH_SHORT).show();
                    }
                    DatabaseReference addToTable = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(state).child(sharedPreferences.getString("locality", "")).child(auth.getUid()).child("Tables").child(table);
                    addToTable.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.hasChild("amountToBePaid")) {
                                Double currentVal = Double.parseDouble(String.valueOf(snapshot.child("amountToBePaid").getValue()));
                                currentVal += Double.parseDouble(orderAmount);
                                addToTable.child("amountToBePaid").setValue(String.valueOf(currentVal));
                            } else
                                addToTable.child("amountToBePaid").setValue(String.valueOf(orderAmount));
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    SharedPreferences storeTablePayInEnd = getSharedPreferences("StoreDataForPayInEnd", MODE_PRIVATE);
                    SharedPreferences.Editor storePayInEndEdit = storeTablePayInEnd.edit();


                if(storeTablePayInEnd.contains(table) && storeTablePayInEnd.getString(table,"").equals(id)){
                    String timeStr = storeTablePayInEnd.getString(table + "time","");
                    double val = Double.parseDouble(restaurantDailyTrack.getString("totalTransactionsToday", ""));
                    val = val + Double.parseDouble(orderAmount);
                    restaurantTrackEditor.putString("totalTransactionsToday", String.valueOf(val));
                    restaurantTrackEditor.apply();

                    if (storeOrdersForAdminInfo.contains(month)) {

                        try {
                            Type type = new TypeToken<List<List<String>>>() {
                            }.getType();
                            gson = new Gson();
                            json = storeOrdersForAdminInfo.getString(month, "");
                            List<List<String>> mainDataList = gson.fromJson(json, type);
                            List<String> date = new ArrayList<>(mainDataList.get(0));
                            List<String> transID = new ArrayList<>(mainDataList.get(1));
                            List<String> userID = new ArrayList<>(mainDataList.get(2));
                            List<String> orderAmountList = new ArrayList<>(mainDataList.get(3));
                            int pos = date.indexOf(timeStr);
                            double valDouble = Double.parseDouble(orderAmountList.get(pos));
                            valDouble += Double.parseDouble(orderAmount);
                            orderAmountList.set(pos, String.valueOf(valDouble));
                            List<List<String>> storeNewList = new ArrayList<>();
                            storeNewList.add(date);
                            storeNewList.add(transID);
                            storeNewList.add(userID);
                            storeNewList.add(orderAmountList);

                            json = gson.toJson(storeNewList);
                            storeEditor.putString(month, json);
                            storeEditor.apply();
                        }catch (Exception e){

                            Type type = new TypeToken<List<List<String>>>() {
                            }.getType();
                            gson = new Gson();
                            json = storeOrdersForAdminInfo.getString(month, "");
                            List<List<String>> mainDataList = gson.fromJson(json, type);
                            List<String> date = new ArrayList<>(mainDataList.get(0));
                            List<String> transID = new ArrayList<>(mainDataList.get(1));
                            List<String> userID = new ArrayList<>(mainDataList.get(2));
                            List<String> orderAmountList = new ArrayList<>(mainDataList.get(3));
                            int pos = date.indexOf(timeStr);
                            double valDouble = Double.parseDouble(orderAmountList.get(pos));
                            valDouble += Double.parseDouble(orderAmount);
                            orderAmountList.set(pos, String.valueOf(valDouble));
                            List<List<String>> storeNewList = new ArrayList<>();
                            storeNewList.add(date);
                            storeNewList.add(transID);
                            storeNewList.add(userID);
                            storeNewList.add(orderAmountList);

                            json = gson.toJson(storeNewList);
                            storeEditor.putString(month, json);
                            storeEditor.apply();
                        }
                        try {
                            int day = calendar.get(Calendar.DAY_OF_MONTH);
                            Type type = new TypeToken<List<List<String>>>() {
                            }.getType();
                            gson = new Gson();
                            json = storeDailyTotalOrdersMade.getString(month, "");
                            List<List<String>> mainList = gson.fromJson(json, type);
                            List<String> days = new ArrayList<>(mainList.get(0));
                            List<String> totalAmounts = new ArrayList<>(mainList.get(1));
                            List<String> totalOrdersPlaced = new ArrayList<>(mainList.get(2));

                            if (Integer.parseInt(days.get(days.size() - 1)) == day) {
                                Double totalAmount = Double.parseDouble(totalAmounts.get(totalAmounts.size() - 1));
                                totalAmount += Double.parseDouble(orderAmount);
                                totalAmounts.set(totalAmounts.size() - 1, String.valueOf(totalAmount));

                            } else {
                                days.add(String.valueOf(day));
                                totalOrdersPlaced.add("1");
                                totalAmounts.add(String.valueOf(orderAmount));
                            }

                            List<List<String>> newList = new ArrayList<>();
                            newList.add(days);
                            newList.add(totalAmounts);
                            newList.add(totalOrdersPlaced);
                            json = gson.toJson(newList);
                            storeDailyEditor.putString(month, json);
                            storeDailyEditor.apply();
                        }catch (Exception e){
                            int day = calendar.get(Calendar.DAY_OF_MONTH);
                            Type type = new TypeToken<List<List<String>>>() {
                            }.getType();
                            gson = new Gson();
                            json = storeDailyTotalOrdersMade.getString(month, "");
                            List<List<String>> mainList = gson.fromJson(json, type);
                            List<String> days = new ArrayList<>(mainList.get(0));
                            List<String> totalAmounts = new ArrayList<>(mainList.get(1));
                            List<String> totalOrdersPlaced = new ArrayList<>(mainList.get(2));

                            if (Integer.parseInt(days.get(days.size() - 1)) == day) {
                                Double totalAmount = Double.parseDouble(totalAmounts.get(totalAmounts.size() - 1));
                                totalAmount += Double.parseDouble(orderAmount);
                                totalAmounts.set(totalAmounts.size() - 1, String.valueOf(totalAmount));

                            } else {
                                days.add(String.valueOf(day));
                                totalOrdersPlaced.add("1");
                                totalAmounts.add(String.valueOf(orderAmount));
                            }

                            List<List<String>> newList = new ArrayList<>();
                            newList.add(days);
                            newList.add(totalAmounts);
                            newList.add(totalOrdersPlaced);
                            json = gson.toJson(newList);
                            storeDailyEditor.putString(month, json);
                            storeDailyEditor.apply();

//                        Log.i("myInfo", storeNewList.toString());
                            Log.i("myInfo", newList.toString());
                        }

//                        Log.i("myInfo", storeNewList.toString());
//                        Log.i("myInfo", newList.toString());
                    }
                }else {
                    String timePaying = System.currentTimeMillis() + "";
                    String str = "ORDER_" + timePaying;
                    storePayInEndEdit.putString(table,id);
                    storePayInEndEdit.putString(table + "id",str);
                    storePayInEndEdit.putString(table + "time",timePaying);
                    storePayInEndEdit.apply();
                    if (restaurantDailyTrack.contains("totalOrdersToday")) {
                        int val = Integer.parseInt(restaurantDailyTrack.getString("totalOrdersToday", ""));
                        val = val + 1;
                        restaurantTrackEditor.putString("totalOrdersToday", String.valueOf(val));
                    } else {
                        restaurantTrackEditor.putString("totalOrdersToday", String.valueOf(1));
                    }

                    if (restaurantDailyTrack.contains("totalTransactionsToday")) {
                        double val = Double.parseDouble(restaurantDailyTrack.getString("totalTransactionsToday", ""));
                        val = val + Double.parseDouble(orderAmount);
                        restaurantTrackEditor.putString("totalTransactionsToday", String.valueOf(val));
                    } else {
                        restaurantTrackEditor.putString("totalTransactionsToday", String.valueOf(orderAmount));
                    }

                    restaurantTrackEditor.apply();

                    if (storeOrdersForAdminInfo.contains(month)) {

                        try {
                            Type type = new TypeToken<List<List<String>>>() {
                            }.getType();
                            gson = new Gson();
                            json = storeOrdersForAdminInfo.getString(month, "");
                            List<List<String>> mainDataList = gson.fromJson(json, type);
                            List<String> date = new ArrayList<>(mainDataList.get(0));
                            List<String> transID = new ArrayList<>(mainDataList.get(1));
                            List<String> userID = new ArrayList<>(mainDataList.get(2));
                            List<String> orderAmountList = new ArrayList<>(mainDataList.get(3));

                            date.add(timePaying);
                            transID.add("ORDER_" + timePaying);
                            userID.add(id);
                            orderAmountList.add(orderAmount + "");

                            List<List<String>> storeNewList = new ArrayList<>();
                            storeNewList.add(date);
                            storeNewList.add(transID);
                            storeNewList.add(userID);
                            storeNewList.add(orderAmountList);

                            json = gson.toJson(storeNewList);
                            storeEditor.putString(month, json);
                            storeEditor.apply();
                        }catch (Exception e){

                            Type type = new TypeToken<List<List<String>>>() {
                            }.getType();
                            gson = new Gson();
                            json = storeOrdersForAdminInfo.getString(month, "");
                            List<List<String>> mainDataList = gson.fromJson(json, type);
                            List<String> date = new ArrayList<>(mainDataList.get(0));
                            List<String> transID = new ArrayList<>(mainDataList.get(1));
                            List<String> userID = new ArrayList<>(mainDataList.get(2));
                            List<String> orderAmountList = new ArrayList<>(mainDataList.get(3));

                            date.add(timePaying);
                            transID.add("ORDER_" + timePaying);
                            userID.add(id);
                            orderAmountList.add(orderAmount + "");

                            List<List<String>> storeNewList = new ArrayList<>();
                            storeNewList.add(date);
                            storeNewList.add(transID);
                            storeNewList.add(userID);
                            storeNewList.add(orderAmountList);

                            json = gson.toJson(storeNewList);
                            storeEditor.putString(month, json);
                            storeEditor.apply();
                        }

                        try {
                            int day = calendar.get(Calendar.DAY_OF_MONTH);
                            Type type = new TypeToken<List<List<String>>>() {
                            }.getType();
                            gson = new Gson();
                            json = storeDailyTotalOrdersMade.getString(month, "");
                            List<List<String>> mainList = gson.fromJson(json, type);
                            List<String> days = new ArrayList<>(mainList.get(0));
                            List<String> totalAmounts = new ArrayList<>(mainList.get(1));
                            List<String> totalOrdersPlaced = new ArrayList<>(mainList.get(2));

                            if (Integer.parseInt(days.get(days.size() - 1)) == day) {
                                Double totalAmount = Double.parseDouble(totalAmounts.get(totalAmounts.size() - 1));
                                totalAmount += Double.parseDouble(orderAmount);
                                totalAmounts.set(totalAmounts.size() - 1, String.valueOf(totalAmount));

                                int totalOrder = Integer.parseInt(totalOrdersPlaced.get(totalOrdersPlaced.size() - 1));
                                totalOrder += 1;
                                totalOrdersPlaced.set(totalOrdersPlaced.size() - 1, String.valueOf(totalOrder));
                            } else {
                                days.add(String.valueOf(day));
                                totalOrdersPlaced.add("1");
                                totalAmounts.add(String.valueOf(orderAmount));
                            }

                            List<List<String>> newList = new ArrayList<>();
                            newList.add(days);
                            newList.add(totalAmounts);
                            newList.add(totalOrdersPlaced);
                            json = gson.toJson(newList);
                            storeDailyEditor.putString(month, json);
                            storeDailyEditor.apply();

//                            Log.i("myInfo", storeNewList.toString());
                            Log.i("myInfo", newList.toString());
                        }catch (Exception e){

                            int day = calendar.get(Calendar.DAY_OF_MONTH);
                            Type type = new TypeToken<List<List<String>>>() {
                            }.getType();
                            gson = new Gson();
                            json = storeDailyTotalOrdersMade.getString(month, "");
                            List<List<String>> mainList = gson.fromJson(json, type);
                            List<String> days = new ArrayList<>(mainList.get(0));
                            List<String> totalAmounts = new ArrayList<>(mainList.get(1));
                            List<String> totalOrdersPlaced = new ArrayList<>(mainList.get(2));

                            if (Integer.parseInt(days.get(days.size() - 1)) == day) {
                                Double totalAmount = Double.parseDouble(totalAmounts.get(totalAmounts.size() - 1));
                                totalAmount += Double.parseDouble(orderAmount);
                                totalAmounts.set(totalAmounts.size() - 1, String.valueOf(totalAmount));

                                int totalOrder = Integer.parseInt(totalOrdersPlaced.get(totalOrdersPlaced.size() - 1));
                                totalOrder += 1;
                                totalOrdersPlaced.set(totalOrdersPlaced.size() - 1, String.valueOf(totalOrder));
                            } else {
                                days.add(String.valueOf(day));
                                totalOrdersPlaced.add("1");
                                totalAmounts.add(String.valueOf(orderAmount));
                            }

                            List<List<String>> newList = new ArrayList<>();
                            newList.add(days);
                            newList.add(totalAmounts);
                            newList.add(totalOrdersPlaced);
                            json = gson.toJson(newList);
                            storeDailyEditor.putString(month, json);
                            storeDailyEditor.apply();

//                            Log.i("myInfo", storeNewList.toString());
                            Log.i("myInfo", newList.toString());
                        }
                    } else {

                        List<List<String>> mainDataList = new ArrayList<>();
                        List<String> date = new ArrayList<>();
                        List<String> transID = new ArrayList<>();
                        List<String> userID = new ArrayList<>();
                        List<String> orderAmountList = new ArrayList<>();
                        int day = calendar.get(Calendar.DAY_OF_MONTH);
                        date.add(timePaying);
                        transID.add("ORDER_" + timePaying);
                        userID.add(id);
                        orderAmountList.add(orderAmount + "");
                        mainDataList.add(date);
                        mainDataList.add(transID);
                        mainDataList.add(userID);
                        mainDataList.add(orderAmountList);

                        gson = new Gson();
                        json = gson.toJson(mainDataList);
                        storeEditor.putString(month, json);
                        storeEditor.apply();

                        List<List<String>> mainList = new ArrayList<>();
                        List<String> days = new ArrayList<>();
                        List<String> totalAmounts = new ArrayList<>();
                        List<String> totalOrdersPlaced = new ArrayList<>();

                        days.add(String.valueOf(day));
                        totalAmounts.add(String.valueOf(orderAmount));
                        totalOrdersPlaced.add(String.valueOf(1));

                        mainList.add(days);
                        mainList.add(totalAmounts);
                        mainList.add(totalOrdersPlaced);

                        gson = new Gson();
                        json = gson.toJson(mainList);
                        storeDailyEditor.putString(month, json);
                        storeDailyEditor.apply();
                        Log.i("myInfo", mainDataList.toString());
                        Log.i("myInfo", mainList.toString());
                    }

                    if(userFrequency.contains(month)){
                        try {
                            java.lang.reflect.Type type = new TypeToken<HashMap<String, String>>() {
                            }.getType();
                            gson = new Gson();
                            json = userFrequency.getString(month, "");
                            HashMap<String, String> map = gson.fromJson(json, type);
                            if (map.containsKey(id)) {
                                int val = Integer.parseInt(map.get(id) + "");
                                val++;
                                map.put(id, val + "");
                            } else {
                                map.put(id, "1");
                            }

                            json = gson.toJson(map);
                        }
                        catch (Exception e){

                            java.lang.reflect.Type type = new TypeToken<HashMap<String, String>>() {
                            }.getType();
                            gson = new Gson();
                            json = userFrequency.getString(month, "");
                            HashMap<String, String> map = gson.fromJson(json, type);
                            if (map.containsKey(id)) {
                                int val = Integer.parseInt(map.get(id) + "");
                                val++;
                                map.put(id, val + "");
                            } else {
                                map.put(id, "1");
                            }

                            json = gson.toJson(map);
                        }
                    }else{
                        HashMap<String,String> map = new HashMap<>();
                        map.put(id,"1");

                        gson = new Gson();
                        json = gson.toJson(map);

                    }
                    userFedit.putString(month,json);
                    userFedit.apply();

                }

                    File file = new File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "RestaurantEarningTracker.xlsx");
                    try {
                        String timePaying = System.currentTimeMillis() + "";
                        Cell cell;
                        FileInputStream fileInputStream = new FileInputStream(file);
                        XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream);
                        Sheet sheet = workbook.getSheetAt(0);
                        int max = sheet.getLastRowNum();
                        max = max + 1;
                        Row row = sheet.createRow(max);
                        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                        Date date = new Date(Long.parseLong(timePaying));
                        cell = row.createCell(0);
                        cell.setCellValue(dateFormat.format(date));
                        cell = row.createCell(1);
                        cell.setCellValue("ORDER_" + timePaying);
                        cell = row.createCell(2);
                        cell.setCellValue("PayInEnd");
                        cell = row.createCell(3);
                        cell.setCellValue("\u20B9" + orderAmount);
                        Log.i("info", max + "");
                        FileOutputStream fileOutputStream = new FileOutputStream(file);
                        workbook.write(fileOutputStream);
                        fileOutputStream.flush();
                        fileOutputStream.close();
                        Toast.makeText(ApproveCurrentOrder.this, "Completed", Toast.LENGTH_SHORT).show();
                        workbook.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    SharedPreferences checkPrem = getSharedPreferences("AdminPremiumDetails", MODE_PRIVATE);
                    if (checkPrem.contains("status") && checkPrem.getString("status", "").equals("active")) {
                        if (storeForDishAnalysis.contains("DishAnalysisMonthBasis")) {
                            try {
                                gson = new Gson();
                                Type type = new TypeToken<HashMap<String, HashMap<String, String>>>() {
                                }.getType();
                                String storedHash = storeForDishAnalysis.getString("DishAnalysisMonthBasis", "");
                                HashMap<String, HashMap<String, String>> myMap = gson.fromJson(storedHash, type);
                                HashMap<String, String> map;
                                if (myMap.containsKey(month)) {
                                    map = new HashMap<>(Objects.requireNonNull(myMap.get(month)));
                                    Log.i("checking", map.toString());
                                    for (int k = 0; k < dishNames.size(); k++) {
                                        if (map.containsKey(dishNames.get(k))) {
                                            int val = Integer.parseInt(Objects.requireNonNull(map.get(dishNames.get(k))));
                                            val++;
                                            map.put(dishNames.get(k), String.valueOf(val));
                                        } else {
                                            map.put(dishNames.get(k), "1");
                                        }
                                    }
                                } else {
                                    map = new HashMap<>();
                                    for (int i = 0; i < dishNames.size(); i++) {
                                        map.put(dishNames.get(i), "1");
                                    }
                                }
                                myMap.put(month, map);
                                dishAnalysis.putString("DishAnalysisMonthBasis", gson.toJson(myMap));
                            }catch (Exception e){

                                gson = new Gson();
                                Type type = new TypeToken<HashMap<String, HashMap<String, String>>>() {
                                }.getType();
                                String storedHash = storeForDishAnalysis.getString("DishAnalysisMonthBasis", "");
                                HashMap<String, HashMap<String, String>> myMap = gson.fromJson(storedHash, type);
                                HashMap<String, String> map;
                                if (myMap.containsKey(month)) {
                                    map = new HashMap<>(Objects.requireNonNull(myMap.get(month)));
                                    Log.i("checking", map.toString());
                                    for (int k = 0; k < dishNames.size(); k++) {
                                        if (map.containsKey(dishNames.get(k))) {
                                            int val = Integer.parseInt(Objects.requireNonNull(map.get(dishNames.get(k))));
                                            val++;
                                            map.put(dishNames.get(k), String.valueOf(val));
                                        } else {
                                            map.put(dishNames.get(k), "1");
                                        }
                                    }
                                } else {
                                    map = new HashMap<>();
                                    for (int i = 0; i < dishNames.size(); i++) {
                                        map.put(dishNames.get(i), "1");
                                    }
                                }
                                myMap.put(month, map);
                                dishAnalysis.putString("DishAnalysisMonthBasis", gson.toJson(myMap));
                            }
                        } else {
                            HashMap<String, HashMap<String, String>> map = new HashMap<>();
                            HashMap<String, String> myMap = new HashMap<>();
                            for (int j = 0; j < dishNames.size(); j++) {
                                myMap.put(dishNames.get(j), "1");
                            }
                            map.put(month, myMap);
                            gson = new Gson();
                            dishAnalysis.putString("DishAnalysisMonthBasis", gson.toJson(map));
                        }
                        dishAnalysis.apply();

                        if (trackingOfTakeAway.contains(month)) {
                            try {
                                java.lang.reflect.Type type = new TypeToken<HashMap<String, String>>() {
                                }.getType();
                                gson = new Gson();
                                json = trackingOfTakeAway.getString(month, "");
                                HashMap<String, String> map = gson.fromJson(json, type);
                                if (map.containsKey(calendar.get(Calendar.DAY_OF_MONTH) + "")) {
                                    int currentVal = Integer.parseInt(Objects.requireNonNull(map.get(calendar.get(Calendar.DAY_OF_MONTH) + "")));
                                    currentVal++;
                                    map.put(calendar.get(Calendar.DAY_OF_MONTH) + "", currentVal + "");
                                } else {
                                    map.put(calendar.get(Calendar.DAY_OF_MONTH) + "", "1");
                                }
                                json = gson.toJson(map);
                            }catch (Exception e){

                                java.lang.reflect.Type type = new TypeToken<HashMap<String, String>>() {
                                }.getType();
                                gson = new Gson();
                                json = trackingOfTakeAway.getString(month, "");
                                HashMap<String, String> map = gson.fromJson(json, type);
                                if (map.containsKey(calendar.get(Calendar.DAY_OF_MONTH) + "")) {
                                    int currentVal = Integer.parseInt(Objects.requireNonNull(map.get(calendar.get(Calendar.DAY_OF_MONTH) + "")));
                                    currentVal++;
                                    map.put(calendar.get(Calendar.DAY_OF_MONTH) + "", currentVal + "");
                                } else {
                                    map.put(calendar.get(Calendar.DAY_OF_MONTH) + "", "1");
                                }
                                json = gson.toJson(map);
                            }
                        } else {
                            gson = new Gson();
                            HashMap<String, String> map = new HashMap<>();
                            map.put(calendar.get(Calendar.DAY_OF_MONTH) + "", "1");
                            json = gson.toJson(map);
                        }
                        trackingDineAndWay.putString(month, json);
                        trackingDineAndWay.apply();
                    }

                    AsyncTask.execute(() -> reference.child("StoreOrdersCheckOut").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                for (int i = 0; i < dishNames.size(); i++) {
                                    if (snapshot.hasChild(dishNames.get(i))) {
                                        int prevAmt = Integer.parseInt(Objects.requireNonNull(snapshot.child(dishNames.get(i)).child("price").getValue(String.class)));
                                        int prev = Integer.parseInt(Objects.requireNonNull(snapshot.child(dishNames.get(i)).child("count").getValue(String.class)));
                                        prev += Integer.parseInt(dishQuantity.get(i));
                                        prevAmt += Integer.parseInt(dishPrices.get(i));
                                        reference.child("StoreOrdersCheckOut").child(dishNames.get(i)).child("count").setValue(String.valueOf(prev));
                                        reference.child("StoreOrdersCheckOut").child(dishNames.get(i)).child("price").setValue(String.valueOf(prevAmt));
                                    } else {
                                        reference.child("StoreOrdersCheckOut").child(dishNames.get(i)).child("image").setValue(image.get(i));
                                        reference.child("StoreOrdersCheckOut").child(dishNames.get(i)).child("count").setValue(dishQuantity.get(i));
                                        reference.child("StoreOrdersCheckOut").child(dishNames.get(i)).child("price").setValue(dishPrices.get(i));
                                        reference.child("StoreOrdersCheckOut").child(dishNames.get(i)).child("type").setValue(type.get(i));
                                        reference.child("StoreOrdersCheckOut").child(dishNames.get(i)).child("halfOr").setValue(dishHalfOr.get(i));
                                    }
                                }
                            } else {
                                for (int i = 0; i < dishNames.size(); i++) {
                                    reference.child("StoreOrdersCheckOut").child(dishNames.get(i)).child("image").setValue(image.get(i));
                                    reference.child("StoreOrdersCheckOut").child(dishNames.get(i)).child("count").setValue(dishQuantity.get(i));
                                    reference.child("StoreOrdersCheckOut").child(dishNames.get(i)).child("price").setValue(dishPrices.get(i));
                                    reference.child("StoreOrdersCheckOut").child(dishNames.get(i)).child("type").setValue(type.get(i));
                                    reference.child("StoreOrdersCheckOut").child(dishNames.get(i)).child("halfOr").setValue(dishHalfOr.get(i));
                                }
                            }
                            runOnUiThread(() -> {
                                Toast.makeText(ApproveCurrentOrder.this, "Completed", Toast.LENGTH_SHORT).show();
                                finish();
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    }));


                }
            }catch (Exception e){
                Toast.makeText(this, "Something went wrong :(", Toast.LENGTH_SHORT).show();
                approve.setEnabled(true);
            }
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

            alert.setPositiveButton("submit", (dialogInterface, ii) -> {
                if(!editText.getText().toString().equals("")) {
                    dialogInterface.dismiss();
                    decline.setEnabled(false);
                    RequestQueue requestQueue = Volley.newRequestQueue(ApproveCurrentOrder.this);
                    JSONObject main = new JSONObject();
//                    new GenratePDF().execute();
                    FirebaseAuth auth = FirebaseAuth.getInstance();
                    String approveTime = String.valueOf(System.currentTimeMillis());
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Users").child(id).child("Recent Orders").child(time);
                    for(int i=0;i<dishNames.size();i++){
                        MyClass myClass = new MyClass(dishNames.get(i),dishPrices.get(i),image.get(i),type.get(i),""+approveTime,dishQuantity.get(i),dishHalfOr.get(i),state,String.valueOf(orderAmount),orderID,orderAndPayment.get(i),"Order Declined",sharedPreferences.getString("locality",""));
                        databaseReference.child(Objects.requireNonNull(auth.getUid())).child(dishNames.get(i)).setValue(myClass);
                    }


                    databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(state).child(sharedPreferences.getString("locality","")).child(auth.getUid());
                    for(int i=0;i<dishNames.size();i++){
                        MyClass myClass = new MyClass(dishNames.get(i),dishPrices.get(i),image.get(i),type.get(i),""+approveTime,dishQuantity.get(i),dishHalfOr.get(i),state,String.valueOf(orderAmount),orderID,orderAndPayment.get(i),"Order Declined",sharedPreferences.getString("locality",""));
                        databaseReference.child("Recent Orders").child("" + time).child(auth.getUid()).child(dishNames.get(i)).setValue(myClass);
                    }
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(state).child(sharedPreferences.getString("locality","")).child(Objects.requireNonNull(auth.getUid())).child("Tables").child(table);
                    if(!paymentType.equals("endCheckOut"))
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
                                header.put("authorization", "key=AAAAjq_WsHs:APA91bGZV-uH-NJddxIniy8h1tDGDHqxhgvFdyNRDaV_raxjvSM_FkKu7JSwtSp4Q_iSmPuTKGGIB2M_07c9rKgPXUH43-RzpK6zkaSaIaNgmeiwUO40rYxYUZAkKoLAQQVeVJ7mXboD");
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

    private void updateTotalAmount(String valueOf) {
        storeTotalAmountMonth.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChild("totalMonthAmount")){
                    Double current = Double.parseDouble(valueOf);
                    Double existingVal = Double.parseDouble(String.valueOf(snapshot.child("totalMonthAmount").getValue()));
                    Double finalVal = current + existingVal;
                    storeTotalAmountMonth.child("totalMonthAmount").setValue(String.valueOf(finalVal));
                }else
                    storeTotalAmountMonth.child("totalMonthAmount").setValue(valueOf);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
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
        SharedPreferences sharedPreferences = getSharedPreferences("loginInfo",MODE_PRIVATE);
        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(state).child(sharedPreferences.getString("locality","")).child(Objects.requireNonNull(auth.getUid())).child("Tables").child(table).child("Current Order");
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
//            StringRequest stringRequest = new StringRequest(Request.Method.POST, testPaymentToVendor, response -> {
//                Log.i("response",response);
                SharedPreferences checkPrem = getSharedPreferences("AdminPremiumDetails",MODE_PRIVATE);
                if(checkPrem.contains("status") && checkPrem.getString("status","").equals("active")) {
                    String month = monthName[calendar.get(Calendar.MONTH)];

                    new Thread(() -> {
                        if(userFrequency.contains(month)){
                            try {
                                java.lang.reflect.Type type = new TypeToken<HashMap<String, String>>() {
                                }.getType();
                                gson = new Gson();
                                json = userFrequency.getString(month, "");
                                HashMap<String, String> map = gson.fromJson(json, type);
                                if (map.containsKey(id)) {
                                    int val = Integer.parseInt(map.get(id) + "");
                                    val++;
                                    map.put(id, val + "");
                                } else {
                                    map.put(id, "1");
                                }

                                json = gson.toJson(map);
                            }catch (Exception e){
                                java.lang.reflect.Type type = new TypeToken<HashMap<String, String>>() {
                                }.getType();
                                gson = new Gson();
                                json = userFrequency.getString(month, "");
                                HashMap<String, String> map = gson.fromJson(json, type);
                                if (map.containsKey(id)) {
                                    int val = Integer.parseInt(map.get(id) + "");
                                    val++;
                                    map.put(id, val + "");
                                } else {
                                    map.put(id, "1");
                                }

                                json = gson.toJson(map);
                            }
                        }else{
                            HashMap<String,String> map = new HashMap<>();
                            map.put(id,"1");

                            gson = new Gson();
                            json = gson.toJson(map);

                        }
                        userFedit.putString(month,json);
                        userFedit.apply();
                    }).start();
                    if (storeOrdersForAdminInfo.contains(month)) {
                        try {
                            Type type = new TypeToken<List<List<String>>>() {
                            }.getType();
                            gson = new Gson();
                            json = storeOrdersForAdminInfo.getString(month, "");
                            List<List<String>> mainDataList = gson.fromJson(json, type);
                            List<String> date = new ArrayList<>(mainDataList.get(0));
                            List<String> transID = new ArrayList<>(mainDataList.get(1));
                            List<String> userID = new ArrayList<>(mainDataList.get(2));
                            List<String> orderAmountList = new ArrayList<>(mainDataList.get(3));

                            date.add(time);
                            transID.add("Online");
                            userID.add(id);
                            orderAmountList.add(orderAmount + "");

                            List<List<String>> storeNewList = new ArrayList<>();
                            storeNewList.add(date);
                            storeNewList.add(transID);
                            storeNewList.add(userID);
                            storeNewList.add(orderAmountList);

                            json = gson.toJson(storeNewList);
                            storeEditor.putString(month, json);
                            storeEditor.apply();
                        }catch (Exception e){

                            Type type = new TypeToken<List<List<String>>>() {
                            }.getType();
                            gson = new Gson();
                            json = storeOrdersForAdminInfo.getString(month, "");
                            List<List<String>> mainDataList = gson.fromJson(json, type);
                            List<String> date = new ArrayList<>(mainDataList.get(0));
                            List<String> transID = new ArrayList<>(mainDataList.get(1));
                            List<String> userID = new ArrayList<>(mainDataList.get(2));
                            List<String> orderAmountList = new ArrayList<>(mainDataList.get(3));

                            date.add(time);
                            transID.add("Online");
                            userID.add(id);
                            orderAmountList.add(orderAmount + "");

                            List<List<String>> storeNewList = new ArrayList<>();
                            storeNewList.add(date);
                            storeNewList.add(transID);
                            storeNewList.add(userID);
                            storeNewList.add(orderAmountList);

                            json = gson.toJson(storeNewList);
                            storeEditor.putString(month, json);
                            storeEditor.apply();
                        }
                        try {
                            int day = calendar.get(Calendar.DAY_OF_MONTH);
                            Type type = new TypeToken<List<List<String>>>() {
                            }.getType();
                            gson = new Gson();
                            json = storeDailyTotalOrdersMade.getString(month, "");
                            List<List<String>> mainList = gson.fromJson(json, type);
                            List<String> days = new ArrayList<>(mainList.get(0));
                            List<String> totalAmounts = new ArrayList<>(mainList.get(1));
                            List<String> totalOrdersPlaced = new ArrayList<>(mainList.get(2));

                            if (Integer.parseInt(days.get(days.size() - 1)) == day) {
                                Double totalAmount = Double.parseDouble(totalAmounts.get(totalAmounts.size() - 1));
                                totalAmount += Double.parseDouble(orderAmount);
                                totalAmounts.set(totalAmounts.size() - 1, String.valueOf(totalAmount));

                                int totalOrder = Integer.parseInt(totalOrdersPlaced.get(totalOrdersPlaced.size() - 1));
                                totalOrder += 1;
                                totalOrdersPlaced.set(totalOrdersPlaced.size() - 1, String.valueOf(totalOrder));
                            } else {
                                days.add(String.valueOf(day));
                                totalOrdersPlaced.add("1");
                                totalAmounts.add(String.valueOf(orderAmount));
                            }

                            List<List<String>> newList = new ArrayList<>();
                            newList.add(days);
                            newList.add(totalAmounts);
                            newList.add(totalOrdersPlaced);
                            json = gson.toJson(newList);
                            storeDailyEditor.putString(month, json);
                            storeDailyEditor.apply();

//                            Log.i("myInfo", storeNewList.toString());
                            Log.i("myInfo", newList.toString());
                        }catch (Exception e){

                            int day = calendar.get(Calendar.DAY_OF_MONTH);
                            Type type = new TypeToken<List<List<String>>>() {
                            }.getType();
                            gson = new Gson();
                            json = storeDailyTotalOrdersMade.getString(month, "");
                            List<List<String>> mainList = gson.fromJson(json, type);
                            List<String> days = new ArrayList<>(mainList.get(0));
                            List<String> totalAmounts = new ArrayList<>(mainList.get(1));
                            List<String> totalOrdersPlaced = new ArrayList<>(mainList.get(2));

                            if (Integer.parseInt(days.get(days.size() - 1)) == day) {
                                Double totalAmount = Double.parseDouble(totalAmounts.get(totalAmounts.size() - 1));
                                totalAmount += Double.parseDouble(orderAmount);
                                totalAmounts.set(totalAmounts.size() - 1, String.valueOf(totalAmount));

                                int totalOrder = Integer.parseInt(totalOrdersPlaced.get(totalOrdersPlaced.size() - 1));
                                totalOrder += 1;
                                totalOrdersPlaced.set(totalOrdersPlaced.size() - 1, String.valueOf(totalOrder));
                            } else {
                                days.add(String.valueOf(day));
                                totalOrdersPlaced.add("1");
                                totalAmounts.add(String.valueOf(orderAmount));
                            }

                            List<List<String>> newList = new ArrayList<>();
                            newList.add(days);
                            newList.add(totalAmounts);
                            newList.add(totalOrdersPlaced);
                            json = gson.toJson(newList);
                            storeDailyEditor.putString(month, json);
                            storeDailyEditor.apply();

//                            Log.i("myInfo", storeNewList.toString());
                            Log.i("myInfo", newList.toString());
                        }
                    } else {
                        List<List<String>> mainDataList = new ArrayList<>();
                        List<String> date = new ArrayList<>();
                        List<String> transID = new ArrayList<>();
                        List<String> userID = new ArrayList<>();
                        List<String> orderAmountList = new ArrayList<>();
                        int day = calendar.get(Calendar.DAY_OF_MONTH);
                        date.add(time);
                        transID.add("Online");
                        userID.add(id);
                        orderAmountList.add(orderAmount + "");
                        mainDataList.add(date);
                        mainDataList.add(transID);
                        mainDataList.add(userID);
                        mainDataList.add(orderAmountList);

                        gson = new Gson();
                        json = gson.toJson(mainDataList);
                        storeEditor.putString(month, json);
                        storeEditor.apply();

                        List<List<String>> mainList = new ArrayList<>();
                        List<String> days = new ArrayList<>();
                        List<String> totalAmounts = new ArrayList<>();
                        List<String> totalOrdersPlaced = new ArrayList<>();

                        days.add(String.valueOf(day));
                        totalAmounts.add(String.valueOf(orderAmount));
                        totalOrdersPlaced.add(String.valueOf(1));

                        mainList.add(days);
                        mainList.add(totalAmounts);
                        mainList.add(totalOrdersPlaced);

                        gson = new Gson();
                        json = gson.toJson(mainList);
                        storeDailyEditor.putString(month, json);
                        storeDailyEditor.apply();
                        Log.i("myInfo", mainDataList.toString());
                        Log.i("myInfo", mainList.toString());
                    }
                    File file = new File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "RestaurantEarningTracker.xlsx");
                    try {
                        Cell cell;
                        FileInputStream fileInputStream = new FileInputStream(file);
                        XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream);
                        Sheet sheet = workbook.getSheetAt(0);
                        int max = sheet.getLastRowNum();
                        max = max + 1;
                        Row row = sheet.createRow(max);
                        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                        Date date = new Date(Long.parseLong(time));
                        cell = row.createCell(0);
                        cell.setCellValue(dateFormat.format(date));
                        cell = row.createCell(1);
                        cell.setCellValue("Online");
                        cell = row.createCell(2);
                        cell.setCellValue("Online");
                        cell = row.createCell(3);
                        cell.setCellValue("\u20B9" + orderAmount);
                        Log.i("info", max + "");
                        FileOutputStream fileOutputStream = new FileOutputStream(file);
                        workbook.write(fileOutputStream);
                        fileOutputStream.flush();
                        fileOutputStream.close();
                        Toast.makeText(ApproveCurrentOrder.this, "Completed", Toast.LENGTH_SHORT).show();
                        workbook.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                Log.i("statusOne", String.valueOf(makePaymentToVendor.getStatus()));
//            }, error -> {
//
//            }){
//                @NonNull
//                @Override
//                protected Map<String, String> getParams() {
//                    Map<String,String> params = new HashMap<>();
//                    params.put("benID","BKkZjAAB9fQmleexouAb2zSRtQm2");
//                    String genratedID = "ORDER_" + System.currentTimeMillis() + "_" + ApproveCurrentTakeAway.RandomString
//                            .getAlphaNumericString(5);
//
//                    transactionIdForExcel = genratedID;
//                    params.put("transID",genratedID);
//                    params.put("token",genratedToken);
//                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Admin").child(Objects.requireNonNull(auth.getUid())).child("Transactions");
//                    PaymentClass paymentClass = new PaymentClass(genratedID,id);
//                    databaseReference.child(time).setValue(paymentClass);
//                    params.put("amount", "1");
//                    return params;
//                }
//            };
//            requestQueue.add(stringRequest);
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

    public class addOrderToTableCurrent extends AsyncTask<Void,Void,Void>{
        
        @Override
        protected Void doInBackground(Void... voids) {
            SharedPreferences sharedPreferences = getSharedPreferences("loginInfo",MODE_PRIVATE);
            FirebaseAuth auth = FirebaseAuth.getInstance();
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(state).child(sharedPreferences.getString("locality","")).child(Objects.requireNonNull(auth.getUid())).child("Tables").child(table);
            for(int i=0;i<dishNames.size();i++){
                databaseReference.child("CurrentOrdersMade").child(time).child(dishNames.get(i)).child("quantity").setValue(dishQuantity.get(i));
                databaseReference.child("CurrentOrdersMade").child(time).child(dishNames.get(i)).child("name").setValue(dishNames.get(i));
            }
            return null;
        }
    }

    public class hugeBackgroundWork extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            String[] monthName = {"January", "February",
                    "March", "April", "May", "June", "July",
                    "August", "September", "October", "November",
                    "December"};

            Calendar calendar = Calendar.getInstance();
            String month = monthName[calendar.get(Calendar.MONTH)];
            String yearCurrent = calendar.get(Calendar.YEAR) + "";
            DatabaseReference trackAmountForGST = FirebaseDatabase.getInstance().getReference().getRoot().child("Complaints").child("AmountTrackingDB").child(state).child(yearCurrent).child(month);
            trackAmountForGST.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.hasChild("amountEarned")){
                        double prev = Double.parseDouble(snapshot.child("amountEarned").getValue(String.class));
                        prev += Double.parseDouble(orderAmount);
                        trackAmountForGST.child("amountEarned").setValue(prev + "");
                    }else
                        trackAmountForGST.child("amountEarned").setValue(orderAmount + "");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            SharedPreferences dish = getSharedPreferences("DishAnalysis",Context.MODE_PRIVATE);

            if(dish.contains("DishAnalysisMonthBasis")){
                gson = new Gson();
                java.lang.reflect.Type types = new TypeToken<HashMap<String, HashMap<String,Integer>>>(){}.getType();
                String storedHash = dish.getString("DishAnalysisMonthBasis","");
                HashMap<String,HashMap<String,Integer>> myMap = gson.fromJson(storedHash,types);
                if(myMap.containsKey(month)){
                    HashMap<String,Integer> map = new HashMap<>(Objects.requireNonNull(myMap.get(month)));


                    
                    Log.i("Dishinfo",map.toString());

                    HashMap<String,Integer> map1 = sortByValue(map);
                    Log.i("Dishinfo",map1.toString());
//               Map<String,String> sorted = map
//                        .entrySet()
//                        .stream()
//                        .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
//                        .collect(
//                                toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2,
//                                        LinkedHashMap::new));
//
//               Log.i("Dishinfo",map.toString());
//               Log.i("Dishinfo",sorted.toString());
//
                    ArrayList<String> keysName = new ArrayList<>(map1.keySet());
                    ArrayList<Integer> valuesName = new ArrayList<>(map1.values());

                    Log.i("info",keysName.toString());
                    Log.i("info",valuesName.toString());
//
                    Collections.reverse(keysName);
                    Collections.reverse(valuesName);

                    String dishName = keysName.get(0);
                    int timesOrderedDish = valuesName.get(0);

                    SharedPreferences sharedPreferences = getSharedPreferences("loginInfo",MODE_PRIVATE);
                    DatabaseReference addToRTDB = FirebaseDatabase.getInstance().getReference().getRoot().child("Offers").child(sharedPreferences.getString("state","")).child(sharedPreferences.getString("locality","")).child(Objects.requireNonNull(auth.getUid()));
                    addToRTDB.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            addToRTDB.child("BestDish").child("name").setValue(dishName);
                            addToRTDB.child("BestDish").child("timesOrdered").setValue(timesOrderedDish + "");
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
//               for(int i=0;i<sorted.size();i++){
//                   valuesName.add("" + sorted.values().toArray()[i]);
//                   keysName.add("" + sorted.keySet().toArray()[i]);
//               }
//
//                Log.i("Dishinfo",keysName.toString());
//                Log.i("Dishinfo",valuesName.toString());

                }
            }


            SharedPreferences sharedPreferences = getSharedPreferences("loginInfo",MODE_PRIVATE);
            DatabaseReference storeForFoodineAnalysis = FirebaseDatabase.getInstance().getReference().getRoot().child("FoodineRestaurantDB").child(sharedPreferences.getString("state","")).child(sharedPreferences.getString("locality","")).child(auth.getUid())
                    .child(month);
            storeForFoodineAnalysis.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        HashMap<String, String> map = new HashMap<>();
                        if(snapshot.hasChild("SalesInfo")){
                            for(DataSnapshot dataSnapshot : snapshot.child("SalesInfo").getChildren()){
                                map.put(dataSnapshot.getKey(),dataSnapshot.getValue(String.class));
                            }
                            String day = "Day" + calendar.get(Calendar.DAY_OF_MONTH);
                            if(map.containsKey(day + "")){
                                double oldVal = Double.parseDouble(map.get(day + ""));
                                oldVal += Double.parseDouble(orderAmount);
                                map.put(day + "",oldVal + "");
                            }else
                                map.put(day + "",orderAmount + "");

                        }else{
                            String day = "Day" + calendar.get(Calendar.DAY_OF_MONTH);
                            map.put(day + "",orderAmount + "");
                        }
                        storeForFoodineAnalysis.child("SalesInfo").setValue(map);

                        DatabaseReference storeForFoodineDailyTrack = FirebaseDatabase.getInstance().getReference().getRoot().child("Complaints").child("FoodineDailyTrack")
                                .child(month);
                        storeForFoodineDailyTrack.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists()){
                                    String day = "Day" + calendar.get(Calendar.DAY_OF_MONTH);
                                    HashMap<String,String> foodineTrack = (HashMap<String, String>) snapshot.getValue();
                                    if(foodineTrack.containsKey(day + ""))
                                    {
                                        double oldVal = Double.parseDouble(foodineTrack.get(day + ""));
                                        oldVal += Double.parseDouble(orderAmount);
                                        foodineTrack.put(day + "",oldVal + "");
                                    }else
                                        foodineTrack.put(day + "",orderAmount + "");

                                    storeForFoodineDailyTrack.setValue(foodineTrack);
                                }else{
                                    String day = "Day" + calendar.get(Calendar.DAY_OF_MONTH);
                                    HashMap<String,String> foodineTrack = new HashMap<>();
                                    foodineTrack.put(day + "",orderAmount + "");
                                    storeForFoodineDailyTrack.setValue(foodineTrack);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });


                        if(dish.contains("DishAnalysisMonthBasis")) {
                            gson = new Gson();
                            java.lang.reflect.Type types = new TypeToken<HashMap<String, HashMap<String, Integer>>>() {
                            }.getType();
                            String storedHash = dish.getString("DishAnalysisMonthBasis", "");
                            HashMap<String, HashMap<String, Integer>> myMap = gson.fromJson(storedHash, types);

                            HashMap<String,Integer> dishmap = new HashMap<>(Objects.requireNonNull(myMap.get(month)));


                            Log.i("Dishinfo",dishmap.toString());

                            HashMap<String,Integer> map1 = sortByValue(dishmap);
                            storeForFoodineAnalysis.child("DishInfo").setValue(map1);
                        }
                    }else{
                        HashMap<String, String> map = new HashMap<>();
                        String day = "Day" + calendar.get(Calendar.DAY_OF_MONTH);
                        map.put(day + "",orderAmount + "");
                        storeForFoodineAnalysis.child("SalesInfo").setValue(map);



                        DatabaseReference storeForFoodineDailyTrack = FirebaseDatabase.getInstance().getReference().getRoot().child("Complaints").child("FoodineDailyTrack")
                                .child(month);
                        storeForFoodineDailyTrack.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists()){
                                    String day = "Day" + calendar.get(Calendar.DAY_OF_MONTH);
                                    HashMap<String,String> foodineTrack = (HashMap<String, String>) snapshot.getValue();
                                    if(foodineTrack.containsKey(day + ""))
                                    {
                                        double oldVal = Double.parseDouble(foodineTrack.get(day + ""));
                                        oldVal += Double.parseDouble(orderAmount);
                                        foodineTrack.put(day + "",oldVal + "");
                                    }else
                                        foodineTrack.put(day + "",orderAmount + "");

                                    storeForFoodineDailyTrack.setValue(foodineTrack);
                                }else{
                                    String day = "Day" + calendar.get(Calendar.DAY_OF_MONTH);
                                    HashMap<String,String> foodineTrack = new HashMap<>();
                                    foodineTrack.put(day + "",orderAmount + "");
                                    storeForFoodineDailyTrack.setValue(foodineTrack);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        if(dish.contains("DishAnalysisMonthBasis")) {
                            gson = new Gson();
                            java.lang.reflect.Type types = new TypeToken<HashMap<String, HashMap<String, Integer>>>() {
                            }.getType();
                            String storedHash = dish.getString("DishAnalysisMonthBasis", "");
                            HashMap<String, HashMap<String, Integer>> myMap = gson.fromJson(storedHash, types);

                            HashMap<String,Integer> dishmap = new HashMap<>(Objects.requireNonNull(myMap.get(month)));


                            Log.i("Dishinfo",dishmap.toString());

                            HashMap<String,Integer> map1 = sortByValue(dishmap);
                            storeForFoodineAnalysis.child("DishInfo").setValue(map1);
                        }

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


            return null;
        }
    }

    public static HashMap<String, Integer>
    sortByValue(HashMap<String, Integer> hm)
    {
        // Create a list from elements of HashMap
        List<Map.Entry<String, Integer> > list
                = new LinkedList<Map.Entry<String, Integer> >(
                hm.entrySet());

        // Sort the list using lambda expression
        Collections.sort(
                list,
                (i1,
                 i2) -> i1.getValue().compareTo(i2.getValue()));

        // put data from sorted list to hashmap
        HashMap<String, Integer> temp
                = new LinkedHashMap<String, Integer>();
        for (Map.Entry<String, Integer> aa : list) {
            temp.put(aa.getKey(), aa.getValue());
        }
        return temp;
    }
}