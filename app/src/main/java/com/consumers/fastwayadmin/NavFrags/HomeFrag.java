package com.consumers.fastwayadmin.NavFrags;

import static com.google.android.play.core.install.model.AppUpdateType.IMMEDIATE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.consumers.fastwayadmin.CreateShowCampaign.CampaignActivity;
import com.consumers.fastwayadmin.DiscountCombo.ComboAndOffers;
import com.consumers.fastwayadmin.DiscountCombo.DiscountActivity;
import com.consumers.fastwayadmin.NavFrags.CurrentTakeAwayOrders.CurrentTakeAway;
import com.consumers.fastwayadmin.NavFrags.Events.OrganiseEvents;
import com.consumers.fastwayadmin.NavFrags.PromotionAdsPack.CreateViewPromotions;
import com.consumers.fastwayadmin.NavFrags.ResEarningTracker.ResEarningTrackerActivity;
import com.consumers.fastwayadmin.NavFrags.homeFrag.homeFragClass;
import com.consumers.fastwayadmin.R;
import com.elconfidencial.bubbleshowcase.BubbleShowCase;
import com.elconfidencial.bubbleshowcase.BubbleShowCaseBuilder;
import com.elconfidencial.bubbleshowcase.BubbleShowCaseListener;
import com.elconfidencial.bubbleshowcase.BubbleShowCaseSequence;
import com.example.flatdialoglibrary.dialog.FlatDialog;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.Task;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.common.reflect.TypeToken;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import karpuzoglu.enes.com.fastdialog.Animations;
import karpuzoglu.enes.com.fastdialog.FastDialog;
import karpuzoglu.enes.com.fastdialog.FastDialogBuilder;
import karpuzoglu.enes.com.fastdialog.Type;

public class HomeFrag extends Fragment {
    String currentTime;
    Calendar calendar;
    int currentOrderCount = 0;
    int currentDay;
    TextView currentTablesText;
    Button seeMoreDetails;
    DecimalFormat decimalFormat = new DecimalFormat("0.00");
    List<String> image = new ArrayList<>();
    List<String> type = new ArrayList<>();
    List<String> orderAndPayment = new ArrayList<>();
    List<String> deliveryInformation = new ArrayList<>();
    List<String> price = new ArrayList<>();
    SharedPreferences.Editor resInfoSharedEdit;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch acceptOrders;
    BubbleShowCaseBuilder bubbleShowCaseBuilder1;
    BubbleShowCaseBuilder bubbleShowCaseBuilder2;
    BubbleShowCaseBuilder bubbleShowCaseBuilder3;
    BubbleShowCaseBuilder bubbleShowCaseBuilder4;
    BubbleShowCaseBuilder bubbleShowCaseBuilder5;
    TextView totalOrdersToday,totalTransactionsToday;
    List<String> customisationList = new ArrayList<>();
    boolean check = false;
    List<String> time = new ArrayList<>();
    List<List<String>> finalDishNames = new ArrayList<>();
    List<List<String>> finalImages = new ArrayList<>();
    List<List<String>> finalTypes = new ArrayList<>();
    List<List<String>> finalDishPrices = new ArrayList<>();
    List<List<String>> finalOrderAndPayments = new ArrayList<>();
    List<List<String>> finalDishQuantity = new ArrayList<>();
    List<List<String>> finalHalfOr = new ArrayList<>();
    List<String> finalPayment = new ArrayList<>();
    List<String> orderIDs = new ArrayList<>();
    List<String> orderAmounts = new ArrayList<>();
    SharedPreferences resInfoShared;
    Gson gson;
    String usernameOfTakeAway;
    String orderId,orderAmount;
    List<String> finalUserNames = new ArrayList<>();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    RecyclerView recyclerView,homeFragTakeAwayRecucler;
    List<String> currentTakeAwayAuth = new ArrayList<>();
    List<String> dishNameCurrentTakeAway = new ArrayList<>();
    List<String> dishQuantityCurrentTakeAway = new ArrayList<>();
    List<String> userNameTakeAway = new ArrayList<>();
    String UID;
    String paymentMode;
    LinearLayout linearLayout,secondLinearLayout;
    List<String> halfOr = new ArrayList<>();

    DatabaseReference onlineOrOfflineRestaurant;
    SharedPreferences accountInfo;
    SharedPreferences restaurantStatus;
    private final int UPDATE_REQUEST_CODE = 69;
    SharedPreferences vendorIdCreated;
    SharedPreferences.Editor vendorIdEditor;
    SharedPreferences.Editor editor;
    LocationRequest locationRequest;
    LinearLayoutManager horizonatl,anotherHori;
    ImageView comboImage;
    SharedPreferences.Editor statusEditor;
    List<List<String>> currentOrdersIfAvailable = new ArrayList<>();
    Toolbar toolbar;
    Button refershRecyclerView,refreshTakeAway;
    List<String> isCurrentOrder = new ArrayList<>();
    SharedPreferences sharedPreferences;
    Switch onlineOrOffline;
    DatabaseReference reference;
    SharedPreferences restaurantDailyTrack;
    SharedPreferences.Editor restaurantTrackEditor;
    SharedPreferences restaurantTrackRecords;
    SharedPreferences.Editor restaurantTrackRecordsEditor;
    FusedLocationProviderClient client;
    List<String> resId = new ArrayList<>();
    List<String> tableNum = new ArrayList<>();
    List<String> amountPaymentPending = new ArrayList<>();
    List<String> seats = new ArrayList<>();
    int count = 0;
    boolean pressed = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.home_frag,container,false);
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                count++;
                pressed = true;
                Toast.makeText(getContext(), "Press again to exit", Toast.LENGTH_SHORT).show();

                if(count == 2 && pressed)
                    requireActivity().finish();

                new Handler().postDelayed(() -> {
                    pressed = false;
                    count = 0;
                },2000);
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this,callback);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        toolbar = view.findViewById(R.id.homeFragToolBar);
        calendar = Calendar.getInstance();
        currentDay = calendar.get(Calendar.DAY_OF_MONTH);
