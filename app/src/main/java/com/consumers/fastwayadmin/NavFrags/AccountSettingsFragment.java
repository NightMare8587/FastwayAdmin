package com.consumers.fastwayadmin.NavFrags;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.consumers.fastwayadmin.HomeScreen.ReportsClassRecycler;
import com.consumers.fastwayadmin.ListViewActivity.CashTrans.CashTransactions;
import com.consumers.fastwayadmin.ListViewActivity.MyAccount;
import com.consumers.fastwayadmin.ListViewActivity.MyOrdersTransactions;
import com.consumers.fastwayadmin.MyService;
import com.consumers.fastwayadmin.NavFrags.CashCommission.CashTransactionCommissionActivity;
import com.consumers.fastwayadmin.NavFrags.FastwayPremiumActivites.FastwayPremiums;
import com.consumers.fastwayadmin.R;
import com.consumers.fastwayadmin.SplashAndIntro.SplashScreen;
import com.developer.kalert.KAlertDialog;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
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

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class AccountSettingsFragment extends PreferenceFragmentCompat {
    GoogleSignInClient googleSignInClient;
    DatabaseReference databaseReference;
    boolean available = false;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    String UID;
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);

        databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Admin").child(Objects.requireNonNull(auth.getUid()));
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChild("freeTrialDate")){
                    available = true;
                }else if(snapshot.hasChild("subscriptionStatus")){
                    long subTime = Long.parseLong(String.valueOf(snapshot.child("subscriptionStatus").getValue()));
                    if(System.currentTimeMillis() < subTime)
                        available = true;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        UID = auth.getUid();
        googleSignInClient = GoogleSignIn.getClient(requireContext(),gso);
        Preference account = findPreference("myAccount");
//        Preference blockedUsers = findPreference("blockedUsers");
        Preference reports = findPreference("viewReportsGenerated");
        Preference viewExcel = findPreference("premiumExcelSheet");
        Preference myTrans = findPreference("myTrans");
        Preference logout = findPreference("logoutNow");
        Preference premium = findPreference("premium");
        Preference terms = findPreference("termsConditions");
        Preference comFee = findPreference("commisionAndFee");
        SwitchPreferenceCompat takeaway = findPreference("TakeAwaySwitch");
        SwitchPreferenceCompat tableOrder = findPreference("tableBookingAllowedSwitch");

        reports.setOnPreferenceClickListener(preference -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setTitle("Choose One Option")
                    .setMessage("Choose weekly or monthly report or See All\nClick anywhere outside to close the dialog")
                    .setPositiveButton("This Week", (dialogInterface, i) -> {

                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        File file12 = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) + "/WeeklyReportTracker.pdf");
                        if(file12.exists()) {
                            Toast.makeText(requireContext(), "Opening....", Toast.LENGTH_SHORT).show();
                            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            intent.setDataAndType(FileProvider.getUriForFile(requireContext(), requireContext().getPackageName() + ".provider", file12), "application/pdf");
                            startActivity(intent);
                        }else
                            Toast.makeText(requireContext(), "No Reports Generated", Toast.LENGTH_SHORT).show();
                    }).setNegativeButton("Open Monthly", (dialogInterface, i) -> {
                        Toast.makeText(requireContext(), "Opening....", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        File file12 = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) + "/MonthlyReportTracker.pdf");
                        if(file12.exists()) {
                            Toast.makeText(requireContext(), "Opening....", Toast.LENGTH_SHORT).show();
                            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            intent.setDataAndType(FileProvider.getUriForFile(requireContext(), requireContext().getPackageName() + ".provider", file12), "application/pdf");
                            startActivity(intent);
                        }else
                            Toast.makeText(requireContext(), "No Reports Generated", Toast.LENGTH_SHORT).show();
                    }).setNeutralButton("See All", (dialogInterface, i) -> {
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(requireContext());
                        alertDialog.setTitle("See Reports");
                        alertDialog.setMessage("Choose which one to see");

                        View view;
                        LayoutInflater inflater = (LayoutInflater) requireActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        view = inflater.inflate(R.layout.viewreports_alertdialog,null);

                        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewAlertDialogLayout);
                        RadioGroup radioGroup = view.findViewById(R.id.radioGroupViewAlertDialog);
                        TextView textView = view.findViewById(R.id.textView46);

                        Gson gson = new Gson();
                        Type type = new TypeToken<HashMap<String, HashMap<String, String>>>() {
                        }.getType();
                        List<String> keyList = new ArrayList<>();
                        SharedPreferences getReports = requireContext().getSharedPreferences("TrackOfAllInsights",MODE_PRIVATE);
                        radioGroup.setOnCheckedChangeListener((radioGroup1, i12) -> {
                            switch (i12){
                                case R.id.WeekradioButton:
                                    keyList.clear();
                                    if(getReports.contains("week")){
                                        recyclerView.setVisibility(View.VISIBLE);
                                        textView.setVisibility(View.INVISIBLE);
                                        HashMap<String,HashMap<String,String>> map = gson.fromJson(getReports.getString("week",""),type);

                                        HashMap<String,String> innerMap = new HashMap<>(map.get("Weekly"));

                                        for(Map.Entry<String,String> data : innerMap.entrySet()){
                                            keyList.add(data.getKey());
                                        }

                                        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
                                        recyclerView.setAdapter(new ReportsClassRecycler(keyList,"Weekly"));

                                    }else
                                    {
                                        recyclerView.setVisibility(View.INVISIBLE);
                                        textView.setVisibility(View.VISIBLE);
                                    }
                                    break;
                                case R.id.MonthradioButton3:
                                    keyList.clear();
                                    if(getReports.contains("month")){
                                        recyclerView.setVisibility(View.VISIBLE);
                                        textView.setVisibility(View.INVISIBLE);
                                        HashMap<String,HashMap<String,String>> map = gson.fromJson(getReports.getString("month",""),type);

                                        HashMap<String,String> innerMap = new HashMap<>(map.get("Monthly"));

                                        for(Map.Entry<String,String> data : innerMap.entrySet()){
                                            keyList.add(data.getKey());
                                        }

                                        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
                                        recyclerView.setAdapter(new ReportsClassRecycler(keyList,"Monthly"));

                                    }else
                                    {
                                        recyclerView.setVisibility(View.INVISIBLE);
                                        textView.setVisibility(View.VISIBLE);
                                    }
                                    break;
                                case R.id.DayradioButton2:
                                    keyList.clear();
                                    if(getReports.contains("daily")){
                                        textView.setVisibility(View.INVISIBLE);
                                        recyclerView.setVisibility(View.VISIBLE);
                                        HashMap<String,HashMap<String,String>> map = gson.fromJson(getReports.getString("daily",""),type);
//                                        Toast.makeText(requireContext(), "Hello", Toast.LENGTH_SHORT).show();
                                        HashMap<String,String> innerMap = new HashMap<>(map.get("Daily"));
                                        int count = 0;
                                        for(Map.Entry<String,String> data : innerMap.entrySet()){
                                            count++;

                                            if(count > 12)
                                                break;
                                            keyList.add(data.getKey());

                                        }

                                        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
                                        recyclerView.setAdapter(new ReportsClassRecycler(keyList,"Daily"));

                                    }else
                                    {
                                        recyclerView.setVisibility(View.INVISIBLE);
                                        textView.setVisibility(View.VISIBLE);
                                    }
                                    break;
                            }


                        });


//                        FirebaseStorage storage;
//                        StorageReference storageReference;
//                        storage = FirebaseStorage.getInstance();
//                        storageReference = storage.getReference();


                        if(getReports.contains("week")){
                            HashMap<String,HashMap<String,String>> map = gson.fromJson(getReports.getString("week",""),type);

                            HashMap<String,String> innerMap = new HashMap<>(map.get("Weekly"));

                            int count = 0;
                            for(Map.Entry<String,String> data : innerMap.entrySet()){
                                count++;

                                if(count > 12)
                                    break;
                                keyList.add(data.getKey());


                            }

                            recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
                            recyclerView.setAdapter(new ReportsClassRecycler(keyList,"Weekly"));

                        }else
                        {
                            recyclerView.setVisibility(View.INVISIBLE);
                            textView.setVisibility(View.VISIBLE);
                        }

                        alertDialog.setView(view);
                        alertDialog.create().show();

                    }).create();
            builder.show();

            return true;
        });

        viewExcel.setOnPreferenceClickListener(preference -> {
            if(available){
                Toast.makeText(requireContext(), "Opening....", Toast.LENGTH_SHORT).show();
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    File file = new File(requireContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS) + "/RestaurantEarningTracker.xlsx");
                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    intent.setDataAndType(FileProvider.getUriForFile(requireContext(), requireContext().getPackageName() + ".provider", file), "application/vnd.ms-excel");
//            intent.setDataAndType(Uri.parse(file.getPath().toString()), "application/vnd.ms-excel");
                    startActivity(intent);
                }catch (Exception e){
                    Toast.makeText(requireContext(), "" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            }else{
                new KAlertDialog(requireContext(),KAlertDialog.NORMAL_TYPE)
                        .setTitleText("Subscribe").setContentText("Subscribe Premium to open excel sheet")
                        .setConfirmText("Subscribe").setCancelText("Exit")
                        .setConfirmClickListener(click -> {
                            startActivity(new Intent(requireContext(),FastwayPremiums.class));
                            click.dismissWithAnimation();
                        }).setCancelClickListener(KAlertDialog::dismissWithAnimation).show();
            }
            return  true;
        });

        comFee.setOnPreferenceClickListener(preference -> {
            startActivity(new Intent(requireContext(), CashTransactionCommissionActivity.class));
            return true;
        });

        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("loginInfo",MODE_PRIVATE);
        SharedPreferences.Editor sharedEdit = sharedPreferences.edit();
        takeaway.setChecked(sharedPreferences.getString("TakeAwayAllowed", "").equals("yes"));
        tableOrder.setChecked(sharedPreferences.getString("TableBookAllowed", "").equals("yes"));
        terms.setOnPreferenceClickListener(preference -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setTitle("Choose one option").setMessage("Open Privacy Policy or Terms And Conditions Or Cancellation and Refund Policy\n Click Anywhere outside to close dialog")
                    .setPositiveButton("Open Terms & Condition", (dialogInterface, i) -> {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://merchant.razorpay.com/policy/Kq6A4G44q0wBXo/terms"));
                        startActivity(browserIntent);
                    }).setNeutralButton("Open Privacy Policy", (dialogInterface, i) -> {
                        Intent privacyIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://merchant.razorpay.com/policy/Kq6A4G44q0wBXo/privacy"));
                        startActivity(privacyIntent);
                    }).setNegativeButton("Cancellation And Refund", (dialogInterface, i) -> {
                        Intent privacyIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://merchant.razorpay.com/policy/Kq6A4G44q0wBXo/refund"));
                        startActivity(privacyIntent);

                    }).create();
            builder.show();

            return true;
        });
