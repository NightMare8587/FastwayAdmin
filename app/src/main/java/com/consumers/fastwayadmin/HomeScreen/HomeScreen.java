package com.consumers.fastwayadmin.HomeScreen;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import androidx.fragment.app.FragmentManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
//import com.aspose.cells.Workbook;
import com.consumers.fastwayadmin.Info.RestaurantDocuments.ReUploadDocumentsAgain;
import com.consumers.fastwayadmin.Info.RestaurantDocuments.UploadRemainingDocs;
import com.consumers.fastwayadmin.NavFrags.AccountSettingsFragment;
import com.consumers.fastwayadmin.NavFrags.BankVerification.SelectPayoutMethodType;
import com.consumers.fastwayadmin.NavFrags.CashCommission.CashTransactionCommissionActivity;
import com.consumers.fastwayadmin.NavFrags.FastwayPremiumActivites.NotifyAdminSubscribePremium;
import com.consumers.fastwayadmin.NavFrags.HomeFrag;
import com.consumers.fastwayadmin.NavFrags.MenuFrag;
import com.consumers.fastwayadmin.NavFrags.ReplaceOrders.ReplaceOrderRequests;
import com.consumers.fastwayadmin.NavFrags.TablesFrag;
import com.consumers.fastwayadmin.NavFrags.BankVerification.VendorDetailsActivity;
import com.consumers.fastwayadmin.R;
import com.consumers.fastwayadmin.RandomChatNoww;
import com.gauravk.bubblenavigation.BubbleNavigationConstraintView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
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
import java.util.Calendar;
import java.util.HashMap;
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
        myEditor.putString("payoutMethodChoosen","imps");
        if(!sharedPreferences.contains("currentYear"))
        {
            myEditor.putString("currentYear",year + "");
        }
        myEditor.apply();
        manager.beginTransaction().replace(R.id.homescreen,new HomeFrag()).commit();
        calenderForExcel = getSharedPreferences("CalenderForExcel",MODE_PRIVATE);
        editor = calenderForExcel.edit();
        adminPrem = getSharedPreferences("AdminPremiumDetails",MODE_PRIVATE);
        premEditor = adminPrem.edit();
        ConnectivityManager cm =
                (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        FirebaseMessaging.getInstance().subscribeToTopic("FastwayQueryDB");
      new BackgroundWork().execute();
        checkIfBankDetailsSubmitted();
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
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
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

                    SharedPreferences storeEditor = getSharedPreferences("DishAnalysis",MODE_PRIVATE);
                    SharedPreferences.Editor storeEdit = storeEditor.edit();
                    storeEdit.clear().apply();

                    SharedPreferences dailyStore = getSharedPreferences("RestaurantDailyStoreForAnalysis",MODE_PRIVATE);
                    SharedPreferences.Editor dailyEdit = dailyStore.edit();
                    dailyEdit.clear().apply();

                    SharedPreferences storeOrder = getSharedPreferences("StoreOrders",MODE_PRIVATE);
                    SharedPreferences.Editor storeEditORder = storeOrder.edit();
                    storeEditORder.clear().apply();

                    SharedPreferences usersF = getSharedPreferences("UsersFrequencyPerMonth",MODE_PRIVATE);
                    SharedPreferences.Editor userFEdit = usersF.edit();
                    userFEdit.clear().apply();
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
                                }).setNegativeButton("Contact Fastway", (dialogInterface, i) -> {
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
        }),800);


        new Handler().postDelayed(() -> {
            DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference().getRoot().child("Admin").child(Objects.requireNonNull(auth.getUid()));
            databaseReference1.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.hasChild("subRefID")){
                        subRefID = String.valueOf(snapshot.child("subRefID").getValue()).trim();
                        new checkStatus().execute();
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
        },1700);

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
                                        final String id = String.valueOf(dataSnapshot.child("customerId").getValue());
                                        int result = time.compareTo(String.valueOf(System.currentTimeMillis()));
                                        if (result < 0) {
                                            assert tableNum != null;
                                            databaseReference.child(tableNum).child("customerId").removeValue();
                                            databaseReference.child(tableNum).child("time").removeValue();
                                            databaseReference.child(tableNum).child("timeInMillis").removeValue();
                                            databaseReference.child(tableNum).child("timeOfBooking").removeValue();
                                            databaseReference.child(tableNum).child("status").setValue("available");
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
                                                        header.put("authorization", "key=AAAAsigSEMs:APA91bEUF9ZFwIu84Jctci56DQd0TQOepztGOIKIBhoqf7N3ueQrkClw0xBTlWZEWyvwprXZmZgW2MNywF1pNBFpq1jFBr0CmlrJ0wygbZIBOnoZ0jP1zZC6nPxqF2MAP6iF3wuBHD2R");
                                                        return header;
                                                    }
                                                };

                                                requestQueue.add(jsonObjectRequest);
                                            } catch (Exception e) {
                                                Toast.makeText(HomeScreen.this, e.getLocalizedMessage() + "null", Toast.LENGTH_SHORT).show();
                                            }
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
        else
          startActivity(new Intent(HomeScreen.this, RandomChatNoww.class));
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
        protected Void doInBackground(Void... voids) {
            resRef = FirebaseDatabase.getInstance().getReference().getRoot().child("Admin").child(Objects.requireNonNull(UID));
            resRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.hasChild("fastwayReply")){
                        AlertDialog.Builder alert = new AlertDialog.Builder(HomeScreen.this);
                        alert.setTitle("Reply")
                                .setMessage("You have a new reply from fastway")
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

            resRef = FirebaseDatabase.getInstance().getReference().getRoot().child("Admin").child(Objects.requireNonNull(UID)).child("Restaurant Documents");
            resRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists() && snapshot.hasChild("reasonForCancel")){
                        AlertDialog.Builder alert = new AlertDialog.Builder(HomeScreen.this);
                        String reason = snapshot.child("reasonForCancel").getValue(String.class);
                        alert.setTitle("Error").setMessage("Your restaurant registration is denied by fastway for following reason's:\n\n" + reason + "\n\nYou can submit another response for restaurant registration")
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
                            checkIfCommissionNeeded.child("registrationDate").setValue(System.currentTimeMillis() + "");
                        }
                    }
                }




                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });




            return null;
        }
    }

    private void checkIfBankDetailsSubmitted() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
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
                                builder.setTitle("Time Exceeded").setMessage("Your 2 weeks period to submit documents is over\nYour restaurant is now suspended from Fastway")
                                        .setPositiveButton("Exit", (dialogInterface, i) -> dialogInterface.dismiss()).create().show();

                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        },400);
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
        },200);
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
}