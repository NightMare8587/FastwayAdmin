package com.consumers.fastwayadmin.SplashAndIntro;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.consumers.fastwayadmin.Login.MainActivity;
import com.consumers.fastwayadmin.R;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.Priority;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseApp;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class SplashScreen extends AppCompatActivity {
    double longi,lati;
    LocationRequest locationRequest;
    FusedLocationProviderClient clientsLocation;
    SharedPreferences loginInfo;
    SharedPreferences.Editor loginEditor;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        ConnectivityManager cm =
                (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        FirebaseApp.initializeApp(/*context=*/ this);
        FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();
        firebaseAppCheck.installAppCheckProviderFactory(
                PlayIntegrityAppCheckProviderFactory.getInstance());
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        //checking is app is connected to internet
        if(!isConnected){
            View parentLayout = findViewById(android.R.id.content);
            Snackbar.make(parentLayout, "Please connect to internet :)", Snackbar.LENGTH_LONG)
                    .setAction("CLOSE", view -> {

                    })
                    .setActionTextColor(getResources().getColor(android.R.color.holo_red_light ))
                    .show();
        }
         sharedPreferences = getSharedPreferences("IntroAct",MODE_PRIVATE);
        SharedPreferences stopServices = getSharedPreferences("Stop Services", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = stopServices.edit();
        editor.putString("online","true");
        loginInfo = getSharedPreferences("loginInfo",MODE_PRIVATE);
        loginEditor = loginInfo.edit();
        editor.apply();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkPermissions();
        }
        //this starts new activity

    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkPermissions() {
        if(ContextCompat.checkSelfPermission(SplashScreen.this, Manifest.permission.ACCESS_FINE_LOCATION) + ContextCompat.checkSelfPermission(SplashScreen.this , Manifest.permission.CAMERA)
                + ContextCompat.checkSelfPermission(SplashScreen.this, Manifest.permission.WRITE_EXTERNAL_STORAGE )
                != PackageManager.PERMISSION_GRANTED){
//            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
            requestPermissions(new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.ACCESS_FINE_LOCATION},1);
        }else
            createLocationRequest();
    }
    //creating location request
    private void createLocationRequest() {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(Priority.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        clientsLocation = LocationServices.getFusedLocationProviderClient(SplashScreen.this);
        clientsLocation.requestLocationUpdates(locationRequest,mLocationCallback, Looper.myLooper());
        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnCompleteListener(task1 -> {
            //                LocationSettingsResponse response = task1.getResult(ApiException.class);
            // All location settings are satisfied. The client can initialize location
            // requests here.
            new Handler().postDelayed(() -> {
                if(sharedPreferences.contains("done")){
                    startActivity(new Intent(SplashScreen.this, MainActivity.class));
                }else {
                    startActivity(new Intent(SplashScreen.this, IntroActivity.class));
                }
                clientsLocation.removeLocationUpdates(mLocationCallback);
                finish();

                overridePendingTransition(R.anim.slide_in, R.anim.fade_out);
            },500);
        });
    }
    //location callback for handling location
    private final LocationCallback mLocationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();

            longi = mLastLocation.getLongitude();
            lati = mLastLocation.getLatitude();
            loginEditor.putString("longi",String.valueOf(longi));
            loginEditor.putString("lati",String.valueOf(lati));
            Geocoder geocoder = new Geocoder(SplashScreen.this, Locale.getDefault());
            List<Address> addresses = null;
            String cityName;
            String subAdmin;
            String postalCode;

            try {
                addresses = geocoder.getFromLocation(lati, longi, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            cityName = Objects.requireNonNull(addresses).get(0).getAdminArea();
            subAdmin = Objects.requireNonNull(addresses).get(0).getAdminArea();

            if(addresses.get(0).getPostalCode() != null)
                postalCode = addresses.get(0).getPostalCode();
            else
                postalCode = "";
            loginEditor.putString("state",cityName);
            loginEditor.putString("locality",subAdmin);
            loginEditor.putString("postalCode",postalCode);
            loginEditor.apply();
            Log.i("info", cityName + " " + subAdmin);
            Log.i("location",longi + " " + lati);
            clientsLocation.removeLocationUpdates(mLocationCallback);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 101) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    // All required changes were successfully made
//                    Toast.makeText(MainActivity.this, states.isLocationPresent() + "", Toast.LENGTH_SHORT).show();
                    new Handler().postDelayed(() -> {
                        if(sharedPreferences.contains("done")){
                            startActivity(new Intent(SplashScreen.this, MainActivity.class));
                        }else {
                            startActivity(new Intent(SplashScreen.this, IntroActivity.class));
                        }
                        clientsLocation.removeLocationUpdates(mLocationCallback);
                        finish();

                        overridePendingTransition(R.anim.slide_in, R.anim.fade_out);
                    },700);
                    break;
                case Activity.RESULT_CANCELED:
                    // The user was asked to change settings, but chose not to

                    createLocationRequest();
                    break;
                default:
                    break;
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 1){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                createLocationRequest();
            }else{
                AlertDialog.Builder builder = new AlertDialog.Builder(SplashScreen.this);
                builder.setTitle("Important");
                builder.setMessage("Location is required for this app to work properly");
                builder.setPositiveButton("Allow", (dialogInterface, i) -> checkPermissions()).setNegativeButton("No", (dialogInterface, i) -> dialogInterface.dismiss()).create();

                builder.show();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
}