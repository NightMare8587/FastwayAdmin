package com.consumers.fastwayadmin.Info;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.consumers.fastwayadmin.HomeScreen.HomeScreen;
import com.consumers.fastwayadmin.Info.ChangeResLocation.MapsActivity2;
import com.consumers.fastwayadmin.Info.RestaurantDocuments.UploadRequiredDocuments;
import com.consumers.fastwayadmin.Info.RestaurantImages.AddRestaurantImages;
import com.consumers.fastwayadmin.R;
import com.firebase.geofire.GeoFireUtils;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.hbb20.CountryCodePicker;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import karpuzoglu.enes.com.fastdialog.Animations;
import karpuzoglu.enes.com.fastdialog.FastDialog;
import karpuzoglu.enes.com.fastdialog.FastDialogBuilder;
import karpuzoglu.enes.com.fastdialog.Type;

public class Info extends AppCompatActivity {
    EditText nameOfRestaurant,AddressOfRestaurant,nearbyPlace,pinCode,contactNumber;
    Button proceed;
    boolean resCreated = false;
    FastDialog loading;
//    LocationRequest locationRequest;
    FirebaseFirestore infoStore;
    Bitmap bitmap;
    StorageReference storageReference;
    AlertDialog.Builder builder;

    boolean mapLocations = false;
    String cityName,subAdminArea;
    AlertDialog alertDialog;
    String lon,lat;
    Uri filePath;
    boolean subLocal = false;
    FirebaseStorage storage;
    CheckBox optForTakeaway,optForTableBook;
    OutputStream outputStream;
    File file;
//    FusedLocationProviderClient clientsLocation;
    double longi,lati;
    CountryCodePicker codePicker;
    FirebaseAuth infoAuth = FirebaseAuth.getInstance();
    DatabaseReference infoRef;
//    FastDialog fastDialog;
    SharedPreferences sharedPreferences;
    DatabaseReference checkRef;
    SharedPreferences.Editor editor;
    String name,address,nearby,pin,number;
    SharedPreferences checkLocationInfo;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        ConnectivityManager cm =
                (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        if(!isConnected){
            View parentLayout = findViewById(android.R.id.content);
            Snackbar.make(parentLayout, "Please connect to internet :)", Snackbar.LENGTH_LONG)
                    .setAction("CLOSE", view -> {

                    })
                    .setActionTextColor(getResources().getColor(android.R.color.holo_red_light ))
                    .show();
        }
        initialise();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        optForTableBook = findViewById(R.id.checkBoxOptForFoodTable);
        optForTakeaway = findViewById(R.id.checkBoxOptForTakeAway);
        checkLocationInfo = getSharedPreferences("LocationMaps",MODE_PRIVATE);
        checkPermissions();
        sharedPreferences = getSharedPreferences("loginInfo",MODE_PRIVATE);
        SharedPreferences location = getSharedPreferences("LocationMaps",MODE_PRIVATE);
        infoStore = FirebaseFirestore.getInstance();

        infoRef = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(this.sharedPreferences.getString("state", "")).child(this.sharedPreferences.getString("locality","")).child(Objects.requireNonNull(infoAuth.getUid()));
        if(location.contains("location")){
            startActivity(new Intent(Info.this, HomeScreen.class));
            finish();
            return;
        }else if(sharedPreferences.contains("restaurantCreated"))
        {
            startActivity(new Intent(Info.this,UploadRequiredDocuments.class));
            finish();
            return;
        }
        alertDialog.show();
        FirebaseFirestore checkStore = FirebaseFirestore.getInstance();
        checkStore.collection(sharedPreferences.getString("state","")).document("Restaurants").collection(sharedPreferences.getString("locality","")).document(Objects.requireNonNull(infoAuth.getUid()))
                .get().addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        DocumentSnapshot documentSnapshot = task.getResult();
                        if(documentSnapshot.exists()) {
                            Map<String, Object> map = documentSnapshot.getData();
                            editor.putString("TableBookAllowed", (String) map.get("TableBookAllowed"));
                            editor.putString("TakeAwayAllowed", (String) map.get("TableBookAllowed"));
                            editor.apply();

                            SharedPreferences resInfoShared = getSharedPreferences("RestaurantInfo", MODE_PRIVATE);
                            SharedPreferences.Editor editor = resInfoShared.edit();
                            editor.putString("hotelName", (String) map.get("name"));
                            editor.putString("hotelAddress", (String) map.get("address"));
                            editor.putString("hotelNumber", (String) map.get("number"));
                            editor.apply();
                            checkStore.terminate();
                            if (!resCreated) {
                                startActivity(new Intent(Info.this, UploadRequiredDocuments.class));
                                finish();
                            }
                        }
                    }
                });
