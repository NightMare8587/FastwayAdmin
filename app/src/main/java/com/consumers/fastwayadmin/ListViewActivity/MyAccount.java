package com.consumers.fastwayadmin.ListViewActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.consumers.fastwayadmin.Login.MainActivity;
import com.consumers.fastwayadmin.R;
import com.developer.kalert.KAlertDialog;
import com.example.flatdialoglibrary.dialog.FlatDialog;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import karpuzoglu.enes.com.fastdialog.Animations;
import karpuzoglu.enes.com.fastdialog.FastDialog;
import karpuzoglu.enes.com.fastdialog.FastDialogBuilder;
import karpuzoglu.enes.com.fastdialog.PositiveClick;
import karpuzoglu.enes.com.fastdialog.Type;
import nl.invissvenska.modalbottomsheetdialog.Item;
import nl.invissvenska.modalbottomsheetdialog.ModalBottomSheetDialog;

public class MyAccount extends AppCompatActivity implements ModalBottomSheetDialog.Listener {
    ListView listView;
    DatabaseReference reference;
    FirebaseAuth auth;
    GoogleSignInClient client;
    GoogleSignInOptions gso;
    String verId;
    PhoneAuthCredential credential;
    ModalBottomSheetDialog modalBottomSheetDialog;
    TextView textView;
    String[] names = {"Change Credentials (Admin)","Change Credentials (Restaurants)","Change Mobile Number","Delete Account"};
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);
        initialise();
        modalBottomSheetDialog = new ModalBottomSheetDialog.Builder()
                .setRoundedModal(true)
                .setHeader("Choose One Option")
                .add(R.menu.bottom_sheet)
                .build();
        ArrayAdapter arrayAdapter = new ArrayAdapter<String>(this, R.layout.list,names);
        listView.setAdapter(arrayAdapter);
        SharedPreferences sharedPreferences = getSharedPreferences("loginInfo",MODE_PRIVATE);
        textView.setText("Hi, " + sharedPreferences.getString("name",""));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i){
                    case 0:
//                        FlatDialog flatDialog = new FlatDialog(MyAccount.this)
//                                .setTitle("Choose One Option")
//                                .setTitleColor(Color.BLACK)
//                                .setFirstButtonText("Change Name")
//                                .setFirstButtonTextColor(Color.BLACK)
//                                .setFirstButtonColor(Color.LTGRAY)
//                                .setSecondButtonColor(Color.LTGRAY)
//                                .setSecondButtonText("Change Email")
//                                .setSecondButtonTextColor()

                        modalBottomSheetDialog.show(getSupportFragmentManager(),"xd");

                        break;
                    case 1:
                        break;
                    case 2:
                        auth = FirebaseAuth.getInstance();
                        reference = FirebaseDatabase.getInstance().getReference().getRoot().child("Users").child(Objects.requireNonNull(auth.getUid()));
                        reference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.hasChild("number")) {
                                    FastDialog fastDialog = new FastDialogBuilder(MyAccount.this, Type.DIALOG)
                                            .setTitleText("Change Mobile Number ")
                                            .setText("Enter New Mobile Number\n")
                                            .setHint("Also Add Your Country Code")
                                            .setAnimation(Animations.SLIDE_TOP)
                                            .positiveText("Change")
                                            .negativeText("Cancel")
                                            .setTextMaxLength(13)
                                            .cancelable(false)
                                            .create();

                                    fastDialog.positiveClickListener(new PositiveClick() {
                                        @Override
                                        public void onClick(View view) {
                                            String inputV = fastDialog.getInputText();
                                            if (inputV.length() <= 12) {
                                                Toast.makeText(MyAccount.this, "Enter Valid Number", Toast.LENGTH_SHORT).show();
                                                return;
                                            } else {
                                                reference = FirebaseDatabase.getInstance().getReference().getRoot().child("Users").child(auth.getUid());

                                                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        if (snapshot.child("number").getValue(String.class).equals(inputV)) {
                                                            new KAlertDialog(MyAccount.this, KAlertDialog.ERROR_TYPE)
                                                                    .setTitleText("Error")
                                                                    .setContentText("Entered Mobile Number is same. Enter Different Mobile Number")
                                                                    .setConfirmText("Ok")
                                                                    .setConfirmClickListener(new KAlertDialog.KAlertClickListener() {
                                                                        @Override
                                                                        public void onClick(KAlertDialog kAlertDialog) {
                                                                            kAlertDialog.dismissWithAnimation();
                                                                        }
                                                                    }).show();
                                                        }else{
                                                            fastDialog.dismiss();
                                                            FastDialog fastDialog1 = new FastDialogBuilder(MyAccount.this, Type.DIALOG)
                                                                    .setTitleText("Code Sent")
                                                                    .setText("Enter 6 Digit Code\n")
                                                                    .setHint("Here")
                                                                    .setAnimation(Animations.SLIDE_TOP)
                                                                    .positiveText("Confirm")
                                                                    .negativeText("Cancel")
                                                                    .setTextMaxLength(6)
                                                                    .cancelable(false)
                                                                    .create();
                                                            fastDialog1.show();
                                                            auth = FirebaseAuth.getInstance();
                                                            PhoneAuthOptions options =  PhoneAuthOptions.newBuilder(auth)
                                                                    .setPhoneNumber(inputV)
                                                                    .setActivity(MyAccount.this)
                                                                    .setTimeout(60L, TimeUnit.SECONDS)
                                                                    .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                                                                        @Override
                                                                        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                                                                            credential = phoneAuthCredential;
                                                                            Toast.makeText(MyAccount.this, "I am here", Toast.LENGTH_SHORT).show();

                                                                        }

                                                                        @Override
                                                                        public void onVerificationFailed(@NonNull FirebaseException e) {
                                                                            Toast.makeText(MyAccount.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

                                                                        }

                                                                        @Override
                                                                        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                                                            Toast.makeText(MyAccount.this, "Code sent", Toast.LENGTH_SHORT).show();
                                                                                verId = s;
                                                                        }
                                                                    }).build();

                                                            fastDialog1.positiveClickListener(new PositiveClick() {
                                                                @Override
                                                                public void onClick(View view) {
                                                                    credential = PhoneAuthProvider.getCredential(verId,fastDialog1.getInputText());
                                                                    auth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                                                            if(task.isSuccessful()){
                                                                                FirebaseUser user = auth.getCurrentUser();
                                                                                user.updatePhoneNumber(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                                        if(task.isSuccessful())
                                                                                            Toast.makeText(MyAccount.this, "Success", Toast.LENGTH_SHORT).show();
                                                                                        else
                                                                                            Toast.makeText(MyAccount.this, "Faliure", Toast.LENGTH_SHORT).show();
                                                                                    }
                                                                                });
                                                                            }
                                                                        }
                                                                    });
                                                                }
                                                            });

                                                            PhoneAuthProvider.verifyPhoneNumber(options);
                                                        }

                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                    }
                                                });
                                            }
                                        }
                                    });

                                    fastDialog.show();

                                } else {
                                    Toast.makeText(MyAccount.this, "No", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        break;

                    case 3:
                        new KAlertDialog(MyAccount.this,KAlertDialog.WARNING_TYPE)
                                .setTitleText("Delete Account")
                                .setContentText("Do you sure wanna delete your account!!!\n"+"This action can't be revert")
                                .setConfirmText("Yes, Delete")
                                .setConfirmClickListener(new KAlertDialog.KAlertClickListener() {
                                    @Override
                                    public void onClick(KAlertDialog kAlertDialog) {

                                        reference = FirebaseDatabase.getInstance().getReference().getRoot().child("Admin");
                                        reference.child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser().getUid())).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                            }
                                        });
                                        reference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants");
                                        reference.child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser().getUid())).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {

                                            }
                                        });
                                        auth = FirebaseAuth.getInstance();
                                        FirebaseUser firebaseUser = auth.getCurrentUser();
                                        auth.signOut();
                                       firebaseUser.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                           @Override
                                           public void onSuccess(Void aVoid) {

                                           }
                                       });
                                        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                                .requestIdToken("765176451275-u5qelumumncbf54dh2fgs1do08luae91.apps.googleusercontent.com")
                                                .requestEmail()
                                                .build();

                                        client = GoogleSignIn.getClient(MyAccount.this,gso);
                                        try{

                                            client.revokeAccess().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {

                                                }
                                            });
                                            client.signOut();
                                        }
                                        catch (Exception e){
                                            Log.i("exception",e.getLocalizedMessage());
                                        }
                                        kAlertDialog.dismissWithAnimation();
                                        startActivity(new Intent(MyAccount.this, MainActivity.class));
                                        finish();
                                    }
                                }).setCancelText("No, Wait")
                                .setCancelClickListener(new KAlertDialog.KAlertClickListener() {
                                    @Override
                                    public void onClick(KAlertDialog kAlertDialog) {
                                        kAlertDialog.dismissWithAnimation();
                                    }
                                }).show();
                        break;
                }
            }
        });
    }
    private void initialise() {
        textView = findViewById(R.id.account_activity_text);
        listView = findViewById(R.id.accountActivityListView);
        auth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference().getRoot().child("Admin").child(Objects.requireNonNull(auth.getUid()));
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onItemSelected(String tag, Item item) {
        int id = item.getId();
        switch (id){
            case R.id.changeNameBottomSheet:
                FlatDialog flatDialog = new FlatDialog(MyAccount.this);
                               flatDialog.setTitle("Enter New Name")
                                       .setBackgroundColor(Color.WHITE)
                                .setTitleColor(Color.BLACK)
                                .setFirstTextFieldHint("Enter New Name")
                                       .setFirstTextFieldBorderColor(Color.BLACK)
                                       .setFirstTextFieldHintColor(Color.BLACK)
                                       .setFirstTextFieldTextColor(Color.BLACK)
                                       .setFirstTextFieldTextColor(Color.BLACK)
                                .setFirstButtonText("Make Changes")
                        .setFirstButtonColor(Color.LTGRAY)
                        .setFirstButtonTextColor(Color.BLACK)
                                       .setSecondButtonText("Cancel")
                                       .setSecondButtonColor(Color.CYAN)
                                       .setSecondButtonTextColor(Color.BLACK)
                        .withFirstButtonListner(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Map<String,Object> map = new HashMap<>();
                                Log.i("auth",auth.getUid());
                                map.put("name",String.valueOf(flatDialog.getFirstTextField().toString()));
                                reference.child("name").setValue(flatDialog.getFirstTextField().toString());
                                Toast.makeText(MyAccount.this, "Name Changed Successfully", Toast.LENGTH_SHORT).show();
                                flatDialog.dismiss();
                            }
                        })
                                       .withSecondButtonListner(new View.OnClickListener() {
                                           @Override
                                           public void onClick(View view) {
                                               flatDialog.dismiss();
                                           }
                                       });

                flatDialog.create();
                flatDialog.show();
                modalBottomSheetDialog.dismiss();
                break;
            case R.id.changeEmailBottomSheet:
                FlatDialog flatDialog1 = new FlatDialog(MyAccount.this);
                flatDialog1.setTitle("Enter New Name")
                        .setBackgroundColor(Color.WHITE)
                        .setTitleColor(Color.BLACK)
                        .setFirstTextFieldBorderColor(Color.BLACK)
                        .setFirstTextFieldHintColor(Color.BLACK)
                        .setFirstTextFieldTextColor(Color.BLACK)
                        .setFirstTextFieldHint("Enter New Name")
                        .setFirstButtonText("Make Changes")
                        .setFirstButtonColor(Color.LTGRAY)
                        .setFirstButtonTextColor(Color.BLACK)
                        .setSecondButtonText("Cancel")
                        .setSecondButtonColor(Color.CYAN)
                        .setSecondButtonTextColor(Color.BLACK)
                        .withFirstButtonListner(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Map<String,Object> map = new HashMap<>();
                                Log.i("auth",auth.getUid());
                                map.put("name",String.valueOf(flatDialog1.getFirstTextField().toString()));
                                reference.child("email").setValue(flatDialog1.getFirstTextField().toString());
                                Toast.makeText(MyAccount.this, "Email Changed Successfully", Toast.LENGTH_SHORT).show();
                                flatDialog1.dismiss();
                            }
                        })
                        .withSecondButtonListner(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                flatDialog1.dismiss();
                            }
                        });

                flatDialog1.create();
                flatDialog1.show();
                modalBottomSheetDialog.dismiss();
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + id);
        }
    }
}