//        Toast.makeText(view.getContext(), "" + currentDay, Toast.LENGTH_SHORT).show();
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
        inAppUpdateInfo();
        UID = auth.getUid() + "";
        totalOrdersToday = view.findViewById(R.id.valueOfTotalOrdersMadeHome);
        totalTransactionsToday = view.findViewById(R.id.valueOfTotalTransactionsTodayHome);
        restaurantDailyTrack = view.getContext().getSharedPreferences("RestaurantTrackingDaily",Context.MODE_PRIVATE);
        restaurantTrackRecords = view.getContext().getSharedPreferences("RestaurantTrackRecords",Context.MODE_PRIVATE);
        restaurantTrackRecordsEditor = restaurantTrackRecords.edit();
        restaurantTrackEditor = restaurantDailyTrack.edit();
        try {
            dailyRestaurantTrackUpdate();
        } catch (IOException e) {
            e.printStackTrace();
        }
//         SharedPreferences clearJust = requireContext().getSharedPreferences("DishOrderedWithOthers",Context.MODE_PRIVATE);
//         SharedPreferences.Editor editorClear = clearJust.edit();
//         editorClear.clear().apply();
         currentTablesText = view.findViewById(R.id.currentTablesTextViewHomeFragAdmin);
        SharedPreferences stopServices = requireActivity().getSharedPreferences("Stop Services",Context.MODE_PRIVATE);
         editor = stopServices.edit();
         acceptOrders = view.findViewById(R.id.acceptingOrdersSwitchHomeFrag);
        resInfoShared = view.getContext().getSharedPreferences("loginInfo",Context.MODE_PRIVATE);
        resInfoSharedEdit = resInfoShared.edit();
        restaurantStatus = view.getContext().getSharedPreferences("RestaurantStatus",Context.MODE_PRIVATE);
        statusEditor = restaurantStatus.edit();
        onlineOrOffline = view.findViewById(R.id.restaurantOnOff);
        linearLayout = view.findViewById(R.id.mainFragLinearLayout);
        homeFragTakeAwayRecucler = view.findViewById(R.id.homeFragTakeAwayRecyclerView);
        secondLinearLayout = view.findViewById(R.id.secondFragLinearLayout);
        vendorIdCreated = view.getContext().getSharedPreferences("VendorID",Context.MODE_PRIVATE);
        vendorIdEditor = vendorIdCreated.edit();

        seeMoreDetails = view.findViewById(R.id.seeMoreDetailsHomeFragButton);
        seeMoreDetails.setOnClickListener(click -> {
            view.getContext().startActivity(new Intent(requireContext(), ResEarningTrackerActivity.class));
        });

        SharedPreferences resInfo = requireContext().getSharedPreferences("RestaurantInfo",Context.MODE_PRIVATE);

        AsyncTask.execute(() -> {
            DatabaseReference addToRTDB = FirebaseDatabase.getInstance().getReference().getRoot().child("Offers").child(resInfoShared.getString("state","")).child(resInfoShared.getString("locality",""));
            if(!resInfoShared.contains("addedToRTDB")){
                GeoFire geoFire = new GeoFire(addToRTDB);
                geoFire.setLocation(auth.getUid(),new GeoLocation(Double.parseDouble(resInfoShared.getString("lati","")),Double.parseDouble(resInfoShared.getString("longi",""))));
                HashMap<String,String> map = new HashMap<>();
                map.put("resName",resInfo.getString("hotelName",""));
                map.put("resAddress",resInfo.getString("hotelAddress",""));
                map.put("resContact",resInfo.getString("hotelNumber",""));
                DatabaseReference setToRTDB = FirebaseDatabase.getInstance().getReference().getRoot().child("Offers").child(resInfoShared.getString("state","")).child(resInfoShared.getString("locality","")).child(Objects.requireNonNull(auth.getUid()));
                setToRTDB.child("ResInfo").setValue(map);
                resInfoSharedEdit.putString("addedToRTDB","yes");
                resInfoSharedEdit.apply();
            }
        });

        SharedPreferences dish = requireContext().getSharedPreferences("DishAnalysis",Context.MODE_PRIVATE);
//        if(dish.contains("DishAnalysisMonthBasis")){
//            gson = new Gson();
//            java.lang.reflect.Type type = new TypeToken<HashMap<String, HashMap<String,String>>>(){}.getType();
//            String storedHash = dish.getString("DishAnalysisMonthBasis","");
//            HashMap<String,HashMap<String,String>> myMap = gson.fromJson(storedHash,type);
//            Log.i("info",myMap.toString());
//            Toast.makeText(requireContext(), "" + myMap.toString(), Toast.LENGTH_SHORT).show();
//        }
        recyclerView = view.findViewById(R.id.homeFragRecyclerView);
        refershRecyclerView = view.findViewById(R.id.refreshCurrentTables);
        refreshTakeAway = view.findViewById(R.id.refreshCurrentTakeAway);
//        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext(),LinearLayoutManager.HORIZONTAL),true);
        comboImage = view.findViewById(R.id.comboDiscountImageView);
        auth = FirebaseAuth.getInstance();
        onlineOrOfflineRestaurant = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(resInfoShared.getString("state","")).child(resInfoShared.getString("locality","")).child(Objects.requireNonNull(UID));
        horizonatl = new LinearLayoutManager(view.getContext(),LinearLayoutManager.HORIZONTAL,false);
        anotherHori = new LinearLayoutManager(view.getContext(),LinearLayoutManager.HORIZONTAL,false);;
        sharedPreferences = requireActivity().getSharedPreferences("locations current", Context.MODE_PRIVATE);
        reference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(resInfoShared.getString("state","")).child(resInfoShared.getString("locality","")).child(Objects.requireNonNull(UID));
        FirebaseMessaging.getInstance().subscribeToTopic(Objects.requireNonNull(UID));

        if(!resInfoShared.contains("homeFragShow"))
            initialise();
        if(restaurantStatus.contains("status")){
            if(restaurantStatus.getString("status","").equals("offline")){
                comboImage.setVisibility(View.INVISIBLE);
                linearLayout.setVisibility(View.INVISIBLE);
                secondLinearLayout.setVisibility(View.INVISIBLE);
                onlineOrOffline.setChecked(false);
                onlineOrOffline.setText("offline");
                acceptOrders.setChecked(false);
                onlineOrOfflineRestaurant.child("status").setValue("offline");
                onlineOrOfflineRestaurant.child("acceptingOrders").setValue("no");
            }else{
                comboImage.setVisibility(View.VISIBLE);
                linearLayout.setVisibility(View.VISIBLE);
                secondLinearLayout.setVisibility(View.VISIBLE);
                onlineOrOffline.setChecked(true);
                onlineOrOffline.setText("online");
                acceptOrders.setChecked(true);
                onlineOrOfflineRestaurant.child("status").setValue("online");
                onlineOrOfflineRestaurant.child("acceptingOrders").setValue("yes");
            }
        }else{
            onlineOrOfflineRestaurant.child("status").setValue("online");
            onlineOrOfflineRestaurant.child("acceptingOrders").setValue("yes");
        }

        if(restaurantStatus.contains("resOrdersAccepting")){
            if(restaurantStatus.getString("resOrdersAccepting","").equals("no")){
                acceptOrders.setChecked(false);
            }else
                acceptOrders.setChecked(true);
        }
        if(ContextCompat.checkSelfPermission(requireActivity(),Manifest.permission.CAMERA) + ContextCompat.checkSelfPermission(requireActivity(),Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.CAMERA,Manifest.permission.ACCESS_COARSE_LOCATION},1);
        }else
