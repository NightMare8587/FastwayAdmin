package com.consumers.fastwayadmin.HomeScreen;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentManager;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
//import com.aspose.cells.Workbook;
import com.consumers.fastwayadmin.Chat.RandomChatFolder.RandomChatWithUsers;
import com.consumers.fastwayadmin.Info.RestaurantDocuments.ReUploadDocumentsAgain;
import com.consumers.fastwayadmin.Info.RestaurantDocuments.UploadRemainingDocs;
import com.consumers.fastwayadmin.NavFrags.AccountSettingsFragment;
import com.consumers.fastwayadmin.NavFrags.BankVerification.SelectPayoutMethodType;
import com.consumers.fastwayadmin.NavFrags.CashCommission.CashTransactionCommissionActivity;
import com.consumers.fastwayadmin.NavFrags.FastwayPremiumActivites.FastwayPremiums;
import com.consumers.fastwayadmin.NavFrags.FastwayPremiumActivites.NotifyAdminSubscribePremium;
import com.consumers.fastwayadmin.NavFrags.HomeFrag;
import com.consumers.fastwayadmin.NavFrags.MenuFrag;
import com.consumers.fastwayadmin.NavFrags.ReplaceOrders.ReplaceOrderRequests;
import com.consumers.fastwayadmin.NavFrags.TablesFrag;
import com.consumers.fastwayadmin.NavFrags.BankVerification.VendorDetailsActivity;
import com.consumers.fastwayadmin.NotificationActivity;
import com.consumers.fastwayadmin.R;
import com.consumers.fastwayadmin.RandomChatNoww;
import com.developer.kalert.KAlertDialog;
import com.gauravk.bubblenavigation.BubbleNavigationConstraintView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.common.reflect.TypeToken;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
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
import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class HomeScreen extends AppCompatActivity {

    BubbleNavigationConstraintView bubble;
    String URL = "https://fcm.googleapis.com/fcm/send";
    FragmentManager manager;
    String url = "https://intercellular-stabi.000webhostapp.com/payouts/prodInitialise.php";
    SharedPreferences sharedPreferences;
    String subRefID;
    SharedPreferences calenderForExcel;
    SharedPreferences trackOfAllGeneratedFiles;
    FirebaseStorage storage;
    StorageReference storageReference;
    SharedPreferences.Editor editorToTrackFiles;
    SharedPreferences adminPrem;

    SharedPreferences.Editor premEditor;
    SharedPreferences.Editor editor;
    Calendar calendar = Calendar.getInstance();
    String[] monthName = {"January", "February",
            "March", "April", "May", "June", "July",
            "August", "September", "October", "November",
            "December"};
    String json;
    Gson gson;
    DatabaseReference checkForBank;
    SharedPreferences.Editor myEditor;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    long currentTime = System.currentTimeMillis();
    Timer timer = new Timer();
    DatabaseReference resRef;
    String UID;
    DatabaseReference reference;
    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint("CommitPrefEdits")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        initialise();
        sharedPreferences = getSharedPreferences("loginInfo",MODE_PRIVATE);
        int year = calendar.get(Calendar.YEAR);
        myEditor = sharedPreferences.edit();
//        myEditor.remove("FileGeneratedExcel");
        myEditor.putString("payoutMethodChoosen","imps");
        if(!sharedPreferences.contains("currentYear"))
        {
            myEditor.putString("currentYear",year + "");
        }
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        SharedPreferences storeImages = getSharedPreferences("storeImages",MODE_PRIVATE);
        SharedPreferences.Editor imageEdit = storeImages.edit();

        myEditor.apply();
        manager.beginTransaction().replace(R.id.homescreen,new HomeFrag()).commit();
        calenderForExcel = getSharedPreferences("CalenderForExcel",MODE_PRIVATE);
        editor = calenderForExcel.edit();
        adminPrem = getSharedPreferences("AdminPremiumDetails",MODE_PRIVATE);
        premEditor = adminPrem.edit();
        ConnectivityManager cm =
                (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        FirebaseMessaging.getInstance().subscribeToTopic("FastwayQueryDB");
        trackOfAllGeneratedFiles= getSharedPreferences("TrackOfAllInsights",MODE_PRIVATE);
        editorToTrackFiles = trackOfAllGeneratedFiles.edit();

//        SharedPreferences clear = getSharedPreferences("DailyInsightsStoringData",MODE_PRIVATE);
//        SharedPreferences.Editor clearEdit = clear.edit();
//        clearEdit.clear().apply();
        resRef = FirebaseDatabase.getInstance().getReference().getRoot().child("Admin").child(Objects.requireNonNull(UID)).child("Restaurant Documents");
        resRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists() && snapshot.hasChild("reasonForCancel")){
                    AlertDialog.Builder alert = new AlertDialog.Builder(HomeScreen.this);
                    String reason = snapshot.child("reasonForCancel").getValue(String.class);
                    alert.setTitle("Error").setMessage("Your restaurant registration is denied by Ordinalo for following reason's:\n\n" + reason + "\n\nYou can submit another response for restaurant registration")
                            .setPositiveButton("Re-Submit", (dialogInterface, i) -> {
                                startActivity(new Intent(HomeScreen.this, ReUploadDocumentsAgain.class));
                                dialogInterface.dismiss();
                            }).setNegativeButton("Skip", (dialogInterface, i) -> dialogInterface.dismiss()).create();

                    alert.show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        AsyncTask.execute(() -> {
            DatabaseReference checkRef = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(sharedPreferences.getString("state","")).child(sharedPreferences.getString("state","")).child(auth.getUid()).child("List of Dish");
            checkRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                        for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                            if(!storeImages.contains(dataSnapshot1.getKey())){
                                imageEdit.putString(dataSnapshot1.getKey(),dataSnapshot1.child("image").getValue(String.class));
                            }
                        }
                        imageEdit.apply();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        });

        DatabaseReference adminCheck = FirebaseDatabase.getInstance().getReference().getRoot().child("Admin").child(UID);
        adminCheck.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists() && snapshot.hasChild("freeTrialDate")){
                    long currTime = Long.parseLong(String.valueOf(snapshot.child("freeTrialDate").getValue()));

                    if(currTime <= System.currentTimeMillis()){
                        SharedPreferences adminPrem = getSharedPreferences("AdminPremiumDetails",MODE_PRIVATE);
                        SharedPreferences.Editor premEdit = adminPrem.edit();
                        premEdit.putString("status", "not active");
                        premEdit.apply();
                        AlertDialog.Builder builder = new AlertDialog.Builder(HomeScreen.this);
                        builder.setCancelable(false);
                        builder.setTitle("Trial Finished").setMessage("Your current free trial is finished\nSubscribe premium")
                                .setPositiveButton("Subscribe", (dialog, which) -> startActivity(new Intent(HomeScreen.this, FastwayPremiums.class)))
                                .setNegativeButton("No", (dialog, which) -> dialog.dismiss()).create();
                        builder.show();
                    }else{
                        SharedPreferences adminPrem = getSharedPreferences("AdminPremiumDetails",MODE_PRIVATE);
                        SharedPreferences.Editor premEdit = adminPrem.edit();
                        premEdit.putString("status", "active");
                        premEdit.apply();
//
//                        SharedPreferences storedOrders = getSharedPreferences("RestaurantDailyStoreForAnalysis",MODE_PRIVATE);
//                        String month = monthName[calendar.get(Calendar.MONTH)];
//
//                        if(storedOrders.contains(month)){
//
//                        }
                    }
                }else{
                    DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference().getRoot().child("Admin").child(Objects.requireNonNull(auth.getUid()));
                    databaseReference1.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.hasChild("subscriptionStatus")){
                                long time = Long.parseLong(String.valueOf(snapshot.child("subscriptionStatus").getValue()));
                                if(System.currentTimeMillis() > time){
                                    premEditor.putString("status","not active");
                                    premEditor.apply();
                                    AlertDialog.Builder builder = new AlertDialog.Builder(HomeScreen.this);
                                    builder.setTitle("ReSubscribe").setMessage("Your Subscription Period is over. ReSubscribe Now")
                                            .setPositiveButton("ReSubscribe", (dialog, which) -> {
                                                dialog.dismiss();
                                                startActivity(new Intent(HomeScreen.this,FastwayPremiums.class));
                                            }).setNegativeButton("Later", (dialog, which) -> {

                                            }).create();
                                    builder.show();
                                }else{
                                    premEditor.putString("status","active");
                                    premEditor.apply();
                                }
                            }else{
                                premEditor.putString("status","not active");
                                premEditor.apply();
                                if(adminPrem.contains("lastNotifiedPrem")){
                                    if(172800000L + System.currentTimeMillis() < Long.parseLong(adminPrem.getString("lastNotifiedPrem",""))){
                                        startActivity(new Intent(HomeScreen.this, NotifyAdminSubscribePremium.class));
                                    }
                                }else
                                {
                                    premEditor.putString("lastNotifiedPrem",String.valueOf(System.currentTimeMillis()));
                                    premEditor.apply();
                                }
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
        SharedPreferences last7daysReport = getSharedPreferences("last7daysReport",MODE_PRIVATE);
        SharedPreferences.Editor last7daysReportEdit = last7daysReport.edit();
//        last7daysReportEdit.putString("daysTracked","5");
        last7daysReportEdit.apply();
        SharedPreferences storeOrders = getSharedPreferences("RestaurantDailyStoreForAnalysis",MODE_PRIVATE);
        new Thread(() -> {
            PdfDocument pdfDocument = new PdfDocument();
            Paint myPaint = new Paint();
            PdfDocument.PageInfo myPage = new PdfDocument.PageInfo.Builder(2080, 2040, 1).create();
            PdfDocument.Page page = pdfDocument.startPage(myPage);

            Paint text = new Paint();
            Canvas canvas = page.getCanvas();

            text.setTextAlign(Paint.Align.LEFT);
            text.setTextSize(90);
            text.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
            canvas.drawText("Last 7 days Report", 100, 155, text);
            text.setTextSize(75);
            canvas.drawText("From " + "12" + " to " + "24" + " " + "October", 100, 265, text);
            text.setTextSize(58);
            text.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
            canvas.drawText("Total Orders Made: " + "11025", 100, 385, text);
            canvas.drawText("Total Transaction Amount: \u20b9" + "152679", 100, 485, text);
            text.setTextSize(65);
            text.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
            canvas.drawText("Highest Sales", 100, 580, text);
            text.setTextSize(50);
            text.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
            canvas.drawText("Date: " + "16" + "th" + " October", 100, 650, text);
            canvas.drawText("Total Orders: " + "55", 100, 715, text);
            canvas.drawText("Total Amount: \u20b9" + "3625", 100, 785, text);

            text.setTextSize(65);
            text.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
            canvas.drawText("Total Customers", 100, 875, text);

            text.setTextSize(50);
            text.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
            canvas.drawText("Customers: " + "560", 100, 950, text);


//            if (last7daysReport.contains("lastAnalysisHashMap")) {
//                Type typeo = new TypeToken<HashMap<String, String>>() {
//                }.getType();
//                HashMap<String, String> prevMap = new HashMap<>(gson.fromJson(last7daysReport.getString("lastAnalysisHashMap", ""), typeo));
//                Double prevSalesAmt = Double.parseDouble(prevMap.get("totalSales"));
//                int ordersMadeTotal = Integer.parseInt(prevMap.get("totalOrders"));
//                int totalCustomersTotal = Integer.parseInt(prevMap.get("totalCustomers"));
                text.setTextSize(90);
                text.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                canvas.drawText("Compare with last 7 days", 600, 1080, text);
                text.setTextSize(58);
                text.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));

                double data1 = ((145682D - 157896D) / 157896D) * 100;
//                if (totalSalesThatPeriod > prevSalesAmt) {
//                    canvas.drawText("Total Sales: Increase By " + new DecimalFormat("0.00").format(data1) + "%", 100, 1160, text);
//                } else {
                    canvas.drawText("Total Sales: Decrease By " + new DecimalFormat("0.00").format(data1) + "%", 100, 1190, text);
//                }

                double data2 = (double) ((525D - 625D) / 625D) * 100;
//                if (totalOrders > ordersMadeTotal) {
//                    canvas.drawText("Total Orders: Increase By" + new DecimalFormat("0.00").format(data2) + "%", 100, 1235, text);
//                } else {

                    canvas.drawText("Total Orders: ↓ Decrease By " + new DecimalFormat("0.00").format(data2) + "%", 100, 1270, text);
//                }

                double data3 = (double) ((896D - 750D) / 750D) * 100;
//                if (totalCust > totalCustomersTotal) {
//                    canvas.drawText("Total Customers: Increase By" + new DecimalFormat("0.00").format(data3) + "%", 100, 1310, text);
//                } else
                    canvas.drawText("Total Customers: ↑ Increase By " + new DecimalFormat("0.00").format(data3) + "%", 100, 1345, text);

//            }

            canvas.drawText("For other info, Check Premium Activity", 100, 1470, text);
            text.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
            text.setTextSize(52);
            canvas.drawText("Contact Ordinalo", 1200, 1745, text);
            canvas.drawText("Phone: +91-8076531395", 1200, 1815, text);
            canvas.drawText("Email: ordinalo.services@gmail.com", 1200, 1885, text);

            pdfDocument.finishPage(page);
            String fileName = "/MonthlyReportTrackerhhhh" + ".pdf";
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) + fileName);

            try {
                pdfDocument.writeTo(new FileOutputStream(file));
                runOnUiThread(() -> Toast.makeText(HomeScreen.this, "Generated", Toast.LENGTH_SHORT).show());
            } catch (IOException e) {
                e.printStackTrace();
            }

            pdfDocument.close();
        });

        new Thread(() -> {
            SharedPreferences dailyInsights = getSharedPreferences("DailyInsightsStoringData",MODE_PRIVATE);
            SharedPreferences.Editor editor = dailyInsights.edit();

            String month = monthName[calendar.get(Calendar.MONTH)];
            int day = calendar.get(Calendar.DAY_OF_MONTH);

//            editor.putString("lastDay","18");
//            editor.apply();

            if(dailyInsights.contains(month)){
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Toast.makeText(HomeScreen.this, "First", Toast.LENGTH_SHORT).show();
//                    }
//                });
                gson = new Gson();
                Type type = new TypeToken<HashMap<String, HashMap<String, String>>>() {
                }.getType();
                HashMap<String,HashMap<String,String>> mainMap = gson.fromJson(dailyInsights.getString(month,""),type);

                if(day != 1 ){

                    day--;
//                    day = 19;
                    if(dailyInsights.contains("lastDay") && dailyInsights.getString("lastDay","").equals(day + ""))
                        return;
                    if(mainMap.containsKey(day + "")){
                        Log.i("checking","here");
                        Type typeo = new TypeToken<List<String>>() {
                        }.getType();
                        HashMap<String,String> innerMap = new HashMap<>(mainMap.get(day + ""));

                        List<String> orderList = new ArrayList<>(gson.fromJson(innerMap.get("orderList"),typeo));
                        List<String> custList = new ArrayList<>(gson.fromJson(innerMap.get("custList"),typeo));
                        String revenueTotal = innerMap.get("revenueTotal");

                        PdfDocument pdfDocument = new PdfDocument();
                        Paint myPaint = new Paint();
                        PdfDocument.PageInfo myPage = new PdfDocument.PageInfo.Builder(2080, 2040, 1).create();
                        PdfDocument.Page page = pdfDocument.startPage(myPage);

                        Paint text = new Paint();
                        Canvas canvas = page.getCanvas();

                        text.setTextAlign(Paint.Align.LEFT);
                        text.setTextSize(90);
                        text.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                        canvas.drawText("Daily Restaurant Insights", 100, 155, text);
                        text.setTextSize(75);
                        canvas.drawText(day + "th " + month, 100, 265, text);
                        text.setTextSize(58);
                        text.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
                        canvas.drawText("Total Orders Made: " + orderList.size(), 100, 385, text);
                        canvas.drawText("Total Transaction Amount: \u20b9" + revenueTotal, 100, 485, text);
//                        text.setTextSize(65);
//                        text.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
//                        canvas.drawText("Highest Sales", 100, 580, text);
//                        text.setTextSize(50);
//                        text.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
//                        canvas.drawText("Date: " + "16" + "th" + " October", 100, 650, text);
//                        canvas.drawText("Total Orders: " + "55", 100, 715, text);
//                        canvas.drawText("Total Amount: \u20b9" + "3625", 100, 785, text);

                        text.setTextSize(65);
                        text.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                        canvas.drawText("Total Customers", 100, 580, text);

                        text.setTextSize(50);
                        text.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
                        canvas.drawText("Customers: " + custList.size(), 100, 650, text);


            if (dailyInsights.contains("lastAnalysisHashMap")) {
                Type typeos = new TypeToken<HashMap<String, String>>() {
                }.getType();
                double totalOrders = Double.parseDouble(orderList.size() + "");
                double totalCust = Double.parseDouble(custList.size() + "");
                Log.i("checking","here");
                Double totalSalesThatPeriod = Double.parseDouble(revenueTotal);
                HashMap<String, String> prevMap = new HashMap<>(gson.fromJson(dailyInsights.getString("lastAnalysisHashMap", ""), typeos));
                Double prevSalesAmt = Double.parseDouble(prevMap.get("totalSales"));
                double ordersMadeTotal = Double.parseDouble(prevMap.get("totalOrders"));
                double totalCustomersTotal = Double.parseDouble(prevMap.get("totalCustomers"));
                        text.setTextSize(90);
                        text.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                        canvas.drawText("Compare with previous day", 600, 760, text);
                        text.setTextSize(58);
                        text.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
                        double data1 = ((totalSalesThatPeriod - prevSalesAmt) / prevSalesAmt) * 100;
                if (totalSalesThatPeriod > prevSalesAmt) {
                    canvas.drawText("Total Sales: ↑ Increase By " + new DecimalFormat("0.00").format(data1) + "%", 100, 860, text);
                    canvas.drawText("Previous Sales \u20b9" + prevSalesAmt, 100, 940, text);
                } else {
                        canvas.drawText("Total Sales: ↓ Decrease By " + new DecimalFormat("0.00").format(data1) + "%", 100, 860, text);
                    canvas.drawText("Previous Sales \u20b9" + prevSalesAmt, 100, 940, text);
                }

                        double data2 =  ((totalOrders - ordersMadeTotal) / ordersMadeTotal) * 100;
                if (totalOrders > ordersMadeTotal) {
                    canvas.drawText("Total Orders: ↑ Increase By: " + new DecimalFormat("0.00").format(data2) + "%", 100, 1010, text);
                    canvas.drawText("Previous Orders: " + ordersMadeTotal, 100, 1080, text);
                } else {
                        canvas.drawText("Total Orders: ↓ Decrease By " + new DecimalFormat("0.00").format(data2) + "%", 100, 1010, text);
                    canvas.drawText("Previous Orders: " + ordersMadeTotal, 100, 1080, text);
                }


                        double data3 =  ((totalCust - totalCustomersTotal) / totalCustomersTotal) * 100;
                if (totalCust > totalCustomersTotal) {
                    canvas.drawText("Total Customers: ↑ Increase By" + new DecimalFormat("0.00").format(data3) + "%", 100, 1150, text);
                    canvas.drawText("Previous Customers: " + totalCustomersTotal, 100, 1220, text);
                } else {
                    canvas.drawText("Total Customers: ↓ Decrease By " + new DecimalFormat("0.00").format(data3) + "%", 100, 1150, text);
                    canvas.drawText("Previous Customers: " + totalCustomersTotal, 100, 1220, text);
                }
            }

                        canvas.drawText("For other info, Check Premium Activity", 100, 1320, text);
                        text.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                        text.setTextSize(52);
                        canvas.drawText("Contact Ordinalo", 1200, 1360, text);
                        canvas.drawText("Phone: +91-8076531395", 1200, 1410, text);
                        canvas.drawText("Email: ordinalo.services@gmail.com", 1200, 1480, text);


                        HashMap<String,String> latestAnalysis = new HashMap<>();
                        latestAnalysis.put("totalSales",revenueTotal);
                        latestAnalysis.put("totalOrders",orderList.size() + "");
                        latestAnalysis.put("totalCustomers",custList.size() + "");
                        editor.putString("lastAnalysisHashMap",gson.toJson(latestAnalysis));
                        editor.putString("lastDay",day + "");
                        editor.apply();

                        pdfDocument.finishPage(page);
                        String fileName = "/DailyReportInsights" + ".pdf";




                        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) + fileName);
                        File file1 = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) + fileName);
                        Log.i("checking",file1.exists() + "");
                        if(file1.exists()) {
                            file1.delete();
                            Log.i("checking",file1.exists() + "");
                        }
                        try {
                            pdfDocument.writeTo(new FileOutputStream(file));
                            runOnUiThread(() -> Toast.makeText(HomeScreen.this, "Report Generated", Toast.LENGTH_SHORT).show());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        String nameOfFile = day + "th " + month;

                        try {
                            StorageReference reference = storageReference.child(auth.getUid() + "/" + "InsightsReports" + "/"  + "Daily" + "/" + nameOfFile);
                            reference.putFile(Uri.fromFile(file)).addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    HashMap<String,HashMap<String,String>> map = new HashMap<>();
                                    HashMap<String,String> innerMapData = new HashMap<>();

                                    innerMapData.put(nameOfFile,"generated");
                                    map.put("Daily",innerMapData);
                                    gson = new Gson();
                                    editorToTrackFiles.putString("daily",gson.toJson(map));
                                    editorToTrackFiles.apply();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            });
                        }catch (Exception e){
                            Toast.makeText(HomeScreen.this, ""+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

                        }

                        pdfDocument.close();

                        runOnUiThread(() -> {
                            AlertDialog.Builder builder = new AlertDialog.Builder(HomeScreen.this);
                            builder.setTitle("Daily Insights").setMessage("Do you wanna open your last day insights file")
                                    .setPositiveButton("Open", (dialogInterface, i) -> {
                                        Intent intent = new Intent(Intent.ACTION_VIEW);
                                        File file12 = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) + "/DailyReportInsights.pdf");
                                        if(file12.exists()) {
                                            Toast.makeText(HomeScreen.this, "Opening....", Toast.LENGTH_SHORT).show();
                                            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                            intent.setDataAndType(FileProvider.getUriForFile(HomeScreen.this, getPackageName() + ".provider", file12), "application/pdf");
                                            startActivity(intent);
                                        }else
                                            Toast.makeText(HomeScreen.this, "No Reports Generated", Toast.LENGTH_SHORT).show();
                                    }).setNegativeButton("Later", (dialogInterface, i) -> {

                                    }).create();
                            builder.show();
                        });
                    }
                }else{
                    if(Calendar.getInstance().get(Calendar.MONTH) != Calendar.JANUARY) {
                        String newMonth = monthName[calendar.get(Calendar.MONTH) - 1];
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                            if(dailyInsights.contains("lastDay") && !dailyInsights.getString("lastDay","").equals(LocalDate.now().withDayOfMonth(1).minusDays(1) + "")){
                                gson = new Gson();
                                HashMap<String,HashMap<String,String>> mainMaps = gson.fromJson(dailyInsights.getString(newMonth,""),type);

                                if(mainMaps.containsKey(LocalDate.now().withDayOfMonth(1).minusDays(1) + "")){
                                    Type typeo = new TypeToken<List<String>>() {
                                    }.getType();
                                    HashMap<String,String> innerMap = new HashMap<>(Objects.requireNonNull(mainMap.get(day + "")));

                                    List<String> orderList = new ArrayList<>(gson.fromJson(innerMap.get("orderList"),typeo));
                                    List<String> custList = new ArrayList<>(gson.fromJson(innerMap.get("custList"),typeo));
                                    String revenueTotal = innerMap.get("revenueTotal");


                                    PdfDocument pdfDocument = new PdfDocument();
                                    Paint myPaint = new Paint();
                                    PdfDocument.PageInfo myPage = new PdfDocument.PageInfo.Builder(2080, 2040, 1).create();
                                    PdfDocument.Page page = pdfDocument.startPage(myPage);

                                    Paint text = new Paint();
                                    Canvas canvas = page.getCanvas();

                                    text.setTextAlign(Paint.Align.LEFT);
                                    text.setTextSize(90);
                                    text.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                                    canvas.drawText("Daily Restaurant Insights", 100, 155, text);
                                    text.setTextSize(75);
                                    canvas.drawText(day + " " + month, 100, 265, text);
                                    text.setTextSize(58);
                                    text.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
                                    canvas.drawText("Total Orders Made: " + orderList.size(), 100, 385, text);
                                    canvas.drawText("Total Transaction Amount: \u20b9" + revenueTotal, 100, 485, text);
//                        text.setTextSize(65);
//                        text.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
//                        canvas.drawText("Highest Sales", 100, 580, text);
//                        text.setTextSize(50);
//                        text.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
//                        canvas.drawText("Date: " + "16" + "th" + " October", 100, 650, text);
//                        canvas.drawText("Total Orders: " + "55", 100, 715, text);
//                        canvas.drawText("Total Amount: \u20b9" + "3625", 100, 785, text);

                                    text.setTextSize(65);
                                    text.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                                    canvas.drawText("Total Customers", 100, 580, text);

                                    text.setTextSize(50);
                                    text.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
                                    canvas.drawText("Customers: " + custList.size(), 100, 650, text);


                                    if (dailyInsights.contains("lastAnalysisHashMap")) {
                                        Type typeos = new TypeToken<HashMap<String, String>>() {
                                        }.getType();
                                        int totalOrders = orderList.size();
                                        int totalCust = custList.size();
                                        Double totalSalesThatPeriod = Double.parseDouble(revenueTotal);
                                        HashMap<String, String> prevMap = new HashMap<>(gson.fromJson(dailyInsights.getString("lastAnalysisHashMap", ""), typeos));
                                        Double prevSalesAmt = Double.parseDouble(prevMap.get("totalSales"));
                                        int ordersMadeTotal = Integer.parseInt(prevMap.get("totalOrders"));
                                        int totalCustomersTotal = Integer.parseInt(prevMap.get("totalCustomers"));
                                        text.setTextSize(90);
                                        text.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                                        canvas.drawText("Compare with previous day", 600, 760, text);
                                        text.setTextSize(58);
                                        text.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
                                        double data1 = ((totalSalesThatPeriod - prevSalesAmt) / prevSalesAmt) * 100;

                                        if (totalSalesThatPeriod > prevSalesAmt) {
                                            canvas.drawText("Total Sales: ↑ Increase By " + new DecimalFormat("0.00").format(data1) + "%", 100, 860, text);
                                        } else {
                                            canvas.drawText("Total Sales: ↓ Decrease By " + new DecimalFormat("0.00").format(data1) + "%", 100, 860, text);
                                        }

                                        double data2 =  ((Double.parseDouble(totalOrders + "") - Double.parseDouble(ordersMadeTotal + "")) / Double.parseDouble(ordersMadeTotal + "")) * 100;
                                        if (totalOrders > ordersMadeTotal) {
                                            canvas.drawText("Total Orders: ↑ Increase By" + new DecimalFormat("0.00").format(data2) + "%", 100, 950, text);
                                        } else {
                                            canvas.drawText("Total Orders: ↓ Decrease By " + new DecimalFormat("0.00").format(data2) + "%", 100, 950, text);
                                        }

                                        double data3 =  (( Double.parseDouble(totalCust + "") -  Double.parseDouble(totalCustomersTotal + "")) / Double.parseDouble(totalCustomersTotal + "")) * 100;
                                        if (totalCust > totalCustomersTotal) {
                                            canvas.drawText("Total Customers: ↑ Increase By" + new DecimalFormat("0.00").format(data3) + "%", 100, 1050, text);
                                        } else
                                            canvas.drawText("Total Customers: ↓ Decrease By " + new DecimalFormat("0.00").format(data3) + "%", 100, 1050, text);

                                    }
                                    canvas.drawText("For other info, Check Premium Activity", 100, 1160, text);
                                    text.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                                    text.setTextSize(52);
                                    canvas.drawText("Contact Ordinalo", 1200, 1260, text);
                                    canvas.drawText("Phone: +91-8076531395", 1200, 1340, text);
                                    canvas.drawText("Email: ordinalo.services@gmail.com", 1200, 1420, text);


                                    HashMap<String,String> latestAnalysis = new HashMap<>();
                                    latestAnalysis.put("totalSales",revenueTotal);
                                    latestAnalysis.put("totalOrders",orderList.size() + "");
                                    latestAnalysis.put("totalCustomers",custList.size() + "");
                                    editor.putString("lastAnalysisHashMap",gson.toJson(latestAnalysis));
                                    editor.putString("lastDay",day + "");
                                    editor.apply();

                                    pdfDocument.finishPage(page);
                                    String fileName = "/DailyReportInsights" + ".pdf";
                                    File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) + fileName);

                                    try {
                                        pdfDocument.writeTo(new FileOutputStream(file));
                                        runOnUiThread(() -> Toast.makeText(HomeScreen.this, "Generated", Toast.LENGTH_SHORT).show());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                    String nameOfFile = day + "th " + month;

                                    try {
                                        StorageReference reference = storageReference.child(auth.getUid() + "/" + "InsightsReports" + "/"  + "Daily" + "/" + nameOfFile);
                                        reference.putFile(Uri.fromFile(file)).addOnCompleteListener(task -> {
                                            if (task.isSuccessful()) {
                                                HashMap<String,HashMap<String,String>> map = new HashMap<>();
                                                HashMap<String,String> innerMapData = new HashMap<>();

                                                innerMapData.put(nameOfFile,"generated");
                                                map.put("Daily",innerMapData);
                                                gson = new Gson();
                                                editorToTrackFiles.putString("daily",gson.toJson(map));
                                                editorToTrackFiles.apply();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {

                                            }
                                        });
                                    }catch (Exception e){
                                        Toast.makeText(HomeScreen.this, ""+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

                                    }

                                    pdfDocument.close();
                                }

                            }

                        }

                    }

                }
            }
        }).start();
        SharedPreferences user7daysTracker = getSharedPreferences("DailyUserTrackingFor7days",MODE_PRIVATE);
        new Thread(() -> {
            String month = monthName[calendar.get(Calendar.MONTH)];
            if(last7daysReport.contains("currentMonth")){
                if( last7daysReport.getString("currentMonth","").equals(month)) {
                    if (last7daysReport.contains("daysTracked")) {
                        int day = calendar.get(Calendar.DAY_OF_MONTH);

                        if (Integer.parseInt(last7daysReport.getString("currentDate","")) != day && Integer.parseInt(last7daysReport.getString("daysTracked", "")) == 7) {
                            String json = storeOrders.getString(month, "");
                            gson = new Gson();
                            Log.i("info1414","kaaaaaaaaaaaaaaaaaaaaaaaaaaala");
                            Type type = new TypeToken<List<List<String>>>() {
                            }.getType();
                            List<List<String>> mainDataList = gson.fromJson(json, type);
                            Type types = new TypeToken<HashMap<String, HashMap<String, Integer>>>() {
                            }.getType();
                            HashMap<String, HashMap<String, Integer>> mainMap = gson.fromJson(user7daysTracker.getString(month, ""), types);
                            if (!mainDataList.isEmpty()) {
                                List<String> date = new ArrayList<>(mainDataList.get(0));
                                List<String> totalORders = new ArrayList<>(mainDataList.get(2));
                                List<String> orderAmountList = new ArrayList<>(mainDataList.get(1));

                                if (date.size() != 0) {
                                    String startDate, endDate;
                                    double highestSalesDay = 0D;
                                    String dateName = "";
                                    int totalCust = 0;
                                    int highestOrderAtDay = 0;
                                    int loopTill = 0;
                                    int totalOrders = 0;
                                    if (date.size() > 7)
                                        loopTill = date.size() - 7;
                                    int daysLeftToShow = 6;
                                    double totalSalesThatPeriod = 0;

                                        endDate = date.get(date.size() - 1);
                                        startDate = date.get(loopTill);
                                        for (int i = date.size() - 1; i >= loopTill; i--) {
                                            totalSalesThatPeriod += Double.parseDouble(orderAmountList.get(i));
                                            totalOrders += Integer.parseInt(totalORders.get(i));
                                            startDate = date.get(i);
                                            if (Double.parseDouble(orderAmountList.get(i)) > highestSalesDay) {
                                                dateName = date.get(i) + "th " + month;
                                                highestSalesDay = Double.parseDouble(orderAmountList.get(i));
                                                highestOrderAtDay = Integer.parseInt(totalORders.get(i));
                                            }

                                            if (mainMap.containsKey(date.get(i))) {
                                                HashMap<String, Integer> innerMap = new HashMap<>(mainMap.get(date.get(i) + ""));
                                                for (String ii : innerMap.keySet()) {
                                                    totalCust++;
                                                }
                                            }
                                        }


                                        PdfDocument pdfDocument = new PdfDocument();
                                        Paint myPaint = new Paint();
                                        PdfDocument.PageInfo myPage = new PdfDocument.PageInfo.Builder(2080, 2040, 1).create();
                                        PdfDocument.Page page = pdfDocument.startPage(myPage);

                                        Paint text = new Paint();
                                        Canvas canvas = page.getCanvas();

                                        text.setTextAlign(Paint.Align.LEFT);
                                        text.setTextSize(90);
                                        text.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                                        canvas.drawText("Last 7 days Report", 100, 155, text);
                                        text.setTextSize(75);
                                        canvas.drawText("From " + startDate + "th to " + endDate + "th " + month, 100, 265, text);
                                        text.setTextSize(58);
                                        text.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
                                        canvas.drawText("Total Orders Made: " + totalOrders, 100, 395, text);
                                        canvas.drawText("Total Transaction Amount: \u20b9" + totalSalesThatPeriod, 100, 470, text);
                                        text.setTextSize(65);
                                        text.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                                        canvas.drawText("Highest Sales", 100, 545, text);
                                        text.setTextSize(50);
                                        text.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
                                        canvas.drawText("Date: " + dateName, 100, 615, text);
                                        canvas.drawText("Total Orders: " + highestOrderAtDay, 100, 690, text);
                                        canvas.drawText("Total Amount: \u20b9" + highestSalesDay, 100, 765, text);

                                        text.setTextSize(65);
                                        text.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                                        canvas.drawText("Total Customers", 100, 850, text);

                                        text.setTextSize(50);
                                        text.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
                                        canvas.drawText("Customers: " + totalCust, 100, 925, text);


                                        if (last7daysReport.contains("lastAnalysisHashMap")) {
                                            Type typeo = new TypeToken<HashMap<String, String>>() {
                                            }.getType();
                                            HashMap<String, String> prevMap = new HashMap<>(gson.fromJson(last7daysReport.getString("lastAnalysisHashMap", ""), typeo));
                                            Double prevSalesAmt = Double.parseDouble(prevMap.get("totalSales"));
                                            double ordersMadeTotal = Double.parseDouble(prevMap.get("totalOrders"));
                                            double totalCustomersTotal = Double.parseDouble(prevMap.get("totalCustomers"));
                                            text.setTextSize(80);
                                            text.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                                            canvas.drawText("Compare with last 7 days", 650, 1070, text);
                                            text.setTextSize(58);
                                            text.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));

                                            double data1 = ((totalSalesThatPeriod - prevSalesAmt) / prevSalesAmt) * 100;
                                            if (totalSalesThatPeriod > prevSalesAmt) {
                                                canvas.drawText("Total Sales: ↑ Increase By " + new DecimalFormat("0.00").format(data1) + "%", 100, 1180, text);
                                                canvas.drawText("Previous Sales \u20b9" + prevSalesAmt, 100, 1250, text);
                                            } else {
                                                canvas.drawText("Total Sales: ↓ Decrease By " + new DecimalFormat("0.00").format(data1) + "%", 100, 1180, text);
                                                canvas.drawText("Previous Sales \u20b9" + prevSalesAmt, 100, 1250, text);
                                            }

                                            double data2 = ((totalOrders - ordersMadeTotal) / ordersMadeTotal) * 100;
                                            if (totalOrders > ordersMadeTotal) {
                                                canvas.drawText("Total Orders: ↑ Increase By " + new DecimalFormat("0.00").format(data2) + "%", 100, 1325, text);
                                                canvas.drawText("Previous Orders " + ordersMadeTotal, 100, 1395, text);
                                            } else {
                                                canvas.drawText("Total Orders: ↓ Decrease By " + new DecimalFormat("0.00").format(data2) + "%", 100, 1325, text);
                                                canvas.drawText("Previous Orders " + ordersMadeTotal, 100, 1395, text);
                                            }

                                            double data3 =  ((totalCust - totalCustomersTotal) / totalCustomersTotal) * 100;
                                            if (totalCust > totalCustomersTotal) {
                                                canvas.drawText("Total Customers: ↑ Increase By " + new DecimalFormat("0.00").format(data3) + "%", 100, 1465, text);
                                                canvas.drawText("Previous Customers " + totalCustomersTotal, 100, 1535, text);
                                            } else {
                                                canvas.drawText("Total Customers: ↓ Decrease By " + new DecimalFormat("0.00").format(data3) + "%", 100, 1465, text);
                                                canvas.drawText("Previous Customers " + totalCustomersTotal, 100, 1535, text);
                                            }
                                        }

                                        canvas.drawText("For other info, Check Premium Activity", 100, 1605, text);
                                        text.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                                        text.setTextSize(52);
                                        canvas.drawText("Contact Ordinalo", 1200, 1765, text);
                                        canvas.drawText("Phone: +91-8076531395", 1200, 1835, text);
                                        canvas.drawText("Email: ordinalo.services@gmail.com", 1200, 1905, text);

                                        pdfDocument.finishPage(page);
                                        String fileName = "/WeeklyReportTracker" + ".pdf";
                                        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) + fileName);

                                        try {
                                            pdfDocument.writeTo(new FileOutputStream(file));
                                            runOnUiThread(() -> Toast.makeText(HomeScreen.this, "Generated", Toast.LENGTH_SHORT).show());

                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }

                                    String nameOfFile = startDate + "-" + endDate + " " + month;

                                    try {
                                        StorageReference reference = storageReference.child(auth.getUid() + "/" + "InsightsReports" + "/"  + "Weekly" + "/" + nameOfFile);
                                        reference.putFile(Uri.fromFile(file)).addOnCompleteListener(task -> {
                                            if (task.isSuccessful()) {
                                                HashMap<String,HashMap<String,String>> map = new HashMap<>();
                                                HashMap<String,String> innerMapData = new HashMap<>();

                                                innerMapData.put(nameOfFile,"generated");
                                                map.put("Weekly",innerMapData);
                                                gson = new Gson();
                                                editorToTrackFiles.putString("week",gson.toJson(map));
                                                editorToTrackFiles.apply();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {

                                            }
                                        });
                                    }catch (Exception e){
                                        Toast.makeText(HomeScreen.this, ""+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

                                    }

                                        pdfDocument.close();

                                        HashMap<String, String> prevAnalysisInfo = new HashMap<>();
                                        prevAnalysisInfo.put("totalSales", totalSalesThatPeriod + "");
                                        prevAnalysisInfo.put("totalOrders", totalOrders + "");
                                        prevAnalysisInfo.put("totalCustomers", totalCust + "");
                                        last7daysReportEdit.putString("lastAnalysisHashMap", gson.toJson(prevAnalysisInfo));
                                        last7daysReportEdit.remove("daysTracked");
                                        last7daysReportEdit.putString("lastDateReport",endDate);
                                        last7daysReportEdit.apply();

                                        runOnUiThread(() -> {
                                            AlertDialog.Builder builder = new AlertDialog.Builder(HomeScreen.this);
                                            builder.setTitle("Weekly Report Generated")
                                                    .setMessage("Your Weekly report is generated of 7 days.\nDo you wanna open it?")
                                                    .setPositiveButton("Open", (dialogInterface, i) -> {
                                                        dialogInterface.dismiss();
                                                        Toast.makeText(HomeScreen.this, "Opening....", Toast.LENGTH_SHORT).show();
                                                        Intent intent = new Intent(Intent.ACTION_VIEW);
                                                        File file13 = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) + "/WeeklyReportTracker.pdf");
                                                        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                                        intent.setDataAndType(FileProvider.getUriForFile(HomeScreen.this, getPackageName() + ".provider", file13), "application/pdf");
                                                        startActivity(intent);
                                                    }).setNegativeButton("Later", (dialogInterface, i) -> dialogInterface.dismiss()).create();
                                            builder.show();
                                        });
                                    }

                            }
                        }
                    }
                }else{

                    if(calendar.get(Calendar.MONTH) != Calendar.JANUARY) {
                        gson = new Gson();
                        String prevMonth = monthName[calendar.get(Calendar.MONTH) - 1];
                        String json = storeOrders.getString(prevMonth, "");
                        gson = new Gson();
                        Type type = new TypeToken<List<List<String>>>() {
                        }.getType();
                        List<List<String>> mainDataList = gson.fromJson(json, type);
                        Type types = new TypeToken<HashMap<String, HashMap<String, Integer>>>() {
                        }.getType();
                        HashMap<String, HashMap<String, Integer>> mainMap = gson.fromJson(user7daysTracker.getString(prevMonth, ""), types);
                        if (!mainDataList.isEmpty()) {
                            List<String> date = new ArrayList<>(mainDataList.get(0));
                            List<String> totalORders = new ArrayList<>(mainDataList.get(2));
                            List<String> orderAmountList = new ArrayList<>(mainDataList.get(1));


                            if(date.size() != 0) {

                                int dateOfList = Integer.parseInt(date.get(date.size() - 1));
                                if (last7daysReport.contains("lastDateReport")) {
                                    int lastDate = Integer.parseInt(last7daysReport.getString("lastDateReport", ""));

                                    if (dateOfList > lastDate) {
                                        String startDate, endDate;
                                        double highestSalesDay = 0D;
                                        String dateName = "";
                                        int totalCust = 0;
                                        int highestOrderAtDay = 0;
                                        int loopTill = 0;
                                        int totalOrders = 0;
                                        if (date.size() > 7)
                                            loopTill = date.size() - 7;
                                        int daysLeftToShow = 6;
                                        double totalSalesThatPeriod = 0;

                                        startDate = lastDate + "";
                                        endDate = date.get(date.size() - 1);
                                        int indexTillLoop = date.indexOf(startDate);
                                        for(int i=date.size()-1;i>indexTillLoop;i--){
                                            totalSalesThatPeriod += Double.parseDouble(orderAmountList.get(i));
                                            totalOrders += Integer.parseInt(totalORders.get(i));
                                            startDate = date.get(i);
                                            if (Double.parseDouble(orderAmountList.get(i)) > highestSalesDay) {
                                                dateName = date.get(i) + "th " + month;
                                                highestSalesDay = Double.parseDouble(orderAmountList.get(i));
                                                highestOrderAtDay = Integer.parseInt(totalORders.get(i));
                                            }

                                            if (mainMap.containsKey(date.get(i))) {
                                                HashMap<String, Integer> innerMap = new HashMap<>(mainMap.get(date.get(i) + ""));
                                                for (String ii : innerMap.keySet()) {
                                                    totalCust++;
                                                }
                                            }
                                        }

                                        PdfDocument pdfDocument = new PdfDocument();
                                        Paint myPaint = new Paint();
                                        PdfDocument.PageInfo myPage = new PdfDocument.PageInfo.Builder(2080, 2040, 1).create();
                                        PdfDocument.Page page = pdfDocument.startPage(myPage);

                                        Paint text = new Paint();
                                        Canvas canvas = page.getCanvas();

                                        text.setTextAlign(Paint.Align.LEFT);
                                        text.setTextSize(80);
                                        text.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                                        canvas.drawText("Last Remaining days Report", 100, 155, text);
                                        text.setTextSize(70);
                                        canvas.drawText("From " + startDate + " to " + endDate + " " + prevMonth, 100, 265, text);
                                        text.setTextSize(58);
                                        text.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
                                        canvas.drawText("Total Orders Made: " + totalOrders, 100, 395, text);
                                        canvas.drawText("Total Transaction Amount: \u20b9" + totalSalesThatPeriod, 100, 470, text);
                                        text.setTextSize(65);
                                        text.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                                        canvas.drawText("Highest Sales", 100, 545, text);
                                        text.setTextSize(50);
                                        text.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
                                        canvas.drawText("Date: " + dateName, 100, 615, text);
                                        canvas.drawText("Total Orders: " + highestOrderAtDay, 100, 690, text);
                                        canvas.drawText("Total Amount: \u20b9" + highestSalesDay, 100, 765, text);

                                        text.setTextSize(65);
                                        text.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                                        canvas.drawText("Total Customers", 100, 850, text);

                                        text.setTextSize(50);
                                        text.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
                                        canvas.drawText("Customers: " + totalCust, 100, 925, text);


//                                        if (last7daysReport.contains("lastAnalysisHashMap")) {
//                                            Type typeo = new TypeToken<HashMap<String, String>>() {
//                                            }.getType();
//                                            HashMap<String, String> prevMap = new HashMap<>(gson.fromJson(last7daysReport.getString("lastAnalysisHashMap", ""), typeo));
//                                            Double prevSalesAmt = Double.parseDouble(prevMap.get("totalSales"));
//                                            double ordersMadeTotal = Double.parseDouble(prevMap.get("totalOrders"));
//                                            double totalCustomersTotal = Double.parseDouble(prevMap.get("totalCustomers"));
//                                            text.setTextSize(80);
//                                            text.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
//                                            canvas.drawText("Compare with last 7 days", 650, 1070, text);
//                                            text.setTextSize(58);
//                                            text.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
//
//                                            double data1 = ((totalSalesThatPeriod - prevSalesAmt) / prevSalesAmt) * 100;
//                                            if (totalSalesThatPeriod > prevSalesAmt) {
//                                                canvas.drawText("Total Sales: ↑ Increase By " + new DecimalFormat("0.00").format(data1) + "%", 100, 1180, text);
//                                            } else {
//                                                canvas.drawText("Total Sales: ↓ Decrease By " + new DecimalFormat("0.00").format(data1) + "%", 100, 1180, text);
//                                            }
//
//                                            double data2 =  ((totalOrders - ordersMadeTotal) / ordersMadeTotal) * 100;
//                                            if (totalOrders > ordersMadeTotal) {
//                                                canvas.drawText("Total Orders: ↑ Increase By " + new DecimalFormat("0.00").format(data2) + "%", 100, 1255, text);
//                                            } else {
//                                                canvas.drawText("Total Orders: ↓ Decrease By " + new DecimalFormat("0.00").format(data2) + "%", 100, 1255, text);
//                                            }
//
//                                            double data3 =  ((totalCust - totalCustomersTotal) / totalCustomersTotal) * 100;
//                                            if (totalCust > totalCustomersTotal) {
//                                                canvas.drawText("Total Customers: ↑ Increase By " + new DecimalFormat("0.00").format(data3) + "%", 100, 1330, text);
//                                            } else
//                                                canvas.drawText("Total Customers: ↓ Decrease By " + new DecimalFormat("0.00").format(data3) + "%", 100, 1330, text);
//
//                                        }

                                        canvas.drawText("For other info, Check Premium Activity", 100, 1470, text);
                                        text.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                                        text.setTextSize(52);
                                        canvas.drawText("Contact Ordinalo", 1200, 1745, text);
                                        canvas.drawText("Phone: +91-8076531395", 1200, 1815, text);
                                        canvas.drawText("Email: ordinalo.services@gmail.com", 1200, 1885, text);

                                        pdfDocument.finishPage(page);
                                        String fileName = "/WeeklyReportTracker" + ".pdf";
                                        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) + fileName);

                                        try {
                                            pdfDocument.writeTo(new FileOutputStream(file));
                                            runOnUiThread(() -> Toast.makeText(HomeScreen.this, "Report Generated", Toast.LENGTH_SHORT).show());

                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }

                                        String nameOfFile = startDate + "-" + endDate + " " + prevMonth;

                                        try {
                                            StorageReference reference = storageReference.child(auth.getUid() + "/" + "InsightsReports" + "/"  + "Weekly" + "/" + nameOfFile);
                                            reference.putFile(Uri.fromFile(file)).addOnCompleteListener(task -> {
                                                if (task.isSuccessful()) {
                                                    HashMap<String,HashMap<String,String>> map = new HashMap<>();
                                                    HashMap<String,String> innerMapData = new HashMap<>();

                                                    innerMapData.put(nameOfFile,"generated");
                                                    map.put("Weekly",innerMapData);
                                                    gson = new Gson();
                                                    editorToTrackFiles.putString("week",gson.toJson(map));
                                                    editorToTrackFiles.apply();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {

                                                }
                                            });
                                        }catch (Exception e){
                                            Toast.makeText(HomeScreen.this, ""+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

                                        }

                                        pdfDocument.close();

                                        HashMap<String, String> prevAnalysisInfo = new HashMap<>();
                                        prevAnalysisInfo.put("totalSales", totalSalesThatPeriod + "");
                                        prevAnalysisInfo.put("totalOrders", totalOrders + "");
                                        prevAnalysisInfo.put("totalCustomers", totalCust + "");
                                        last7daysReportEdit.putString("lastAnalysisHashMap", gson.toJson(prevAnalysisInfo));
                                        last7daysReportEdit.putString("lastDateReport", endDate);
                                        last7daysReportEdit.remove("daysTracked");
                                        last7daysReportEdit.apply();

                                        runOnUiThread(() -> {
                                            AlertDialog.Builder builder = new AlertDialog.Builder(HomeScreen.this);
                                            builder.setTitle("Week End Report Generated")
                                                    .setMessage("Your Week end report is generated of remaining days.\nDo you wanna open it?")
                                                    .setPositiveButton("Open", (dialogInterface, i) -> {
                                                        dialogInterface.dismiss();
                                                        Toast.makeText(HomeScreen.this, "Opening....", Toast.LENGTH_SHORT).show();
                                                        Intent intent = new Intent(Intent.ACTION_VIEW);
                                                        File file1 = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) + "/WeeklyReportTracker.pdf");
                                                        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                                        intent.setDataAndType(FileProvider.getUriForFile(HomeScreen.this, getPackageName() + ".provider", file1), "application/pdf");
                                                        startActivity(intent);
                                                    }).setNegativeButton("Later", (dialogInterface, i) -> dialogInterface.dismiss()).create();
                                            builder.show();
                                        });
                                    }
                                }
                            }
                            }
                    }

                }
            }

            SharedPreferences lastMonthReport = getSharedPreferences("lastMonthlyReport",MODE_PRIVATE);
            SharedPreferences.Editor editorMonthly = lastMonthReport.edit();
