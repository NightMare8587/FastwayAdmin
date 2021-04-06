package com.consumers.fastwayadmin.Login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.consumers.fastwayadmin.HomeScreen;
import com.consumers.fastwayadmin.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;

import java.util.concurrent.TimeUnit;

public class AlreadyHavAccount extends AppCompatActivity {

    EditText phone;
    CountryCodePicker ccp;
    EditText code;
    String verId;
    Button sendCode,verifyCode;
    PhoneAuthProvider.ForceResendingToken myToken;
    FirebaseUser currentUser;
    PhoneAuthCredential credential;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_already_hav_account);

        initialise();

        sendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(phone.length() <= 9){
                    phone.requestFocus();
                    phone.setError("Invalid Number");
                    return;
                }

                verifyCode.setEnabled(true);
                sendCode.setVisibility(View.INVISIBLE);
                verifyCode.setVisibility(View.VISIBLE);

                startPhoneNumberVerification(ccp.getSelectedCountryCodeWithPlus() + phone.getText().toString());
            }
        });

        verifyCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(code.length() <= 5){
                    code.requestFocus();
                    code.setError("invalid code");
                    return;
                }
                credential = PhoneAuthProvider.getCredential(verId,code.getText().toString());

                mAuth.signInWithCredential(credential).addOnCompleteListener(AlreadyHavAccount.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(AlreadyHavAccount.this, "Login Successfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(AlreadyHavAccount.this,HomeScreen.class));
                        }
                    }
                });
            }
        });
    }
    private void startPhoneNumberVerification(String number) {
        PhoneAuthOptions options =  PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber(number)
                .setActivity(this)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        Toast.makeText(AlreadyHavAccount.this, "Code sent", Toast.LENGTH_SHORT).show();
                        credential = phoneAuthCredential;
                        signInWithPhoneAuthCredentials(phoneAuthCredential);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        Toast.makeText(AlreadyHavAccount.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        verId = s;
                    }
                }).build();

        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void signInWithPhoneAuthCredentials(PhoneAuthCredential phoneAuthCredential) {
        mAuth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(AlreadyHavAccount.this, "Login Successfully", Toast.LENGTH_SHORT).show();

                    startActivity(new Intent(AlreadyHavAccount.this, HomeScreen.class));
                    finish();
                }
            }
        });
    }


    private void initialise() {
        phone = findViewById(R.id.alreadyHavingNumber);
        ccp = findViewById(R.id.CCP);
        sendCode = findViewById(R.id.startCodeVerification);
        verifyCode = findViewById(R.id.verifyCodeSent);
        mAuth = FirebaseAuth.getInstance();
        code = findViewById(R.id.sentdigitCode);
    }
}