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

import com.consumers.fastwayadmin.DiscountCombo.ComboAndOffers;
import com.consumers.fastwayadmin.DiscountCombo.CustomOffer;
import com.consumers.fastwayadmin.DiscountCombo.DiscountActivity;
import com.consumers.fastwayadmin.NavFrags.CurrentTakeAwayOrders.CurrentTakeAway;
import com.consumers.fastwayadmin.NavFrags.homeFrag.homeFragClass;
import com.consumers.fastwayadmin.R;
import com.example.flatdialoglibrary.dialog.FlatDialog;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

//import com.consumers.fastwayadmin.NavFrags.homeFrag.homeAdapter;
//import com.consumers.fastwayadmin.NavFrags.homeFrag.homeModel;

public class HomeFrag extends Fragment {


    List<List<String>> finalDishNames = new ArrayList<>();
    List<List<String>> finalDishQuantity = new ArrayList<>();
    List<List<String>> finalHalfOr = new ArrayList<>();
    List<String> finalPayment = new ArrayList<>();
    List<String> orderIDs = new ArrayList<>();
    List<String> orderAmounts = new ArrayList<>();
    SharedPreferences resInfoShared;
    String usernameOfTakeAway;
    String orderId,orderAmount;
    List<String> finalUserNames = new ArrayList<>();
    FirebaseAuth auth;
    RecyclerView recyclerView,homeFragTakeAwayRecucler;
    List<String> currentTakeAwayAuth = new ArrayList<>();
    List<String> dishNameCurrentTakeAway = new ArrayList<>();
    List<String> dishQuantityCurrentTakeAway = new ArrayList<>();
    List<String> userNameTakeAway = new ArrayList<>();
    String paymentMode;
    LinearLayout linearLayout,secondLinearLayout;
    List<String> halfOr = new ArrayList<>();
    DatabaseReference checkForBank;
    DatabaseReference onlineOrOfflineRestaurant;
    SharedPreferences accountInfo;
    SharedPreferences restaurantStatus;
    private final int UPDATE_REQUEST_CODE = 69;
    SharedPreferences vendorIdCreated;
    SharedPreferences.Editor vendorIdEditor;
    LocationRequest locationRequest;
    LinearLayoutManager horizonatl,anotherHori;
    ImageView comboImage;
    SharedPreferences.Editor statusEditor;
    List<List<String>> currentOrdersIfAvailable = new ArrayList<>();
    Toolbar toolbar;
    Button refershRecyclerView,refreshTakeAway;
    List<String> isCurrentOrder = new ArrayList<>();
//    homeAdapter homeAdapter;
    SharedPreferences sharedPreferences;
    Switch onlineOrOffline;
    DatabaseReference reference;
    FusedLocationProviderClient client;
    List<String> resId = new ArrayList<>();
    List<String> tableNum = new ArrayList<>();
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
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
        inAppUpdateInfo();
        SharedPreferences stopServices = requireActivity().getSharedPreferences("Stop Services",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = stopServices.edit();
        resInfoShared = view.getContext().getSharedPreferences("loginInfo",Context.MODE_PRIVATE);
        restaurantStatus = view.getContext().getSharedPreferences("RestaurantStatus",Context.MODE_PRIVATE);
        statusEditor = restaurantStatus.edit();
        onlineOrOffline = view.findViewById(R.id.restaurantOnOff);
        linearLayout = view.findViewById(R.id.mainFragLinearLayout);
        homeFragTakeAwayRecucler = view.findViewById(R.id.homeFragTakeAwayRecyclerView);
        secondLinearLayout = view.findViewById(R.id.secondFragLinearLayout);
        vendorIdCreated = view.getContext().getSharedPreferences("VendorID",Context.MODE_PRIVATE);
        vendorIdEditor = vendorIdCreated.edit();
        new checkBank().execute();



        recyclerView = view.findViewById(R.id.homeFragRecyclerView);
        refershRecyclerView = view.findViewById(R.id.refreshCurrentTables);
//        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext(),LinearLayoutManager.HORIZONTAL),true);
        comboImage = view.findViewById(R.id.comboDiscountImageView);
        auth = FirebaseAuth.getInstance();
        onlineOrOfflineRestaurant = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(resInfoShared.getString("state","")).child(Objects.requireNonNull(auth.getUid()));
        horizonatl = new LinearLayoutManager(view.getContext(),LinearLayoutManager.HORIZONTAL,false);
        anotherHori = new LinearLayoutManager(view.getContext(),LinearLayoutManager.HORIZONTAL,false);;
        sharedPreferences = requireActivity().getSharedPreferences("locations current", Context.MODE_PRIVATE);
        reference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(resInfoShared.getString("state","")).child(Objects.requireNonNull(auth.getUid()));
        FirebaseMessaging.getInstance().subscribeToTopic(Objects.requireNonNull(auth.getUid()));
        if(restaurantStatus.contains("status")){
            if(restaurantStatus.getString("status","").equals("offline")){
                comboImage.setVisibility(View.INVISIBLE);
                linearLayout.setVisibility(View.INVISIBLE);
                onlineOrOffline.setChecked(false);
                onlineOrOffline.setText("offline");
            }else{
                comboImage.setVisibility(View.VISIBLE);
                linearLayout.setVisibility(View.VISIBLE);
                onlineOrOffline.setChecked(true);
                onlineOrOffline.setText("online");
            }
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
                    .setFirstButtonText("DISCOUNT & OFFERS")
                    .setSecondButtonColor(Color.parseColor("#fee9b2"))
                    .setSecondButtonTextColor(Color.parseColor("#000000"))
                    .setSecondButtonText("COMBO/THALI")
                    .setThirdButtonColor(Color.parseColor("#fbd1b7"))
                    .setThirdButtonTextColor(Color.parseColor("#000000"))
                    .setThirdButtonText("ADD CUSTOM OFFER")
                    .withFirstButtonListner(view11 -> {
                        startActivity(new Intent(getContext(), DiscountActivity.class));
                        flatDialog.dismiss();
                    })
                    .withSecondButtonListner(view112 -> {
                        startActivity(new Intent(getContext(), ComboAndOffers.class));
                        flatDialog.dismiss();
                    })
                    .withThirdButtonListner(view113 -> {
                        flatDialog.dismiss();
                        startActivity(new Intent(getContext(), CustomOffer.class));
                    })
                    .show();

        });