//            editorMonthly.putString("currentMonth","November");
//            editorMonthly.apply();
            if(lastMonthReport.contains("currentMonth")){
                if(!lastMonthReport.getString("currentMonth","").equals(month)){
                    if(calendar.get(Calendar.MONTH) != Calendar.JANUARY) {
                        String monthNameOfReport = monthName[calendar.get(Calendar.MONTH) - 1];

                        if(lastMonthReport.contains("prevMonthNameReport") && lastMonthReport.getString("prevMonthNameReport","").equals(monthNameOfReport)){

                        }else{
                            gson = new Gson();
                            String prevMonth = monthName[calendar.get(Calendar.MONTH) - 1];
                            String json = storeOrders.getString(prevMonth, "");
                            Type type = new TypeToken<List<List<String>>>() {
                            }.getType();
                            List<List<String>> mainDataList = gson.fromJson(json, type);
                            Type types = new TypeToken<HashMap<String, HashMap<String, Integer>>>() {
                            }.getType();
                            HashMap<String, HashMap<String, Integer>> mainMap = gson.fromJson(user7daysTracker.getString(prevMonth, ""), types);

                            if(!mainDataList.isEmpty()){

                                List<String> date = new ArrayList<>(mainDataList.get(0));
                                List<String> totalORders = new ArrayList<>(mainDataList.get(2));
                                List<String> orderAmountList = new ArrayList<>(mainDataList.get(1));


                                if(date.size() != 0) {
                                    String startDate, endDate;
                                    double highestSalesDay = 0D;
                                    String dateName = "";
                                    int totalCust = 0;
                                    int highestOrderAtDay = 0;
                                    int loopTill = 0;
                                    int totalOrders = 0;
                                    int daysLeftToShow = 6;
                                    double totalSalesThatPeriod = 0;


                                    startDate = date.get(0);
                                    endDate = date.get(date.size() - 1);


                                    for(int i=0; i <  date.size() ; i++){

                                        totalSalesThatPeriod += Double.parseDouble(orderAmountList.get(i));
                                        totalOrders += Integer.parseInt(totalORders.get(i));

                                        if (Double.parseDouble(orderAmountList.get(i)) > highestSalesDay) {
                                            dateName = date.get(i) + "th " + month;
                                            highestSalesDay = Double.parseDouble(orderAmountList.get(i));
                                            highestOrderAtDay = Integer.parseInt(totalORders.get(i));
                                        }

                                        if (mainMap.containsKey(date.get(i))) {
                                            HashMap<String, Integer> innerMap = new HashMap<>(mainMap.get(date.get(i) + ""));
                                            for (String ii : innerMap.keySet()) {
                                                totalCust++;
                                            }
                                        }

                                    }


                                    PdfDocument pdfDocument = new PdfDocument();
                                    Paint myPaint = new Paint();
                                    PdfDocument.PageInfo myPage = new PdfDocument.PageInfo.Builder(2080, 2040, 1).create();
                                    PdfDocument.Page page = pdfDocument.startPage(myPage);

                                    Paint text = new Paint();
                                    Canvas canvas = page.getCanvas();

                                    text.setTextAlign(Paint.Align.LEFT);
                                    text.setTextSize(90);
                                    text.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                                    canvas.drawText("Monthly Report", 100, 155, text);
                                    text.setTextSize(75);
                                    canvas.drawText("From " + startDate + " to " + endDate + " " + prevMonth, 100, 265, text);
                                    text.setTextSize(58);
                                    text.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
                                    canvas.drawText("Total Orders Made: " + totalOrders, 100, 395, text);
                                    canvas.drawText("Total Transaction Amount: \u20b9" + totalSalesThatPeriod, 100, 470, text);
                                    text.setTextSize(65);
                                    text.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                                    canvas.drawText("Highest Sales", 100, 545, text);
                                    text.setTextSize(50);
                                    text.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
                                    canvas.drawText("Date: " + dateName, 100, 615, text);
                                    canvas.drawText("Total Orders: " + highestOrderAtDay, 100, 690, text);
                                    canvas.drawText("Total Amount: \u20b9" + highestSalesDay, 100, 765, text);

                                    text.setTextSize(65);
                                    text.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                                    canvas.drawText("Total Customers", 100, 850, text);

                                    text.setTextSize(50);
                                    text.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
                                    canvas.drawText("Customers: " + totalCust, 100, 925, text);


                                    if(lastMonthReport.contains("lastAnalysisHashMap")){
                                        Type typeo = new TypeToken<HashMap<String, String>>() {
                                        }.getType();
                                        HashMap<String, String> prevMap = new HashMap<>(gson.fromJson(lastMonthReport.getString("lastAnalysisHashMap", ""), typeo));
                                        Double prevSalesAmt = Double.parseDouble(prevMap.get("totalSales"));
                                        double ordersMadeTotal = Double.parseDouble(prevMap.get("totalOrders"));
                                        double totalCustomersTotal = Double.parseDouble(prevMap.get("totalCustomers"));
                                        text.setTextSize(80);
                                        text.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                                        canvas.drawText("Compare with last Month", 650, 1070, text);
                                        text.setTextSize(58);
                                        text.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));

                                        double data1 = ((totalSalesThatPeriod - prevSalesAmt) / prevSalesAmt) * 100;
                                        if (totalSalesThatPeriod > prevSalesAmt) {
                                            canvas.drawText("Total Sales: ↑ Increase By " + new DecimalFormat("0.00").format(data1) + "%", 100, 1180, text);
                                            canvas.drawText("Previous Sales \u20b9" + prevSalesAmt, 100, 1250, text);
                                        } else {
                                            canvas.drawText("Total Sales: ↓ Decrease By " + new DecimalFormat("0.00").format(data1) + "%", 100, 1180, text);
                                            canvas.drawText("Previous Sales \u20b9" + prevSalesAmt, 100, 1250, text);
                                        }

                                        double data2 =  ((totalOrders - ordersMadeTotal) / ordersMadeTotal) * 100;
                                        if (totalOrders > ordersMadeTotal) {
                                            canvas.drawText("Total Orders: ↑ Increase By " + new DecimalFormat("0.00").format(data2) + "%", 100, 1320, text);
                                            canvas.drawText("Previous Orders: " + ordersMadeTotal, 100, 1390, text);
                                        } else {
                                            canvas.drawText("Total Orders: ↓ Decrease By " + new DecimalFormat("0.00").format(data2) + "%", 100, 1320, text);
                                            canvas.drawText("Previous Orders: " + ordersMadeTotal, 100, 1390, text);
                                        }

                                        double data3 =  ((totalCust - totalCustomersTotal) / totalCustomersTotal) * 100;
                                        if (totalCust > totalCustomersTotal) {
                                            canvas.drawText("Total Customers: ↑ Increase By " + new DecimalFormat("0.00").format(data3) + "%", 100, 1460, text);
                                            canvas.drawText("Previous Customers: " + totalCustomersTotal, 100, 1530, text);
                                        } else {
                                            canvas.drawText("Total Customers: ↓ Decrease By " + new DecimalFormat("0.00").format(data3) + "%", 100, 1460, text);
                                            canvas.drawText("Previous Customers: " + totalCustomersTotal, 100, 1530, text);
                                        }

                                    }

                                    canvas.drawText("For other info, Check Premium Activity", 100, 1600, text);
                                    text.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                                    text.setTextSize(52);
                                    canvas.drawText("Contact Ordinalo", 1200, 1765, text);
                                    canvas.drawText("Phone: +91-8076531395", 1200, 1835, text);
                                    canvas.drawText("Email: ordinalo.services@gmail.com", 1200, 1905, text);

                                    pdfDocument.finishPage(page);
                                    String fileName = "/MonthlyReportTracker" + ".pdf";
                                    File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) + fileName);

                                    try {
                                        pdfDocument.writeTo(new FileOutputStream(file));
                                        runOnUiThread(() -> Toast.makeText(HomeScreen.this, "Report Generated", Toast.LENGTH_SHORT).show());

                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                    String nameOfFile = startDate + "-" + endDate + " " + prevMonth;

                                    try {
                                        StorageReference reference = storageReference.child(auth.getUid() + "/" + "InsightsReports" + "/"  + "Monthly" + "/" + nameOfFile);
                                        reference.putFile(Uri.fromFile(file)).addOnCompleteListener(task -> {
                                            if (task.isSuccessful()) {
                                                HashMap<String,HashMap<String,String>> map = new HashMap<>();
                                                HashMap<String,String> innerMapData = new HashMap<>();

                                                innerMapData.put(nameOfFile,"generated");
                                                map.put("Monthly",innerMapData);
                                                gson = new Gson();
                                                editorToTrackFiles.putString("month",gson.toJson(map));
                                                editorToTrackFiles.apply();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {

                                            }
                                        });
                                    }catch (Exception e){
                                        Toast.makeText(HomeScreen.this, ""+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

                                    }

                                    pdfDocument.close();

                                    HashMap<String, String> prevAnalysisInfo = new HashMap<>();
                                    prevAnalysisInfo.put("totalSales", totalSalesThatPeriod + "");
                                    prevAnalysisInfo.put("totalOrders", totalOrders + "");
                                    prevAnalysisInfo.put("totalCustomers", totalCust + "");
                                    editorMonthly.putString("lastAnalysisHashMap", gson.toJson(prevAnalysisInfo));
                                    editorMonthly.putString("prevMonthNameReport",prevMonth);
                                    editorMonthly.apply();


                                    runOnUiThread(() -> {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(HomeScreen.this);
                                        builder.setTitle("Monthly Report Generated")
                                                .setMessage("Your Monthly report is generated of " + prevMonth + " Month.\nDo you wanna open it?")
                                                .setPositiveButton("Open", (dialogInterface, i) -> {
                                                    dialogInterface.dismiss();
                                                    Toast.makeText(HomeScreen.this, "Opening....", Toast.LENGTH_SHORT).show();
                                                    Intent intent = new Intent(Intent.ACTION_VIEW);
                                                    File file12 = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) + "/MonthlyReportTracker.pdf");
                                                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                                    intent.setDataAndType(FileProvider.getUriForFile(HomeScreen.this, getPackageName() + ".provider", file12), "application/pdf");
                                                    startActivity(intent);
                                                }).setNegativeButton("Later", (dialogInterface, i) -> dialogInterface.dismiss()).create();
                                        builder.show();
                                    });
                                }
                            }
                        }
                    }
                }
            }
        }).start();


      new BackgroundWork().execute();
