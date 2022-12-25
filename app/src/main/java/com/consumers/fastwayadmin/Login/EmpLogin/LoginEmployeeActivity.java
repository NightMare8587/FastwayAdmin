package com.consumers.fastwayadmin.Login.EmpLogin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.consumers.fastwayadmin.Info.Info;
import com.consumers.fastwayadmin.Login.DatabaseAdmin;
import com.consumers.fastwayadmin.Login.MainActivity;
import com.consumers.fastwayadmin.R;
import com.developer.kalert.KAlertDialog;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
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

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import karpuzoglu.enes.com.fastdialog.Animations;
import karpuzoglu.enes.com.fastdialog.FastDialog;
import karpuzoglu.enes.com.fastdialog.FastDialogBuilder;
import karpuzoglu.enes.com.fastdialog.Type;

public class LoginEmployeeActivity extends AppCompatActivity {
    TextView login;
    EditText name,phone;
    Button startVerification;
    SharedPreferences getBanInfo;
    FastDialog verifyCodeDialog,fastDialog;
    SharedPreferences loginInfo;
    SharedPreferences.Editor editor;
    PhoneAuthCredential credential;
    CountryCodePicker ccp;
    PhoneAuthProvider.ForceResendingToken myToken;
    DatabaseReference reference;
    String verId;
    String names,phones;
    EditText phoneNumber;
    FirebaseAuth loginAuth;
    TextView termsAndCon;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_employee);
        login = findViewById(R.id.loginAsAdminTextViewMain);
        loginAuth = FirebaseAuth.getInstance();
        name = findViewById(R.id.nameLoginEmp);
