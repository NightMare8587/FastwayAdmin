package com.consumers.fastwayadmin.NavFrags.ResEarningTracker;

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
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.consumers.fastwayadmin.R;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import org.eazegraph.lib.charts.BarChart;
import org.eazegraph.lib.models.BarModel;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class ResEarningTrackerActivity extends AppCompatActivity  {
    SharedPreferences resTrackInfo;
    SharedPreferences storeOrdersForAdminInfo;
    Calendar calendar;
    RecyclerView recyclerView;
    TackerAdapter tackerAdapter;
    int totalAmountPerMonth = 0;
    Gson gson;
    TextView totalOrdersMade,totalTransactionsMade;
    String json;
    String[] monthName = {"January", "February",
            "March", "April", "May", "June", "July",
            "August", "September", "October", "November",
            "December"};
    List<String> allMonthsNames;
    int currentDay;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_res_earning_tracker);
        resTrackInfo = getSharedPreferences("RestaurantTrackingDaily",MODE_PRIVATE);
        calendar = Calendar.getInstance();
        storeOrdersForAdminInfo = getSharedPreferences("StoreOrders",MODE_PRIVATE);
        totalOrdersMade = findViewById(R.id.totalOrdersMadeTextViewResTransactions);
        totalTransactionsMade = findViewById(R.id.totalTransactionAmountTextViewResTrans);
        String month = monthName[calendar.get(Calendar.MONTH)];
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        java.lang.reflect.Type type = new TypeToken<List<List<String>>>() {
        }.getType();
        gson = new Gson();
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
    }
    public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
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
        }
    };
}