//        checkIfBankDetailsSubmitted();
//        if(sharedPreferences.contains("myListStored")){
//            Type type = new TypeToken<List<List<String>>>() {
//            }.getType();
//            gson = new Gson();
//            json = sharedPreferences.getString("myListStored","");
//            List<List<String>> arrPackageData = gson.fromJson(json, type);
//            Toast.makeText(this, "" + arrPackageData.toString(), Toast.LENGTH_SHORT).show();
//        }else{
//            List<String> lits = new ArrayList<>();
//            lits.add("a");
//            lits.add("b");
//            lits.add("c");
//            List<String> litss = new ArrayList<>();
//            litss.add("a");
//            litss.add("b");
//            litss.add("c");
//            List<List<String>> myList = new ArrayList<>();
//            myList.add(lits);
//            myList.add(litss);
//             gson = new Gson();
//             json = gson.toJson(myList);
//            myEditor.putString("myListStored",json);
//            myEditor.apply();
//        }

//        if(sharedPreferences.contains("sastaHasp")){
//            gson = new Gson();
//            String storedHash = sharedPreferences.getString("sastaHasp","");
//            Type type = new TypeToken<HashMap<String,String>>(){
//
//            }.getType();
//            HashMap<String, String> testHashMap2 = gson.fromJson(storedHash, type);
//            Toast.makeText(this, "" + testHashMap2.toString(), Toast.LENGTH_SHORT).show();
//            Log.i("map",testHashMap2.toString());
//        }else{
//            HashMap<String,String> map = new HashMap<>();
//            map.put("one","1");
//            map.put("two","2");
//            map.put("three","3");
//            map.put("four","4");
//            gson = new Gson();
//            myEditor.putString("sastaHasp",gson.toJson(map));
//            myEditor.apply();
//        }
        bubble.setNavigationChangeListener((view, position) -> {
            switch (position){
                case 0:
                    manager.beginTransaction().replace(R.id.homescreen,new HomeFrag()).commit();
                    break;
                case 1:
                    manager.beginTransaction().replace(R.id.homescreen,new MenuFrag()).commit();
                    break;
                case 2:
                    manager.beginTransaction().replace(R.id.homescreen,new TablesFrag()).commit();
                    break;
                case 3:
                    manager.beginTransaction().replace(R.id.homescreen,new AccountSettingsFragment()).commit();
                    break;
            }
        });
