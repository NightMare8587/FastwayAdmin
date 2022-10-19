package com.consumers.fastwayadmin.NavFrags.ResEarningTracker.RestaurantAnalysis;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.consumers.fastwayadmin.R;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class RestaurantEarningAnalysis extends AppCompatActivity {
    SharedPreferences storedOrders;
    SharedPreferences.Editor storeEditor;
    Gson gson;
    int daysLeftToShow = 0;
    Button moreDays;
    TextView dateThatDay;
    TextView averageOrderThatDay;
    RecyclerView recyclerView;
    TextView totalCustomersLast7days;
    double totalAmountOrdersText;
    TextView highestSalesDayText,highestSalesAmountThatDay,highestSalesOrderThatDay;
    TextView totalOrders,totalAmount;
    int totalOrdersMade;
    TextView textView,totalOrderThatDay,totalAmountThatDay;
    List<String> days = new ArrayList<>();
    List<String> orders = new ArrayList<>();
    SharedPreferences user7daysTracker;
    List<String> amounts = new ArrayList<>();
    String[] monthName = {"January", "February",
            "March", "April", "May", "June", "July",
            "August", "September", "October", "November",
            "December"};
    ProgressBar progressBar;
    Calendar calendar = Calendar.getInstance();
    com.github.mikephil.charting.charts.BarChart barChart,barChart1;

    String json;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_earning_analysis);
        storedOrders = getSharedPreferences("RestaurantDailyStoreForAnalysis",MODE_PRIVATE);
        storeEditor = storedOrders.edit();
        totalCustomersLast7days = findViewById(R.id.totalCustomersInLast7daysTextView);
        highestSalesDayText = findViewById(R.id.highestSalesDayOrdersTextView);
        user7daysTracker = getSharedPreferences("DailyUserTrackingFor7days",MODE_PRIVATE);
        progressBar = findViewById(R.id.progressBarRestaurantEarningAnalysis);
        barChart1 = findViewById(R.id.lineChartRestaurantEarn7days);
        highestSalesAmountThatDay = findViewById(R.id.highestDateNameDay);
        highestSalesOrderThatDay = findViewById(R.id.highestSalesAmountTextViewDay);
        barChart = findViewById(R.id.barchartAnalysis);
        averageOrderThatDay = findViewById(R.id.averageOrderSizeThatPerticularDay);
        barChart.getDescription().setEnabled(false);
//        dateTotalCustomers = findViewById(R.id.DatetotalCustomersInLast7daysTextView);
        dateThatDay = findViewById(R.id.dateOfThatDayParticular);
        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        barChart.setMaxVisibleValueCount(35);

        // scaling can now only be done on x- and y-axis separately
        barChart.setPinchZoom(false);
        barChart.setFitBars(true);

        barChart.setDrawBarShadow(true);
        barChart.setDrawGridBackground(false);
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new MyDecimalValueFormatter());
        xAxis.setGranularityEnabled(true);
        xAxis.setDrawGridLines(false);

        barChart.getAxisRight().setGranularityEnabled(true);
        barChart.getAxisRight().setValueFormatter(new MyDecimalValueFormatter());

        barChart.getAxisLeft().setDrawGridLines(false);

        Legend l = barChart.getLegend();

        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setForm(Legend.LegendForm.SQUARE);
        l.setFormSize(9f);
        l.setTextSize(11f);
        l.setXEntrySpace(4f);
        XAxis xAxis1 = barChart1.getXAxis();
        xAxis1.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis1.setValueFormatter(new MyDecimalValueFormatter());
        xAxis1.setGranularityEnabled(true);
        xAxis1.setDrawGridLines(false);
        barChart1.getDescription().setEnabled(false);
        barChart1.setPinchZoom(false);

        barChart1.setDrawBarShadow(true);
        barChart1.setDrawGridBackground(false);

        barChart1.getAxisLeft().setDrawGridLines(false);
        barChart1.getAxisRight().setGranularityEnabled(true);
        barChart1.getAxisRight().setValueFormatter(new MyDecimalValueFormatter());
        barChart1.getAxisLeft().setValueFormatter(new MyDecimalValueFormatter());
        barChart1.getAxisLeft().setGranularityEnabled(true);
        Legend l1 = barChart1.getLegend();

        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setForm(Legend.LegendForm.SQUARE);
        l.setFormSize(9f);
        l.setTextSize(11f);
        l.setXEntrySpace(4f);


        l1.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l1.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l1.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l1.setDrawInside(false);
        l1.setForm(Legend.LegendForm.SQUARE);
        l1.setFormSize(9f);
        l1.setTextSize(11f);
        l1.setXEntrySpace(4f);


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        recyclerView = findViewById(R.id.restaurantEarningAnalysisRecyclerView);
        textView = findViewById(R.id.textView33);
