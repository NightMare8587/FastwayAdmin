package com.consumers.fastwayadmin.NavFrags.ResEarningTracker;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.consumers.fastwayadmin.NavFrags.ResDishTracker.RecyclerClassView;
import com.consumers.fastwayadmin.NavFrags.ResDishTracker.seeAllDishAnalysis;
import com.consumers.fastwayadmin.NavFrags.ResEarningTracker.RestaurantAnalysis.RestaurantEarningAnalysis;
import com.consumers.fastwayadmin.R;
import com.google.common.reflect.TypeToken;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import org.eazegraph.lib.charts.BarChart;
import org.eazegraph.lib.models.BarModel;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ResEarningTrackerActivity extends AppCompatActivity  {
    SharedPreferences resTrackInfo;
    SharedPreferences storeOrdersForAdminInfo;
    double totalValOver = 0;
    TextView currentMonthNameViewing;
    double totalDineWayOverall = 0;
    double totalResDineWayOverAll = 0;
    double totalResOver = 0;
    Calendar calendar;
//    int overVeg = 0,overNon = 0,overVegan = 0,resVegVal = 0,resVeganVal = 0,resNonVal = 0;
    boolean resAvailable = false;
    boolean overallAvailable = false;
    LinearLayout overall,restaurant,overallDineAndWay,ResDineAndWayy;
    TextView overallVeg,overallNon,overallVegan,resVeg,resNon,resVegan,resHeading,overallHeading,overAllDine,overAllTake,ResTake,ResDine;
    RecyclerView recyclerView,dishRecyclerView;
    TackerAdapter tackerAdapter;
    SharedPreferences loginInfoShared;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    SharedPreferences usersFrequencyPref;
    DecimalFormat df = new DecimalFormat("0.00");
    double totalAmountPerMonth = 0;
    Button seeMoreDetails,seeAnalysis;
    Gson gson;
    TextView totalOrdersMade,totalTransactionsMade;
    String json;
    TextView totalCustomers,oneTime,Recuuring;
    SharedPreferences dish;
    SharedPreferences.Editor editor;
    SharedPreferences.Editor userFrequencyEdit;
    String[] monthName = {"January", "February",
            "March", "April", "May", "June", "July",
            "August", "September", "October", "November",
            "December"};
    List<String> allMonthsNames;
    int currentDay;
    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_res_earning_tracker);
        initialise();
        loginInfoShared = getSharedPreferences("loginInfo",MODE_PRIVATE);
        SharedPreferences adminPrem = getSharedPreferences("AdminPremiumDetails",MODE_PRIVATE);
        usersFrequencyPref = getSharedPreferences("UsersFrequencyPerMonth",MODE_PRIVATE);
        userFrequencyEdit = usersFrequencyPref.edit();
        SharedPreferences.Editor premEdit = adminPrem.edit();
        totalCustomers = findViewById(R.id.totalCustomerInAMonthTracker);
        oneTime = findViewById(R.id.oneTimeCustomersTracker);
        Recuuring = findViewById(R.id.recurringCustomersTracker);
        editor = loginInfoShared.edit();
        currentMonthNameViewing = findViewById(R.id.currentMonthNameBelowBarCharTextView);
        seeAnalysis = findViewById(R.id.seeRestaurantAnalysisButtonTracker);
        if(adminPrem.contains("status")) {
            if (!adminPrem.getString("status", "").equals("active")) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ResEarningTrackerActivity.this);
                builder.setCancelable(false);
                builder.setTitle("Dialog").setMessage("You need to subscribe to premium plan to see analysis")
                        .setPositiveButton("Subscribe Now", (dialogInterface, i) -> {
                            dialogInterface.dismiss();
                            premEdit.putString("status", "active");
                            premEdit.apply();
                            finish();
//                        startActivity(new Intent(ResEarningTrackerActivity.this, FastwayPremiums.class));
                        }).setNegativeButton("Not Now", (dialogInterface, i) -> {
                            dialogInterface.dismiss();
                            finish();
                        }).create().show();
                return;
            }
        }else{
            AlertDialog.Builder builder = new AlertDialog.Builder(ResEarningTrackerActivity.this);
            builder.setCancelable(false);
            builder.setTitle("Dialog").setMessage("You need to subscribe to premium plan to see analysis")
                    .setPositiveButton("Subscribe Now", (dialogInterface, i) -> {
                        dialogInterface.dismiss();
                        premEdit.putString("status", "active");
                        premEdit.apply();
                        finish();
//                        startActivity(new Intent(ResEarningTrackerActivity.this, FastwayPremiums.class));
                    }).setNegativeButton("Not Now", (dialogInterface, i) -> {
                        dialogInterface.dismiss();
                        finish();
                    }).create().show();
            return;
        }

        new newAsyncTask().execute();
        seeAnalysis.setOnClickListener(click -> {
            startActivity(new Intent(click.getContext(), RestaurantEarningAnalysis.class));
        });
        resTrackInfo = getSharedPreferences("RestaurantTrackingDaily",MODE_PRIVATE);
        calendar = Calendar.getInstance();
        dishRecyclerView = findViewById(R.id.dishTrackerRecyclerViewAnalysis);
        dish = getSharedPreferences("DishAnalysis",Context.MODE_PRIVATE);
        storeOrdersForAdminInfo = getSharedPreferences("StoreOrders",MODE_PRIVATE);
        totalOrdersMade = findViewById(R.id.totalOrdersMadeTextViewResTransactions);
        totalTransactionsMade = findViewById(R.id.totalTransactionAmountTextViewResTrans);
        String month = monthName[calendar.get(Calendar.MONTH)];
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        java.lang.reflect.Type type = new TypeToken<List<List<String>>>() {
        }.getType();
        gson = new Gson();
        seeMoreDetails = findViewById(R.id.seeMoreDishAnalysisDetails);
        currentMonthNameViewing.setText("Month: " + month);
        if(usersFrequencyPref.contains(month)){
            java.lang.reflect.Type types = new TypeToken<HashMap<String,String>>() {
            }.getType();
            json = usersFrequencyPref.getString(month,"");
            HashMap<String,String> map = gson.fromJson(json,types);

            totalCustomers.setText("Total Customer's: " + map.size() + "");

            int count = 0;
            int otherCount = 0;
            for(String i : map.keySet()){
                int value = Integer.parseInt(map.get(i));
                if(value == 1)
                    count++;
                else
                    otherCount++;
            }

            oneTime.setText("One Time Customer: " + count);
            Recuuring.setText("Recurring Customers: " + otherCount);
        }

        if(storeOrdersForAdminInfo.contains(month)) {
            json = storeOrdersForAdminInfo.getString(month, "");
            List<List<String>> mainDataListText = gson.fromJson(json, type);
            List<String> dateText = new ArrayList<>(mainDataListText.get(0));
            List<String> transIDText = new ArrayList<>(mainDataListText.get(1));
            List<String> userIDText = new ArrayList<>(mainDataListText.get(2));
            List<String> orderAmountListText = new ArrayList<>(mainDataListText.get(3));
            for (int i = 0; i < orderAmountListText.size(); i++) {
                totalAmountPerMonth += Double.parseDouble(orderAmountListText.get(i));
            }
            totalOrdersMade.setText("Total Transactions Made: " + dateText.size());
            totalTransactionsMade.setText("Total Transactions Amount: \u20B9" + df.format(totalAmountPerMonth));
        }else{
            totalOrdersMade.setText("Total Transactions Made: " + 0);
            totalTransactionsMade.setText("Total Transactions Amount: \u20B9" + 0);
        }


        recyclerView = findViewById(R.id.monthNamesListViewRes);
        allMonthsNames = new ArrayList<>(Arrays.asList(monthName));
        currentDay = calendar.get(Calendar.DAY_OF_MONTH);
        BarChart mBarChart = findViewById(R.id.barchart);