//            createLocationRequest();
        new retriveTable().execute();
        comboImage.setOnClickListener(view1 -> {
//                startActivity(new Intent(getContext(),CreateDiscountCombo.class));
            final FlatDialog flatDialog = new FlatDialog(getContext());
            flatDialog.setCanceledOnTouchOutside(true);
            flatDialog.setTitle("Choose One Option")
                    .setTitleColor(Color.BLACK)
                    .setBackgroundColor(Color.parseColor("#f9fce1"))
                    .setFirstButtonColor(Color.parseColor("#d3f6f3"))
                    .setFirstButtonTextColor(Color.parseColor("#000000"))
                    .setFirstButtonText("CREATE/SHOW CAMPAIGN")
                    .setSecondButtonColor(Color.parseColor("#fee9b2"))
                    .setSecondButtonTextColor(Color.parseColor("#000000"))
                    .setSecondButtonText("PROMOTION ADS")
                    .setThirdButtonText("Events")
                    .setThirdButtonColor(Color.parseColor("#b3b3ff"))
                    .setThirdButtonTextColor(Color.parseColor("#000000"))
                    .withFirstButtonListner(view11 -> {
                        startActivity(new Intent(getContext(), CampaignActivity.class));
                        flatDialog.dismiss();
                    })
                    .withSecondButtonListner(view112 -> {
                        startActivity(new Intent(getContext(), CreateViewPromotions.class));
                        flatDialog.dismiss();
                    })
                    .withThirdButtonListner(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startActivity(new Intent(getContext(), OrganiseEvents.class));
                            flatDialog.dismiss();
                        }
                    })
                    .show();
        });

        new MyTask().execute();

        acceptOrders.setOnCheckedChangeListener((compoundButton, b) -> {
            if(b){
                acceptOrders.setChecked(true);
                statusEditor.putString("resOrdersAccepting","yes");
                statusEditor.apply();
                onlineOrOfflineRestaurant.child("acceptingOrders").setValue("yes");
                Toast.makeText(getContext(), "Restaurant is now accepting orders", Toast.LENGTH_SHORT).show();
                FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                firestore.collection(resInfoShared.getString("state","")).document("Restaurants").collection(resInfoShared.getString("locality",""))
                        .document(UID).update("acceptingOrders","yes");
            }else{
                onlineOrOfflineRestaurant.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.hasChild("Current TakeAway")){
                            AlertDialog.Builder alert = new AlertDialog.Builder(requireContext());
                            alert.setTitle("Error").setMessage("You still have an current takeaway order in queue. Contact  user before closing restaurant");
                            alert.setPositiveButton("Exit", (dialogInterface, i) -> {
                                onlineOrOffline.setChecked(true);
                                acceptOrders.setChecked(true);
                                dialogInterface.dismiss();
                            }).create();

                            alert.show();
                        }else
                            checkCurrentInfo();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });


        onlineOrOffline.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                statusEditor.putString("status","online");
                statusEditor.putString("resOrdersAccepting","yes");
                onlineOrOffline.setText("online");
                acceptOrders.setChecked(true);
                secondLinearLayout.setVisibility(View.VISIBLE);
                statusEditor.apply();
                comboImage.setVisibility(View.VISIBLE);
                linearLayout.setVisibility(View.VISIBLE);
                onlineOrOfflineRestaurant.child("status").setValue("online");
                FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                firestore.collection(resInfoShared.getString("state","")).document("Restaurants").collection(resInfoShared.getString("locality",""))
                                .document(UID).update("status","online","acceptingOrders","yes");
                onlineOrOfflineRestaurant.child("acceptingOrders").setValue("yes");
                editor.putString("online","true");
            }else{

                onlineOrOfflineRestaurant.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.hasChild("Current TakeAway")){
                            AlertDialog.Builder alert = new AlertDialog.Builder(requireContext());
                            alert.setTitle("Error").setMessage("You still have an current takeaway order in queue. Contact  user before closing restaurant");
                            alert.setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    onlineOrOffline.setChecked(true);
                                    acceptOrders.setChecked(true);
                                    dialogInterface.dismiss();
                                }
                            }).create();

                            alert.show();
                        }else
                            checkInfo();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });



            }
            editor.apply();

        });

        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                new TakeAwayClass().execute();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                new TakeAwayClass().execute();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                new TakeAwayClass().execute();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        reference.child("Tables").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                updateDatabase();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//                Toast.makeText(view.getContext(), ""+snapshot.child("status").getValue(), Toast.LENGTH_SHORT).show();
                updateDatabase();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                updateDatabase();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                updateDatabase();
            }
        });



        refreshTakeAway.setOnClickListener(click -> {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(resInfoShared.getString("state","")).child(resInfoShared.getString("locality","")).child(Objects.requireNonNull(UID)).child("Current TakeAway");
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        currentTakeAwayAuth.clear();
                        dishNameCurrentTakeAway.clear();
                        userNameTakeAway.clear();
                        halfOr.clear();
                        dishQuantityCurrentTakeAway.clear();
                        finalDishQuantity.clear();
                        image.clear();
                        type.clear();
                        finalImages.clear();
                        finalTypes.clear();
                        customisationList.clear();
                        finalPayment.clear();
                        orderAndPayment.clear();
                        time.clear();
                        finalUserNames.clear();
                        finalHalfOr.clear();
                        finalDishNames.clear();
                        String cus = "";
                        for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                            currentTakeAwayAuth.add(String.valueOf(dataSnapshot.getKey()));
                            for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                                dishNameCurrentTakeAway.add(String.valueOf(dataSnapshot1.child("name").getValue()));
                                halfOr.add(String.valueOf(dataSnapshot1.child("halfOr").getValue()));
                                orderAmount = String.valueOf(dataSnapshot1.child("orderAmount").getValue());
                                orderId = String.valueOf(dataSnapshot1.child("orderID").getValue());
                                paymentMode = String.valueOf(dataSnapshot1.child("paymmentMode").getValue());
                                dishQuantityCurrentTakeAway.add(String.valueOf(dataSnapshot1.child("timesOrdered").getValue()));
                                orderAndPayment.add(String.valueOf(dataSnapshot1.child("orderAndPayment").getValue()));
                                image.add(String.valueOf(dataSnapshot1.child("image").getValue()));
                                type.add(String.valueOf(dataSnapshot1.child("type").getValue()));
                                userNameTakeAway.add(String.valueOf(dataSnapshot1.child("nameOfUser").getValue()));
                                usernameOfTakeAway = String.valueOf(dataSnapshot1.child("nameOfUser").getValue());
                                currentTime = String.valueOf(dataSnapshot1.child("time").getValue());
                                if(dataSnapshot1.hasChild("deliveryInformation"))
                                    deliveryInformation.add(dataSnapshot1.child("deliveryInformation").getValue(String.class));
                                else
                                    deliveryInformation.add("");
                                cus = String.valueOf(dataSnapshot.child("customisation").getValue());
                            }
                            time.add(currentTime);
                            customisationList.add(cus);
                            finalDishNames.add(new ArrayList<>(dishNameCurrentTakeAway));
                            finalDishQuantity.add(new ArrayList<>(dishQuantityCurrentTakeAway));
                            finalHalfOr.add(new ArrayList<>(halfOr));
                            finalPayment.add(paymentMode);
                            finalUserNames.add(usernameOfTakeAway);
                            dishNameCurrentTakeAway.clear();
                            finalDishPrices.add(new ArrayList<>(price));
                            price.clear();
                            dishQuantityCurrentTakeAway.clear();
                            finalTypes.add(new ArrayList<>(type));
                            finalImages.add(new ArrayList<>(image));
                            type.clear();
                            image.clear();
                            finalOrderAndPayments.add(new ArrayList<>(orderAndPayment));
                            orderAndPayment.clear();
                            orderIDs.add(orderId);
                            orderAmounts.add(orderAmount);
                            halfOr.clear();
                        }
                        Log.i("time",time.toString());