//        Preference privacy = findPreference("privacyPolicy");

//        privacy.setOnPreferenceClickListener(preference -> {

//            return true;
//        });
        premium.setOnPreferenceClickListener(preference -> {
            startActivity(new Intent(requireContext(), FastwayPremiums.class));
            return true;
        });

        logout.setOnPreferenceClickListener(preference -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setTitle("Logout")
                    .setMessage("Do you wanna logout?")
                    .setPositiveButton("Yes", (dialogInterface, i1) -> {
                        SharedPreferences settings = requireContext().getSharedPreferences("loginInfo", MODE_PRIVATE);
//                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(settings.getString("state","")).child(settings.getString("locality","")).child(UID);
//                        databaseReference.child("status").setValue("offline");
//                        databaseReference.child("acceptingOrders").setValue("no");
                        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                        firestore.collection(settings.getString("state","")).document("Restaurants").collection(settings.getString("locality","")).document(UID)
                                        .update("status","offline","acceptingOrders","no");
                        FirebaseMessaging.getInstance().unsubscribeFromTopic(Objects.requireNonNull(UID));
                        requireContext().stopService(new Intent(requireContext(), MyService.class));
                        SharedPreferences stopServices = requireActivity().getSharedPreferences("Stop Services", MODE_PRIVATE);
                        SharedPreferences.Editor editor = stopServices.edit();
                        editor.putString("online","false");
                        editor.apply();
                        auth.signOut();
                        googleSignInClient.signOut().addOnCompleteListener((Activity) requireContext(), task -> {
                        });

                        settings.edit().clear().apply();

                        SharedPreferences res = requireContext().getSharedPreferences("RestaurantInfo", MODE_PRIVATE);
                        res.edit().clear().apply();

                        SharedPreferences intro = requireContext().getSharedPreferences("IntroAct", MODE_PRIVATE);
                        intro.edit().clear().apply();

                        SharedPreferences current = requireContext().getSharedPreferences("locations current", MODE_PRIVATE);
                        current.edit().clear().apply();

                        SharedPreferences AdminPremiumDetails = requireContext().getSharedPreferences("AdminPremiumDetails", MODE_PRIVATE);
                        AdminPremiumDetails.edit().clear().apply();

                        SharedPreferences CalenderForExcel = requireContext().getSharedPreferences("CalenderForExcel", MODE_PRIVATE);
                        CalenderForExcel.edit().clear().apply();

                        SharedPreferences resDailyStore = requireContext().getSharedPreferences("RestaurantDailyStoreForAnalysis", MODE_PRIVATE);
                        resDailyStore.edit().clear().apply();


                        SharedPreferences last7days = requireContext().getSharedPreferences("last7daysReport", MODE_PRIVATE);
                        last7days.edit().clear().apply();

                        SharedPreferences storeOrders = requireContext().getSharedPreferences("StoreOrders", MODE_PRIVATE);
                        storeOrders.edit().clear().apply();

                        SharedPreferences location = requireContext().getSharedPreferences("LocationMaps", MODE_PRIVATE);
                        location.edit().clear().apply();

                        SharedPreferences storeImages = requireContext().getSharedPreferences("storeImages", MODE_PRIVATE);
                        storeImages.edit().clear().apply();

                        SharedPreferences StoreDataForPayInEnd = requireContext().getSharedPreferences("StoreDataForPayInEnd", MODE_PRIVATE);
                        StoreDataForPayInEnd.edit().clear().apply();

                        SharedPreferences cashCommission = requireContext().getSharedPreferences("CashCommission", MODE_PRIVATE);
                        cashCommission.edit().clear().apply();

                        SharedPreferences DailyUserTrackingFor7days = requireContext().getSharedPreferences("DailyUserTrackingFor7days", MODE_PRIVATE);
                        DailyUserTrackingFor7days.edit().clear().apply();

                        SharedPreferences DailyAverageOrderMonthly = requireContext().getSharedPreferences("DailyAverageOrderMonthly", MODE_PRIVATE);
                        DailyAverageOrderMonthly.edit().clear().apply();

                        SharedPreferences RestaurantTrackingDaily = requireContext().getSharedPreferences("RestaurantTrackingDaily", MODE_PRIVATE);
                        RestaurantTrackingDaily.edit().clear().apply();

                        SharedPreferences RestaurantTrackRecords = requireContext().getSharedPreferences("RestaurantTrackRecords", MODE_PRIVATE);
                        RestaurantTrackRecords.edit().clear().apply();

                        SharedPreferences UserFrequencyPerMonth = requireContext().getSharedPreferences("UserFrequencyPerMonth", MODE_PRIVATE);
                        UserFrequencyPerMonth.edit().clear().apply();

                        SharedPreferences DishOrderedWithOthers = requireContext().getSharedPreferences("DishOrderedWithOthers", MODE_PRIVATE);
                        DishOrderedWithOthers.edit().clear().apply();

                        SharedPreferences DishAnalysis = requireContext().getSharedPreferences("DishAnalysis", MODE_PRIVATE);
                        DishAnalysis.edit().clear().apply();
                        SharedPreferences trackTake = requireContext().getSharedPreferences("TrackingOfTakeAway", MODE_PRIVATE);
                        trackTake.edit().clear().apply();
                        SharedPreferences trackFood = requireContext().getSharedPreferences("TrackingOfFoodDining", MODE_PRIVATE);
                        trackFood.edit().clear().apply();

                        startActivity(new Intent(getActivity(), SplashScreen.class));
                        requireActivity().finish();
                    })
                    .setNegativeButton("No", (dialogInterface, i15) -> dialogInterface.dismiss()).create();

            builder.show();
            return true;
        });

        myTrans.setOnPreferenceClickListener(preference -> {
            startActivity(new Intent(getActivity(), CashTransactions.class));
//            AlertDialog.Builder alert = new AlertDialog.Builder(requireContext());
//            alert.setTitle("Choose");
//            alert.setMessage("Choose one option from below");
//            alert.setPositiveButton("Show Online Transactions", (dialogInterface, i12) -> {
//                dialogInterface.dismiss();
//                startActivity(new Intent(getActivity(), MyOrdersTransactions.class));
//            }).setNegativeButton("Show Cash Transactions", (dialogInterface, i13) -> {
//                dialogInterface.dismiss();
//                startActivity(new Intent(getActivity(), CashTransactions.class));
//            }).setNeutralButton("Cancel", (dialogInterface, i14) -> dialogInterface.dismiss()).create();
//
//            alert.show();
            return true;
        });