//        checkRef = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(sharedPreferences.getString("state", "")).child(sharedPreferences.getString("locality",""));
//        checkRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if(snapshot.hasChild(Objects.requireNonNull(infoAuth.getUid()))){
//                    if(snapshot.child(infoAuth.getUid()).child("TableBookAllowed").getValue(String.class).equals("yes")){
//                        editor.putString("TableBookAllowed", "yes");
//                    }
//                    else
//                        editor.putString("TableBookAllowed", "no");
//
//                    if(snapshot.child(infoAuth.getUid()).child("TakeAwayAllowed").getValue(String.class).equals("yes")){
//                        editor.putString("TakeAwayAllowed", "yes");
//                    }
//                    else
//                        editor.putString("TakeAwayAllowed", "no");
//
//                    editor.apply();
//
//                    SharedPreferences resInfoShared = getSharedPreferences("RestaurantInfo", MODE_PRIVATE);
//                            SharedPreferences.Editor editor = resInfoShared.edit();
//                            editor.putString("hotelName", snapshot.child(infoAuth.getUid()).child("name").getValue(String.class));
//                            editor.putString("hotelAddress", snapshot.child(infoAuth.getUid()).child("address").getValue(String.class));
//                            editor.putString("hotelNumber", snapshot.child(infoAuth.getUid()).child("number").getValue(String.class));
//                            editor.apply();
//                    AlertDialog.Builder alert = new AlertDialog.Builder(Info.this);
//                    alert.setTitle("Images");
//                    alert.setMessage("Do you wanna add images of your restaurant!!\nYou can skip this step and add image later");
//                    alert.setPositiveButton("Add Image", (dialogInterface, i) -> {
//                        Intent intent = new Intent(Info.this, AddRestaurantImages.class);
//                        intent.putExtra("state", sharedPreferences.getString("state", ""));
//                        intent.putExtra("locality", sharedPreferences.getString("locality", ""));
//                        alertDialog.dismiss();
//                        startActivity(intent);
//                        finish();
//                    }).setNegativeButton("Skip", (dialogInterface, i) -> {
//                        dialogInterface.dismiss();
//                        infoRef.child("DisplayImage").setValue("");
//                        startActivity(new Intent(Info.this, UploadRequiredDocuments.class));
//                        alertDialog.dismiss();
//                        finish();
//                    }).create();
//
//                    alert.setCancelable(false);
//                    alert.show();
//                    return;
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });


        if(!sharedPreferences.getString("locality","").equals(""))
            subLocal = true;
        if(!sharedPreferences.getString("postalCode","").equals(""))
            pinCode.setText(sharedPreferences.getString("postalCode",""));
        editor = sharedPreferences.edit();


        AlertDialog.Builder builder = new AlertDialog.Builder(Info.this);
        builder.setTitle("Important").setMessage("Are you currently present at restaurant location").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).setNegativeButton("No", (dialogInterface, i) -> {
            AlertDialog.Builder info = new AlertDialog.Builder(Info.this);
            info.setTitle("Location").setMessage("For better accuracy you need to be at restaurant location to proceed further\nDo you wanna manually input data??")
                    .setPositiveButton("Ok", (dialogInterface1, i1) -> startActivityForResult(new Intent(Info.this, MapsActivity2.class),510)).setNegativeButton("No", (dialogInterface12, i12) -> {
                        Toast.makeText(Info.this, "Try again when present at restaurant", Toast.LENGTH_SHORT).show();
                        new Handler().postDelayed(() -> {
                            dialogInterface12.dismiss();
                           finish();
                        },150);
                    }).create();
            info.setCancelable(false);
            info.show();
        }).create();
        builder.setCancelable(false);
        builder.show();
        alertDialog.dismiss();
//        fastDialog = new FastDialogBuilder(Info.this, Type.PROGRESS)
//                .progressText("Checking Database...")
//                .setAnimation(Animations.SLIDE_TOP)
//                .cancelable(false)
//                .create();
//        fastDialog.show();


