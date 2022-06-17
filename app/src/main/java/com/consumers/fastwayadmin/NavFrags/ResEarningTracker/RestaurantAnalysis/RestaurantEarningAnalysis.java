package com.consumers.fastwayadmin.NavFrags.ResEarningTracker.RestaurantAnalysis;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.consumers.fastwayadmin.R;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class RestaurantEarningAnalysis extends AppCompatActivity {
    SharedPreferences storedOrders;
    SharedPreferences.Editor storeEditor;
    Gson gson;
    String[] monthName = {"January", "February",
            "March", "April", "May", "June", "July",
            "August", "September", "October", "November",
            "December"};
    Calendar calendar = Calendar.getInstance();
    String json;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_earning_analysis);
        storedOrders = getSharedPreferences("StoreOrders",MODE_PRIVATE);
        storeEditor = storedOrders.edit();
        String month = monthName[calendar.get(Calendar.MONTH)];
        Type type = new TypeToken<List<List<String>>>() {
        }.getType();
        gson = new Gson();
        json = storedOrders.getString(month,"");
        List<List<String>> mainDataList = gson.fromJson(json, type);
        List<String> date = new ArrayList<>(mainDataList.get(0));
        List<String> transID = new ArrayList<>(mainDataList.get(1));
        List<String> userID = new ArrayList<>(mainDataList.get(2));
        List<String> orderAmountList = new ArrayList<>(mainDataList.get(3));


        
        Log.i("info",date.toString());
        Log.i("info",transID.toString());
        Log.i("info",userID.toString());
        Log.i("info",orderAmountList.toString());
    }
}