//        blockedUsers.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
//            @Override
//            public boolean onPreferenceClick(Preference preference) {
//                startActivity(new Intent(requireContext(), BlockedUsers.class));
//                return true;
//            }
//        });
        account.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                startActivity(new Intent(requireContext(), MyAccount.class));
                return true;
            }
        });

        takeaway.setOnPreferenceChangeListener((preference, newValue) -> {
            SharedPreferences login = requireContext().getSharedPreferences("loginInfo",MODE_PRIVATE);
            Toast.makeText(requireContext(), "" + newValue, Toast.LENGTH_SHORT).show();

            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(login.getString("state","")).child(login.getString("locality","")).child(UID);
            boolean isChecked = (boolean) newValue;
            if(isChecked){
                firestore.collection(login.getString("state","")).document("Restaurants").collection(login.getString("locality","")).document(UID)
                        .update("TakeAwayAllowed","yes");
                databaseReference.child("TakeAwayAllowed").setValue("yes");
//                        takeaway.setChecked(true);
                sharedEdit.putString("TakeAwayAllowed","yes");
            }else{
                firestore.collection(login.getString("state","")).document("Restaurants").collection(login.getString("locality","")).document(UID)
                        .update("TakeAwayAllowed","no");
                databaseReference.child("TakeAwayAllowed").setValue("no");
//                        takeaway.setChecked(false);
                sharedEdit.putString("TakeAwayAllowed","no");

            }

            sharedEdit.apply();
            return true;
        });

        tableOrder.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