//                        Log.i("Current",currentTakeAwayAuth.toString() + " " + dishNameCurrentTakeAway.toString() + " " + userNameTakeAway.toString());
                        homeFragTakeAwayRecucler.setLayoutManager(anotherHori);
                        Log.i("message",finalDishNames.toString() + "\n" + finalPayment.toString() + "\n" + finalDishQuantity.toString());
//                        homeFragTakeAwayRecucler.setAdapter(new CurrentTakeAway(currentTakeAwayAuth,dishNameCurrentTakeAway,dishQuantityCurrentTakeAway,userNameTakeAway,halfOr,paymentMode));
                        homeFragTakeAwayRecucler.setAdapter(new CurrentTakeAway(finalDishNames,finalDishQuantity,finalHalfOr,finalUserNames,finalPayment,orderIDs,orderAmounts,currentTakeAwayAuth,time,customisationList,finalOrderAndPayments,finalDishPrices,finalImages,finalTypes,deliveryInformation));
                    }else
                    {
                        currentTakeAwayAuth.clear();
                        finalDishNames.clear();
                        dishNameCurrentTakeAway.clear();
                        userNameTakeAway.clear();
                        deliveryInformation.clear();
                        halfOr.clear();
                        finalDishQuantity.clear();
                        finalPayment.clear();
                        finalUserNames.clear();
                        orderIDs.clear();
                        finalOrderAndPayments.add(new ArrayList<>(orderAndPayment));
                        orderAndPayment.clear();
                        orderAmounts.clear();
                        customisationList.clear();
                        time.clear();
                        finalHalfOr.clear();
                        dishQuantityCurrentTakeAway.clear();
                        homeFragTakeAwayRecucler.setLayoutManager(anotherHori);
                        homeFragTakeAwayRecucler.setAdapter(new CurrentTakeAway(finalDishNames,finalDishQuantity,finalHalfOr,finalUserNames,finalPayment,orderIDs,orderAmounts,currentTakeAwayAuth,time,customisationList,finalOrderAndPayments,finalDishPrices,finalImages,finalTypes,deliveryInformation));

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        });


        refershRecyclerView.setOnClickListener(view12 -> {
            tableNum.clear();
            seats.clear();

            reference.child("Tables").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        resId.clear();
                        seats.clear();
                        currentOrdersIfAvailable.clear();
                        amountPaymentPending.clear();
                        isCurrentOrder.clear();
                        tableNum.clear();
//                    Toast.makeText(view.getContext(), ""+snapshot.getChildrenCount(), Toast.LENGTH_SHORT).show();
                        for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                            if(Objects.equals(dataSnapshot.child("status").getValue(String.class), "unavailable")){

                                resId.add(dataSnapshot.child("customerId").getValue(String.class));
                                tableNum.add(dataSnapshot.child("tableNum").getValue(String.class));
                                seats.add(dataSnapshot.child("numSeats").getValue(String.class));
                                if(dataSnapshot.hasChild("amountToBePaid"))
                                    amountPaymentPending.add("1");
                                else
                                    amountPaymentPending.add("0");
                                if(dataSnapshot.hasChild("Current Order")){
                                    isCurrentOrder.add("1");
                                }else
                                    isCurrentOrder.add("0");
                            }
                        }
                        recyclerView.setLayoutManager(horizonatl);
//                    Toast.makeText(view.getContext(), ""+seats.toString(), Toast.LENGTH_SHORT).show();
                        recyclerView.setAdapter(new homeFragClass(tableNum,seats,resId,isCurrentOrder,amountPaymentPending));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        });

    }

    private void dailyRestaurantTrackUpdate() throws IOException {
        if(restaurantDailyTrack.contains("currentDate")){
            if(Integer.parseInt(restaurantDailyTrack.getString("currentDate","")) == currentDay){
                if(restaurantDailyTrack.contains("totalOrdersToday"))
                    totalOrdersToday.setText(restaurantDailyTrack.getString("totalOrdersToday",""));

                if(restaurantDailyTrack.contains("totalTransactionsToday"))
                    totalTransactionsToday.setText("\u20B9" + decimalFormat.format(Double.parseDouble(restaurantDailyTrack.getString("totalTransactionsToday",""))));
            }else{
                SharedPreferences trackingOfFiles = requireContext().getSharedPreferences("TrackingFilesForML",Context.MODE_PRIVATE);
                if(trackingOfFiles.contains("dailyStoringFile")){
                    Calendar calendar = Calendar.getInstance();
                    String[] monthName = {"January", "February",
                            "March", "April", "May", "June", "July",
                            "August", "September", "October", "November",
                            "December"};
                    String month = monthName[calendar.get(Calendar.MONTH)];
                    String[] daysName = {"Monday","Tuesday","Wednesday","Thursday","Friday"
                    ,"Saturday","Sunday"};
                    SharedPreferences restaurantDailyStoreTrack = requireContext().getSharedPreferences("RestaurantDailyStoreForAnalysis",Context.MODE_PRIVATE);
                    java.lang.reflect.Type type = new TypeToken<List<List<String>>>() {
                    }.getType();
                    Gson gson = new Gson();
                    if(restaurantDailyTrack.getString("totalOrdersToday","").equals("0"))
                        return;
                    if(restaurantDailyStoreTrack.contains(month)){
                        String json = restaurantDailyStoreTrack.getString(month, "");
                        List<List<String>> mainList = gson.fromJson(json, type);
                        List<String> days = new ArrayList<>(mainList.get(0));
                        List<String> totalAmounts = new ArrayList<>(mainList.get(1));
                        List<String> totalOrdersPlaced = new ArrayList<>(mainList.get(2));

                        File file = new File(requireContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS),"RestaurantDailyStoringData.csv");
                        try{
                            CSVWriter csvWriter = new CSVWriter(new FileWriter(file.getAbsoluteFile(),true));
                            String[] record = new String[5];
                            record[0] = month;
                            record[1] = days.get(days.size()-1);
                            record[2] = restaurantDailyTrack.getString("currentDay","");
                            record[3] = totalAmounts.get(totalAmounts.size()-1);
                            record[4] = totalOrdersPlaced.get(totalOrdersPlaced.size()-1);
                            csvWriter.writeNext(record,false);
                            csvWriter.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                restaurantTrackEditor.putString("currentDate", String.valueOf(currentDay));
                Calendar cal = Calendar.getInstance();
                cal.setTime(new Date());
                DateFormat formatter = new SimpleDateFormat("EEEE");
                String dayOfWeekString = formatter.format(cal.getTime());
                restaurantTrackEditor.putString("totalOrdersToday","0");
                restaurantTrackEditor.putString("currentDay",dayOfWeekString);
                restaurantTrackEditor.putString("totalOrdersToday","0");
                restaurantTrackEditor.putString("totalTransactionsToday","0");
                restaurantTrackEditor.apply();
            }
        }else{
            restaurantTrackEditor.putString("currentDate",String.valueOf(currentDay));
            restaurantTrackEditor.apply();
        }
    }

    private void initialise() {
        bubbleShowCaseBuilder5 = new BubbleShowCaseBuilder(requireActivity());
        bubbleShowCaseBuilder5.title("Welcome to Ordinalo").titleTextSize(20).listener(new BubbleShowCaseListener() {
            @Override
            public void onTargetClick(@NonNull BubbleShowCase bubbleShowCase) {

            }

            @Override
            public void onCloseActionImageClick(@NonNull BubbleShowCase bubbleShowCase) {

            }

            @Override
            public void onBackgroundDimClick(@NonNull BubbleShowCase bubbleShowCase) {
                bubbleShowCase.dismiss();
            }

            @Override
            public void onBubbleClick(@NonNull BubbleShowCase bubbleShowCase) {
                bubbleShowCase.dismiss();
            }
        });
        bubbleShowCaseBuilder1 = new BubbleShowCaseBuilder(requireActivity());
        bubbleShowCaseBuilder1.title("Accept Order & Online")
                .description("You choose weather restaurant is online or taking order's right now")
                .targetView(acceptOrders).listener(new BubbleShowCaseListener() {
                    @Override
                    public void onTargetClick(@NonNull BubbleShowCase bubbleShowCase) {

                    }

                    @Override
                    public void onCloseActionImageClick(@NonNull BubbleShowCase bubbleShowCase) {

                    }

                    @Override
                    public void onBackgroundDimClick(@NonNull BubbleShowCase bubbleShowCase) {
                        bubbleShowCase.dismiss();
                    }

                    @Override
                    public void onBubbleClick(@NonNull BubbleShowCase bubbleShowCase) {
                        bubbleShowCase.dismiss();
                    }
                });
        bubbleShowCaseBuilder2 = new BubbleShowCaseBuilder(requireActivity());
        bubbleShowCaseBuilder2.title("Create Combo And Offer's")
                .description("Here you can create different offer's and combo's")
                .targetView(comboImage).listener(new BubbleShowCaseListener() {
                    @Override
                    public void onTargetClick(@NonNull BubbleShowCase bubbleShowCase) {

                    }

                    @Override
                    public void onCloseActionImageClick(@NonNull BubbleShowCase bubbleShowCase) {

                    }

                    @Override
                    public void onBackgroundDimClick(@NonNull BubbleShowCase bubbleShowCase) {
                        bubbleShowCase.dismiss();
                    }

                    @Override
                    public void onBubbleClick(@NonNull BubbleShowCase bubbleShowCase) {
                        bubbleShowCase.dismiss();
                    }
                });
        BubbleShowCaseSequence bubbleShowCaseSequence = new BubbleShowCaseSequence();
        bubbleShowCaseSequence.addShowCase(bubbleShowCaseBuilder5).addShowCase(bubbleShowCaseBuilder1).addShowCase(bubbleShowCaseBuilder2);


        if(totalOrdersToday.isShown()) {
            bubbleShowCaseBuilder3 = new BubbleShowCaseBuilder(requireActivity());
            bubbleShowCaseBuilder3.title("Total Order's And Transactions")
                    .description("Here all of your current day transactions and order's made will be shown")
                    .targetView(totalOrdersToday).listener(new BubbleShowCaseListener() {
                        @Override
                        public void onTargetClick(@NonNull BubbleShowCase bubbleShowCase) {

                        }

                        @Override
                        public void onCloseActionImageClick(@NonNull BubbleShowCase bubbleShowCase) {

                        }

                        @Override
                        public void onBackgroundDimClick(BubbleShowCase bubbleShowCase) {
                            bubbleShowCase.dismiss();
                        }

                        @Override
                        public void onBubbleClick(@NonNull BubbleShowCase bubbleShowCase) {
                            bubbleShowCase.dismiss();
                        }
                    });
            bubbleShowCaseSequence.addShowCase(bubbleShowCaseBuilder3);
        }

        if(seeMoreDetails.isShown()) {
            bubbleShowCaseBuilder4 = new BubbleShowCaseBuilder(requireActivity());
            bubbleShowCaseBuilder4.title("Restaurant Analysis")
                    .description("Here you can see restaurant all time analysis of order's and dishes")
                    .targetView(seeMoreDetails).listener(new BubbleShowCaseListener() {
                        @Override
                        public void onTargetClick(BubbleShowCase bubbleShowCase) {

                        }

                        @Override
                        public void onCloseActionImageClick(BubbleShowCase bubbleShowCase) {

                        }

                        @Override
                        public void onBackgroundDimClick(BubbleShowCase bubbleShowCase) {
                            bubbleShowCase.dismiss();
                        }

                        @Override
                        public void onBubbleClick(BubbleShowCase bubbleShowCase) {
                            bubbleShowCase.dismiss();
                        }
                    });
            bubbleShowCaseSequence.addShowCase(bubbleShowCaseBuilder4);
        }


        bubbleShowCaseSequence.show();
        resInfoSharedEdit.putString("homeFragShow","yes");
        resInfoSharedEdit.apply();
    }

    private void checkCurrentInfo() {

        onlineOrOfflineRestaurant.child("Tables").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                        if(Objects.equals(dataSnapshot.child("status").getValue(String.class), "unavailable")){
                            check = true;
                            AlertDialog.Builder alert = new AlertDialog.Builder(requireContext());
                            alert.setTitle("Error").setMessage("You still have an active table. Contact user before not accepting orders");
                            alert.setPositiveButton("Exit", (dialogInterface, i) -> dialogInterface.dismiss()).create();
                            onlineOrOffline.setChecked(true);
                            acceptOrders.setChecked(true);
                            alert.show();
                            break;
                        }else if(Objects.equals(dataSnapshot.child("status").getValue(String.class), "Reserved")){
                            check = true;
                            AlertDialog.Builder alert = new AlertDialog.Builder(requireContext());
                            alert.setTitle("Error").setMessage("You still have an active Reserved Table. Contact user before not accepting orders");
                            alert.setPositiveButton("Exit", (dialogInterface, i) -> dialogInterface.dismiss()).create();
                            onlineOrOffline.setChecked(true);
                            acceptOrders.setChecked(true);
                            alert.show();
                            break;
                        }
                    }

                    if(!check){
                        acceptOrders.setChecked(false);
                        onlineOrOfflineRestaurant.child("acceptingOrders").setValue("no");
                        statusEditor.putString("resOrdersAccepting","no");
                        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                        firestore.collection(resInfoShared.getString("state","")).document("Restaurants").collection(resInfoShared.getString("locality",""))
                                .document(UID).update("acceptingOrders","no");
                        statusEditor.apply();
                        Toast.makeText(requireContext(), "Restaurant will not receive orders", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void checkInfo() {

        new Thread(() -> {
            onlineOrOfflineRestaurant.child("Tables").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                            if(Objects.equals(dataSnapshot.child("status").getValue(String.class), "unavailable")){
                                check = true;
                                AlertDialog.Builder alert = new AlertDialog.Builder(requireContext());
                                alert.setTitle("Error").setMessage("You still have an active table. Contact user before closing restaurant");
                                alert.setPositiveButton("Exit", (dialogInterface, i) -> dialogInterface.dismiss()).create();
                                onlineOrOffline.setChecked(true);
                                acceptOrders.setChecked(true);
                                alert.show();
                                break;
                            }else if(Objects.equals(dataSnapshot.child("status").getValue(String.class), "Reserved")){
                                check = true;
                                AlertDialog.Builder alert = new AlertDialog.Builder(requireContext());
                                alert.setTitle("Error").setMessage("You still have an active Reserved Table. Contact user before closing restaurant");
                                alert.setPositiveButton("Exit", (dialogInterface, i) -> dialogInterface.dismiss()).create();
                                onlineOrOffline.setChecked(true);
                                acceptOrders.setChecked(true);
                                alert.show();
                                break;
                            }
                        }

                        if(!check){
                            statusEditor.putString("status","offline");
                            statusEditor.putString("resOrdersAccepting","no");
                            onlineOrOffline.setText("offline");
                            acceptOrders.setChecked(false);
                            statusEditor.apply();
                            comboImage.setVisibility(View.INVISIBLE);
                            linearLayout.setVisibility(View.INVISIBLE);
                            secondLinearLayout.setVisibility(View.INVISIBLE);
                            onlineOrOfflineRestaurant.child("status").setValue("offline");
                            onlineOrOfflineRestaurant.child("acceptingOrders").setValue("no");
                            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                            firestore.collection(resInfoShared.getString("state","")).document("Restaurants").collection(resInfoShared.getString("locality",""))
                                    .document(UID).update("status","offline","acceptingOrders","no");
                            editor.putString("online","false");
                            FastDialog fastDialog = new FastDialogBuilder(requireContext(), Type.PROGRESS)
                                    .progressText("Closing restaurant... please wait")
                                    .setAnimation(Animations.SLIDE_BOTTOM)
                                    .cancelable(false)
                                    .create();

                            fastDialog.show();

                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(resInfoShared.getString("state","")).child(resInfoShared.getString("locality","")).child(Objects.requireNonNull(UID)).child("Tables");
                            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(snapshot.exists()){
                                        for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                                            if(String.valueOf(dataSnapshot.child("status").getValue()).equals("unavailable")){
                                                databaseReference.child(String.valueOf(dataSnapshot.getKey())).child("status").setValue("available");
                                                databaseReference.child(String.valueOf(dataSnapshot.getKey())).child("customerId").removeValue();
                                                databaseReference.child(String.valueOf(dataSnapshot.getKey())).child("Current Order").removeValue();
                                            }else if(String.valueOf(dataSnapshot.child("status").getValue()).equals("Reserved")){
                                                databaseReference.child(String.valueOf(dataSnapshot.getKey())).child("status").setValue("available");
                                                databaseReference.child(String.valueOf(dataSnapshot.getKey())).child("customerId").removeValue();
                                                databaseReference.child(String.valueOf(dataSnapshot.getKey())).child("time").removeValue();
                                                databaseReference.child(String.valueOf(dataSnapshot.getKey())).child("timeInMillis").removeValue();
                                            }
                                        }
                                        fastDialog.dismiss();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }).start();


    }

    private void inAppUpdateInfo() {
        AppUpdateManager appUpdateManager = AppUpdateManagerFactory.create(requireContext());

// Returns an intent object that you use to check for an update.
        com.google.android.play.core.tasks.Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();

// Checks that the platform will allow the specified type of update.
        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    // This example applies an immediate update. To apply a flexible update
                    // instead, pass in AppUpdateType.FLEXIBLE
                    && appUpdateInfo.isUpdateTypeAllowed(IMMEDIATE)) {
                // Request the update.

                try {
                    appUpdateManager.startUpdateFlowForResult(appUpdateInfo, IMMEDIATE, (Activity) requireContext(),UPDATE_REQUEST_CODE);
                } catch (IntentSender.SendIntentException e) {
                    Toast.makeText(getContext(), "" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateDatabase() {
        reference.child("Tables").addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    currentOrderCount = 0;
                    tableNum.clear();
                    seats.clear();
                    isCurrentOrder.clear();
                    amountPaymentPending.clear();
                    resId.clear();
//                    Toast.makeText(view.getContext(), "I am invoked", Toast.LENGTH_SHORT).show();
//                    Toast.makeText(view.getContext(), ""+snapshot.getChildrenCount(), Toast.LENGTH_SHORT).show();
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                        if(Objects.equals(dataSnapshot.child("status").getValue(String.class), "unavailable")){

                            resId.add(dataSnapshot.child("customerId").getValue(String.class));
                            tableNum.add(dataSnapshot.child("tableNum").getValue(String.class));
                            seats.add(dataSnapshot.child("numSeats").getValue(String.class));
                            if(dataSnapshot.hasChild("amountToBePaid"))
                                amountPaymentPending.add("1");
                            else
                                amountPaymentPending.add("0");
                            if(dataSnapshot.hasChild("Current Order")){
                                currentOrderCount++;
                                isCurrentOrder.add("1");
                            }else
                                isCurrentOrder.add("0");
                        }
                    }
                    if(currentOrderCount != 0)
                    currentTablesText.setText("CURRENT TABLES (" + currentOrderCount + ")");
                    else
                        currentTablesText.setText("CURRENT TABLES");

                    if(currentOrderCount > 2)
                    {
                        refershRecyclerView.setText("MORE");
                        refershRecyclerView.setOnClickListener(click -> {

                        });
                    }

                    recyclerView.setLayoutManager(horizonatl);
//                    Toast.makeText(view.getContext(), ""+seats.toString(), Toast.LENGTH_SHORT).show();
                    recyclerView.setAdapter(new homeFragClass(tableNum,seats,resId,isCurrentOrder,amountPaymentPending));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == 1){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(getActivity(), "Permission Granted", Toast.LENGTH_SHORT).show();
            }else if(grantResults[0] == PackageManager.PERMISSION_DENIED && grantResults[1] == PackageManager.PERMISSION_DENIED){
                AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
                builder.setCancelable(false);
                builder.setTitle("Important").setMessage("Camera is required for proper functioning of app. Wanna provide permission??")
                        .setPositiveButton("Yes", (dialogInterface, i) -> {
                            if(ContextCompat.checkSelfPermission(requireActivity(),Manifest.permission.CAMERA) + ContextCompat.checkSelfPermission(requireActivity(),Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                                requestPermissions(new String[]{Manifest.permission.CAMERA,Manifest.permission.ACCESS_COARSE_LOCATION},1);
                            }
                        }).setNegativeButton("No", (dialogInterface, i) -> dialogInterface.dismiss()).create();
                builder.show();
            }
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);
        if (requestCode == 101) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    // All required changes were successfully made
                    Toast.makeText(getActivity(), states.isLocationPresent() + "", Toast.LENGTH_SHORT).show();
                    break;
                case Activity.RESULT_CANCELED:
                    // The user was asked to change settings, but chose not to
                    Toast.makeText(getActivity(), "Canceled", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    }
    private final LocationCallback mLocationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
        }
    };
    
    private class MyTask extends AsyncTask<Void,Void,Void>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);

        }

        @Override
        protected Void doInBackground(Void... voids) {
            reference.child("Tables").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        tableNum.clear();
                        seats.clear();
                        resId.clear();
                        amountPaymentPending.clear();
                        isCurrentOrder.clear();
                        for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                            if(Objects.equals(dataSnapshot.child("status").getValue(String.class), "unavailable")){
                                resId.add(dataSnapshot.child("customerId").getValue(String.class));
                                tableNum.add(dataSnapshot.child("tableNum").getValue(String.class));
                                seats.add(dataSnapshot.child("numSeats").getValue(String.class));
                                if(dataSnapshot.hasChild("amountToBePaid"))
                                    amountPaymentPending.add("1");
                                else
                                    amountPaymentPending.add("0");

                                if(dataSnapshot.hasChild("Current Order")){
                                    isCurrentOrder.add("1");
                                }else
                                    isCurrentOrder.add("0");
                            }
                        }
                        recyclerView.setLayoutManager(horizonatl);
//                    Toast.makeText(view.getContext(), ""+seats.toString(), Toast.LENGTH_SHORT).show();
                        recyclerView.setAdapter(new homeFragClass(tableNum,seats,resId,isCurrentOrder,amountPaymentPending));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }
    
    private void createLocationRequest() {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(600000);
        locationRequest.setFastestInterval(600000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        client = LocationServices.getFusedLocationProviderClient(requireActivity());
        client.requestLocationUpdates(locationRequest,mLocationCallback, Looper.myLooper());
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);


        SettingsClient client = LocationServices.getSettingsClient(requireActivity());
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnCompleteListener(task1 -> {
            try {
                LocationSettingsResponse response = task1.getResult(ApiException.class);
                // All location settings are satisfied. The client can initialize location
                // requests here.

            } catch (ApiException exception) {
                switch (exception.getStatusCode()) {
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the
                        // user a dialog.
                        try {
                            // Cast to a resolvable exception.
                            ResolvableApiException resolvable = (ResolvableApiException) exception;
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            resolvable.startResolutionForResult(
                                    getActivity(),
                                    101);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        } catch (ClassCastException e) {
                            // Ignore, should be an impossible error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        break;
                }
            }
        });

    }

    private class retriveTable extends AsyncTask<Void,Void,Void>{

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);

        }

        @Override
        protected Void doInBackground(Void... voids) {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(resInfoShared.getString("state","")).child(resInfoShared.getString("locality","")).child(UID).child("Tables");
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                        if(dataSnapshot.hasChild("Current Order")){
                            Log.d("current order","yes");
                        }
                        else
                            Log.d("current order","no");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            return null;
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        AppUpdateManager appUpdateManager = AppUpdateManagerFactory.create(getContext());

// Returns an intent object that you use to check for an update.
        com.google.android.play.core.tasks.Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();
        appUpdateManager.getAppUpdateInfo().addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability()
                    == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                // If an in-app update is already running, resume the update.
                try {
                    appUpdateManager.startUpdateFlowForResult(
                            appUpdateInfo,
                            IMMEDIATE,
                            (Activity) getContext(),
                            UPDATE_REQUEST_CODE);
                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                }
            }
        });
    }



    public class TakeAwayClass extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... voids) {

            FirebaseAuth auth = FirebaseAuth.getInstance();
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(resInfoShared.getString("state","")).child(resInfoShared.getString("locality","")).child(Objects.requireNonNull(UID)).child("Current TakeAway");
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        String cus = "";
                        currentTakeAwayAuth.clear();
                        dishNameCurrentTakeAway.clear();
                        userNameTakeAway.clear();
                        halfOr.clear();
                        orderAndPayment.clear();
                        dishQuantityCurrentTakeAway.clear();
                        finalDishQuantity.clear();
                        image.clear();
                        type.clear();
                        finalImages.clear();
                        finalTypes.clear();
                        customisationList.clear();
                        finalPayment.clear();
                        price.clear();
                        time.clear();
                        finalUserNames.clear();
                        finalHalfOr.clear();
                        finalDishNames.clear();
                        for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                            currentTakeAwayAuth.add(String.valueOf(dataSnapshot.getKey()));
                            for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                                dishNameCurrentTakeAway.add(String.valueOf(dataSnapshot1.child("name").getValue()));
                                halfOr.add(String.valueOf(dataSnapshot1.child("halfOr").getValue()));
                                orderAmount = String.valueOf(dataSnapshot1.child("orderAmount").getValue());
                                orderId = String.valueOf(dataSnapshot1.child("orderID").getValue());
                                paymentMode = String.valueOf(dataSnapshot1.child("paymmentMode").getValue());
                                dishQuantityCurrentTakeAway.add(String.valueOf(dataSnapshot1.child("timesOrdered").getValue()));
                                image.add(String.valueOf(dataSnapshot1.child("image").getValue()));
                                type.add(String.valueOf(dataSnapshot1.child("type").getValue()));
                                price.add(String.valueOf(dataSnapshot1.child("price").getValue()));
                                userNameTakeAway.add(String.valueOf(dataSnapshot1.child("nameOfUser").getValue()));
                                orderAndPayment.add(String.valueOf(dataSnapshot1.child("orderAndPayment").getValue()));
                                usernameOfTakeAway = String.valueOf(dataSnapshot1.child("nameOfUser").getValue());
                                currentTime = String.valueOf(dataSnapshot1.child("time").getValue());
                                cus = String.valueOf(dataSnapshot1.child("customisation").getValue());
                                if(dataSnapshot1.hasChild("deliveryInformation"))
                                    deliveryInformation.add(dataSnapshot1.child("deliveryInformation").getValue(String.class));
                                else
                                    deliveryInformation.add("");
                            }
                            customisationList.add(cus);
                            time.add(currentTime);
                            finalDishNames.add(new ArrayList<>(dishNameCurrentTakeAway));
                            finalDishQuantity.add(new ArrayList<>(dishQuantityCurrentTakeAway));
                            finalHalfOr.add(new ArrayList<>(halfOr));
                            finalPayment.add(paymentMode);
                            finalUserNames.add(usernameOfTakeAway);
                            finalImages.add(new ArrayList<>(image));
                            finalTypes.add(new ArrayList<>(type));
                            image.clear();
                            type.clear();
                            finalOrderAndPayments.add(new ArrayList<>(orderAndPayment));
                            finalDishPrices.add(new ArrayList<>(price));
                            price.clear();
                            orderAndPayment.clear();

                            dishNameCurrentTakeAway.clear();
                            dishQuantityCurrentTakeAway.clear();
                            orderIDs.add(orderId);
                            orderAmounts.add(orderAmount);
                            halfOr.clear();
                        }
                        Log.i("time",time.toString());