//        emailAddress = findViewById(R.id.emailLogin);
        phoneNumber = findViewById(R.id.phoneNumberEmp);
        loginInfo = getSharedPreferences("loginInfo",MODE_PRIVATE);
        editor = loginInfo.edit();
        ccp = findViewById(R.id.ccpEmp);
        startVerification = findViewById(R.id.startVerificationEmp);
        termsAndCon = findViewById(R.id.textClicktoReadTermsAndCOnditionsEmp);
        reference = FirebaseDatabase.getInstance().getReference().getRoot();
        login.setOnClickListener(click -> {
            startActivity(new Intent(LoginEmployeeActivity.this, MainActivity.class));
            finish();
        });

        verifyCodeDialog = new FastDialogBuilder(LoginEmployeeActivity.this, Type.DIALOG)
                .setTitleText("Verify")
                .setHint("Enter 6 digit Code")
                .positiveText("Verify")
                .cancelable(false)
                .negativeText("Cancel")
                .create();

        verifyCodeDialog.positiveClickListener(view -> {
            if(verifyCodeDialog.getInputText().length() <= 5){
                Toast.makeText(LoginEmployeeActivity.this, "Enter Valid Code", Toast.LENGTH_SHORT).show();
            }else{
                credential = PhoneAuthProvider.getCredential(verId,verifyCodeDialog.getInputText());

                loginAuth.signInWithCredential(credential).addOnCompleteListener(LoginEmployeeActivity.this, task -> {
                    if(task.isSuccessful()){
                        Toast.makeText(LoginEmployeeActivity.this, "Login Successfully", Toast.LENGTH_SHORT).show();
                        DatabaseAdmin user = new DatabaseAdmin(names,"",phones);
                        editor.putString("name",names);
                        editor.putString("email","");

                        editor.apply();
                        verifyCodeDialog.dismiss();
                        loginAuth = FirebaseAuth.getInstance();
//                                reference.child("Admin").child(Objects.requireNonNull(loginAuth.getUid())).setValue(user);
                        AsyncTask.execute(() -> reference.child("EmployeeDB").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(!snapshot.hasChild(Objects.requireNonNull(loginAuth.getUid()))){
//                                    sendEmailFunction(email);
                                    reference.child("EmployeeDB").child(Objects.requireNonNull(loginAuth.getUid())).setValue(user);

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        }));
//                        clientsLocation.removeLocationUpdates(mLocationCallback);
                        editor.putString("loginAsEmployee","yes");
                        editor.apply();
                        startActivity(new Intent(getApplicationContext(), ApplyForRestaurantAndDocs.class));
                        finish();
                    }else{
                        Toast.makeText(LoginEmployeeActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        verifyCodeDialog.negativeClickListener(view -> {
            Toast.makeText(LoginEmployeeActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
            startVerification.setVisibility(View.VISIBLE);
            verifyCodeDialog.dismiss();

            SharedPreferences sharedPreferences = getSharedPreferences("loginInfo",MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            long nowPlus5Minutes = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(5);
            editor.putString("banTime",String.valueOf(nowPlus5Minutes));
            editor.apply();
        });

        startVerification.setOnClickListener(click -> {
            getBanInfo = getSharedPreferences("loginInfo",MODE_PRIVATE);
            if(getBanInfo.contains("banTime")){
                long value = Long.parseLong(getBanInfo.getString("banTime",""));
                long currentTime = Long.parseLong(String.valueOf(System.currentTimeMillis()));

                if(currentTime > value){
                    getBanInfo.edit().remove("banTime").apply();
                }else{
                    Toast.makeText(LoginEmployeeActivity.this, "Wait till ban is removed " + TimeUnit.MILLISECONDS.toMinutes(value - currentTime) + " minutes Remaining", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            if(name.length() == 0){
                name.requestFocus();
                name.setError("Field can't be Empty");
                return;
            }if(phoneNumber.length() != 10){
                phoneNumber.requestFocus();
                phoneNumber.setError("Enter valid number");
                return;
            }if(!ccp.getSelectedCountryCodeWithPlus().equals("+91")){
                Toast.makeText(LoginEmployeeActivity.this, "This app currently operates only in India", Toast.LENGTH_SHORT).show();
                return;
            }

            new KAlertDialog(LoginEmployeeActivity.this,KAlertDialog.WARNING_TYPE)
                    .setContentText("Do you sure wanna continue with this number")
                    .setTitleText("Confirmation")
                    .setConfirmText("Yes, Send OTP")
                    .setCancelText("No, Wait")
                    .setConfirmClickListener(kAlertDialog -> {
                        names = name.getText().toString();
                        phones = ccp.getSelectedCountryCodeWithPlus() + phoneNumber.getText().toString() + "";
                        fastDialog = new FastDialogBuilder(LoginEmployeeActivity.this, Type.PROGRESS)
                                .progressText("Sending OTP Please wait")
                                .setAnimation(Animations.FADE_IN)
                                .create();
                        fastDialog.show();
                        startVerification.setVisibility(View.INVISIBLE);
                        startPhoneNumberVerification(phones);

                        kAlertDialog.dismissWithAnimation();
                    }).setCancelClickListener(KAlertDialog::dismissWithAnimation).show();
        });
    }

    private void startPhoneNumberVerification(String number) {
        PhoneAuthOptions options =  PhoneAuthOptions.newBuilder(loginAuth)
                .setPhoneNumber(number)
                .setActivity(this)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        Toast.makeText(LoginEmployeeActivity.this, "Code sent", Toast.LENGTH_SHORT).show();
                        credential = phoneAuthCredential;
                        signInWithPhoneAuthCredentials(phoneAuthCredential);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        Toast.makeText(LoginEmployeeActivity.this, e.getLocalizedMessage()+"", Toast.LENGTH_SHORT).show();
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
                FirebaseAuth auth = FirebaseAuth.getInstance();
                Map<String,Object> map = new HashMap<>();
                map.put("name",names);
                map.put("email","");
//                reciverMailID = email;
                editor.putString("name",names);
                editor.putString("email","");
                editor.apply();
//                    wait.setVisibility(View.INVISIBLE);
                Toast.makeText(LoginEmployeeActivity.this, "Login Successfully", Toast.LENGTH_SHORT).show();
                DatabaseAdmin user = new DatabaseAdmin(names,"",phones);
//                db.collection(Objects.requireNonNull(loginAuth.getUid())).document("info")
//                clientsLocation.removeLocationUpdates(mLocationCallback);
                reference.child("EmployeeDB").child(auth.getUid()+"").setValue(user);
                editor.putString("loginAsEmployee","yes");
                editor.apply();
                startActivity(new Intent(LoginEmployeeActivity.this, ApplyForRestaurantAndDocs.class));
                finish();
            }
        });
    }
}