//                Toast.makeText(requireContext(), "" + UID, Toast.LENGTH_SHORT).show();
                    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                    SharedPreferences login = requireContext().getSharedPreferences("loginInfo",MODE_PRIVATE);
//                    Toast.makeText(requireContext(), "" + newValue, Toast.LENGTH_SHORT).show();
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(login.getString("state","")).child(login.getString("locality","")).child(UID);
                    boolean isChecked = (boolean) newValue;
                    if(isChecked){
                        firestore.collection(login.getString("state","")).document("Restaurants").collection(login.getString("locality","")).document(UID)
                                .update("TableBookAllowed","yes");
                        databaseReference.child("TableBookAllowed").setValue("yes");
//                        takeaway.setChecked(true);
                        sharedEdit.putString("TableBookAllowed","yes");
                    }else{
                        firestore.collection(login.getString("state","")).document("Restaurants").collection(login.getString("locality","")).document(UID)
                                .update("TableBookAllowed","no");
                        databaseReference.child("TableBookAllowed").setValue("no");
//                        takeaway.setChecked(false);
                        sharedEdit.putString("TableBookAllowed","no");

                    }

                    sharedEdit.apply();
//                    SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
//                    boolean isChecked = pref.getBoolean("tableBookingAllowedSwitch",false);
//
//                    if(isChecked){
//
//                    }
//                    if(preference instanceof SwitchPreferenceCompat && newValue.equals("false")){
//                        databaseReference.child("TableBookAllowed").setValue("no");
////                        takeaway.setChecked(false);
//                        sharedEdit.putString("TableBookAllowed","no");
//                    }else{
//                        databaseReference.child("TableBookAllowed").setValue("yes");
////                        takeaway.setChecked(true);
//                        sharedEdit.putString("TableBookAllowed","yes");
//                    }
                    sharedEdit.apply();

                return true;
            }
        });
    }
}