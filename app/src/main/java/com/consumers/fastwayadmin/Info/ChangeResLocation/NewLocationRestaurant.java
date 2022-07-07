package com.consumers.fastwayadmin.Info.ChangeResLocation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.consumers.fastwayadmin.Info.MapsActivity;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class NewLocationRestaurant extends AppCompatActivity {
    LocationRequest locationRequest;
    boolean imageUploaded = false;
    boolean stateChange = false;
    boolean localChange = false;

    Uri filePath;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    boolean imageTaken = false;
    boolean allSameAsBefore = false;
    FirebaseStorage storage;
    StorageReference storageReference;
    String oldState,oldLocal;
    FusedLocationProviderClient clientsLocation;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    DatabaseReference toPath;
    boolean pin = false;
    boolean local = false;
    String cityName,pinCode;
    EditText newAddress,newLocality,newState,newPinCode;
    String subAdminArea;
    Button uploadResProof;
    Button change;
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
        uploadResProof = findViewById(R.id.uploadRestaurantProofDocuments);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        change = findViewById(R.id.uploadAndChangeNewLocation);
        sharedPreferences = getSharedPreferences("loginInfo",MODE_PRIVATE);
        oldState = sharedPreferences.getString("state","");
        oldLocal = sharedPreferences.getString("locality","");
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
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(NewLocationRestaurant.this);
                    builder1.setTitle("Important").setMessage("We need you to be present at required restaurant location for better accuracy\nDo you wanna proceed now or wait")
                            .setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(NewLocationRestaurant.this, MapsActivity2.class);
                                    startActivityForResult(intent,500);
                                }
                            }).setNegativeButton("Wait", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    finish();
                                }
                            }).create().show();
                }).create();
        builder.setCancelable(false);
        builder.show();


        uploadResProof.setOnClickListener(click -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction("android.intent.action.PICK");
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
        });


        change.setOnClickListener(click -> {
            if(imageTaken) {
                if (allSameAsBefore) {
                    if (newAddress.getText().toString().equals("")) {
                        newAddress.requestFocus();
                        Toast.makeText(this, "Enter new address", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    FirebaseAuth auth = FirebaseAuth.getInstance();
                    StorageReference ref = storageReference.child(auth.getUid() + "/" + "Documents" + "/" + "resProof");
                    ref.putFile(filePath).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if(task.isSuccessful()){
                                StorageReference reference = storageReference.child(auth.getUid() + "/" + "Documents" + "/" + "resProof");
                                reference.getDownloadUrl().addOnSuccessListener(uri -> {
                                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Admin").child(auth.getUid()).child("Restaurant Documents");
                                    databaseReference.child("resProof").setValue(uri + "");
                                });
                            }
                        }
                    });
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(cityName).child(subAdminArea).child(auth.getUid());
                    databaseReference.child("address").setValue(newAddress.getText().toString());
                    databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(cityName).child(subAdminArea).child(auth.getUid()).child("location");
                    databaseReference.child("lat").setValue(String.valueOf(lati));
                    databaseReference.child("lon").setValue(String.valueOf(longi));

                    databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Complaints").child("Registered Restaurants").child(cityName).child(auth.getUid());
                    databaseReference.child("locationChange").setValue("yes");

                    Toast.makeText(this, "Location Changed Successfully", Toast.LENGTH_SHORT).show();
                    Toast.makeText(this, "New Location will be verified by Fastway...", Toast.LENGTH_SHORT).show();
                    new Handler().postDelayed(this::finish, 550);

                }else if(stateChange){
                    if (newAddress.getText().toString().equals("")) {
                        newAddress.requestFocus();
                        Toast.makeText(this, "Enter new address", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    SharedPreferences sharedPreferences = getSharedPreferences("loginInfo",MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("state",cityName);
                    editor.putString("locality",subAdminArea);
                    editor.apply();
                    FirebaseAuth auth = FirebaseAuth.getInstance();
                    StorageReference ref = storageReference.child(auth.getUid() + "/" + "Documents" + "/" + "resProof");
                    ref.putFile(filePath).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if(task.isSuccessful()){
                                StorageReference reference = storageReference.child(auth.getUid() + "/" + "Documents" + "/" + "resProof");
                                reference.getDownloadUrl().addOnSuccessListener(uri -> {
                                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Admin").child(auth.getUid()).child("Restaurant Documents");
                                    databaseReference.child("resProof").setValue(uri + "");
                                });
                            }
                        }
                    });

                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(oldState).child(oldLocal).child(auth.getUid());
                    DatabaseReference newPath = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(cityName).child(subAdminArea).child(auth.getUid());
                    DatabaseReference finalNewPath = newPath;
                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            finalNewPath.setValue(snapshot.getValue(), (error, ref1) -> {

                            });

                            databaseReference.setValue(null);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    newPath = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(cityName).child(subAdminArea).child(auth.getUid());
                    newPath.child("address").setValue(newAddress.getText().toString());
                    newPath = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(cityName).child(subAdminArea).child(auth.getUid()).child("location");
                    newPath.child("lat").setValue(lati + "");
                    newPath.child("lon").setValue(longi + "");
                    DatabaseReference changeFastwayDB = FirebaseDatabase.getInstance().getReference().getRoot().child("Complaints").child("Registered Restaurants").child(oldState).child(auth.getUid());
                    changeFastwayDB.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            FirebaseDatabase.getInstance().getReference().getRoot().child("Complaints").child("Registered Restaurants").child(cityName).child(auth.getUid()).setValue(snapshot.getValue(), (error, ref12) -> {

                            });

                            changeFastwayDB.setValue(null);
                            DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference().getRoot().child("Complaints").child("Registered Restaurants").child(cityName).child(auth.getUid());
                            databaseReference1.child("locationChanges").setValue("yes");
                            databaseReference1.child("locality").setValue(subAdminArea);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    Toast.makeText(this, "Location Changed Successfully", Toast.LENGTH_SHORT).show();
                    Toast.makeText(this, "New Location will be verified by Fastway...", Toast.LENGTH_SHORT).show();
                    new Handler().postDelayed(this::finish, 550);
                }else if(localChange){
                    if (newAddress.getText().toString().equals("")) {
                        newAddress.requestFocus();
                        Toast.makeText(this, "Enter new address", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    FirebaseAuth auth = FirebaseAuth.getInstance();
                    StorageReference ref = storageReference.child(auth.getUid() + "/" + "Documents" + "/" + "resProof");
                    ref.putFile(filePath).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if(task.isSuccessful()){
                                StorageReference reference = storageReference.child(auth.getUid() + "/" + "Documents" + "/" + "resProof");
                                reference.getDownloadUrl().addOnSuccessListener(uri -> {
                                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Admin").child(auth.getUid()).child("Restaurant Documents");
                                    databaseReference.child("resProof").setValue(uri + "");
                                });
                            }
                        }
                    });
                    SharedPreferences sharedPreferences = getSharedPreferences("loginInfo",MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("locality",subAdminArea);
                    editor.apply();
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(oldState).child(oldLocal).child(Objects.requireNonNull(auth.getUid()));
                    DatabaseReference newPath = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(oldState).child(subAdminArea).child(auth.getUid());
                    DatabaseReference finalNewPath = newPath;
                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            finalNewPath.setValue(snapshot.getValue(), (error, ref1) -> {

                            });

                            databaseReference.setValue(null);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    newPath = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(oldState).child(subAdminArea).child(auth.getUid());
                    newPath.child("address").setValue(newAddress.getText().toString());
                    newPath = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(oldState).child(subAdminArea).child(auth.getUid()).child("location");
                    DatabaseReference changeFastwayDB = FirebaseDatabase.getInstance().getReference().getRoot().child("Complaints").child("Registered Restaurants").child(oldState).child(auth.getUid());
                    newPath.child("lat").setValue(lati + "");
                    newPath.child("lon").setValue(longi + "");
                    changeFastwayDB.child("locality").setValue(subAdminArea);
                    changeFastwayDB.child("locationChange").setValue("yes");
                    Toast.makeText(this, "Location Changed Successfully", Toast.LENGTH_SHORT).show();
                    Toast.makeText(this, "New Location will be verified by Fastway...", Toast.LENGTH_SHORT).show();
                    new Handler().postDelayed(this::finish, 550);

                }
            }else{
                Toast.makeText(this, "You need to upload new restaurant property proof", Toast.LENGTH_SHORT).show();
            }
        });
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
            newState.setText(cityName);
            newState.setEnabled(false);
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