        new MyTask().execute();

//        reference.child("Tables").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if(snapshot.exists()){
//                    tableNum.clear();
//                    seats.clear();
//                    resId.clear();
////                    Toast.makeText(view.getContext(), "I am invoked", Toast.LENGTH_SHORT).show();
////                    Toast.makeText(view.getContext(), ""+snapshot.getChildrenCount(), Toast.LENGTH_SHORT).show();
//                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
//                        if(dataSnapshot.child("status").getValue(String.class).equals("unavailable")){
//                            resId.add(dataSnapshot.child("customerId").getValue(String.class));
//                            tableNum.add(dataSnapshot.child("tableNum").getValue(String.class));
//                            seats.add(dataSnapshot.child("numSeats").getValue(String.class));
//                        }
//                    }
//                    recyclerView.setLayoutManager(horizonatl);
////                    Toast.makeText(view.getContext(), ""+seats.toString(), Toast.LENGTH_SHORT).show();
//                    recyclerView.setAdapter(new homeFragClass(tableNum,seats,resId));
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });

        onlineOrOffline.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                statusEditor.putString("status","online");
                onlineOrOffline.setText("online");
                statusEditor.apply();
                comboImage.setVisibility(View.VISIBLE);
                linearLayout.setVisibility(View.VISIBLE);
                onlineOrOfflineRestaurant.child("status").setValue("online");
                editor.putString("online","true");
            }else{
                statusEditor.putString("status","offline");
                onlineOrOffline.setText("offline");
                statusEditor.apply();
                comboImage.setVisibility(View.INVISIBLE);
                linearLayout.setVisibility(View.INVISIBLE);
                onlineOrOfflineRestaurant.child("status").setValue("offline");
                editor.putString("online","false");
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
                updateDatabase();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                updateDatabase();
            }
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
                        isCurrentOrder.clear();
                        tableNum.clear();