//        BarChart mBarChart = (BarChart) findViewById(R.id.barchartAnalysis);
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

                if(date.size() != 0) {
                    double highestSalesDay = 0D;
                    String dateName = "";
                    int totalCust = 0;
                    int highestOrderAtDay = 0;
                    moreDays.setVisibility(View.INVISIBLE);
                    int loopTill = 0;
                    int addToVal = 0;
                    if(date.size() > 7)
                        loopTill = date.size() - 7;
                    daysLeftToShow = 6;
                    ArrayList<BarEntry> values = new ArrayList<>();
                    Type types = new TypeToken<HashMap<String,HashMap<String,Integer>>>() {
                    }.getType();
                    int day = calendar.get(Calendar.DAY_OF_MONTH);
                    Gson gson1 = new Gson();
                    HashMap<String,HashMap<String,Integer>> mainMap = gson1.fromJson(user7daysTracker.getString(month,""),types);
                    ArrayList<BarEntry> value = new ArrayList<>();
//                    xAxis.setAxisMaximum(Float.parseFloat(date.get(date.size()-1)));
//                    xAxis.setAxisMinimum(Float.parseFloat(date.get(loopTill)));
                    for (int i = date.size() - 1; i >= loopTill; i--) {
                        daysLeftToShow--;

                        days.add(date.get(i) + "th " + month);
                        orders.add(totalORders.get(i) + "");
                        amounts.add(orderAmountList.get(i));
                        if(mainMap.containsKey(date.get(i) + "")) {
                            addToVal = 0;

                            HashMap<String, Integer> innerMap = new HashMap<>(mainMap.get(date.get(i) + ""));
                            for(String ii : innerMap.keySet()){
                                totalCust++;
                                addToVal++;
                            }
                            value.add(new BarEntry(Integer.parseInt(date.get(i)),addToVal));
                            Log.i("infoVALLLL",addToVal + "");
                        }

                        if(Double.parseDouble(orderAmountList.get(i)) > highestSalesDay){
                            dateName = date.get(i) + "th " + month;
                            highestSalesDay = Double.parseDouble(orderAmountList.get(i));
                            highestOrderAtDay = Integer.parseInt(totalORders.get(i));
                        }
                        totalOrdersMade += Integer.parseInt(totalORders.get(i));
                        totalAmountOrdersText += Double.parseDouble(orderAmountList.get(i));
//                        mBarChart.addBar(new BarModel(Float.parseFloat(orderAmountList.get(i)), 0xFF1BA4E6));
                        values.add(new BarEntry(Integer.parseInt(date.get(i)),Float.parseFloat(orderAmountList.get(i))));
                    }

                    BarDataSet barDataSet = new BarDataSet(values,"Month: " + month);
                    barDataSet.setColors(ColorTemplate.VORDIPLOM_COLORS);
                    barDataSet.setDrawValues(true);

                    ArrayList<IBarDataSet> dataSets = new ArrayList<>();
                    dataSets.add(barDataSet);
                    BarData data = new BarData(dataSets);

                    data.setValueFormatter(new MyDecimalValueFormatter());
                    totalCustomersLast7days.setText("Total Customer's: " + totalCust);
                    barChart.setData(data);
                    barChart.setFitBars(true);



                    barChart.animateY(1650);

                    BarDataSet barDataSet1 = new BarDataSet(value,"Month: " + month);
                    barDataSet1.setColors(ColorTemplate.VORDIPLOM_COLORS);
                    barDataSet1.setDrawValues(true);

                    ArrayList<IBarDataSet> dataSets1 = new ArrayList<>();
                    dataSets1.add(barDataSet1);
                    BarData data1 = new BarData(dataSets1);
                    data1.setValueFormatter(new MyDecimalValueFormatter());
                    barChart1.setData(data1);
                    barChart1.setFitBars(true);


                    barChart1.animateY(1150);

//                    LineDataSet set1 = new LineDataSet(value, "DataSet 1");
//
//                    ArrayList<ILineDataSet> dataSetss = new ArrayList<>();
//                    dataSetss.add(set1);
//                    // create a data object with the data sets
//                    LineData dataa = new LineData(dataSetss);
//
//                    // set data
//                    lineChart.setData(dataa);
//                    lineChart.animateY(1500);



                    barChart1.getLegend().setEnabled(false);
                    barChart.getLegend().setEnabled(false);
                    highestSalesDayText.setText("Date: " + dateName);
                    highestSalesAmountThatDay.setText("Amount: \u20b9" + highestSalesDay);
                    highestSalesOrderThatDay.setText("Order's: " + highestOrderAtDay + "");
                    totalAmount.setText("Total Transaction Amount: \u20b9" + new DecimalFormat("0.00").format(totalAmountOrdersText));
                    totalOrders.setText("Total Order's: " + totalOrdersMade + "");
                    recyclerView.setLayoutManager(linearLayoutManager);
                    recyclerView.setAdapter(new TackerAdapterAnalysis(days, days.get(0), orders, amounts));
                    totalAmountThatDay.setText("Total Transaction Amount: \u20b9" + orderAmountList.get(orderAmountList.size() - 1) + "");
                    totalOrderThatDay.setText("Total Order's: " + totalORders.get(totalORders.size() - 1) + "");
                    averageOrderThatDay.setText("Average Order Size: \u20b9" + new DecimalFormat("0.00").format(Double.parseDouble(orderAmountList.get(orderAmountList.size()-1))/Integer.parseInt(totalORders.get(totalORders.size()-1))));
                    dateThatDay.setText(date.get(date.size()-1) + "th " + month);
                    LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                            new IntentFilter("custom-message-analysis"));

                    progressBar.setVisibility(View.INVISIBLE);