//            if(local){
//                FirebaseAuth auth = FirebaseAuth.getInstance();
//                String key = auth.getUid();
//
//                FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(sharedPreferences.getString("state","")).child(sharedPreferences.getString("locality","")).child(auth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(cityName).child("North Delhi").child(auth.getUid()).setValue(snapshot.getValue());
//                        Toast.makeText(NewLocationRestaurant.this, "Completed", Toast.LENGTH_SHORT).show();
//
//                        FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(sharedPreferences.getString("state","")).child(sharedPreferences.getString("locality","")).child(auth.getUid()).setValue(null);
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });
//            }

            clientsLocation.removeLocationUpdates(mLocationCallback);
            dialog.dismiss();

            checkIfResLocationAreSimilar(cityName,subAdminArea);


        }
    };

    private void checkIfResLocationAreSimilar(String cityName, String subAdminArea) {
        if(cityName.equals(sharedPreferences.getString("state",""))){
            if(subAdminArea.equals(sharedPreferences.getString("locality",""))){
                allSameAsBefore = true;
            }else{
                localChange = true;
            }
        }else{
            stateChange = true;
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode ==  RESULT_OK && data != null){
            imageTaken = true;
            filePath = data.getData();
        }

        if(requestCode == 500 && resultCode == RESULT_OK){
            longi = Double.parseDouble(data.getStringExtra("lon"));
            lati = Double.parseDouble(data.getStringExtra("lat"));
            cityName = data.getStringExtra("state");
            subAdminArea = data.getStringExtra("locality");
                newLocality.setText(subAdminArea);
                newLocality.setEnabled(false);
                local = true;
            pinCode = data.getStringExtra("pin");
            newPinCode.setText(pinCode);
            newPinCode.setEnabled(false);
            pin = true;
            newState.setText(cityName);
            newState.setEnabled(false);
            checkIfResLocationAreSimilar(cityName,subAdminArea);
        }else if(requestCode == 500 && resultCode == RESULT_CANCELED){
            Toast.makeText(this, "Try Again Later", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}