//        recyclerView = findViewById(R.id.monthNamesListViewRes);

        recyclerView.setLayoutManager(linearLayoutManager);
        tackerAdapter = new TackerAdapter(allMonthsNames,month);
        recyclerView.setAdapter(new TackerAdapter(allMonthsNames,month));
        recyclerView.scrollToPosition(allMonthsNames.indexOf(month));
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("custom-message"));

//        BarChart mBarChart = (BarChart) findViewById(R.id.barchart);

//            ValueLineSeries series = new ValueLineSeries();
//            series.setColor(0xFF56B7F1);
        SharedPreferences storeOrder = getSharedPreferences("RestaurantDailyStoreForAnalysis",MODE_PRIVATE);
        if(storeOrder.contains(month)){
            json = storeOrder.getString(month, "");
            List<List<String>> mainDataList = gson.fromJson(json, type);
            Log.i("info",mainDataList.toString());

            if (!mainDataList.isEmpty()) {
                List<String> date = new ArrayList<>(mainDataList.get(0));
                List<String> totalORders = new ArrayList<>(mainDataList.get(2));
                List<String> orderAmountList = new ArrayList<>(mainDataList.get(1));

                for(int i=0;i<orderAmountList.size();i++){
                    mBarChart.addBar(new BarModel(Float.parseFloat(orderAmountList.get(i)),0xFF1BA4E6));
                }

                mBarChart.startAnimation();
//                mBarChart.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Toast.makeText(ResEarningTrackerActivity.this, "Clcked", Toast.LENGTH_SHORT).show();
//
//                    }
//                });
            }else
                mBarChart.clearChart();
        }else {
            mBarChart.clearChart();
        }
