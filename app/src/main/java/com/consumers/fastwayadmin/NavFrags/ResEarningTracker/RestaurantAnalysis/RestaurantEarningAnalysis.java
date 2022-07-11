package com.consumers.fastwayadmin.NavFrags.ResEarningTracker.RestaurantAnalysis;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.consumers.fastwayadmin.R;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import org.eazegraph.lib.charts.BarChart;
import org.eazegraph.lib.models.BarModel;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class RestaurantEarningAnalysis extends AppCompatActivity {
    SharedPreferences storedOrders;
    SharedPreferences.Editor storeEditor;
    Gson gson;
    int daysLeftToShow = 0;
    Button moreDays;
    double totalAmountOrdersText;
    TextView totalOrders,totalAmount;
    int totalOrdersMade;
    TextView textView;
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
        textView = findViewById(R.id.textView33);
        BarChart mBarChart = (BarChart) findViewById(R.id.barchartAnalysis);
        moreDays = findViewById(R.id.moreThanSevenDaysAnalysisShow);
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
                        totalOrdersMade += Integer.parseInt(totalORders.get(i));
                        totalAmountOrdersText += Double.parseDouble(orderAmountList.get(i));
                        mBarChart.addBar(new BarModel(Float.parseFloat(orderAmountList.get(i)), 0xFF1BA4E6));
                    }
                    totalAmount.setText(String.valueOf("Total Transaction Amount: " + totalAmountOrdersText));
                    totalOrders.setText("Total Order's: "  + totalOrdersMade + "");

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
}