//        if(sharedPreferences.contains("state")){
//
//            SharedPreferences location = getSharedPreferences("LocationMaps",MODE_PRIVATE);
//            if(!sharedPreferences.getString("locality","").equals("")) {
//                checkRef = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(sharedPreferences.getString("state", "")).child(sharedPreferences.getString("locality",""));
//                checkRef.addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        if (snapshot.hasChild(Objects.requireNonNull(infoAuth.getUid()))) {
//                            SharedPreferences resInfoShared = getSharedPreferences("RestaurantInfo", MODE_PRIVATE);
//                            SharedPreferences.Editor editor = resInfoShared.edit();
//                            editor.putString("hotelName", snapshot.child(infoAuth.getUid()).child("name").getValue(String.class));
//                            editor.putString("hotelAddress", snapshot.child(infoAuth.getUid()).child("address").getValue(String.class));
//                            editor.putString("hotelNumber", snapshot.child(infoAuth.getUid()).child("number").getValue(String.class));
//                            editor.apply();
//
//
//                            if (location.contains("location")) {
//                                clientsLocation.removeLocationUpdates(mLocationCallback);
//                                startActivity(new Intent(Info.this, HomeScreen.class));
//                            } else {
//                                clientsLocation.removeLocationUpdates(mLocationCallback);
//                                startActivity(new Intent(Info.this, UploadRequiredDocuments.class));
//                            }
//
//                            fastDialog.dismiss();
//                            finish();
//                            overridePendingTransition(R.anim.slide_in, R.anim.fade_out);
//                        } else
//                            fastDialog.dismiss();
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });
//            }
//        }else {
//            createLocationRequest();
//            fastDialog.dismiss();
//        }

        proceed.setOnClickListener(view -> {
            if(nameOfRestaurant.length() == 0){
                nameOfRestaurant.requestFocus();
                nameOfRestaurant.setError("Field can't be Empty");
                return;
            } if(AddressOfRestaurant.length() == 0){
                AddressOfRestaurant.requestFocus();
                AddressOfRestaurant.setError("Field cant be Empty");
                return;
            } if(pinCode.length() != 6){
                pinCode.requestFocus();
                pinCode.setError("Invalid PinCode");
                return;
            } if(nearbyPlace.length() == 0){
                nearbyPlace.requestFocus();
                nearbyPlace.setError("Field can't be Empty");
                return;
            } if(contactNumber.length() != 10){
                contactNumber.requestFocus();
                contactNumber.setError("Invalid Number");
                return;
            } if(!codePicker.getSelectedCountryCodeWithPlus().equals("+91")){
                Toast.makeText(Info.this, "This app currently operates only in India", Toast.LENGTH_SHORT).show();
                return;
            }
            name = nameOfRestaurant.getText().toString();
            address = AddressOfRestaurant.getText().toString();
            pin = pinCode.getText().toString();
            nearby = nearbyPlace.getText().toString();
            number = codePicker.getSelectedCountryCodeWithPlus() +  contactNumber.getText().toString() + "";

            createChildForRestaurant();
        });
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkPermissions() {
        if(ContextCompat.checkSelfPermission(Info.this, Manifest.permission.ACCESS_FINE_LOCATION) + ContextCompat.checkSelfPermission(Info.this , Manifest.permission.CAMERA)
                + ContextCompat.checkSelfPermission(Info.this, Manifest.permission.WRITE_EXTERNAL_STORAGE )
                != PackageManager.PERMISSION_GRANTED){
//            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
            requestPermissions(new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.ACCESS_FINE_LOCATION},1);
        }
