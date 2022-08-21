package com.consumers.fastwayadmin.NavFrags.ResEarningTracker;

import static java.util.stream.Collectors.toMap;

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
import org.eazegraph.lib.charts.ValueLineChart;
import org.eazegraph.lib.models.BarModel;
import org.eazegraph.lib.models.ValueLinePoint;
import org.eazegraph.lib.models.ValueLineSeries;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ResEarningTrackerActivity extends AppCompatActivity  {
    SharedPreferences resTrackInfo;
    SharedPreferences storeOrdersForAdminInfo;
    double totalValOver = 0;
    double totalResOver = 0;
    Calendar calendar;
//    int overVeg = 0,overNon = 0,overVegan = 0,resVegVal = 0,resVeganVal = 0,resNonVal = 0;
    boolean resAvailable = false;
    boolean overallAvailable = false;
    LinearLayout overall,restaurant;
    TextView overallVeg,overallNon,overallVegan,resVeg,resNon,resVegan,resHeading,overallHeading;
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
            totalTransactionsMade.setText("Total Transactions Made: \u20B9" + df.format(totalAmountPerMonth));
        }else{
            totalOrdersMade.setText("Total Transactions Made: " + 0);
            totalTransactionsMade.setText("Total Transactions Made: \u20B9" + 0);
        }


        recyclerView = findViewById(R.id.monthNamesListViewRes);
        allMonthsNames = new ArrayList<>(Arrays.asList(monthName));
        currentDay = calendar.get(Calendar.DAY_OF_MONTH);
        ValueLineChart mBarChart = (ValueLineChart) findViewById(R.id.barchart);

        ValueLineSeries series = new ValueLineSeries();
        series.setColor(0xFF56B7F1);
        recyclerView = findViewById(R.id.monthNamesListViewRes);

        recyclerView.setLayoutManager(linearLayoutManager);
        tackerAdapter = new TackerAdapter(allMonthsNames,month);
        recyclerView.setAdapter(new TackerAdapter(allMonthsNames,month));
        recyclerView.scrollToPosition(allMonthsNames.indexOf(month));
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("custom-message"));

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
                    series.addPoint(new ValueLinePoint(date.get(i) + "",(float) Double.parseDouble(orderAmountList.get(i))));
                }
                mBarChart.addSeries(series);
                mBarChart.startAnimation();
//                mBarChart.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Toast.makeText(ResEarningTrackerActivity.this, "Clcked", Toast.LENGTH_SHORT).show();
//
//                    }
//                });
            }
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
            java.lang.reflect.Type types = new TypeToken<HashMap<String, HashMap<String,String>>>(){}.getType();
            String storedHash = dish.getString("DishAnalysisMonthBasis","");
            HashMap<String,HashMap<String,String>> myMap = gson.fromJson(storedHash,types);
            if(myMap.containsKey(month)){
                HashMap<String,String> map = new HashMap<>(myMap.get(month));
                List<String> values = new ArrayList<String>(map.values());
                Log.i("info",values.toString());
                Collections.sort(values);
                Log.i("info",values.toString());

               Map<String,String> sorted = map
                        .entrySet()
                        .stream()
                        .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                        .collect(
                                toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2,
                                        LinkedHashMap::new));

               Log.i("info",map.toString());
               Log.i("info",sorted.toString());

               ArrayList<String> keysName = new ArrayList<>();
               ArrayList<String> valuesName = new ArrayList<>();

               for(int i=0;i<sorted.size();i++){
                   valuesName.add("" + sorted.values().toArray()[i]);
                   keysName.add("" + sorted.keySet().toArray()[i]);
               }

                Log.i("info",keysName.toString());
                Log.i("info",valuesName.toString());
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
        overallVegan = findViewById(R.id.overallVegan);
        overallVeg = findViewById(R.id.overallVeg);
        overallNon = findViewById(R.id.overallNon);
        overallHeading = findViewById(R.id.textViewShowingDishTracker);
        resVeg = findViewById(R.id.resVeg);
        resNon = findViewById(R.id.resNon);
        resVegan = findViewById(R.id.resVegan);
        resHeading = findViewById(R.id.textViewShowingDishTrackerRestaurant);
    }

    public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @SuppressLint("SetTextI18n")
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String MonthName = intent.getStringExtra("month");
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
                totalTransactionsMade.setText("Total Transactions Made: \u20B9" + df.format(totalAmountPerMonth));
            }else{
                totalOrdersMade.setText("Total Transactions Made: " + 0);
                totalTransactionsMade.setText("Total Transactions Made: \u20B9" + 0);
                Toast.makeText(context, "No transactions made in Month " + MonthName, Toast.LENGTH_SHORT).show();
            }
            ValueLineChart mBarChart = (ValueLineChart) findViewById(R.id.barchart);

            ValueLineSeries series = new ValueLineSeries();
            series.setColor(0xFF56B7F1);
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
                        series.addPoint(new ValueLinePoint(date.get(i) + "",(float) Double.parseDouble(orderAmountList.get(i))));
                    }
                    mBarChart.addSeries(series);
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
                mBarChart.clearStandardValues();
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
                java.lang.reflect.Type types = new TypeToken<HashMap<String, HashMap<String,String>>>(){}.getType();
                String storedHash = dish.getString("DishAnalysisMonthBasis","");
                HashMap<String,HashMap<String,String>> myMap = gson.fromJson(storedHash,types);
                if(myMap.containsKey(MonthName)){
                    HashMap<String,String> map = new HashMap<>(myMap.get(MonthName));
                    List<String> values = new ArrayList<String>(map.values());
                    Log.i("info",values.toString());
                    Collections.sort(values,Collections.reverseOrder());
                    Log.i("info",values.toString());

                    Map<String,String> sorted = map
                            .entrySet()
                            .stream()
                            .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                            .collect(
                                    toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2,
                                            LinkedHashMap::new));

                    Log.i("info",map.toString());
                    Log.i("info",sorted.toString());

                    ArrayList<String> keysName = new ArrayList<>();
                    ArrayList<String> valuesName = new ArrayList<>();

                    for(int i=0;i<sorted.size();i++){
                        valuesName.add("" + sorted.values().toArray()[i]);
                        keysName.add("" + sorted.keySet().toArray()[i]);
                    }

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

                        totalValOver = vegVal + nonVegVal + veganVal;

                        overall.setVisibility(View.VISIBLE);
                        overallHeading.setVisibility(View.VISIBLE);
                        overallHeading.setText(loginInfoShared.getString("locality","") + " Users Preferences");
                        overallVeg.setText("Veg: " + decimalFormat.format(vegVal * 100 / totalValOver)  + "%");
                        overallNon.setText("NonVeg: "  + decimalFormat.format(nonVegVal * 100 / totalValOver)  + "%");
                        overallVegan.setText("Vegan: " + decimalFormat.format(veganVal * 100 / totalValOver)  + "%");

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
}