//                    Toast.makeText(view.getContext(), ""+snapshot.getChildrenCount(), Toast.LENGTH_SHORT).show();
                        for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                            if(Objects.equals(dataSnapshot.child("status").getValue(String.class), "unavailable")){

                                resId.add(dataSnapshot.child("customerId").getValue(String.class));
                                tableNum.add(dataSnapshot.child("tableNum").getValue(String.class));
                                seats.add(dataSnapshot.child("numSeats").getValue(String.class));
                                if(dataSnapshot.hasChild("Current Order")){
                                    isCurrentOrder.add("1");
                                }else
                                    isCurrentOrder.add("0");
                            }
                        }
                        recyclerView.setLayoutManager(horizonatl);
//                    Toast.makeText(view.getContext(), ""+seats.toString(), Toast.LENGTH_SHORT).show();
                        recyclerView.setAdapter(new homeFragClass(tableNum,seats,resId,isCurrentOrder));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        });

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
                    appUpdateManager.startUpdateFlowForResult(appUpdateInfo, IMMEDIATE, (Activity) getContext(),UPDATE_REQUEST_CODE);
                } catch (IntentSender.SendIntentException e) {
                    Toast.makeText(getContext(), "" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateDatabase() {
        reference.child("Tables").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    tableNum.clear();
                    seats.clear();
                    isCurrentOrder.clear();
                    resId.clear();
//                    Toast.makeText(view.getContext(), "I am invoked", Toast.LENGTH_SHORT).show();
//                    Toast.makeText(view.getContext(), ""+snapshot.getChildrenCount(), Toast.LENGTH_SHORT).show();
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                        if(Objects.equals(dataSnapshot.child("status").getValue(String.class), "unavailable")){

                            resId.add(dataSnapshot.child("customerId").getValue(String.class));
                            tableNum.add(dataSnapshot.child("tableNum").getValue(String.class));
                            seats.add(dataSnapshot.child("numSeats").getValue(String.class));

                            if(dataSnapshot.hasChild("Current Order")){
                                isCurrentOrder.add("1");
                            }else
                                isCurrentOrder.add("0");
                        }
                    }
                    recyclerView.setLayoutManager(horizonatl);
//                    Toast.makeText(view.getContext(), ""+seats.toString(), Toast.LENGTH_SHORT).show();
                    recyclerView.setAdapter(new homeFragClass(tableNum,seats,resId,isCurrentOrder));
                }else{

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
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if(ContextCompat.checkSelfPermission(requireActivity(),Manifest.permission.CAMERA) + ContextCompat.checkSelfPermission(requireActivity(),Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                                    requestPermissions(new String[]{Manifest.permission.CAMERA,Manifest.permission.ACCESS_COARSE_LOCATION},1);
                                }
                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).create();
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
//            RestLocation restLocation = new RestLocation(sharedPreferences.getString("lati",""),sharedPreferences.getString("longi",""));
//            reference.child("location").setValue(restLocation);
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
            reference.child("Tables").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        tableNum.clear();
                        seats.clear();
                        resId.clear();
                        isCurrentOrder.clear();
//                    Toast.makeText(view.getContext(), "I am invoked", Toast.LENGTH_SHORT).show();
//                    Toast.makeText(view.getContext(), ""+snapshot.getChildrenCount(), Toast.LENGTH_SHORT).show();
                        for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                            if(dataSnapshot.child("status").getValue(String.class).equals("unavailable")){
                                resId.add(dataSnapshot.child("customerId").getValue(String.class));
                                tableNum.add(dataSnapshot.child("tableNum").getValue(String.class));
                                seats.add(dataSnapshot.child("numSeats").getValue(String.class));

                                if(dataSnapshot.hasChild("Current Order")){
                                    isCurrentOrder.add("1");
                                }else
                                    isCurrentOrder.add("0");
                            }
                        }
                        recyclerView.setLayoutManager(horizonatl);
//                    Toast.makeText(view.getContext(), ""+seats.toString(), Toast.LENGTH_SHORT).show();
                        recyclerView.setAdapter(new homeFragClass(tableNum,seats,resId,isCurrentOrder));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        @Override
        protected Void doInBackground(Void... voids) {
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
            FirebaseAuth auth = FirebaseAuth.getInstance();
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(resInfoShared.getString("state","")).child(auth.getUid()).child("Tables");
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
        }

        @Override
        protected Void doInBackground(Void... voids) {
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

    public class checkBank extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            checkForBank = FirebaseDatabase.getInstance().getReference().getRoot().child("Admin").child(Objects.requireNonNull(auth.getUid()));
            checkForBank.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(!snapshot.hasChild("Bank Details")){
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(requireContext());
                        alertDialog.setTitle("Important");
                        alertDialog.setMessage("You need to add bank details to accept payments");
                        alertDialog.setPositiveButton("Add", (dialogInterface, i) -> {
                            accountInfo = requireContext().getSharedPreferences("AccountInfo",Context.MODE_PRIVATE);
                            Intent intent = new Intent(requireContext(),VendorDetailsActivity.class);
                            intent.putExtra("name",accountInfo.getString("name",""));
                            intent.putExtra("email",accountInfo.getString("email",""));
                            startActivity(intent);
                        }).create();

                        alertDialog.show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            return null;
        }
    }

    public class TakeAwayClass extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(resInfoShared.getString("state","")).child(Objects.requireNonNull(auth.getUid())).child("Current TakeAway");
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
                        finalPayment.clear();
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
                                userNameTakeAway.add(String.valueOf(dataSnapshot1.child("nameOfUser").getValue()));
                                usernameOfTakeAway = String.valueOf(dataSnapshot1.child("nameOfUser").getValue());
                            }
                            finalDishNames.add(new ArrayList<>(dishNameCurrentTakeAway));
                            finalDishQuantity.add(new ArrayList<>(dishQuantityCurrentTakeAway));
                            finalHalfOr.add(new ArrayList<>(halfOr));
                            finalPayment.add(paymentMode);
                            finalUserNames.add(usernameOfTakeAway);
                            dishNameCurrentTakeAway.clear();
                            dishQuantityCurrentTakeAway.clear();
                            orderIDs.add(orderId);
                            orderAmounts.add(orderAmount);
                            halfOr.clear();
                        }