//            createLocationRequest();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void createChildForRestaurant() {
        if(subLocal) {

            SharedPreferences sharedPreferences = getSharedPreferences("RestaurantInfo", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("hotelName", name);
            editor.putString("hotelAddress", address);
            editor.putString("hotelNumber", number);
            this.editor.putString("restaurantCreated", "yes");
            editor.apply();

            Map<String,String> map = new HashMap<>();
            map.put("name",name);
            map.put("address",address);
            map.put("number",number);
            map.put("nearby",nearby);
            map.put("pin",pin);
            map.put("rating","0");
            map.put("DisplayImage","");
            map.put("totalRate","0");
            map.put("count","0");
            map.put("status","online");
            map.put("acceptingOrders","yes");
            map.put("totalReports","0");

            infoRef.child("name").setValue(name);
            infoRef.child("address").setValue(address);
            infoRef.child("number").setValue(number);
            infoRef.child("nearby").setValue(nearby);
            infoRef.child("pin").setValue(pin);
            infoRef.child("rating").setValue("0");
            infoRef.child("DisplayImage").setValue("");
            infoRef.child("totalRate").setValue("0");
            infoRef.child("count").setValue("0");
            infoRef.child("status").setValue("online");
            infoRef.child("acceptingOrders").setValue("yes");
            infoRef.child("totalReports").setValue("0");



            if (optForTakeaway.isChecked()) {
                map.put("TakeAwayAllowed","yes");
                infoRef.child("TakeAwayAllowed").setValue("yes");
                this.editor.putString("TakeAwayAllowed", "yes");
            } else {
                map.put("TakeAwayAllowed","no");
                infoRef.child("TakeAwayAllowed").setValue("no");
                this.editor.putString("TakeAwayAllowed", "no");
            }

            if (optForTableBook.isChecked()) {
                map.put("TableBookAllowed","yes");
                infoRef.child("TableBookAllowed").setValue("yes");
                this.editor.putString("TableBookAllowed", "yes");
            } else {
                map.put("TableBookAllowed","no");
                infoRef.child("TableBookAllowed").setValue("no");
                this.editor.putString("TableBookAllowed", "no");
            }

            Calendar calendar = Calendar.getInstance();
            this.editor.putString("currentYear",String.valueOf(calendar.get(Calendar.YEAR)));

            if(mapLocations){
                this.editor.putString("state",cityName);
                this.editor.putString("locality",subAdminArea);
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(cityName).child(subAdminArea).child(infoAuth.getUid());
                RestLocation restLocation = new RestLocation(String.valueOf(lon),String.valueOf(lat));
                databaseReference.child("location").setValue(restLocation);
                map.put("location",lon + "/" + lat);
                String hash = GeoFireUtils.getGeoHashForLocation(new GeoLocation(Double.parseDouble(lat), Double.parseDouble(lon)));
                map.put("geoHash",hash);
                SharedPreferences sharedPreferences1 = getSharedPreferences("LocationMaps",MODE_PRIVATE);
                SharedPreferences.Editor editor1 = sharedPreferences1.edit();
                editor1.putString("location","yes");
                editor1.apply();
            }
//            this.editor.putString("firstTimeRestaurant")
            this.editor.apply();
            resCreated = true;
            infoStore.collection(this.sharedPreferences.getString("state","")).document("Restaurants").collection(this.sharedPreferences.getString("locality","")).document(Objects.requireNonNull(infoAuth.getUid())).set(map);

//            clientsLocation.removeLocationUpdates(mLocationCallback);
            AlertDialog.Builder alert = new AlertDialog.Builder(Info.this);
            alert.setTitle("Images");
            alert.setMessage("Do you wanna add images of your restaurant\n(This image will be visible to user)!!\nYou can skip this step and add image later");
            alert.setPositiveButton("Add Image", (dialogInterface, i) -> {
                Intent intent = new Intent(Info.this, AddRestaurantImages.class);
                SharedPreferences sharedPreferences1 = getSharedPreferences("loginInfo",MODE_PRIVATE);
                intent.putExtra("state", sharedPreferences1.getString("state", ""));
                intent.putExtra("locality", sharedPreferences1.getString("locality", ""));
                startActivity(intent);
            }).setNegativeButton("Skip", (dialogInterface, i) -> {
                dialogInterface.dismiss();
                infoRef.child("DisplayImage").setValue("");
                startActivity(new Intent(Info.this, UploadRequiredDocuments.class));
                finish();
            }).create();

            alert.setCancelable(false);
            alert.show();

        }else
            Toast.makeText(this, "Something went wrong we can't find location", Toast.LENGTH_SHORT).show();
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
        builder = new AlertDialog.Builder(Info.this);
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View view = layoutInflater.inflate(R.layout.fastway_custom_loading_dialog,null);
        builder.setView(view);
        alertDialog = builder.create();
//        progressBar = findViewById(R.id.progressBar);

    }