//                        Log.i("Current",currentTakeAwayAuth.toString() + " " + dishNameCurrentTakeAway.toString() + " " + userNameTakeAway.toString());
                        homeFragTakeAwayRecucler.setLayoutManager(anotherHori);
                        Log.i("message",finalDishNames.toString() + "\n" + finalPayment.toString() + "\n" + finalDishQuantity.toString());
//                        homeFragTakeAwayRecucler.setAdapter(new CurrentTakeAway(currentTakeAwayAuth,dishNameCurrentTakeAway,dishQuantityCurrentTakeAway,userNameTakeAway,halfOr,paymentMode));
                    }else
                    {
                        currentTakeAwayAuth.clear();
                        finalDishNames.clear();
                        dishNameCurrentTakeAway.clear();
                        userNameTakeAway.clear();
                        halfOr.clear();
                        finalDishQuantity.clear();
                        customisationList.clear();
                        finalDishPrices.clear();
                        finalPayment.clear();
                        finalUserNames.clear();
                        orderIDs.clear();
                        deliveryInformation.clear();
                        finalImages.clear();
                        finalTypes.clear();
                        orderAmounts.clear();
                        finalOrderAndPayments.clear();
                        time.clear();
                        finalHalfOr.clear();
                        dishQuantityCurrentTakeAway.clear();
                        homeFragTakeAwayRecucler.setLayoutManager(anotherHori);

                    }
                    homeFragTakeAwayRecucler.setAdapter(new CurrentTakeAway(finalDishNames,finalDishQuantity,finalHalfOr,finalUserNames,finalPayment,orderIDs,orderAmounts,currentTakeAwayAuth,time,customisationList,finalOrderAndPayments,finalDishPrices,finalImages,finalTypes,deliveryInformation));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            return null;
        }
    }

}
