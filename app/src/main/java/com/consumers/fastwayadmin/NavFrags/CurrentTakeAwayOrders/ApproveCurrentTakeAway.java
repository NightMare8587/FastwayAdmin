package com.consumers.fastwayadmin.NavFrags.CurrentTakeAwayOrders;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
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
import com.consumers.fastwayadmin.HomeScreen.ReportSupport.RequestRefundClass;
import com.consumers.fastwayadmin.NavFrags.homeFrag.ApproveCurrentOrder;
import com.consumers.fastwayadmin.R;
import com.developer.kalert.KAlertDialog;
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

import karpuzoglu.enes.com.fastdialog.Animations;
import karpuzoglu.enes.com.fastdialog.FastDialog;
import karpuzoglu.enes.com.fastdialog.FastDialogBuilder;
import karpuzoglu.enes.com.fastdialog.Type;

public class ApproveCurrentTakeAway extends AppCompatActivity {
    List<String> quantity;
    List<String> dishName;
    String deliveryInformation;
    List<String> image;
    List<String> type;
    boolean isGstAvailable = false;
    List<String> dishPrice;
    String amountPaidByUser;
    int veg = 0,nonVeg = 0,vegan = 0;
    List<String> orderAndPayment;
    String transactionIdForExcel;
    SharedPreferences storeDailyTotalOrdersMade;
    SharedPreferences dailyUserTrackingFor7days;
    SharedPreferences.Editor user7daysEdit;
    SharedPreferences dishDailyTrackForReports;
    SharedPreferences.Editor dailyReportTrackDish;
    SharedPreferences.Editor storeDailyEditor;
    FirebaseStorage storage;
    SharedPreferences dailyAverageOrder;
    SharedPreferences.Editor averageEditor;
    Gson gson;
    String json;
    double amountToBeSend;
    DatabaseReference storeTotalAmountOfMonth;
    Calendar calendar = Calendar.getInstance();
    String[] monthName = {"January", "February",
            "March", "April", "May", "June", "July",
            "August", "September", "October", "November",
            "December"};
    SharedPreferences storeOrdersForAdminInfo;
    SharedPreferences.Editor storeEditor;
    StorageReference storageReference;
    String userName,userEmail;
    String paymentMode;
    SharedPreferences restaurantDailyTrack;
    SharedPreferences.Editor restaurantTrackEditor;
    SharedPreferences restaurantTrackRecords;
    SharedPreferences userFrequency;
    SharedPreferences.Editor restaurantTrackRecordsEditor;
    String genratedToken;
    String customisation;
    String testPayoutToken = "https://intercellular-stabi.000webhostapp.com/CheckoutPayouts/testToken.php";
    String prodPayoutToken = "https://intercellular-stabi.000webhostapp.com/CheckoutPayouts/payoutIMPS.php";
    String testBearerToken = "https://intercellular-stabi.000webhostapp.com/CheckoutPayouts/test/testBearerToken.php";
    String prodBearerToken = "https://intercellular-stabi.000webhostapp.com/CheckoutPayouts/authBEarerToken.php";
    String testPaymentToVendor = "https://intercellular-stabi.000webhostapp.com/CheckoutPayouts/test/testPayment.php";
    String prodPaymentToVendor = "https://intercellular-stabi.000webhostapp.com/CheckoutPayouts/PaymentToVendor.php";
    FirebaseAuth auth = FirebaseAuth.getInstance();
    List<String> halfOr;
    String url = "https://intercellular-stabi.000webhostapp.com/refunds/initiateRefund.php";
    DatabaseReference saveRefundInfo;
    Bitmap bmp,scaled,bmp1,scaled1;
    Button decline,approve;
    SharedPreferences trackingOfTakeAway;
    SharedPreferences.Editor trackingDineAndWay;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    MakePaymentToVendor makePaymentToVendor = new MakePaymentToVendor();
    String digitCode;
    String time;
    ListView listView,dishNames,halfOrList;
    Button showCustom;
    SharedPreferences.Editor userFedit;
    String id,orderId,orderAmount;
    String URL = "https://fcm.googleapis.com/fcm/send";
    String state;
    SharedPreferences storeForDishAnalysis;
    SharedPreferences.Editor dishAnalysis;
    File path;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approve_current_take_away);

        id = getIntent().getStringExtra("id");
        showCustom = findViewById(R.id.showCustomisationCurrentTakeaway);
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().getRoot().child("Users").child(id);
        bmp = BitmapFactory.decodeResource(getResources(), R.drawable.foodinelogo);
        restaurantDailyTrack = getSharedPreferences("RestaurantTrackingDaily", Context.MODE_PRIVATE);
        restaurantTrackRecords = getSharedPreferences("RestaurantTrackRecords",Context.MODE_PRIVATE);
        userFrequency = getSharedPreferences("UsersFrequencyPerMonth",MODE_PRIVATE);
        trackingOfTakeAway = getSharedPreferences("TrackingOfTakeAway",MODE_PRIVATE);
        dailyUserTrackingFor7days = getSharedPreferences("DailyUserTrackingFor7days",MODE_PRIVATE);
        deliveryInformation = getIntent().getStringExtra("deliveryInformation");
        if(!deliveryInformation.equals("no")){
            AlertDialog.Builder builder = new AlertDialog.Builder(ApproveCurrentTakeAway.this);
            builder.setTitle("Delivery Information").setMessage("Below are the delivery information\n\n" + deliveryInformation)
                    .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    }).create();
            builder.show();
        }
        user7daysEdit = dailyUserTrackingFor7days.edit();
        trackingDineAndWay = trackingOfTakeAway.edit();
        AsyncTask.execute(() -> {
            DatabaseReference admin = FirebaseDatabase.getInstance().getReference().getRoot().child("Admin").child(auth.getUid()).child("Restauran Documents");
            admin.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.hasChild("gstinNum"))
                        isGstAvailable = true;
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        });
        dishDailyTrackForReports = getSharedPreferences("DishDailyTrackForReports",MODE_PRIVATE);
        dailyReportTrackDish = dishDailyTrackForReports.edit();
        userFedit = userFrequency.edit();
        restaurantTrackRecordsEditor = restaurantTrackRecords.edit();
        restaurantTrackEditor = restaurantDailyTrack.edit();
        StrictMode.VmPolicy.Builder builders = new StrictMode.VmPolicy.Builder();
        storeTotalAmountOfMonth = FirebaseDatabase.getInstance().getReference().getRoot().child("Admin").child(Objects.requireNonNull(auth.getUid()));
        StrictMode.setVmPolicy(builders.build());
        scaled = Bitmap.createScaledBitmap(bmp,500,500,false);
        storeForDishAnalysis = getSharedPreferences("DishAnalysis",MODE_PRIVATE);
        dailyAverageOrder = getSharedPreferences("DailyAverageOrderMonthly",MODE_PRIVATE);
        averageEditor = dailyAverageOrder.edit();
        dishAnalysis = storeForDishAnalysis.edit();
