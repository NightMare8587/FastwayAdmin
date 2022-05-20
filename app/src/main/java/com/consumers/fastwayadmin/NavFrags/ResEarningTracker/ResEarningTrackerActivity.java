package com.consumers.fastwayadmin.NavFrags.ResEarningTracker;

import static java.util.stream.Collectors.toMap;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.consumers.fastwayadmin.NavFrags.FastwayPremiumActivites.FastwayPremiums;
import com.consumers.fastwayadmin.NavFrags.ResDishTracker.RecyclerClassView;
import com.consumers.fastwayadmin.NavFrags.ResDishTracker.seeAllDishAnalysis;
import com.consumers.fastwayadmin.R;
import com.google.common.reflect.TypeToken;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.protobuf.Value;

import org.eazegraph.lib.charts.BarChart;
import org.eazegraph.lib.models.BarModel;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ResEarningTrackerActivity extends AppCompatActivity  {
    SharedPreferences resTrackInfo;
    SharedPreferences storeOrdersForAdminInfo;
    Calendar calendar;
    RecyclerView recyclerView,dishRecyclerView;
    TackerAdapter tackerAdapter;
    SharedPreferences loginInfoShared;
    int totalAmountPerMonth = 0;
    Button seeMoreDetails;
    Gson gson;
    TextView totalOrdersMade,totalTransactionsMade;
    String json;
    SharedPreferences dish;
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
        loginInfoShared = getSharedPreferences("loginInfo",MODE_PRIVATE);
        if(!loginInfoShared.contains("FastwayAdminPrem")){
            AlertDialog.Builder builder = new AlertDialog.Builder(ResEarningTrackerActivity.this);
            builder.setCancelable(false);
            builder.setTitle("Dialog").setMessage("You need to subscribe to premium plan to see analysis")
                    .setPositiveButton("Subscribe Now", (dialogInterface, i) -> {
                        dialogInterface.dismiss();
                        startActivity(new Intent(ResEarningTrackerActivity.this, FastwayPremiums.class));
                    }).setNegativeButton("Not Now", (dialogInterface, i) -> {
                        dialogInterface.dismiss();
                        finish();
                    }).create().show();
            return;
        }
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

        if(storeOrdersForAdminInfo.contains(month)) {
            json = storeOrdersForAdminInfo.getString(month, "");
            List<List<String>> mainDataListText = gson.fromJson(json, type);
            List<String> dateText = new ArrayList<>(mainDataListText.get(0));
            List<String> transIDText = new ArrayList<>(mainDataListText.get(1));
            List<String> userIDText = new ArrayList<>(mainDataListText.get(2));
            List<String> orderAmountListText = new ArrayList<>(mainDataListText.get(3));
            for (int i = 0; i < orderAmountListText.size(); i++) {
                totalAmountPerMonth += Integer.parseInt(orderAmountListText.get(i));
            }
            totalOrdersMade.setText("Total Transactions Made: " + dateText.size());
            totalTransactionsMade.setText("Total Transactions Made: \u20B9" + totalAmountPerMonth);
        }else{
            totalOrdersMade.setText("Total Transactions Made: " + 0);
            totalTransactionsMade.setText("Total Transactions Made: \u20B9" + 0);
        }


        recyclerView = findViewById(R.id.monthNamesListViewRes);
        allMonthsNames = new ArrayList<>(Arrays.asList(monthName));
        currentDay = calendar.get(Calendar.DAY_OF_MONTH);
        BarChart mBarChart = (BarChart) findViewById(R.id.barchart);
        recyclerView = findViewById(R.id.monthNamesListViewRes);
        recyclerView.setLayoutManager(linearLayoutManager);
        tackerAdapter = new TackerAdapter(allMonthsNames);
        recyclerView.setAdapter(new TackerAdapter(allMonthsNames));
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("custom-message"));

        for(int i=0;i<12;i++){
            if(storeOrdersForAdminInfo.contains(monthName[i])) {
                json = storeOrdersForAdminInfo.getString(monthName[i],"");
                List<List<String>> mainDataList = gson.fromJson(json, type);
                List<String> date = new ArrayList<>(mainDataList.get(0));
                List<String> transID = new ArrayList<>(mainDataList.get(1));
                List<String> userID = new ArrayList<>(mainDataList.get(2));
                List<String> orderAmountList = new ArrayList<>(mainDataList.get(3));
                float orderAmountTotal = 0;
                for(int ij=0;ij<orderAmountList.size();ij++)
                    orderAmountTotal += Float.parseFloat(orderAmountList.get(ij));

                mBarChart.addBar(new BarModel(orderAmountTotal, 0xFF1BA4E6));
            }else{
                mBarChart.addBar(new BarModel(0.f, 0xFF123456));
            }
        }
        mBarChart.startAnimation();


        if(dish.contains("DishAnalysisMonthBasis")){
            gson = new Gson();
            java.lang.reflect.Type types = new TypeToken<HashMap<String, HashMap<String,String>>>(){}.getType();
            String storedHash = dish.getString("DishAnalysisMonthBasis","");
            HashMap<String,HashMap<String,String>> myMap = gson.fromJson(storedHash,types);
            if(myMap.containsKey(month)){
                HashMap<String,String> map = new HashMap<>(myMap.get(month));
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
                    totalAmountPerMonth += Integer.parseInt(orderAmountListText.get(i));
                }
                totalOrdersMade.setText("Total Transactions Made: " + dateText.size());
                totalTransactionsMade.setText("Total Transactions Made: \u20B9" + totalAmountPerMonth);
            }else{
                totalOrdersMade.setText("Total Transactions Made: " + 0);
                totalTransactionsMade.setText("Total Transactions Made: \u20B9" + 0);
                Toast.makeText(context, "No transactions made in Month " + MonthName, Toast.LENGTH_SHORT).show();
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
}