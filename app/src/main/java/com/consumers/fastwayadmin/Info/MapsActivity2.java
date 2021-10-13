package com.consumers.fastwayadmin.Info;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

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
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class MapsActivity2 extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    FusedLocationProviderClient client;
    FloatingActionButton actionButton;
    LocationRequest locationRequest;
    SharedPreferences currentLocation;
    SharedPreferences resLocationInfo;
    SharedPreferences.Editor editor;
    EditText editText;
    SharedPreferences.Editor locationEditor;
    ImageButton imageButton;
    FirebaseAuth auth;
    DatabaseReference ref;
    Button proceed;
    Double latitude,longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        checkRequiredPermission();
        resLocationInfo = getSharedPreferences("loginInfo",MODE_PRIVATE);
        locationEditor = resLocationInfo.edit();
        currentLocation = getSharedPreferences("locations current",MODE_PRIVATE);
        editor = currentLocation.edit();
        auth = FirebaseAuth.getInstance();
        ref = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants");
        editText = findViewById(R.id.searchRestuarantinMap);
        proceed = findViewById(R.id.proceedToSaveLatLong);
        imageButton = findViewById(R.id.searchButtonInMap);
        locationRequest = LocationRequest.create();
        createLocationRequest();
        actionButton = findViewById(R.id.refreshMap);
        client = LocationServices.getFusedLocationProviderClient(this);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createLocationRequest();
                client.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            Toast.makeText(MapsActivity2.this, "Yes", Toast.LENGTH_SHORT).show();
                            mMap.clear();
                            LatLng current = new LatLng(location.getLatitude(), location.getLongitude());
                            mMap.addMarker(new MarkerOptions().position(current).title("Current Location"));
//                            mMap.animateCamera(CameraUpdateFactory.zoomIn());
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(current, 15));
                        }else
                            createLocationRequest();
                    }
                });
            }
        });


        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ref = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(resLocationInfo.getString("state","")).child(Objects.requireNonNull(auth.getUid()));
                RestLocation restLocation = new RestLocation(String.valueOf(latitude),String.valueOf(longitude));
                ref.child("location").setValue(restLocation);
                setResult(69);
                finish();
            }
        });

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<Address> addressList = null;
                if(editText.length() == 0){
                    editText.requestFocus();
                    editText.setError("Field can't be Empty");
                }

                String location = editText.getText().toString();
                Geocoder geocoder = new Geocoder(getApplicationContext());
                try {
                    addressList = geocoder.getFromLocationName(location, 1);

                } catch (IOException e) {
                    e.printStackTrace();
                    Log.i("info",e.getLocalizedMessage());
                }
                if(addressList.size() == 0){
                    Toast.makeText(MapsActivity2.this, "No Result Founded\nTry some recognized places", Toast.LENGTH_SHORT).show();
                }else {
                    Address address = addressList.get(0);
                    LatLng current = new LatLng(address.getLatitude(), address.getLongitude());
                    mMap.clear();
                    latitude = address.getLatitude();
                    longitude = address.getLongitude();
                    mMap.addMarker(new MarkerOptions().position(current).title("Current Location"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(current, 15));
                }
            }
        });
    }

    private void checkRequiredPermission() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
            }
        }
    }

    private void createLocationRequest() {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(600000);
        locationRequest.setFastestInterval(600000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        client = LocationServices.getFusedLocationProviderClient(this);
        client.requestLocationUpdates(locationRequest,mLocationCallback, Looper.myLooper());
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
                                        MapsActivity2.this,
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

    private LocationCallback mLocationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            if(mLastLocation != null){
                mMap.clear();
                latitude = mLastLocation.getLatitude();
                longitude = mLastLocation.getLongitude();
                editor.putString("longi",String.valueOf(latitude));
                editor.putString("lati",String.valueOf(longitude));
                editor.apply();
                Geocoder geocoder = new Geocoder(MapsActivity2.this, Locale.getDefault());
                List<Address> addresses = null;
                String cityName;
                String stateName;
                String countryName;
                try {
                    addresses = geocoder.getFromLocation(latitude, longitude, 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                cityName = addresses.get(0).getLocality();

                locationEditor.putString("state",cityName);
                locationEditor.apply();
//               RestLocation restLocation = new RestLocation(String.valueOf(latitude),String.valueOf(longitude));
//               ref = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants");
//               ref.child("Locations").child(auth.getUid()).setValue(restLocation);
                LatLng current = new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude());
                mMap.addMarker(new MarkerOptions().title("Current position").position(current));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(current,15));
            }
        }
    };

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        client = LocationServices.getFusedLocationProviderClient(this);
//        createLocationRequest();
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mMap.clear();
                latitude = latLng.latitude;
                longitude = latLng.longitude;
                editor.putString("longi",String.valueOf(latitude));
                editor.putString("lati",String.valueOf(longitude));
                editor.apply();
                mMap.addMarker(new MarkerOptions().title("Current Position").position(latLng));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
            }
        });

        client.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location != null){
                    Toast.makeText(MapsActivity2.this, "Hello", Toast.LENGTH_SHORT).show();
                    LatLng current = new LatLng(location.getLatitude(),location.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(current).title("Current position"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(current,15));
                }else{
//                    Toast.makeText(MapsActivity.this, "Something went wrong!! Enable location and restart the App :)", Toast.LENGTH_SHORT).show();
                }
            }
        });
        // Add a marker in Sydney and move the camera

    }


    @Override
    protected void onStart() {
        super.onStart();
        client.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location != null){
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                }

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);
        switch (requestCode) {
            case 101:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        // All required changes were successfully made
                        Toast.makeText(MapsActivity2.this, states.isLocationPresent() + "", Toast.LENGTH_SHORT).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        // The user was asked to change settings, but chose not to
                        Toast.makeText(MapsActivity2.this, "Canceled", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 1){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                createLocationRequest();
            }else{
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Important").setMessage("Location is required for proper functioning of this app. Wanna provide permission?")
                        .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                checkRequiredPermission();
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
}