//        bmp1 = BitmapFactory.decodeResource(getResources(), R.drawable.orderdeclined);
//        scaled1 = Bitmap.createScaledBitmap(bmp1,500,500,false);
        sharedPreferences = getSharedPreferences("loginInfo",MODE_PRIVATE);
        editor = sharedPreferences.edit();
        state = sharedPreferences.getString("state","");
        storeOrdersForAdminInfo = getSharedPreferences("StoreOrders",MODE_PRIVATE);
        storeDailyTotalOrdersMade = getSharedPreferences("RestaurantDailyStoreForAnalysis",MODE_PRIVATE);
        storeDailyEditor = storeDailyTotalOrdersMade.edit();
        storeEditor = storeOrdersForAdminInfo.edit();
        saveRefundInfo = FirebaseDatabase.getInstance().getReference().getRoot().child("Users").child(id);
        orderAmount = getIntent().getStringExtra("orderAmount");
        customisation = getIntent().getStringExtra("customisation");
        orderId = getIntent().getStringExtra("orderID");
        time = getIntent().getStringExtra("time");
        paymentMode = getIntent().getStringExtra("payment");
        dishName = new ArrayList<>(getIntent().getStringArrayListExtra("dishName"));
        quantity = new ArrayList<>(getIntent().getStringArrayListExtra("DishQ"));
        halfOr = new ArrayList<>(getIntent().getStringArrayListExtra("halfOr"));
        dishPrice = new ArrayList<>(getIntent().getStringArrayListExtra("dishPrice"));
        Log.i("info",dishPrice.toString());
        image = new ArrayList<>(getIntent().getStringArrayListExtra("image"));
        type = new ArrayList<>(getIntent().getStringArrayListExtra("type"));
        orderAndPayment = new ArrayList<>(getIntent().getStringArrayListExtra("orderAndPayment"));
        String str = orderAndPayment.toString().replace("[","").replace("]","");

         AsyncTask.execute(() -> {
             for(int i=0;i<type.size();i++){
                 DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(state).child(sharedPreferences.getString("locality","")).child(auth.getUid()).child("List of Dish");
                 databaseReference.child(type.get(i)).child(dishName.get(i)).addListenerForSingleValueEvent(new ValueEventListener() {
                     @Override
                     public void onDataChange(@NonNull DataSnapshot snapshot) {


                         if(Objects.equals(snapshot.child("dishType").getValue(String.class), "Veg"))
                             veg++;
                         else if(Objects.equals(snapshot.child("dishType").getValue(String.class), "NonVeg"))
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



        String[] orderPaymentArr = str.split(",");
        amountPaidByUser = orderPaymentArr[2];
//        Toast.makeText(this, "" + amountPaidByUser, Toast.LENGTH_SHORT).show();
        Log.i("name",dishName.toString());
        decline = findViewById(R.id.declineTakeAwayButton);
        listView = findViewById(R.id.quantityTakeAwayListView);
        halfOrList = findViewById(R.id.halfOrTakeAwayListView);
        dishNames = findViewById(R.id.DishNamesTakeAwayListView);
        approve = findViewById(R.id.approveTakeAwayButton);
        path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        if(System.currentTimeMillis() - Long.parseLong(time) >= 600000){
            AlertDialog.Builder builder = new AlertDialog.Builder(ApproveCurrentTakeAway.this);
            RequestQueue requestQueue = Volley.newRequestQueue(ApproveCurrentTakeAway.this);
            JSONObject main = new JSONObject();
//            new GenratePDF().execute();
            FirebaseAuth auth = FirebaseAuth.getInstance();
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(state).child(sharedPreferences.getString("locality","")).child(Objects.requireNonNull(auth.getUid())).child("Current TakeAway").child(id);
            String approveTime = String.valueOf(System.currentTimeMillis());
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Users").child(id).child("Recent Orders").child(time);
            if (paymentMode.equals("online")) {
                for(int i=0;i<dishName.size();i++){
                    MyClass myClass = new MyClass(dishName.get(i),dishPrice.get(i),image.get(i),type.get(i),""+approveTime,quantity.get(i),halfOr.get(i),state,String.valueOf(orderAmount),orderId,"TakeAway,Online","Order Declined",sharedPreferences.getString("locality",""));
                    databaseReference.child(auth.getUid()).child(dishName.get(i)).setValue(myClass);
                }


                databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(state).child(sharedPreferences.getString("locality","")).child(auth.getUid());
                for(int i=0;i<dishName.size();i++){
                    MyClass myClass = new MyClass(dishName.get(i),dishPrice.get(i),image.get(i),type.get(i),""+approveTime,quantity.get(i),halfOr.get(i),state,String.valueOf(orderAmount),orderId,"TakeAway,Online","Order Declined",sharedPreferences.getString("locality",""));
                    databaseReference.child("Recent Orders").child("" + time).child(id).child(dishName.get(i)).setValue(myClass);
                }
//                new InitiateRefund().execute();
                DatabaseReference requestRefundOrdinalo = FirebaseDatabase.getInstance().getReference().getRoot().child("Complaints").child("RefundRequest").child(auth.getUid());
                RequestRefundClass requestRefundClass = new RequestRefundClass(orderId,orderAmount,time,"Order Cancelled because not approved/denied by restaurant");
                requestRefundOrdinalo.setValue(requestRefundClass);

                runOnUiThread(() -> {
                    Toast.makeText(ApproveCurrentTakeAway.this, "Refund Request Initiated", Toast.LENGTH_SHORT).show();
                });
                try {
                    main.put("to", "/topics/" + id + "");
                    JSONObject notification = new JSONObject();
                    notification.put("title", "Order Declined");
                    notification.put("click_action", "Table Frag");
                    notification.put("body", "Your order is cancelled automatically because it was neither accepted nor denied. You can download updated invoice from my orders for future reference. Refund will be initiated Shortly");
                    main.put("notification", notification);

                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, main, response -> {

                    }, error -> Toast.makeText(ApproveCurrentTakeAway.this, error.getLocalizedMessage() + "null", Toast.LENGTH_SHORT).show()) {
                        @Override
                        public Map<String, String> getHeaders() {
                            Map<String, String> header = new HashMap<>();
                            header.put("content-type", "application/json");
                            header.put("authorization", "key=AAAAjq_WsHs:APA91bGZV-uH-NJddxIniy8h1tDGDHqxhgvFdyNRDaV_raxjvSM_FkKu7JSwtSp4Q_iSmPuTKGGIB2M_07c9rKgPXUH43-RzpK6zkaSaIaNgmeiwUO40rYxYUZAkKoLAQQVeVJ7mXboD");
                            return header;
                        }
                    };
                    reference.removeValue();
                    userRef.child("digitCode").removeValue();
                    requestQueue.add(jsonObjectRequest);
                } catch (Exception e) {
                    Toast.makeText(ApproveCurrentTakeAway.this, e.getLocalizedMessage() + "null", Toast.LENGTH_SHORT).show();
                }
            } else {
                for(int i=0;i<dishName.size();i++){
                    MyClass myClass = new MyClass(dishName.get(i),dishPrice.get(i),image.get(i),type.get(i),""+approveTime,quantity.get(i),halfOr.get(i),state,String.valueOf(orderAmount),orderId,"TakeAway,Cash","Order Declined",sharedPreferences.getString("locality",""));
                    databaseReference.child(auth.getUid()).child(dishName.get(i)).setValue(myClass);
                }


                databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(state).child(sharedPreferences.getString("locality","")).child(auth.getUid());
                for(int i=0;i<dishName.size();i++){
                    MyClass myClass = new MyClass(dishName.get(i),dishPrice.get(i),image.get(i),type.get(i),""+approveTime,quantity.get(i),halfOr.get(i),state,String.valueOf(orderAmount),orderId,"TakeAway,Cash","Order Declined",sharedPreferences.getString("locality",""));
                    databaseReference.child("Recent Orders").child("" + time).child(id).child(dishName.get(i)).setValue(myClass);
                }
                try {
                    main.put("to", "/topics/" + id + "");
                    JSONObject notification = new JSONObject();
                    notification.put("title", "Order Declined");
                    notification.put("click_action", "Table Frag");
                    notification.put("body", "Your order is declined by the owner. If you paid cash already then ask owner to refund it");
                    main.put("notification", notification);

                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, main, response -> {

                    }, error -> Toast.makeText(ApproveCurrentTakeAway.this, error.getLocalizedMessage() + "null", Toast.LENGTH_SHORT).show()) {
                        @Override
                        public Map<String, String> getHeaders() {
                            Map<String, String> header = new HashMap<>();
                            header.put("content-type", "application/json");
                            header.put("authorization", "key=AAAAjq_WsHs:APA91bGZV-uH-NJddxIniy8h1tDGDHqxhgvFdyNRDaV_raxjvSM_FkKu7JSwtSp4Q_iSmPuTKGGIB2M_07c9rKgPXUH43-RzpK6zkaSaIaNgmeiwUO40rYxYUZAkKoLAQQVeVJ7mXboD");
                            return header;
                        }
                    };
                    reference.removeValue();
                    requestQueue.add(jsonObjectRequest);
                } catch (Exception e) {
                    Toast.makeText(ApproveCurrentTakeAway.this, e.getLocalizedMessage() + "null", Toast.LENGTH_SHORT).show();
                }
            }
           builder.setTitle("Info").setMessage("Order has been automatically cancelled because it was neither accepted nor denied")
                   .setPositiveButton("Exit", (dialogInterface, i) -> new Handler().postDelayed(this::finish,300)).create();
            builder.setCancelable(false);
            builder.show();
        }

        showCustom.setOnClickListener(click -> {
            AlertDialog.Builder alert = new AlertDialog.Builder(ApproveCurrentTakeAway.this);
            alert.setTitle("Customisation").setMessage("User has requested for following customisation to his/her order\n\n\n" + customisation).setPositiveButton("Exit", (dialogInterface, i) -> dialogInterface.dismiss()).create();

            alert.show();
        });

        if(!customisation.equals("")){
            AlertDialog.Builder alert = new AlertDialog.Builder(ApproveCurrentTakeAway.this);
            alert.setTitle("Customisation").setMessage("User has requested for following customisation to his/her order\n\n\n" + customisation).setPositiveButton("Exit", (dialogInterface, i) -> {
                dialogInterface.dismiss();
                showCustom.setVisibility(View.VISIBLE);
            }).create();

            alert.show();
        }
        DatabaseReference totalOrders = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(sharedPreferences.getString("state","")).child(sharedPreferences.getString("locality","")).child(Objects.requireNonNull(auth.getUid()));
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                digitCode = String.valueOf(snapshot.child("digitCode").getValue());
                userName = String.valueOf(snapshot.child("name").getValue());
                userEmail = String.valueOf(snapshot.child("email").getValue());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, quantity);
        listView.setAdapter(arrayAdapter);

        ArrayAdapter<String> arrayAdapter1 = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, dishName);
        dishNames.setAdapter(arrayAdapter1);

        ArrayAdapter<String> arrayAdapter2 = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, halfOr);
        halfOrList.setAdapter(arrayAdapter2);

        approve.setOnClickListener(view -> {
            try {

                approve.setEnabled(false);
                if (paymentMode.equals("cash")) {

                    Log.i("infose", veg + " " + nonVeg + " " + vegan);
                    SharedPreferences checkPrem = getSharedPreferences("AdminPremiumDetails", MODE_PRIVATE);
                    if (checkPrem.contains("status") && checkPrem.getString("status", "").equals("active")) {
//                    AsyncTask.execute(() -> {
//
//                    });
                        String month = monthName[calendar.get(Calendar.MONTH)];
                        new KAlertDialog(ApproveCurrentTakeAway.this, KAlertDialog.WARNING_TYPE)
                                .setTitleText("Warning")
                                .setContentText("Approve order only after you received cash payment")
                                .setConfirmText("Confirm Order")
                                .setConfirmClickListener(kAlertDialog -> {
                                    kAlertDialog.dismissWithAnimation();

//                                    SharedPreferences dailyUserFrequencyShared = getSharedPreferences("DailyUserFrequencyPref",MODE_PRIVATE);
//                                    SharedPreferences.Editor dailyUserEdit = dailyUserFrequencyShared.edit();
//
//                                    if(dailyUserFrequencyShared.contains(month)){
//                                        java.lang.reflect.Type type = new TypeToken<HashMap<String,String>>() {
//                                        }.getType();
//
//                                        gson = new Gson();
//                                        json = userFrequency.getString(month,"");
//                                        HashMap<String,String> map = gson.fromJson(json,type);
//                                        int day = calendar.get(Calendar.DAY_OF_MONTH);
//
//                                        if(map.containsKey(day + "")){
//                                            int val = Integer.parseInt(map.get(day + ""));
//                                            val++;
//                                        }
//                                    }

//                                    }).start();
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
//                                    Calendar cal = Calendar.getInstance();
//                                    cal.setTime(new Date());
//                                    DateFormat formatter = new SimpleDateFormat("EEEE");
//                                    String dayOfWeekString = formatter.format(cal.getTime());
//                                    restaurantTrackEditor.putString("currentDay",dayOfWeekString);
                                    updateTotalAmountValueDB(String.valueOf(orderAmount));
                                    restaurantTrackEditor.apply();

                                    FirebaseAuth auth = FirebaseAuth.getInstance();
                                    DatabaseReference trackTotalCash = FirebaseDatabase.getInstance().getReference().getRoot().child("Admin").child(Objects.requireNonNull(auth.getUid()));
                                    trackTotalCash.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.hasChild("totalCashTakeAway")) {
                                                double prevAmount = Double.parseDouble(String.valueOf(snapshot.child("totalCashTakeAway").getValue()));
                                                double currAmount = Double.parseDouble(orderAmount);
                                                trackTotalCash.child("totalCashTakeAway").setValue(String.valueOf(currAmount + prevAmount));
                                            } else {
                                                trackTotalCash.child("totalCashTakeAway").setValue(String.valueOf(orderAmount));
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });


                                    String approveTime = String.valueOf(System.currentTimeMillis());
                                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Users").child(id).child("Recent Orders").child(time);
                                    try {
                                        for (int i = 0; i < dishName.size(); i++) {
                                            MyClass myClass = new MyClass(dishName.get(i), dishPrice.get(i), image.get(i), type.get(i), "" + approveTime, quantity.get(i), halfOr.get(i), state, String.valueOf(orderAmount), orderId, "TakeAway,Cash", "Order Approved", sharedPreferences.getString("locality", ""));
                                            databaseReference.child(auth.getUid()).child(dishName.get(i)).setValue(myClass);
                                        }

                                        databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(state).child(sharedPreferences.getString("locality", "")).child(auth.getUid());
                                        for (int i = 0; i < dishName.size(); i++) {
                                            MyClass myClass = new MyClass(dishName.get(i), dishPrice.get(i), image.get(i), type.get(i), "" + approveTime, quantity.get(i), halfOr.get(i), state, String.valueOf(orderAmount), orderId, "TakeAway,Cash", "Order Approved", sharedPreferences.getString("locality", ""));
                                            databaseReference.child("Recent Orders").child("" + time).child(id).child(dishName.get(i)).setValue(myClass);
                                        }
                                    }catch (Exception ignored){

                                    }



                                    if (storeOrdersForAdminInfo.contains(month)) {
                                        java.lang.reflect.Type type = new TypeToken<List<List<String>>>() {
                                        }.getType();
                                        gson = new Gson();
                                        json = storeOrdersForAdminInfo.getString(month, "");
                                        List<List<String>> mainDataList = gson.fromJson(json, type);
                                        List<String> date = new ArrayList<>(mainDataList.get(0));
                                        List<String> transID = new ArrayList<>(mainDataList.get(1));
                                        List<String> userID = new ArrayList<>(mainDataList.get(2));
                                        List<String> orderAmountList = new ArrayList<>(mainDataList.get(3));
//                                    HashMap<String,String> map = new HashMap<String,String>(mainDataList.get(4));

                                        date.add(time);
                                        transID.add("Cash");
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
                                        int day = calendar.get(Calendar.DAY_OF_MONTH);
                                        type = new TypeToken<List<List<String>>>() {
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

                                        Log.i("myInfo", storeNewList.toString());
                                        Log.i("myInfo", newList.toString());
                                    } else {
                                        List<List<String>> mainDataList = new ArrayList<>();
                                        List<String> date = new ArrayList<>();
                                        List<String> transID = new ArrayList<>();
                                        List<String> userID = new ArrayList<>();
                                        List<String> orderAmountList = new ArrayList<>();

                                        date.add(time);
                                        transID.add("Cash");
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
                                        int day = calendar.get(Calendar.DAY_OF_MONTH);
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
                                        @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                                        Date date = new Date(Long.parseLong(time));
                                        cell = row.createCell(0);
                                        cell.setCellValue(dateFormat.format(date));
                                        cell = row.createCell(1);
                                        cell.setCellValue(transactionIdForExcel);
                                        cell = row.createCell(2);
                                        cell.setCellValue("Cash");
                                        cell = row.createCell(3);
                                        cell.setCellValue("\u20B9" + orderAmount);
                                        Log.i("info", max + "");
                                        FileOutputStream fileOutputStream = new FileOutputStream(file);
                                        workbook.write(fileOutputStream);
                                        fileOutputStream.flush();
                                        fileOutputStream.close();
                                        Toast.makeText(ApproveCurrentTakeAway.this, "Completed", Toast.LENGTH_SHORT).show();
                                        workbook.close();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }


                                    new hugeBackgroundWork().execute();
                                    new Thread(() -> {

                                        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference().getRoot().child("Complaints").child("ResAnalysis").child(state).child(sharedPreferences.getString("locality", ""));
                                        databaseReference1.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if (snapshot.exists()) {
                                                    if (snapshot.hasChild("foodTakeAway")) {
                                                        int curVal = Integer.parseInt(Objects.requireNonNull(snapshot.child("foodTakeAway").getValue(String.class)));
                                                        curVal++;
                                                        databaseReference1.child("foodTakeAway").setValue(curVal + "");
                                                    } else
                                                        databaseReference1.child("foodTakeAway").setValue("1");
                                                } else {
                                                    databaseReference1.child("foodTakeAway").setValue("1");
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });

                                        if(dishDailyTrackForReports.contains(month)){
                                            gson = new Gson();
                                            java.lang.reflect.Type type = new TypeToken<HashMap<Integer, HashMap<String, Integer>>>() {
                                            }.getType();

                                            HashMap<Integer , HashMap<String,Integer>> mainMap = gson.fromJson(dishDailyTrackForReports.getString(month,""),type);
                                            int day = calendar.get(Calendar.DAY_OF_MONTH);
                                            if(mainMap.containsKey(day)){
                                                HashMap<String, Integer> innerMap = new HashMap<>(mainMap.get(day));

                                                for(int l = 0;l< dishName.size();l++){
                                                    if(innerMap.containsKey(dishName.get(l))){
                                                        int prev = innerMap.get(dishName.get(l));
                                                        prev += Integer.parseInt(quantity.get(l));
                                                        innerMap.put(dishName.get(l),prev);
                                                    }else
                                                        innerMap.put(dishName.get(l),Integer.parseInt(quantity.get(l)));
                                                }

                                                mainMap.put(day,innerMap);
                                            }else{
                                                HashMap<String, Integer> innerMap = new HashMap<>();
                                                for(int l=0;l < dishName.size();l++){
                                                    innerMap.put(dishName.get(l),Integer.parseInt(quantity.get(l)));
                                                }

                                                mainMap.put(day,innerMap);
                                            }

                                            dailyReportTrackDish.putString(month,gson.toJson(mainMap));
                                            dailyReportTrackDish.apply();

                                        }else{
                                            int day = calendar.get(Calendar.DAY_OF_MONTH);
                                            HashMap<String, Integer> innerMap = new HashMap<>();
                                            gson = new Gson();
                                            for(int l=0;l < dishName.size();l++){
                                                innerMap.put(dishName.get(l),Integer.parseInt(quantity.get(l)));
                                            }

                                            HashMap<Integer , HashMap<String,Integer>> mainMap = new HashMap<>();
                                            mainMap.put(day,innerMap);

                                            dailyReportTrackDish.putString(month,gson.toJson(mainMap));
                                            dailyReportTrackDish.apply();
                                        }

                                        SharedPreferences dailyInsightStoring = getSharedPreferences("DailyInsightsStoringData",MODE_PRIVATE);
                                        SharedPreferences.Editor dailyInsightEditor = dailyInsightStoring.edit();

                                        if(dailyInsightStoring.contains(month)) {
                                            gson = new Gson();
                                            java.lang.reflect.Type type = new TypeToken<HashMap<String, HashMap<String, String>>>() {
                                            }.getType();
                                            HashMap<String, HashMap<String, String>> mainMap = gson.fromJson(dailyInsightStoring.getString(month, ""), type);
                                            int day = calendar.get(Calendar.DAY_OF_MONTH);

                                            if (mainMap.containsKey(day + "")) {
                                                HashMap<String, String> innerMap = new HashMap<>(mainMap.get(day + ""));
                                                java.lang.reflect.Type ordersType = new TypeToken<List<String>>() {
                                                }.getType();

                                                List<String> ordersList = new ArrayList<>(gson.fromJson(innerMap.get("orderList"), ordersType));
                                                ordersList.add(System.currentTimeMillis() + "");

                                                innerMap.put("orderList", gson.toJson(ordersList));

                                                List<String> custList = new ArrayList<>(gson.fromJson(innerMap.get("custList"), ordersType));
                                                if (!custList.contains(id)) {
                                                    custList.add(id);
                                                    innerMap.put("custList", gson.toJson(custList));
                                                }

                                                java.lang.reflect.Type timeZoneMap = new TypeToken<HashMap<String, Integer>>() {
                                                }.getType();

                                                HashMap<String, Integer> timeMap = new HashMap<>(gson.fromJson(innerMap.get("timeZoneMap"), timeZoneMap));
                                                int hours = calendar.get(Calendar.HOUR_OF_DAY);

                                                if (timeMap.containsKey(hours + "")) {
                                                    int prev = timeMap.get(hours + "");
                                                    prev++;
                                                    timeMap.put(hours + "", prev);
                                                } else
                                                    timeMap.put(hours + "", 1);

                                                innerMap.put("timeZoneMap",gson.toJson(timeMap));

                                                double prevVal = Double.parseDouble(innerMap.get("revenueTotal"));
                                                prevVal += Double.parseDouble(orderAmount);
                                                innerMap.put("revenueTotal", prevVal + "");

                                                java.lang.reflect.Type timeZoneRevenue = new TypeToken<HashMap<String, String>>() {
                                                }.getType();

                                                HashMap<String,String> revenueMap = new HashMap<>(gson.fromJson(innerMap.get("revenueMap"),timeZoneRevenue));

                                                if(revenueMap.containsKey(hours + "")){
                                                    double prevs = Double.parseDouble(revenueMap.get(hours + ""));
                                                    prevs += Double.parseDouble(orderAmount);
                                                    revenueMap.put(hours + "",prevs + "");
                                                }else
                                                    revenueMap.put(hours + "",orderAmount);

                                                innerMap.put("revenueMap",gson.toJson(revenueMap));

                                                mainMap.put(day + "",innerMap);

                                            }else{
                                                gson = new Gson();
                                                HashMap<String,String> innerMap = new HashMap<>();
                                                List<String> ordersList = new ArrayList<>();
                                                ordersList.add(System.currentTimeMillis() + "");

                                                innerMap.put("orderList",gson.toJson(ordersList));

                                                List<String> custList = new ArrayList<>();
                                                custList.add(id);
                                                innerMap.put("custList",gson.toJson(custList));

                                                HashMap<String,Integer> timeMap = new HashMap<>();
                                                int hours = calendar.get(Calendar.HOUR_OF_DAY);
                                                timeMap.put(hours + "",1);
                                                innerMap.put("timeZoneMap",gson.toJson(timeMap));

                                                innerMap.put("revenueTotal",orderAmount);

                                                HashMap<String,String> revenueMap = new HashMap<>();
                                                revenueMap.put(hours + "",orderAmount);
                                                innerMap.put("revenueMap",gson.toJson(revenueMap));

                                                mainMap.put(day + "",innerMap);
                                            }

                                            dailyInsightEditor.putString(month,gson.toJson(mainMap));
                                            dailyInsightEditor.apply();


                                        }else{
                                            int day = calendar.get(Calendar.DAY_OF_MONTH);
                                            gson = new Gson();
                                            HashMap<String,String> innerMap = new HashMap<>();
                                            List<String> ordersList = new ArrayList<>();
                                            ordersList.add(System.currentTimeMillis() + "");

                                            innerMap.put("orderList",gson.toJson(ordersList));

                                            List<String> custList = new ArrayList<>();
                                            custList.add(id);
                                            innerMap.put("custList",gson.toJson(custList));

                                            HashMap<String,Integer> timeMap = new HashMap<>();
                                            int hours = calendar.get(Calendar.HOUR_OF_DAY);
                                            timeMap.put(hours + "",1);
                                            innerMap.put("timeZoneMap",gson.toJson(timeMap));

                                            innerMap.put("revenueTotal",orderAmount);

                                            HashMap<String,String> revenueMap = new HashMap<>();
                                            revenueMap.put(hours + "",orderAmount);
                                            innerMap.put("revenueMap",gson.toJson(revenueMap));

                                            HashMap<String,HashMap<String,String>> mainMap = new HashMap<>();
                                            mainMap.put(day + "",innerMap);

                                            dailyInsightEditor.putString(month,gson.toJson(mainMap));
                                            dailyInsightEditor.apply();

                                        }

                                        if (storeForDishAnalysis.contains("DishAnalysisMonthBasis")) {
                                            gson = new Gson();
                                            java.lang.reflect.Type type = new TypeToken<HashMap<String, HashMap<String, Integer>>>() {
                                            }.getType();
                                            String storedHash = storeForDishAnalysis.getString("DishAnalysisMonthBasis", "");
                                            HashMap<String, HashMap<String, Integer>> myMap = gson.fromJson(storedHash, type);
                                            HashMap<String, Integer> map;

                                            if (myMap.containsKey(month)) {
                                                map = new HashMap<>(Objects.requireNonNull(myMap.get(month)));
                                                Log.i("checking", map.toString());
                                                for (int k = 0; k < dishName.size(); k++) {
                                                    if (map.containsKey(dishName.get(k))) {
                                                        String currDishName = dishName.get(k);
                                                        int val = Objects.requireNonNull(map.get(dishName.get(k)));
                                                        val = val + Integer.parseInt(quantity.get(k));
                                                        map.put(dishName.get(k), val);

//                                                        if(dishShared.contains(dishName.get(k))){
//                                                            java.lang.reflect.Type type1 = new TypeToken<HashMap<String,Integer>>(){}.getType();
//                                                            HashMap<String,Integer> dishMapIndividual = gson.fromJson(dishShared.getString(dishName.get(k),""),type1);
//                                                            for(int l=0;l<dishName.size();l++){
//                                                                if(!dishName.equals(currDishName)){
//                                                                    if(dishMapIndividual.containsKey(dishName.get(l))){
//                                                                        int prev = dishMapIndividual.get(dishName.get(l));
//                                                                        prev++;
//                                                                        dishMapIndividual.put(dishName.get(l),prev);
//                                                                    }else
//                                                                        dishMapIndividual.put(dishName.get(l),1);
//                                                                }
//                                                            }
//                                                            dishSharedEdit.putString(currDishName,gson.toJson(dishMapIndividual));
//                                                        }else{
//                                                            HashMap<String,Integer> dishMapIndividual = new HashMap<>();
//                                                            for(int l=0;l<dishName.size();l++){
//                                                                if(!dishName.equals(currDishName))
//                                                                dishMapIndividual.put(dishName.get(l),1);
//                                                            }
//
//                                                            dishSharedEdit.putString(currDishName,gson.toJson(dishMapIndividual));
//                                                        }
                                                    } else {
                                                        map.put(dishName.get(k), Integer.parseInt(quantity.get(k)));
                                                    }
                                                }
                                            } else {
                                                map = new HashMap<>();
                                                for (int i = 0; i < dishName.size(); i++) {
                                                    map.put(dishName.get(i), Integer.parseInt(quantity.get(i)));
                                                }
                                            }
                                            myMap.put(month, map);

                                            dishAnalysis.putString("DishAnalysisMonthBasis", gson.toJson(myMap));
                                        } else {
                                            HashMap<String, HashMap<String, Integer>> map = new HashMap<>();
                                            HashMap<String, Integer> myMap = new HashMap<>();
                                            for (int j = 0; j < dishName.size(); j++) {
                                                myMap.put(dishName.get(j), Integer.parseInt(quantity.get(j)));
                                            }
                                            map.put(month, myMap);
                                            gson = new Gson();
                                            dishAnalysis.putString("DishAnalysisMonthBasis", gson.toJson(map));
                                        }
                                        dishAnalysis.apply();
                                        SharedPreferences lastMonthReport = getSharedPreferences("lastMonthlyReport",MODE_PRIVATE);
                                        SharedPreferences.Editor editorMonthly = lastMonthReport.edit();
                                        SharedPreferences last7daysReport = getSharedPreferences("last7daysReport",MODE_PRIVATE);
                                        SharedPreferences.Editor last7daysReportEdit = last7daysReport.edit();
//                                        new Thread(() -> {
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

//                                        }).start();

                                        if(dailyUserTrackingFor7days.contains(month)){
                                            gson = new Gson();
                                            java.lang.reflect.Type type = new TypeToken<HashMap<String,HashMap<String,Integer>>>() {
                                            }.getType();
                                            HashMap<String,HashMap<String,Integer>> mainMap = gson.fromJson(dailyUserTrackingFor7days.getString(month,""),type);
                                            int day = calendar.get(Calendar.DAY_OF_MONTH);
                                            HashMap<String, Integer> integerHashMap;
                                            if(mainMap.containsKey(day + "")){
                                                integerHashMap = new HashMap<>(mainMap.get(day + ""));
                                                if(integerHashMap.containsKey(id)){
                                                    int prev = integerHashMap.get(id);
                                                    prev++;
                                                    integerHashMap.put(id,prev);
                                                }else
                                                    integerHashMap.put(id,1);

                                            }else{
                                                integerHashMap = new HashMap<>();
                                                integerHashMap.put(id,1);
                                            }
                                            mainMap.put(day + "",integerHashMap);
                                            user7daysEdit.putString(month,gson.toJson(mainMap));
                                            user7daysEdit.apply();
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
                                        java.lang.reflect.Type type1 = new TypeToken<HashMap<String,HashMap<String,Integer>>>(){}.getType();

                                        HashMap<String,HashMap<String,Integer>> mapDishMain;
                                        SharedPreferences dishShared = getSharedPreferences("DishOrderedWithOthers",MODE_PRIVATE);
                                        SharedPreferences.Editor dishSharedEdit = dishShared.edit();
                                        if(dishShared.contains(month)){
                                            mapDishMain = gson.fromJson(dishShared.getString(month,""),type1);
                                            HashMap<String,Integer> innerMap;

                                            for(int m=0;m<dishName.size();m++){
                                                if(mapDishMain.containsKey(dishName.get(m))){
                                                    innerMap = new HashMap<>(mapDishMain.get(dishName.get(m)));
                                                    for(int i=0;i<dishName.size();i++){
                                                        if(innerMap.containsKey(dishName.get(i))){
                                                            int prev = innerMap.get(dishName.get(i));
                                                            prev++;
                                                            innerMap.put(dishName.get(i),prev);
                                                        }else
                                                            innerMap.put(dishName.get(i),1);
                                                    }
                                                }else{
                                                    innerMap = new HashMap<>();
                                                    for(int i=0;i<dishName.size();i++)
                                                        innerMap.put(dishName.get(i),1);
                                                }


                                                mapDishMain.put(dishName.get(m),innerMap);
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
                                            for(int l=0;l<dishName.size();l++){
                                                for(int i=0;i<dishName.size();i++)
                                                    innerMap.put(dishName.get(i),1);

                                                mapDishMain.put(dishName.get(l),innerMap);
                                            }

                                        }

                                        dishSharedEdit.putString(month,gson.toJson(mapDishMain));
                                        dishSharedEdit.apply();

                                        Calendar calendar = Calendar.getInstance();
                                        int yearCurrent = calendar.get(Calendar.YEAR);
//                                    new Thread(() -> {
                                        if (userFrequency.contains(month)) {
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
                                        } else {
                                            HashMap<String, String> map = new HashMap<>();
                                            map.put(id, "1");

                                            gson = new Gson();
                                            json = gson.toJson(map);

                                        }
                                        userFedit.putString(month, json);
                                        userFedit.apply();


                                        if (trackingOfTakeAway.contains(month)) {
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
                                        } else {
                                            gson = new Gson();
                                            HashMap<String, String> map = new HashMap<>();
                                            map.put(calendar.get(Calendar.DAY_OF_MONTH) + "", "1");
                                            json = gson.toJson(map);
                                        }
                                        trackingDineAndWay.putString(month, json);
                                        trackingDineAndWay.apply();


                                    }).start();
                                    //


                                    Toast.makeText(ApproveCurrentTakeAway.this, "Order Confirmed", Toast.LENGTH_SHORT).show();
                                    RequestQueue requestQueue = Volley.newRequestQueue(ApproveCurrentTakeAway.this);
                                    JSONObject main = new JSONObject();

                                    CashTransactionClass cashTransactionClass = new CashTransactionClass(orderId, orderAmount, time, id);
                                    DatabaseReference saveOrderInfo = FirebaseDatabase.getInstance().getReference().getRoot().child("Admin").child(Objects.requireNonNull(auth.getUid()));
                                    saveOrderInfo.child("Cash Transactions").child(time).setValue(cashTransactionClass);
                                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(state).child(sharedPreferences.getString("locality", "")).child(Objects.requireNonNull(auth.getUid())).child("Current TakeAway").child(id);

                                    try {
                                        main.put("to", "/topics/" + id + "");
                                        JSONObject notification = new JSONObject();
                                        notification.put("title", "Order Confirmed");
                                        notification.put("click_action", "Table Frag");
                                        notification.put("body", "Your order is confirmed by the owner");
                                        main.put("notification", notification);

                                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, main, response -> {

                                        }, error -> Toast.makeText(ApproveCurrentTakeAway.this, error.getLocalizedMessage() + "null", Toast.LENGTH_SHORT).show()) {
                                            @Override
                                            public Map<String, String> getHeaders() {
                                                Map<String, String> header = new HashMap<>();
                                                header.put("content-type", "application/json");
                                                header.put("authorization", "key=AAAAjq_WsHs:APA91bGZV-uH-NJddxIniy8h1tDGDHqxhgvFdyNRDaV_raxjvSM_FkKu7JSwtSp4Q_iSmPuTKGGIB2M_07c9rKgPXUH43-RzpK6zkaSaIaNgmeiwUO40rYxYUZAkKoLAQQVeVJ7mXboD");
                                                return header;
                                            }
                                        };
                                        reference.removeValue();
                                        requestQueue.add(jsonObjectRequest);
                                    } catch (Exception e) {
                                        Toast.makeText(ApproveCurrentTakeAway.this, e.getLocalizedMessage() + "null", Toast.LENGTH_SHORT).show();
                                    }

                                    DatabaseReference databaseReferences = FirebaseDatabase.getInstance().getReference().getRoot().child("Complaints").child("ResAnalysis").child(state).child(sharedPreferences.getString("locality", ""));
                                    databaseReferences.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.exists()) {
                                                if (snapshot.hasChild("veg")) {
                                                    int vegVal = Integer.parseInt(String.valueOf(snapshot.child("veg").getValue()));
                                                    vegVal += veg;
                                                    databaseReferences.child("veg").setValue(vegVal + "");
                                                } else
                                                    databaseReferences.child("veg").setValue(veg + "");

                                                if (snapshot.hasChild("NonVeg")) {
                                                    int vegVal = Integer.parseInt(String.valueOf(snapshot.child("NonVeg").getValue()));
                                                    vegVal += nonVeg;
                                                    databaseReferences.child("NonVeg").setValue(vegVal + "");
                                                } else
                                                    databaseReferences.child("NonVeg").setValue(nonVeg + "");

                                                if (snapshot.hasChild("vegan")) {
                                                    int vegVal = Integer.parseInt(String.valueOf(snapshot.child("vegan").getValue()));
                                                    vegVal += vegan;
                                                    databaseReferences.child("vegan").setValue(vegVal + "");
                                                } else
                                                    databaseReferences.child("vegan").setValue(vegan + "");

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
                                                    databaseReferences.child("veg").setValue(veg + "");

                                                if (!(vegan == 0))
                                                    databaseReferences.child("vegan").setValue(vegan + "");

                                                if (!(nonVeg == 0))
                                                    databaseReferences.child("NonVeg").setValue(nonVeg + "");
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                    new Handler().postDelayed(this::finish, 1500);
                                }).setCancelText("No Wait")
                                .setCancelClickListener(KAlertDialog::dismissWithAnimation).show();
                    } else {
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
                                        if (snapshot.hasChild("foodTakeAway")) {
                                            int curVal = Integer.parseInt(Objects.requireNonNull(snapshot.child("foodTakeAway").getValue(String.class)));
                                            curVal++;
                                            databaseReference1.child("foodTakeAway").setValue(curVal + "");
                                        } else
                                            databaseReference1.child("foodTakeAway").setValue("1");
                                    } else {
                                        databaseReference1.child("foodTakeAway").setValue("1");
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        });
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
                        updateTotalAmountValueDB(String.valueOf(orderAmount));
                        restaurantTrackEditor.apply();

                        FirebaseAuth auth = FirebaseAuth.getInstance();
                        DatabaseReference trackTotalCash = FirebaseDatabase.getInstance().getReference().getRoot().child("Admin").child(Objects.requireNonNull(auth.getUid()));
                        trackTotalCash.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.hasChild("totalCashTakeAway")) {
                                    double prevAmount = Double.parseDouble(String.valueOf(snapshot.child("totalCashTakeAway").getValue()));
                                    double currAmount = Double.parseDouble(orderAmount);
                                    trackTotalCash.child("totalCashTakeAway").setValue(String.valueOf(currAmount + prevAmount));
                                } else {
                                    trackTotalCash.child("totalCashTakeAway").setValue(String.valueOf(orderAmount));
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        String approveTime = String.valueOf(System.currentTimeMillis());
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Users").child(id).child("Recent Orders").child(time);
                        for (int i = 0; i < dishName.size(); i++) {
                            MyClass myClass = new MyClass(dishName.get(i), dishPrice.get(i), image.get(i), type.get(i), "" + approveTime, quantity.get(i), halfOr.get(i), state, String.valueOf(orderAmount), orderId, "TakeAway,Cash", "Order Approved", sharedPreferences.getString("locality", ""));
                            databaseReference.child(auth.getUid()).child(dishName.get(i)).setValue(myClass);
                        }


                        databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(state).child(sharedPreferences.getString("locality", "")).child(auth.getUid());
                        for (int i = 0; i < dishName.size(); i++) {
                            MyClass myClass = new MyClass(dishName.get(i), dishPrice.get(i), image.get(i), type.get(i), "" + approveTime, quantity.get(i), halfOr.get(i), state, String.valueOf(orderAmount), orderId, "TakeAway,Cash", "Order Approved", sharedPreferences.getString("locality", ""));
                            databaseReference.child("Recent Orders").child("" + time).child(id).child(dishName.get(i)).setValue(myClass);
                        }


                        Toast.makeText(ApproveCurrentTakeAway.this, "Order Confirmed", Toast.LENGTH_SHORT).show();
                        RequestQueue requestQueue = Volley.newRequestQueue(ApproveCurrentTakeAway.this);
                        JSONObject main = new JSONObject();

                        CashTransactionClass cashTransactionClass = new CashTransactionClass(orderId, orderAmount, time, id);
                        DatabaseReference saveOrderInfo = FirebaseDatabase.getInstance().getReference().getRoot().child("Admin").child(Objects.requireNonNull(auth.getUid()));
                        saveOrderInfo.child("Cash Transactions").child(time).setValue(cashTransactionClass);
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(state).child(sharedPreferences.getString("locality", "")).child(Objects.requireNonNull(auth.getUid())).child("Current TakeAway").child(id);

                        try {
                            main.put("to", "/topics/" + id + "");
                            JSONObject notification = new JSONObject();
                            notification.put("title", "Order Confirmed");
                            notification.put("click_action", "Table Frag");
                            notification.put("body", "Your order is confirmed by the owner");
                            main.put("notification", notification);

                            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, main, response -> {

                            }, error -> Toast.makeText(ApproveCurrentTakeAway.this, error.getLocalizedMessage() + "null", Toast.LENGTH_SHORT).show()) {
                                @Override
                                public Map<String, String> getHeaders() {
                                    Map<String, String> header = new HashMap<>();
                                    header.put("content-type", "application/json");
                                    header.put("authorization", "key=AAAAjq_WsHs:APA91bGZV-uH-NJddxIniy8h1tDGDHqxhgvFdyNRDaV_raxjvSM_FkKu7JSwtSp4Q_iSmPuTKGGIB2M_07c9rKgPXUH43-RzpK6zkaSaIaNgmeiwUO40rYxYUZAkKoLAQQVeVJ7mXboD");
                                    return header;
                                }
                            };
                            reference.removeValue();
                            requestQueue.add(jsonObjectRequest);
                        } catch (Exception e) {
                            Toast.makeText(ApproveCurrentTakeAway.this, e.getLocalizedMessage() + "null", Toast.LENGTH_SHORT).show();
                        }
                        new Handler().postDelayed(this::finish, 1500);
                    }
                } else {
                    SharedPreferences loginInfo = getSharedPreferences("loginInfo", MODE_PRIVATE);


                    FastDialog fastDialog = new FastDialogBuilder(ApproveCurrentTakeAway.this, Type.DIALOG)
                            .setTitleText("OTP Code")
                            .setText("Enter 6 digit code below provided by user")
                            .setHint("Enter Code here")
                            .positiveText("Confirm")
                            .negativeText("Cancel")
                            .setAnimation(Animations.SLIDE_TOP)
                            .create();

                    fastDialog.show();

                    fastDialog.positiveClickListener(view12 -> {
                        if (fastDialog.getInputText().equals("")) {
                            Toast.makeText(ApproveCurrentTakeAway.this, "Field can't be empty", Toast.LENGTH_SHORT).show();
                            fastDialog.dismiss();
                        } else if (fastDialog.getInputText().equals(digitCode.trim())) {
                            new hugeBackgroundWork().execute();

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
                                            if (snapshot.hasChild("foodTakeAway")) {
                                                int curVal = Integer.parseInt(Objects.requireNonNull(snapshot.child("foodTakeAway").getValue(String.class)));
                                                curVal++;
                                                databaseReference1.child("foodTakeAway").setValue(curVal + "");
                                            } else
                                                databaseReference1.child("foodTakeAway").setValue("1");
                                        } else {
                                            databaseReference1.child("foodTakeAway").setValue("1");
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            });


                            updateTotalAmountValueDB(orderAmount);
                            String approveTime = String.valueOf(System.currentTimeMillis());
                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Users").child(id).child("Recent Orders").child(time);
                            for (int i = 0; i < dishName.size(); i++) {
                                MyClass myClass = new MyClass(dishName.get(i), dishPrice.get(i), image.get(i), type.get(i), "" + approveTime, quantity.get(i), halfOr.get(i), state, String.valueOf(orderAmount), orderId, "TakeAway,Online", "Order Approved", sharedPreferences.getString("locality", ""));
                                databaseReference.child(auth.getUid()).child(dishName.get(i)).setValue(myClass);
                            }


                            databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(state).child(sharedPreferences.getString("locality", "")).child(auth.getUid());
                            for (int i = 0; i < dishName.size(); i++) {
                                MyClass myClass = new MyClass(dishName.get(i), dishPrice.get(i), image.get(i), type.get(i), "" + approveTime, quantity.get(i), halfOr.get(i), state, String.valueOf(orderAmount), orderId, "TakeAway,Online", "Order Approved", sharedPreferences.getString("locality", ""));
                                databaseReference.child("Recent Orders").child("" + time).child(id).child(dishName.get(i)).setValue(myClass);
                            }

                            Toast.makeText(ApproveCurrentTakeAway.this, "Order Confirmed", Toast.LENGTH_SHORT).show();
//                            if (loginInfo.contains("payoutMethodChoosen")) {
//                                if (loginInfo.getString("payoutMethodChoosen", "").equals("imps")) {
//                                    amountToBeSend = Double.parseDouble(orderAmount);
//                                    amountToBeSend = amountToBeSend - 2;
                                    new MakePaymentToVendor().execute();
//                                }
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

//                            } else {
////                            Toast.makeText(this, "Defau", Toast.LENGTH_SHORT).show();
//                                Toast.makeText(this, "No payout method choosen\nDefault Method will be applicable", Toast.LENGTH_LONG).show();
//                                DatabaseReference updatePayoutOrder = FirebaseDatabase.getInstance().getReference().getRoot().child("Admin").child(auth.getUid());
//                                updatePayoutOrder.addListenerForSingleValueEvent(new ValueEventListener() {
//                                    @Override
//                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                        if (snapshot.hasChild("totalPayoutAmount")) {
//                                            double current = Double.parseDouble(String.valueOf(snapshot.child("totalPayoutAmount").getValue()));
//                                            current += Double.parseDouble(orderAmount);
//                                            updatePayoutOrder.child("totalPayoutAmount").setValue(String.valueOf(current));
//                                        } else {
//                                            updatePayoutOrder.child("totalPayoutAmount").setValue(String.valueOf(orderAmount));
//                                        }
//                                    }
//
//                                    @Override
//                                    public void onCancelled(@NonNull DatabaseError error) {
//
//                                    }
//                                });
//                            }
                            RequestQueue requestQueue = Volley.newRequestQueue(ApproveCurrentTakeAway.this);
                            fastDialog.dismiss();
                            JSONObject main = new JSONObject();
                            FirebaseAuth auth = FirebaseAuth.getInstance();
                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(state).child(sharedPreferences.getString("locality", "")).child(Objects.requireNonNull(auth.getUid())).child("Current TakeAway").child(id);

                            try {
                                main.put("to", "/topics/" + id + "");
                                JSONObject notification = new JSONObject();
                                notification.put("title", "Order Confirmed");
                                notification.put("click_action", "Table Frag");
                                notification.put("body", "Your order is confirmed by the owner");
                                main.put("notification", notification);

                                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, main, response -> {

                                }, error -> Toast.makeText(ApproveCurrentTakeAway.this, error.getLocalizedMessage() + "null", Toast.LENGTH_SHORT).show()) {
                                    @Override
                                    public Map<String, String> getHeaders() {
                                        Map<String, String> header = new HashMap<>();
                                        header.put("content-type", "application/json");
                                        header.put("authorization", "key=AAAAjq_WsHs:APA91bGZV-uH-NJddxIniy8h1tDGDHqxhgvFdyNRDaV_raxjvSM_FkKu7JSwtSp4Q_iSmPuTKGGIB2M_07c9rKgPXUH43-RzpK6zkaSaIaNgmeiwUO40rYxYUZAkKoLAQQVeVJ7mXboD");
                                        return header;
                                    }
                                };
                                reference.removeValue();
                                userRef.child("digitCode").removeValue();
                                requestQueue.add(jsonObjectRequest);
                            } catch (Exception e) {
                                Toast.makeText(ApproveCurrentTakeAway.this, e.getLocalizedMessage() + "null", Toast.LENGTH_SHORT).show();
                            }
                            new Handler().postDelayed(this::finish, 1500);
                        } else {
                            Toast.makeText(ApproveCurrentTakeAway.this, "Wrong Code", Toast.LENGTH_SHORT).show();
                        }
                    });


                    fastDialog.negativeClickListener(view1 -> fastDialog.dismiss());
                }
            }catch (Exception e){
                approve.setEnabled(true);
                Toast.makeText(this, "Something went wrong :(", Toast.LENGTH_SHORT).show();
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



            alert.setPositiveButton("submit", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int ij) {
                    if(!editText.getText().toString().equals("")) {
                        RequestQueue requestQueue = Volley.newRequestQueue(ApproveCurrentTakeAway.this);
                        JSONObject main = new JSONObject();
//                        new GenratePDF().execute();
                        decline.setEnabled(false);
                        FirebaseAuth auth = FirebaseAuth.getInstance();
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(state).child(sharedPreferences.getString("locality","")).child(Objects.requireNonNull(auth.getUid())).child("Current TakeAway").child(id);
                        if (paymentMode.equals("online")) {

                            AsyncTask.execute(() -> {
                                try{
                                    main.put("to","/topics/"+"RequestPayout");
                                    JSONObject notification = new JSONObject();
                                    notification.put("title","Refund Request");
                                    notification.put("body","You have a new refund request. Check now");
                                    main.put("notification",notification);

                                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, main, response -> {

                                    }, error -> Toast.makeText(ApproveCurrentTakeAway.this, error.getLocalizedMessage()+"null", Toast.LENGTH_SHORT).show()){
                                        @Override
                                        public Map<String, String> getHeaders() {
                                            Map<String,String> header = new HashMap<>();
                                            header.put("content-type","application/json");
                                            header.put("authorization","key=AAAAjq_WsHs:APA91bGZV-uH-NJddxIniy8h1tDGDHqxhgvFdyNRDaV_raxjvSM_FkKu7JSwtSp4Q_iSmPuTKGGIB2M_07c9rKgPXUH43-RzpK6zkaSaIaNgmeiwUO40rYxYUZAkKoLAQQVeVJ7mXboD");
                                            return header;
                                        }
                                    };

                                    requestQueue.add(jsonObjectRequest);

                                }
                                catch (Exception e){
                                    Toast.makeText(ApproveCurrentTakeAway.this, e.getLocalizedMessage()+"null", Toast.LENGTH_SHORT).show();
                                }
                            });
                            String approveTime = String.valueOf(System.currentTimeMillis());
                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Users").child(id).child("Recent Orders").child(time);
                            for(int i=0;i<dishName.size();i++){
                                MyClass myClass = new MyClass(dishName.get(i),dishPrice.get(i),image.get(i),type.get(i),""+approveTime,quantity.get(i),halfOr.get(i),state,String.valueOf(orderAmount),orderId,"TakeAway,Online","Order Declined",sharedPreferences.getString("locality",""));
                                databaseReference.child(auth.getUid()).child(dishName.get(i)).setValue(myClass);
                            }


                            databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(state).child(sharedPreferences.getString("locality","")).child(auth.getUid());
                            for(int i=0;i<dishName.size();i++){
                                MyClass myClass = new MyClass(dishName.get(i),dishPrice.get(i),image.get(i),type.get(i),""+approveTime,quantity.get(i),halfOr.get(i),state,String.valueOf(orderAmount),orderId,"TakeAway,Online","Order Declined",sharedPreferences.getString("locality",""));
                                databaseReference.child("Recent Orders").child("" + time).child(id).child(dishName.get(i)).setValue(myClass);
                            }
                            DatabaseReference requestRefundOrdinalo = FirebaseDatabase.getInstance().getReference().getRoot().child("Complaints").child("RefundRequest").child(id);
                            RequestRefundClass requestRefundClass = new RequestRefundClass(orderId,orderAmount,time,"Order Cancelled because denied by restaurant");
                            requestRefundOrdinalo.setValue(requestRefundClass);

                            runOnUiThread(() -> {
                                Toast.makeText(ApproveCurrentTakeAway.this, "Refund Request Initiated", Toast.LENGTH_SHORT).show();
                            });
//                            new InitiateRefund().execute();
                            try {
                                main.put("to", "/topics/" + id + "");
                                JSONObject notification = new JSONObject();
                                notification.put("title", "Order Declined");
                                notification.put("click_action", "Table Frag");
                                notification.put("body", "Your order is declined by the owner. You can download updated invoice from my orders for future reference. Refund will be initiated Shortly\n" + editText.getText().toString());
                                main.put("notification", notification);

                                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, main, response -> {

                                }, error -> Toast.makeText(ApproveCurrentTakeAway.this, error.getLocalizedMessage() + "null", Toast.LENGTH_SHORT).show()) {
                                    @Override
                                    public Map<String, String> getHeaders() throws AuthFailureError {
                                        Map<String, String> header = new HashMap<>();
                                        header.put("content-type", "application/json");
                                        header.put("authorization", "key=AAAAjq_WsHs:APA91bGZV-uH-NJddxIniy8h1tDGDHqxhgvFdyNRDaV_raxjvSM_FkKu7JSwtSp4Q_iSmPuTKGGIB2M_07c9rKgPXUH43-RzpK6zkaSaIaNgmeiwUO40rYxYUZAkKoLAQQVeVJ7mXboD");
                                        return header;
                                    }
                                };
                                reference.removeValue();
                                userRef.child("digitCode").removeValue();
                                requestQueue.add(jsonObjectRequest);
                            } catch (Exception e) {
                                Toast.makeText(ApproveCurrentTakeAway.this, e.getLocalizedMessage() + "null", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            try {
                                String approveTime = String.valueOf(System.currentTimeMillis());
                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Users").child(id).child("Recent Orders").child(time);
                                for(int i=0;i<dishName.size();i++){
                                    MyClass myClass = new MyClass(dishName.get(i),dishPrice.get(i),image.get(i),type.get(i),""+approveTime,quantity.get(i),halfOr.get(i),state,String.valueOf(orderAmount),orderId,"TakeAway,Cash","Order Declined",sharedPreferences.getString("locality",""));
                                    databaseReference.child(auth.getUid()).child(dishName.get(i)).setValue(myClass);
                                }


                                databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(state).child(sharedPreferences.getString("locality","")).child(auth.getUid());
                                for(int i=0;i<dishName.size();i++){
                                    MyClass myClass = new MyClass(dishName.get(i),dishPrice.get(i),image.get(i),type.get(i),""+approveTime,quantity.get(i),halfOr.get(i),state,String.valueOf(orderAmount),orderId,"TakeAway,Cash","Order Declined",sharedPreferences.getString("locality",""));
                                    databaseReference.child("Recent Orders").child("" + time).child(id).child(dishName.get(i)).setValue(myClass);
                                }
                                main.put("to", "/topics/" + id + "");
                                JSONObject notification = new JSONObject();
                                notification.put("title", "Order Declined");
                                notification.put("click_action", "Table Frag");
                                notification.put("body", "Your order is declined by the owner. If you paid cash already then ask owner to refund it");
                                main.put("notification", notification);

                                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, main, response -> {

                                }, error -> Toast.makeText(ApproveCurrentTakeAway.this, error.getLocalizedMessage() + "null", Toast.LENGTH_SHORT).show()) {
                                    @Override
                                    public Map<String, String> getHeaders() {
                                        Map<String, String> header = new HashMap<>();
                                        header.put("content-type", "application/json");
                                        header.put("authorization", "key=AAAAjq_WsHs:APA91bGZV-uH-NJddxIniy8h1tDGDHqxhgvFdyNRDaV_raxjvSM_FkKu7JSwtSp4Q_iSmPuTKGGIB2M_07c9rKgPXUH43-RzpK6zkaSaIaNgmeiwUO40rYxYUZAkKoLAQQVeVJ7mXboD");
                                        return header;
                                    }
                                };
                                reference.removeValue();
                                requestQueue.add(jsonObjectRequest);
                            } catch (Exception e) {
                                Toast.makeText(ApproveCurrentTakeAway.this, e.getLocalizedMessage() + "null", Toast.LENGTH_SHORT).show();
                            }
                        }
                        new Handler().postDelayed(() -> finish(), 1500);
                    }else
                        Toast.makeText(v.getContext(), "Enter reason for table cancellation", Toast.LENGTH_SHORT).show();
                }
            }).setNegativeButton("back", (dialogInterface, i) -> {

            });

            alert.create().show();

        });
    }

    private void updateTotalAmountValueDB(String valueOf) {
        storeTotalAmountOfMonth.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChild("totalMonthAmount")){
                    Double current = Double.parseDouble(valueOf);
                    Double existingVal = Double.parseDouble(String.valueOf(snapshot.child("totalMonthAmount").getValue()));
                    Double finalVal = current + existingVal;
                    storeTotalAmountOfMonth.child("totalMonthAmount").setValue(String.valueOf(finalVal));
                }else
                    storeTotalAmountOfMonth.child("totalMonthAmount").setValue(valueOf);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public class InitiateRefund extends AsyncTask<Void,Void,Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            RequestQueue requestQueue = Volley.newRequestQueue(ApproveCurrentTakeAway.this);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, response -> Log.i("res", response), error -> {

            }){
                @NonNull
                @Override
                protected Map<String, String> getParams() {
                    Map<String,String> params = new HashMap<>();
                    FirebaseAuth auth = FirebaseAuth.getInstance();
                    String referid = orderId;
                    String finalReferIDForInfo = "refund_" + referid + "s";
                    Log.i("refundID",referid);
                    String time = String.valueOf(System.currentTimeMillis());
                    CancelClass cancelClass = new CancelClass(finalReferIDForInfo,orderAmount + "",orderId + "");
                    saveRefundInfo.child("Refunds").child(time).setValue(cancelClass);
                    params.put("referID",referid + "");
                    params.put("refundAmount",orderAmount);
                    params.put("orderID",orderId);
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
            RequestQueue requestQueue = Volley.newRequestQueue(ApproveCurrentTakeAway.this);
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
            RequestQueue requestQueue = Volley.newRequestQueue(ApproveCurrentTakeAway.this);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, testBearerToken, response -> {
                Log.i("response",response);
                if(response.trim().equals("Token is valid")){

                    makePaymentToVendor.execute();
                }
            }, error -> {

            }){
                @NonNull
                @Override
                protected Map<String, String> getParams() {
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
            Calendar calendar  = Calendar.getInstance();
            SharedPreferences checkPrem = getSharedPreferences("AdminPremiumDetails",MODE_PRIVATE);
            Log.i("statusTwo", String.valueOf(makePaymentToVendor.getStatus()));
            RequestQueue requestQueue = Volley.newRequestQueue(ApproveCurrentTakeAway.this);
//            StringRequest stringRequest = new StringRequest(Request.Method.POST, testPaymentToVendor, response -> {
                if(checkPrem.contains("status") && checkPrem.getString("status","").equals("active")) {
                    String month = monthName[calendar.get(Calendar.MONTH)];

                    SharedPreferences dailyInsightStoring = getSharedPreferences("DailyInsightsStoringData",MODE_PRIVATE);
                    SharedPreferences.Editor dailyInsightEditor = dailyInsightStoring.edit();

                    if(dailyInsightStoring.contains(month)) {
                        gson = new Gson();
                        java.lang.reflect.Type type = new TypeToken<HashMap<String, HashMap<String, String>>>() {
                        }.getType();
                        HashMap<String, HashMap<String, String>> mainMap = gson.fromJson(dailyInsightStoring.getString(month, ""), type);
                        int day = calendar.get(Calendar.DAY_OF_MONTH);

                        if (mainMap.containsKey(day + "")) {
                            HashMap<String, String> innerMap = new HashMap<>(mainMap.get(day + ""));
                            java.lang.reflect.Type ordersType = new TypeToken<List<String>>() {
                            }.getType();

                            List<String> ordersList = new ArrayList<>(gson.fromJson(innerMap.get("orderList"), ordersType));
                            ordersList.add(System.currentTimeMillis() + "");

                            innerMap.put("orderList", gson.toJson(ordersList));

                            List<String> custList = new ArrayList<>(gson.fromJson(innerMap.get("custList"), ordersType));
                            if (!custList.contains(id)) {
                                custList.add(id);
                                innerMap.put("custList", gson.toJson(custList));
                            }

                            java.lang.reflect.Type timeZoneMap = new TypeToken<HashMap<String, Integer>>() {
                            }.getType();

                            HashMap<String, Integer> timeMap = new HashMap<>(gson.fromJson(innerMap.get("timeZoneMap"), timeZoneMap));
                            int hours = calendar.get(Calendar.HOUR_OF_DAY);

                            if (timeMap.containsKey(hours + "")) {
                                int prev = timeMap.get(hours + "");
                                prev++;
                                timeMap.put(hours + "", prev);
                            } else
                                timeMap.put(hours + "", 1);

                            innerMap.put("timeZoneMap",gson.toJson(timeMap));

                            double prevVal = Double.parseDouble(innerMap.get("revenueTotal"));
                            prevVal += Double.parseDouble(orderAmount);
                            innerMap.put("revenueTotal", prevVal + "");

                            java.lang.reflect.Type timeZoneRevenue = new TypeToken<HashMap<String, String>>() {
                            }.getType();

                            HashMap<String,String> revenueMap = new HashMap<>(gson.fromJson(innerMap.get("revenueMap"),timeZoneRevenue));

                            if(revenueMap.containsKey(hours + "")){
                                double prevs = Double.parseDouble(revenueMap.get(hours + ""));
                                prevs += Double.parseDouble(orderAmount);
                                revenueMap.put(hours + "",prevs + "");
                            }else
                                revenueMap.put(hours + "",orderAmount);

                            innerMap.put("revenueMap",gson.toJson(revenueMap));

                            mainMap.put(day + "",innerMap);

                        }else{
                            gson = new Gson();
                            HashMap<String,String> innerMap = new HashMap<>();
                            List<String> ordersList = new ArrayList<>();
                            ordersList.add(System.currentTimeMillis() + "");

                            innerMap.put("orderList",gson.toJson(ordersList));

                            List<String> custList = new ArrayList<>();
                            custList.add(id);
                            innerMap.put("custList",gson.toJson(custList));

                            HashMap<String,Integer> timeMap = new HashMap<>();
                            int hours = calendar.get(Calendar.HOUR_OF_DAY);
                            timeMap.put(hours + "",1);
                            innerMap.put("timeZoneMap",gson.toJson(timeMap));

                            innerMap.put("revenueTotal",orderAmount);

                            HashMap<String,String> revenueMap = new HashMap<>();
                            revenueMap.put(hours + "",orderAmount);
                            innerMap.put("revenueMap",gson.toJson(revenueMap));

                            mainMap.put(day + "",innerMap);
                        }

                        dailyInsightEditor.putString(month,gson.toJson(mainMap));
                        dailyInsightEditor.apply();


                    }else{
                        int day = calendar.get(Calendar.DAY_OF_MONTH);
                        gson = new Gson();
                        HashMap<String,String> innerMap = new HashMap<>();
                        List<String> ordersList = new ArrayList<>();
                        ordersList.add(System.currentTimeMillis() + "");

                        innerMap.put("orderList",gson.toJson(ordersList));

                        List<String> custList = new ArrayList<>();
                        custList.add(id);
                        innerMap.put("custList",gson.toJson(custList));

                        HashMap<String,Integer> timeMap = new HashMap<>();
                        int hours = calendar.get(Calendar.HOUR_OF_DAY);
                        timeMap.put(hours + "",1);
                        innerMap.put("timeZoneMap",gson.toJson(timeMap));

                        innerMap.put("revenueTotal",orderAmount);

                        HashMap<String,String> revenueMap = new HashMap<>();
                        revenueMap.put(hours + "",orderAmount);
                        innerMap.put("revenueMap",gson.toJson(revenueMap));

                        HashMap<String,HashMap<String,String>> mainMap = new HashMap<>();
                        mainMap.put(day + "",innerMap);

                        dailyInsightEditor.putString(month,gson.toJson(mainMap));
                        dailyInsightEditor.apply();

                    }


                    if(dishDailyTrackForReports.contains(month)){
                        gson = new Gson();
                        java.lang.reflect.Type type = new TypeToken<HashMap<Integer, HashMap<String, Integer>>>() {
                        }.getType();

                        HashMap<Integer , HashMap<String,Integer>> mainMap = gson.fromJson(dishDailyTrackForReports.getString(month,""),type);
                        int day = calendar.get(Calendar.DAY_OF_MONTH);
                        if(mainMap.containsKey(day)){
                            HashMap<String, Integer> innerMap = new HashMap<>(mainMap.get(day));

                            for(int l = 0;l< dishName.size();l++){
                                if(innerMap.containsKey(dishName.get(l))){
                                    int prev = innerMap.get(dishName.get(l));
                                    prev += Integer.parseInt(quantity.get(l));
                                    innerMap.put(dishName.get(l),prev);
                                }else
                                    innerMap.put(dishName.get(l),Integer.parseInt(quantity.get(l)));
                            }

                            mainMap.put(day,innerMap);
                        }else{
                            HashMap<String, Integer> innerMap = new HashMap<>();
                            for(int l=0;l < dishName.size();l++){
                                innerMap.put(dishName.get(l),Integer.parseInt(quantity.get(l)));
                            }

                            mainMap.put(day,innerMap);
                        }

                        dailyReportTrackDish.putString(month,gson.toJson(mainMap));
                        dailyReportTrackDish.apply();

                    }else{
                        int day = calendar.get(Calendar.DAY_OF_MONTH);
                        HashMap<String, Integer> innerMap = new HashMap<>();
                        gson = new Gson();
                        for(int l=0;l < dishName.size();l++){
                            innerMap.put(dishName.get(l),Integer.parseInt(quantity.get(l)));
                        }

                        HashMap<Integer , HashMap<String,Integer>> mainMap = new HashMap<>();
                        mainMap.put(day,innerMap);

                        dailyReportTrackDish.putString(month,gson.toJson(mainMap));
                        dailyReportTrackDish.apply();
                    }


                    if (storeForDishAnalysis.contains("DishAnalysisMonthBasis")) {
                        try {
                            gson = new Gson();
                            Log.i("here", "first");
                            java.lang.reflect.Type type = new TypeToken<HashMap<String, HashMap<String, Integer>>>() {
                            }.getType();
                            String storedHash = storeForDishAnalysis.getString("DishAnalysisMonthBasis", "");
                            HashMap<String, HashMap<String, Integer>> myMap = gson.fromJson(storedHash, type);
                            HashMap<String, Integer> map;
                            if (myMap.containsKey(month)) {
                                map = new HashMap<>(Objects.requireNonNull(myMap.get(month)));
                                Log.i("checking", map.toString());
                                for (int k = 0; k < dishName.size(); k++) {
                                    if (map.containsKey(dishName.get(k))) {
                                        int val = Objects.requireNonNull(map.get(dishName.get(k)));
                                        val = val + Integer.parseInt(quantity.get(k));
                                        map.put(dishName.get(k), val);
                                    } else {
                                        map.put(dishName.get(k), Integer.parseInt(quantity.get(k)));
                                    }
                                }
                            } else {
                                map = new HashMap<>();
                                for (int i = 0; i < dishName.size(); i++) {
                                    map.put(dishName.get(i), Integer.parseInt(quantity.get(i)));
                                }
                            }
                            myMap.put(month, map);
                            dishAnalysis.putString("DishAnalysisMonthBasis", gson.toJson(myMap));
                        }catch (Exception e){
                            gson = new Gson();
                            Log.i("here", "first");
                            java.lang.reflect.Type type = new TypeToken<HashMap<String, HashMap<String, Integer>>>() {
                            }.getType();
                            String storedHash = storeForDishAnalysis.getString("DishAnalysisMonthBasis", "");
                            HashMap<String, HashMap<String, Integer>> myMap = gson.fromJson(storedHash, type);
                            HashMap<String, Integer> map;
                            SharedPreferences dishShared = getSharedPreferences("DishOrderedWithOthers",MODE_PRIVATE);
                            SharedPreferences.Editor dishSharedEdit = dishShared.edit();
                            if (myMap.containsKey(month)) {
                                map = new HashMap<>(Objects.requireNonNull(myMap.get(month)));
                                Log.i("checking", map.toString());
                                for (int k = 0; k < dishName.size(); k++) {
                                    if (map.containsKey(dishName.get(k))) {
                                        int val = Objects.requireNonNull(map.get(dishName.get(k)));
                                        val++;
                                        map.put(dishName.get(k), val);
                                    } else {
                                        map.put(dishName.get(k), 1);
                                    }
                                }
                            } else {
                                map = new HashMap<>();
                                for (int i = 0; i < dishName.size(); i++) {
                                    map.put(dishName.get(i), 1);
                                }
                            }
                            myMap.put(month, map);
                            dishSharedEdit.apply();
                            dishAnalysis.putString("DishAnalysisMonthBasis", gson.toJson(myMap));
                        }
                    } else {
                        HashMap<String, HashMap<String, Integer>> map = new HashMap<>();
                        HashMap<String, Integer> myMap = new HashMap<>();
                        for (int j = 0; j < dishName.size(); j++) {
                            myMap.put(dishName.get(j), 1);
                        }
                        map.put(month, myMap);
                        gson = new Gson();
                        dishAnalysis.putString("DishAnalysisMonthBasis", gson.toJson(map));
                    }
                    dishAnalysis.apply();
//                    Log.i("response", response);

                    java.lang.reflect.Type type1 = new TypeToken<HashMap<String,HashMap<String,Integer>>>(){}.getType();

                    HashMap<String,HashMap<String,Integer>> mapDishMain;
                    //
                    SharedPreferences dishShared = getSharedPreferences("DishOrderedWithOthers",MODE_PRIVATE);
                    SharedPreferences.Editor dishSharedEdit = dishShared.edit();
                    if(dishShared.contains(month)){
                        mapDishMain = gson.fromJson(dishShared.getString(month,""),type1);
                        HashMap<String,Integer> innerMap;

                        for(int m=0;m<dishName.size();m++){
                            if(mapDishMain.containsKey(dishName.get(m))){
                                innerMap = new HashMap<>(mapDishMain.get(dishName.get(m)));
                                for(int i=0;i<dishName.size();i++){
                                    if(innerMap.containsKey(dishName.get(i))){
                                        int prev = innerMap.get(dishName.get(i));
                                        prev++;
                                        innerMap.put(dishName.get(i),prev);
                                    }else
                                        innerMap.put(dishName.get(i),1);
                                }
                            }else{
                                innerMap = new HashMap<>();
                                for(int i=0;i<dishName.size();i++)
                                    innerMap.put(dishName.get(i),1);
                            }


                            mapDishMain.put(dishName.get(m),innerMap);
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
                        for(int l=0;l<dishName.size();l++){
                            for(int i=0;i<dishName.size();i++)
                                innerMap.put(dishName.get(i),1);

                            mapDishMain.put(dishName.get(l),innerMap);
                        }

                    }
                    dishSharedEdit.putString(month,gson.toJson(mapDishMain));
                    dishSharedEdit.apply();

                    SharedPreferences lastMonthReport = getSharedPreferences("lastMonthlyReport",MODE_PRIVATE);
                    SharedPreferences.Editor editorMonthly = lastMonthReport.edit();
                    SharedPreferences last7daysReport = getSharedPreferences("last7daysReport",MODE_PRIVATE);
                    SharedPreferences.Editor last7daysReportEdit = last7daysReport.edit();
//                    new Thread(() -> {
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
//                    }).start();

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

//                    new Thread(() -> {
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
//                    }).start();

                    if(trackingOfTakeAway.contains(month)){
                        try {
                            java.lang.reflect.Type type = new TypeToken<HashMap<String, String>>() {
                            }.getType();
                            gson = new Gson();
                            json = trackingOfTakeAway.getString(month, "");
                            HashMap<String, String> map = gson.fromJson(json, type);
                            if (map.containsKey(calendar.get(Calendar.DAY_OF_MONTH) + "")) {
                                int currentVal = Integer.parseInt(map.get(calendar.get(Calendar.DAY_OF_MONTH) + ""));
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
                                int currentVal = Integer.parseInt(map.get(calendar.get(Calendar.DAY_OF_MONTH) + ""));
                                currentVal++;
                                map.put(calendar.get(Calendar.DAY_OF_MONTH) + "", currentVal + "");
                            } else {
                                map.put(calendar.get(Calendar.DAY_OF_MONTH) + "", "1");
                            }
                            json = gson.toJson(map);
                        }
                    }else{
                        gson = new Gson();
                        HashMap<String,String> map = new HashMap<>();
                        map.put(calendar.get(Calendar.DAY_OF_MONTH) + "","1");
                        json = gson.toJson(map);
                    }
                    trackingDineAndWay.putString(month,json);
                    trackingDineAndWay.apply();

                    if (storeOrdersForAdminInfo.contains(month)) {
                        try {
                            java.lang.reflect.Type type = new TypeToken<List<List<String>>>() {
                            }.getType();
                            gson = new Gson();
                            Log.i("here", "second");
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
                            java.lang.reflect.Type type = new TypeToken<List<List<String>>>() {
                            }.getType();
                            gson = new Gson();
                            Log.i("here", "second");
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
                            java.lang.reflect.Type type  = new TypeToken<List<List<String>>>() {
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
//                            Log.i("myInfo", storeNewList.toString());
                        }catch (Exception e){
                            int day = calendar.get(Calendar.DAY_OF_MONTH);
                            java.lang.reflect.Type type  = new TypeToken<List<List<String>>>() {
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
//                            Log.i("myInfo", storeNewList.toString());
                        }
                    } else {
                        List<List<String>> mainDataList = new ArrayList<>();
                        List<String> date = new ArrayList<>();
                        List<String> transID = new ArrayList<>();
                        List<String> userID = new ArrayList<>();
                        List<String> orderAmountList = new ArrayList<>();

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
                        int day = calendar.get(Calendar.DAY_OF_MONTH);
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
                        Log.i("myInfo", mainDataList + " " + mainList);
                    }

                    if(dailyAverageOrder.contains(month)){
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
                    }else{
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

                        averageEditor.putString(month,gson.toJson(newList));
                    } averageEditor.apply();
                    Log.i("here", "4444");
                    File file = new File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "RestaurantEarningTracker.xlsx");
                    try {
                        Cell cell;
                        FileInputStream fileInputStream = new FileInputStream(file);
                        XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream);
                        Sheet sheet = workbook.getSheetAt(0);
                        int max = sheet.getLastRowNum();
                        max = max + 1;
                        Log.i("here", "third");
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
                        Toast.makeText(ApproveCurrentTakeAway.this, "Completed", Toast.LENGTH_SHORT).show();
                        workbook.close();
                    } catch (Exception e) {
                        Log.i("black","" + e.getLocalizedMessage());
                        e.printStackTrace();
                    }
                }
                Log.i("statusOne", String.valueOf(makePaymentToVendor.getStatus()));
//            }, error -> {
//
//            });
//            {
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
//                    PaymentClass paymentClass = new PaymentClass(genratedID,auth.getUid() + "");
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
        public static String getAlphaNumericString(int n) {

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
            canvas.drawText("Invoice Number: " + orderId,45,860,text);

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
            storage = FirebaseStorage.getInstance();
            storageReference = storage.getReference();
            FirebaseAuth auth = FirebaseAuth.getInstance();
//            fastDialog.show();
            try {
                StorageReference reference = storageReference.child(id + "/" + "invoice" + "/"  + fileName);
                reference.putFile(Uri.fromFile(file)).addOnCompleteListener(task -> {
                    Toast.makeText(ApproveCurrentTakeAway.this, "Order cancelled successfully", Toast.LENGTH_SHORT).show();
                    String newString = fileName.replace("/","");
                    deleteFile(newString);
                    file.delete();
                }).addOnFailureListener(e -> {

                });
            }catch (Exception e){
                Toast.makeText(ApproveCurrentTakeAway.this, ""+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

            }

            pdfDocument.close();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            return null;
        }
    }


//    public String getFilePath(){
//        ContextWrapper contextWrapper = new ContextWrapper(getApplicationContext());
//        File direc = contextWrapper.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
//        File file = new File(direc,"ResTransactions" + ".xlsx");
//        return file.getPath();
//    }

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
            if(isGstAvailable) {
                DatabaseReference trackAmountForGST = FirebaseDatabase.getInstance().getReference().getRoot().child("Complaints").child("AmountTrackingDB").child(state).child(yearCurrent).child(month);
                trackAmountForGST.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.hasChild("amountEarned")) {
                            double prev = Double.parseDouble(snapshot.child("amountEarned").getValue(String.class));
                            prev += Double.parseDouble(orderAmount);
                            trackAmountForGST.child("amountEarned").setValue(prev + "");
                        } else
                            trackAmountForGST.child("amountEarned").setValue(orderAmount + "");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
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
                    DatabaseReference addToRTDB = FirebaseDatabase.getInstance().getReference().getRoot().child("Offers").child(sharedPreferences.getString("state","")).child(sharedPreferences.getString("state","")).child(Objects.requireNonNull(auth.getUid()));
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
                            String day = "Day" +  calendar.get(Calendar.DAY_OF_MONTH);
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
                                    String day = "Day" +  calendar.get(Calendar.DAY_OF_MONTH);
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

                            if(myMap.containsKey(month)) {
                                HashMap<String, Integer> dishmap = new HashMap<>(Objects.requireNonNull(myMap.get(month)));


                                Log.i("Dishinfo", dishmap.toString());

                                HashMap<String, Integer> map1 = sortByValue(dishmap);
                                storeForFoodineAnalysis.child("DishInfo").setValue(map1);
                            }
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

                            if(myMap.containsKey(month)) {
                                HashMap<String, Integer> dishmap = new HashMap<>(Objects.requireNonNull(myMap.get(month)));


                                Log.i("Dishinfo", dishmap.toString());

                                HashMap<String, Integer> map1 = sortByValue(dishmap);
                                storeForFoodineAnalysis.child("DishInfo").setValue(map1);
                            }
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