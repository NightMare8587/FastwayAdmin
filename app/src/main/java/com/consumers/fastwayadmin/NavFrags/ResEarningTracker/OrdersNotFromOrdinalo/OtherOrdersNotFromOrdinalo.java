package com.consumers.fastwayadmin.NavFrags.ResEarningTracker.OrdersNotFromOrdinalo;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.consumers.fastwayadmin.R;
import com.google.common.reflect.TypeToken;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class OtherOrdersNotFromOrdinalo extends AppCompatActivity {
    EditText searchDishName;
    RecyclerView recyclerView;
    Button button;
    List<String> dishName = new ArrayList<>();
    Button seeAllDishAdded;
    String json;
    String month;
    List<String> dishImage = new ArrayList<>();
    List<String> dishType = new ArrayList<>();
    List<String> menuType = new ArrayList<>();
    Calendar calendar = Calendar.getInstance();
    String[] monthName = {"January", "February",
            "March", "April", "May", "June", "July",
            "August", "September", "October", "November",
            "December"};
    SharedPreferences loginInfo;
    DatabaseReference databaseReference;
    Gson gson;
    List<String> selectedDishName = new ArrayList<>();
    List<String> selectedDishImage = new ArrayList<>();
    List<String> selectedDishType = new ArrayList<>();
    List<String> selectedDishMenuType = new ArrayList<>();

    FirebaseAuth auth = FirebaseAuth.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_orders_not_from_ordinalo);
        loginInfo = getSharedPreferences("loginInfo",MODE_PRIVATE);
        searchDishName = findViewById(R.id.enterDishNameToSearch);
        recyclerView = findViewById(R.id.recyclerViewhDish);
        button = findViewById(R.id.proceedToAddOrders);
        recyclerView.setLayoutManager(new LinearLayoutManager(OtherOrdersNotFromOrdinalo.this));
        seeAllDishAdded = findViewById(R.id.seeAllAddedDishOrders);
        databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(loginInfo.getString("state","")).child(loginInfo.getString("state","")).child(Objects.requireNonNull(auth.getUid())).child("List of Dish");
        LocalBroadcastManager.getInstance(OtherOrdersNotFromOrdinalo.this).registerReceiver(mMessageReceiver,
                new IntentFilter("sendback-dishdetails"));
        searchDishName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                dishName.clear();
                dishType.clear();
                dishImage.clear();
                menuType.clear();
                String s = charSequence.toString().toLowerCase(Locale.ROOT);
                if(s.equals("")) {
                    dishName.clear();
                    menuType.clear();
                    dishImage.clear();
                    dishType.clear();
                    seeAllDishAdded.setVisibility(View.INVISIBLE);
                    recyclerView.setAdapter(new recyclerOrderAdp(dishName,dishImage,dishType,menuType));
                    return;
                }

                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            dishName.clear();
                            dishType.clear();
                            dishImage.clear();
                            menuType.clear();
                            for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                                for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                                    if(Objects.requireNonNull(dataSnapshot1.getKey()).toLowerCase(Locale.ROOT).contains(s)){
                                        dishName.add(dataSnapshot1.getKey());
                                        dishImage.add(dataSnapshot1.child("image").getValue(String.class));
                                        dishType.add(dataSnapshot1.child("dishType").getValue(String.class));
                                        if(!Objects.equals(dataSnapshot.getKey(), "Combo"))
                                        menuType.add(dataSnapshot1.child("menuType").getValue(String.class));
                                        else
                                            menuType.add(dataSnapshot.getKey());
                                    }
                                }
                            }

                            Log.i("infoDish",dishName.toString());
                            recyclerView.setAdapter(new recyclerOrderAdp(dishName,dishImage,dishType,menuType));

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        seeAllDishAdded.setOnClickListener(click -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(OtherOrdersNotFromOrdinalo.this);
            builder.setTitle("List of dish").setMessage("Below are the dish added. Click on dish to remove them")
                    .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
            LinearLayout linearLayout = new LinearLayout(OtherOrdersNotFromOrdinalo.this);
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            ListView listView = new ListView(OtherOrdersNotFromOrdinalo.this);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(OtherOrdersNotFromOrdinalo.this, android.R.layout.simple_list_item_1,selectedDishName);
            listView.setAdapter(adapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Toast.makeText(OtherOrdersNotFromOrdinalo.this, "Dish Removed :)", Toast.LENGTH_SHORT).show();
                    selectedDishImage.remove(i);
                    selectedDishName.remove(i);
                    selectedDishMenuType.remove(i);
                    selectedDishType.remove(i);

                    adapter.notifyDataSetChanged();

                    if(selectedDishName.size() == 0) {
                        seeAllDishAdded.setVisibility(View.INVISIBLE);
                    }
                }
            });
            linearLayout.addView(listView);
            builder.setView(linearLayout);
            builder.create().show();

        });

        button.setOnClickListener(click -> {
            if(selectedDishName.size() == 0){
                Toast.makeText(this, "Add some dish to continue :)", Toast.LENGTH_SHORT).show();
                return;
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(click.getContext());
            LinearLayout linearLayout = new LinearLayout(click.getContext());
            EditText editText = new EditText(click.getContext());
            editText.setHint("Enter Amount");
            editText.setInputType(InputType.TYPE_CLASS_NUMBER);
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            linearLayout.addView(editText);
            builder.setView(linearLayout);
            builder.setTitle("Amount").setMessage("Enter amount paid by user in below field").setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int ii) {
                    SharedPreferences storeOrdersForAdminInfo = getSharedPreferences("StoreOrders",MODE_PRIVATE);
                    SharedPreferences.Editor storeEditor = storeOrdersForAdminInfo.edit();
                    SharedPreferences restaurantDailyTrack;
                    SharedPreferences.Editor restaurantTrackEditor;
                    SharedPreferences storeDailyTotalOrdersMade = getSharedPreferences("RestaurantDailyStoreForAnalysis",MODE_PRIVATE);
                    SharedPreferences.Editor storeDailyEditor = storeDailyTotalOrdersMade.edit();
                    restaurantDailyTrack = getSharedPreferences("RestaurantTrackingDaily", Context.MODE_PRIVATE);
                    restaurantTrackEditor = restaurantDailyTrack.edit();
                    if(editText.getText().toString().equals("") || editText.getText().toString().equals("0")){
                        Toast.makeText(OtherOrdersNotFromOrdinalo.this, "Invalid Input", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    month = monthName[calendar.get(Calendar.MONTH)];

                    double amt = Double.parseDouble(editText.getText().toString());
                    if (restaurantDailyTrack.contains("totalOrdersToday")) {
                        int val = Integer.parseInt(restaurantDailyTrack.getString("totalOrdersToday", ""));
                        val = val + 1;
                        restaurantTrackEditor.putString("totalOrdersToday", String.valueOf(val));
                    } else {
                        restaurantTrackEditor.putString("totalOrdersToday", String.valueOf(1));
                    }
                    if (restaurantDailyTrack.contains("totalTransactionsToday")) {
                        double val = Double.parseDouble(restaurantDailyTrack.getString("totalTransactionsToday", ""));
                        val = val + amt;
                        restaurantTrackEditor.putString("totalTransactionsToday", String.valueOf(val));
                    } else {
                        restaurantTrackEditor.putString("totalTransactionsToday", String.valueOf(amt));
                    }

                    restaurantTrackEditor.apply();

                    if (storeOrdersForAdminInfo.contains(month)) {
                        Type type = new TypeToken<List<List<String>>>() {
                        }.getType();
                        gson = new Gson();
                        json = storeOrdersForAdminInfo.getString(month, "");
                        List<List<String>> mainDataList = gson.fromJson(json, type);
                        List<String> date = new ArrayList<>(mainDataList.get(0));
                        List<String> transID = new ArrayList<>(mainDataList.get(1));
                        List<String> userID = new ArrayList<>(mainDataList.get(2));
                        List<String> orderAmountList = new ArrayList<>(mainDataList.get(3));
//                                    HashMap<String,String> map = new HashMap<String,String>(mainDataList.get(4));

                        date.add(System.currentTimeMillis() + "");
                        transID.add("Custom Order");
                        userID.add("id");
                        orderAmountList.add(amt + "");

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
                            totalAmount += amt;
                            totalAmounts.set(totalAmounts.size() - 1, String.valueOf(totalAmount));

                            int totalOrder = Integer.parseInt(totalOrdersPlaced.get(totalOrdersPlaced.size() - 1));
                            totalOrder += 1;
                            totalOrdersPlaced.set(totalOrdersPlaced.size() - 1, String.valueOf(totalOrder));
                        } else {
                            days.add(String.valueOf(day));
                            totalOrdersPlaced.add("1");
                            totalAmounts.add(String.valueOf(amt));
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

                        date.add(System.currentTimeMillis() + "");
                        transID.add("Custom Order");
                        userID.add("id");
                        orderAmountList.add(amt + "");
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
                        totalAmounts.add(String.valueOf(amt));
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
                        Date date = new Date(System.currentTimeMillis());
                        cell = row.createCell(0);
                        cell.setCellValue(dateFormat.format(date));
                        cell = row.createCell(1);
                        cell.setCellValue("id");
                        cell = row.createCell(2);
                        cell.setCellValue("Custom Order");
                        cell = row.createCell(3);
                        cell.setCellValue("\u20B9" + amt);
                        Log.i("info", max + "");
                        FileOutputStream fileOutputStream = new FileOutputStream(file);
                        workbook.write(fileOutputStream);
                        fileOutputStream.flush();
                        fileOutputStream.close();
                        Toast.makeText(OtherOrdersNotFromOrdinalo.this, "Completed", Toast.LENGTH_SHORT).show();
                        workbook.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    SharedPreferences storeForDishAnalysis = getSharedPreferences("DishAnalysis",MODE_PRIVATE);
                    SharedPreferences.Editor dishAnalysis = storeForDishAnalysis.edit();
                    if (storeForDishAnalysis.contains("DishAnalysisMonthBasis")) {
                        gson = new Gson();
                        Type type = new TypeToken<HashMap<String, HashMap<String, Integer>>>() {
                        }.getType();
                        String storedHash = storeForDishAnalysis.getString("DishAnalysisMonthBasis", "");
                        HashMap<String, HashMap<String, Integer>> myMap = gson.fromJson(storedHash, type);
                        HashMap<String, Integer> map;

                        if (myMap.containsKey(month)) {
                            map = new HashMap<>(Objects.requireNonNull(myMap.get(month)));
                            Log.i("checking", map.toString());
                            for (int k = 0; k < selectedDishName.size(); k++) {
                                if (map.containsKey(selectedDishName.get(k))) {
                                    String currDishName = selectedDishName.get(k);
                                    int val = Objects.requireNonNull(map.get(selectedDishName.get(k)));
                                    val++;
                                    map.put(selectedDishName.get(k), val);

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
                                    map.put(selectedDishName.get(k), 1);
                                }
                            }
                        } else {
                            map = new HashMap<>();
                            for (int i = 0; i < selectedDishName.size(); i++) {
                                map.put(selectedDishName.get(i), 1);
                            }
                        }
                        myMap.put(month, map);

                        dishAnalysis.putString("DishAnalysisMonthBasis", gson.toJson(myMap));
                    } else {
                        HashMap<String, HashMap<String, Integer>> map = new HashMap<>();
                        HashMap<String, Integer> myMap = new HashMap<>();
                        for (int j = 0; j < selectedDishName.size(); j++) {
                            myMap.put(selectedDishName.get(j), 1);
                        }
                        map.put(month, myMap);
                        gson = new Gson();
                        dishAnalysis.putString("DishAnalysisMonthBasis", gson.toJson(map));
                    }
                    dishAnalysis.apply();

                    SharedPreferences dailyInsightStoring = getSharedPreferences("DailyInsightsStoringData",MODE_PRIVATE);
                    SharedPreferences.Editor dailyInsightEditor = dailyInsightStoring.edit();

                    if(dailyInsightStoring.contains(month)) {
                        gson = new Gson();
                        Type type = new TypeToken<HashMap<String, HashMap<String, String>>>() {
                        }.getType();
                        HashMap<String, HashMap<String, String>> mainMap = gson.fromJson(dailyInsightStoring.getString(month, ""), type);
                        int day = calendar.get(Calendar.DAY_OF_MONTH);

                        if (mainMap.containsKey(day + "")) {
                            HashMap<String, String> innerMap = new HashMap<>(mainMap.get(day + ""));
                            Type ordersType = new TypeToken<List<String>>() {
                            }.getType();

                            List<String> ordersList = new ArrayList<>(gson.fromJson(innerMap.get("orderList"), ordersType));
                            ordersList.add(System.currentTimeMillis() + "");

                            innerMap.put("orderList", gson.toJson(ordersList));

                            List<String> custList = new ArrayList<>(gson.fromJson(innerMap.get("custList"), ordersType));
                            if (!custList.contains("id")) {
                                custList.add("id");
                                innerMap.put("custList", gson.toJson(custList));
                            }

                            Type timeZoneMap = new TypeToken<HashMap<String, Integer>>() {
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
                            prevVal += amt;
                            innerMap.put("revenueTotal", prevVal + "");

                            Type timeZoneRevenue = new TypeToken<HashMap<String, String>>() {
                            }.getType();

                            HashMap<String,String> revenueMap = new HashMap<>(gson.fromJson(innerMap.get("revenueMap"),timeZoneRevenue));

                            if(revenueMap.containsKey(hours + "")){
                                double prevs = Double.parseDouble(revenueMap.get(hours + ""));
                                prevs += amt;
                                revenueMap.put(hours + "",prevs + "");
                            }else
                                revenueMap.put(hours + "",amt + "");

                            innerMap.put("revenueMap",gson.toJson(revenueMap));

                            mainMap.put(day + "",innerMap);

                        }else{
                            gson = new Gson();
                            HashMap<String,String> innerMap = new HashMap<>();
                            List<String> ordersList = new ArrayList<>();
                            ordersList.add(System.currentTimeMillis() + "");

                            innerMap.put("orderList",gson.toJson(ordersList));

                            List<String> custList = new ArrayList<>();
                            custList.add("id");
                            innerMap.put("custList",gson.toJson(custList));

                            HashMap<String,Integer> timeMap = new HashMap<>();
                            int hours = calendar.get(Calendar.HOUR_OF_DAY);
                            timeMap.put(hours + "",1);
                            innerMap.put("timeZoneMap",gson.toJson(timeMap));

                            innerMap.put("revenueTotal",amt + "");

                            HashMap<String,String> revenueMap = new HashMap<>();
                            revenueMap.put(hours + "",amt + "");
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
                        custList.add("id");
                        innerMap.put("custList",gson.toJson(custList));

                        HashMap<String,Integer> timeMap = new HashMap<>();
                        int hours = calendar.get(Calendar.HOUR_OF_DAY);
                        timeMap.put(hours + "",1);
                        innerMap.put("timeZoneMap",gson.toJson(timeMap));

                        innerMap.put("revenueTotal",amt + "");

                        HashMap<String,String> revenueMap = new HashMap<>();
                        revenueMap.put(hours + "",amt + "");
                        innerMap.put("revenueMap",gson.toJson(revenueMap));

                        HashMap<String,HashMap<String,String>> mainMap = new HashMap<>();
                        mainMap.put(day + "",innerMap);

                        dailyInsightEditor.putString(month,gson.toJson(mainMap));
                        dailyInsightEditor.apply();




                    }
                    Toast.makeText(OtherOrdersNotFromOrdinalo.this, "Added Successfully", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }).setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            }).create();

            builder.show();

        });

    }

    public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @SuppressLint("SetTextI18n")
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("infoY",intent.getStringExtra("dishName"));
            selectedDishName.add(intent.getStringExtra("dishName"));
            selectedDishImage.add(intent.getStringExtra("dishImage"));
            selectedDishMenuType.add(intent.getStringExtra("menuType"));
            selectedDishType.add(intent.getStringExtra("dishType"));
            seeAllDishAdded.setVisibility(View.VISIBLE);
        }
    };
}