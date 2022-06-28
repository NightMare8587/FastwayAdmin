package com.consumers.fastwayadmin.Info.ChangeResLocation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class NewLocationRestaurant extends AppCompatActivity {
    LocationRequest locationRequest;
    FusedLocationProviderClient clientsLocation;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    DatabaseReference toPath;
    boolean pin = false;
    boolean local = false;
    String cityName,pinCode;
    EditText newAddress,newLocality,newState,newPinCode;
    String subAdminArea;
    AlertDialog dialog;
    double longi,lati;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_location_restaurant);
        newAddress = findViewById(R.id.newAddressNewLocationRequest);
        newLocality = findViewById(R.id.localityNameNewLocationRequest);
        newState = findViewById(R.id.stateNameNewLocationRequest);
        newPinCode = findViewById(R.id.newPinCodeNewLocationRequest);
        sharedPreferences = getSharedPreferences("loginInfo",MODE_PRIVATE);
        editor = sharedPreferences.edit();
        AlertDialog.Builder builder = new AlertDialog.Builder(NewLocationRestaurant.this);
        builder.setTitle("Important").setMessage("Are you currently present at new restaurant location?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        createLocationRequest();
                        AlertDialog.Builder builder = new AlertDialog.Builder(NewLocationRestaurant.this);
                        LayoutInflater layoutInflater = LayoutInflater.from(NewLocationRestaurant.this);
                        View view = layoutInflater.inflate(R.layout.fastway_custom_loading_dialog,null);
                        builder.setView(view);
                        builder.setCancelable(false);
                        dialog = builder.create();
                        dialog.show();
                    }
                }).setNegativeButton("No", (dialogInterface, i) -> {

                }).create();
        builder.setCancelable(false);
        builder.show();
    }

        private void createLocationRequest() {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        clientsLocation = LocationServices.getFusedLocationProviderClient(NewLocationRestaurant.this);
        clientsLocation.requestLocationUpdates(locationRequest,mLocationCallback, Looper.myLooper());
        SettingsClient client = LocationServices.getSettingsClient(this);
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
                                    NewLocationRestaurant.this,
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

    private final LocationCallback mLocationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            longi = mLastLocation.getLongitude();
            lati = mLastLocation.getLatitude();
            editor.putString("longi",String.valueOf(longi));
            editor.putString("lati",String.valueOf(lati));
            Geocoder geocoder = new Geocoder(NewLocationRestaurant.this, Locale.getDefault());
            List<Address> addresses = null;

            try {
                addresses = geocoder.getFromLocation(lati, longi, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            cityName = addresses.get(0).getLocality();
            if(addresses.get(0).getSubAdminArea() != null) {
                subAdminArea = addresses.get(0).getSubAdminArea();
                newLocality.setText(subAdminArea);
                newLocality.setEnabled(false);
                local = true;
            }
            if(addresses.get(0).getSubAdminArea() != null) {
                pinCode = addresses.get(0).getPostalCode();
                newPinCode.setText(pinCode);
                newPinCode.setEnabled(false);
                pin = true;
            }
            Log.i("info",cityName);

            if(local){
                FirebaseAuth auth = FirebaseAuth.getInstance();
                String key = auth.getUid();
                FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(sharedPreferences.getString("state","")).child(sharedPreferences.getString("locality","")).child(auth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(cityName).child("North Delhi").child(auth.getUid()).setValue(snapshot.getValue());
                        Toast.makeText(NewLocationRestaurant.this, "Completed", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            clientsLocation.removeLocationUpdates(mLocationCallback);
            dialog.dismiss();

//            checkIfResLocationAreSimilar(cityName,subAdminArea);


        }
    };

    private void checkIfResLocationAreSimilar(String cityName, String subAdminArea) {
        if(cityName.equals(sharedPreferences.getString("state",""))){
            if(subAdminArea.equals(sharedPreferences.getString("locality",""))){

            }
        }else{
//            FirebaseAuth auth = FirebaseAuth.getInstance();
//
//            DatabaseReference fromPath = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(sharedPreferences.getString("state","")).child("North Delhi").child(Objects.requireNonNull(auth.getUid()));
//            fromPath.addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                    toPath.setValue(snapshot.getValue(), new DatabaseReference.CompletionListener() {
//                        @Override
//                        public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
//                            Toast.makeText(NewLocationRestaurant.this, "Completed", Toast.LENGTH_SHORT).show();
//                        }
//                    });
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError error) {
//
//                }
//            });
        }
    }
}