//        new checkBank().execute();
        StrictMode.VmPolicy.Builder builderr = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builderr.build());
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
//        Workbook workbook;
//        workbook.getWorksheets().get(0).getCells().get("A1").putValue("Date");
//        try {
//            workbook.save(path + "/MyFile.xlsx",SaveFormat.XLSX);
//            Log.i("info",path.getAbsolutePath() + "");
//            Log.i("info",getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath()+ "");
//            Toast.makeText(this, "File Saved", Toast.LENGTH_SHORT).show();
//        } catch (Exception e) {
//            Toast.makeText(this, "File Not Saved", Toast.LENGTH_SHORT).show();
//            e.printStackTrace();
//        }


        if(adminPrem.contains("status") && adminPrem.getString("status","").equals("active")) {
            if (!sharedPreferences.contains("FileGeneratedExcel")) {
                try {
                    String month = monthName[calendar.get(Calendar.MONTH)];

                    File file = new File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "RestaurantEarningTracker.xlsx");
                    XSSFWorkbook workbook1 = new XSSFWorkbook();
//                Sheet sheet = workbook1.getSheetAt(0);
//                int row = sheet.getLastRowNum();
//                Toast.makeText(this, "" + row, Toast.LENGTH_SHORT).show();
                    Sheet sheet = workbook1.createSheet("" + month + "_Earnings_" + year);
                    Row row = sheet.createRow(0);
                    Cell cell = row.createCell(0);
                    cell.setCellValue("Date");
                    cell = row.createCell(1);

                    cell.setCellValue("Transaction ID");
                    cell = row.createCell(2);
                    cell.setCellValue("Payment Mode");
                    cell = row.createCell(3);
                    cell.setCellValue("Amount");
                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    workbook1.write(fileOutputStream);
                    fileOutputStream.flush();
                    fileOutputStream.close();
                    myEditor.putString("FileGeneratedExcel", "f");
                    editor.putString("currentMonth", month + "_" + year);
                    editor.apply();
                    myEditor.apply();
//                Toast.makeText(this, "Finished", Toast.LENGTH_SHORT).show();

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Noob", Toast.LENGTH_SHORT).show();
                }
            } else {

                if(year > Integer.parseInt(sharedPreferences.getString("currentYear",""))){

                    myEditor.putString("currentYear",year + "");
                    myEditor.apply();
                    int whichY = year - 1;
                    SharedPreferences trackInsight = getSharedPreferences("TrackOfAllInsights",MODE_PRIVATE);
                    SharedPreferences.Editor ediTrack = trackInsight.edit();
                    ediTrack.clear().apply();
                    SharedPreferences previousYearStore = getSharedPreferences("PreviousYearAnalysisDish" + whichY,MODE_PRIVATE);
                    SharedPreferences.Editor prevYearEdit = previousYearStore.edit();
                    SharedPreferences storeEditor = getSharedPreferences("DishAnalysis",MODE_PRIVATE);

                    Map<String,?> storeMap = storeEditor.getAll();

                    for(Map.Entry<String,?> entry : storeMap.entrySet()){
                        Log.d("map values",entry.getKey() + ": " + entry.getValue().toString());
                        String value = (String) entry.getValue();
                        String key = entry.getKey();

                        prevYearEdit.putString(key,value);

                    }
                    prevYearEdit.apply();

//
                    SharedPreferences.Editor storeEdit = storeEditor.edit();
                    storeEdit.clear().apply();

                    SharedPreferences dailyStore = getSharedPreferences("RestaurantDailyStoreForAnalysis",MODE_PRIVATE);
                    SharedPreferences prevDailyStore = getSharedPreferences("PyeviousYearDailyStoreAnalysis" + whichY,MODE_PRIVATE);
                    SharedPreferences.Editor prevDailyEdit = prevDailyStore.edit();
                    Map<String,?> storeMapDaily = dailyStore.getAll();

                    for(Map.Entry<String,?> entry : storeMapDaily.entrySet()){
                        Log.d("map values",entry.getKey() + ": " + entry.getValue().toString());
                        String value = (String) entry.getValue();
                        String key = entry.getKey();

                        prevDailyEdit.putString(key,value);

                    }
                    prevDailyEdit.apply();

                    SharedPreferences.Editor dailyEdit = dailyStore.edit();
                    dailyEdit.clear().apply();

                    SharedPreferences storeOrder = getSharedPreferences("StoreOrders",MODE_PRIVATE);
                    SharedPreferences prevStoreORder = getSharedPreferences("PreviousStoreORder" + whichY,MODE_PRIVATE);
                    SharedPreferences.Editor prevEditorStoreOrder = prevStoreORder.edit();
                    Map<String,?> storeMapOrder = storeOrder.getAll();

                    for(Map.Entry<String,?> entry : storeMapOrder.entrySet()){
                        Log.d("map values",entry.getKey() + ": " + entry.getValue().toString());
                        String value = (String) entry.getValue();
                        String key = entry.getKey();

                        prevEditorStoreOrder.putString(key,value);

                    }
                    prevEditorStoreOrder.apply();

                    SharedPreferences.Editor storeEditORder = storeOrder.edit();
                    storeEditORder.clear().apply();

                    SharedPreferences last7daysReportShared = getSharedPreferences("last7daysReport",MODE_PRIVATE);
                    SharedPreferences.Editor editor7days = last7daysReportShared.edit();
                    editor7days.clear().apply();

                    SharedPreferences lastMonthReportShared = getSharedPreferences("lastMonthlyReport",MODE_PRIVATE);
                    SharedPreferences.Editor ditorMonths = lastMonthReportShared.edit();
                    ditorMonths.clear().apply();

                    SharedPreferences usersF = getSharedPreferences("UsersFrequencyPerMonth",MODE_PRIVATE);
                    SharedPreferences prevUserF = getSharedPreferences("PreviousUserFrequency" + whichY,MODE_PRIVATE);
                    Map<String,?> prevF = usersF.getAll();
                    SharedPreferences.Editor prevUserEdit = prevUserF.edit();
                    for(Map.Entry<String,?> entry : prevF.entrySet()){
                        Log.d("map values",entry.getKey() + ": " + entry.getValue().toString());
                        String value = (String) entry.getValue();
                        String key = entry.getKey();

                        prevUserEdit.putString(key,value);

                    }
                    prevUserEdit.apply();


                    SharedPreferences.Editor userFEdit = usersF.edit();
                    userFEdit.clear().apply();

                    SharedPreferences dishOrderedTogetherAnalysis = getSharedPreferences("DishOrderedWithOthers",MODE_PRIVATE);
                    SharedPreferences prevdishOrderedTogetherAnalysis = getSharedPreferences("PreviousDishOrderedWithOthers" + whichY,MODE_PRIVATE);
                    SharedPreferences.Editor prevdishOrderedTogetherAnalysisEdit = prevdishOrderedTogetherAnalysis.edit();
                    Map<String,?> prevOr = prevdishOrderedTogetherAnalysis.getAll();
                    for(Map.Entry<String,?> entry : prevOr.entrySet()){
                        Log.d("map values",entry.getKey() + ": " + entry.getValue().toString());
                        String value = (String) entry.getValue();
                        String key = entry.getKey();

                        prevdishOrderedTogetherAnalysisEdit.putString(key,value);

                    }
                    prevdishOrderedTogetherAnalysisEdit.apply();
                    SharedPreferences.Editor dishOrderedTogetherEditor = dishOrderedTogetherAnalysis.edit();
                    dishOrderedTogetherEditor.clear().apply();

//
                    SharedPreferences trackTake = getSharedPreferences("TrackingOfTakeAway", MODE_PRIVATE);
                    trackTake.edit().clear().apply();
                    SharedPreferences trackFood = getSharedPreferences("TrackingOfFoodDining", MODE_PRIVATE);
                    trackFood.edit().clear().apply();
                    SharedPreferences DailyUserTrackingFor7days = getSharedPreferences("DailyUserTrackingFor7days", MODE_PRIVATE);
                    DailyUserTrackingFor7days.edit().clear().apply();

                    SharedPreferences DailyAverageOrderMonthly = getSharedPreferences("DailyAverageOrderMonthly", MODE_PRIVATE);
                    DailyAverageOrderMonthly.edit().clear().apply();

                    SharedPreferences RestaurantTrackingDaily = getSharedPreferences("RestaurantTrackingDaily", MODE_PRIVATE);
                    RestaurantTrackingDaily.edit().clear().apply();
//
                    SharedPreferences RestaurantTrackRecords = getSharedPreferences("RestaurantTrackRecords", MODE_PRIVATE);
                    RestaurantTrackRecords.edit().clear().apply();

//                    SharedPreferences UserFrequencyPerMonth = getSharedPreferences("UserFrequencyPerMonth", MODE_PRIVATE);
//                    UserFrequencyPerMonth.edit().clear().apply();

//                    SharedPreferences resDailyStore = getSharedPreferences("RestaurantDailyStoreForAnalysis", MODE_PRIVATE);
//                    resDailyStore.edit().clear().apply();

//
//                    SharedPreferences last7days = getSharedPreferences("last7daysReport", MODE_PRIVATE);
//                    last7days.edit().clear().apply();

                }

                String month = monthName[calendar.get(Calendar.MONTH)] + "_" + year;
                if (month.equals(calenderForExcel.getString("currentMonth", ""))) {

                } else {
                    File file = new File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "RestaurantEarningTracker.xlsx");
                    try {
                        FileInputStream fileInputStream = new FileInputStream(file);
                        XSSFWorkbook workbooks = new XSSFWorkbook(fileInputStream);
                        Sheet sheet = workbooks.createSheet("" + month + "_Earnings_" + year);
                        workbooks.setSheetOrder("" + month + "_Earnings_" + year, 0);
                        workbooks.setActiveSheet(0);
                        Row row = sheet.createRow(0);
                        Cell cell = row.createCell(0);
                        cell.setCellValue("Date");
                        cell = row.createCell(1);
                        editor.putString("currentMonth", month);
                        editor.apply();
                        cell.setCellValue("Transaction ID");
                        cell = row.createCell(2);
                        cell.setCellValue("Payment Mode");
                        cell = row.createCell(3);
                        cell.setCellValue("Amount");

                        FileOutputStream fileOutputStream = new FileOutputStream(file);
                        workbooks.write(fileOutputStream);
                        fileOutputStream.flush();
                        fileOutputStream.close();
                        workbooks.close();
                    } catch (Exception ignored) {

                    }
                }
            }
        }
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference().getRoot().child("Admin").child(UID).child("Restaurant Documents");
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(sharedPreferences.getString("state","")).child(sharedPreferences.getString("locality","")).child(Objects.requireNonNull(UID)).child("Tables");
        new Handler().postDelayed(() -> myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.hasChild("verified")){
                    if(Objects.equals(dataSnapshot.child("verified").getValue(String.class), "no")){
                        AlertDialog.Builder alert = new AlertDialog.Builder(HomeScreen.this);
                        alert.setTitle("Error")
                                .setMessage("Your restaurant is not yet verified so you can't accept orders until verified")
                                .setPositiveButton("Exit", (dialogInterface, i) -> {
                                    dialogInterface.dismiss();
                                }).setNegativeButton("Contact Ordinalo", (dialogInterface, i) -> {
                                    dialogInterface.dismiss();

                                }).create();
                        alert.setCancelable(false);
                        alert.show();
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        }),90);