//                    for (int i = daysLeftToShow; i >= 0; i--)
//                        mBarChart.addBar(new BarModel(0.f, 0xFF1BA4E6));
                }
//                }else{
//                    int remainingDays = date.size() - 7;
//                    for(int i = date.size() - 1; i >= remainingDays;i--){
//                        totalOrdersMade += Integer.parseInt(totalORders.get(i));
//                        days.add(date.get(i) + "th " + month);
//                        totalAmountOrdersText += Double.parseDouble(orderAmountList.get(i));
//                        orders.add(totalORders.get(i) + "");
//                        amounts.add(orderAmountList.get(i));
//                        mBarChart.addBar(new BarModel(Float.parseFloat(orderAmountList.get(i)), 0xFF1BA4E6));
//                    }
//                    totalAmount.setText("Total Transaction Amount: " + totalAmountOrdersText);
//                    totalOrders.setText("Total Order's: "  + totalOrdersMade + "");
//
//
//                    moreDays.setOnClickListener(click -> {
//                        totalOrdersMade = 0;
//                        totalAmountOrdersText = 0;
//                        moreDays.setVisibility(View.INVISIBLE);
//                        textView.setText("Showing last 14 days analysis");
//                        if(date.size() < 14){
//                            daysLeftToShow = 13;
//                            for(int i=date.size() - 1;i>=0;i--){
//                                daysLeftToShow--;
//                                totalOrdersMade += Integer.parseInt(totalORders.get(i));
//                                totalAmountOrdersText += Double.parseDouble(orderAmountList.get(i));
//                                mBarChart.addBar(new BarModel(Float.parseFloat(orderAmountList.get(i)), 0xFF1BA4E6));
//                            }
//                            totalAmount.setText(String.valueOf("Total Transaction Amount: " + totalAmountOrdersText));
//                            totalOrders.setText("Total Order's: "  + totalOrdersMade + "");
//
//                            for(int i = daysLeftToShow;i >= 0;i--)
//                                mBarChart.addBar(new BarModel(0.f, 0xFF1BA4E6));
//                        }else{
//                            int daysLeft = date.size() - 14;
//                            for(int i = date.size() - 1; i >= daysLeft;i--){
//                                totalOrdersMade += Integer.parseInt(totalORders.get(i));
//                                totalAmountOrdersText += Double.parseDouble(orderAmountList.get(i));
//                                mBarChart.addBar(new BarModel(Float.parseFloat(orderAmountList.get(i)), 0xFF1BA4E6));
//                            }
//                            totalAmount.setText(String.valueOf("Total Transaction Amount: " + totalAmountOrdersText));
//                            totalOrders.setText("Total Order's: "  + totalOrdersMade + "");
//                        }
//                    });
//                    recyclerView.setLayoutManager(linearLayoutManager);
//                    recyclerView.setAdapter(new TackerAdapterAnalysis(days,days.get(0),orders,amounts));
//                    totalAmountThatDay.setText("Total Transaction Amount: \u20b9" + orderAmountList.get(orderAmountList.size()-1) + "");
//                    totalOrderThatDay.setText("Total Order's: " + totalORders.get(totalORders.size()-1) + "");
//                    LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
//                            new IntentFilter("custom-message-analysis"));
//                }

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
            progressBar.setVisibility(View.VISIBLE);
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
            averageOrderThatDay.setText("Average Order Size: \u20b9" + new DecimalFormat("0.00").format(Double.parseDouble(amounts)/Integer.parseInt(orders)));
            dateThatDay.setText( MonthName);
            new Handler().postDelayed(() -> progressBar.setVisibility(View.INVISIBLE),1155);
//            Toast.makeText(context, "" + orders + " " + amounts + " " + MonthName, Toast.LENGTH_SHORT).show();

        }
    };

//    public class MyYAxisValueFormatter implements IValueFormatter {
//
//        private DecimalFormat mFormat;
//
//        public MyYAxisValueFormatter(){
//            mFormat = new DecimalFormat("###,###,##0");
//        }
//
//        @Override
//        public String getFormattedValue(float value, YAxis yAxis) {
//            return mFormat.format(value);
//        }
//    }

    public class MyDecimalValueFormatter extends ValueFormatter {

        private DecimalFormat mFormat;

        public MyDecimalValueFormatter() {
            mFormat = new DecimalFormat("#");
        }

        @Override
        public String getFormattedValue(float value) {
            return mFormat.format(value);
        }
    }
}