//        for(int i=0;i<12;i++){
//            if(storeOrdersForAdminInfo.contains(monthName[i])) {
//                json = storeOrdersForAdminInfo.getString(monthName[i],"");
//                List<List<String>> mainDataList = gson.fromJson(json, type);
//                List<String> date = new ArrayList<>(mainDataList.get(0));
//                List<String> transID = new ArrayList<>(mainDataList.get(1));
//                List<String> userID = new ArrayList<>(mainDataList.get(2));
//                List<String> orderAmountList = new ArrayList<>(mainDataList.get(3));
//                float orderAmountTotal = 0;
//                for(int ij=0;ij<orderAmountList.size();ij++)
//                    orderAmountTotal += Float.parseFloat(orderAmountList.get(ij));
//
//                mBarChart.addBar(new BarModel(orderAmountTotal, 0xFF1BA4E6));
//            }else{
//                mBarChart.addBar(new BarModel(0.f, 0xFF123456));
//            }
//        }
//        mBarChart.startAnimation();


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
//               for(int i=0;i<sorted.size();i++){
//                   valuesName.add("" + sorted.values().toArray()[i]);
//                   keysName.add("" + sorted.keySet().toArray()[i]);
//               }
//
//                Log.i("Dishinfo",keysName.toString());
//                Log.i("Dishinfo",valuesName.toString());
                seeMoreDetails.setVisibility(View.VISIBLE);
                dishRecyclerView.setVisibility(View.VISIBLE);
                seeMoreDetails.setOnClickListener(view -> {
                    Intent intent1 = new Intent(ResEarningTrackerActivity.this, seeAllDishAnalysis.class);
                    intent1.putExtra("dishName", keysName);
                    intent1.putExtra("dishValue",  valuesName);
                    startActivity(intent1);
                });
                dishRecyclerView.setLayoutManager(new LinearLayoutManager(ResEarningTrackerActivity.this));
                dishRecyclerView.setAdapter(new RecyclerClassView(keysName,valuesName,ResEarningTrackerActivity.this));
            }else {
                seeMoreDetails.setVisibility(View.INVISIBLE);
                dishRecyclerView.setVisibility(View.INVISIBLE);
            }
        }
    }

    private void initialise() {
        overall = findViewById(R.id.linearLayoutResEarningOverall);
        restaurant = findViewById(R.id.linearLayoutResEarningRestaurant);
        overallDineAndWay = findViewById(R.id.dineWayOverAllAnalysisLayout);
        ResDineAndWayy = findViewById(R.id.restaurantOverAllDineAndTakeAway);
        overallVegan = findViewById(R.id.overallVegan);
        overallVeg = findViewById(R.id.overallVeg);
        overallNon = findViewById(R.id.overallNon);
        overallHeading = findViewById(R.id.textViewShowingDishTracker);
        resVeg = findViewById(R.id.resVeg);
        resNon = findViewById(R.id.resNon);
        resVegan = findViewById(R.id.resVegan);
        resHeading = findViewById(R.id.textViewShowingDishTrackerRestaurant);
        overAllDine = findViewById(R.id.overAllDining);
        overAllTake = findViewById(R.id.overAllTakeAway);
        ResDine = findViewById(R.id.ResAllDining);
        ResTake = findViewById(R.id.ResAllTakeAway);
    }

    public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @SuppressLint("SetTextI18n")
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String MonthName = intent.getStringExtra("month");
            currentMonthNameViewing.setText("Month: " + MonthName);
            if(storeOrdersForAdminInfo.contains(MonthName)){
                java.lang.reflect.Type type = new TypeToken<List<List<String>>>() {
                }.getType();
                json = storeOrdersForAdminInfo.getString(MonthName, "");
                List<List<String>> mainDataListText = gson.fromJson(json, type);
                List<String> dateText = new ArrayList<>(mainDataListText.get(0));
                List<String> transIDText = new ArrayList<>(mainDataListText.get(1));
                List<String> userIDText = new ArrayList<>(mainDataListText.get(2));
                List<String> orderAmountListText = new ArrayList<>(mainDataListText.get(3));
                totalAmountPerMonth = 0;
                for (int i = 0; i < orderAmountListText.size(); i++) {
                    totalAmountPerMonth += Double.parseDouble(orderAmountListText.get(i));
                }
                totalOrdersMade.setText("Total Transactions Made: " + dateText.size());
                totalTransactionsMade.setText("Total Transactions Amount: \u20B9" + df.format(totalAmountPerMonth));
            }else{
                totalOrdersMade.setText("Total Transactions Made: " + 0);
                totalTransactionsMade.setText("Total Transactions Amount: \u20B9" + 0);
                Toast.makeText(context, "No transactions made in Month " + MonthName, Toast.LENGTH_SHORT).show();
            }
            BarChart mBarChart = findViewById(R.id.barchart);