//        new Handler().postDelayed(() -> {
//
//        },1700);

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                boolean isConnected = activeNetwork != null &&
                        activeNetwork.isConnectedOrConnecting();

                if (!isConnected) {
                    View parentLayout = findViewById(android.R.id.content);
                    Snackbar.make(parentLayout, "Please connect to internet :)", Snackbar.LENGTH_SHORT)
                            .setAction("CLOSE", view -> {

                            })
                            .setActionTextColor(getResources().getColor(android.R.color.holo_red_light))
                            .show();
                } else {
                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    if (dataSnapshot.hasChild("timeInMillis")) {
                                        String tableNum = dataSnapshot.child("tableNum").getValue(String.class);
                                        String time = String.valueOf(dataSnapshot.child("timeInMillis").getValue());
                                        String seats = dataSnapshot.child("numSeats").getValue(String.class);
                                        final String id = String.valueOf(dataSnapshot.child("customerId").getValue());
                                        int result = time.compareTo(String.valueOf(System.currentTimeMillis()));
                                        if (result < 0) {

                                            DatabaseReference checkForNextReservation = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(sharedPreferences.getString("state","")).child(sharedPreferences.getString("locality","")).child(auth.getUid())
                                                    .child("Tables").child(tableNum).child("nextReservation");

                                            checkForNextReservation.limitToFirst(1).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    if(snapshot.exists()){
                                                        for(DataSnapshot dataSnapshotMine : snapshot.getChildren()){
                                                            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                                                            firestore.collection(sharedPreferences.getString("state","")).document("Restaurants").collection(sharedPreferences.getString("state","")).document(auth.getUid())
                                                                    .collection("Tables").document(tableNum)
                                                                    .update("timeInMillis",dataSnapshotMine.child("timeInMillis").getValue(String.class),"timeOfBooking",dataSnapshotMine.child("timeOfBooking").getValue(String.class)
                                                                            ,"customerId",dataSnapshotMine.child("customerId").getValue(String.class) + "","status","Reserved","seats",seats);
                                                            String storeVal = dataSnapshotMine.child("timeInMillis").getValue(String.class);
                                                            databaseReference.child(tableNum).child("customerId").setValue(dataSnapshotMine.child("customerId").getValue(String.class));
                                                            databaseReference.child(tableNum).child("time").setValue(dataSnapshotMine.child("time").getValue(String.class));
                                                            databaseReference.child(tableNum).child("status").setValue("Reserved");
                                                            databaseReference.child(tableNum).child("timeInMillis").setValue(dataSnapshotMine.child("timeInMillis").getValue(String.class));
                                                            databaseReference.child(tableNum).child("timeOfBooking").setValue(dataSnapshotMine.child("timeOfBooking").getValue(String.class));

                                                            DatabaseReference removeFromUser = FirebaseDatabase.getInstance().getReference().getRoot().child("Users").child(id).child("Reserve Tables").child(UID);
                                                            removeFromUser.child(Objects.requireNonNull(dataSnapshot.getKey())).removeValue();
                                                            RequestQueue requestQueue = Volley.newRequestQueue(HomeScreen.this);


                                                            DatabaseReference checkForNextReservationDelete = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(sharedPreferences.getString("state","")).child(sharedPreferences.getString("locality","")).child(auth.getUid())
                                                                    .child("Tables").child(tableNum).child("nextReservation");
                                                            checkForNextReservationDelete.child(storeVal).removeValue();
                                                            JSONObject main = new JSONObject();
                                                            try {
                                                                main.put("to", "/topics/" + id + "");
                                                                JSONObject notification = new JSONObject();
                                                                notification.put("title", "Cancelled");
                                                                notification.put("click_action", "Table Frag");
                                                                notification.put("body", "Your Reserved Tables is cancelled because you didn't make it on time");
                                                                main.put("notification", notification);

                                                                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, main, response -> {

                                                                }, error -> {
//                                               Toast.makeText(, error.getLocalizedMessage()+"null", Toast.LENGTH_SHORT).show();
                                                                }) {
                                                                    @Override
                                                                    public Map<String, String> getHeaders() {
                                                                        Map<String, String> header = new HashMap<>();
                                                                        header.put("content-type", "application/json");
                                                                        header.put("authorization", "key=AAAAjq_WsHs:APA91bGZV-uH-NJddxIniy8h1tDGDHqxhgvFdyNRDaV_raxjvSM_FkKu7JSwtSp4Q_iSmPuTKGGIB2M_07c9rKgPXUH43-RzpK6zkaSaIaNgmeiwUO40rYxYUZAkKoLAQQVeVJ7mXboD");
                                                                        return header;
                                                                    }
                                                                };

                                                                requestQueue.add(jsonObjectRequest);
                                                            } catch (Exception e) {
                                                                Toast.makeText(HomeScreen.this, e.getLocalizedMessage() + "null", Toast.LENGTH_SHORT).show();
                                                            }

                                                        }
                                                    }else {
                                                        assert tableNum != null;
                                                        databaseReference.child(tableNum).child("customerId").removeValue();
                                                        databaseReference.child(tableNum).child("time").removeValue();
                                                        databaseReference.child(tableNum).child("timeInMillis").removeValue();
                                                        databaseReference.child(tableNum).child("timeOfBooking").removeValue();
                                                        databaseReference.child(tableNum).child("status").setValue("available");
                                                        Toast.makeText(HomeScreen.this, "" + seats, Toast.LENGTH_SHORT).show();
                                                        HashMap<String,String> myMap = new HashMap<>();
                                                        myMap.put("status","available");
                                                        myMap.put("tableNum",tableNum);
                                                        myMap.put("seats",seats);
                                                        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                                                        firestore.collection(sharedPreferences.getString("state","")).document("Restaurants").collection(sharedPreferences.getString("locality","")).document(Objects.requireNonNull(auth.getUid()))
                                                                .collection("Tables").document(tableNum).set(myMap);

                                                        DatabaseReference removeFromUser = FirebaseDatabase.getInstance().getReference().getRoot().child("Users").child(id).child("Reserve Tables").child(UID);
                                                        removeFromUser.child(Objects.requireNonNull(dataSnapshot.getKey())).removeValue();
                                                        RequestQueue requestQueue = Volley.newRequestQueue(HomeScreen.this);
                                                        JSONObject main = new JSONObject();
                                                        try {
                                                            main.put("to", "/topics/" + id + "");
                                                            JSONObject notification = new JSONObject();
                                                            notification.put("title", "Cancelled");
                                                            notification.put("click_action", "Table Frag");
                                                            notification.put("body", "Your Reserved Tables is cancelled because you didn't make it on time");
                                                            main.put("notification", notification);

                                                            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, main, response -> {

                                                            }, error -> {
//                                               Toast.makeText(, error.getLocalizedMessage()+"null", Toast.LENGTH_SHORT).show();
                                                            }) {
                                                                @Override
                                                                public Map<String, String> getHeaders() {
                                                                    Map<String, String> header = new HashMap<>();
                                                                    header.put("content-type", "application/json");
                                                                    header.put("authorization", "key=AAAAjq_WsHs:APA91bGZV-uH-NJddxIniy8h1tDGDHqxhgvFdyNRDaV_raxjvSM_FkKu7JSwtSp4Q_iSmPuTKGGIB2M_07c9rKgPXUH43-RzpK6zkaSaIaNgmeiwUO40rYxYUZAkKoLAQQVeVJ7mXboD");
                                                                    return header;
                                                                }
                                                            };

                                                            requestQueue.add(jsonObjectRequest);
                                                        } catch (Exception e) {
                                                            Toast.makeText(HomeScreen.this, e.getLocalizedMessage() + "null", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });

//                                            assert tableNum != null;
//                                            databaseReference.child(tableNum).child("customerId").removeValue();
//                                            databaseReference.child(tableNum).child("time").removeValue();
//                                            databaseReference.child(tableNum).child("timeInMillis").removeValue();
//                                            databaseReference.child(tableNum).child("timeOfBooking").removeValue();
//                                            databaseReference.child(tableNum).child("status").setValue("available");
//                                            Toast.makeText(HomeScreen.this, "" + seats, Toast.LENGTH_SHORT).show();
//                                            HashMap<String,String> myMap = new HashMap<>();
//                                            myMap.put("status","available");
//                                            myMap.put("tableNum",tableNum);
//                                            myMap.put("seats",seats);
//                                            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
//                                            firestore.collection(sharedPreferences.getString("state","")).document("Restaurants").collection(sharedPreferences.getString("locality","")).document(Objects.requireNonNull(auth.getUid()))
//                                                    .collection("Tables").document(tableNum).set(myMap);
//
//                                            DatabaseReference removeFromUser = FirebaseDatabase.getInstance().getReference().getRoot().child("Users").child(id).child("Reserve Tables").child(UID);
//                                            removeFromUser.child(Objects.requireNonNull(dataSnapshot.getKey())).removeValue();
//                                            RequestQueue requestQueue = Volley.newRequestQueue(HomeScreen.this);
//                                            JSONObject main = new JSONObject();
//                                            try {
//                                                main.put("to", "/topics/" + id + "");
//                                                JSONObject notification = new JSONObject();
//                                                notification.put("title", "Cancelled");
//                                                notification.put("click_action", "Table Frag");
//                                                notification.put("body", "Your Reserved Tables is cancelled because you didn't make it on time");
//                                                main.put("notification", notification);
//
//                                                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, main, response -> {
//
//                                                }, error -> {
////                                               Toast.makeText(, error.getLocalizedMessage()+"null", Toast.LENGTH_SHORT).show();
//                                                }) {
//                                                    @Override
//                                                    public Map<String, String> getHeaders() {
//                                                        Map<String, String> header = new HashMap<>();
//                                                        header.put("content-type", "application/json");
//                                                        header.put("authorization", "key=AAAAjq_WsHs:APA91bGZV-uH-NJddxIniy8h1tDGDHqxhgvFdyNRDaV_raxjvSM_FkKu7JSwtSp4Q_iSmPuTKGGIB2M_07c9rKgPXUH43-RzpK6zkaSaIaNgmeiwUO40rYxYUZAkKoLAQQVeVJ7mXboD");
//                                                        return header;
//                                                    }
//                                                };
//
//                                                requestQueue.add(jsonObjectRequest);
//                                            } catch (Exception e) {
//                                                Toast.makeText(HomeScreen.this, e.getLocalizedMessage() + "null", Toast.LENGTH_SHORT).show();
//                                            }
                                        }
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        },0,10000);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.notification,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.notification)
            startActivity(new Intent(HomeScreen.this, SupportActivity.class));
        else if(id == R.id.randomChatMessagesUsers){
            startActivity(new Intent(HomeScreen.this, ReplaceOrderRequests.class));
        }
        else if(id == R.id.notificationShowAdmin){
            startActivity(new Intent(HomeScreen.this, NotificationActivity.class));
        }
        else {
            AlertDialog.Builder builder = new AlertDialog.Builder(HomeScreen.this);
            builder.setTitle("Choose one").setMessage("Choose one option from below")
                            .setPositiveButton("Open Ordinalo Chat", (dialog, which) -> {
                                dialog.dismiss();
                                startActivity(new Intent(HomeScreen.this, RandomChatNoww.class));
                            }).setNegativeButton("Open Users Chat", (dialog, which) -> {
                                dialog.dismiss();
                                startActivity(new Intent(HomeScreen.this, RandomChatWithUsers.class));
                            }).setNeutralButton("Exit", (dialog, which) -> dialog.dismiss()).create();
            builder.show();

        }
        return super.onOptionsItemSelected(item);
    }

    private void initialise() {
        UID = auth.getUid() + "";
        auth = FirebaseAuth.getInstance();
        bubble = findViewById(R.id.top_navigation_constraint);
        manager = getSupportFragmentManager();
        FirebaseMessaging.getInstance().subscribeToTopic(Objects.requireNonNull(UID));
        reference = FirebaseDatabase.getInstance().getReference().getRoot().child("Admin").child(UID);
    }

    public class BackgroundWork extends AsyncTask<Void,Void,Void>{
        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            resRef = FirebaseDatabase.getInstance().getReference().getRoot().child("Admin").child(Objects.requireNonNull(UID));
            resRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.hasChild("fastwayReply")){
                        AlertDialog.Builder alert = new AlertDialog.Builder(HomeScreen.this);
                        alert.setTitle("Reply")
                                .setMessage("You have a new reply from Ordinalo")
                                .setCancelable(false)
                                .setPositiveButton("Exit", (dialog, which) -> {
                                    resRef.child("fastwayReply").removeValue();
                                    dialog.dismiss();
                                }).setNegativeButton("See Message", (dialog, which) -> {
                            resRef.child("fastwayReply").removeValue();
                            startActivity(new Intent(HomeScreen.this, RandomChatNoww.class));
                            dialog.dismiss();
                        }).create();
                        alert.setOnCancelListener(dialog -> {
                            dialog.dismiss();
                            resRef.child("fastwayReply").removeValue();
                        });
                        alert.show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });



            DatabaseReference checkIfCommissionNeeded = FirebaseDatabase.getInstance().getReference().getRoot().child("Admin").child(Objects.requireNonNull(UID));
            checkIfCommissionNeeded.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.hasChild("lastCommissionPaid")){
                            long lastPaidDate = Long.parseLong(Objects.requireNonNull(dataSnapshot.child("lastCommissionPaid").getValue(String.class)));
                        if(currentTime - lastPaidDate >= 2678400000L){
                            checkIfNeededToAddToQuery();
                            SharedPreferences sharedPreferences = getSharedPreferences("CashCommission",MODE_PRIVATE);
                             SharedPreferences.Editor editor = sharedPreferences.edit();
                             editor.putString("fine","10");
                             editor.apply();
                            AlertDialog.Builder alert = new AlertDialog.Builder(HomeScreen.this);
                            alert.setTitle("Cash Transaction").setMessage("It's time for payment of cash transaction commission, since you have exceeded time limit fine of 10% will be applied\nDo you wanna pay now or you can pay later!")
                                    .setPositiveButton("Pay Now", (dialogInterface, i) -> {
                                        dialogInterface.dismiss();
                                        startActivity(new Intent(HomeScreen.this, CashTransactionCommissionActivity.class));
                                    }).setNegativeButton("Later", (dialogInterface, i) -> dialogInterface.dismiss()).create();
                            alert.setCancelable(false);
                            alert.show();
                        } else if(currentTime - lastPaidDate >= 2592000000L){
                            checkIfNeededToAddToQuery();
                            AlertDialog.Builder alert = new AlertDialog.Builder(HomeScreen.this);
                            alert.setTitle("Cash Transaction").setMessage("It's time for payment of cash transaction commission, today is last day for else fine of 10% will be applied\nDo you wanna pay now or you can pay later!")
                                    .setPositiveButton("Pay Now", (dialogInterface, i) -> {
                                        dialogInterface.dismiss();
                                        startActivity(new Intent(HomeScreen.this, CashTransactionCommissionActivity.class));
                                    }).setNegativeButton("Later", (dialogInterface, i) -> dialogInterface.dismiss()).create();
                            alert.setCancelable(false);
                            alert.show();
                        }
                        else if(currentTime - lastPaidDate >= 2505600000L){
                            checkIfNeededToAddToQuery();
                            AlertDialog.Builder alert = new AlertDialog.Builder(HomeScreen.this);
                            alert.setTitle("Cash Transaction").setMessage("It's time for payment of cash transaction commission, tomorrow is last day after that fine will be applied\nDo you wanna pay now or you can pay later!")
                                    .setPositiveButton("Pay Now", (dialogInterface, i) -> {
                                        dialogInterface.dismiss();
                                        startActivity(new Intent(HomeScreen.this, CashTransactionCommissionActivity.class));
                                    }).setNegativeButton("Later", (dialogInterface, i) -> dialogInterface.dismiss()).create();
                            alert.setCancelable(false);
                            alert.show();
                        }
                        else if(currentTime - lastPaidDate >= 2419200000L){
                            checkIfNeededToAddToQuery();
                            AlertDialog.Builder alert = new AlertDialog.Builder(HomeScreen.this);
                            alert.setTitle("Cash Transaction").setMessage("It's time for payment of cash transaction commission\nDo you wanna pay now or you can pay later!")
                                    .setPositiveButton("Pay Now", (dialogInterface, i) -> {
                                        dialogInterface.dismiss();
                                        startActivity(new Intent(HomeScreen.this, CashTransactionCommissionActivity.class));
//                                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Complaints").child("Fines");
//                                        CashCommissionClass cashCommissionClass = new CashCommissionClass("","Cash Commission & Platform Fee",System.currentTimeMillis() + "","Admin");
//                                        databaseReference.child(Objects.requireNonNull(auth.getUid())).setValue(cashCommissionClass);
                                    }).setNegativeButton("Later", (dialogInterface, i) -> dialogInterface.dismiss()).create();
                            alert.setCancelable(false);
                            alert.show();
                        }
                    }else{
                        if(dataSnapshot.hasChild("registrationDate")){
                            long registerTime = Long.parseLong(Objects.requireNonNull(dataSnapshot.child("registrationDate").getValue(String.class)));

                            if(currentTime - registerTime >= 2678400000L){
                                checkIfNeededToAddToQuery();
                                SharedPreferences sharedPreferences = getSharedPreferences("CashCommission",MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("fine","10");
                                editor.apply();
                                AlertDialog.Builder alert = new AlertDialog.Builder(HomeScreen.this);
                                alert.setTitle("Cash Transaction").setMessage("It's time for payment of cash transaction commission, since you have exceeded time limit fine of 10% will be applied\nDo you wanna pay now or you can pay later!")
                                        .setPositiveButton("Pay Now", (dialogInterface, i) -> {
                                            dialogInterface.dismiss();
                                            startActivity(new Intent(HomeScreen.this, CashTransactionCommissionActivity.class));

                                        }).setNegativeButton("Later", (dialogInterface, i) -> dialogInterface.dismiss()).create();
                                alert.setCancelable(false);
                                alert.show();
                            }
                           else if(currentTime - registerTime >= 2592000000L){
                                checkIfNeededToAddToQuery();
                                AlertDialog.Builder alert = new AlertDialog.Builder(HomeScreen.this);
                                alert.setTitle("Cash Transaction").setMessage("It's time for payment of cash transaction commission, today is last day for else fine of 10% will be applied\nDo you wanna pay now or you can pay later!")
                                        .setPositiveButton("Pay Now", (dialogInterface, i) -> {
                                            dialogInterface.dismiss();
                                            startActivity(new Intent(HomeScreen.this, CashTransactionCommissionActivity.class));
                                        }).setNegativeButton("Later", (dialogInterface, i) -> dialogInterface.dismiss()).create();
                                alert.setCancelable(false);
                                alert.show();
                            }
                            else if(currentTime - registerTime >= 2505600000L){
                                checkIfNeededToAddToQuery();
                                AlertDialog.Builder alert = new AlertDialog.Builder(HomeScreen.this);
                                alert.setTitle("Cash Transaction").setMessage("It's time for payment of cash transaction commission, tomorrow is last day after that fine will be applied\nDo you wanna pay now or you can pay later!")
                                        .setPositiveButton("Pay Now", (dialogInterface, i) -> {
                                            dialogInterface.dismiss();
                                            startActivity(new Intent(HomeScreen.this, CashTransactionCommissionActivity.class));
                                        }).setNegativeButton("Later", (dialogInterface, i) -> dialogInterface.dismiss()).create();
                                alert.setCancelable(false);
                                alert.show();
                            }
                            else if(currentTime - registerTime >= 2419200000L){
                                checkIfNeededToAddToQuery();
                                AlertDialog.Builder alert = new AlertDialog.Builder(HomeScreen.this);
                                alert.setTitle("Cash Transaction").setMessage("It's time for payment of cash transaction commission\nDo you wanna pay now or you can pay later!")
                                        .setPositiveButton("Pay Now", (dialogInterface, i) -> {
                                            dialogInterface.dismiss();
                                            startActivity(new Intent(HomeScreen.this, CashTransactionCommissionActivity.class));

                                        }).setNegativeButton("Later", (dialogInterface, i) -> dialogInterface.dismiss()).create();
                                alert.setCancelable(false);
                                alert.show();
                            }
                        }else{
                            DatabaseReference checkIfApproved = FirebaseDatabase.getInstance().getReference().getRoot().child("Complaints").child("Registered Restaurants").child(sharedPreferences.getString("state",""));
                            checkIfApproved.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(snapshot.hasChild(Objects.requireNonNull(auth.getUid()))){

                                    }else
                                        checkIfCommissionNeeded.child("registrationDate").setValue(System.currentTimeMillis() + "");
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                        }
                    }
                }


                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


            checkForBank = FirebaseDatabase.getInstance().getReference().getRoot().child("Admin").child(Objects.requireNonNull(UID));
            checkForBank.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(!snapshot.hasChild("Bank Details")){
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(HomeScreen.this);
                        alertDialog.setTitle("Important");
                        alertDialog.setMessage("You need to add bank details to accept payments");
                        alertDialog.setPositiveButton("Add", (dialogInterface, i) -> {
                            SharedPreferences accountInfo = getSharedPreferences("AccountInfo",Context.MODE_PRIVATE);
                            Intent intent = new Intent(HomeScreen.this, VendorDetailsActivity.class);
                            intent.putExtra("name",accountInfo.getString("name",""));
                            intent.putExtra("email",accountInfo.getString("email",""));
                            startActivity(intent);
                        }).create();

                        alertDialog.show();
                    }else{
                        SharedPreferences sharedPreferences = getSharedPreferences("loginInfo",MODE_PRIVATE);
                        if(!sharedPreferences.contains("payoutMethodChoosen")){
                            AlertDialog.Builder builder = new AlertDialog.Builder(HomeScreen.this);
                            builder.setTitle("Payout Method").setMessage("You need to choose payout method in-order to receive payments")
                                    .setPositiveButton("Choose Now", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            startActivity(new Intent(HomeScreen.this, SelectPayoutMethodType.class));
                                        }
                                    }).setNegativeButton("Later", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    }).create().show();
                        }
                    }

                    if(snapshot.child("Restaurant Documents").hasChild("timeToUploadDocs")){
                        long remainingTime = Long.parseLong(Objects.requireNonNull(snapshot.child("Restaurant Documents").child("timeToUploadDocs").getValue(String.class)));
                        if(remainingTime > System.currentTimeMillis()) {
                            long daysLeft = remainingTime - System.currentTimeMillis();
                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(HomeScreen.this);
                            alertDialog.setTitle("Important");

                            alertDialog.setMessage("You need to upload required documents within " + TimeUnit.MILLISECONDS.toDays(daysLeft) + " days");
                            alertDialog.setPositiveButton("Upload Now", (dialogInterface, i) -> {
                                startActivity(new Intent(HomeScreen.this, UploadRemainingDocs.class));
                            }).setNegativeButton("Later", (dialogInterface, i) -> dialogInterface.dismiss()).create();

                            alertDialog.show();
                        }else{
                            AlertDialog.Builder builder = new AlertDialog.Builder(HomeScreen.this);
                            builder.setTitle("Time Exceeded").setMessage("Your 30 days period to submit documents is over\nYour restaurant is now suspended from Ordinalo")
                                    .setPositiveButton("Exit", (dialogInterface, i) -> dialogInterface.dismiss()).create().show();

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

    private void checkIfBankDetailsSubmitted() {

    }

    private void checkIfNeededToAddToQuery() {
        new Handler().postDelayed(() -> {
            DatabaseReference check = FirebaseDatabase.getInstance().getReference().getRoot().child("Complaints").child("Fines");
            check.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(!snapshot.hasChild(Objects.requireNonNull(auth.getUid()))){
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Complaints").child("Fines");

                        CashCommissionClass cashCommissionClass = new CashCommissionClass("","Cash Commission & Platform Fee",System.currentTimeMillis() + "","Admin");
                        databaseReference.child(Objects.requireNonNull(auth.getUid())).setValue(cashCommissionClass);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        },80);
    }

    public class checkStatus extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            RequestQueue requestQueue = Volley.newRequestQueue(HomeScreen.this);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, response -> {
                Log.i("resp",response + "");
                Log.i("id",subRefID);
                String respo = response.trim();
                if(respo.equals("INITIALIZE")) {
                    premEditor.putString("status","initialized");
                    premEditor.apply();
                }else if(respo.equals("ACTIVE")){
                    premEditor.putString("status","active");
                    premEditor.apply();
                }else{
                    premEditor.putString("status","not active");
                    premEditor.apply();
                }
//                editor.putString("status","active");
//                editor.apply();
            }, error -> {
                Log.i("resp","error");
            }){
                @NonNull
                @Override
                protected Map<String, String> getParams() {
                    Map<String,String> params = new HashMap<>();
                    params.put("subID",subRefID);
                    return params;
                }
            };
            requestQueue.add(stringRequest);
            return null;
        }
    }
    private final BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Extract data included in the Intent
            String t = intent.getStringExtra("value1");
            String t1 = intent.getStringExtra("value2");

            AlertDialog.Builder builder = new AlertDialog.Builder(HomeScreen.this);
            builder.setTitle("Collect Cash").setMessage(t + "\n" + t1)
                    .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    }).create();
            builder.show();
            //alert data here
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("myFunction"));
        SharedPreferences sharedPreferences = getSharedPreferences("loginInfo",MODE_PRIVATE);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(sharedPreferences.getString("state","")).child(sharedPreferences.getString("state",""))
                .child(auth.getUid());
        databaseReference.child("isAdminLive").setValue("yes");
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences sharedPreferences = getSharedPreferences("loginInfo",MODE_PRIVATE);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(sharedPreferences.getString("state","")).child(sharedPreferences.getString("state",""))
                .child(auth.getUid());
        databaseReference.child("isAdminLive").setValue("no");
    }

    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences sharedPreferences = getSharedPreferences("loginInfo",MODE_PRIVATE);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(sharedPreferences.getString("state","")).child(sharedPreferences.getString("state",""))
                .child(auth.getUid());
        databaseReference.child("isAdminLive").setValue("no");
    }
}