//    private void createLocationRequest() {
//        locationRequest = LocationRequest.create();
//        locationRequest.setInterval(1000);
//        locationRequest.setFastestInterval(1000);
//        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//
//        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
//                .addLocationRequest(locationRequest);
//
//        clientsLocation = LocationServices.getFusedLocationProviderClient(Info.this);
//        clientsLocation.requestLocationUpdates(locationRequest,mLocationCallback, Looper.myLooper());
//        SettingsClient client = LocationServices.getSettingsClient(this);
//        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
//
//        task.addOnCompleteListener(task1 -> {
//            try {
//                LocationSettingsResponse response = task1.getResult(ApiException.class);
//                // All location settings are satisfied. The client can initialize location
//                // requests here.
//
//            } catch (ApiException exception) {
//                switch (exception.getStatusCode()) {
//                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
//                        // Location settings are not satisfied. But could be fixed by showing the
//                        // user a dialog.
//                        try {
//                            // Cast to a resolvable exception.
//                            ResolvableApiException resolvable = (ResolvableApiException) exception;
//                            // Show the dialog by calling startResolutionForResult(),
//                            // and check the result in onActivityResult().
//                            resolvable.startResolutionForResult(
//                                    Info.this,
//                                    101);
//                        } catch (IntentSender.SendIntentException e) {
//                            // Ignore the error.
//                        } catch (ClassCastException e) {
//                            // Ignore, should be an impossible error.
//                        }
//                        break;
//                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
//                        // Location settings are not satisfied. However, we have no way to fix the
//                        // settings so we won't show the dialog.
//                        break;
//                }
//            }
//        });
//    }
//
//    private final LocationCallback mLocationCallback = new LocationCallback() {
//
//        @Override
//        public void onLocationResult(LocationResult locationResult) {
//            Location mLastLocation = locationResult.getLastLocation();
//            longi = mLastLocation.getLongitude();
//            lati = mLastLocation.getLatitude();
//            editor.putString("longi",String.valueOf(longi));
//            editor.putString("lati",String.valueOf(lati));
//            Geocoder geocoder = new Geocoder(Info.this, Locale.getDefault());
//            List<Address> addresses = null;
//            String cityName;
//            try {
//                addresses = geocoder.getFromLocation(lati, longi, 1);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            cityName = addresses.get(0).getLocality();
//
//            editor.putString("state",cityName);
//            editor.apply();
//            Log.i("infoses", cityName + " " );
//            Log.i("locationes",longi + " " + lati);
//
//            SharedPreferences location = getSharedPreferences("LocationMaps",MODE_PRIVATE);
//            checkRef = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(sharedPreferences.getString("state",""));
//            checkRef.addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                    if(snapshot.hasChild(Objects.requireNonNull(infoAuth.getUid()))){
//                        if(location.contains("location")){
//                            startActivity(new Intent(Info.this,HomeScreen.class));
//                        }else
//                            startActivity(new Intent(Info.this,UploadRequiredDocuments.class));
//
//                        clientsLocation.removeLocationUpdates(mLocationCallback);
//                        finish();
//
//                        overridePendingTransition(R.anim.slide_in, R.anim.fade_out);
//                    }else
//                        fastDialog.dismiss();
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError error) {
//
//                }
//            });
//        }
//    };
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void CheckPermission() {
        if(ContextCompat.checkSelfPermission(Info.this, Manifest.permission.CAMERA) + ContextCompat.checkSelfPermission(Info.this
                ,Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }else
        {
            AlertDialog.Builder newDialog = new AlertDialog.Builder(Info.this);
            newDialog.setTitle("Choose One Option");
            newDialog.setMessage("Choose one option from below")
                    .setPositiveButton("Choose From Gallery", (dialogInterface, i) -> {
                        dialogInterface.dismiss();
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
                    }).setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss()).create();

            newDialog.setCancelable(false);
            newDialog.show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 1){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                AlertDialog.Builder newDialog = new AlertDialog.Builder(Info.this);
                newDialog.setTitle("Choose One Option");
                newDialog.setMessage("Choose one option from below")
                        .setPositiveButton("Choose From Gallery", (dialogInterface, i) -> {

                        }).setNegativeButton("Cancel", (dialogInterface, i) -> {
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); //IMAGE CAPTURE CODE
                            startActivityForResult(intent, 20);
                        }).create();

                newDialog.setCancelable(false);
                newDialog.show();
            }else{
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 20 && resultCode == RESULT_OK && data != null){
             loading = new FastDialogBuilder(Info.this,Type.PROGRESS)
                    .setAnimation(Animations.SLIDE_BOTTOM)
                    .progressText("Uploading Image.....")
                    .create();

            loading.show();

            bitmap = (Bitmap) data.getExtras().get("data");
            File filepath = Environment.getExternalStorageDirectory();
            File dir = new File(filepath.getAbsolutePath());
            dir.mkdir();
            file = new File(dir, "DisplayImage" + ".jpg");
            try {
                Log.i("file stored","yes");
                outputStream = new FileOutputStream(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            try {
                outputStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
                loading.dismiss();
            }
            try {
                StorageReference reference = storageReference.child(infoAuth.getUid() + "/" + "image" + "/"  + "DisplayImage");
                reference.putFile(Uri.fromFile(file)).addOnCompleteListener(task -> {
                    StorageReference reference1 = storageReference.child(infoAuth.getUid() + "/" + "image" + "/"  + "DisplayImage");
                    reference1.getDownloadUrl().addOnSuccessListener(uri -> {
                        Toast.makeText(Info.this, "Upload Complete and image saved in phone successfully", Toast.LENGTH_SHORT).show();
                        loading.dismiss();
                        DatabaseReference dish = FirebaseDatabase.getInstance().getReference().getRoot();
                        dish.child("Restaurants").child(sharedPreferences.getString("state","")).child(Objects.requireNonNull(infoAuth.getUid())).child("DisplayImage").setValue(uri + "");
                        startActivity(new Intent(Info.this, UploadRequiredDocuments.class));
                        finish();
                    });

                }).addOnFailureListener(e -> {
                    Toast.makeText(Info.this,
                            "Something went wrong", Toast.LENGTH_SHORT).show();
                    loading.dismiss();
                });
            }catch (Exception e){
                Toast.makeText(Info.this, ""+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                loading.dismiss();
            }

        }else if(requestCode == 1 && resultCode == RESULT_OK && data != null){
            loading = new FastDialogBuilder(Info.this,Type.PROGRESS)
                    .setAnimation(Animations.SLIDE_BOTTOM)
                    .progressText("Uploading Image.....")
                    .create();

            loading.show();
            filePath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),filePath);
                uploadImage();
            } catch (IOException e) {
                e.printStackTrace();
                loading.dismiss();
            }


        }

        if(requestCode == 510 && resultCode == RESULT_OK){
            if(!data.getStringExtra("locality").equals("")) {
                pinCode.setText(data.getStringExtra("pin"));
                subLocal = true;
                lon = data.getStringExtra("lon");
                lat = data.getStringExtra("lat");
                mapLocations = true;
                cityName = data.getStringExtra("state");
                subAdminArea = data.getStringExtra("locality");
                infoRef = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(data.getStringExtra("state")).child(data.getStringExtra("locality")).child(Objects.requireNonNull(infoAuth.getUid()));
            }
        }

        if(requestCode == 510 && resultCode == RESULT_CANCELED){
            Toast.makeText(this, "Try again later", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void uploadImage() {
        if(filePath != null){
            StorageReference reference = storageReference.child(infoAuth.getUid() + "/" + "image" + "/"  + "DisplayImage");
            reference.putFile(filePath).addOnSuccessListener(taskSnapshot -> {
                StorageReference reference1 = storageReference.child(infoAuth.getUid() + "/" + "image" + "/"  + "DisplayImage");
                reference1.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(@NonNull Uri uri) {
                        Toast.makeText(Info.this, "Upload Complete and image saved in phone successfully", Toast.LENGTH_SHORT).show();
                        loading.dismiss();
                        DatabaseReference dish = FirebaseDatabase.getInstance().getReference().getRoot();
                        dish.child("Restaurants").child(sharedPreferences.getString("state","")).child(Objects.requireNonNull(infoAuth.getUid())).child("DisplayImage").setValue(uri + "");
                        startActivity(new Intent(Info.this, UploadRequiredDocuments.class));
                        finish();
                    }
                });
            }).addOnFailureListener(e -> loading.dismiss());
        }else
            loading.dismiss();
    }
}