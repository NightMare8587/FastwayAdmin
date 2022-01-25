package com.consumers.fastwayadmin.Login;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
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
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.consumers.fastwayadmin.GMailSender;
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
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
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
import karpuzoglu.enes.com.fastdialog.NegativeClick;
import karpuzoglu.enes.com.fastdialog.PositiveClick;
import karpuzoglu.enes.com.fastdialog.Type;


public class MainActivity extends AppCompatActivity {
    String subject = "Welcome to Fastway";
    String body = "Haha you nigga";
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
    protected boolean isProgressShowing = false;
    String verId;
    FirebaseFirestore db =  FirebaseFirestore.getInstance();
    SharedPreferences loginInfo;
    SharedPreferences.Editor editor;
    ViewGroup group;
     ImageView signInButton;
//    ProgressBar wait;
    PhoneAuthProvider.ForceResendingToken myToken;
    EditText fullName,emailAddress,phoneNumber;
    CountryCodePicker ccp;
    Button startVerification;
    DatabaseReference reference;
    String name,email,number;
    PhoneAuthCredential credential;
    FirebaseUser currentUser;

    GMailSender sender;
    String emailOfSender = "fastway85187@gmail.com";
    String passOfSender = "@Ploya8587";

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialise();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

//        Sprite bounce = new Wave();
//        spinKitView.setColor(R.color.teal_200);
//        spinKitView.setIndeterminateDrawable(bounce);
        checkPermissions();
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
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signIn = client.getSignInIntent();
                startActivityForResult(signIn,3);
            }
        });

        // start phone auth verification
        startVerification.setOnClickListener(new View.OnClickListener() {

            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
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

                new KAlertDialog(MainActivity.this,KAlertDialog.WARNING_TYPE)
                        .setContentText("Do you sure wanna continue with this number")
                        .setTitleText("Confirmation")
                        .setConfirmText("Yes, Send OTP")
                        .setCancelText("No, Wait")
                        .setConfirmClickListener(new KAlertDialog.KAlertClickListener() {
                            @Override
                            public void onClick(KAlertDialog kAlertDialog) {
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
                            }
                        }).setCancelClickListener(new KAlertDialog.KAlertClickListener() {
                    @Override
                    public void onClick(KAlertDialog kAlertDialog) {
                        kAlertDialog.dismissWithAnimation();
                    }
                }).show();

//                CFAlertDialog.Builder builder = new CFAlertDialog.Builder(MainActivity.this)
//                        .setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT)
//                        .setTitle("Confirmation")
//                        .setMessage("Do you wanna continue with this number " + phoneNumber.getText().toString())
//                        .addButton("Yes", Color.BLACK,Color.LTGRAY, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED,
//                                ((dialogInterface, i) -> {
//                                    name = fullName.getText().toString();
//                                    email = emailAddress.getText().toString();
//                                    number = ccp.getSelectedCountryCodeWithPlus() + phoneNumber.getText().toString() + "";
//
//                                    startVerification.setVisibility(View.INVISIBLE);
//                                    startPhoneNumberVerification(number);
//
//                                    dialogInterface.dismiss();
//                                }))
//                        .addButton("No",Color.BLACK,Color.YELLOW, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED
//                        ,((dialogInterface, i) -> {
//                                    phoneNumber.requestFocus();
//                                    dialogInterface.dismiss();
//                                }));
//
//                builder.show();

            }
        });

    }


    private void createLocationRequest() {
         locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(50000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        clientsLocation = LocationServices.getFusedLocationProviderClient(MainActivity.this);
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
            Log.i("info", cityName + " " );
            Log.i("location",longi + " " + lati);

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
        loginAuth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
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
                            .set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(MainActivity.this, "Data Uploaded Successfully", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MainActivity.this, e.getLocalizedMessage()+"", Toast.LENGTH_SHORT).show();
                        }
                    });
                    clientsLocation.removeLocationUpdates(mLocationCallback);
                    reference.child("Admin").child(loginAuth.getUid()+"").setValue(user);
                    startActivity(new Intent(MainActivity.this, Info.class));
                    finish();
                }
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
                .requestIdToken("765176451275-u1ati379eiinc9b21472ml968chmlsqh.apps.googleusercontent.com")
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

        verifyCodeDialog.positiveClickListener(new PositiveClick() {
            @Override
            public void onClick(View view) {
                if(verifyCodeDialog.getInputText().length() <= 5){
                    Toast.makeText(MainActivity.this, "Enter Valid Code", Toast.LENGTH_SHORT).show();
                    return;
                }else{
                    credential = PhoneAuthProvider.getCredential(verId,verifyCodeDialog.getInputText());

                    loginAuth.signInWithCredential(credential).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(MainActivity.this, "Login Successfully", Toast.LENGTH_SHORT).show();
                                DatabaseAdmin user = new DatabaseAdmin(name,email,number);
                                editor.putString("name",name);
                                editor.putString("email",email);
                                editor.apply();
                                verifyCodeDialog.dismiss();
                                loginAuth = FirebaseAuth.getInstance();
//                                reference.child("Admin").child(Objects.requireNonNull(loginAuth.getUid())).setValue(user);
                                reference.child("Admin").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if(!snapshot.hasChild(Objects.requireNonNull(loginAuth.getUid()))){
                                            sender = new GMailSender(emailOfSender,passOfSender);
                                            new MyAsyncClass().execute();
                                            reference.child("Admin").child(Objects.requireNonNull(loginAuth.getUid())).setValue(user);
                                            reference.child("Admin").child(loginAuth.getUid()).child("registrationDate").setValue(System.currentTimeMillis() + "");
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                                clientsLocation.removeLocationUpdates(mLocationCallback);
                                startActivity(new Intent(getApplicationContext(),Info.class));
                                finish();
                            }else{
                                Toast.makeText(MainActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        verifyCodeDialog.negativeClickListener(new NegativeClick() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
                startVerification.setVisibility(View.VISIBLE);
                verifyCodeDialog.dismiss();

                SharedPreferences sharedPreferences = getSharedPreferences("loginInfo",MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                long nowPlus5Minutes = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(5);
                editor.putString("banTime",String.valueOf(nowPlus5Minutes));
                editor.apply();
            }
        });



    }
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
                builder.setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        checkPermissions();
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
            sender = new GMailSender(emailOfSender,passOfSender);
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
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            loginAuth = FirebaseAuth.getInstance();
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "signInWithCredential:success");
                            FirebaseUser user = loginAuth.getCurrentUser();
                            assert user != null;
                            loginAuth.updateCurrentUser(user);
                            reference.child("Admin").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(!snapshot.hasChild(Objects.requireNonNull(loginAuth.getUid()))){
                                        sender = new GMailSender(emailOfSender,passOfSender);
                                        new MyAsyncClass().execute();
                                        GoogleSignInDB googleSignInDB = new GoogleSignInDB(account.getDisplayName(),account.getEmail());
                                        reference = FirebaseDatabase.getInstance().getReference().getRoot().child("Admin").child(user.getUid());
                                        reference.setValue(googleSignInDB);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

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
                    }
                });
    }

    private void getSignInInformation() {
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        if (acct != null) {
            String personName = acct.getDisplayName();
            String personGivenName = acct.getGivenName();
            String personFamilyName = acct.getFamilyName();
            String personEmail = acct.getEmail();
            reciverMailID = personEmail;
            String personId = acct.getId();
            Uri personPhoto = acct.getPhotoUrl();
            editor.putString("email",personEmail);
            editor.putString("name",personName);
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
                sender.sendMail(subject, body, emailOfSender, reciverMailID);
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