//                        Log.i("Current",currentTakeAwayAuth.toString() + " " + dishNameCurrentTakeAway.toString() + " " + userNameTakeAway.toString());
                        homeFragTakeAwayRecucler.setLayoutManager(anotherHori);
                        Log.i("message",finalDishNames.toString() + "\n" + finalPayment.toString() + "\n" + finalDishQuantity.toString());
//                        homeFragTakeAwayRecucler.setAdapter(new CurrentTakeAway(currentTakeAwayAuth,dishNameCurrentTakeAway,dishQuantityCurrentTakeAway,userNameTakeAway,halfOr,paymentMode));
                            homeFragTakeAwayRecucler.setAdapter(new CurrentTakeAway(finalDishNames,finalDishQuantity,finalHalfOr,finalUserNames,finalPayment,orderIDs,orderAmounts,currentTakeAwayAuth));
                    }else
                    {
                        currentTakeAwayAuth.clear();
                        finalDishNames.clear();
                        dishNameCurrentTakeAway.clear();
                        userNameTakeAway.clear();
                        halfOr.clear();
                        finalDishQuantity.clear();
                        finalPayment.clear();
                        finalUserNames.clear();
                        orderIDs.clear();
                        orderAmounts.clear();
                        finalHalfOr.clear();
                        dishQuantityCurrentTakeAway.clear();
                        homeFragTakeAwayRecucler.setLayoutManager(anotherHori);
                        homeFragTakeAwayRecucler.setAdapter(new CurrentTakeAway(finalDishNames,finalDishQuantity,finalHalfOr,finalUserNames,finalPayment,orderIDs,orderAmounts,currentTakeAwayAuth));

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            return null;
        }
    }
}
