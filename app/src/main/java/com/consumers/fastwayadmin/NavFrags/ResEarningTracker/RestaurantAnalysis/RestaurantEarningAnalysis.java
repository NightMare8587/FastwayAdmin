package com.consumers.fastwayadmin.NavFrags.ResEarningTracker.RestaurantAnalysis;

import static java.util.stream.Collectors.toMap;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.consumers.fastwayadmin.NavFrags.ResDishTracker.RecyclerClassView;
import com.consumers.fastwayadmin.NavFrags.ResDishTracker.seeAllDishAnalysis;
import com.consumers.fastwayadmin.NavFrags.ResEarningTracker.ResEarningTrackerActivity;
import com.consumers.fastwayadmin.NavFrags.ResEarningTracker.TackerAdapter;
import com.consumers.fastwayadmin.R;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import org.eazegraph.lib.charts.BarChart;
import org.eazegraph.lib.models.BarModel;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class RestaurantEarningAnalysis extends AppCompatActivity {
    SharedPreferences storedOrders;
    SharedPreferences.Editor storeEditor;
    Gson gson;
    int daysLeftToShow = 0;
    Button moreDays;
    RecyclerView recyclerView;
    double totalAmountOrdersText;
    TextView totalOrders,totalAmount;
    int totalOrdersMade;
    TextView textView,totalOrderThatDay,totalAmountThatDay;
    List<String> days = new ArrayList<>();
    List<String> orders = new ArrayList<>();
    List<String> amounts = new ArrayList<>();
    String[] monthName = {"January", "February",
            "March", "April", "May", "June", "July",
            "August", "September", "October", "November",
            "December"};
    Calendar calendar = Calendar.getInstance();
    String json;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_earning_analysis);
        storedOrders = getSharedPreferences("RestaurantDailyStoreForAnalysis",MODE_PRIVATE);
        storeEditor = storedOrders.edit();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        recyclerView = findViewById(R.id.restaurantEarningAnalysisRecyclerView);
        textView = findViewById(R.id.textView33);
        BarChart mBarChart = (BarChart) findViewById(R.id.barchartAnalysis);
        moreDays = findViewById(R.id.moreThanSevenDaysAnalysisShow);
        totalOrderThatDay = findViewById(R.id.totalOrdersPerticularDayaRecycler);
        totalAmountThatDay = findViewById(R.id.totalTransaAmountThatDayRecycler);
        String month = monthName[calendar.get(Calendar.MONTH)];
        totalAmount = findViewById(R.id.totalAmountLastCoupleDays);
        totalOrders = findViewById(R.id.totalOrderdLastCoupleDays);
        Type type = new TypeToken<List<List<String>>>() {
        }.getType();
        gson = new Gson();
        if(storedOrders.contains(month)) {
            json = storedOrders.getString(month, "");
            List<List<String>> mainDataList = gson.fromJson(json, type);
            if (!mainDataList.isEmpty()) {
                List<String> date = new ArrayList<>(mainDataList.get(0));
                List<String> totalORders = new ArrayList<>(mainDataList.get(2));
                List<String> orderAmountList = new ArrayList<>(mainDataList.get(1));

                if(date.size() < 7){
                    moreDays.setVisibility(View.INVISIBLE);
                    daysLeftToShow = 6;
                    for(int i=date.size() - 1;i>=0;i--){
                        daysLeftToShow--;
                        days.add(date.get(i) + "th " + month);
                        orders.add(totalORders.get(i) + "");
                        amounts.add(orderAmountList.get(i));
                        totalOrdersMade += Integer.parseInt(totalORders.get(i));
                        totalAmountOrdersText += Double.parseDouble(orderAmountList.get(i));
                        mBarChart.addBar(new BarModel(Float.parseFloat(orderAmountList.get(i)), 0xFF1BA4E6));
                    }
                    totalAmount.setText("Total Transaction Amount: \u20b9" + totalAmountOrdersText);
                    totalOrders.setText("Total Order's: "  + totalOrdersMade + "");
                    recyclerView.setLayoutManager(linearLayoutManager);
                    recyclerView.setAdapter(new TackerAdapterAnalysis(days,days.get(0),orders,amounts));
                    totalAmountThatDay.setText("Total Transaction Amount: \u20b9" + orderAmountList.get(orderAmountList.size()-1) + "");
                    totalOrderThatDay.setText("Total Order's: " + totalORders.get(totalORders.size()-1) + "");
                    LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                            new IntentFilter("custom-message-analysis"));

                    for(int i = daysLeftToShow;i >= 0;i--)
                        mBarChart.addBar(new BarModel(0.f, 0xFF1BA4E6));
                }else{
                    int remainingDays = date.size() - 7;
                    for(int i = date.size() - 1; i >= remainingDays;i--){
                        totalOrdersMade += Integer.parseInt(totalORders.get(i));
                        totalAmountOrdersText += Double.parseDouble(orderAmountList.get(i));
                        mBarChart.addBar(new BarModel(Float.parseFloat(orderAmountList.get(i)), 0xFF1BA4E6));
                    }
                    totalAmount.setText(String.valueOf("Total Transaction Amount: " + totalAmountOrdersText));
                    totalOrders.setText("Total Order's: "  + totalOrdersMade + "");


                    moreDays.setOnClickListener(click -> {
                        textView.setText("Showing last 14 days analysis");
                        if(date.size() < 14){
                            daysLeftToShow = 13;
                            for(int i=date.size() - 1;i>=0;i--){
                                daysLeftToShow--;
                                totalOrdersMade += Integer.parseInt(totalORders.get(i));
                                totalAmountOrdersText += Double.parseDouble(orderAmountList.get(i));
                                mBarChart.addBar(new BarModel(Float.parseFloat(orderAmountList.get(i)), 0xFF1BA4E6));
                            }
                            totalAmount.setText(String.valueOf("Total Transaction Amount: " + totalAmountOrdersText));
                            totalOrders.setText("Total Order's: "  + totalOrdersMade + "");

                            for(int i = daysLeftToShow;i >= 0;i--)
                                mBarChart.addBar(new BarModel(0.f, 0xFF1BA4E6));
                        }else{
                            int daysLeft = date.size() - 14;
                            for(int i = date.size() - 1; i >= daysLeft;i--){
                                totalOrdersMade += Integer.parseInt(totalORders.get(i));
                                totalAmountOrdersText += Double.parseDouble(orderAmountList.get(i));
                                mBarChart.addBar(new BarModel(Float.parseFloat(orderAmountList.get(i)), 0xFF1BA4E6));
                            }
                            totalAmount.setText(String.valueOf("Total Transaction Amount: " + totalAmountOrdersText));
                            totalOrders.setText("Total Order's: "  + totalOrdersMade + "");
                        }
                    });
                }

                Log.i("info", date.toString());
                Log.i("info", totalORders.toString());
                Log.i("info", orderAmountList.toString());
            } else {
                Toast.makeText(this, "No order made", Toast.LENGTH_SHORT).show();
            }
        }else
            Toast.makeText(this, "No order made", Toast.LENGTH_SHORT).show();


    }
    public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @SuppressLint("SetTextI18n")
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String MonthName = intent.getStringExtra("month");
            String orders = intent.getStringExtra("orders");
            String amounts = intent.getStringExtra("amounts");
            Type type = new TypeToken<List<List<String>>>() {
            }.getType();
            List<List<String>> mainDataList = gson.fromJson(json, type);
            List<String> date = new ArrayList<>(mainDataList.get(0));
            List<String> totalORders = new ArrayList<>(mainDataList.get(2));
            List<String> orderAmountList = new ArrayList<>(mainDataList.get(1));
            totalAmountThatDay.setText("Total Transaction Amount: \u20b9" + amounts + "");
            totalOrderThatDay.setText("Total Order's: " + orders + "");
//            Toast.makeText(context, "" + orders + " " + amounts + " " + MonthName, Toast.LENGTH_SHORT).show();

        }
    };
}