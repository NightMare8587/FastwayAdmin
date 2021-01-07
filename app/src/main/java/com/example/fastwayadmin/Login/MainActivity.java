package com.example.fastwayadmin.Login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.fastwayadmin.Info;
import com.example.fastwayadmin.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hbb20.CountryCodePicker;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth loginAuth;
    String verId;
    PhoneAuthProvider.ForceResendingToken myToken;
    EditText fullName,emailAddress,phoneNumber,codeSent;
    CountryCodePicker ccp;
    Button startVerification,verifyCode;
    DatabaseReference reference;
    String name,email,number;
    PhoneAuthCredential credential;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialise();
        startVerification.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                if(fullName.length() == 0){
                    fullName.requestFocus();
                    fullName.setError("Field can't be Empty");
                    return;
                }else if(emailAddress.length() == 0){
                    emailAddress.requestFocus();
                    emailAddress.setError("Field can't be Empty");
                    return;
                }else if(phoneNumber.length() <= 9){
                    phoneNumber.requestFocus();
                    phoneNumber.setError("Enter valid number");
                    return;
                }
                name = fullName.getText().toString();
                email = emailAddress.getText().toString();
                number = ccp.getSelectedCountryCodeWithPlus() + phoneNumber.getText().toString() + "";

                codeSent.setEnabled(true);
                startVerification.setVisibility(View.INVISIBLE);
                verifyCode.setVisibility(View.VISIBLE);
                startPhoneNumberVerification(number);
            }
        });


        verifyCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(codeSent.length() <= 5){
                    codeSent.requestFocus();
                    codeSent.setError("invalid code");
                    return;
                }
              credential = PhoneAuthProvider.getCredential(verId,codeSent.getText().toString());
                loginAuth.signInWithCredential(credential).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(MainActivity.this, "Login Successfully", Toast.LENGTH_SHORT).show();
                            DatabaseAdmin user = new DatabaseAdmin(name,email,number);
                            reference.child("Admin").child(loginAuth.getUid()+"").setValue(user);
                            startActivity(new Intent(MainActivity.this,Info.class));
                            finish();
                        }
                    }
                });
            }
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
                        Toast.makeText(MainActivity.this, "Code sent", Toast.LENGTH_SHORT).show();
                        credential = phoneAuthCredential;
                        signInWithPhoneAuthCredentials(phoneAuthCredential);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        Toast.makeText(MainActivity.this, e.getLocalizedMessage()+"", Toast.LENGTH_SHORT).show();
                        verifyCode.setVisibility(View.INVISIBLE);
                        startVerification.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        verId = s;
                        myToken = forceResendingToken;


                    }
                }).build();

        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void signInWithPhoneAuthCredentials(PhoneAuthCredential phoneAuthCredential) {
        loginAuth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(MainActivity.this, "Login Successfully", Toast.LENGTH_SHORT).show();
                    DatabaseAdmin user = new DatabaseAdmin(name,email,number);
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
        codeSent = findViewById(R.id.codeSent);
        startVerification = findViewById(R.id.startVerification);
        verifyCode = findViewById(R.id.verifyVerification);
        reference = FirebaseDatabase.getInstance().getReference().getRoot();
    }

}