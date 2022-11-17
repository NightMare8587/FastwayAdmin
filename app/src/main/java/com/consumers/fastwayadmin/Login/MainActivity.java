package com.consumers.fastwayadmin.Login;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.consumers.fastwayadmin.GMailSender;
import com.consumers.fastwayadmin.HomeScreen.HomeScreen;
import com.consumers.fastwayadmin.Info.Info;
import com.consumers.fastwayadmin.R;
import com.developer.kalert.KAlertDialog;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
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
import com.google.android.gms.location.Priority;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hbb20.CountryCodePicker;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import karpuzoglu.enes.com.fastdialog.Animations;
import karpuzoglu.enes.com.fastdialog.FastDialog;
import karpuzoglu.enes.com.fastdialog.FastDialogBuilder;
import karpuzoglu.enes.com.fastdialog.Type;
public class MainActivity extends AppCompatActivity {
    FirebaseAuth loginAuth;
    String reciverMailID;
    double longi,lati;
    LocationRequest locationRequest;
    GoogleSignInOptions gso;
    SharedPreferences getBanInfo;
    FusedLocationProviderClient clientsLocation;
    FastDialog verifyCodeDialog,fastDialog;
    GoogleSignInAccount account;
    GoogleSignInClient client;
    String verId;
    FirebaseFirestore db =  FirebaseFirestore.getInstance();
    SharedPreferences loginInfo;
    SharedPreferences.Editor editor;
    ImageView signInButton;
    PhoneAuthProvider.ForceResendingToken myToken;
    EditText fullName,emailAddress,phoneNumber;
    CountryCodePicker ccp;
    Button startVerification;
    DatabaseReference reference;
    String name,email,number;
    TextView readTAndC;
    PhoneAuthCredential credential;
    FirebaseUser currentUser;
    GMailSender sender;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialise();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        checkPermissions();
        readTAndC = findViewById(R.id.textClicktoReadTermsAndCOnditions);
        readTAndC.setOnClickListener(click -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.websitepolicies.com/policies/view/CpwDZziF"));
            startActivity(browserIntent);
        });
        SharedPreferences stopServices = getSharedPreferences("Stop Services", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor5 = stopServices.edit();
        editor5.putString("online","true");
        editor5.apply();
        SharedPreferences sharedPreferences1 = getSharedPreferences("After Logout",MODE_PRIVATE);
        SharedPreferences.Editor editor1 = sharedPreferences1.edit();
        editor1.putString("logout","no");
        editor1.apply();
        // checking is user is currently logged in
        if(currentUser != null){
            startActivity(new Intent(getApplicationContext(),Info.class));
            clientsLocation.removeLocationUpdates(mLocationCallback);
            finish();
        }

        //saving user login info
        loginInfo = getSharedPreferences("loginInfo",MODE_PRIVATE);
        editor = loginInfo.edit();

        //google sign in button
        signInButton.setOnClickListener(view -> {
            Intent signIn = client.getSignInIntent();
            startActivityForResult(signIn,3);
        });

        // start phone auth verification
        startVerification.setOnClickListener(view -> {
            getBanInfo = getSharedPreferences("loginInfo",MODE_PRIVATE);
            if(getBanInfo.contains("banTime")){
                long value = Long.parseLong(getBanInfo.getString("banTime",""));
                long currentTime = Long.parseLong(String.valueOf(System.currentTimeMillis()));

                if(currentTime > value){
                    getBanInfo.edit().remove("banTime").apply();
                }else{
                    Toast.makeText(MainActivity.this, "Wait till ban is removed " + TimeUnit.MILLISECONDS.toMinutes(value - currentTime) + " minutes Remaining", Toast.LENGTH_SHORT).show();

                    return;
                }
            }
            if(fullName.length() == 0){
                fullName.requestFocus();
                fullName.setError("Field can't be Empty");
                return;
            }if(emailAddress.length() == 0){
                emailAddress.requestFocus();
                emailAddress.setError("Field can't be Empty");
                return;
            }if(phoneNumber.length() != 10){
                phoneNumber.requestFocus();
                phoneNumber.setError("Enter valid number");
                return;
            }if(!ccp.getSelectedCountryCodeWithPlus().equals("+91")){
                Toast.makeText(MainActivity.this, "This app currently operates only in India", Toast.LENGTH_SHORT).show();
                return;
            }


            if(emailAddress.getText().toString().equals("foodinetest858784@gmail.com") && phoneNumber.getText().toString().equals("1234567890")){
                FastDialog fastDialog = new FastDialogBuilder(MainActivity.this,Type.PROGRESS)
                        .setAnimation(Animations.FADE_IN)
                        .progressText("Logging in using test... Please Wait")
                        .create();
                fastDialog.show();
                DatabaseAdmin user = new DatabaseAdmin(phoneNumber.getText().toString(),fullName.getText().toString(),emailAddress.getText().toString());
                editor.putString("name",fullName.getText().toString());
                editor.putString("email",emailAddress.getText().toString());
                editor.putString("number",phoneNumber.getText().toString());
                editor.putString("state","TestRes");
                editor.putString("locality","Test");
                editor.apply();
                loginAuth.signInWithEmailAndPassword("foodinetest858784@gmail.com","FoodineTest858784").addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Admin");
                        databaseReference.child("8AzmseJgrNcYL7GcH05B3mYcDfp2").setValue(user);
                        databaseReference.child("8AzmseJgrNcYL7GcH05B3mYcDfp2").child("Restaurant Documents").child("verified").setValue("yes");
                        fastDialog.dismiss();
                        editor.putString("testAdmin","8AzmseJgrNcYL7GcH05B3mYcDfp2");
                        editor.apply();
                        AsyncTask.execute(() -> {
                            DatabaseReference addResTest = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child("TestRes").child("Test").child("8AzmseJgrNcYL7GcH05B3mYcDfp2");
                            addResTest.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(snapshot.exists()){

                                    }else
                                    {
                                        addResTest.child("DisplayImage").setValue("https://firebasestorage.googleapis.com/v0/b/fastway-e3c4a.appspot.com/o/BKkZjAAB9fQmleexouAb2zSRtQm2%2FresImages%2FDisplayImage?alt=media&token=02bd9e2d-fd2f-4570-97f5-4213c5ad4a87");
                                        addResTest.child("TakeAwayAllowed").setValue("yes");
                                        addResTest.child("acceptingOrders").setValue("yes");
                                        addResTest.child("address").setValue("1234qwert");
                                        addResTest.child("name").setValue("Chaudari Dhaba");
                                        addResTest.child("number").setValue("1234567892");
                                        addResTest.child("nearby").setValue("near ratan park");
                                        addResTest.child("status").setValue("online");
                                        addResTest.child("pin").setValue("110015");
                                        addResTest.child("totalRate").setValue("0");
                                        addResTest.child("totalReports").setValue("0");
                                        addResTest.child("location").child("lat").setValue("28.647849374299167");
                                        addResTest.child("location").child("lon").setValue("77.13484514504671");
                                        addResTest.child("count").setValue("1");
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        });
                        Toast.makeText(MainActivity.this, "Test Login Successfully", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(),HomeScreen.class));
                        finish();
                    }else {
                        fastDialog.dismiss();
                        Toast.makeText(MainActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                });
                return;
            }

            new KAlertDialog(MainActivity.this,KAlertDialog.WARNING_TYPE)
                    .setContentText("Do you sure wanna continue with this number")
                    .setTitleText("Confirmation")
                    .setConfirmText("Yes, Send OTP")
                    .setCancelText("No, Wait")
                    .setConfirmClickListener(kAlertDialog -> {
                        name = fullName.getText().toString();
                        email = emailAddress.getText().toString();
                        number = ccp.getSelectedCountryCodeWithPlus() + phoneNumber.getText().toString() + "";
                        fastDialog = new FastDialogBuilder(MainActivity.this, Type.PROGRESS)
                                .progressText("Sending OTP Please wait")
                                .setAnimation(Animations.FADE_IN)
                                .create();
                        fastDialog.show();
                        startVerification.setVisibility(View.INVISIBLE);
                        startPhoneNumberVerification(number);

                        kAlertDialog.dismissWithAnimation();
                    }).setCancelClickListener(KAlertDialog::dismissWithAnimation).show();

        });

    }


    private void createLocationRequest() {
         locationRequest = LocationRequest.create();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(Priority.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        clientsLocation = LocationServices.getFusedLocationProviderClient(MainActivity.this);
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
                                    MainActivity.this,
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
            Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
            List<Address> addresses = null;
            String cityName;
            String subAdmin;
            String postalCode;

            try {
                addresses = geocoder.getFromLocation(lati, longi, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            cityName = addresses.get(0).getAdminArea();
            subAdmin = addresses.get(0).getAdminArea();


            if(addresses.get(0).getPostalCode() != null)
                postalCode = addresses.get(0).getPostalCode();
            else
                postalCode = "";
            editor.putString("state",cityName);
            editor.putString("locality",subAdmin);
            editor.putString("postalCode",postalCode);
            editor.apply();
            Log.i("info", cityName + " " + subAdmin);
            Log.i("location",longi + " " + lati);

            clientsLocation.removeLocationUpdates(mLocationCallback);
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkPermissions() {
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) + ContextCompat.checkSelfPermission(MainActivity.this , Manifest.permission.CAMERA)
                + ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE )
                 != PackageManager.PERMISSION_GRANTED){
//            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
            requestPermissions(new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.ACCESS_FINE_LOCATION},1);
        }else
            createLocationRequest();
    }

    private void startPhoneNumberVerification(String number) {
        PhoneAuthOptions options =  PhoneAuthOptions.newBuilder(loginAuth)
                .setPhoneNumber(number)
                .setActivity(this)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        Toast.makeText(MainActivity.this, "Code sent", Toast.LENGTH_SHORT).show();
                        credential = phoneAuthCredential;
                        signInWithPhoneAuthCredentials(phoneAuthCredential);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        Toast.makeText(MainActivity.this, e.getLocalizedMessage()+"", Toast.LENGTH_SHORT).show();
                        startVerification.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        verId = s;
                        myToken = forceResendingToken;
                        fastDialog.dismiss();
                        verifyCodeDialog.show();
                    }
                }).build();

        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void signInWithPhoneAuthCredentials(PhoneAuthCredential phoneAuthCredential) {
        loginAuth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(this, task -> {
            if(task.isSuccessful()){

                Map<String,Object> map = new HashMap<>();
                map.put("name",name);
                map.put("email",email);
                reciverMailID = email;
                editor.putString("name",name);
                editor.putString("email",email);
                editor.apply();
//                    wait.setVisibility(View.INVISIBLE);
                Toast.makeText(MainActivity.this, "Login Successfully", Toast.LENGTH_SHORT).show();
                DatabaseAdmin user = new DatabaseAdmin(name,email,number);
                db.collection(Objects.requireNonNull(loginAuth.getUid())).document("info")
                        .set(map).addOnSuccessListener(aVoid -> Toast.makeText(MainActivity.this, "Data Uploaded Successfully", Toast.LENGTH_SHORT).show()).addOnFailureListener(e -> Toast.makeText(MainActivity.this, e.getLocalizedMessage()+"", Toast.LENGTH_SHORT).show());
                clientsLocation.removeLocationUpdates(mLocationCallback);
                reference.child("Admin").child(loginAuth.getUid()+"").setValue(user);
                startActivity(new Intent(MainActivity.this, Info.class));
                finish();
            }
        });
    }

    private void initialise() {
        loginAuth = FirebaseAuth.getInstance();
        fullName = findViewById(R.id.nameLogin);
        emailAddress = findViewById(R.id.emailLogin);
        phoneNumber = findViewById(R.id.phoneNumber);
        ccp = findViewById(R.id.ccp);
        startVerification = findViewById(R.id.startVerification);
        reference = FirebaseDatabase.getInstance().getReference().getRoot();
        signInButton = findViewById(R.id.signInButton);

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("612835438715-9560p1fhto5ber96usm1gjt8fbi6k4da.apps.googleusercontent.com")
                .requestEmail()
                .requestProfile()
                .build();

        client = GoogleSignIn.getClient(MainActivity.this,gso);

        verifyCodeDialog = new FastDialogBuilder(MainActivity.this,Type.DIALOG)
                .setTitleText("Verify")
                .setHint("Enter 6 digit Code")
                .positiveText("Verify")
                .cancelable(false)
                .negativeText("Cancel")
                .create();

        verifyCodeDialog.positiveClickListener(view -> {
            if(verifyCodeDialog.getInputText().length() <= 5){
                Toast.makeText(MainActivity.this, "Enter Valid Code", Toast.LENGTH_SHORT).show();
            }else{
                credential = PhoneAuthProvider.getCredential(verId,verifyCodeDialog.getInputText());

                loginAuth.signInWithCredential(credential).addOnCompleteListener(MainActivity.this, task -> {
                    if(task.isSuccessful()){
                        Toast.makeText(MainActivity.this, "Login Successfully", Toast.LENGTH_SHORT).show();
                        DatabaseAdmin user = new DatabaseAdmin(name,email,number);
                        editor.putString("name",name);
                        editor.putString("email",email);

                        editor.apply();
                        verifyCodeDialog.dismiss();
                        loginAuth = FirebaseAuth.getInstance();
//                                reference.child("Admin").child(Objects.requireNonNull(loginAuth.getUid())).setValue(user);
                        AsyncTask.execute(() -> reference.child("Admin").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(!snapshot.hasChild(Objects.requireNonNull(loginAuth.getUid()))){
                                    sendEmailFunction(email);
                                    reference.child("Admin").child(Objects.requireNonNull(loginAuth.getUid())).setValue(user);
                                    reference.child("Admin").child(loginAuth.getUid()).child("registrationDate").setValue(System.currentTimeMillis() + "");
                                    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                                    firestore.collection("Admin").document(loginAuth.getUid()).set(user);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        }));
                        clientsLocation.removeLocationUpdates(mLocationCallback);
                        startActivity(new Intent(getApplicationContext(),Info.class));
                        finish();
                    }else{
                        Toast.makeText(MainActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        verifyCodeDialog.negativeClickListener(view -> {
            Toast.makeText(MainActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
            startVerification.setVisibility(View.VISIBLE);
            verifyCodeDialog.dismiss();

            SharedPreferences sharedPreferences = getSharedPreferences("loginInfo",MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            long nowPlus5Minutes = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(5);
            editor.putString("banTime",String.valueOf(nowPlus5Minutes));
            editor.apply();
        });



    }

    private void sendEmailFunction(String email) {
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
            String emailSendingURL = "https://intercellular-stabi.000webhostapp.com/email/adminEmail/sendEmail.php";
            StringRequest stringRequest = new StringRequest(Request.Method.POST, emailSendingURL, response -> {

            }, error -> {

            }){
                @Nullable
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String> params = new HashMap<>();
                    params.put("to",email);
                    return params;
                }
            };
            requestQueue.add(stringRequest);
        }
        catch (Exception ex) {
            Log.d("exceptionsending", ex.toString());
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
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Important");
                builder.setMessage("Location is required for this app to work properly");
                builder.setPositiveButton("Allow", (dialogInterface, i) -> checkPermissions()).setNegativeButton("No", (dialogInterface, i) -> dialogInterface.dismiss()).create();

                builder.show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);
        if (requestCode == 101) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    // All required changes were successfully made
//                    Toast.makeText(MainActivity.this, states.isLocationPresent() + "", Toast.LENGTH_SHORT).show();
//                    createLocationRequest();
                    break;
                case Activity.RESULT_CANCELED:
                    // The user was asked to change settings, but chose not to
                    Toast.makeText(MainActivity.this, "Canceled", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }else if(requestCode == 3){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
             account = completedTask.getResult(ApiException.class);
            // Signed in successfully, show authenticated UI.
            Toast.makeText(this, "Sign In", Toast.LENGTH_SHORT).show();
            createFirebaseAuthID(account.getIdToken());
            getSignInInformation();

        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("TAG", "signInResult:failed code=" + e.getStatusCode());
            Toast.makeText(this, e.getLocalizedMessage()+"", Toast.LENGTH_SHORT).show();
        }
    }

    private void createFirebaseAuthID(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        Log.i("credentials",String.valueOf(credential));
        loginAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        loginAuth = FirebaseAuth.getInstance();
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("TAG", "signInWithCredential:success");
                        FirebaseUser user = loginAuth.getCurrentUser();
                        assert user != null;
                        loginAuth.updateCurrentUser(user);
                        AsyncTask.execute(new Runnable() {
                            @Override
                            public void run() {
                                reference.child("Admin").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if(!snapshot.hasChild(Objects.requireNonNull(loginAuth.getUid()))){
                                            sendEmailFunction(account.getEmail());
                                            GoogleSignInDB googleSignInDB = new GoogleSignInDB(account.getDisplayName(),account.getEmail());
                                            reference = FirebaseDatabase.getInstance().getReference().getRoot().child("Admin").child(user.getUid());
                                            reference.setValue(googleSignInDB);
                                            Map<String,String> map = new HashMap<>();
                                            map.put("name",account.getDisplayName());
                                            map.put("email",account.getEmail());
                                            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                                            firestore.collection("Admin").document(loginAuth.getUid()).set(map);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        });

                        clientsLocation.removeLocationUpdates(mLocationCallback);
                        startActivity(new Intent(MainActivity.this,Info.class));
                        finish();
//                            updateUI(user);
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("TAG", "signInWithCredential:failure", task.getException());
//                            updateUI(null);
                    }
                });
    }

    private void getSignInInformation() {
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        if (acct != null) {
            String personName = acct.getDisplayName();
            String personFamilyName = acct.getFamilyName();
            String personEmail = acct.getEmail();
            reciverMailID = personEmail;
            editor.putString("email",personEmail);
            editor.putString("name",personName);
            editor.putString("storeInDevice","yes");
            editor.apply();
            Log.i("info",personName+ " " + personEmail + " " + personFamilyName);
        }
    }
    class MyAsyncClass extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {

            super.onPreExecute();

        }

        @Override

        protected Void doInBackground(Void... mApi) {
            try {

                // Add subject, Body, your mail Id, and receiver mail Id.
                Log.d("send", "done");
            }
            catch (Exception ex) {
                Log.d("exceptionsending", ex.toString());
            }
            return null;
        }

        @Override

        protected void onPostExecute(Void result) {

            super.onPostExecute(result);
            Toast.makeText(MainActivity.this, "mail send", Toast.LENGTH_SHORT).show();

        }
    }
}