//            ValueLineSeries series = new ValueLineSeries();
//            series.setColor(0xFF56B7F1);

            SharedPreferences storeOrder = getSharedPreferences("RestaurantDailyStoreForAnalysis",MODE_PRIVATE);
            if(storeOrder.contains(MonthName)){
                java.lang.reflect.Type type = new TypeToken<List<List<String>>>() {
                }.getType();
                json = storeOrder.getString(MonthName, "");
                List<List<String>> mainDataList = gson.fromJson(json, type);
                Log.i("info",mainDataList.toString());

                if (!mainDataList.isEmpty()) {
                    List<String> date = new ArrayList<>(mainDataList.get(0));
                    List<String> totalORders = new ArrayList<>(mainDataList.get(2));
                    List<String> orderAmountList = new ArrayList<>(mainDataList.get(1));

                    for(int i=0;i<orderAmountList.size();i++){
                        mBarChart.addBar(new BarModel(Float.parseFloat(orderAmountList.get(i)),0xFF1BA4E6));
                    }

                    mBarChart.startAnimation();
//                mBarChart.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Toast.makeText(ResEarningTrackerActivity.this, "Clcked", Toast.LENGTH_SHORT).show();
//
//                    }
//                });
                }else
                    mBarChart.clearChart();
            }else {
                mBarChart.clearChart();
            }

            if(usersFrequencyPref.contains(MonthName)){
                java.lang.reflect.Type types = new TypeToken<HashMap<String,String>>() {
                }.getType();
                json = usersFrequencyPref.getString(MonthName,"");
                HashMap<String,String> map = gson.fromJson(json,types);

                totalCustomers.setText("Total Customer's: " + map.size() + "");

                int count = 0;
                int otherCount = 0;
                for(String i : map.keySet()){
                    int value = Integer.parseInt(map.get(i));
                    if(value == 1)
                        count++;
                    else
                        otherCount++;
                }

                oneTime.setText("One Time Customer: " + count);
                Recuuring.setText("Recurring Customers: " + otherCount);
            }else{
                totalCustomers.setText("0");
                oneTime.setText("0");
                Recuuring.setText("0");
            }

            if(dish.contains("DishAnalysisMonthBasis")){
                gson = new Gson();
                java.lang.reflect.Type types = new TypeToken<HashMap<String, HashMap<String,Integer>>>(){}.getType();
                String storedHash = dish.getString("DishAnalysisMonthBasis","");
                HashMap<String,HashMap<String,Integer>> myMap = gson.fromJson(storedHash,types);
                if(myMap.containsKey(MonthName)){
                    HashMap<String,Integer> map = new HashMap<>(Objects.requireNonNull(myMap.get(MonthName)));


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

                    Collections.reverse(keysName);
                    Collections.reverse(valuesName);

                    Log.i("info",keysName.toString());
                    Log.i("info",valuesName.toString());



                    seeMoreDetails.setVisibility(View.VISIBLE);
                    dishRecyclerView.setVisibility(View.VISIBLE);
                    seeMoreDetails.setOnClickListener(click -> {
                        Intent intent1 = new Intent(ResEarningTrackerActivity.this, seeAllDishAnalysis.class);
                        intent1.putExtra("dishName",  keysName);
                        intent1.putExtra("dishValue",  valuesName);
                        startActivity(intent1);
                    });
                    dishRecyclerView.setLayoutManager(new LinearLayoutManager(ResEarningTrackerActivity.this));
                    dishRecyclerView.setAdapter(new RecyclerClassView(keysName,valuesName,ResEarningTrackerActivity.this));
                }else {
                    seeMoreDetails.setVisibility(View.INVISIBLE);
                    dishRecyclerView.setVisibility(View.INVISIBLE);
                }
            }
        }
    };

    public class newAsyncTask extends AsyncTask<Void,Void,Void>{



        @Override
        protected Void doInBackground(Void... voids) {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Complaints").child("ResAnalysis").child(loginInfoShared.getString("state","")).child(loginInfoShared.getString("locality",""));
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {

                @SuppressLint("SetTextI18n")
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        DecimalFormat decimalFormat = new DecimalFormat("0.00");
                        double vegVal = 0;
                        double nonVegVal = 0;
                        double veganVal = 0;
                        double foodDining = 0;
                        double foodTakeAway = 0;


                        overallAvailable = true;
                        Log.i("resInfo","i am gere");
                        if(snapshot.hasChild("veg")){
                            vegVal = Double.parseDouble(Objects.requireNonNull(snapshot.child("veg").getValue(String.class)));
                        }
                        if(snapshot.hasChild("NonVeg")){
                            nonVegVal =  Double.parseDouble(Objects.requireNonNull(snapshot.child("NonVeg").getValue(String.class)));
                        }
                        if(snapshot.hasChild("vegan")){
                            veganVal =  Double.parseDouble(Objects.requireNonNull(snapshot.child("vegan").getValue(String.class)));
                        }

                        if(snapshot.hasChild("foodDining"))
                            foodDining = Double.parseDouble(String.valueOf(snapshot.child("foodDining").getValue()));
                        if(snapshot.hasChild("foodTakeAway"))
                            foodTakeAway = Double.parseDouble(String.valueOf(snapshot.child("foodTakeAway").getValue()));

                        totalValOver = vegVal + nonVegVal + veganVal;
                        totalDineWayOverall = foodDining + foodTakeAway;

                        overall.setVisibility(View.VISIBLE);
                        overallHeading.setVisibility(View.VISIBLE);
                        overallDineAndWay.setVisibility(View.VISIBLE);
                        overallHeading.setText(loginInfoShared.getString("locality","") + " Users Preferences");
                        overallVeg.setText("Veg: " + decimalFormat.format(vegVal * 100 / totalValOver)  + "%");
                        overallNon.setText("NonVeg: "  + decimalFormat.format(nonVegVal * 100 / totalValOver)  + "%");
                        overallVegan.setText("Vegan: " + decimalFormat.format(veganVal * 100 / totalValOver)  + "%");

                        if(totalDineWayOverall != 0) {
                            overAllDine.setText("Dining: " + decimalFormat.format(foodDining * 100 / totalDineWayOverall) + "%");
                            overAllTake.setText("TakeAway: " + decimalFormat.format(foodTakeAway * 100 / totalDineWayOverall) + "%");
                        }

                        double ResTakeAway = 0;
                        double ResDineVal = 0;
                        SharedPreferences sharedPreferences = getSharedPreferences("AdminPremiumDetails",MODE_PRIVATE);
                        if(sharedPreferences.contains("status") && sharedPreferences.getString("status","").equals("active")){
                            String month = monthName[calendar.get(Calendar.MONTH)];
                            ResDineAndWayy.setVisibility(View.VISIBLE);
                            SharedPreferences trackingTakeAway = getSharedPreferences("TrackingOfTakeAway",MODE_PRIVATE);
                            SharedPreferences trackingFoodDining = getSharedPreferences("TrackingOfFoodDining",MODE_PRIVATE);
                            if(trackingTakeAway.contains(month)){
                                java.lang.reflect.Type type = new TypeToken<HashMap<String,String>>() {
                                }.getType();
                                gson = new Gson();
                                json = trackingTakeAway.getString(month,"");
                                HashMap<String,String> map = gson.fromJson(json,type);

                                for(String i : map.keySet()){
                                    double val = Double.parseDouble(Objects.requireNonNull(map.get(i)));
                                    ResTakeAway += val;
                                }
                            }

                            if(trackingFoodDining.contains(month)){
                                java.lang.reflect.Type type = new TypeToken<HashMap<String,String>>() {
                                }.getType();
                                gson = new Gson();
                                json = trackingFoodDining.getString(month,"");
                                HashMap<String,String> map = gson.fromJson(json,type);

                                for(String i : map.keySet()){
                                    double val = Double.parseDouble(Objects.requireNonNull(map.get(i)));
                                    ResDineVal += val;
                                }
                            }

                            totalResDineWayOverAll = ResDineVal + ResTakeAway;
                            if(totalResDineWayOverAll != 0){
                                ResDine.setText("Dining: " + decimalFormat.format(ResDineVal * 100 / totalResDineWayOverAll) + "%");
                                ResTake.setText("TakeAway: " + decimalFormat.format(ResTakeAway * 100 / totalResDineWayOverAll) + "%");
                            }

                        }


                        DatabaseReference resRef = FirebaseDatabase.getInstance().getReference().getRoot().child("Complaints").child("ResAnalysis").child(loginInfoShared.getString("state","")).child(loginInfoShared.getString("locality","")).child(auth.getUid());
                        resRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists()){
                                    double ResvegVal = 0;
                                    double ResnonVegVal = 0;
                                    double ResveganVal = 0;

                                    resAvailable = true;

                                    if(snapshot.hasChild("veg")){
                                        ResvegVal =  Double.parseDouble(Objects.requireNonNull(snapshot.child("veg").getValue(String.class)));
                                    }
                                    if(snapshot.hasChild("NonVeg")){
                                        ResnonVegVal =  Double.parseDouble(Objects.requireNonNull(snapshot.child("NonVeg").getValue(String.class)));
                                    }
                                    if(snapshot.hasChild("vegan")){
                                        ResveganVal =  Double.parseDouble(Objects.requireNonNull(snapshot.child("vegan").getValue(String.class)));
                                    }


                                    totalResOver = ResvegVal + ResnonVegVal + ResveganVal;

                                    restaurant.setVisibility(View.VISIBLE);
                                    resHeading.setVisibility(View.VISIBLE);
                                    resHeading.setText("Your Restaurant Users Preferences");
                                    resVeg.setText("Veg: " + decimalFormat.format(ResvegVal * 100 / totalResOver)  + "%");
                                    resNon.setText("NonVeg: " + decimalFormat.format(ResnonVegVal * 100 / totalResOver)  + "%");
                                    resVegan.setText("Vegan: " + decimalFormat.format(ResveganVal * 100 / totalResOver)  + "%");
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            return null;
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            Log.i("info","here there iam");

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