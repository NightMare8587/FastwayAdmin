package com.consumers.fastwayadmin.Info;

import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.consumers.fastwayadmin.HomeScreen.HomeScreen;
import com.consumers.fastwayadmin.Login.MainActivity;
import com.consumers.fastwayadmin.R;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hbb20.CountryCodePicker;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import karpuzoglu.enes.com.fastdialog.Animations;
import karpuzoglu.enes.com.fastdialog.FastDialog;
import karpuzoglu.enes.com.fastdialog.FastDialogBuilder;
import karpuzoglu.enes.com.fastdialog.Type;

public class Info extends AppCompatActivity {

    EditText nameOfRestaurant,AddressOfRestaurant,nearbyPlace,pinCode,contactNumber;
    Button proceed;
    LocationRequest locationRequest;
    FusedLocationProviderClient clientsLocation;
    double longi,lati;
    CountryCodePicker codePicker;
    FirebaseAuth infoAuth;
    DatabaseReference infoRef;
    FastDialog fastDialog;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String name,address,nearby,pin,number;
    SharedPreferences checkLocationInfo;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        initialise();
        checkLocationInfo = getSharedPreferences("LocationMaps",MODE_PRIVATE);
        checkPermissions();
        sharedPreferences = getSharedPreferences("loginInfo",MODE_PRIVATE);
        editor = sharedPreferences.edit();
        fastDialog = new FastDialogBuilder(Info.this, Type.PROGRESS)
                .progressText("Checking Database...")
                .setAnimation(Animations.SLIDE_TOP)
                .create();
        fastDialog.show();
        if(!sharedPreferences.contains("state")){
            fastDialog.dismiss();
            createLocationRequest();
        }else{
            if(checkLocationInfo.contains("location")){
                startActivity(new Intent(Info.this,HomeScreen.class));
                fastDialog.dismiss();
                finish();
            }
            fastDialog.dismiss();
        }
        Log.i("myLocation",sharedPreferences.getString("state",""));



        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(nameOfRestaurant.length() == 0){
                    nameOfRestaurant.requestFocus();
                    nameOfRestaurant.setError("Field can't be Empty");
                    return;
                }else if(AddressOfRestaurant.length() == 0){
                    AddressOfRestaurant.requestFocus();
                    AddressOfRestaurant.setError("Field cant be Empty");
                    return;
                }else if(pinCode.length() <= 5){
                    pinCode.requestFocus();
                    pinCode.setError("Invalid PinCode");
                    return;
                }else if(nearbyPlace.length() == 0){
                    nearbyPlace.requestFocus();
                    nearbyPlace.setError("Field can't be Empty");
                    return;
                }else if(contactNumber.length() <= 9 && contactNumber.length() >= 11){
                    contactNumber.requestFocus();
                    contactNumber.setError("Invalid Number");
                    return;
                }
                name = nameOfRestaurant.getText().toString();
                address = AddressOfRestaurant.getText().toString();
                pin = pinCode.getText().toString();
                nearby = nearbyPlace.getText().toString();
                number = codePicker.getSelectedCountryCodeWithPlus() +  contactNumber.getText().toString() + "";

                createChildForRestaurant();
            }
        });
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkPermissions() {
        if(ContextCompat.checkSelfPermission(Info.this, Manifest.permission.ACCESS_FINE_LOCATION) + ContextCompat.checkSelfPermission(Info.this , Manifest.permission.CAMERA)
                + ContextCompat.checkSelfPermission(Info.this, Manifest.permission.WRITE_EXTERNAL_STORAGE )
                != PackageManager.PERMISSION_GRANTED){
//            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
            requestPermissions(new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.ACCESS_FINE_LOCATION},1);
        }else
            createLocationRequest();
    }

    private void createChildForRestaurant() {
        SharedPreferences sharedPreferences = getSharedPreferences("RestaurantInfo",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("hotelName",name);
        editor.putString("hotelAddress",address);
        editor.putString("hotelNumber",number);
        editor.apply();
//        InfoRestaurant infoRestaurant = new InfoRestaurant(name,address,pin,number,nearby,"0","0","0","online");
//        infoRef.child("Restaurants").child(Objects.requireNonNull(infoAuth.getUid())).setValue(infoRestaurant);
        infoRef = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(this.sharedPreferences.getString("state","")).child(Objects.requireNonNull(infoAuth.getUid()));
        infoRef.child("name").setValue(name);
        infoRef.child("address").setValue(address);
        infoRef.child("number").setValue(number);
        infoRef.child("nearby").setValue(nearby);
        infoRef.child("pin").setValue(pin);
        infoRef.child("rating").setValue("0");
        infoRef.child("totalRate").setValue("0");
        infoRef.child("count").setValue("0");
        infoRef.child("status").setValue("online");
        clientsLocation.removeLocationUpdates(mLocationCallback);
        startActivity(new Intent(Info.this, MapsActivity.class));
        finish();
    }

    private void initialise() {
        infoAuth = FirebaseAuth.getInstance();
        nameOfRestaurant = findViewById(R.id.nameOfRestaurant);
        AddressOfRestaurant = findViewById(R.id.AddressOfRestaurant);
        nearbyPlace = findViewById(R.id.nearbyPlace);
        pinCode = findViewById(R.id.pincodeRestaurant);
        proceed = findViewById(R.id.proceed);
        codePicker = findViewById(R.id.codePicker);
        contactNumber = findViewById(R.id.contactNumber);
//        progressBar = findViewById(R.id.progressBar);

    }

    private void createLocationRequest() {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        clientsLocation = LocationServices.getFusedLocationProviderClient(Info.this);
        clientsLocation.requestLocationUpdates(locationRequest,mLocationCallback, Looper.myLooper());
        SettingsClient client = LocationServices.getSettingsClient(this);
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
                                        Info.this,
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

    private final LocationCallback mLocationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            longi = mLastLocation.getLongitude();
            lati = mLastLocation.getLatitude();
            editor.putString("longi",String.valueOf(longi));
            editor.putString("lati",String.valueOf(lati));
            Geocoder geocoder = new Geocoder(Info.this, Locale.getDefault());
            List<Address> addresses = null;
            String cityName;
            String stateName;
            String countryName;
            try {
                addresses = geocoder.getFromLocation(lati, longi, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            cityName = addresses.get(0).getLocality();

            editor.putString("state",cityName);
            editor.apply();
            Log.i("infoses", cityName + " " );
            Log.i("locationes",longi + " " + lati);
        }
    };
}