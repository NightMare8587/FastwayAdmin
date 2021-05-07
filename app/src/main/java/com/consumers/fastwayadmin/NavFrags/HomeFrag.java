package com.consumers.fastwayadmin.NavFrags;

import android.Manifest;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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
import com.consumers.fastwayadmin.DiscountCombo.DiscountActivity;
//import com.consumers.fastwayadmin.NavFrags.homeFrag.homeAdapter;
import com.consumers.fastwayadmin.NavFrags.homeFrag.homeFragClass;
//import com.consumers.fastwayadmin.NavFrags.homeFrag.homeModel;
import com.consumers.fastwayadmin.R;
import com.developer.kalert.KAlertDialog;
import com.example.flatdialoglibrary.dialog.FlatDialog;
import com.firebase.ui.database.FirebaseRecyclerOptions;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
import java.util.Timer;
import java.util.TimerTask;

public class HomeFrag extends Fragment {

    FirebaseAuth auth;
    RecyclerView recyclerView;
    LocationRequest locationRequest;
    LinearLayoutManager horizonatl;
    ImageView comboImage;
    Toolbar toolbar;
    Button refershRecyclerView;
//    homeAdapter homeAdapter;
    SharedPreferences sharedPreferences;
    DatabaseReference reference;
    FusedLocationProviderClient client;
    List<String> resId = new ArrayList<>();
    List<String> tableNum = new ArrayList<>();
    List<String> seats = new ArrayList<>();
    int count = 0;
    boolean pressed = false;
    int total = 0;
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
                    Objects.requireNonNull(getActivity()).finish();

                new Handler().postDelayed(() -> {
                    pressed = false;
                    count = 0;
                },2000);
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this,callback);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        toolbar = view.findViewById(R.id.homeFragToolBar);
        ((AppCompatActivity) Objects.requireNonNull(getActivity())).setSupportActionBar(toolbar);
        recyclerView = view.findViewById(R.id.homeFragRecyclerView);
        refershRecyclerView = view.findViewById(R.id.refreshCurrentTables);
//        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext(),LinearLayoutManager.HORIZONTAL),true);
        comboImage = view.findViewById(R.id.comboDiscountImageView);
        auth = FirebaseAuth.getInstance();
        horizonatl = new LinearLayoutManager(view.getContext(),LinearLayoutManager.HORIZONTAL,false);
        sharedPreferences = getActivity().getSharedPreferences("locations current", Context.MODE_PRIVATE);
        reference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(Objects.requireNonNull(auth.getUid()));
        FirebaseMessaging.getInstance().subscribeToTopic(Objects.requireNonNull(auth.getUid()));
        if(ContextCompat.checkSelfPermission(Objects.requireNonNull(getActivity()),Manifest.permission.CAMERA) + ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.CAMERA,Manifest.permission.ACCESS_COARSE_LOCATION},1);
        }else
//            createLocationRequest();

        comboImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                        .withFirstButtonListner(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                startActivity(new Intent(getContext(), DiscountActivity.class));
                                flatDialog.dismiss();
                            }
                        })
                        .withSecondButtonListner(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                startActivity(new Intent(getContext(), ComboAndOffers.class));
                                flatDialog.dismiss();
                            }
                        })
                        .withThirdButtonListner(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                flatDialog.dismiss();
                            }
                        }).show();

            }
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




        refershRecyclerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tableNum.clear();
                seats.clear();

                reference.child("Tables").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            resId.clear();
                            seats.clear();
                            tableNum.clear();
//                    Toast.makeText(view.getContext(), ""+snapshot.getChildrenCount(), Toast.LENGTH_SHORT).show();
                            for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                                if(dataSnapshot.child("status").getValue(String.class).equals("unavailable")){
                                    resId.add(dataSnapshot.child("customerId").getValue(String.class));
                                    tableNum.add(dataSnapshot.child("tableNum").getValue(String.class));
                                    seats.add(dataSnapshot.child("numSeats").getValue(String.class));
                                }
                            }
                            recyclerView.setLayoutManager(horizonatl);
//                    Toast.makeText(view.getContext(), ""+seats.toString(), Toast.LENGTH_SHORT).show();
                            recyclerView.setAdapter(new homeFragClass(tableNum,seats,resId));
                        }else{

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
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
                    resId.clear();
//                    Toast.makeText(view.getContext(), "I am invoked", Toast.LENGTH_SHORT).show();
//                    Toast.makeText(view.getContext(), ""+snapshot.getChildrenCount(), Toast.LENGTH_SHORT).show();
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                        if(dataSnapshot.child("status").getValue(String.class).equals("unavailable")){
                            resId.add(dataSnapshot.child("customerId").getValue(String.class));
                            tableNum.add(dataSnapshot.child("tableNum").getValue(String.class));
                            seats.add(dataSnapshot.child("numSeats").getValue(String.class));
                        }
                    }
                    recyclerView.setLayoutManager(horizonatl);
//                    Toast.makeText(view.getContext(), ""+seats.toString(), Toast.LENGTH_SHORT).show();
                    recyclerView.setAdapter(new homeFragClass(tableNum,seats,resId));
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
                AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
                builder.setCancelable(false);
                builder.setTitle("Important").setMessage("Camera is required for proper functioning of app. Wanna provide permission??")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if(ContextCompat.checkSelfPermission(Objects.requireNonNull(getActivity()),Manifest.permission.CAMERA) + ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
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
        switch (requestCode) {
            case 101:
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
                break;
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
//                    Toast.makeText(view.getContext(), "I am invoked", Toast.LENGTH_SHORT).show();
//                    Toast.makeText(view.getContext(), ""+snapshot.getChildrenCount(), Toast.LENGTH_SHORT).show();
                        for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                            if(dataSnapshot.child("status").getValue(String.class).equals("unavailable")){
                                resId.add(dataSnapshot.child("customerId").getValue(String.class));
                                tableNum.add(dataSnapshot.child("tableNum").getValue(String.class));
                                seats.add(dataSnapshot.child("numSeats").getValue(String.class));
                            }
                        }
                        recyclerView.setLayoutManager(horizonatl);
//                    Toast.makeText(view.getContext(), ""+seats.toString(), Toast.LENGTH_SHORT).show();
                        recyclerView.setAdapter(new homeFragClass(tableNum,seats,resId));
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
        client = LocationServices.getFusedLocationProviderClient(Objects.requireNonNull(getActivity()));
        client.requestLocationUpdates(locationRequest,mLocationCallback, Looper.myLooper());
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);


        SettingsClient client = LocationServices.getSettingsClient(Objects.requireNonNull(getActivity()));
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(Task<LocationSettingsResponse> task) {
                try {
                    LocationSettingsResponse response = task.getResult(ApiException.class);
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
            }
        });

    }

}
